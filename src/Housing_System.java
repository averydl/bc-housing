import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Scanner;
import java.io.*;

public class Housing_System {

	public static void main(String[] args) {
		Scanner in = new Scanner(System.in);
		Connection conn = null;
		Integer user = null;
		boolean stayIn = true;
		
		// run general "hello" menu	
		try{
			while (stayIn == true){
				int userType = mainManuSelection(in);
				
				if(userType == 3){
					System.out.println("Thank you for using our service!");
					stayIn = false;
				}
				else{
					conn = openConnection();
					user = createUserConnection(in, conn);
					if (user == null){
						System.out.println("Wrong input. Try again:");
						System.out.println();
					}
					else{
						switch(userType){
						case(1):
							//System.out.println(user);  //to check
							//check if student has application
							ApplicationObject application = getCurrentApplication(user,conn);
							LeaseObject lease = getCurrentLease(user, conn);
							List <MaintenanceRequestObject> mRequest = getCurrentRequests(user, conn);
							
							if(lease != null){
								System.out.println("You have active lease.");
								lease.printOutLeaseObject();
								System.out.println();
								
								if(!mRequest.isEmpty()){
									System.out.println("You have active maintenance request:");
									for (MaintenanceRequestObject temp : mRequest) {
										temp.printOutRequesrObject();
										System.out.println();
									}
								}
							}
							
							boolean currentApplExist = false;
							if (application != null){
								currentApplExist = true;
								System.out.println("You have active application.");
								application.printOutApplicationObject();
								System.out.println();								
							}
							
							int selectNextAction = studentManuSelection(in, currentApplExist);
							switch(selectNextAction){
								case 3:
									stayIn = false;
									break;
								case 4:
									int [] preferences = updateCurrentApplication(in, conn, application);
									
									System.out.println();
									System.out.println("Your new application: ");
									application.printOutApplicationObject();
									System.out.println();
															
									System.out.println("To save changes pless 1, to skip press 0: ");
									String updateAgree = in.next();
									if(updateAgree.equals("1")){
										updateApplicationInDB(conn, preferences, application);
									}
									else{
										in.next();
									}
									
									break;
									
								case 5:
									System.out.println("Are you sure you want to cancel your application? (Y/N)");
									String dessision = in.next();
									if(dessision.toUpperCase().equals("Y")){
										cancelApplicationInDB(conn, application);
									}
									else{
										in.next();
									}
									break;
									
								case 6:
									ApplicationObject newApplication = new ApplicationObject();
									int[] roomPreferences = createNewApplication(in, conn, newApplication);
									
									System.out.println();
									System.out.println("Your application: ");
									newApplication.printOutApplicationObject();
									
									
									System.out.println("To save your application press 1");
									String saveAgree = in.next();
									if(saveAgree.toUpperCase().equals("1")){
										setNewApplicationToDB(conn, roomPreferences, newApplication);
									}
									else{
										in.next();
									}
									break;
							}
												
							break;
						case 2:
							//query menu for admin;
							
							break;
						}
					}
				}
			}
//			System.out.println("Thank you for using our service!");
		}
		catch(SQLException | ClassNotFoundException ex) {
		System.out.println(ex);
		} 
		finally {
			if (conn != null) {
				try {
					conn.close();
				}catch (SQLException e) { /* ignored */};
			}
			in.close();
		}
	}


	public static int mainManuSelection(Scanner in) {
		boolean validSelect = false;
		
		System.out.println("****************************************************************************************");
		System.out.println("                        Welcome to the Housing System");
		System.out.println("****************************************************************************************");
		System.out.println("                                1. Student Login");
		System.out.println("                      	        2. Admin");
		System.out.println("                                3. Quit");
		System.out.println();
		
		int select = 0;
		
		while (validSelect == false){
			System.out.println("Type in your option:");
			try{
				select = in.nextInt();
			}catch(InputMismatchException e){
				select = 0;
			}			
			if (select > 0 & select < 4) {
				validSelect = true;
			}
			else{
				// dump unnecessary input
				in.next();
				System.out.println("Wrong input. Please, try again.");
			}
		}
		return select;
	}

	public static Integer createUserConnection(Scanner in, Connection conn) throws SQLException {
		Integer userId = null;
		String password = "";
		boolean validInput = false;
		
		//input and validation user's id
		while(validInput == false){
			System.out.println("Enter ID: ");
			try{
//				userId = 555555555;
				userId = 120000000;
//				userId = in.nextInt();
				validInput = idLenghtValidation(userId);
				if(validInput == false){
					System.out.println("No such id exists. Try again.\n");
				}
			}catch(InputMismatchException e){
				System.out.println("you need to input number id. Try again.\n");
				in.next();
			}
			
		}
		
		//input password
		System.out.println("Enter password: ");
//		password = "Student";
		password = "Swords";
//		password = in.next();
		
//maybe go to procedure**************************************************************
		String query = "SELECT * FROM personVerification WHERE bc_id = ? AND password = ?";
		PreparedStatement st = conn.prepareStatement(query);
		st.clearParameters();
		st.setInt(1,userId);
		st.setString(2,password);
		ResultSet result = st.executeQuery();
		if(result.next() == false){
			userId = null;
		}
		return userId;		
	}

