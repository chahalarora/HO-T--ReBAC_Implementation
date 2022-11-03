package ca.ucalgary.ispia.graphpatterns;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.Map;

import org.apache.commons.dbutils.DbUtils;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;

import ca.ucalgary.ispia.graphpatterns.gpchecker.opt.impl.DiscoverablePeriodFinderWithDbAccess;
import ca.ucalgary.ispia.graphpatterns.gpchecker.opt.impl.OfficialPeriodFinder;
import ca.ucalgary.ispia.graphpatterns.graph.AlgorithmType;
import ca.ucalgary.ispia.graphpatterns.graph.GraphPattern;
import ca.ucalgary.ispia.graphpatterns.graph.MyNode;
import edu.stanford.nlp.util.Interval;
import edu.stanford.nlp.util.IntervalTree;

public class UpdatingOfficialPeriodsTable {

	private AlgorithmType algorithm = AlgorithmType.FC;
	private boolean valueOrdering = false;
	
	
	public void updateOfficialPeriodTables(GraphDatabaseService graphDb, GraphPattern gp, int relationshipId, Map<MyNode, Node> info) {
		
		MysqlConnection mysqlConnection = new MysqlConnection();
		Connection mysqlConn = mysqlConnection.mysqlConn;
		
		Iterator<MyNode> infoKeysetIterator =  info.keySet().iterator();
		int requestorId = (int) info.get(infoKeysetIterator.next()).getId();
		int resourceId = (int) info.get(infoKeysetIterator.next()).getId();
		
		int lastCheckedTimePoint = getLastCheckedTimePoint(mysqlConn, requestorId, resourceId, relationshipId);
		
		DiscoverablePeriodFinderWithDbAccess discoverablePeriodFinder = new DiscoverablePeriodFinderWithDbAccess(graphDb, algorithm, valueOrdering, lastCheckedTimePoint);
		IntervalTree discoverablePeriodsTree = discoverablePeriodFinder.FCLBJTemporalInit(graphDb, gp, info);
		
		OfficialPeriodFinder OfficialPeriodFinder = new OfficialPeriodFinder();
		IntervalTree officialPeriodsSet = OfficialPeriodFinder.findOfficialPeriods(discoverablePeriodsTree);
		
//		System.out.println(Integer.MAX_VALUE);
		
		int ongoingRowId = selectOngoingOfficialPeriod(mysqlConn, requestorId, resourceId, relationshipId, Integer.MAX_VALUE);
		
		if(ongoingRowId != -1) {
			deleteOngoingOfficialPeriod(mysqlConn, ongoingRowId);
		}
		
		updateOfficialPeriodTable(mysqlConn, requestorId, resourceId, relationshipId, officialPeriodsSet);
		
//		Date date = new Date();
//		
//		long time = date.getTime();
		
		lastCheckedTimePoint = 104;
		
		updateLastCheckedTable(mysqlConn, requestorId, resourceId, relationshipId, lastCheckedTimePoint);
		
		DbUtils.closeQuietly(mysqlConn);
	}

	public int getLastCheckedTimePoint(Connection mysqlConn, int requestorId, int resourceId, int relationshipId) {
		
		PreparedStatement lastUpdateTimePrepareStatement = null;
		ResultSet resultSet = null;
		
		int resultTimePoint = 0; 
		
	 	try {
			
			String lastUpdateTimeQueryStatement = "SELECT last_checked_time  FROM last_checked WHERE requestor_id = ? and resource_id = ? and relationship_id = ? ORDER BY id DESC LIMIT 1";
							
			lastUpdateTimePrepareStatement = mysqlConn.prepareStatement(lastUpdateTimeQueryStatement);
			lastUpdateTimePrepareStatement.setInt(1, requestorId);
			lastUpdateTimePrepareStatement.setInt(2, resourceId);
			lastUpdateTimePrepareStatement.setInt(3, relationshipId);
			resultSet = lastUpdateTimePrepareStatement.executeQuery();
			
			if(resultSet.next()) {
				
				resultTimePoint = resultSet.getInt("last_checked_time");
			}
			
		} catch (SQLException e) {
			 e.printStackTrace();
			 
		} finally {
		    
			DbUtils.closeQuietly(resultSet);
			DbUtils.closeQuietly(lastUpdateTimePrepareStatement);
		}
		
		return resultTimePoint;
		
	}
	
