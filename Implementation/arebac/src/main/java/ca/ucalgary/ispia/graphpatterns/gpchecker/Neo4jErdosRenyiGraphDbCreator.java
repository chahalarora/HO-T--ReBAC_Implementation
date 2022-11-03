package ca.ucalgary.ispia.graphpatterns.gpchecker;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.ResourceIterator;
import org.neo4j.graphdb.Transaction;

import ca.ucalgary.ispia.graphpatterns.Driver;
import ca.ucalgary.ispia.graphpatterns.graph.ErdosRenyiGraphCreator;


public class Neo4jErdosRenyiGraphDbCreator {

	/**
	 * The main control for specifying tasks.
	 * @param args
	 */
	public static void main(String[] args){	
		
		Driver driver = new Driver();
		GraphDatabaseService graphDb = driver.getGraphDb("graph.db");		
		String propFileLocation= "./properties/GraphCreation.properties";
		File file = new File(propFileLocation);
		InputStream in;
		try {
			in = new FileInputStream(file);
			Properties properties = new Properties();
			properties.load(in);
		    int numberofNodeInNeo4jGraphDatabse = Integer.parseInt(properties.getProperty("numberofNodeInNeo4jGraphDatabse"));
		    int batchSize = Integer.parseInt(properties.getProperty("batchSize"));
		    int historyStartTime = Integer.parseInt(properties.getProperty("historyStartTime"));
		    int historyEndTime = Integer.parseInt(properties.getProperty("historyEndTime")); 
		    double probabilityofRelationshipR0 = Double.parseDouble(properties.getProperty("probabilityofRelationshipR0"));
		    double probabilityofRelationshipR1 = Double.parseDouble(properties.getProperty("probabilityofRelationshipR1"));
		    double probabilityofRelationshipR2 = Double.parseDouble(properties.getProperty("probabilityofRelationshipR2"));
		    double probabilityofRelationshipR3 = Double.parseDouble(properties.getProperty("probabilityofRelationshipR3"));
		    double probabilityofRelationshipR4 = Double.parseDouble(properties.getProperty("probabilityofRelationshipR4"));
		    double probabilityofRelationshipR5 = Double.parseDouble(properties.getProperty("probabilityofRelationshipR5"));
		    double probabilityofRelationshipR6 = Double.parseDouble(properties.getProperty("probabilityofRelationshipR6"));
		    double probablityEventActualization = Double.parseDouble(properties.getProperty("probablityEventActualization"));
	
			double[] probabilityRelTypeArray = new double[7];
			probabilityRelTypeArray[0] = probabilityofRelationshipR0;
			probabilityRelTypeArray[1] = probabilityofRelationshipR1;
			probabilityRelTypeArray[2] = probabilityofRelationshipR2;
			probabilityRelTypeArray[3] = probabilityofRelationshipR3;
			probabilityRelTypeArray[4] = probabilityofRelationshipR4;
			probabilityRelTypeArray[5] = probabilityofRelationshipR5;
			probabilityRelTypeArray[6] = probabilityofRelationshipR6;
			
			ErdosRenyiGraphCreator randomHistoryGraphCreator = new ErdosRenyiGraphCreator(graphDb, numberofNodeInNeo4jGraphDatabse, probabilityRelTypeArray, probablityEventActualization, batchSize, historyStartTime, historyEndTime);
				
			System.out.println("Graph creation started.");
			
			randomHistoryGraphCreator.createRandomGraphMatrixList();
			randomHistoryGraphCreator.createNeo4jNodesInBatch();
			randomHistoryGraphCreator.createNeo4jRelationshipsInBatch();
			
			System.out.println("Graph creation ended.");
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
				
	}
}
