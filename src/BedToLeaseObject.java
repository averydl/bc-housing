
class BedToLeaseObject {
	private int unitNum;
	private int bedNum;
	private int bedType;
	private String occupancy;
	
	public int getUnitNum() {return this.unitNum;}
	public int getBedNum() {return this.bedNum;}
	public int getBedType() {return this.bedType;}
	public String getOccupancy() {return this.occupancy;}
	
	public void setUnitNum(int unit){this.unitNum = unit;}
	public void setBedNum(int bed){this.bedNum = bed;}
	public void setBedType(int type){this.bedType = type;}
	public void setOccupancy(String occupancyIndex){this.occupancy = occupancyIndex;}
	
	public void printOutBedToLeaseObject (){
		System.out.print("bed# " + this.bedNum + "; unit# " + this.unitNum); 
		if(this.occupancy.equals("FREE")){
			System.out.print(" unit is fully available");
		}	
	}

}