	private static Connection openConnection() throws ClassNotFoundException, SQLException {
		Class.forName("com.mysql.cj.jdbc.Driver");
		String url = "jdbc:mysql://localhost:3306/BCHousing?serverTimezone=UTC&useSSL=TRUE";
		
		//hurdcode to create connection to VM DB
		Connection conn = DriverManager.getConnection(url, "student", "password");
		
		return conn;
	}
	
	//check the length of id input
	static boolean idLenghtValidation (int id){
		return (9 == String.valueOf(id).length());
	}

	public static ApplicationObject getCurrentApplication(Integer userId, Connection conn) throws SQLException{
		CallableStatement cStmt = conn.prepareCall("{call getActiveStudentApplication(?)}");
		cStmt.setInt(1, userId);
		ResultSet result = cStmt.executeQuery();
		
		ApplicationObject application = null;
		
		if (result.next()) {
			application = new ApplicationObject();
			application.setApplNum(result.getInt("appl_num"));
			application.setApplDate(result.getString("appl_date"));
			application.setStartQuarter(result.getString("start_quarter"));
			application.setEndQuarter(result.getString("end_quarter"));
			application.setPrefSid(result.getInt("pref_sid"));
			application.setSid(userId);		
			application = getApplicationPreferences(conn, application);
		}
		
		return application;
	}
	
	private static ApplicationObject getApplicationPreferences(Connection conn, ApplicationObject appl) throws SQLException{
		CallableStatement cStmt = conn.prepareCall("{call getApplicationPreferences(?)}");
		int applNum = appl.getApplNum();
		cStmt.setInt(1, applNum);
		ResultSet result = cStmt.executeQuery();
		
		String[] pref = new String[3];
		int index = 0;
		while (result.next()) {
			pref[index] = getBedTypeDescription(result.getInt("bd_id"));
			index++;
		}
		
		appl.setFirstPref(pref[0]);
		appl.setSecondPref(pref[1]);
		appl.setThirdPref(pref[2]);
		
		return appl;
	}
	
	private static LeaseObject getCurrentLease(Integer userId, Connection conn) throws SQLException{
		CallableStatement cStmt = conn.prepareCall("{call getActiveStudentLease(?)}");
		cStmt.setInt(1, userId);
		ResultSet result = cStmt.executeQuery();
		
		LeaseObject lease = null;
		
		if (result.next()) {
			lease = new LeaseObject();
			lease.setLeaseNum(result.getInt("lid"));
			lease.setApplNum(result.getInt("appl_num"));
			lease.setBedNum(result.getInt("bed_num"));
			lease.setUnitNum(result.getInt("unit_num"));
			lease.setCost(result.getFloat("cost"));
			lease.setStartDate(result.getString("start_date"));
			lease.setEndDate(result.getString("end_date"));
		}
		
		return lease;
	}
		
	private static List<MaintenanceRequestObject> getCurrentRequests(Integer userId, Connection conn) throws SQLException{
		List <MaintenanceRequestObject> requests = new ArrayList <MaintenanceRequestObject> ();
		
		CallableStatement cStmt = conn.prepareCall("{call getActiveRequests(?)}");
		cStmt.setInt(1, userId);
		ResultSet result = cStmt.executeQuery();
		
		while (result.next()) {
			MaintenanceRequestObject newRequest = new MaintenanceRequestObject();
			newRequest.setRequestNum(result.getInt("rid"));
			newRequest.setDescription(result.getString("description"));
			newRequest.setDate(result.getString("date_sent"));
			
			requests.add(newRequest);
		}
		
		return requests;
	}
	
	public static int studentManuSelection(Scanner in, boolean currentApplExist) {
		boolean validSelect = false;
		
		System.out.println("To choose your next action please press");
		if (currentApplExist == true){
			System.out.println("4. Edit your application" );
			System.out.println("5. Cansel your application");
			
		}
		else{
			System.out.println("6. Create new application");
		}
		System.out.println("3. Quit");
		System.out.println();
		
		int select = 0;
		
		while (validSelect == false){
			System.out.println("Type in your option:");
			try{
				select = in.nextInt();
			}catch(InputMismatchException e){
				select = 0;
			}			
			if (select > 2 & select < 7) {
				validSelect = true;
			}
			else{
				// dump unnecessary input
				in.next();
				System.out.println("Wrong input. Please, try again.");
			}
		}
		return select;
	}
	
