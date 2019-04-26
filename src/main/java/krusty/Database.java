package krusty;

import spark.Request;
import spark.Response;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

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
		try {
			Statement stmt = conn.createStatement();
			String sql = "SELECT ingridient AS name, quantity AS amount, unit FROM Warehouse;";
			ResultSet rs = stmt.executeQuery(sql);
			return JSONizer.toJSON(rs, "raw-materials");
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return "";

	}

	public String getCookies(Request req, Response res) {
		try {
			Statement stmt = conn.createStatement();
			String sql = "SELECT pName AS name FROM Products;";
			ResultSet rs = stmt.executeQuery(sql);
			return JSONizer.toJSON(rs, "cookies");
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return "";
	}

	public String getRecipes(Request req, Response res) {
		try {
			Statement stmt = conn.createStatement();
			String sql = "SELECT pName AS cookie, ingridient AS raw_material, amount, unit FROM Ingridients natural join Warehouse order by pName;";
			ResultSet rs = stmt.executeQuery(sql);
			return JSONizer.toJSON(rs, "recipes");

		} catch (SQLException e) {
			e.printStackTrace();
		}
		return "";
	}

	public String getPallets(Request req, Response res) {
		int n = 0;

		String sql = "SELECT pId as id, pName as cookie, production_date, company AS customer, blocked FROM Pallets left outer join Orders on pId = Orders.id ";

		ArrayList<String> values = new ArrayList<String>();

		if (req.queryParams("from") != null) {
			String from = req.queryParams("from");
			if (n == 0) {
				sql += "WHERE production_date >= ? ";
			} else if (n > 0) {
				sql += "and production_date >= ? ";
			}
			values.add(from);
			n++;
		}

		if (req.queryParams("to") != null) {
			String to = req.queryParams("to");
			if (n == 0) {
				sql += "WHERE production_date <= ? ";
			} else if (n > 0) {
				sql += "and production_date <= ? ";
			}
			values.add(to);
			n++;
		}
		if (req.queryParams("cookie") != null) {
			String cookie = req.queryParams("cookie");
			if (n == 0) {
				sql += "WHERE pName = ? ";
			} else if (n > 0) {
				sql += "and pName = ? ";
			}
			values.add(cookie);
			n++;
		}
		if (req.queryParams("blocked") != null) {
			String blocked = req.queryParams("blocked");
			if (n == 0) {
				sql += "WHERE blocked = ? ";
			} else if (n > 0) {
				sql += "and blocked = ? ";
			}

			values.add(blocked);
			n++;
		}
		sql += "order by production_date DESC;";
		try (PreparedStatement ps = conn.prepareStatement(sql)) {
			for (int i = 0; i < values.size(); i++) {
				ps.setString(i + 1, values.get(i));
			}
			ResultSet rs = ps.executeQuery();
			return JSONizer.toJSON(rs, "pallets");
		}

		catch (SQLException e) {
			e.printStackTrace();
		}
		return "";
	}

	public String reset(Request req, Response res) {
		try {
			Statement stmt = conn.createStatement();
			String sql = "Truncate table Pallets;";
			stmt.executeUpdate(sql);

			String update = "Update Warehouse set quantity = ? where 1=1";

			PreparedStatement ps = conn.prepareStatement(update);

			ps.setInt(1, 500000);

			ps.executeUpdate();

		} catch (SQLException e) {

		}
		return "";

	}

	public String createPallet(Request req, Response res) {
		try {

			String cookie = req.queryParams("cookie");

			String sql = "select pName from Products where pName IN(select pName from Products) and pName = ?";

			PreparedStatement p = conn.prepareStatement(sql);
			p.setString(1, cookie);

			ResultSet findcookie = p.executeQuery();
			if (findcookie.next()) {

				String sql2 = "insert into Pallets values(?,?,?,?,?)";
				PreparedStatement ps = conn.prepareStatement(sql2, Statement.RETURN_GENERATED_KEYS);
				ps.setInt(1, 0);
				ps.setString(2, "no");
				ps.setString(3, cookie);
				ps.setObject(4, null);
				ps.setTimestamp(5, java.sql.Timestamp.valueOf(java.time.LocalDateTime.now()));

				ps.executeUpdate();

				String updateWarehouse = "update Warehouse, Ingridients set quantity = quantity - (amount*54)"
						+ " where Warehouse.ingridient = Ingridients.ingridient and Ingridients.pName = '" + cookie
						+ "';";

				Statement updateW = conn.createStatement();
				updateW.executeUpdate(updateWarehouse);

				ResultSet rs2 = ps.getGeneratedKeys();

				int id = 0;
				if (rs2.next()) {
					id = rs2.getInt(1);
				}

				return "{ \"status\": \"ok\" \n" + "  \"id\": " + id + " }\n";

			} else if (!findcookie.next()) {
				return "{ \"status\": \"unknown cookie\" }\n";
			}

			return "{ \"status\": \"error\" }\n";
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return "";

	}

}
