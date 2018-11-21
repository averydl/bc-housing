import java.sql.*;
import java.util.InputMismatchException;
import java.util.Scanner;
import java.io.*;

public class Housing_System {

	public static void main(String[] args) {
		Scanner in = new Scanner(System.in);
		Connection conn = null;
		
		// run general "hello" menu	
		int userType = mainManuSelection(in);
		
//		//clear console
//		System.out.flush(); 
		
		//if user wants to close session
		if(userType == 3){
			System.out.println("Thank you for using our service!");
		}
		else{
			
			try {
				
				//login to connect with db
				conn = openConnection();
				if (createUserConnection(in, conn) == false){
					System.out.println("Wrong input. Try again:");
					System.out.println();
					userType = mainManuSelection(in); //go to the cycle
				}
				
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

	public static boolean createUserConnection(Scanner in, Connection conn) throws SQLException {
		int id = 0;
		String password = "";
		
		System.out.println("Enter ID: ");
		try{
			id = in.nextInt();
		}catch(InputMismatchException e){
			// go to the main menu************************************************************
		}
		System.out.println("Enter password: ");
		password = in.next();
		
		//maybe go to procedure**************************************************************
		String query = "SELECT * FROM personVerification WHERE bc_id = ? AND password = ?";
		PreparedStatement st = conn.prepareStatement(query);
		st.clearParameters();
		st.setInt(1,id);
		st.setString(2,password);
		ResultSet result = st.executeQuery();
		
		return result.next();
		
	}

	private static Connection openConnection() throws ClassNotFoundException, SQLException {
		Class.forName("com.mysql.cj.jdbc.Driver");
		String url = "jdbc:mysql://localhost:3306/BCHousing?serverTimezone=UTC&useSSL=TRUE";
		
		//hurdcode to create connection to VM DB
		Connection conn = DriverManager.getConnection(url, "student", "password");
		
		return conn;
	}

}
