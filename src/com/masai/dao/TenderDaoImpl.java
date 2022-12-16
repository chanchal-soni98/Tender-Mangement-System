package com.masai.dao;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.masai.beans.Tender;
import com.masai.utilites.DBUtil;

public class TenderDaoImpl implements TenderDao{
	
	@Override
	public List<Tender> getAllTenders() {

		List<Tender> tenderList = new ArrayList<Tender>();

		Connection con = DBUtil.provideConnection();

		PreparedStatement ps = null;
		ResultSet rs = null;

		try {
			ps = con.prepareStatement("select * from tender");

			rs = ps.executeQuery();
			while (rs.next()) {
				Tender tender = new Tender();

				tender.setTid(rs.getInt("tid"));
				tender.setTname(rs.getString("tname"));
				tender.setTtype(rs.getString("ttype"));
				tender.setTprice(rs.getInt("tprice"));
				tender.setTdesc(rs.getString("tdesc"));
				tender.setTstatus(rs.getString("tstatus"));
				tenderList.add(tender);
			}

		} catch (SQLException e) {

			System.out.println("Exception occurred....");
//			e.printStackTrace();
		} finally {

			DBUtil.closeConnection(ps);

			DBUtil.closeConnection(rs);

			DBUtil.closeConnection(con);

		}

		return tenderList;
	}

	@Override
	public String createTender(Tender tender) {

		String status = "Tender Addition Failed!";

		Connection conn = DBUtil.provideConnection();

		PreparedStatement pst = null;
		PreparedStatement ps = null;

		try {
			ps = conn.prepareStatement("select * from tender where tname=? AND ttype=? AND tprice=? AND tdesc=?");

			ps.setString(1, tender.getTname());
			ps.setString(2, tender.getTtype());
			ps.setInt(3, tender.getTprice());
			ps.setString(4, tender.getTdesc());

			ResultSet rs = ps.executeQuery();

			if (rs.next()) {
				status = "Tender Declined! \nTender already Exists with ID: " + rs.getInt("tid");
			} else {
				try {
					pst = conn.prepareStatement("insert into tender(tname,ttype,tprice,tdesc,tstatus) values(?,?,?,?,?)");
	
					pst.setString(1, tender.getTname());
					pst.setString(2, tender.getTtype());
					pst.setInt(3, tender.getTprice());
					pst.setString(4, tender.getTdesc());
					pst.setString(5, tender.getTstatus());

					int x = pst.executeUpdate();
					if (x > 0)
						status = "New Tender Inserted \nYour Tender ID: " + getTenderId(tender);
				} catch (SQLException e) {

					status = "Error : " + e.getMessage();

//					e.printStackTrace();
				}
			}

		} catch (SQLException e) {

			status = "Error : " + e.getMessage();

//			e.printStackTrace();

		} finally {

			DBUtil.closeConnection(pst);

			DBUtil.closeConnection(ps);

			DBUtil.closeConnection(conn);

		}

		return status;
	}
	
	@Override
	public int getTenderId(Tender tender) {

		int tid = -1;

		Connection conn = DBUtil.provideConnection();

		PreparedStatement ps = null;

		try {
			ps = conn.prepareStatement("select * from tender where tname=? AND ttype=? AND tprice=? AND tdesc=?");

			ps.setString(1, tender.getTname());
			ps.setString(2, tender.getTtype());
			ps.setInt(3, tender.getTprice());
			ps.setString(4, tender.getTdesc());

			ResultSet rs = ps.executeQuery();

			if (rs.next()) {
				tid = rs.getInt("tid");
			} 

		} catch (SQLException e) {

			System.out.println("Exception occurred....");
//			e.printStackTrace();

		} finally {

			DBUtil.closeConnection(ps);

			DBUtil.closeConnection(conn);

		}

		return tid;
	}

//	@Override
//	public boolean removeTender(int tid) {
//
//		boolean flag = false;
//
//		Connection con = DBUtil.provideConnection();
//
//		PreparedStatement ps = null;
//		
//		try {
//
//			ps = con.prepareStatement("delete from tender where tid=?");
//
//			ps.setInt(1, tid);
//
//			int x = ps.executeUpdate();
//
//			if (x > 0) {
//
//				flag = true;
//
//			}
//		} catch (SQLException e) {
//			e.printStackTrace();
//		} finally {
//
//			DBUtil.closeConnection(ps);
//
//			DBUtil.closeConnection(con);
//
//		}
//
//		return flag;
//	}

