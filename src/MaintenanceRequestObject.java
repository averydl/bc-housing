
class MaintenanceRequestObject {

	private int requestId;
	private String description;
	private String requestDate;
	
	public int getRequestNum(){return this.requestId;}
	public String getDescription(){return this.description;}
	public String getDate(){return this.requestDate;}
	
	public void setRequestNum(int rId){this.requestId = rId;}
	public void setDescription(String descr){this.description = descr;}
	public void setDate(String date){this.requestDate = date;}
	
	public void printOutRequesrObject(){
		System.out.println("Request# " + this.requestId);
		System.out.println("Creation date: " + this.requestDate);
		System.out.println("Reason: " + this.description);	
	}	
}
