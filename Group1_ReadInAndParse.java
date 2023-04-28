//Group 1
//Due: 4/28/2023

import java.io.File;
import java.sql.*;
import java.util.*;
import java.security.MessageDigest;

public class Group1_ReadInAndParse {

    private Connection conn;
    private ResultSet rs;
    private Statement stmt;
    private String sql;
    private int col;

    final String DEFAULT_DRIVER = "com.mysql.cj.jdbc.Driver";

    public Group1_ReadInAndParse() {
    }

    public boolean connect() {
        conn = null;
        Scanner in = new Scanner(System.in);
        String userName = "root";
        System.out.println("User: \"root\"");

        //asking for the password
        System.out.print("Enter your MYSQL password (if it's 'student' just hit enter): ");
        String pass = in.nextLine();
        String password = "";
        if (pass.equals("")) {
            password = "student";
        } else {
            password = pass;
        }

        String url = "jdbc:mysql://localhost/group1";

        url = url + "?serverTimezone=UTC"; //added 8/27  Mac Users

        try {
            Class.forName(DEFAULT_DRIVER);
            conn = DriverManager.getConnection(url, userName, password);
            System.out.println("\nCreated Connection!\n");
        } catch (ClassNotFoundException cnfe) {
            System.out.println("ERROR, CAN NOT CONNECT!!");
            System.out.println("Class");
            System.out.println("ERROR MESSAGE-> " + cnfe);
            System.exit(0);
        } catch (SQLException sqle) {
            System.out.println("ERROR SQLExcepiton in connect()");
            System.out.println("ERROR MESSAGE -> " + sqle);
            sqle.printStackTrace();
            System.exit(0);
        }//end of catch

        return (conn != null);
    } // End of connect method

    public void close() {
        try {
            rs.close();
            stmt.close();
            conn.close();
        } catch (SQLException sqle) {
            System.out.println("ERROR IN METHOD close()");
            System.out.println("ERROR MESSAGE -> " + sqle);
        }// end of catch
    }//end of method close

    public int readInTxt(String file) {
        int rows = 0;
        //file format:
        //title\n
        //author(s) name(s): first last (multile seperated by ,)\n
        //abstract

        try {
            Scanner sc = new Scanner(new File(file));
            String title = sc.nextLine();
            String[] author = sc.nextLine().split(", ");
            String abst = "";

            while (sc.hasNextLine()) {
                abst += sc.nextLine();
            }

            String[][] authorSplit = new String[author.length][2];
            for (int i = 0; i < author.length; i++) {
                authorSplit[i] = author[i].split(" ");
            }

            //abstract parcing
            rows = parseAbstract(title, abst);

            //professor parcing 
            rows += parseProfessor(author, authorSplit);

            //author parcing 
            rows += parseAuthor(authorSplit, title, abst);

        } catch (Exception e) {
            System.out.println("Fatal error: \n" + e);
            return -1;
        }
        return rows;
    }

    public int readInDocx(String file) {
        return 0;
    }

    public int parseAbstract(String title, String abst) {
        int rows = 0;
        try {
            //check to see if abstract is already in database
            sql = "SELECT * FROM abstract WHERE title = (?) AND abstract = (?)";

            PreparedStatement stmt = conn.prepareStatement(sql);
            // bind values into the parameters
            stmt.setString(1, title);
            stmt.setString(2, abst);

            rs = stmt.executeQuery();

            //if not add abstract to database
            if (!rs.next()) {
                sql = "INSERT INTO abstract (title, abstract)VALUES(?,?)";

                stmt = conn.prepareStatement(sql);
                // bind values into the parameters
                stmt.setString(1, title);
                stmt.setString(2, abst);

                rows = stmt.executeUpdate();
            }
        } catch (SQLException sqle) {
            System.out.println("SQL ERROR");
            System.out.println("PARSE ABSTRACT FAILED!!!!");
            System.out.println("ERROR MESSAGE IS -> " + sqle);
            return rows;
        } catch (Exception e) {
            System.out.println("Error occured in parseAbstract method");
            System.out.println("ERROR MESSAGE is -> " + e);
            return rows;
        }
        return rows;
    }