	private static String getBedTypeDescription(int bedId){
		String description = "";
		switch(bedId){
		case 1:
			description = "One Bedroom Suite, One Person";
			break;
		case 2:
			description = "One Bedroom Suite, Two Persons";
			break;
		case 3:
			description = "Two Bedroom Suite, Two Persons";
			break;
		case 4:
			description = "Two Bedroom Suite, Three Persons - Shared Room";
			break;
		case 5:
			description = "Two Bedroom Suite, Three Persons - Private Room";
			break;
		case 6:
			description = "Two Bedroom Suite, Four Persons";
			break;
		case 7:
			description = "Two Bedroom Apartment, Four Persons";
			break;
		case 8:
			description = "Four Bedroom Apartment, Four Persons";
			break;
		}
		return description;
	}
	
	private static double getBedTypePrice (int bedId){
		double price = 0;
		switch(bedId){
		case 1:
			price = 4667;
			break;
		case 2:
			price = 3667;
			break;
		case 3:
			price = 3500;
			break;
		case 4:
			price = 2500;
			break;
		case 5:
			price = 3500;
			break;
		case 6:
			price = 2500;
			break;
		case 7:
			price = 3334;
			break;
		case 8:
			price = 4000;
			break;
		}
		return price;
	}
	
	private static void addApplicationPrefSid(Scanner in, Connection conn, ApplicationObject application)
			throws SQLException {
		
		boolean validId = false;
		System.out.println("You want to lease a room with: ");
		//input and validation user's id
		while(validId == false){
			try{
				Integer id = in.nextInt();
				validId = idLenghtValidation(id);
				if(validId == false){
					System.out.println("No such id exists. Try again.\n");
				}
				else if(id == application.getSid()){
					System.out.println("This is your student ID. Try again.\n");
					validId = false;
					in.next();
				}
				else{
					//check db if sid exists
					String query = "SELECT * FROM student WHERE sid = ?";
					PreparedStatement st = conn.prepareStatement(query);
					st.clearParameters();
					st.setInt(1,id);
					ResultSet result = st.executeQuery();
					if(result.next() == false){
						validId = false;
					}
					else{
						application.setPrefSid(id);
					}
				}
			}catch(InputMismatchException e){
				System.out.println("you need to input number id. Try again.\n");
				in.next();
			}
		}
	}
	
	private static int choosePrefereRoomType(Scanner in) {
		int type = 0;
		boolean validType = false;
		
		System.out.println(" preference for room type: ");
		while(validType == false){
			try{
				type = in.nextInt();
				if(type < 1 || type > 8){
					System.out.println("No such type exists. Try again.\n");
				}
				else{
					validType = true;
				}											
			}catch(InputMismatchException e){
				System.out.println("you need to input number from 1 to 8. Try again.\n");
				in.next();
			}
		}
		return type;
	}
	
	private static int[] getRoomPreferences(Scanner in) {
		int[] roomPtef = new int [3];
		
		System.out.println("Room types: ");
		for(int i = 1; i < 9; i++){
			System.out.println(i + ". " + getBedTypeDescription(i) + "Price: " + getBedTypePrice(i) + " per quarter");
		}
		System.out.println();
											
		System.out.println("Please choose your 3 room type preference.");
		
		System.out.print("Your first");
		roomPtef[0] = choosePrefereRoomType(in);
		
		
		System.out.print("Your second");
		roomPtef[1] = choosePrefereRoomType(in);
		
		
		System.out.print("Your third");
		roomPtef[2] = choosePrefereRoomType(in);
		
		return roomPtef;
	}
	
	private static boolean quarterInputVerification(String quarter) {
		return (quarter.equals("F")||quarter.equals("W")||quarter.equals("S"));
	}
	
	private static String chooseQuarter(Scanner in, String dateType ) {
		boolean validInput = false;
		String quarter = "";
		while(validInput == false){
			System.out.println("New " + dateType +" quarter (F for fall, W for winter, S for spring): ");
			quarter = in.next().toUpperCase();
			validInput = quarterInputVerification(quarter);
			if(validInput == false){
				System.out.println("Wrong input.Try again.");
			}
		}
		return quarter;
	}

	private static int[] createNewApplication(Scanner in, Connection conn, ApplicationObject newApplication) throws SQLException{
		newApplication.setApplDate(LocalDate.now().toString());
		
		System.out.println("Quarter to start: ");
		String start = chooseQuarter(in, "start");
		newApplication.setStartQuarter(start);
		
		System.out.println("Quarter to end: ");
		String end = chooseQuarter(in, "end");
		newApplication.setEndQuarter(end);

		addApplicationPrefSid(in, conn, newApplication);
		
		int [] preferences = getRoomPreferences(in);
		newApplication.setFirstPref(getBedTypeDescription(preferences[0]));
		newApplication.setSecondPref(getBedTypeDescription(preferences[1]));
		newApplication.setThirdPref(getBedTypeDescription(preferences[2]));
		
		return preferences;
	}

