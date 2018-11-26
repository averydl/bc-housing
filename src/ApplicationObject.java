
class ApplicationObject {

	int applNum;
	int sid;
	String applDate;
	String startQuarter;
	String endQuarter;
	Integer prefSid;
	String firstPref;
	String secondPref;
	String thirdPref;
	
	
	public int getApplNum(){return this.applNum;}
	public int getSid() {return this.sid;}
	public String getApplDate() {return this.applDate;}
	public String getStartQuarter() {return this.startQuarter;}
	public String getEndQuarter() {return this.endQuarter;}
	public Integer getPrefSid() {return this.prefSid;}
	public String getFirstPref() {return this.firstPref;}
	public String getSecondPref() {return this.secondPref;}
	public String getThirdPref() {return this.thirdPref;}
	
	public void setApplNum(int num){this.applNum = num;}
	public void setSid(int sId){this.sid = sId;}
	public void setApplDate(String date){this.applDate = date;}
	public void setStartQuarter(String quarter){this.startQuarter = quarter;}
	public void setEndQuarter(String quarter){this.endQuarter = quarter;}
	public void setPrefSid(Integer prefSidNum) {this.prefSid = prefSidNum;}
	public void setFirstPref(String first){this.firstPref = first;}
	public void setSecondPref(String second){this.secondPref = second;}
	public void setThirdPref(String third){this.thirdPref = third;}
	
	public void printOutApplicationObject (){
		System.out.println("Application# " + this.applNum + " from " + this.applDate);
		System.out.println("SID: " + this.sid);
		System.out.println("Quarter to start: " + this.startQuarter);
		System.out.println("Quarter to end: " + this.endQuarter);
		if(this.prefSid != 0){
			System.out.println("You prefere to lease with " + this.prefSid.toString());
		}
		if(this.firstPref != null){
			System.out.println("First preference to leave in: " + this.firstPref);
		}
		if(this.secondPref != null){
			System.out.println("Second preference to leave in: " + this.secondPref);
		}
		if(this.thirdPref != null){
			System.out.println("Third preference to leave in: " + this.thirdPref);
		}		
	}
}
