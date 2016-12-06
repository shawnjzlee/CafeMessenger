/*
 * Template JAVA User Interface
 * =============================
 *
 * Database Management Systems
 * Department of Computer Science &amp; Engineering
 * University of California - Riverside
 *
 * Target DBMS: 'Postgres'
 *
 */


import java.sql.DriverManager;
import java.sql.Connection;
import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.io.File;
import java.io.FileReader;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.List;
import java.util.ArrayList;

/**
 * This class defines a simple embedded SQL utility class that is designed to
 * work with PostgreSQL JDBC drivers.
 *
 */
public class Cafe {

   //login info for later use
   private static String authorisedUser = null;

   // reference to physical database connection.
   private Connection _connection = null;

   // handling the keyboard inputs through a BufferedReader
   // This variable can be global for convenience.
   static BufferedReader in = new BufferedReader(
                                new InputStreamReader(System.in));

   /**
    * Creates a new instance of Cafe
    *
    * @param hostname the MySQL or PostgreSQL server hostname
    * @param database the name of the database
    * @throws java.sql.SQLException when failed to make a connection.
    */
   public Cafe (String dbname, String dbport) throws SQLException {

      System.out.print("Connecting to database...");
      try{
         // constructs the connection URL
         String url = "jdbc:postgresql://127.0.0.1:" + dbport + "/" + dbname;
         System.out.println ("Connection URL: " + url + "\n");

         // obtain a physical connection
         this._connection = DriverManager.getConnection(url);
         System.out.println("Done");
      }catch (Exception e){
         System.err.println("Error - Unable to Connect to Database: " + e.getMessage() );
         System.out.println("Make sure you started postgres on this machine");
         System.exit(-1);
      }//end catch
   }//end Cafe

   /**
    * Method to execute an update SQL statement.  Update SQL instructions
    * includes CREATE, INSERT, UPDATE, DELETE, and DROP.
    *
    * @param sql the input SQL string
    * @throws java.sql.SQLException when update failed
    */
   public void executeUpdate (String sql) throws SQLException {
      // creates a statement object
      Statement stmt = this._connection.createStatement ();

      // issues the update instruction
      stmt.executeUpdate (sql);

      // close the instruction
      stmt.close ();
   }//end executeUpdate

   /**
    * Method to execute an input query SQL instruction (i.e. SELECT).  This
    * method issues the query to the DBMS and outputs the results to
    * standard out.
    *
    * @param query the input query string
    * @return the number of rows returned
    * @throws java.sql.SQLException when failed to execute the query
    */
   public int executeQueryAndPrintResult (String query) throws SQLException {
      // creates a statement object
      Statement stmt = this._connection.createStatement ();

      // issues the query instruction
      ResultSet rs = stmt.executeQuery (query);

      /*
       ** obtains the metadata object for the returned result set.  The metadata
       ** contains row and column info.
       */
      ResultSetMetaData rsmd = rs.getMetaData ();
      int numCol = rsmd.getColumnCount ();
      int rowCount = 0;

      // iterates through the result set and output them to standard out.
      boolean outputHeader = true;
      while (rs.next()){
	 if(outputHeader){
	    for(int i = 1; i <= numCol; i++){
		System.out.print(rsmd.getColumnName(i) + "\t");
	    }
	    System.out.println();
	    outputHeader = false;
	 }
         for (int i=1; i<=numCol; ++i)
            System.out.print (rs.getString (i) + "\t");
         System.out.println ();
         ++rowCount;
      }//end while
      stmt.close ();
      return rowCount;
   }//end executeQuery

   /**
    * Method to execute an input query SQL instruction (i.e. SELECT).  This
    * method issues the query to the DBMS and returns the results as
    * a list of records. Each record in turn is a list of attribute values
    *
    * @param query the input query string
    * @return the query result as a list of records
    * @throws java.sql.SQLException when failed to execute the query
    */
   public List<List<String>> executeQueryAndReturnResult (String query) throws SQLException { 
      // creates a statement object 
      Statement stmt = this._connection.createStatement (); 
 
      // issues the query instruction 
      ResultSet rs = stmt.executeQuery (query); 
 
      /* 
       ** obtains the metadata object for the returned result set.  The metadata 
       ** contains row and column info. 
       */ 
      ResultSetMetaData rsmd = rs.getMetaData (); 
      int numCol = rsmd.getColumnCount (); 
      int rowCount = 0; 
 
      // iterates through the result set and saves the data returned by the query. 
      boolean outputHeader = false;
      List<List<String>> result  = new ArrayList<List<String>>(); 
      while (rs.next()){
          List<String> record = new ArrayList<String>(); 
         for (int i=1; i<=numCol; ++i) 
            record.add(rs.getString (i)); 
         result.add(record); 
      }//end while 
      stmt.close (); 
      return result; 
   }//end executeQueryAndReturnResult

