package com.masai.dao;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.masai.beans.Bidder;
import com.masai.utilites.DBUtil;

public class BidderDaoImpl implements BidderDao {
	
	@Override
	public String acceptBid(int tenderId) {
		
		String status = "Bid Assignment Failed";

		Connection con = DBUtil.provideConnection();

		PreparedStatement ps = null;
		PreparedStatement pst = null;
		ResultSet rs = null;

		try {

			ps = con.prepareStatement("select * from tender where tid=? AND tstatus='Assigned'");
			ps.setInt(1, tenderId);
			rs = ps.executeQuery();
			if (rs.next()) {
				status = "Tender Already Assigned";
			} else {
				
				Bidder bid = bestBids(tenderId);
				
				if(bid==null) {
					status = "No Bids for the Tendor is Found";
				}else {
					pst = con.prepareStatement("update bidder set status = ? where bid=? and status=?");

					pst.setString(1, "Accepted");
					pst.setInt(2, bid.getBid());
					pst.setString(3, "Pending");

					int x = pst.executeUpdate();
					if (x > 0) {
						status = "Bid Has Been Accepted Successfully!";
						TenderDao dao = new TenderDaoImpl();
						status = status + "\n" + dao.assignTender(tenderId);
					}
				}
				
				
			}
		} catch (SQLException e) {

			status = status + "Error: " + e.getMessage();
//			e.printStackTrace();
		} finally {

			DBUtil.closeConnection(con);

			DBUtil.closeConnection(ps);
			
			DBUtil.closeConnection(pst);
		}
		return status;
	}

	@Override
	public String rejectBid(int tenderId) {
		
		String status = "Bid Rejection Failed";

		Connection con = DBUtil.provideConnection();

		PreparedStatement ps = null;

		try {
			ps = con.prepareStatement("update bidder set status = ? where tid=? and status = ?");

			ps.setString(1, "Rejected");
			ps.setInt(2, tenderId);
			ps.setString(3, "Pending");

			int x = ps.executeUpdate();
			if (x > 0)
				status = "Bid Has Been Rejected Successfully!";

		} catch (SQLException e) {

			status = status + "Error: " + e.getMessage();
//			e.printStackTrace();
		} finally {

			DBUtil.closeConnection(con);

			DBUtil.closeConnection(ps);
		}
		return status;

	}

	@Override
	public String bidTender(Bidder bidder) {

		String status = "Tender Bidding Failed!";
		
		Connection con = DBUtil.provideConnection();

		PreparedStatement ps = null;

		try {
			ps = con.prepareStatement("insert into bidder(vid,tid,bidAmount,status,biddate) values(?,?,?,?,sysdate())");

			ps.setString(1, bidder.getVid());

			ps.setInt(2, bidder.getTid());

			ps.setInt(3, bidder.getBidAmount());

			ps.setString(4, bidder.getStatus());

			int x = ps.executeUpdate();

			if (x > 0)
				status = "You have successfully Bid for the tender";

		} catch (SQLException e) {
			status = status + " Duplicate Bid Found or Invalid Tender Details Found";
//			e.printStackTrace();
		} finally {

			DBUtil.closeConnection(con);

			DBUtil.closeConnection(ps);
		}

		return status;
	}

	@Override
	public List<Bidder> getAllBidsOfaTender(int tenderId) {

		List<Bidder> bidderList = new ArrayList<Bidder>();

		Connection con = DBUtil.provideConnection();
		PreparedStatement ps = null;
		ResultSet rs = null;

		try {

			ps = con.prepareStatement("select * from bidder where tid=?");

			ps.setInt(1, tenderId);

			rs = ps.executeQuery();

			while (rs.next()) {
				Bidder bidder = new Bidder();

				bidder.setBidAmount(rs.getInt("bidamount"));
				bidder.setBid(rs.getInt("bid"));
				bidder.setStatus(rs.getString("status"));
				bidder.setTid(rs.getInt("tid"));
				bidder.setVid(rs.getString("vid"));

				bidderList.add(bidder);
			}

		} catch (SQLException e) {
//			System.out.println("No Bids Found.........");
//			e.printStackTrace();

			System.out.println("Exception occurred....");
		} finally {

			DBUtil.closeConnection(con);

			DBUtil.closeConnection(ps);

			DBUtil.closeConnection(rs);
		}

		return bidderList;
	}

	@Override
	public List<Bidder> getAllBidsOfaVendor(String vendorId) {
		List<Bidder> bidderList = new ArrayList<Bidder>();

		Connection con = DBUtil.provideConnection();
		PreparedStatement ps = null;
		ResultSet rs = null;

		try {

			ps = con.prepareStatement("select * from bidder where vid=?");

			ps.setString(1, vendorId);

			rs = ps.executeQuery();

			while (rs.next()) {
				Bidder bidder = new Bidder();
				
				bidder.setBid(rs.getInt("bid"));
				bidder.setVid(rs.getString("vid"));
				bidder.setTid(rs.getInt("tid"));
				bidder.setBidAmount(rs.getInt("bidamount"));
				bidder.setStatus(rs.getString("status"));				

				bidderList.add(bidder);
			}

		} catch (SQLException e) {

			System.out.println("Exception occurred....");
//			e.printStackTrace();
		} finally {

			DBUtil.closeConnection(con);

			DBUtil.closeConnection(ps);

			DBUtil.closeConnection(rs);
		}

		return bidderList;
	}

	@Override
	public String getStatusOfABid(int tid,String vendorId) {

		String status = "Bid Not Found";

		Connection con = DBUtil.provideConnection();
		PreparedStatement ps = null;
		ResultSet rs = null;

		try {

			ps = con.prepareStatement("select * from bidder where tid=? AND vid=?");

			ps.setInt(1, tid);
			ps.setString(2, vendorId);

			rs = ps.executeQuery();

			if (rs.next()) {
				status = rs.getString("status");
			}

		} catch (SQLException e) {
			status = status + " Exception Occured";
//			e.printStackTrace();
		} finally {

			DBUtil.closeConnection(con);

			DBUtil.closeConnection(ps);

			DBUtil.closeConnection(rs);
		}

		return status;

	}

	@Override
	public Bidder bestBids(int tendorId) {
		
		Bidder bidder = null;
		
		Connection con = DBUtil.provideConnection();
		PreparedStatement ps = null;
		ResultSet rs = null;

		try {

			ps = con.prepareStatement("select * from bidder where tid=? AND bidAmount = "
					+ "(select min(bidAmount) from bidder where tid=?) AND biddate = "
					+ "(select min(biddate) from bidder where tid=?)");

			ps.setInt(1, tendorId);
			ps.setInt(2, tendorId);
			ps.setInt(3, tendorId);

			rs = ps.executeQuery();

			if (rs.next()) {
				bidder = new Bidder();
				
				bidder.setBid(rs.getInt("bid"));
				bidder.setVid(rs.getString("vid"));
				bidder.setTid(rs.getInt("tid"));
				bidder.setBidAmount(rs.getInt("bidamount"));
				bidder.setStatus(rs.getString("status"));
			}

		} catch (SQLException e) {

			System.out.println("Exception occurred....");
//			e.printStackTrace();
		} finally {

			DBUtil.closeConnection(ps);

			DBUtil.closeConnection(rs);
		}

		
		return bidder;
	}

}
