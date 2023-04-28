//Group 1
//Data Layer
//Due 4/14/2023

import java.io.File;
import java.sql.*;
import java.util.*;
import java.security.MessageDigest;

public class Group1_DataLayer {

    private Connection conn;
    private ResultSet rs;
    private Statement stmt;
    private String sql;
    private int col;

    final String DEFAULT_DRIVER = "com.mysql.cj.jdbc.Driver";

    public Group1_DataLayer() {
    }

    public boolean connect() {
        conn = null;
        Scanner in = new Scanner(System.in);
        String userName = "root";
        System.out.println("User: \"root\"");

        //asking for the password
        System.out.print("Enter your MYSQL password (Default: student): ");
        String pass = in.nextLine();
        String password = "";
        if (pass.equals("")) {
            password = "student";
        } else {
            password = pass;
        }

        String url = "jdbc:mysql://localhost/group1";

        url = url + "?serverTimezone=UTC"; 

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

        try {
            Scanner sc = new Scanner(new File(file));
            String title = sc.nextLine();
            String[] author = sc.nextLine().split(", ");
            String abst = "";

            while (sc.hasNext()) {
                abst += sc.next() + " ";
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
                    System.out.print("Is " + author[i] + " a professor at RIT? (y/n): ");
                    boolean prof = checkYesNo();
                    if (prof) {
                        sql = "INSERT INTO professor (firstName, lastName, buildingNum, officeNum, email)VALUES(?,?,?,?,?)";

                        System.out.print("\nEnter the building number of " + author[i] + ": ");
                        int building = GetInput.readInt();
                        System.out.print("\nEnter the office number of " + author[i] + ": ");
                        int office = GetInput.readInt();
                        System.out.print("\nEnter the email of " + author[i] + ": ");
                        String email = GetInput.readWord();

                        stmt = conn.prepareStatement(sql);
                        // bind values into the parameters
                        stmt.setString(1, authorSplit[i][0]);
                        stmt.setString(2, authorSplit[i][1]);
                        stmt.setInt(3, building);
                        stmt.setInt(4, office);
                        stmt.setString(5, email);

                        rows += stmt.executeUpdate();

                        //add account for professor
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
                        String username = (authorSplit[i][0].substring(0, 1) + authorSplit[i][1] + id).toLowerCase();
                        String password = "f3753089c2d408f362dc5808b7f73742612be2b1";

                        System.out.println("\nUsername created for " + author[i] + ": " + username);

                        stmt = conn.prepareStatement(sql);
                        // bind values into the parameters
                        stmt.setInt(1, id);
                        stmt.setString(2, username);
                        stmt.setString(3, password);

                        rows += stmt.executeUpdate();
                    }
                }
            }
        } catch (SQLException sqle) {
            System.out.println("SQL ERROR");
            System.out.println("PARSE PROFESSOR FAILED!!!!");
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

                //check to see if professor
                if (rs.next()) {
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

                    //check to see if professor_abstract is already in database
                    sql = "SELECT * FROM professor_abstract WHERE professor_ID = (?) AND abstract_ID = (?)";

                    stmt = conn.prepareStatement(sql);
                    // bind values into the parameters
                    stmt.setInt(1, professor);
                    stmt.setInt(2, abstID);

                    rs = stmt.executeQuery();

                    //if not add professor_abstract to database
                    if (!rs.next()) {
                        sql = "INSERT INTO professor_abstract (professor_ID, abstract_ID)VALUES(?,?)";

                        stmt = conn.prepareStatement(sql);
                        // bind values into the parameters
                        stmt.setInt(1, professor);
                        stmt.setInt(2, abstID);

                        rows += stmt.executeUpdate();
                    }
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
    public boolean facultyLogin(String username, String password) {
        try {
            //debug/maintenance login override
            if (username.equals("AdminTest") && hashPass(password).equals("f3753089c2d408f362dc5808b7f73742612be2b1")) {
                return true;
            }
            //hash password
            password = hashPass(password);
            //check to see if login is correct
            sql = "SELECT * FROM professor_account WHERE password = (?) AND professor_userName = (?)";

            PreparedStatement stmt = conn.prepareStatement(sql);
            // bind values into the parameters
            stmt.setString(1, password);
            stmt.setString(2, username);

            rs = stmt.executeQuery();

            if (!rs.next()) {
                return false;
            } else {
                return true;
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
    }

    //hash password to store in database
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
   //Show Professor Keywords
   public int showKeywords(String userName) {
      int rows = 0;
      try {
         sql = "SELECT professorID FROM professor_account WHERE professor_userName = (?)";
         PreparedStatement stmt = conn.prepareStatement(sql);
         stmt.setString(1, userName);
         rs = stmt.executeQuery();
         rs.next();
         int prof_id = rs.getInt(1);
         sql = "SELECT keyword FROM keyword, professor_keyword WHERE keyword.keyword_id = professor_keyword.keyword_id AND professorkey_id = (?) ";
         stmt = conn.prepareStatement(sql);
         stmt.setInt(1, prof_id);
         rs = stmt.executeQuery();
         System.out.println("\nCurrent Keywords: ");
         while (rs.next()) {
            String keyword = rs.getString(1);
            System.out.print(keyword+", ");
         }
         
      } catch (SQLException sqle) {
            System.out.println("SQL ERROR");
            System.out.println("ADD STUDENT FAILED!!!!");
            System.out.println("ERROR MESSAGE IS -> " + sqle);
            return 0;
      } catch (Exception e) {
            System.out.println("Error occured in addStudent method");
            System.out.println("ERROR MESSAGE is -> " + e);
            return 0;
      }
      return rows;
   }
   //Show student interest
   public int showInterests(String userName) {
      int rows = 0;
      try {
         sql = "SELECT studentID FROM student_account WHERE student_userName = (?)";
         PreparedStatement stmt = conn.prepareStatement(sql);
         stmt.setString(1, userName);
         rs = stmt.executeQuery();
         rs.next();
         int stu_id = rs.getInt(1);
         sql = "SELECT interest FROM interests, student_interests WHERE interests.interest_id = student_interests.interest_id AND student_id = (?) ";
         stmt = conn.prepareStatement(sql);
         stmt.setInt(1, stu_id);
         rs = stmt.executeQuery();
         System.out.println("\nCurrent Interests: ");
         while (rs.next()) {
            String interest = rs.getString(1);
            System.out.print(interest+", ");
         }
         
      } catch (SQLException sqle) {
            System.out.println("SQL ERROR");
            System.out.println("ADD STUDENT FAILED!!!!");
            System.out.println("ERROR MESSAGE IS -> " + sqle);
            return 0;
      } catch (Exception e) {
            System.out.println("Error occured in addStudent method");
            System.out.println("ERROR MESSAGE is -> " + e);
            return 0;
      }
      return rows;
   }
   //add student interest
   public int addStudentInterest(String userName, String interest) {
      int rows = 0;
      try {
         sql = "SELECT * FROM interests WHERE interest = (?)";
         PreparedStatement stmt = conn.prepareStatement(sql);
         stmt.setString(1, interest);
         rs = stmt.executeQuery();
         if (!rs.next()) {
            //Creating interest in interest table
            sql = "INSERT INTO interests (interest) VALUES (?)";
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, interest);
            rows += stmt.executeUpdate();
            //Getting keyword id
            sql = "SELECT interest_id FROM interests WHERE interest = (?)";
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, interest);
            rs = stmt.executeQuery();
            rs.next();
            int int_id = rs.getInt(1);
            //Getting professor id from userName
            sql = "SELECT studentID FROM student_account WHERE student_userName = (?)";
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, userName);
            rs = stmt.executeQuery();
            rs.next();
            int stu_id = rs.getInt(1);
            //Adding entry to professor_keyword
            sql = "INSERT INTO student_interests VALUES (?,?)";
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, stu_id);
            stmt.setInt(2, int_id);
            rows += stmt.executeUpdate();
         } else {
            //Getting interest id
            sql = "SELECT interest_id FROM interests WHERE interest = (?)";
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, interest);
            rs = stmt.executeQuery();
            rs.next();
            int int_id = rs.getInt(1);
            //Getting student id from userName
            sql = "SELECT studentID FROM student_account WHERE student_userName = (?)";
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, userName);
            rs = stmt.executeQuery();
            rs.next();
            int stu_id = rs.getInt(1);
            //Adding entry to student_interest
            sql = "INSERT INTO student_interests VALUES (?,?)";
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, stu_id);
            stmt.setInt(2, int_id);
            rows += stmt.executeUpdate();
         }
         
         
      } catch (SQLException sqle) {
            System.out.println("SQL ERROR");
            System.out.println("ADD STUDENT FAILED!!!!");
            System.out.println("ERROR MESSAGE IS -> " + sqle);
            return 0;
      } catch (Exception e) {
            System.out.println("Error occured in addStudent method");
            System.out.println("ERROR MESSAGE is -> " + e);
            return 0;
      }
      return rows;
   }
   
   //remove student_interest
   public int removeStuInterest(String userName, String interest) {
      int rows = 0;
      try {
            //Gets existing interest_id
            sql = "SELECT interest_id FROM interests WHERE interest = (?)";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, interest);
            rs = stmt.executeQuery();
            rs.next();
            int int_id = rs.getInt(1);
            //Getting professor id from userName
            sql = "SELECT studentID FROM student_account WHERE student_userName = (?)";
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, userName);
            rs = stmt.executeQuery();
            rs.next();
            int stu_id = rs.getInt(1);
            sql = "DELETE FROM student_interests WHERE student_id = (?) AND interest_id = (?)";
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, stu_id);
            stmt.setInt(2, int_id);
            rows += stmt.executeUpdate();
         
      } catch (SQLException sqle) {
            System.out.println("SQL ERROR");
            System.out.println("ADD FACULTY KEYWORD FAILED!!!!");
            System.out.println("ERROR MESSAGE IS -> " + sqle);
            return 0;
      } catch (Exception e) {
            System.out.println("Error occured in addFacultyKeyword method");
            System.out.println("ERROR MESSAGE is -> " + e);
            return 0;
      }
      return rows;
   }
   
   //Student lookup prof by keyword(s)
   public int searchProfKey(String[] keywords) {
      int rows = 0;
      try {
         sql = "SELECT * FROM student"; //useless filler SQL
         PreparedStatement stmt = conn.prepareStatement(sql);
         int i = 0;
         while (i < keywords.length){
            sql = "SELECT professorkey_id FROM professor_keyword, keyword WHERE keyword.keyword = (?) AND keyword.keyword_id = professor_keyword.keyword_id";
            stmt = conn.prepareStatement(sql);
            keywords[i] = keywords[i].strip();
            stmt.setString(1, keywords[i]);
            rs.next();
            rs = stmt.executeQuery();
            System.out.println("Professors with the "+ keywords[i] +" keyword:");
            int col = 1;
            while (rs.next()){
               int prof_id = rs.getInt(col);
               sql = "SELECT CONCAT(lastName,',  ', firstName,',  ', email) AS prof_info FROM professor WHERE professor_id = (?);";
               stmt = conn.prepareStatement(sql);
               stmt.setInt(1, prof_id);
               rs = stmt.executeQuery();
               rs.next();
               String prof_info = rs.getString(1);
               System.out.println(prof_info+"\n");
               col = col + 1;
            }
            i = i + 1;
         }
      } catch (SQLException sqle) {
            System.out.println("SQL ERROR");
            System.out.println("ADD STUDENT FAILED!!!!");
            System.out.println("ERROR MESSAGE IS -> " + sqle);
            return 0;
      } catch (Exception e) {
            System.out.println("Error occured in addStudent method");
            System.out.println("ERROR MESSAGE is -> " + e);
            return 0;
      }
      return rows;
   }
    //add student and student_account to database
    public int addStudent(String fName, String lName, String s_email) {
        int rows = -1;

        try {
            //check to see if student is already in database
            sql = "SELECT * FROM student WHERE firstName = (?) AND lastName = (?) AND email = (?)";

            PreparedStatement stmt = conn.prepareStatement(sql);
            // bind values into the parameters
            stmt.setString(1, fName);
            stmt.setString(2, lName);
            stmt.setString(3, s_email);

            rs = stmt.executeQuery();

            //if not add student and student_account to database
            if (!rs.next()) {
                //add student
                sql = "INSERT INTO student (firstName, lastName, email)VALUES(?,?,?)";

                stmt = conn.prepareStatement(sql);
                // bind values into the parameters
                stmt.setString(1, fName);
                stmt.setString(2, lName);
                stmt.setString(3, s_email);

                rows = stmt.executeUpdate();

                //get student_id created
                sql = "SELECT student_id FROM student WHERE firstName = (?) AND lastName = (?) AND email = (?)";

                stmt = conn.prepareStatement(sql);
                // bind values into the parameters
                stmt.setString(1, fName);
                stmt.setString(2, lName);
                stmt.setString(3, s_email);

                rs = stmt.executeQuery();
                rs.next();

                int id = rs.getInt(1);

                //create username
                String username = (fName.substring(0, 1) + lName + id).toLowerCase();

                //add student_account
                sql = "INSERT INTO student_account (studentID, student_userName)VALUES(?,?)";

                stmt = conn.prepareStatement(sql);
                // bind values into the parameters
                stmt.setInt(1, id);
                stmt.setString(2, username);

                rows += stmt.executeUpdate();

                System.out.println("\nUsername created: " + username);
            } else {
                System.out.println("Student is already in the database");
            }
        } catch (SQLException sqle) {
            System.out.println("SQL ERROR");
            System.out.println("ADD STUDENT FAILED!!!!");
            System.out.println("ERROR MESSAGE IS -> " + sqle);
            return 0;
        } catch (Exception e) {
            System.out.println("Error occured in addStudent method");
            System.out.println("ERROR MESSAGE is -> " + e);
            return 0;
        }

        return rows;
    }

    //delete student and related account and interests from database
    public int deleteStudent(String username) {
        int rows = 0;

        try {
            //check to see if student is in database
            sql = "SELECT * FROM student_account WHERE student_userName = (?)";

            PreparedStatement stmt = conn.prepareStatement(sql);
            // bind values into the parameters
            stmt.setString(1, username);

            rs = stmt.executeQuery();

            //if student is in database delete the student and student_account
            if (rs.next()) {
                //get student id
                int id = rs.getInt(1);
                //delete student_account
                sql = "DELETE FROM student_account WHERE student_userName = (?)";

                stmt = conn.prepareStatement(sql);
                // bind values into the parameters
                stmt.setString(1, username);

                rows = stmt.executeUpdate();

                //delete student
                sql = "DELETE FROM student WHERE student_id = (?)";

                stmt = conn.prepareStatement(sql);
                // bind values into the parameters
                stmt.setInt(1, id);

                rows += stmt.executeUpdate();
            } else {
                System.out.println("Username is not correct");
            }

        } catch (SQLException sqle) {
            System.out.println("SQL ERROR");
            System.out.println("ADD STUDENT FAILED!!!!");
            System.out.println("ERROR MESSAGE IS -> " + sqle);
            return 0;
        } catch (Exception e) {
            System.out.println("Error occured in addStudent method");
            System.out.println("ERROR MESSAGE is -> " + e);
            return 0;
        }

        return rows;
    }
   //add faculty keywords
   public int addFacultyKeyword(String userName, String keyword){
      int rows = 0;
      try {
         sql = "SELECT * FROM keyword WHERE keyword = (?)";
         PreparedStatement stmt = conn.prepareStatement(sql);
         stmt.setString(1, keyword);
         rs = stmt.executeQuery();
         if (!rs.next()) {
            //Creating keyword in keyword table
            sql = "INSERT INTO keyword (keyword) VALUES (?)";
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, keyword);
            rows += stmt.executeUpdate();
            //Getting keyword id
            sql = "SELECT keyword_id FROM keyword WHERE keyword = (?)";
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, keyword);
            rs = stmt.executeQuery();
            rs.next();
            int key_id = rs.getInt(1);
            //Getting professor id from userName
            sql = "SELECT professorID FROM professor_account WHERE professor_userName = (?)";
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, userName);
            rs = stmt.executeQuery();
            rs.next();
            int prof_id = rs.getInt(1);
            //Adding entry to professor_keyword
            sql = "INSERT INTO professor_keyword VALUES (?,?)";
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, prof_id);
            stmt.setInt(2, key_id);
            rows += stmt.executeUpdate();
         } else {
            //Gets existing keyword_id
            sql = "SELECT keyword_id FROM keyword WHERE keyword = (?)";
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, keyword);
            rs = stmt.executeQuery();
            rs.next();
            int key_id = rs.getInt(1);
            //Getting professor id from userName
            sql = "SELECT professorID FROM professor_account WHERE professor_userName = (?)";
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, userName);
            rs = stmt.executeQuery();
            rs.next();
            int prof_id = rs.getInt(1);
            //Adding entry to professor_keyword
            sql = "INSERT INTO professor_keyword VALUES (?,?)";
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, prof_id);
            stmt.setInt(2, key_id);
            rows += stmt.executeUpdate();
         }
         
      } catch (SQLException sqle) {
            System.out.println("SQL ERROR");
            System.out.println("ADD FACULTY KEYWORD FAILED!!!!");
            System.out.println("ERROR MESSAGE IS -> " + sqle);
            return 0;
      } catch (Exception e) {
            System.out.println("Error occured in addFacultyKeyword method");
            System.out.println("ERROR MESSAGE is -> " + e);
            return 0;
      }

      return rows;
   }
   //remove professor_keyword
   public int removeProfKeyword(String userName, String keyword) {
      int rows = 0;
      try {
            //Gets existing keyword_id
            sql = "SELECT keyword_id FROM keyword WHERE keyword = (?)";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, keyword);
            rs = stmt.executeQuery();
            rs.next();
            int key_id = rs.getInt(1);
            //Getting professor id from userName
            sql = "SELECT professorID FROM professor_account WHERE professor_userName = (?)";
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, userName);
            rs = stmt.executeQuery();
            rs.next();
            int prof_id = rs.getInt(1);
            sql = "DELETE FROM professor_keyword WHERE professorkey_id = (?) AND keyword_id = (?)";
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, prof_id);
            stmt.setInt(2, key_id);
            rows += stmt.executeUpdate();
         
      } catch (SQLException sqle) {
            System.out.println("SQL ERROR");
            System.out.println("ADD FACULTY KEYWORD FAILED!!!!");
            System.out.println("ERROR MESSAGE IS -> " + sqle);
            return 0;
      } catch (Exception e) {
            System.out.println("Error occured in addFacultyKeyword method");
            System.out.println("ERROR MESSAGE is -> " + e);
            return 0;
      }
      return rows;
   }
    //add faculty
    public int addFaculty(String fName, String lName, int building, int office, String email) {
        int rows = 0;
        
        try {
            //check to see if faculty is already in database
            sql = "SELECT * FROM professor WHERE firstName = (?) AND lastName = (?) "
                    + "AND buildingNum = (?) AND officeNum = (?) AND email = (?)";

            PreparedStatement stmt = conn.prepareStatement(sql);
            // bind values into the parameters
            stmt.setString(1, fName);
            stmt.setString(2, lName);
            stmt.setInt(3, building);
            stmt.setInt(4, office);
            stmt.setString(5, email);

            rs = stmt.executeQuery();

            //if not add professor and professor_account to database
            if (!rs.next()) {
                sql = "INSERT INTO professor (firstName, lastName, buildingNum, officeNum, email)VALUES(?,?,?,?,?)";

                stmt = conn.prepareStatement(sql);
                // bind values into the parameters
                stmt.setString(1, fName);
                stmt.setString(2, lName);
                stmt.setInt(3, building);
                stmt.setInt(4, office);
                stmt.setString(5, email);

                rows += stmt.executeUpdate();

                //add account for professor
                //get professor id
                sql = "SELECT professor_id FROM professor WHERE firstName = (?) AND lastName = (?)";

                stmt = conn.prepareStatement(sql);
                // bind values into the parameters
                stmt.setString(1, fName);
                stmt.setString(2, lName);

                rs = stmt.executeQuery();
                rs.next();
                int id = rs.getInt(1);

                //create username and hashed default password and insert into account
                sql = "INSERT INTO professor_account (professorID, professor_userName, password) VALUES (?,?,?)";
                String username = (fName.substring(0, 1) + lName + id).toLowerCase();
                String password = "f3753089c2d408f362dc5808b7f73742612be2b1";

                System.out.println("\nUsername created for " + fName + " " + lName + ": " + username);

                stmt = conn.prepareStatement(sql);
                // bind values into the parameters
                stmt.setInt(1, id);
                stmt.setString(2, username);
                stmt.setString(3, password);

                rows += stmt.executeUpdate();
            } else {
                System.out.println("Faculty is already in the database");
            }

        } catch (SQLException sqle) {
            System.out.println("SQL ERROR");
            System.out.println("ADD FACULTY FAILED!!!!");
            System.out.println("ERROR MESSAGE IS -> " + sqle);
            return 0;
        } catch (Exception e) {
            System.out.println("Error occured in addFaculty method");
            System.out.println("ERROR MESSAGE is -> " + e);
            return 0;
        }

        return rows;
    }

    //delete faculty
    public int deleteFaculty(String username) {
        int rows = 0;

        try {
            //check to see if professor is in database
            sql = "SELECT * FROM professor_account WHERE professor_userName = (?)";

            PreparedStatement stmt = conn.prepareStatement(sql);
            // bind values into the parameters
            stmt.setString(1, username);

            rs = stmt.executeQuery();

            //if professor is in database delete the professor, professor_abstract, and professor_account
            if (rs.next()) {
                //get professor id
                int id = rs.getInt(1);
                //delete professor_account
                sql = "DELETE FROM professor_account WHERE professor_userName = (?)";

                stmt = conn.prepareStatement(sql);
                // bind values into the parameters
                stmt.setString(1, username);

                rows = stmt.executeUpdate();

                //delete professor_abstract
                sql = "DELETE FROM professor_abstract WHERE professor_id = (?)";

                stmt = conn.prepareStatement(sql);
                // bind values into the parameters
                stmt.setInt(1, id);

                rows += stmt.executeUpdate();

                //delete professor
                sql = "DELETE FROM professor WHERE professor_id = (?)";

                stmt = conn.prepareStatement(sql);
                // bind values into the parameters
                stmt.setInt(1, id);

                rows += stmt.executeUpdate();
            } else {
                System.out.println("The username is incorrect");
            }
        } catch (SQLException sqle) {
            System.out.println("SQL ERROR");
            System.out.println("DELETE FACULTY FAILED!!!!");
            System.out.println("ERROR MESSAGE IS -> " + sqle);
            return 0;
        } catch (Exception e) {
            System.out.println("Error occured in deleteFaculty method");
            System.out.println("ERROR MESSAGE is -> " + e);
            return 0;
        }

        return rows;
    }

