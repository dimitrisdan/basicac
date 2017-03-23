/**
 * @author Dimitrios Danampasis
 */
package ac.libs;

import java.sql.*;

public class DatabaseConnection {

	 // JDBC driver name and database URL
	 static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";  
	 static final String DB_URL = "jdbc:mysql://localhost/security";
	
	 //  Database credentials
	 static final String USER = "root";
	 static final String PASS = "";
	 
	 public Connection connectToMysql(){
		 Connection conn = null;
		 try {
			Class.forName("com.mysql.jdbc.Driver");
			conn = DriverManager.getConnection(DB_URL,USER,PASS);
			return conn;
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			return conn;
		}catch (SQLException e) {
			e.printStackTrace();
			return conn;
		}
	 }	 
}