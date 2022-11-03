package ca.ucalgary.ispia.policy.opt;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.commons.dbutils.DbUtils;


import ca.ucalgary.ispia.graphpatterns.MysqlConnection;
import ca.ucalgary.ispia.graphpatterns.tests.Killable;

public class PolicyExecute implements Killable {

	static int timeoutSec = 60;
	
	public int executePolicySql(Connection mysqlConn, String policySqlQuery) {
		
		PreparedStatement policySqlQueryPrepareStatement = null;
		ResultSet resultSet = null;
		int resultCount = -1; 
		
	 	try {
	 		
	 		policySqlQueryPrepareStatement = mysqlConn.prepareStatement(policySqlQuery);
	 		
	 		policySqlQueryPrepareStatement.setQueryTimeout(timeoutSec);
	 		
	 		resultSet = policySqlQueryPrepareStatement.executeQuery();
			
			while (resultSet.next()) {
				resultCount = resultSet.getInt(1);
			}
			
		} catch (SQLException e) {
//			 e.printStackTrace();
			 System.out.println("Exception: "+ e);
			 
		} finally {
//			DbUtils.closeQuietly(mysqlConn);
			DbUtils.closeQuietly(resultSet);
			DbUtils.closeQuietly(policySqlQueryPrepareStatement);
		}
		
		return resultCount;
		
	}

	@Override
	public void kill() {
		// TODO Auto-generated method stub
		
	}
	
}