   /**
    * Method to execute an input query SQL instruction (i.e. SELECT).  This
    * method issues the query to the DBMS and returns the number of results
    *
    * @param query the input query string
    * @return the number of rows returned
    * @throws java.sql.SQLException when failed to execute the query
    */
   public int executeQuery (String query) throws SQLException {
       // creates a statement object
       Statement stmt = this._connection.createStatement ();

       // issues the query instruction
       ResultSet rs = stmt.executeQuery (query);

       int rowCount = 0;

       // iterates through the result set and count nuber of results.
       if(rs.next()){
          rowCount++;
       }//end while
       stmt.close ();
       return rowCount;
   }

   /**
    * Method to fetch the last value from sequence. This
    * method issues the query to the DBMS and returns the current 
    * value of sequence used for autogenerated keys
    *
    * @param sequence name of the DB sequence
    * @return current value of a sequence
    * @throws java.sql.SQLException when failed to execute the query
    */
   public int getCurrSeqVal(String sequence) throws SQLException {
	Statement stmt = this._connection.createStatement ();
	
	ResultSet rs = stmt.executeQuery (String.format("Select currval('%s')", sequence));
	if (rs.next())
		return rs.getInt(1);
	return -1;
   }

   /**
    * Method to close the physical connection if it is open.
    */
   public void cleanup(){
      try{
         if (this._connection != null){
            this._connection.close ();
         }//end if
      }catch (SQLException e){
         // ignored.
      }//end try
   }//end cleanup