	public void updateOfficialPeriodTable(Connection mysqlConn, int requestorId, int resourceId, int relationshipId, IntervalTree officialPeriodsTree) {
		
		Iterator officialPeriodsTreeIterator = officialPeriodsTree.iterator();
		
		while(officialPeriodsTreeIterator.hasNext()) {
			
			Interval officialPeriodInterval = (Interval) officialPeriodsTreeIterator.next();
			
			int startTime = (int) officialPeriodInterval.first;
			int endTime = (int) officialPeriodInterval.second;
			
			PreparedStatement officialPeriodUpdatePrepareStatement = null;
						
		 	try {

				String officialPeriodUpdateQueryStatement = "INSERT INTO requestor_resource SET requestor_id = ? , resource_id = ? , relationship_id = ? , start_time = ? , end_time = ? ";
								
				officialPeriodUpdatePrepareStatement = mysqlConn.prepareStatement(officialPeriodUpdateQueryStatement);
				officialPeriodUpdatePrepareStatement.setInt(1, requestorId);
				officialPeriodUpdatePrepareStatement.setInt(2, resourceId);
				officialPeriodUpdatePrepareStatement.setInt(3, relationshipId);
				officialPeriodUpdatePrepareStatement.setInt(4, startTime);
				officialPeriodUpdatePrepareStatement.setInt(5, endTime);
				
				officialPeriodUpdatePrepareStatement.executeUpdate();
				
			} catch (SQLException e) {
				 e.printStackTrace();
				 
			} finally {
			    
				DbUtils.closeQuietly(officialPeriodUpdatePrepareStatement);
			}
		 	
		}

	}
	
	
	public void updateLastCheckedTable(Connection mysqlConn, int requestorId, int resourceId, int relationshipId, int lastCheckedTime) {
		
		PreparedStatement officialPeriodUpdatePrepareStatement = null;
					
	 	try {

			String officialPeriodUpdateQueryStatement = "INSERT INTO last_checked SET requestor_id = ? , resource_id = ? , relationship_id = ? , last_checked_time = ?";
							
			officialPeriodUpdatePrepareStatement = mysqlConn.prepareStatement(officialPeriodUpdateQueryStatement);
			officialPeriodUpdatePrepareStatement.setInt(1, requestorId);
			officialPeriodUpdatePrepareStatement.setInt(2, resourceId);
			officialPeriodUpdatePrepareStatement.setInt(3, relationshipId);
			officialPeriodUpdatePrepareStatement.setInt(4, lastCheckedTime);
			
			officialPeriodUpdatePrepareStatement.executeUpdate();
			
		} catch (SQLException e) {
			
			 e.printStackTrace();
			 
		} finally {
		    
			DbUtils.closeQuietly(officialPeriodUpdatePrepareStatement);
		}
		 	
	}
	
	
	public int selectOngoingOfficialPeriod(Connection mysqlConn, int requestorId, int resourceId, int relationshipId, int endTime) {
		
		PreparedStatement ongoingPeriodPrepareStatement = null;
		ResultSet resultSet = null;
		
		int resultRowId = -1; 
					
	 	try {

			String ongoingPeriodQueryStatement = "SELECT id FROM requestor_resource WHERE requestor_id = ? and resource_id = ? and relationship_id = ? and end_time = ? ORDER BY id DESC LIMIT 1;";
							
			ongoingPeriodPrepareStatement = mysqlConn.prepareStatement(ongoingPeriodQueryStatement);
			ongoingPeriodPrepareStatement.setInt(1, requestorId);
			ongoingPeriodPrepareStatement.setInt(2, resourceId);
			ongoingPeriodPrepareStatement.setInt(3, relationshipId);
			ongoingPeriodPrepareStatement.setInt(4, endTime);
			
			resultSet = ongoingPeriodPrepareStatement.executeQuery();
			if(resultSet.next()) {
				resultRowId = resultSet.getInt("id");
			}
			
		} catch (SQLException e) {
			
			 e.printStackTrace();
			 
		} finally {
		    
			DbUtils.closeQuietly(resultSet);
			DbUtils.closeQuietly(ongoingPeriodPrepareStatement);
		}
	 	
	 	return resultRowId;
	}
	
	
	public void deleteOngoingOfficialPeriod(Connection mysqlConn, int rowId) {

		PreparedStatement deleteOngoingPeriodPrepareStatement = null;
					
	 	try {

			String deleteOngoingPeriodQueryStatement = "DELETE FROM requestor_resource WHERE id = ?";
							
			deleteOngoingPeriodPrepareStatement = mysqlConn.prepareStatement(deleteOngoingPeriodQueryStatement);
			deleteOngoingPeriodPrepareStatement.setInt(1, rowId);
			deleteOngoingPeriodPrepareStatement.executeUpdate();
			
		} catch (SQLException e) {
			 e.printStackTrace();
 
		} finally {
		    
			DbUtils.closeQuietly(deleteOngoingPeriodPrepareStatement);
		}
	 	
	}
}
