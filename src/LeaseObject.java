
class LeaseObject {
	
	int leaseId;
	int applNum;
	String startDate;
	String endDate;
	float cost;
	int bedNum;
	int unitNum;
	
	public int getLeaseNum(){return this.leaseId;}
	public int getApplNum(){return this.applNum;}
	public int getBedNum(){return this.bedNum;}
	public int getUnitNum(){return this.unitNum;}
	public float getCost(){return this.cost;}
	public String getStartDate(){return this.startDate;}
	public String getEndDate(){return this.endDate;}
	
	public void setLeaseNum(int lId){this.leaseId = lId;}
	public void setApplNum(int num){this.applNum = num;}
	public void setBedNum(int bNum){this.bedNum = bNum;}
	public void setUnitNum(int uNum){this.unitNum = uNum;}
	public void setCost(float price){this.cost = price;}
	public void setStartDate(String date){this.startDate = date;}
	public void setEndDate(String date){this.endDate = date;}
	
	public void printOutLeaseObject(){
		System.out.println("Lease# " + this.leaseId);
		System.out.println("Start date: " + this.startDate);
		System.out.println("End date: " + this.endDate);
		System.out.println("Adress: 3000 Landerholm Cir SE");
		System.out.println("Appartment #: " + this.unitNum + "/" + this.bedNum);
		System.out.println("Lease price: " + this.cost);	
	}
	
}
