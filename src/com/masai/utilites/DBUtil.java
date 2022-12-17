package com.masai.utilites;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DBUtil {
	
	private static Connection con = null;

//	public DBUtil() {
//
//	}

	public static Connection provideConnection() {
		
		try {

			if (con == null || con.isClosed() == true) {

				try {
					Class.forName("com.mysql.cj.jdbc.Driver");
				} catch (ClassNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				String url = "jdbc:mysql://localhost:3306/cw";

				con = DriverManager.getConnection(url, "root", "Soni@123");

			}

		} catch (SQLException e) {

			e.printStackTrace();
		}

		return con;
	}

	public static void closeConnection(PreparedStatement ps) {

		try {
			if (ps != null)
				ps.close();
		} catch (SQLException e) {

			e.printStackTrace();
		}

	}

	public static void closeConnection(ResultSet rs) {

		try {
			if (rs != null)
				rs.close();
		} catch (SQLException e) {

			e.printStackTrace();
		}

	}

	public static void closeConnection(Connection con) {

		try {
			if (con != null)
				con.close();
		} catch (SQLException e) {

			e.printStackTrace();
		}

	}

}