   /**
    * The main execution method
    *
    * @param args the command line arguments this inclues the <mysql|pgsql> <login file>
    */
   public static void main (String[] args) {
      if (args.length != 2) {
         System.err.println (
            "Usage: " +
            "java [-classpath <classpath>] " +
            Cafe.class.getName () +
            " <dbname> <port>");
         return;
      }//end if

      Greeting();
      Cafe esql = null;
      try{
         // use postgres JDBC driver.
         Class.forName ("org.postgresql.Driver").newInstance ();
         // instantiate the Cafe object and creates a physical
         // connection.
         String dbname = args[0];
         String dbport = args[1];
         esql = new Cafe (dbname, dbport);

         boolean keepon = true;
         while(keepon) {
            // These are sample SQL statements
            System.out.println("MAIN MENU");
            System.out.println("---------");
            System.out.println("1. Create user");
            System.out.println("2. Log in");
            System.out.println("9. < EXIT");
            authorisedUser = null;
            switch (readChoice()){
               case 1: CreateUser(esql); break;
               case 2: authorisedUser = LogIn(esql); break;
               case 9: keepon = false; break;
               default : System.out.println("Unrecognized choice!"); break;
            }//end switch
            if (authorisedUser != null) {
              boolean usermenu = true;
              String user_type = find_type(esql, authorisedUser);
	      switch (user_type){
		case "Customer": 
		  while(usermenu) {
                    System.out.println("MAIN MENU");
                    System.out.println("---------");
                    System.out.println("0. Browse full Menu");
                    System.out.println("1. Browse Menu by ItemName");
                    System.out.println("2. Browse Menu by Type");
                    System.out.println("3. Add Order");
                    System.out.println("4. Update Order");
                    System.out.println("5. View Order History");
                    System.out.println("6. View Order Status");
                    System.out.println("7. Update User Info");
                    System.out.println("8. View User Info");
                    System.out.println(".........................");
                    System.out.println("9. Log out");
                      switch (readChoice()){
                       case 0: BrowseMenu(esql); break;
                       case 1: BrowseMenuName(esql); break;
                       case 2: BrowseMenuType(esql); break;
                       case 3: AddOrder(esql, authorisedUser); break;
                       case 4: UpdateOrder(esql, authorisedUser); break;
                       case 5: ViewOrderHistory(esql); break;
                       case 6: ViewOrderStatus(esql); break;
                       case 7: UpdateUserInfo(esql, authorisedUser, 0); break;
                       case 8: ViewUserInfo(esql, authorisedUser); break;
                       case 9: usermenu = false; break;
                       default : System.out.println("Unrecognized choice!"); break;
		      }//end switch
		  } break;
		case "Employee": 
		  while(usermenu) {
                    System.out.println("MAIN MENU");
                    System.out.println("---------");
                    System.out.println("0. Browse full Menu");
                    System.out.println("1. Browse Menu by ItemName");
                    System.out.println("2. Browse Menu by Type");
                    System.out.println("3. Add Order");
                    System.out.println("4. Update Order");
                    System.out.println("5. View Current Orders");
                    System.out.println("6. View Order Status");
                    System.out.println("7. Update User Info");
                    System.out.println("8. View User Info");
                    System.out.println(".........................");
                    System.out.println("9. Log out");
                      switch (readChoice()){
                       case 0: BrowseMenu(esql); break;
                       case 1: BrowseMenuName(esql); break;
                       case 2: BrowseMenuType(esql); break;
                       case 3: AddOrder(esql, authorisedUser); break;
                       case 4: EmployeeUpdateOrder(esql); break;
                       case 5: ViewCurrentOrder(esql, authorisedUser, 1); break;
                       case 6: ViewOrderStatus(esql); break;
                       case 7: UpdateUserInfo(esql, authorisedUser, 0); break;
                       case 8: ViewUserInfo(esql, authorisedUser); break;
                       case 9: usermenu = false; break;
                       default : System.out.println("Unrecognized choice!"); break;
		      }//end switch
		  } break;
		case "Manager ": 
		  while(usermenu) {
                    System.out.println("MAIN MENU");
                    System.out.println("---------");
                    System.out.println("0. Browse full Menu");
                    System.out.println("1. Browse Menu by ItemName");
                    System.out.println("2. Browse Menu by Type");
                    System.out.println("3. Add Order");
                    System.out.println("4. Update Order");
                    System.out.println("5. View Current Orders");
                    System.out.println("6. View Order Status");
                    System.out.println("7. Update User Info");
                    System.out.println("8. Update Menu");
                    System.out.println(".........................");
                    System.out.println("9. Log out");
                      switch (readChoice()){
                       case 0: BrowseMenu(esql); break;
                       case 1: BrowseMenuName(esql); break;
                       case 2: BrowseMenuType(esql); break;
                       case 3: AddOrder(esql, authorisedUser); break;
                       case 4: EmployeeUpdateOrder(esql); break;
                       case 5: ViewCurrentOrder(esql, authorisedUser, 1); break;
                       case 6: ViewOrderStatus(esql); break;
                       case 7: ManagerUpdateUserInfo(esql, authorisedUser); break;
                       case 8: UpdateMenu(esql); break;
                       case 9: usermenu = false; break;
                       default : System.out.println("Unrecognized choice!"); break;
		      }//end switch
		  } break;
	      }//end switch
            }//end if
         }//end while
      }catch(Exception e) {
         System.err.println (e.getMessage ());
      }finally{
         // make sure to cleanup the created table and close the connection.
         try{
            if(esql != null) {
               System.out.print("Disconnecting from database...");
               esql.cleanup ();
               System.out.println("Done\n\nBye !");
            }//end if
         }catch (Exception e) {
            // ignored.
         }//end try
      }//end try
   }//end main

   public static void Greeting(){
      System.out.println(
         "\n\n*******************************************************\n" +
         "              User Interface                         \n" +
         "*******************************************************\n");
   }//end Greeting

   /*
    * Reads the users choice given from the keyboard
    * @int
    **/
   public static int readChoice() {
      int input;
      // returns only if a correct value is given.
      do {
         System.out.print("Please make your choice: ");
         try { // read the integer, parse it and break.
            input = Integer.parseInt(in.readLine());
            break;
         }catch (Exception e) {
            System.out.println("Your input is invalid!");
            continue;
         }//end try
      }while (true);
      return input;
   }//end readChoice

   /*
    * Creates a new user with privided login, password and phoneNum
    **/
   public static void CreateUser(Cafe esql){
      try{
         System.out.print("\tEnter user login: ");
         String login = in.readLine();
         System.out.print("\tEnter user password: ");
         String password = in.readLine();
         System.out.print("\tEnter user phone: ");
         String phone = in.readLine();
         
	 String type="Customer";
	 String favItems="";

	 String query = String.format("INSERT INTO USERS (phoneNum, login, password, favItems, type) VALUES ('%s','%s','%s','%s','%s')", phone, login, password, favItems, type);

         esql.executeUpdate(query);
         System.out.println ("User successfully created!");
      }catch(Exception e){
         System.err.println (e.getMessage ());
      }
   }//end
   