    public int parseProfessor(String[] author, String[][] authorSplit) {
        int rows = 0;
        try {
            for (int i = 0; i < authorSplit.length; i++) {
                //check to see if professor is already in database
                sql = "SELECT * FROM professor WHERE firstName = (?) AND lastName = (?)";

                PreparedStatement stmt = conn.prepareStatement(sql);
                // bind values into the parameters
                stmt.setString(1, authorSplit[i][0]);
                stmt.setString(2, authorSplit[i][1]);

                rs = stmt.executeQuery();

                //if not add professor to database
                if (!rs.next()) {
                    sql = "INSERT INTO professor (firstName, lastName, buildingNum, officeNum, email)VALUES(?,?,?,?,?)";

                    System.out.print("\nEnter the building number of " + author[i] + ": ");
                    int building = GetInput.readInt();
                    System.out.print("\nEnter the office number of " + author[i] + ": ");
                    int office = GetInput.readInt();
                    System.out.print("\nEnter the email of " + author[i] + ": ");
                    String email = GetInput.readLine();

                    stmt = conn.prepareStatement(sql);
                    // bind values into the parameters
                    stmt.setString(1, authorSplit[i][0]);
                    stmt.setString(2, authorSplit[i][1]);
                    stmt.setInt(3, building);
                    stmt.setInt(4, office);
                    stmt.setString(5, email);

                    rows += stmt.executeUpdate();

                    //get professor id
                    sql = "SELECT professor_id FROM professor WHERE firstName = (?) AND lastName = (?)";

                    stmt = conn.prepareStatement(sql);
                    // bind values into the parameters
                    stmt.setString(1, authorSplit[i][0]);
                    stmt.setString(2, authorSplit[i][1]);

                    rs = stmt.executeQuery();
                    rs.next();
                    int id = rs.getInt(1);

                    //create username and hashed default password and insert into account
                    sql = "INSERT INTO professor_account (professorID, professor_userName, password) VALUES (?,?,?)";
                    String username = authorSplit[i][0].substring(0, 1) + authorSplit[i][1] + id;
                    String password = hashPass("*Admin*");

                    stmt = conn.prepareStatement(sql);
                    // bind values into the parameters
                    stmt.setInt(1, id);
                    stmt.setString(2, username);
                    stmt.setString(3, password);

                    rows += stmt.executeUpdate();
                }
            }

        } catch (SQLException sqle) {
            System.out.println("SQL ERROR");
            System.out.println("PARSE ABSTRACT FAILED!!!!");
            System.out.println("ERROR MESSAGE IS -> " + sqle);
            return rows;
        } catch (Exception e) {
            System.out.println("Error occured in parseProfessor method");
            System.out.println("ERROR MESSAGE is -> " + e);
            return rows;
        }
        return rows;
    }

    public int parseAuthor(String[][] authorSplit, String title, String abst) {
        int rows = 0;

        try {
            for (int i = 0; i < authorSplit.length; i++) {
                //get professor id
                sql = "SELECT professor_ID FROM professor WHERE firstName = (?) AND lastName = (?)";

                PreparedStatement stmt = conn.prepareStatement(sql);
                // bind values into the parameters
                stmt.setString(1, authorSplit[i][0]);
                stmt.setString(2, authorSplit[i][1]);

                rs = stmt.executeQuery();
                rs.next();
                int professor = rs.getInt(1);

                //get abstract id
                sql = "SELECT abstract_ID FROM abstract WHERE title = (?) AND abstract = (?)";

                stmt = conn.prepareStatement(sql);
                // bind values into the parameters
                stmt.setString(1, title);
                stmt.setString(2, abst);

                rs = stmt.executeQuery();
                rs.next();
                int abstID = rs.getInt(1);

                //check to see if author is already in database
                sql = "SELECT * FROM author WHERE professor_ID = (?) AND abstract_ID = (?)";

                stmt = conn.prepareStatement(sql);
                // bind values into the parameters
                stmt.setInt(1, professor);
                stmt.setInt(2, abstID);

                rs = stmt.executeQuery();

                //if not add author to database
                if (!rs.next()) {
                    sql = "INSERT INTO author (professor_ID, abstract_ID)VALUES(?,?)";

                    stmt = conn.prepareStatement(sql);
                    // bind values into the parameters
                    stmt.setInt(1, professor);
                    stmt.setInt(2, abstID);

                    rows += stmt.executeUpdate();
                }
            }

        } catch (SQLException sqle) {
            System.out.println("SQL ERROR");
            System.out.println("PARSE AUTHOR FAILED!!!!");
            System.out.println("ERROR MESSAGE IS -> " + sqle);
            return rows;
        } catch (Exception e) {
            System.out.println("Error occured in parseAuthor method");
            System.out.println("ERROR MESSAGE is -> " + e);
            return rows;
        }
        return rows;
    }

    //login faculty
    public boolean facultyLogin() {
        boolean correct = false;

        try {
            while (!correct) {
                //get username and password
                System.out.print("\nEnter username: ");
                String username = GetInput.readLine();
                System.out.print("Enter password: ");
                //hash the password
                String password = hashPass(GetInput.readLine());

                //check to see if login is correct
                sql = "SELECT * FROM professor_account WHERE password = (?) AND professor_userName = (?)";

                PreparedStatement stmt = conn.prepareStatement(sql);
                // bind values into the parameters
                stmt.setString(1, password);
                stmt.setString(2, username);

                rs = stmt.executeQuery();

                if (!rs.next()) {
                    System.out.println("Incorect username or password");
                    System.out.println("1. Try again");
                    System.out.println("2. Exit");
                    System.out.print("Selected: ");
                    int selected = GetInput.readInt();

                    switch (selected) {
                        case 1: //try again
                            break;

                        case 2: //exit
                            return false;
                    }
                } else {
                    correct = true;
                    return true;
                }
            }
        } catch (SQLException sqle) {
            System.out.println("SQL ERROR");
            System.out.println("FACULTY LOGIN FAILED!!!!");
            System.out.println("ERROR MESSAGE IS -> " + sqle);
            return false;
        } catch (Exception e) {
            System.out.println("Error occured in facultyLogin method");
            System.out.println("ERROR MESSAGE is -> " + e);
            return false;
        }
        return true;
    }

    //hash password
    public String hashPass(String password) {
        String hashed = "";

        byte[] bytes = password.getBytes();

        try {
            MessageDigest digest = MessageDigest.getInstance("SHA");
            digest.reset();
            digest.update(bytes);
            byte[] encodedPassword = digest.digest();

            StringBuilder builder = new StringBuilder();
            for (byte b : encodedPassword) {
                if ((b & 0xff) < 0x10) {
                    builder.append("0");
                }
                builder.append(Long.toString(b & 0xff, 16));
            }
            hashed = builder.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return hashed;
    }
}
