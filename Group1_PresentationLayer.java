//Group 1
//Presentation Layer
//Due 4/28/2023

import java.util.*;
import java.util.Scanner;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;

public class Group1_PresentationLayer {

    Group1_DataLayer mySql = new Group1_DataLayer();
    JFileChooser chooser = new JFileChooser();
    FileNameExtensionFilter filter = new FileNameExtensionFilter(".txt", "txt");

    public Group1_PresentationLayer() {
        Scanner in = new Scanner(System.in);
        boolean student = false;
        boolean faculty = false;
        boolean guest = false;
        String username = "";

        System.out.println("Connecting to the database group1...");
        mySql.connect();
        System.out.println("You have connected to the database!\n");

        //loop until account is selected
        while (true) {
            //select login or guest
            System.out.println("Select the login for your account (Faculty/Student) or guest account (no login):");
            System.out.println("1. Faculty");
            System.out.println("2. Student");
            System.out.println("3. Guest");
            System.out.println("4. Exit");
            System.out.println("5. Debug/Maintenance");
            System.out.print("Selected: ");
            int selected = GetInput.readInt();

            switch (selected) {
                case 1: //faculty selected (login faculty)
                    boolean loop = true;
                    faculty = false;
                    while (!faculty && loop) {
                        //get username and password
                        System.out.print("\nEnter username: ");
                        username = GetInput.readWord();
                        System.out.print("Enter password: ");
                        String password = GetInput.readWord();
                        faculty = mySql.facultyLogin(username, password);

                        //if login returns false
                        if (!faculty) {
                            System.out.println("Incorect username or password");
                            System.out.println("1. Try again");
                            System.out.println("2. Return");
                            System.out.print("Selected: ");
                            switch (selected) {
                                case 1: //try again
                                    break;

                                case 2: //return
                                    loop = false;
                            }
                        }
                        if (faculty) {
                           loop = true;
                           while (loop) {
                               System.out.println("\nSelect an option below");
                               System.out.println("1. Add an abstract");
                               System.out.println("2. Show all abstracts");
                               System.out.println("3. Update one of your abstracts");
                               System.out.println("4. Delete one of your abstracts");
                               System.out.println("5. Change password");
                               System.out.println("6. Add a New Keyword");
                               System.out.println("7. Remove a Keyword");
                               System.out.println("8. Log out");
                               System.out.println("9. Exit");
                               System.out.print("Selected: ");
                               selected = GetInput.readInt();
               
                               switch (selected) {
                                   case 1: //add abstract
                                       chooser.setFileFilter(filter);
                                       int returnVal = chooser.showOpenDialog(null);
                                       if (returnVal == JFileChooser.APPROVE_OPTION) {
                                           int rows = -1;
                                           String fileName = chooser.getSelectedFile().getName();
                                           if (fileName.substring(fileName.lastIndexOf("."), fileName.length()).equals(".txt")) {
                                               rows = mySql.readInTxt(chooser.getSelectedFile().toString());
                                           } else if (fileName.substring(fileName.lastIndexOf("."), fileName.length()).equals(".docx")) {
                                               rows = mySql.readInDocx(chooser.getSelectedFile().toString());
                                           }
                                           System.out.println("Rows affected --> " + rows);
                                       }
                                       break;
               
                                   case 2: //show all abstracts
                                       String abstracts_faculty = new String();
                                       abstracts_faculty = mySql.showAllAbstracts();
                                       System.out.flush();
                                       System.out.println(abstracts_faculty);
                                       break;
               
                                   case 3: //update abstract
                                       System.out.println("Showing your abstracts:  ");
                                       String abstracts_yours = new String();
               
                                       if (username.equals("AdminTest")) {
                                           abstracts_yours = mySql.showAllAbstracts();
               
                                           System.out.flush();
                                           System.out.println(abstracts_yours);
                                       } else {
               
                                           abstracts_yours = mySql.showYourAbstracts(username);
               
                                           System.out.flush();
                                           System.out.println(abstracts_yours);
                                       }
               
                                       System.out.println("\nPlease enter the ID of the abstract you would like to update.  ");
                                       int choice = GetInput.readInt();
               
                                       if (!username.equals("AdminTest")) {
                                           boolean proceed = false;
                                           int profID = mySql.getProfessorID(username);
                                           proceed = mySql.verifyPermission(profID, choice);
               
                                           if (!proceed) {
                                               System.out.println("You don't have permissions to edit/delete that abstract.");
                                               break;
                                           }
                                       }
               
                                       System.out.println("Would you like to update the abstract title? (Y/N)");
               
                                       if (mySql.checkYesNo()) {
                                           System.out.println("Please enter the new title: ");
                                           String title = GetInput.readLine();
                                           int rows = mySql.updateAbstractTitle(choice, title);
                                           System.out.println("Rows affected --> " + rows);
                                       }
               
                                       System.out.println("Would you like to update the abstract body? (Y/N)");
               
                                       if (mySql.checkYesNo()) {
                                           System.out.println("Please enter the new body: ");
                                           String body = GetInput.readLine();
                                           int rows = mySql.updateAbstractBody(choice, body);
                                           System.out.println("Rows affected --> " + rows);
                                       }
               
                                       break;
               
                                   case 4: //delete abstract
                                       System.out.println("Showing your abstracts:  ");
                                       abstracts_yours = new String();
               
                                       if (username.equals("AdminTest")) {
                                           abstracts_yours = mySql.showAllAbstracts();
               
                                           System.out.flush();
                                           System.out.println(abstracts_yours);
                                       } else {
               
                                           abstracts_yours = mySql.showYourAbstracts(username);
               
                                           System.out.flush();
                                           System.out.println(abstracts_yours);
                                       }
               
                                       System.out.println("\nPlease enter the ID of the abstract you would like to delete.  ");
                                       choice = GetInput.readInt();
               
                                       if (!username.equals("AdminTest")) {
                                           boolean proceed = false;
                                           int profID = mySql.getProfessorID(username);
                                           proceed = mySql.verifyPermission(profID, choice);
               
                                           if (!proceed) {
                                               System.out.println("You don't have permissions to edit/delete that abstract.");
                                               break;
                                           }
                                       }
               
                                       int rows = 0;
                                       rows = mySql.deleteAbstract(choice);
               
                                       System.out.println("Rows affected --> " + rows);
               
                                       break;
               
                                   case 5: //change password
                                       System.out.print("\nEnter current password: ");
                                       String curPass = GetInput.readWord();
                                       System.out.print("Enter new password: ");
                                       String newPass = GetInput.readWord();
                                       System.out.println("Rows affected --> " + mySql.changePass(username, curPass, newPass));
                                       break;
               
                                   case 6: //Add Keyword
                                       mySql.showKeywords(username);
                                       System.out.print("(These are YOUR CURRENT keywords)\nEnter keyword you'd like to add: ");
                                       String newKey = GetInput.readWord();
                                       System.out.println("Rows affected --> " + mySql.addFacultyKeyword(username, newKey));
                                       break;
                                       
                                   case 7: //Delete Keyword
                                       mySql.showKeywords(username);
                                       System.out.print("(These are YOUR CURRENT keywords)\nEnter keyword you'd like to remove: ");
                                       String oldKey = GetInput.readWord();
                                       System.out.println("Rows affected --> " + mySql.removeProfKeyword(username, oldKey));
                                       break;
                                       
                                   case 8: //log out
                                       username = "";
                                       loop = false;
                                       break;
               
                                   case 9: //Exit
                                       java.util.Date today = new java.util.Date();
                                       System.out.println("\nProgram terminated @ " + today + "\n");
                                       System.exit(0);
                                       break;
               
                                   default: //nothing selected
                                       break;
                               }
               
                           }
                       }
                    }
                    break;

                case 2: //student selected (login student)
                    loop = true;
                    student = false;
                    while (!student && loop) {
                        //get username
                        System.out.print("\nEnter username: ");
                        username = GetInput.readWord();
                        student = mySql.studentLogin(username);
                        //if login returns false
                        if (!student) {
                            System.out.println("Incorect username");
                            System.out.println("1. Try again");
                            System.out.println("2. Return");
                            System.out.print("Selected: ");
                            selected = GetInput.readInt();

                            switch (selected) {
                                case 1: //try again
                                    break;

                                case 2: //return
                                    loop = false;
                                    break;
                            }
                        }
                        if (student) {
                           loop = true;
                           while (loop) {
                               System.out.println("\nSelect an option below");
                               System.out.println("1. Search for abstracts with inputted words");
                               System.out.println("2. Show all abstracts");
                               System.out.println("3. Search for Professors based on inputted keywords");
                               System.out.println("4. Add Interest");
                               System.out.println("5. Delete Interest");
                               System.out.println("6. Log out");
                               System.out.println("7. Exit");
                               System.out.print("Selected: ");
                               selected = GetInput.readInt();
               
                               switch (selected) {
                                   case 1: //search key words
                                       //System.out.println("Place holder");
                                       String keywords = "";
               
                                       //Get input all in one string and trim it
                                       System.out.println("\nPlease enter 1 - 3 key words separated by commas.");
                                       keywords = GetInput.readLine();
                                       keywords = keywords.trim();
               
                                       String sendingArr[] = {"", "", ""};
               
                                       String[] keyArr = keywords.split(",", 3);
               
                                       switch (keyArr.length) {
                                           case 0:
                                               //nothing
                                               break;
               
                                           case 1:
                                               System.arraycopy(keyArr, 0, sendingArr, 0, 1);
                                               break;
               
                                           case 2:
                                               System.arraycopy(keyArr, 0, sendingArr, 0, 2);
                                               break;
               
                                           case 3:
                                               System.arraycopy(keyArr, 0, sendingArr, 0, 3);
                                               break;
                                       }
                                       //Call abstract search method
                                       System.out.println("Rows affected --> " + mySql.searchAbstract(username, sendingArr[0], sendingArr[1], sendingArr[2]));
                                       break;
               
                                   case 2: //show all abstracts
                                       String abstracts_student = new String();
                                       abstracts_student = mySql.showAllAbstracts();
                                       System.out.println(abstracts_student);
                                       break;
                                       
                                   case 3: //Search prof based on keywords
                                       System.out.println("\nPlease enter 1 - 3 key words separated by commas.");
                                       keywords = GetInput.readLine();
                                       keywords = keywords.trim();
                                       String[] key_Arr = keywords.split(",", 3);
                                       mySql.searchProfKey(key_Arr);
                                       break;
                                   case 4: //add interest
                                       mySql.showInterests(username);
                                       System.out.println("(These are YOUR CURRENT interests)\nPlease enter a different interest you'd like to add: ");
                                       String interest = GetInput.readWord();
                                       System.out.println("Rows affected --> " + mySql.addStudentInterest(username, interest));
                                       break;
                                       
                                   case 5: //delete interest
                                       mySql.showInterests(username);
                                       System.out.println("(These are YOUR CURRENT interests)\nPlease enter the interest you'd like to delete: ");
                                       interest = GetInput.readWord();
                                       System.out.println("Rows affected --> " + mySql.removeStuInterest(username, interest));
                                       break;
                                       
                                   case 6: //log out
                                       loop = false;
                                       break;
               
                                   case 7: //exit
                                       java.util.Date today = new java.util.Date();
                                       System.out.println("\nProgram terminated @ " + today + "\n");
                                       System.exit(0);
                                       break;
               
                                   default: //nothing selected
                                       break;
                               }
                           }
                       }
                    }
                    break;
                case 3: //guest
                     loop = true;
                     while (loop) {
                         System.out.println("\nSelect an option below");
                         System.out.println("1. Search students interests for key words");
                         System.out.println("2. Show all abstracts");
                         System.out.println("3. Log out");
                         System.out.println("4. Exit");
                         System.out.print("Selected: ");
                         selected = GetInput.readInt();
         
                         switch (selected) {
                             case 1: //search key words
                                 String keywords = "";
         
                                 //Get input all in one string and trim it
                                 System.out.println("\nPlease enter 1 - 3 key words separated by commas.");
                                 keywords = GetInput.readLine();
                                 keywords = keywords.trim();
         
                                 String sendingArr[] = {"", "", ""};
         
                                 String[] keyArr = keywords.split(",", 3);
         
                                 switch (keyArr.length) {
                                     case 0:
                                         //nothing
                                         break;
         
                                     case 1:
                                         System.arraycopy(keyArr, 0, sendingArr, 0, 1);
                                         break;
         
                                     case 2:
                                         System.arraycopy(keyArr, 0, sendingArr, 0, 2);
                                         break;
         
                                     case 3:
                                         System.arraycopy(keyArr, 0, sendingArr, 0, 3);
                                         break;
                                 }
                                 //Call interest search method
                                 System.out.println("Rows affected --> " + mySql.searchInterests(username, sendingArr[0], sendingArr[1], sendingArr[2]));
                                 break;
         
                             case 2: //show all abstracts
                                 String abstracts_guest = new String();
                                 abstracts_guest = mySql.showAllAbstracts();
                                 System.out.flush();
                                 System.out.println(abstracts_guest);
                                 break;
         
                             case 3: //log out
                                 loop = false;
                                 break;
         
                             case 4: //exit
                                 java.util.Date today = new java.util.Date();
                                 System.out.println("\nProgram terminated @ " + today + "\n");
                                 System.exit(0);
                                 break;
         
                             default: //nothing selected
                                 break;
                         }
                      }
                    break;
                case 4: //exit
                    java.util.Date today = new java.util.Date();
                    System.out.println("\nProgram terminated @ " + today + "\n");
                    System.exit(0);
                    break;

                case 5: //debug/maintenance
                    loop = true;
                    while (loop) {
                        //get username and password
                        System.out.print("\nEnter username: ");
                        username = GetInput.readWord();
                        System.out.print("Enter password: ");
                        String password = GetInput.readWord();
                        faculty = mySql.facultyLogin(username, password);

                        //if login returns false
                        if (!faculty) {
                            System.out.println("Incorect username or password");
                            System.out.println("1. Try again");
                            System.out.println("2. Return");
                            System.out.print("Selected: ");
                            selected = GetInput.readInt();

                            switch (selected) {
                                case 1: //try again
                                    break;

                                case 2: //return
                                    loop = false;
                                    break;

                                default: //nothing selected
                                    break;
                            }
                        } else { //add and remove students from database(maintenance)
                            while (loop) {
                                System.out.println("\nSelect an option below");
                                System.out.println("1. Add student");
                                System.out.println("2. Delete student");
                                System.out.println("3. Add faculty");
                                System.out.println("4. Delete faculty");
                                System.out.println("5. Log out");
                                System.out.println("6. Exit");
                                System.out.print("Selected: ");
                                selected = GetInput.readInt();

                                switch (selected) {
                                    case 1: //add student
                                        System.out.print("Enter student's first name: ");
                                        String fName = GetInput.readWord();
                                        System.out.print("Enter student's last name: ");
                                        String lName = GetInput.readWord();
                                        System.out.print("Enter student's email: ");
                                        String s_email = GetInput.readWord();
                                        System.out.println("Rows affected --> "
                                                + mySql.addStudent(fName, lName, s_email));
                                        break;

                                    case 2: //delete student
                                        System.out.print("Enter students username: ");
                                        username = GetInput.readWord();
                                        System.out.println("Rows affected --> "
                                                + mySql.deleteStudent(username));
                                        break;

                                    case 3: //add faculty
                                        System.out.print("Enter first name: ");
                                        fName = GetInput.readWord();
                                        System.out.print("Enter last name: ");
                                        lName = GetInput.readWord();
                                        System.out.print("Enter building number: ");
                                        int building = GetInput.readInt();
                                        System.out.print("Enter office number: ");
                                        int office = GetInput.readInt();
                                        System.out.print("Enter email: ");
                                        String email = GetInput.readWord();
                                        System.out.println("Rows affected --> "
                                                + mySql.addFaculty(fName, lName, building, office, email));
                                        break;

                                    case 4: //delete faculty
                                        System.out.print("\nEnter username of account to delete: ");
                                        username = GetInput.readWord();
                                        System.out.println("Rows affected --> " + mySql.deleteFaculty(username));
                                        break;

                                    case 5: //log out
                                        username = "";
                                        loop = false;
                                        break;

                                    case 6: //exit
                                        today = new java.util.Date();
                                        System.out.println("\nProgram terminated @ " + today + "\n");
                                        System.exit(0);
                                        break;

                                    default: //nothing selected
                                        break;
                                }
                            }
                        }
                    }
                    break;

                default: //nothing selected
                    break;
            }
        }
    }
    public static void main(String[] args) {
        new Group1_PresentationLayer();
    }
}