	private static int[] updateCurrentApplication(Scanner in, Connection conn, ApplicationObject application)
			throws SQLException {
		application.setApplDate(LocalDate.now().toString());
		
		//fill start/end quarters
		System.out.println("Quarter to start: " + application.getStartQuarter());
		String startQuarter = chooseQuarter(in, "start");
		application.setStartQuarter(startQuarter);
		
		System.out.println("Quarter to end: " + application.getEndQuarter());
		String endQuarter = chooseQuarter(in, "end");
		application.setEndQuarter(endQuarter);
		
		
		//fill out the pref. SID
		if(application.prefSid != 0){
			System.out.println("You prefere to lease with: " + application.getPrefSid().toString());
		}
		String choice = "";
		System.out.println("If you want to specify you rommate, press 1. To skip, press 0: ");
		choice = in.next();
		if(choice.toUpperCase().equals("1")){
			addApplicationPrefSid(in, conn, application);
		}
		else{
			application.setPrefSid(0);
		}
		
		//fill out 3 room-type preferences
		int [] preferences = getRoomPreferences(in);
		application.setFirstPref(getBedTypeDescription(preferences[0]));
		application.setSecondPref(getBedTypeDescription(preferences[1]));
		application.setThirdPref(getBedTypeDescription(preferences[2]));
		
		return preferences;
	}
	
	private static void updateApplicationInDB(Connection conn, int [] preferences, ApplicationObject application) throws SQLException {
		CallableStatement cStmt = conn.prepareCall("{call updateCurrentApplication(?,?,?,?,?)}");
		
		cStmt.setInt(1, application.getApplNum());
		cStmt.setString(2, application.getStartQuarter());
		cStmt.setString(3, application.getEndQuarter());
		if(application.getPrefSid() == 0){
			cStmt.setString(4, null);
		}
		else{
			cStmt.setInt(4, application.getPrefSid());
		}
		boolean queryExists = false;
		cStmt.setBoolean(5, queryExists);
		cStmt.execute();
		
		boolean prefQueryExecute = uploadApplBedPrefInDB(conn, preferences, application.getApplNum());
			
		if (cStmt.getBoolean(5) == true & prefQueryExecute == true) {
			System.out.println("Your application is successfully updated");
			System.out.println();
		}		
	}

	private static boolean uploadApplBedPrefInDB(Connection conn, int[] preferences, int applNum) 
			throws SQLException {
		
		CallableStatement cStmtPref = conn.prepareCall("{call updateCurrentApplicationPrefer(?,?,?,?,?)}");
		
		cStmtPref.setInt(1, applNum);
		cStmtPref.setInt(2, preferences[0]);
		cStmtPref.setInt(3, preferences[1]);
		cStmtPref.setInt(4, preferences[2]);
		boolean prefQueryExists = false;
		cStmtPref.setBoolean(5, prefQueryExists);
		
		cStmtPref.execute();
		return cStmtPref.getBoolean(5);
	}

	private static void cancelApplicationInDB(Connection conn, ApplicationObject application) throws SQLException {
		CallableStatement cStmt = conn.prepareCall("{call cancelCurrentApplication(?,?)}");
		
		cStmt.setInt(1, application.getApplNum());
		boolean queryExists = false;
		cStmt.setBoolean(2, queryExists);
				
		cStmt.execute();
		if (cStmt.getBoolean(2) == true) {
			System.out.println("Your application is canceled");
			System.out.println();
		}		
		
	}

	private static void setNewApplicationToDB(Connection conn, int[] preferences, ApplicationObject newApplication) throws SQLException{
		CallableStatement cStmt = conn.prepareCall("{call createNewApplication(?,?,?,?,?)}");
		
		cStmt.setInt(1, newApplication.getSid());
		cStmt.setString(2, newApplication.getEndQuarter());
		cStmt.setString(3, newApplication.getEndQuarter());
		if(newApplication.getPrefSid() == 0){
			cStmt.setString(4, null);
		}
		else{
			cStmt.setInt(4, newApplication.getPrefSid());
		}
		cStmt.registerOutParameter(5, java.sql.Types.INTEGER);
		cStmt.execute();
		
		int newApplNum = cStmt.getInt(5);
		boolean prefQueryExecute = uploadApplBedPrefInDB(conn, preferences, newApplNum);
				
		
		if (prefQueryExecute == true) {
			System.out.println("Your application is successfully uploaded");
			System.out.println();
		}		
		
	}
	
}
