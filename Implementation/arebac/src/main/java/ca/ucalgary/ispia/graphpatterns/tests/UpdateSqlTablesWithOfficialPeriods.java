package ca.ucalgary.ispia.graphpatterns.tests;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectOutputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.Random;
import java.util.Set;

import org.apache.commons.dbutils.DbUtils;
import org.apache.commons.lang3.tuple.Triple;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;

import ca.ucalgary.ispia.graphpatterns.Driver;
import ca.ucalgary.ispia.graphpatterns.MysqlConnection;
import ca.ucalgary.ispia.graphpatterns.graph.AlgorithmType;
import ca.ucalgary.ispia.graphpatterns.graph.GraphPattern;
import ca.ucalgary.ispia.graphpatterns.graph.MyNode;
import ca.ucalgary.ispia.graphpatterns.util.Pair;
import ca.ucalgary.ispia.policy.opt.AllenRelation;
import ca.ucalgary.ispia.policy.opt.PolicyPrefix;
import edu.stanford.nlp.util.Interval;
import edu.stanford.nlp.util.IntervalTree;

public class UpdateSqlTablesWithOfficialPeriods {

	public static GraphDatabaseService graphDb = null;

	static String propFileLocation = "./properties/SqlUpdate.properties";

	/**
	 * The main control for specifying tasks.
	 * @param args
	 */
	public static void main(String[] args){	
		
		Driver d = new Driver();
		graphDb = d.getGraphDb("graph.db");
		
		try {
			
			
			File file = new File(propFileLocation);
			InputStream in = new FileInputStream(file);
		    Properties properties = new Properties();
		    properties.load(in);
		    
		    int evaluationType = Integer.parseInt(properties.getProperty("evaluationType"));
		    int numberofIteration = Integer.parseInt(properties.getProperty("numberofIteration"));
		    int numberofNodesInGP = Integer.parseInt(properties.getProperty("numberofNodesInGP"));
		    int snapshotTimePoint =	Integer.parseInt(properties.getProperty("snapshotTimePoint"));
		    int numberofNodeInNeo4jGraphDatabse = Integer.parseInt(properties.getProperty("numberofNodeInNeo4jGraphDatabse"));
		    int historyEndTime = Integer.parseInt(properties.getProperty("historyEndTime")); 
		    
		    
		    EvaluateDiscoverablePeriodFinder evaluateDiscoverablePeriodFinder = new EvaluateDiscoverablePeriodFinder();		    
		    evaluateDiscoverablePeriodFinder.graphDb = graphDb;
		    evaluateDiscoverablePeriodFinder.algorithm = AlgorithmType.FC_LBJ_Improved;
		    
		    
		    if(evaluationType == 0) {
		    	
		    	evaluateDiscoverablePeriodFinder.createFilesForGPInfoPair(numberofIteration, numberofNodeInNeo4jGraphDatabse, numberofNodesInGP, snapshotTimePoint, historyEndTime);
		    
		    } else if(evaluationType == 1) {
		    	
//		    	EvaluateDiscoverablePeriodFinder evaluateDiscoverablePeriodFinderFileRead = new EvaluateDiscoverablePeriodFinder();
//		    	Set<Pair<GraphPattern, Map<MyNode, Node>>> gpInfoPairSet = evaluateDiscoverablePeriodFinderFileRead.readGPInfoPairFromFile(numberofIteration, numberofNodeInNeo4jGraphDatabse, historyEndTime, numberofNodesInGP);
//		    	evaluateDiscoverablePeriodFinderFileRead.evaluateDiscoverblePeriodFinderTimePointPresent(gpInfoPairSet, numberofIteration, snapshotTimePoint);
		    	
//		    	Set<Triple<Long, Long, Interval>> sqlUpdateTripleSet = evaluateDiscoverablePeriodFinderFileRead.sqlUpdateTripleSet;
		    	
		    	Set<Triple<Long, Long, Interval>> sqlUpdateTripleSet = generatesqlUpdateTripleSet();
		    	
		    	
		    	System.out.println(sqlUpdateTripleSet.size());
		    	
		    	MysqlConnection mysqlConnection = new MysqlConnection();
				Connection mysqlConn = mysqlConnection.mysqlConn;
		    	
		    	String insertQuery = getInsertQuery(sqlUpdateTripleSet);
		    	
//		    	System.out.println(insertQuery);
		    	
		    	updateOfficialPeriodTable(mysqlConn, insertQuery);
		    	
		    	System.out.println("Sql table updated.");
		    }
		    
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		
		graphDb.shutdown();
	}
	
	public static Set<Triple<Long, Long, Interval>> generatesqlUpdateTripleSet() {
		Set<Triple<Long, Long, Interval>> result = new HashSet<>();
		
		for(int i=0; i < 25000; i++) {
			
			Random randomStart = new Random();
			Random randomEnd = new Random();
			
			Random randomSource = new Random();
			Random randomTarget = new Random();
			Integer sourceId = randomSource.nextInt(5000);
			Integer targetId = randomTarget.nextInt(5000);
			
			Integer startTime = randomStart.nextInt(1);
			Integer endTime = startTime + randomEnd.nextInt(1);
			
			Random randomOfficialPeriods = new Random();
			Integer numberofOfficialPeriods = randomOfficialPeriods.nextInt(8);
			
			for(int j=0; j < numberofOfficialPeriods; j++) {
				startTime = endTime + randomStart.nextInt(50);
				endTime = startTime + randomEnd.nextInt(50);
				
				Interval interval = (Interval) Interval.toInterval(startTime, endTime);
				Triple<Long, Long, Interval> sqlUpdateTriple = Triple.of(Long.parseLong(sourceId.toString()), Long.parseLong(targetId.toString()), interval);
				result.add(sqlUpdateTriple);
			}
		}
		
		return result;
	}

	public static String getInsertQuery(Set<Triple<Long, Long, Interval>> sqlUpdateTripleSet) {
		String resultString = "";
		
		resultString += "Insert Into official_periods (`source_id`,\n" + "`destination_id`,\n"
				+ "`rebac_relationship_type_id`,\n" + "`start_time`,\n" + "`end_time`) VALUES ";

		Iterator<Triple<Long, Long, Interval>> sqlUpdateTripleIterator = sqlUpdateTripleSet.iterator();

		while(sqlUpdateTripleIterator.hasNext()) {

			Triple<Long, Long, Interval> sqlUpdateValue = sqlUpdateTripleIterator.next();
			
			
			
			resultString += "(" + sqlUpdateValue.getLeft() + ", " + sqlUpdateValue.getMiddle() + ", " + "0" + ", "
					+ sqlUpdateValue.getRight().getBegin() + ", " + sqlUpdateValue.getRight().getEnd() + " )";
			
			
			if(sqlUpdateTripleIterator.hasNext()) {
				resultString += ", ";
			}
			
		}

		return resultString;

	}

	public static void updateOfficialPeriodTable(Connection mysqlConn, String insertQuery) {

		PreparedStatement officialPeriodUpdatePrepareStatement = null;

		try {

			String officialPeriodInsertQueryStatement = insertQuery;

			officialPeriodUpdatePrepareStatement = mysqlConn.prepareStatement(officialPeriodInsertQueryStatement);

			officialPeriodUpdatePrepareStatement.executeUpdate();

		} catch (SQLException e) {
			e.printStackTrace();

		} finally {

			DbUtils.closeQuietly(officialPeriodUpdatePrepareStatement);
		}

	}

}
