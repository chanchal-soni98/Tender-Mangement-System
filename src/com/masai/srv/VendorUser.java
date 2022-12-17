package com.masai.srv;
import java.util.List;
import java.util.Scanner;

import com.masai.beans.Bidder;
import com.masai.beans.Tender;
import com.masai.beans.Vendor;
import com.masai.dao.BidderDao;
import com.masai.dao.BidderDaoImpl;
import com.masai.dao.TenderDao;
import com.masai.dao.TenderDaoImpl;
import com.masai.dao.VendorDao;
import com.masai.dao.VendorDaoImpl;
public class VendorUser extends User{
	
	public VendorUser() {
		super();
	}

	public VendorUser(String username, String password) {
		super(username, password);
	}

	public boolean loginVendor() {

		VendorDao vdao = new VendorDaoImpl();

		if (vdao.validatePassword(getUsername(), getPassword())) {

			System.out.println("***************************************");
			System.out.println("Login Succesfull");
			System.out.println("***************************************");
			return true;
		} else {
			System.out.println("***************************************");
			System.out.println("Invalid User Details");
			System.out.println("***************************************");
			return false;
		}
	}

	public void updateProfile() {

		Scanner sc = new Scanner(System.in);

		Vendor vendor = new Vendor(this.getUsername(), this.getPassword());

		System.out.println("Enter Vender Details:");
		System.out.println("Enter Vendor's Name:");
		vendor.setVname(sc.nextLine());

		System.out.println("Enter vendor's Email:");
		vendor.setVemail(sc.nextLine());

		System.out.println("Enter vendor's Company:");
		vendor.setCompany(sc.nextLine());

		System.out.println("Enter vendor's Mobile:");
		vendor.setVmob(sc.nextLine());

		System.out.println("Enter vendor's Address");
		vendor.setAddress(sc.nextLine());

		String status = new VendorDaoImpl().updateProfile(vendor);

		System.out.println("***************************************");
		System.out.println(status);
		System.out.println("***************************************");
	}

	public void viewAllCurrentTendors() {

		TenderDao tdao = new TenderDaoImpl();

		List<Tender> tenders = tdao.getAllTenders();

		if (tenders.isEmpty()) {
			System.out.println("***************************************");
			System.out.println("No Tenders Found");
			System.out.println("***************************************");
		} else {

			System.out.println("***************************************");
			System.out.println("List of ALL Current Not Assigned Tenders: ");
			System.out.println("***************************************");
			int count = 1;
			for (int i = 0; i < tenders.size(); i++) {
				Tender t = tenders.get(i);
				if (t.getTstatus().equalsIgnoreCase("Not Assigned")) {

					System.out.println(count + " Tender Details:");
					System.out.println("*ID: " + t.getTid());
					System.out.println("*Title: " + t.getTname());
					System.out.println("*Price: " + t.getTprice());
					System.out.println("*Type: " + t.getTtype());
					System.out.println("*Description: " + t.getTdesc());
					System.out.println("*Status: " + t.getTstatus());
					System.out.println("***************************************");

					count++;
				}

			}
		}

	}

	public void bidTender() {

		Scanner sc = new Scanner(System.in);

		try {
			System.out.println("Enter Tender Id");
			int tid = Integer.parseInt(sc.nextLine());

			Tender t = new TenderDaoImpl().getTenderDataById(tid);

			if (t == null || t.getTstatus().equalsIgnoreCase("Assigned")) {

				System.out.println("***************************************");
				System.out.println("Tender: " + tid + " not Found");
				System.out.println("***************************************");
			} else {
				System.out.println("Enter Bid Amount");
				int bidAmount = Integer.parseInt(sc.nextLine());

				BidderDao bdao = new BidderDaoImpl();

				String status = bdao.bidTender(new Bidder(0, this.getUsername(), tid, bidAmount, "Pending"));

				System.out.println("***************************************");
				System.out.println(status);
				System.out.println("***************************************");
			}
		} catch (NumberFormatException e) {
			System.out.println("Invalid Input");
		}

	}

	public void getAllBidsOfaVendor() {

		BidderDao bdao = new BidderDaoImpl();

		List<Bidder> bids = bdao.getAllBidsOfaVendor(this.getUsername());

		if (bids.isEmpty()) {
			System.out.println("***************************************");
			System.out.println("No Bids Found");
			System.out.println("***************************************");
		} else {
			System.out.println("***************************************");
			System.out.println("List of Bids For Vendor: " + this.getUsername());
			System.out.println("***************************************");
			int count = 1;
			for (int i = 0; i < bids.size(); i++) {
				Bidder b = bids.get(i);
				System.out.println(count + " Bids Details:");
				System.out.println("*Bid ID: " + b.getBid());
				System.out.println("*Tender ID: " + b.getTid());
				System.out.println("*Vendor ID: " + b.getVid());
				System.out.println("*Bid Amount: " + b.getBidAmount());
				System.out.println("*Bid Status: " + b.getStatus());
				System.out.println("***************************************");

				count++;
			}
		}
	}

	public void getStatusOfABid() {

		Scanner sc = new Scanner(System.in);

		try {
			System.out.println("Enter the Tender Id to get Its Bid Status");
			int tid = Integer.parseInt(sc.nextLine());
			BidderDao bdao = new BidderDaoImpl();

			String status = bdao.getStatusOfABid(tid, this.getUsername());

			System.out.println("***************************************");
			System.out.println("Status for Bid on Tender id : " + tid + " is " + status);
			System.out.println("***************************************");
		} catch (NumberFormatException e) {
			System.out.println("Invalid Input");
		}


    }
}
