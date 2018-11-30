import java.sql.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.*;
import java.util.Date;
import java.io.*;

public class Housing_System {

	public static void main(String[] args) throws ParseException {
		Scanner in = new Scanner(System.in);
		Connection conn = null;
		Integer user = null;
		boolean stayIn = true;

		// run general "hello" menu
		try{

			//it will run since user want to stay in
			while (stayIn == true){

				//run general menu
				int userType = mainManuSelection(in);

				//end the session
				if(userType == 3){
					System.out.println("Thank you for using our service!");
					stayIn = false;
				}
				else{

					//create connection to db
					conn = openConnection();

					//recognize does the user exists (login and password)
					user = createUserConnection(in, conn);
					if (user == null){
						System.out.println("Wrong input. Try again:");
						System.out.println();
					}
					else{
						switch(userType){

						//If user is student
						case(1):
							boolean stayLogIn = true;
							while (stayLogIn == true){

								//check db if student has active application
								ApplicationObject application = getCurrentApplication(user,conn);

								//check db if student has lease
								LeaseObject lease = getCurrentLease(user, conn);

								//check db if student has maintenance requests
								List <MaintenanceRequestObject> mRequest = getCurrentRequests(user, conn);

								//output lease and maint.requests for user
								boolean currentLeaseExist = false;
								if(lease != null){
									currentLeaseExist = true;
									System.out.println("You have an active lease.");
									lease.printOutLeaseObject();
									System.out.println();

									if(!mRequest.isEmpty()){
										System.out.println("You have an active maintenance request:");
										for (MaintenanceRequestObject temp : mRequest) {
											temp.printOutRequesrObject();
											System.out.println();
										}
									}
								}

								boolean currentApplExist = false;
								if (application != null){
									currentApplExist = true;

									//output current active application if exists
									System.out.println();
									System.out.println("You have an active application.");
									application.printOutApplicationObject();
									System.out.println();
								}

								//student menu to select actions
								int selectNextAction = studentMenuSelection(in, currentApplExist, currentLeaseExist);
								switch(selectNextAction){

									//go out
									case 3:
										stayLogIn = false;
										break;

									//update current application and change the date for current date
									case 4:
										int [] preferences = updateCurrentApplication(in, conn, application);

										System.out.println();
										System.out.println("Your new application: ");
										application.printOutApplicationObject();
										System.out.println();

										//double check the edited application
										System.out.println("To save changes press 1, to skip press 0: ");
										String updateAgree = in.next();
										if(updateAgree.equals("1")){

											//update db
											updateApplicationInDB(conn, preferences, application);
										}
										else{
											//in.next();
										}

										break;

									// cancel the active application
									case 5:
										System.out.println();
										System.out.println("Are you sure you want to cancel your application? (Y/N)");
										String dessision = in.next();
										if(dessision.toUpperCase().equals("Y")){

											//update db
											changeApplicationStatusInDB(conn, application, "C");
										}
										else{
									//		in.next();
										}
										break;

									// create new application if student doesn't has it
									case 6:
										ApplicationObject newApplication = new ApplicationObject();
										int newApplNum;
										newApplication.setSid(user);
										int[] roomPreferences = createNewApplication(in, conn, newApplication);

										//output new application to double check before saving
										System.out.println();
										System.out.println("Your application: ");
										newApplication.printOutApplicationObject();

										//upload new application to db
										System.out.println();
										System.out.println("To save your application press 1");
										String saveAgree = in.next();
										if(saveAgree.toUpperCase().equals("1")){
											newApplNum = setNewApplicationToDB(conn, roomPreferences, newApplication);
											newApplication.setApplNum(newApplNum);
										}
										else{
											//in.next();
										}

										//check available pleases
										int prefSid = newApplication.getPrefSid();
										if(prefSid != 0 ){
											//check if we have active application from second person (with our user like prefSid)
											ApplicationObject secondAppl = getCurrentApplication(prefSid,conn);

											//we have 2 correct application
											if(secondAppl != null & secondAppl.getPrefSid() == user){
												System.out.println("Both applications are obtained.");
												System.out.println(" We will send you a mail with offer as soon "
														+ "as we find a room according your preferences.");
												System.out.println();
											}
											//we don't have application from second person or this appl doesn't has right prefSid
											else{
												System.out.println("We are waiting on an application from"
														+ " the second person to complete your request");
												System.out.println();
											}
										}

										//if user doesn't have prefSid we search available beds immediately
										else{
											List <BedToLeaseObject> avalableBeds = checkAvialability(conn, newApplication, user, roomPreferences);
											if (avalableBeds.isEmpty()){
												System.out.println("Sorry, but right now we don't have avalable rooms for you. ");
												System.out.println("You can wait for a message if space becomes available, ");
												System.out.println("or change your preferences in your application");
												System.out.println();
											}
											else{
												//offer created
												int numberOfOffers = printOutAllOffers(avalableBeds);

												//choose and accept offer
												LeaseObject newLease = AcceptingOffer(numberOfOffers, in, newApplication, avalableBeds);
												setNewLeaseToDB(conn, newLease, newApplication);
											}
										}
										break;
										case 7:
											System.out.println("Sorry, this function is not yet available");
											break;
								}
							}
							break;
						case 2:

							//admin menu
							adminMenuSelection(in, conn);
							break;
						}
					}
				}
			}
		}
		catch(SQLException | ClassNotFoundException ex) {
		  System.out.println(ex);
//		  System.out.println(ex.getStackTrace());
		}
		finally {

			//close connection
			if (conn != null) {
				try {
					conn.close();
				}catch (SQLException e) { /* ignored */};
			}
			//close scanner
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

		//validation loop
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

	//login with ID and password (pretend the connection like on BC web site)
	public static Integer createUserConnection(Scanner in, Connection conn) throws SQLException {
		Integer userId = null;
		String password = "";
		boolean validInput = false;

		//input and validation user's id
		while(validInput == false){
			System.out.println("Enter ID: ");
			try{
				userId = in.nextInt();
				validInput = idLenghtValidation(userId);
				if(validInput == false){
					System.out.println("No such id exists. Try again.\n");
				}
			}catch(InputMismatchException e){
				System.out.println("You must enter your id number. Please try again.\n");
				in.next();
			}

		}

		//input password
		System.out.println("Enter password: ");
		password = in.next();

		//validation in db
		String query = "SELECT * FROM personVerification WHERE bc_id = ? AND password = ?";
		PreparedStatement st = conn.prepareStatement(query);
		st.clearParameters();
		st.setInt(1,userId);
		st.setString(2,password);
		ResultSet result = st.executeQuery();
		if(result.next() == false){
			userId = null;
		}
		//if user exists and password is correct - return user ID
		return userId;
	}

	//open connection
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

	//if student has active application this method will retrieve this info from db
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

	//retrieve preferences regarding active application and set them to application object
	private static ApplicationObject getApplicationPreferences(Connection conn, ApplicationObject appl) throws SQLException{
		CallableStatement cStmt = conn.prepareCall("{call getApplicationPreferences(?)}");
		int applNum = appl.getApplNum();
		cStmt.setInt(1, applNum);
		ResultSet result = cStmt.executeQuery();

		//max 3 preferences
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

	//if student has active lease this method will retrieve this info from db
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

	//if student has active Maintenance Request this method will retrieve this info from db
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

	//show up the student menu to select action
	public static int studentMenuSelection(Scanner in, boolean currentApplExist, boolean currentLeaseExist) {
		boolean validSelect = false;

		System.out.println("To choose your next action please press");
		System.out.println();
		System.out.println("3. Quit");
		if (currentApplExist == true){
			System.out.println("4. Edit your application" );
			System.out.println("5. Cancel your application");

		}
		else if (currentLeaseExist == true){
			System.out.println("7. Create new maintenance request");
		}
		else{
			System.out.println("6. Create new application");
		}
		System.out.println();

		int select = 0;

		//select validation
		while (validSelect == false){

			System.out.println("Type in your option:");
			try{
				select = in.nextInt();
			}catch(InputMismatchException e){
				select = 0;
			}
			if (select > 2 & select < 8) {
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

	//give description for bed type throw the index
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

	//give price per quarter for bed type throw the index
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

	//check does prefId exists and add it to application
	private static void addApplicationPrefSid(Scanner in, Connection conn, ApplicationObject application)
			throws SQLException {

		boolean validId = false;
		System.out.println("You want to lease a room with: ");
		//input and validation (does it exists in db) user's id
		while(validId == false){
			try{
				Integer id = in.nextInt();
				validId = idLenghtValidation(id);
				if(validId == false){
					System.out.println("No such ID exists. Try again.\n");
				}

				//prevent to input our users ID like pref.Id
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
				System.out.println("You must enter your ID number. Please try again.\n");
				in.next();
			}
		}
	}

	//validation room type input
	private static int choosePrefereRoomType(Scanner in) {
		int type = 0;
		boolean validType = false;

		System.out.println(" Preferred room type: ");
		while(validType == false){
			try{
				type = in.nextInt();
				if(type < 1 || type > 8){
					System.out.println("No such room is available. Please try again.\n");
				}
				else{
					validType = true;
				}
			}catch(InputMismatchException e){
				System.out.println("You must enter a number between 1 and 8. Please try again.\n");
				in.next();
			}
		}
		return type;
	}

	//user select 3 preference room types
	private static int[] getRoomPreferences(Scanner in) {
		int[] roomPtef = new int [3];

		System.out.println("Room types: ");
		for(int i = 1; i < 9; i++){
			System.out.println(i + ". " + getBedTypeDescription(i) + "Price: " + getBedTypePrice(i) + " per quarter");
		}
		System.out.println();

		System.out.println("Please choose your 3 room type preferences.");

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

	//user chooses the start/end quarter
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

	//create new and assign values to application object
	private static int[] createNewApplication(Scanner in, Connection conn, ApplicationObject newApplication) throws SQLException{
		newApplication.setApplDate(LocalDate.now().toString());

		System.out.println("Quarter to start: ");
		String start = chooseQuarter(in, "start");
		newApplication.setStartQuarter(start);

		System.out.println("Quarter to end: ");
		String end = chooseQuarter(in, "end");
		newApplication.setEndQuarter(end);

		String choice = "";
		System.out.println("If you want to specify you rommate, press 1. To skip, press 0: ");
		choice = in.next();
		if(choice.toUpperCase().equals("1")){
			addApplicationPrefSid(in, conn, newApplication);
		}
		else{
			newApplication.setPrefSid(0);
		}

		int [] preferences = getRoomPreferences(in);
		newApplication.setFirstPref(getBedTypeDescription(preferences[0]));
		newApplication.setSecondPref(getBedTypeDescription(preferences[1]));
		newApplication.setThirdPref(getBedTypeDescription(preferences[2]));

		return preferences;
	}

	//edit active application
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
		if(application.getPrefSid() != 0){
			System.out.println("You prefer to lease with: " + application.getPrefSid().toString());
		}
		String choice = "";
		System.out.println("If you want to specify a roommate, press 1. To skip, press 0: ");
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

	// update current active application in db
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

		//update bed preferences in db
		boolean prefQueryExecute = uploadApplBedPrefInDB(conn, preferences, application.getApplNum());

		if (cStmt.getBoolean(5) == true & prefQueryExecute == true) {
			System.out.println("Your application is successfully updated");
			System.out.println();
		}
	}

	//upload bed new preferences in db
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

	//change application status - in DB
	private static void changeApplicationStatusInDB(Connection conn, ApplicationObject application, String newStatus) throws SQLException {
		CallableStatement cStmt = conn.prepareCall("{call changeApplicationStatus(?,?,?)}");

		cStmt.setInt(1, application.getApplNum());
		cStmt.setString(2, newStatus);
		boolean queryExists = false;
		cStmt.setBoolean(3, queryExists);

		cStmt.execute();
		if (cStmt.getBoolean(3) == true) {
			System.out.println("Done!");
			System.out.println();
		}

	}

	//create new application in db
	private static int setNewApplicationToDB(Connection conn, int[] preferences, ApplicationObject newApplication) throws SQLException{
		CallableStatement cStmt = conn.prepareCall("{call createNewApplication(?,?,?,?,?)}");

		cStmt.setInt(1, newApplication.getSid());
		cStmt.setString(2, newApplication.getStartQuarter());
		cStmt.setString(3, newApplication.getEndQuarter());
		if(newApplication.getPrefSid() == 0){
			cStmt.setString(4, null);
		}
		else{
			cStmt.setInt(4, newApplication.getPrefSid());
		}
		//retrieve application number for new application
		cStmt.registerOutParameter(5, java.sql.Types.INTEGER);
		cStmt.execute();

		int newApplNum = cStmt.getInt(5);
		boolean prefQueryExecute = uploadApplBedPrefInDB(conn, preferences, newApplNum);


		if (prefQueryExecute == true) {
			System.out.println("Your application is successfully uploaded");
			System.out.println();
		}

		return newApplNum;
	}

	//retrieve list of available bed regarding preferences on active application
	private static List<BedToLeaseObject> checkAvialability(Connection conn, ApplicationObject newApplication, Integer sid, int[] roomPreferences) throws SQLException, ParseException{

		List<BedToLeaseObject> beds = new ArrayList<BedToLeaseObject>();

		//check the sex of user to create correct list
		String userSex = getUsersSex(conn, sid);

		CallableStatement cStmt = conn.prepareCall("{call findOccupancy(?,?)}");

		//convert quarter abbreviation to dates
		Date startDate = convertQuarterToCurrentYearDate(newApplication.getStartQuarter())[0];
		Date endDate = convertQuarterToCurrentYearDate(newApplication.getEndQuarter())[1];

		Timestamp start = new Timestamp(startDate.getTime());
		Timestamp end = new Timestamp(endDate.getTime());

		cStmt.setTimestamp(1, start);
		cStmt.setTimestamp(2, end);

		ResultSet result = cStmt.executeQuery();

		BedToLeaseObject bed = null;

		while (result.next()) {
			bed = new BedToLeaseObject();
			bed.setBedNum(result.getInt("bed_num"));
			bed.setUnitNum(result.getInt("unit_num"));
			bed.setBedType(result.getInt("bd_id"));
			bed.setOccupancy(result.getString("occupancy"));
			if((bed.getOccupancy().equals(userSex) || bed.getOccupancy().equals("FREE")) &
					(bed.getBedType() == roomPreferences[0] ||
					 bed.getBedType() == roomPreferences[1] ||
					 bed.getBedType() == roomPreferences[2])){
				beds.add(bed);
			}
		}
		return beds;
	}

	//check the sex of user
	private static String getUsersSex(Connection conn, Integer sid) throws SQLException {
		String query = "SELECT * FROM person WHERE bc_id = ?";
		PreparedStatement st = conn.prepareStatement(query);
		st.clearParameters();
		st.setInt(1, sid);

		ResultSet resultSex = st.executeQuery();
		resultSex.next();
		String userSex = resultSex.getString("sex");
		return userSex;
	}

	//convert quarter abbreviation to dates
	private static Date[] convertQuarterToCurrentYearDate(String quarter) throws ParseException{
		Date[] dates = new Date[2];
		SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd");

		//we hardcoded dates for particular school year
		if(quarter.equals("F")){
			dates[0] = fmt.parse("2018-09-03");
			dates[1] = fmt.parse("2018-12-06");
		}
		else if (quarter.equals("W")){
			dates[0] = fmt.parse("2019-01-03");
			dates[1] = fmt.parse("2019-03-15");
		}
		else if(quarter.equals("S")){
			dates[0] = fmt.parse("2019-04-03");
			dates[1] = fmt.parse("2019-06-15");
		}
		return dates;
	}

	//print out all available beds + description + prices
	private static int printOutAllOffers(List<BedToLeaseObject> avalableBeds) {
		System.out.println("For now we have this rooms:");
		int index = 0;
		for (index = 0; index < avalableBeds.size(); index++) {
			System.out.print("Offer# " + (index+1) + ". ");
			avalableBeds.get(index).printOutBedToLeaseObject();
			System.out.print(" " + getBedTypeDescription(avalableBeds.get(index).getBedType()));
			System.out.println(" " + getBedTypePrice(avalableBeds.get(index).getBedType()));
		}
		System.out.println();
		return index;
	}

	//create new lease object on base of users choice
	private static LeaseObject AcceptingOffer(int numberOfOffers, Scanner in, ApplicationObject newApplication, List <BedToLeaseObject> avalableBeds) {

		boolean validSelect = false;
        int select = 0;

		while (validSelect == false){
			System.out.println("To choose your room, please type the number of your offer: ");
			try{
				select = in.nextInt();
			}catch(InputMismatchException e){
				select = 0;
			}
			if (select > 0 & select <= numberOfOffers) {
				validSelect = true;
			}
			else{
				// dump unnecessary input
				in.next();
				System.out.println("Wrong input. Please, try again.");
			}
		}

		LeaseObject newLease = new LeaseObject();
		newLease.setApplNum(newApplication.getApplNum());
		newLease.setStartDate(newApplication.getStartQuarter());
		newLease.setEndDate(newApplication.getEndQuarter());
		double price = getBedTypePrice(avalableBeds.get(select - 1).getBedType());
		int numberOfquarters;
		if(newLease.getStartDate().equals(newLease.getEndDate())){
			numberOfquarters = 1;
		}
		else if(newLease.getStartDate().equals("F") & newLease.getEndDate().equals("S")){
			numberOfquarters = 3;
		}
		else{
			numberOfquarters = 2;
		}
		double cost = numberOfquarters * price;
		newLease.setCost(cost);
		newLease.setBedNum(avalableBeds.get(select - 1).getBedNum());
		newLease.setUnitNum(avalableBeds.get(select - 1).getUnitNum());

		return newLease;
	}

	//create new lease in db and change status of application from A-active to L -lease
	private static void setNewLeaseToDB(Connection conn, LeaseObject newLease, ApplicationObject newApplication) throws SQLException, ParseException {
		CallableStatement cStmt = conn.prepareCall("{call createNewLease(?,?,?,?,?,?,?)}");

		Date startDate = convertQuarterToCurrentYearDate(newLease.getStartDate())[0];
		Date endDate = convertQuarterToCurrentYearDate(newLease.getEndDate())[1];

		Timestamp start = new Timestamp(startDate.getTime());
		Timestamp end = new Timestamp(endDate.getTime());

		cStmt.setInt(1, newLease.getApplNum());
//		System.out.println(newLease.getApplNum());
		cStmt.setTimestamp(2, start);
		cStmt.setTimestamp(3, end);
		cStmt.setDouble(4, newLease.getCost());
		cStmt.setInt(5, newLease.getBedNum());
		cStmt.setInt(6, newLease.getUnitNum());

		boolean leaseUploaded = false;
		cStmt.setBoolean(7, leaseUploaded);

		cStmt.execute();
		if(cStmt.getBoolean(7) == true){
			System.out.println("You have successfully accepted your offer! Congratulations!");

			//change status of application from A-active to L -lease
			changeApplicationStatusInDB(conn, newApplication, "L");
		}
	}

	public static void adminMenuSelection(Scanner in, Connection conn) {
		boolean prompt = true;
	  while(prompt) {
	    System.out.println("****************************************************************************************");
	    System.out.println("                       Welcome to the Bellevue College Housing");
	    System.out.println("                                 Administrator Menu");
	    System.out.println("****************************************************************************************");
	    System.out.println("                                1. Manage Residents");
	    System.out.println("                      	        2. Manage Applicants");
	    System.out.println("                                3. Demographic Studies");
	    System.out.println("                                4. Manage Maintenance Orders");
	    System.out.println("                                5. Administrative Reports");
	    System.out.println("                                6. Quit");
	    System.out.println();

	    // store user input
	    int option;
	    try {
	      option = in.nextInt();
	      switch(option) {
	        case 1:
						System.out.println("This feature is not yet available");
	          break;
	        case 2:
						System.out.println("This feature is not yet available");
	          break;
	        case 3:
						System.out.println("This feature is not yet available");
	          break;
	        case 4:
						maintenanceReportQuery(in, conn);
	          break;
	        case 5:
						System.out.println("This feature is not yet available");
	          break;
	        case 6:
						prompt = false;
						return;
	        default:
						System.out.println("Invalid input");
	      }
	    } catch (Exception e) {
	      System.out.println("Query failed: " + e);
				prompt = false;
	    }
	  }
	}

	public static void maintenanceReportQuery(Scanner in, Connection conn) throws SQLException {
	  System.out.println("In order to see when units will be vacated,");
	  System.out.println("please enter the current quarter (F, W, or S)");

	  String quarter = "";
	  String endDate;
	  boolean running = true;

	  // accept user input for current quarter (re-prompt until valid input is received)
	  while(running) {
	    quarter = in.next().toUpperCase();

	    if(quarter.equals("F") || quarter.equals("W") || quarter.equals("S")) {
	      running = false;
	    } else {
	      System.out.println("Invalid input");
			}
	  }
		// set endDate to the selected quarter's end date
	  if(quarter.equals("F"))
	    endDate = "2018-09-15";
	  else if(quarter.equals("F"))
	    endDate = "2019-03-15";
	  else
	    endDate = "2019-06-15";

		// execute query to return all units to be vacated at the end of the quarter
	  CallableStatement cStmt = conn.prepareCall("{call getVacantUnits(?)}");
		cStmt.setString(1, endDate);
		ResultSet result = cStmt.executeQuery();

		// print results of the query to std out

		System.out.println("The following units will be vacant");
		System.out.println("at the end of the quarter:\n\n");

	  while(result.next())
	    System.out.println(
	      "Unit: " + result.getInt("unit_num") + ", " +
	      "Bed Number: " + result.getInt("bed_num") + ", " +
	      "Vacant After: " + result.getString("end_date")
	    );
	}
}