//change password
    public int changePass(String username, String curPass, String newPass) {
        int rows = -1;

        curPass = hashPass(curPass);
        newPass = hashPass(newPass);

        try {
            //check to see if current password is correct
            sql = "SELECT * FROM professor_account WHERE password = (?) AND professor_userName = (?)";

            PreparedStatement stmt = conn.prepareStatement(sql);
            // bind values into the parameters
            stmt.setString(1, curPass);
            stmt.setString(2, username);

            rs = stmt.executeQuery();

            if (!rs.next()) {
                System.out.println("Incorrect current password");
                return 0;
            } //check to see if the passwords are different
            else if (newPass.equals(curPass)) {
                System.out.println("New password is the same as Current Password");
                return 0;
            } else {
                //update the password in the database
                sql = "UPDATE professor_account SET password = ? WHERE professor_userName = ?";

                stmt = conn.prepareStatement(sql);
                // bind values into the parameters
                stmt.setString(1, newPass);
                stmt.setString(2, username);

                rows = stmt.executeUpdate();
            }

        } catch (SQLException sqle) {
            System.out.println("SQL ERROR");
            System.out.println("CHANGE PASSWORD FAILED!!!!");
            System.out.println("ERROR MESSAGE IS -> " + sqle);
            return 0;
        } catch (Exception e) {
            System.out.println("Error occured in changePass method");
            System.out.println("ERROR MESSAGE is -> " + e);
            return 0;
        }
        return rows;
    }

    //login student
    public boolean studentLogin(String username) {
        try {
            //check to see if login is correct
            sql = "SELECT * FROM student_account WHERE student_userName = (?)";

            PreparedStatement stmt = conn.prepareStatement(sql);
            // bind values into the parameters
            stmt.setString(1, username);

            rs = stmt.executeQuery();

            if (!rs.next()) {
                return false;
            } else {
                return true;
            }
        } catch (SQLException sqle) {
            System.out.println("SQL ERROR");
            System.out.println("Student LOGIN FAILED!!!!");
            System.out.println("ERROR MESSAGE IS -> " + sqle);
            return false;
        } catch (Exception e) {
            System.out.println("Error occured in studentLogin method");
            System.out.println("ERROR MESSAGE is -> " + e);
            return false;
        }
    }

    /**search abstract for key words, Checks to see if these interests exist in intersts table
    , Also tags student with interests, so needs their username, Checks student_account table for student_ID based on username
    Checks interests table for interest id, Makes new record in student_interests table pairing the two**/
    
    public int searchAbstract(String uname, String inter1, String inter2, String inter3) {

        int rows = 0;
        PreparedStatement stmt;

        String inters = "";
        int checks = 0;

        try {

            //Check if first interest is blank and if in database AND tagged to student, puts in database and/or tags if needed
            if (inter1 != "") {
                //add interest to search string
                inters += inter1;
                //add one to check
                checks++;

                sql = "SELECT * FROM interests WHERE interest = (?)";
                stmt = conn.prepareStatement(sql);

                stmt.setString(1, inter1);
                rs = stmt.executeQuery();

                //if not in database, add interest to database
                if (!rs.next()) {
                    sql = "INSERT INTO interests (interest) VALUES(?)";
                    stmt = conn.prepareStatement(sql);

                    stmt.setString(1, inter1);

                    rows += stmt.executeUpdate();
                }

                rs.next();
                //tag students with the interests they searched
                //get student_ID
                sql = "SELECT studentID FROM student_account WHERE student_userName = (?)";
                stmt = conn.prepareStatement(sql);

                stmt.setString(1, uname);
                rs = stmt.executeQuery();
                rs.next();
                String s_ID = rs.getString(1);

                //get interest_ID
                sql = "SELECT interest_ID FROM interests WHERE interest = (?)";
                stmt = conn.prepareStatement(sql);

                stmt.setString(1, inter1);
                rs = stmt.executeQuery();
                rs.next();
                String i_ID = rs.getString(1);

                //check for existing relationship between student_ID and interest_ID
                sql = "SELECT * FROM student_interests WHERE interest_ID = (?) AND student_ID = (?)";
                stmt = conn.prepareStatement(sql);

                stmt.setString(1, i_ID);
                stmt.setString(2, s_ID);
                rs = stmt.executeQuery();

                //if none, add relationship in student_interests table
                if (!rs.next()) {
                    sql = "INSERT INTO student_interests (student_ID,interest_ID) VALUES(?,?)";
                    stmt = conn.prepareStatement(sql);

                    stmt.setString(1, s_ID);
                    stmt.setString(2, i_ID);

                    rows += stmt.executeUpdate();
                }

            }
            rs.next();
            //Check if second interest is blank and if in database AND tagged to student, puts in database and/or tags if needed
            if (inter2 != "") {
                //check current checks passsed and add interst to search string
                if (checks > 0) {
                    inters += "|" + inter2;
                } else {
                    inters += inter2;
                }
                //add one to check
                checks++;

                sql = "SELECT * FROM interests WHERE interest = (?)";
                stmt = conn.prepareStatement(sql);

                stmt.setString(1, inter2);
                rs = stmt.executeQuery();
                rs.next();

                //if not in database, add interest to database
                if (!rs.next()) {
                    sql = "INSERT INTO interests (interest) VALUES(?)";
                    stmt = conn.prepareStatement(sql);

                    stmt.setString(1, inter2);

                    rows += stmt.executeUpdate();
                }
                rs.next();
                //tag students with the interests they searched
                //get student_ID
                sql = "SELECT studentID FROM student_account WHERE student_userName = (?)";
                stmt = conn.prepareStatement(sql);

                stmt.setString(1, uname);
                rs = stmt.executeQuery();
                rs.next();
                String s_ID = rs.getString(1);

                //get interest_ID
                sql = "SELECT interest_ID FROM interests WHERE interest = (?)";
                stmt = conn.prepareStatement(sql);

                stmt.setString(1, inter2);
                rs = stmt.executeQuery();
                rs.next();
                String i_ID = rs.getString(1);

                //check for existing relationship between student_ID and interest_ID
                sql = "SELECT * FROM student_interests WHERE interest_ID = (?) AND student_ID = (?)";
                stmt = conn.prepareStatement(sql);

                stmt.setString(1, i_ID);
                stmt.setString(2, s_ID);
                rs = stmt.executeQuery();

                //if none, add relationship in student_interests table
                if (!rs.next()) {
                    sql = "INSERT INTO student_interests (student_ID,interest_ID) VALUES(?,?)";
                    stmt = conn.prepareStatement(sql);

                    stmt.setString(1, s_ID);
                    stmt.setString(2, i_ID);

                    rows += stmt.executeUpdate();
                }
            }
            rs.next();
            //Check if third interest is blank and if in database AND tagged to student, puts in database and/or tags if needed
            if (inter3 != "") {
                //check current checks passsed and add interst to search string
                if (checks > 0) {
                    inters += "|" + inter3;
                } else {
                    inters += inter3;
                }
                //add one to check
                checks++;

                sql = "SELECT * FROM interests WHERE interest = (?)";
                stmt = conn.prepareStatement(sql);

                stmt.setString(1, inter3);
                rs = stmt.executeQuery();

                //if not in database, add interest to database
                if (!rs.next()) {
                    sql = "INSERT INTO interests (interest) VALUES(?)";
                    stmt = conn.prepareStatement(sql);

                    stmt.setString(1, inter3);

                    rows += stmt.executeUpdate();
                }
                rs.next();
                //tag students with the interests they searched
                //get student_ID
                sql = "SELECT studentID FROM student_account WHERE student_userName = (?)";
                stmt = conn.prepareStatement(sql);

                stmt.setString(1, uname);
                rs = stmt.executeQuery();
                rs.next();
                String s_ID = rs.getString(1);

                //get interest_ID
                sql = "SELECT interest_ID FROM interests WHERE interest = (?)";
                stmt = conn.prepareStatement(sql);

                stmt.setString(1, inter3);
                rs = stmt.executeQuery();
                rs.next();
                String i_ID = rs.getString(1);

                //check for existing relationship between student_ID and interest_ID
                sql = "SELECT * FROM student_interests WHERE interest_ID = (?) AND student_ID = (?)";
                stmt = conn.prepareStatement(sql);

                stmt.setString(1, i_ID);
                stmt.setString(2, s_ID);
                rs = stmt.executeQuery();

                //if none, add relationship in student_interests table
                if (!rs.next()) {
                    sql = "INSERT INTO student_interests (student_ID,interest_ID) VALUES(?,?)";
                    stmt = conn.prepareStatement(sql);

                    stmt.setString(1, s_ID);
                    stmt.setString(2, i_ID);

                    rows += stmt.executeUpdate();
                }
                rs.next();

            }

            //actually finally search abstracts with the longest sql command in the world
            sql = "SELECT abstract.title AS 'Title', concat(professor.lastName, ', ',  professor.firstName) AS 'Name', "
                    + "professor.buildingNum AS 'Building Number', professor.officeNum AS 'Office Number', professor.email FROM abstract "
                    + "JOIN professor_abstract ON abstract.abstract_id = professor_abstract.abstract_id "
                    + "JOIN professor ON professor_abstract.professor_id = professor.professor_id "
                    + "WHERE abstract.title REGEXP ? OR abstract.abstract REGEXP ?";
            stmt = conn.prepareStatement(sql);

            stmt.setString(1, inters);
            stmt.setString(2, inters);

            rs = stmt.executeQuery();

            System.out.println();
            System.out.printf("%-80s %-35s %-20s %-20s %-23s", "Title", "Name", "Building Number", "Office Number", "Email");
            System.out.println();

            if (rs.next()) {//if results are found
                System.out.println();
                System.out.printf("%-80s %-35s %-20s %-20s %-23s", "Title", "Name", "Building Number", "Office Number", "Email");
                System.out.println();

                //if there is a result print it out
                do {
                    String title = rs.getString(1);
                    String name = rs.getString(2);
                    int building = rs.getInt(3);
                    int office = rs.getInt(4);
                    String email = rs.getString(5);

                    System.out.printf("%-80s %-35s %-20d %-20d %-23s", title, name, building, office, email);
                    System.out.println();
                } while (rs.next());//loops through all results
            } else {//if no results are found
                System.out.println("No results found");
            }

        } catch (SQLException sqle) {
            System.out.println("SQL ERROR");
            System.out.println("SEARCH/INSERT FAILED!!!!");
            System.out.println("ERROR MESSAGE IS -> " + sqle);
            return 0;
        } catch (Exception e) {
            System.out.println("Error occured in searchAbstract method");
            System.out.println("ERROR MESSAGE is -> " + e);
            return 0;
        }
        return rows;

        /**tag students with the interests they searched, get student_ID, get interest_ID, 
        check for existing relationship between student_ID and interest_ID
        if none, add relationship**/
    }

    //show all abstracts
    public String showAllAbstracts() {

        String message = new String();

        try {
            stmt = conn.createStatement();
            sql = "select title, group_concat(concat(lastName, \", \", firstName) separator ' | ') from abstract "
                    + "join professor_abstract using (abstract_id) "
                    + "join professor using (professor_id)"
                    + "group by title";
            rs = stmt.executeQuery(sql);

            message = "Abstracts\n";
            message += "---------------------------------------------\n";

            //Get full set of results using pointer
            ResultSetMetaData rsmd = rs.getMetaData();

            //Figuring out the number of columns
            int col = rsmd.getColumnCount();

            //Creating array to figure out column lengths needed
            int[] lengths = new int[col];

            //Looping through to populate lengths array
            for (int currCol = 1; currCol <= col; currCol++) {
                //Set max length for certain column based on entries
                //    lengths[currCol-1] = rsmd.getColumnDisplaySize(currCol) + 5;
                lengths[currCol - 1] = rsmd.getColumnDisplaySize(currCol);

            }

            while (rs.next()) {
                // Retrieve resultset data to put in string and
                // return to the Presentation layer
                String title = rs.getString(1);
                String author = rs.getString(2);

                message += title;
                message += " by ";
                message += author;
                message += "\n";

            }//end of while loop

        } catch (SQLException sqle) {
            System.out.println("SQL ERROR");
            System.out.println("SHOW ALL ABSTRACTS FAILED!!!!");
            System.out.println("ERROR MESSAGE IS -> " + sqle);
        } catch (Exception e) {
            System.out.println("Error occured in showAllAbstracts method");
            System.out.println("ERROR MESSAGE is -> " + e);
        }
        return message;
    }

    //search student interests for key words
    public int searchInterests(String uname, String inter1, String inter2, String inter3) {                          //INTEREST SEARCH STARTS HERE

        int rows = 0;
        PreparedStatement stmt;

        String inters = "";
        int checks = 0;

        try {

            //Check if first interest is blank and if in database AND tagged to student, puts in database and/or tags if needed
            if (inter1 != "") {
                //add interest to search string
                inters += inter1;
                //add one to check
                checks++;

                sql = "SELECT * FROM interests WHERE interest = (?)";
                stmt = conn.prepareStatement(sql);

                stmt.setString(1, inter1);
                rs = stmt.executeQuery();

                //if not in database, add interest to database
                if (!rs.next()) {
                    sql = "INSERT INTO interests (interest) VALUES(?)";
                    stmt = conn.prepareStatement(sql);

                    stmt.setString(1, inter1);

                    rows += stmt.executeUpdate();
                }

                rs.next();

            }
            rs.next();
            //Check if second interest is blank and if in database AND tagged to student, puts in database and/or tags if needed
            if (inter2 != "") {
                //check current checks passsed and add interst to search string
                if (checks > 0) {
                    inters += "|" + inter2;
                } else {
                    inters += inter2;
                }
                //add one to check
                checks++;

                sql = "SELECT * FROM interests WHERE interest = (?)";
                stmt = conn.prepareStatement(sql);

                stmt.setString(1, inter2);
                rs = stmt.executeQuery();
                rs.next();

                //if not in database, add interest to database
                if (!rs.next()) {
                    sql = "INSERT INTO interests (interest) VALUES(?)";
                    stmt = conn.prepareStatement(sql);

                    stmt.setString(1, inter2);

                    rows += stmt.executeUpdate();
                }
                rs.next();

            }
            rs.next();
            //Check if third interest is blank and if in database AND tagged to student, puts in database and/or tags if needed
            if (inter3 != "") {
                //check current checks passsed and add interst to search string
                if (checks > 0) {
                    inters += "|" + inter3;
                } else {
                    inters += inter3;
                }
                //add one to check
                checks++;

                sql = "SELECT * FROM interests WHERE interest = (?)";
                stmt = conn.prepareStatement(sql);

                stmt.setString(1, inter3);
                rs = stmt.executeQuery();

                //if not in database, add interest to database
                if (!rs.next()) {
                    sql = "INSERT INTO interests (interest) VALUES(?)";
                    stmt = conn.prepareStatement(sql);

                    stmt.setString(1, inter3);

                    rows += stmt.executeUpdate();
                }
                rs.next();

            }

            //find students with those interests
            sql = "SELECT concat(student.lastName, ', ', student.firstName, ', ', student.email) AS 'Name' FROM student "
                    + "JOIN student_interests ON student.student_id = student_interests.student_id "
                    + "JOIN interests ON interests.interest_id = student_interests.interest_id "
                    + "WHERE interests.interest REGEXP ?";
            stmt = conn.prepareStatement(sql);

            stmt.setString(1, inters);
            rs = stmt.executeQuery();
            System.out.println();
            System.out.printf("%-80s", "Name");
            System.out.println();

            while (rs.next()) {
                String name = rs.getString(1);

                System.out.printf("%-80s", name);
                System.out.println();
            }//INTEREST SEARCH ENDS HERE

        } catch (SQLException sqle) {
            System.out.println("SQL ERROR");
            System.out.println("SEARCH/INSERT FAILED!!!!");
            System.out.println("ERROR MESSAGE IS -> " + sqle);
            return 0;
        } catch (Exception e) {
            System.out.println("Error occured in searchIntrests method");
            System.out.println("ERROR MESSAGE is -> " + e);
            return 0;
        }
        return rows;
    }

    //show just your own abstracts (or all if you're override)
    public String showYourAbstracts(String uname) {

        String message = new String();

        try {
            stmt = conn.createStatement();
            String profID = "";

            //Get professor's id based on passed username
            sql = "SELECT professorID FROM professor_account WHERE professor_userName = (?)";
            PreparedStatement stmt = conn.prepareStatement(sql);

            stmt.setString(1, uname);
            rs = stmt.executeQuery();
            rs.next();
            profID = rs.getString(1);

            sql = "select abstract_id, title, group_concat(concat(lastName, \", \", firstName) separator ' | ') from abstract "
                    + "join professor_abstract using (abstract_id) "
                    + "join professor using (professor_id) "
                    + "where professor_id = (?) "
                    + "group by title, abstract_id";
            stmt = conn.prepareStatement(sql);

            stmt.setString(1, profID);
            rs = stmt.executeQuery();

            message = "Abstracts\n";
            message += "---------------------------------------------\n";

            //Get full set of results using pointer
            ResultSetMetaData rsmd = rs.getMetaData();

            //Figuring out the number of columns
            int col = rsmd.getColumnCount();

            //Creating array to figure out column lengths needed
            int[] lengths = new int[col];

            //Looping through to populate lengths array
            for (int currCol = 1; currCol <= col; currCol++) {
                lengths[currCol - 1] = rsmd.getColumnDisplaySize(currCol);

            }

            while (rs.next()) {
                // Retrieve resultset data to put in string and return to the Presentation layer
                String title = rs.getString(1);
                String author = rs.getString(2);

                message += title;
                message += " by ";
                message += author;
                message += "\n";

            }//end of while loop

        } catch (SQLException sqle) {
            System.out.println("SQL ERROR");
            System.out.println("SHOW YOUR ABSTRACTS FAILED!!!!");
            System.out.println("ERROR MESSAGE IS -> " + sqle);
        } catch (Exception e) {
            System.out.println("Error occured in showYourAbstracts method");
            System.out.println("ERROR MESSAGE is -> " + e);
        }
        return message;
    }

    // get professor_id 
    public int getProfessorID(String username) {
        int id = -1;
        try {
            sql = "select professorID from professor_account where professor_userName = (?)";

            PreparedStatement stmt = conn.prepareStatement(sql);

            stmt.setString(1, username);
            rs = stmt.executeQuery();
            if (rs.next()) {
                id = rs.getInt(1);
            }

        } catch (SQLException sqle) {
            System.out.println("SQL ERROR");
            System.out.println("GET PROFESSORID FAILED!!!!");
            System.out.println("ERROR MESSAGE IS -> " + sqle);
        } catch (Exception e) {
            System.out.println("Error occured in getProfessorID method");
            System.out.println("ERROR MESSAGE is -> " + e);
        }

        return id;

    }

    //update abstract
    public int updateAbstractTitle(int id, String abstractTitle) {
        int rows = -1;

        try {
            sql = "UPDATE abstract set title = ? where abstract_id = ?";

            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, abstractTitle);
            stmt.setInt(2, id);

            rows = stmt.executeUpdate();

        } catch (SQLException sqle) {
            System.out.println("SQL ERROR");
            System.out.println("GET PROFESSORID FAILED!!!!");
            System.out.println("ERROR MESSAGE IS -> " + sqle);
        } catch (Exception e) {
            System.out.println("Error occured in updateAbstractTitle method");
            System.out.println("ERROR MESSAGE is -> " + e);
        }

        return rows;
    }

    public int updateAbstractBody(int id, String body) {
        int rows = -1;

        try {
            sql = "UPDATE abstract set abstract = ? where abstract_id = ?";

            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, body);
            stmt.setInt(2, id);

            rows = stmt.executeUpdate();

        } catch (SQLException sqle) {
            System.out.println("SQL ERROR");
            System.out.println("GET PROFESSORID FAILED!!!!");
            System.out.println("ERROR MESSAGE IS -> " + sqle);
        } catch (Exception e) {
            System.out.println("Error occured in updateAbstractBody method");
            System.out.println("ERROR MESSAGE is -> " + e);
        }

        return rows;
    }

    //delete abstract
    public int deleteAbstract(int id) {
        int rows = -1;

        try {
            sql = "DELETE FROM abstract where abstract_id = (?)";

            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, id);

            rows = stmt.executeUpdate();

        } catch (SQLException sqle) {
            System.out.println("SQL ERROR");
            System.out.println("GET PROFESSORID FAILED!!!!");
            System.out.println("ERROR MESSAGE IS -> " + sqle);
        } catch (Exception e) {
            System.out.println("Error occured in getProfessorID method");
            System.out.println("ERROR MESSAGE is -> " + e);
        }

        return rows;
    }

    //verify user can change that abstract
    public boolean verifyPermission(int profID, int abstractID) {
        boolean verdict = false;
        try {
            //see if abstract_ID matches professor_ID
            sql = "SELECT professor_abstract.abstract_ID FROM professor_abstract "
                    + "JOIN professor_account ON professor_abstract.professor_id = professor_account.professorID "
                    + "WHERE professor_abstract.professor_id = ? AND professor_abstract.abstract_id = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);

            stmt.setInt(1, profID);
            stmt.setInt(2, abstractID);
            rs = stmt.executeQuery();

            if (rs.next()) {
                verdict = true;
            }
        } catch (SQLException sqle) {
            System.out.println("SQL ERROR");
            System.out.println("VERIFY PERMISSION FAILED!!!!");
            System.out.println("ERROR MESSAGE IS -> " + sqle);
        } catch (Exception e) {
            System.out.println("Error occured in verifyPermission method");
            System.out.println("ERROR MESSAGE is -> " + e);
        }

        return verdict;
    }

    //check yes no responses
    public boolean checkYesNo() {
        boolean entered = false;
        boolean ans = false;

        while (!entered) {
            String yn = GetInput.readLine();
            switch (yn.toLowerCase()) {
                case "y":
                    entered = true;
                    ans = true;
                    break;
                case "n":
                    entered = true;
                    ans = false;
                    break;
                default:
                    System.out.print("Enter y or n: ");
            }
        }
        return ans;
    }
}
