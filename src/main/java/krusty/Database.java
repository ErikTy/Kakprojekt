package krusty;

import spark.Request;
import spark.Response;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;







public class Database {
	
	private Connection conn;
	public void connect() {
		
		try {
			
			Class.forName("com.mysql.cj.jdbc.Driver");
			conn = DriverManager.getConnection(
					"jdbc:mysql://puccini.cs.lth.se/hbg36?user=hbg36&password=vvg546cv&useSSL=false&autoReconnect=true");
			
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
		}
		
		// Connect to database here
	}

	public String getCustomers(Request req, Response res) {
		try {
			
			Statement stmt = conn.createStatement();
			
			String sql = "SELECT company AS name, adress as address FROM Customers;";
			ResultSet resultSet = stmt.executeQuery(sql);
			return JSONizer.toJSON(resultSet, "customers");
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return "";
		
	}

	public String getRawMaterials(Request req, Response res) {
		try{
			Statement stmt = conn.createStatement();
			String sql = "SELECT ingridient AS name, quantity AS amount, unit FROM Warehouse;";
			ResultSet rs = stmt.executeQuery(sql);
			return JSONizer.toJSON(rs, "raw-materials");
		}
		catch (SQLException e) {
			e.printStackTrace();
		}
		return "";
		
		
	}
		
	

	public String getCookies(Request req, Response res) {
		try{
			Statement stmt = conn.createStatement();
			String sql = "SELECT pName AS name FROM Products;";
			ResultSet rs = stmt.executeQuery(sql);
			return JSONizer.toJSON(rs, "cookies");
		}
		catch (SQLException e) {
			e.printStackTrace();
		}
		return "";
	}

	public String getRecipes(Request req, Response res) {
		try{
			Statement stmt = conn.createStatement();
			String sql = "SELECT pName AS cookie, ingridient AS raw_material, amount, unit FROM Ingridients natural join Warehouse order by pName;";
			ResultSet rs = stmt.executeQuery(sql);
			return JSONizer.toJSON(rs, "recipes");
			
		}
		catch (SQLException e) {
			e.printStackTrace();
		}
		return "";
	}

	public String getPallets(Request req, Response res) { //EJ KLAR METOD SKALL ÄNDRAS
		try{
			Statement stmt = conn.createStatement();
			String sql = "SELECT Pallets.id, pName as cookie, orderDate AS production_date, company AS customer, blocked FROM Pallets, Orders WHERE Pallets.id = Orders.id";
			ResultSet rs = stmt.executeQuery(sql);
			return JSONizer.toJSON(rs, "pallets");
		}
		catch (SQLException e) {
			e.printStackTrace();
		}
		return "";
	}

	public String reset(Request req, Response res) {
		return "{}";
		//truncate table t, längst ned i föreläsningarna
		// sätt quantity 500000 i Warehouse
	}

	public String createPallet(Request req, Response res) {
		try{
			//String id = req.params("id");
			String cookie = req.queryParams("cookie");
			//String pdate = req.params("production_date");
			//String cust = req.params("customer");
			//String blocked = req.params("blocked");
			
			Statement stmt = conn.createStatement();
			//String sql = "SELECT pName AS cookie from Products where pName LIKE '" + cookie +"' ";
			String sql = "SELECT pName AS cookie from Products where pName = " + cookie +";";
				ResultSet findcookie = stmt.executeQuery(sql);
				if(findcookie.next()){
					String sql2 = "insert into Pallets values(?,?,?,?,?)";
					PreparedStatement ps = conn.prepareStatement(sql2);
					ps.setInt(1, 1);
					ps.setString(2, "no");
					ps.setString(3, cookie);
					ps.setString(4, "NULL");
					String date = "select CURRENT_TIMESTAMP();";
					Statement stmt2 = conn.createStatement();
					ResultSet getdate = stmt2.executeQuery(date);
					ps.setString(5, getdate.getString(1));
					
					ResultSet getid = ps.executeQuery();
					int id = getid.getInt(1);
					
					String updateWarehouse = "update Warehouse, Ingridients set quantity = quantity - (amount*54)"
							+ " where Warehouse.ingridient = Ingridients.ingridient and Ingridients.pName = " + cookie +"; ";
				
					Statement updateW = conn.createStatement();
					updateW.executeQuery(updateWarehouse);
					
				
					return "{ \"status\": \"ok\" \n" + "  \"id\": " + id +  " }\n";
					
					
				}
				else if(!findcookie.next()){
					return "{ \"status\": \"unknown cookie\" }\n";
				}
			
				return "{ \"status\": \"error\" }\n";
		}
		catch(SQLException e){
			e.printStackTrace();
		}
	
		return "";
		
	
	}

public String test(){
	return "";
	
}




}