   /*
    * Check log in credentials for an existing user
    * @return User login or null is the user does not exist
    **/
   public static String LogIn(Cafe esql){
      try{
         System.out.print("\tEnter user login: ");
         String login = in.readLine();
         System.out.print("\tEnter user password: ");
         String password = in.readLine();

         String query = String.format("SELECT * FROM Users WHERE login = '%s' AND password = '%s'", login, password);
         int userNum = esql.executeQuery(query);
	 if (userNum > 0)
		return login;
         return null;
      }catch(Exception e){
         System.err.println (e.getMessage ());
         return null;
      }
   }//end

   public static String find_type(Cafe esql, String authorisedUser){
      try {
         String query = String.format("SELECT type FROM Users WHERE login = '%s'", authorisedUser);
         List<String> type = new ArrayList<String>();
         List<List<String>> typeList = new ArrayList<List<String>>();
        
         typeList = esql.executeQueryAndReturnResult(query);
         for(int i = 0; i < typeList.size(); i++) {
           type.add(typeList.get(i).get(0));
        }
         return type.get(0);
      }
      catch (Exception except) {
         System.err.println (except.getMessage());
      }
      return "Employee";
   }

   public static void BrowseMenuName(Cafe esql){
      try {
         System.out.print("\tEnter item name: ");
         String name = in.readLine();
         String query = "SELECT M.itemName, M.price, M.description FROM Menu M WHERE M.itemName = '";
         query += name + "';";
	      esql.executeQueryAndPrintResult(query);
      }
      catch (Exception except) {
         System.err.println (except.getMessage());
      }
   }//end

   public static void BrowseMenuType(Cafe esql){
      try {
         System.out.print("\tEnter item type: ");
         String type = in.readLine();
         String query = "SELECT M.itemName, M.price, M.description FROM Menu M WHERE M.type = '";
         query += type + "';";
	      esql.executeQueryAndPrintResult(query);
      }
      catch (Exception except) {
         System.err.println (except.getMessage());
      }
   }//end

   public static void AddOrder(Cafe esql, String authorisedUser){
      try {
         List<String> orderid = new ArrayList<String>();
         List<List<String>> orderidList = new ArrayList<List<String>>();
         
         String query = "SELECT MAX(O.orderid) FROM Orders O";
         orderidList = esql.executeQueryAndReturnResult(query);
         for(int i = 0; i < orderidList.size(); i++) {
           orderid.add(orderidList.get(i).get(0));
         }
         
         String item = null;
         double get_total = 0;
         int get_orderid = Integer.parseInt(orderid.get(0)) + 1;
         
         query = String.format("INSERT INTO Orders(orderid, login, paid, timeStampRecieved, total) VALUES (%d, '%s', false, CURRENT_TIMESTAMP, %f)", get_orderid, authorisedUser, get_total);
         esql.executeUpdate(query);   
         
         while (item != "q" || item != "Q") {
            System.out.print("\tEnter new item name (q to quit): ");
            item = in.readLine();
            
            if (item.equalsIgnoreCase("q")){
              break;
            }
            String query_item = String.format("SELECT M.price FROM Menu M WHERE M.itemName = '%s'", item);
            
            System.out.print("\tEnter comments: ");
            String comments = in.readLine();
            // Add ItemStatus
            String query_status = String.format("INSERT INTO ItemStatus(orderid, itemName, lastUpdated, status, comments) VALUES (%d, '%s', CURRENT_TIMESTAMP, 'Hasn''t Started', '%s')",  get_orderid, item, comments);
            esql.executeUpdate(query_status);
            
            List<String> total = new ArrayList<String>();
            List<List<String>> totalList = new ArrayList<List<String>>();
           
            totalList = esql.executeQueryAndReturnResult(query_item);
            for(int i = 0; i < totalList.size(); i++) {
              total.add(totalList.get(i).get(0));
            }
            
            get_total = get_total + Double.parseDouble(total.get(0));
   	      System.out.println("Order " + get_orderid + " added " + item + " successfully.");
   	      System.out.println("Your current order total is " + get_total);
         }
         query = String.format("UPDATE Orders SET total = %f WHERE orderid = %d", get_total, get_orderid);
         esql.executeUpdate(query);   
      }
      catch (Exception except) {
         System.err.println (except.getMessage());
      }
   }//end 

