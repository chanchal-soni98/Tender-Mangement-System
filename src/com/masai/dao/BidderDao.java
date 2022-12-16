package com.masai.dao;
import java.util.List;

import com.masai.beans.Bidder;

public interface BidderDao {
	
	public String acceptBid(int tenderId);

	public String rejectBid(int tendorId);

	public String bidTender(Bidder bidder);

	public List<Bidder> getAllBidsOfaTender(int tenderId);

	public List<Bidder> getAllBidsOfaVendor(String vendorId);

	public String getStatusOfABid(int tid,String vendorId);
	
	public Bidder bestBids(int tendorId); 
	

}
