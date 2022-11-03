
package ca.ucalgary.ispia.graphpatterns;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class MysqlConnection {

	public static Connection mysqlConn = null;

	public MysqlConnection() {
		try {
			Class.forName("com.mysql.jdbc.Driver");
			
		} catch (ClassNotFoundException e) {
			
			e.printStackTrace();
			return;
		}
 
		try {
			
			mysqlConn = DriverManager.getConnection("jdbc:mysql://localhost:3306/HAReBAC", "root", "Hello123");
			if (mysqlConn != null) {
				
//				System.out.println("Sql connection established");
				
			} else {
				
				System.out.println("Sql connection not established");
			}
		} catch (SQLException e) {
			
			System.out.println("Error establishing sql connection");
			
			e.printStackTrace();
			return;
		}
	}
}