   public static void UpdateOrder(Cafe esql, String authorisedUser){
      try {
         System.out.println("Displaying list of non-paid orders: ");
         String query = String.format("SELECT O.orderid, I.itemName, I.comments FROM Orders O, ItemStatus I WHERE O.orderid = I.orderid and O.paid = false and O.login = '%s';", authorisedUser);
	 esql.executeQueryAndPrintResult(query);

         System.out.println("Enter the orderid of the order you wish to update: ");
         String orderID = in.readLine();
         System.out.println("Enter the item name of the order you wish to update:");
         String orderName = in.readLine();
         System.out.println("Enter new comments: ");
         String newComments = in.readLine();
         query = "UPDATE ItemStatus SET comments = '" + newComments + "' WHERE orderid = " + orderID + " AND itemName = '" + orderName + "';";
	      esql.executeUpdate(query);
      }
      catch (Exception except) {
         System.err.println (except.getMessage());
      }
   }//end

   public static void EmployeeUpdateOrder(Cafe esql){
      try {
         System.out.println("1. Update Paid Status");
         System.out.println("2. Update Item Status");
         int input = Integer.parseInt(in.readLine());

         String orderID;
         String query;
         switch(input) {
            case 1:
               System.out.println("Enter Order ID");
               orderID = in.readLine();
               query = "UPDATE Orders SET paid = TRUE WHERE orderid = ";
               query += orderID + ";";
               esql.executeUpdate(query);
               System.out.println("The order is now paid");
               break;
            case 2:
               System.out.print("Enter Order ID: ");
               orderID = in.readLine();
               query = String.format("SELECT I.itemName, I.status FROM ItemStatus I WHERE I.orderid = %s", orderID);
               esql.executeQueryAndPrintResult(query);
               System.out.print("Enter the item you wish to update: ");
               String itemName = in.readLine();
               System.out.print("Enter the new order status: ");
               String orderStatus = in.readLine();
               
               if(orderStatus.equalsIgnoreCase("Hasn't Started")) orderStatus = "Hasn''t Started";
               query = String.format("UPDATE ItemStatus SET status = '%s' WHERE orderid = %s and itemName = '%s'", orderStatus, orderID, itemName);
               System.out.println("The order status has been updated");
               esql.executeUpdate(query);
               break;
            default:
               System.out.println("Your choice is invalid");
               break;
         }
      }
      catch (Exception except) {
         System.err.println (except.getMessage());
      }
   }//end

   public static void ViewOrderHistory(Cafe esql){
      try {
         String query = String.format("SELECT O.orderid, O.timeStampRecieved FROM (SELECT * FROM Orders ORDER BY timeStampRecieved DESC LIMIT 5) O WHERE O.login = '%s'", authorisedUser);
         System.out.println("Your 5 most recent Orders: ");
	 esql.executeQueryAndPrintResult(query);
      }
      catch (Exception except) {
         System.err.println (except.getMessage());
      }
   }//end

   public static void UpdateUserInfo(Cafe esql, String authorisedUser, int perm){
      try {
         System.out.println("Please enter the following information to update, press ENTER to skip.");
         
         /* Phone Number */
         System.out.println("Please enter a new phone number: ");
         String phoneNum = in.readLine();
         while (phoneNum.length() != 13 && !phoneNum.isEmpty()) {
            System.out.print("Invalid phone number. ");
            System.out.println("Please enter a new phone number: ");
            phoneNum = in.readLine();
         }
         if (!phoneNum.isEmpty()) {
            String query = String.format("UPDATE Users SET phoneNum = '%s' WHERE login = '%s'", phoneNum, authorisedUser);
   	      esql.executeUpdate(query);
   	      System.out.println("Updated phone number successfully.");
         }
         else { 
            System.out.println("Phone number not changed.");
         }
	      
	      /* Password */
	      System.out.println("Please enter a new password: ");
	      String password = in.readLine();
	      while (password.length() > 50 || password.length() < 4) {
	         if (password.length() < 4) {
	            System.out.print("Password is too short. ");
	         }
	         else { 
	            System.out.print("Password is too long. "); 
	         }
	         System.out.println("Please enter a new password: ");
	         password = in.readLine();
	      }
	      if (!password.isEmpty()) {
   	      String query = String.format("UPDATE Users SET password = '%s' WHERE login = '%s'", password, authorisedUser);
   	      esql.executeUpdate(query);
   	      System.out.println("Updated password successfully.");
	      }
	      else {
	         System.out.println("Password not changed.");
	      }
	      
	      /* Favorite Items */
	      System.out.println("Please enter your favorite items, separated by a comma: ");
	      String favItems = in.readLine();
	      while (favItems.length() > 400) {
	         System.out.print("Too many favorite items. ");
	         System.out.println("Please enter your favorite items, separated by a comma: ");
	         favItems = in.readLine();
	      }
	      if (!favItems.isEmpty()) {
   	      String query = String.format("UPDATE Users SET favItems = '%s' WHERE login = '%s'", favItems, authorisedUser);
   	      esql.executeUpdate(query);
   	      System.out.println("Updated favorite items successfully.");
	      }
	      else {
	         System.out.println("Favorite items not changed.");
	      }
	      
      }
      catch (Exception except) {
         System.err.println (except.getMessage());
      }
   }//end

