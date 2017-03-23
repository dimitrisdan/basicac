package ac.services;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import ac.libs.PasswordHash;
import ac.libs.RemoteInterface;
import ac.libs.DatabaseConnection;

public class AuthenticationService extends UnicastRemoteObject implements RemoteInterface {

	private static final long serialVersionUID = 1L;
	static DatabaseConnection DB = new DatabaseConnection();
	
	public AuthenticationService() throws RemoteException {
		super();
	}

	@Override
	public List<String> buildMenuACL(String username) throws RemoteException {
		List<String> MENU = new ArrayList<String>();
		Connection conn = DB.connectToMysql();
		PreparedStatement stmt;
		String sql;
		ResultSet rs;
		sql = "SELECT permission FROM acl WHERE username = ?";
		try {
			stmt = conn.prepareStatement(sql);
	        stmt.setString(1, username);
	        rs = stmt.executeQuery();
	        rs.first();
	        MENU.add("0.Logout");
	        int counter = 1;
	        do{
	        	MENU.add(Integer.toString(counter)+"."+rs.getString("permission"));
	        	counter++;
	        }while(rs.next());
	        MENU.add("10.Exit");  
		}catch(SQLException sqle){
			System.out.println("!SQL exception: buildMenuACL()...!");
		}
		return MENU;
		
	}

	@Override
	public List<String> buildMenuRBAC(String username) throws RemoteException {
		int authority=0;
		List<String> MENU = new ArrayList<String>();
		  
		Connection conn = DB.connectToMysql();
		PreparedStatement stmt_auth,stmt_law;
		String sql_auth,sql_law;
		ResultSet rs;
		sql_auth = "SELECT authority FROM users WHERE username = ?";
		sql_law = "SELECT * FROM laws WHERE auth_id = ?";
		try {
			stmt_auth = conn.prepareStatement(sql_auth);
	        stmt_auth.setString(1, username);
	        rs = stmt_auth.executeQuery();
	        if(rs!=null && rs.next()){
	            authority = rs.getInt("authority");
	        }else
	        	return MENU;
	        stmt_law = conn.prepareStatement(sql_law);
	        stmt_law.setInt(1, authority);
	        rs = stmt_law.executeQuery();
	        MENU.add("0.Logout");
	        if(rs!=null && rs.next()){
	            if(rs.getInt("print") 		== 1 ) MENU.add("1.Print");
	            if(rs.getInt("queue") 		== 1 ) MENU.add("2.Queue");
	            if(rs.getInt("topqueue") 	== 1 ) MENU.add("3.Top Queue");
	            if(rs.getInt("start") 		== 1 ) MENU.add("4.Start Service");
	            if(rs.getInt("stop") 		== 1 ) MENU.add("5.Stop Service");
	            if(rs.getInt("restart") 	== 1 ) MENU.add("6.Restart Service");
	            if(rs.getInt("status") 		== 1 ) MENU.add("7.Status");
	            if(rs.getInt("readConfig") 	== 1 ) MENU.add("8.Read Config");
	            if(rs.getInt("setConfig") 	== 1 ) MENU.add("9.Set Config");
	        }
	        MENU.add("10.Exit");  
		}catch(SQLException sqle){
			System.out.println("!SQL exception: buildMenuRBAC()...!");
		}
		return MENU;  
	}
	
	@Override
	public String getAuthorizationName(String username) throws RemoteException{
		Connection conn = DB.connectToMysql();
		PreparedStatement stmt;
		ResultSet rs;
		String authorityName = null;
		String sql = "SELECT name FROM authorities WHERE "
					+ "id = (SELECT authority FROM users WHERE username = ? )";
		try{
			stmt = conn.prepareStatement(sql);
	        stmt.setString(1, username);
	        rs = stmt.executeQuery();
	        if(rs!=null && rs.next()){
	             authorityName= rs.getString("name");
	        }
		}catch(SQLException sqle){
			sqle.printStackTrace();
			return "No authority";
		}
	    return authorityName;
	}
	public int findLastId(Connection conn) throws SQLException{
		Statement stmt = conn.createStatement();
		ResultSet rs = stmt.executeQuery("SELECT id FROM users ORDER BY ID");
		int key;
		if (rs.last())
			key = rs.getInt("id");
		else
			key = 1;
		return key;
	}

	@Override
	public Boolean validateUser(String username, String passwordToValidate)
			throws RemoteException, NoSuchAlgorithmException, InvalidKeySpecException {
		
		DatabaseConnection db = new DatabaseConnection();
		Connection conn = db.connectToMysql();
		Statement stmt;
		ResultSet rs = null;
		String sql = "SELECT * FROM users WHERE username = '"+username+"'";
		//System.out.println(sql);
		try {
			stmt = conn.createStatement();
					
	        rs = stmt.executeQuery(sql);
	        if(rs!=null && rs.next()){
	        	String correctHashedPassword = rs.getString("password");
			    String salt = rs.getString("salt");
			    if (PasswordHash.validatePasswordFromServer(correctHashedPassword,passwordToValidate,salt)){
			    	conn.close();
			    	return true;
			    }else
			    	conn.close();
			    	return false;
	        }else{
	        	conn.close();
	        	return false;
	        }
		} catch (SQLException e) {
			System.out.println("!!SQL exception while validating a user...");
			return false;
		}
	}	

	protected static boolean checkAuthority(String username, String method_name) {
		Connection conn = DB.connectToMysql();
		PreparedStatement stmt;
		ResultSet rs = null;
		String sql = " SELECT * FROM laws WHERE auth_id = (SELECT authority FROM users WHERE username = ?) ";
		try{
			stmt = conn.prepareStatement(sql);
	        stmt.setString(1, username);
	        rs = stmt.executeQuery();
	        if(rs!=null && rs.next()){
				if (rs.getBoolean(method_name) == true) 
					return true;
				else
					return false;
	        }  
	        else return false;
		}catch(SQLException e){
			System.out.println("SQL exception in AuthenticationService constructor");
			return false;
		}
	}
	
	public static boolean checkACL(String username, String permission) {
		Connection conn = DB.connectToMysql();
		PreparedStatement stmt;
		ResultSet rs = null;
		String sql = "SELECT * FROM acl WHERE username = ? AND permission = ?";
		try{
			stmt = conn.prepareStatement(sql);
	        stmt.setString(1, username);
	        stmt.setString(1, permission);
	        rs = stmt.executeQuery();
	        if(rs!=null && rs.next())  return true;
	        else return false;
		}catch(SQLException e){
			System.out.println("SQL exception in AuthenticationService constructor");
			return false;
		}
		
	}	
}