	@Override
	public String updateTender(Tender tender) {

		String status = "Tender Updation Failed!";

		Connection con = DBUtil.provideConnection();

		PreparedStatement pst = null;

		try {
			pst = con.prepareStatement(
					"UPDATE tender SET tname=?,ttype=?,tprice=?,tdesc=?, tstatus=? where tid=?");

			pst.setString(1, tender.getTname());
			pst.setString(2, tender.getTtype());
			pst.setInt(3, tender.getTprice());
			pst.setString(4, tender.getTdesc());
			pst.setString(5, tender.getTstatus());
			
			pst.setInt(6, tender.getTid());
			
			int x = pst.executeUpdate();
			if (x > 0)
				status = "TENDER DETAILS UPDATED SUCCSESFULLY";

		} catch (SQLException e) {
			status = "Error: " + e.getMessage();
//			e.printStackTrace();
		} finally {

			DBUtil.closeConnection(pst);

			DBUtil.closeConnection(con);

		}

		return status;
	}

	@Override
	public Tender getTenderDataById(int tid) {

		Tender tender = null;

		Connection con = DBUtil.provideConnection();

		PreparedStatement ps = null;

		try {

			ps = con.prepareStatement("select * from tender where tid=?");

			ps.setInt(1, tid);

			ResultSet rs = ps.executeQuery();

			if (rs.next()) {
				int id = rs.getInt(1);
				String name = rs.getString(2);
				String type = rs.getString(3);
				int price = rs.getInt(4);
				String desc = rs.getString(5);
				String status = rs.getString(6);

				tender = new Tender(id, name, type, price, desc, status);
			}

		} catch (SQLException e) {
			// TODO Auto-generated catch block
//			e.printStackTrace();

			System.out.println("Exception occurred....");
		} finally {

			DBUtil.closeConnection(ps);

			DBUtil.closeConnection(con);

		}

		return tender;
	}

	@Override
	public String getTenderStatus(int tenderId) {

		String status = "Not Assigned";

		Connection con = DBUtil.provideConnection();

		PreparedStatement ps = null;

		ResultSet rs = null;

		try {
			ps = con.prepareStatement("select * from tender where tid=?");

			ps.setInt(1, tenderId);

			rs = ps.executeQuery();

			if (rs.next()) {
				status = rs.getString("tstatus");
			} else {
				status = "Tendor Id Not Found: " + tenderId;
			}

		} catch (SQLException e) {
			status = "Error: " + e.getMessage();
//			e.printStackTrace();
		} finally {

			DBUtil.closeConnection(con);
			DBUtil.closeConnection(ps);
			DBUtil.closeConnection(rs);

		}
		return status;
	}

	@Override
	public String assignTender(int tenderId) {

		String status = "Tender Assigning failed";

		Connection con = DBUtil.provideConnection();

		PreparedStatement ps = null;
		ResultSet rs = null;

		try {
			ps = con.prepareStatement("update tender set tstatus='Assigned' where tid=?");
			ps.setInt(1, tenderId);

			int k = ps.executeUpdate();
			if (k > 0) {
				status = "Tender: " + tenderId + " has been Assigned";
			}

		} catch (SQLException e) {
			status = status + e.getMessage();
//			e.printStackTrace();
		} finally {
			DBUtil.closeConnection(ps);
			DBUtil.closeConnection(rs);
			DBUtil.closeConnection(con);
		}

		return status;
	}

	@Override
	public List<Tender> getAllAssignedTenders() {

		List<Tender> statusList = new ArrayList<Tender>();

		Connection con = DBUtil.provideConnection();

		PreparedStatement ps = null;

		ResultSet rs = null;

		try {

			ps = con.prepareStatement("select * from tender where tstatus='Assigned'");

			rs = ps.executeQuery();

			while (rs.next()) {

				Tender tender = new Tender();

				tender.setTid(rs.getInt("tid"));
				tender.setTname(rs.getString("tname"));
				tender.setTtype(rs.getString("ttype"));
				tender.setTprice(rs.getInt("tprice"));
				tender.setTdesc(rs.getString("tdesc"));
				tender.setTstatus(rs.getString("tstatus"));

				statusList.add(tender);
			}

		} catch (SQLException e) {

			System.out.println("Exception occurred....");
//			e.printStackTrace();
		}

		finally {

			DBUtil.closeConnection(con);

			DBUtil.closeConnection(ps);

			DBUtil.closeConnection(rs);

		}

		return statusList;
	}

}