   public static String getSearchResultsAndPrintQuery(Cafe esql, int searchType) {
      try {
         String[] searchString = { "a user's login ID", "an item name", "an order id" };
         String[] primaryKey = { "login", "itemName", "orderID" };
         String[] table = { "Users", "Menu", "Orders"};
         
         System.out.println("Please enter " + searchString[searchType] + " to edit: ");
         String search = in.readLine();
         String query = "SELECT DISTINCT(" + primaryKey[searchType] + ") FROM " + 
                        table[searchType] + " WHERE " + primaryKey[searchType] + 
                        " LIKE '%" + search + "%'";
         
         List<String> results = new ArrayList<String>();
         List<List<String>> resultsList = new ArrayList<List<String>>();
         
         resultsList = esql.executeQueryAndReturnResult(query);
         for(int i = 0; i < resultsList.size(); i++) {
            results.add(resultsList.get(i).get(0));
            System.out.println(i + ". " + resultsList.get(i).get(0));
         }
         
         System.out.println("\n" + results.size() + " results found. Please enter the number from the list above, or a login ID: ");
         search = in.readLine();
   
         try {
            int selection = Integer.parseInt(search);
            search = results.get(selection);
         }
         catch (Exception e) {
         }
         
         query = "SELECT " + primaryKey[searchType] + " FROM " + table[searchType] + " WHERE " + primaryKey[searchType] + " = '" + search + "';";
         
         return query;
      }
      catch (Exception except) {
         System.err.println (except.getMessage());
      }
      return null;
   }

   public static void ManagerUpdateUserInfo(Cafe esql, String authorisedUser){
      try {
         boolean pending_selection = true;
          
         while (pending_selection) {
            System.out.println("Please select an option: ");
            System.out.println("\t 1. Change my information.");
            System.out.println("\t 2. Change another user's information.");
            System.out.println("\t 3. Go back.");
            
            switch (readChoice()) {
               case 1:
                  UpdateUserInfo(esql, authorisedUser, 0);
                  pending_selection = false;
                  break;
                  
               case 2:
                  String query = getSearchResultsAndPrintQuery(esql, 0);
                  
                  List<String> selectedUser = new ArrayList<String>();
                  List<List<String>> selectedUserList = new ArrayList<List<String>>();
                  
                  selectedUserList = esql.executeQueryAndReturnResult(query);
                  for(int i = 0; i < selectedUserList.size(); i++) {
                     selectedUser.add(selectedUserList.get(i).get(0));
                  }

                  /* Type of User */
         	      System.out.println("Please update the user's type: ");
         	      String typeOfUser = in.readLine();
         	      while (typeOfUser.length() > 8) {
         	         System.out.print("Not a valid user type. ");
         	         System.out.println("Please update the user's type: ");
         	         typeOfUser = in.readLine();
         	      }
         	      if (!typeOfUser.isEmpty()) {
            	      query = String.format("UPDATE Users SET type = '%s' WHERE login = '%s'", typeOfUser, selectedUser.get(0));
            	      esql.executeUpdate(query);
            	      System.out.println("Updated user type successfully.");
         	      }
         	      else {
         	         System.out.println("User type not changed.");
         	      }
         	      pending_selection = false;
                  break;
                  
               case 3:
                  pending_selection = false;
                  break;
                  
               default: 
                  System.out.println("Unrecognized choice!");
                  break;
            }
         }
      }
      catch (Exception except) {
         System.err.println (except.getMessage());
      }
   }//end

   public static void UpdateMenu(Cafe esql){
      try {
         System.out.println("1. Add new item");
         System.out.println("2. Delete existing item");
         System.out.println("3. Update existing item");
         String query = "";
         String name = "";
           switch (readChoice()){
             case 1:
               System.out.print("\tEnter item name: ");
               name = in.readLine();
               System.out.print("\tEnter item type: ");
               String type = in.readLine();
               System.out.print("\tEnter item price: ");
               String price = in.readLine();
               System.out.print("\tEnter item description: ");
               String desc = in.readLine();
               System.out.print("\tEnter item URL: ");
               String url = in.readLine();

               query = String.format("INSERT INTO Menu(itemName, type, price, description, imageURL) VALUES ('%s', '%s', %s, '%s', '%s');", name, type, price, desc, url);
               break;
             case 2:
               System.out.print("\tEnter the name of item to be deleted: ");
               name = in.readLine();
               query = "DELETE FROM Menu WHERE itemName = '" + name + "';";
               break;
             case 3:
               System.out.print("\tEnter the name of item to be updated: "); 
               name = in.readLine();
               System.out.println("1. Update item name");
               System.out.println("2. Update item type");
               System.out.println("3. Update item price");
               System.out.println("4. Update item description");
               System.out.println("5. Update item url");
               switch (readChoice()){
                 case 1:
                   System.out.print("\tEnter new name: ");
                    String newName = in.readLine();
                   query = "UPDATE Menu SET itemName = '" + newName + "' WHERE itemName = '" + name + "';"; 
                   break;
                 case 2:
                   System.out.print("\tEnter new type: ");
                   String newType = in.readLine();
                   query = "UPDATE Menu SET type = '" + newType + "' WHERE itemName = '" + name + "';"; 
                   break;
                 case 3:
                   System.out.print("\tEnter new price: ");
                   String newPrice = in.readLine();
                   query = "UPDATE Menu SET price = " + newPrice + " WHERE itemName = '" + name + "';"; 
                   break;
                 case 4:
                   System.out.print("\tEnter new description: ");
                   String newDescription = in.readLine();
                   query = "UPDATE Menu SET description = '" + newDescription + "' WHERE itemName = '" + name + "';";
                   break;
                 case 5:
                   System.out.print("\tEnter new url: ");
                   String newUrl = in.readLine();
                   query = "UPDATE Menu SET imageURL = '" + newUrl + "' WHERE itemName = '" + name + "';";
                   break;
                 default: break;
               }
               break;
             default: break;
           }

	   esql.executeUpdate(query);
      }
      catch (Exception except) {
         System.err.println (except.getMessage());
      }
   }//end

   public static void ViewOrderStatus(Cafe esql){
      try {
         System.out.print("\tEnter Order ID: ");
         String orderID = in.readLine();
         String query = "SELECT * FROM Orders WHERE orderid = ";
         query += orderID + ";";
	 esql.executeQueryAndPrintResult(query);

         query = "SELECT I.itemName FROM ItemStatus I WHERE I.orderid = " + orderID + ";";
         esql.executeQueryAndPrintResult(query);
      }
      catch (Exception except) {
         System.err.println (except.getMessage());
      }
   }//end

   public static void ViewCurrentOrder(Cafe esql, String authorisedUser, int perm){
      try {
         String query = String.format("SELECT O.orderid, O.timeStampRecieved FROM Orders O WHERE O.timeStampRecieved > (now() - interval '24 hours') AND O.paid = false");
	     esql.executeQueryAndPrintResult(query);
      }
      catch (Exception except) {
         System.err.println (except.getMessage());
      }
   }//end

   public static void ViewUserInfo(Cafe esql, String authorisedUser){
       try {
          String query = String.format("SELECT U.login, U.phoneNum, U.favItems, U.type FROM Users U WHERE U.login = '%s'", authorisedUser);
          esql.executeQueryAndPrintResult(query);
       }
       catch (Exception except) {
          System.err.println(except.getMessage());
       }
   }

   public static void BrowseMenu(Cafe esql){
       try {
          String query = String.format("SELECT * FROM Menu");
          esql.executeQueryAndPrintResult(query);
       }
       catch (Exception except) {
          System.err.println(except.getMessage());
       }

   }

}//end Cafe
