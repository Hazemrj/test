DROP DATABASE IF EXISTS `group1`;
CREATE DATABASE  IF NOT EXISTS `group1` /*!40100 DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci */ /*!80016 DEFAULT ENCRYPTION='N' */;
USE `group1`;
-- MySQL dump 10.13  Distrib 8.0.26, for Win64 (x86_64)
--
-- Host: localhost    Database: group1
-- ------------------------------------------------------
-- Server version	8.0.26

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `abstract`
--

DROP TABLE IF EXISTS `abstract`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `abstract` (
  `abstract_id` int NOT NULL AUTO_INCREMENT,
  `title` varchar(100) NOT NULL,
  `abstract` varchar(800) NOT NULL,
  PRIMARY KEY (`abstract_id`),
  KEY `abstract_id_idx` (`abstract_id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `abstract`
--

LOCK TABLES `abstract` WRITE;
/*!40000 ALTER TABLE `abstract` DISABLE KEYS */;
INSERT INTO `abstract` VALUES (1,'Learn C and C++ by Samples','This book, Learn C and C++ by Samples written by James R. Habermas, is a companion to A First Book Ansi C++ by Gary Bronson. It is the author’s firm belief that one can never have too many samples. If a textbook is to be useful, it needs primary support through an instructor and/or more samples. This textbook contains a wealth of useful C & C++ samples that are fashioned to further demonstrate the topics outlined in the text.'),(2,'C through Design','This book presents ‘standard’ C, i.e., code that compiles cleanly with a compiler that meets the ANSI C standard. This book has over 90 example programs that illustrate the topics of each chapters. In addition complete working programs are developed fully, from design to program output. This book is filled with Antibugging Notes (the stress traps to be avoided), and Quick Notes, that emphasize important points to be remembered. '),(3,'Introduction to Computing and Programming in PYTHON : A Multimedia Approach','The programming language used in this book is Python. Python has been described as “executable pseudo-code.” I have found that both computer science majors and non majors can learn Python. Since Python is actually used for communications tasks (e.g., Web site Development), it’s relevant language for an in introductory computing course. The specific dialect of Python used in this book is Jython. Jython is Python. The differences between Python (normally implemented in C) and Jython (which is implemented in Java) are akin to the differences between any two language implementations (e.g., Microsoft vs. GNU C++ implementations). ');
/*!40000 ALTER TABLE `abstract` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `professor_abstract`
--

DROP TABLE IF EXISTS `professor_abstract`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `professor_abstract` (
  `professor_id` int NOT NULL,
  `abstract_id` int NOT NULL,
  PRIMARY KEY (`abstract_id`,`professor_id`),
  CONSTRAINT `abstract_id` FOREIGN KEY (`abstract_id`) REFERENCES `abstract` (`abstract_id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `professor_id` FOREIGN KEY (`professor_id`) REFERENCES `professor` (`professor_id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `professor_abstract`
--

LOCK TABLES `professor_abstract` WRITE;
/*!40000 ALTER TABLE `professor_abstract` DISABLE KEYS */;
INSERT INTO `professor_abstract` VALUES (1,1),(2,2),(3,2),(4,3);
/*!40000 ALTER TABLE `professor_abstract` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `interests`
--

DROP TABLE IF EXISTS `interests`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `interests` (
  `interest_id` int NOT NULL AUTO_INCREMENT,
  `interest` varchar(20) NOT NULL,
  PRIMARY KEY (`interest_id`)
) ENGINE=InnoDB AUTO_INCREMENT=10 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `interests`
--

LOCK TABLES `interests` WRITE;
/*!40000 ALTER TABLE `interests` DISABLE KEYS */;
INSERT INTO `interests` VALUES (1, 'Python');
INSERT INTO `interests` VALUES (2, 'SQL');
INSERT INTO `interests` VALUES (3, 'Java');
INSERT INTO `interests` VALUES (4, 'Linux');
INSERT INTO `interests` VALUES (5, 'HTML');
INSERT INTO `interests` VALUES (6, 'C');
INSERT INTO `interests` VALUES (7, 'C++');
INSERT INTO `interests` VALUES (8, 'Assembly');
INSERT INTO `interests` VALUES (9, 'Java Script');
/*!40000 ALTER TABLE `interests` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `professor`
--

DROP TABLE IF EXISTS `professor`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `professor` (
  `professor_id` int NOT NULL AUTO_INCREMENT,
  `firstName` varchar(45) NOT NULL,
  `lastName` varchar(45) NOT NULL,
  `buildingNum` int NOT NULL,
  `officeNum` int NOT NULL,
  `email` varchar(45) NOT NULL,
  PRIMARY KEY (`professor_id`),
  KEY `professor_id_idx` (`professor_id`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `professor`
--

LOCK TABLES `professor` WRITE;
/*!40000 ALTER TABLE `professor` DISABLE KEYS */;
INSERT INTO `professor` VALUES (1,'James','Habermas',2673,70,'jim.habermas@rit.edu'),(2,'George','Defenbaugh',2673,50,'gdefenbaugh@rit.edu'),(3,'‎Richard','Smedley',2673,75,'rsmedley@rit.edu'),(4,'Barbara','Ericson',2673,90,'bericson@rit.edu');
/*!40000 ALTER TABLE `professor` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `professor_account`
--

DROP TABLE IF EXISTS `professor_account`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `professor_account` (
  `professorID` int NOT NULL,
  `professor_userName` varchar(45) NOT NULL,
  `password` varchar(150) NOT NULL,
  PRIMARY KEY (`professorID`, `professor_userName`),
  CONSTRAINT `professorID` FOREIGN KEY (`professorID`) REFERENCES `professor` (`professor_id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `professor_account`
--

LOCK TABLES `professor_account` WRITE;
/*!40000 ALTER TABLE `professor_account` DISABLE KEYS */;
INSERT INTO `professor_account` VALUES (1,'jhabermas1','f3753089c2d408f362dc5808b7f73742612be2b1'),(2,'gdefenbaugh2','f3753089c2d408f362dc5808b7f73742612be2b1'),(3,'‎smedley3','f3753089c2d408f362dc5808b7f73742612be2b1'),(4,'bericson4','f3753089c2d408f362dc5808b7f73742612be2b1');
/*!40000 ALTER TABLE `professor_account` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `student`
--

DROP TABLE IF EXISTS `student`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `student` (
  `student_id` int NOT NULL AUTO_INCREMENT,
  `firstName` varchar(45) NOT NULL,
  `lastName` varchar(45) NOT NULL,
  `email` varchar(45) NOT NULL,
  PRIMARY KEY (`student_id`)
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `student`
--

LOCK TABLES `student` WRITE;
/*!40000 ALTER TABLE `student` DISABLE KEYS */;
INSERT INTO `student` VALUES (1,'Lewis','Atkinson', 'lja5415@g.rit.edu');
INSERT INTO `student` VALUES (2,'Doug','Bailey', 'djb1892@g.rit.edu');
INSERT INTO `student` VALUES (3,'Kanisha','Agrawal', 'ka5679@g.rit.edu');
INSERT INTO `student` VALUES (4,'Hazim','Al Raijal', 'ha9396@g.rit.edu');
INSERT INTO `student` VALUES (5,'Edilia','Bueno', 'emb9544@g.rit.edu');
INSERT INTO `student` VALUES (6,'Saquib','Ahmed', 'sa4825@g.rit.edu');
/*!40000 ALTER TABLE `student` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `student_account`
--

DROP TABLE IF EXISTS `student_account`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `student_account` (
  `studentID` int NOT NULL,
  `student_userName` varchar(45) NOT NULL,
  PRIMARY KEY (`studentID`, `student_userName`),
  CONSTRAINT `studentID` FOREIGN KEY (`studentID`) REFERENCES `student` (`student_id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `student_account`
--

LOCK TABLES `student_account` WRITE;
/*!40000 ALTER TABLE `student_account` DISABLE KEYS */;
INSERT INTO `student_account` VALUES (1,'latkinson1');
INSERT INTO `student_account` VALUES (2,'dbailey2');
INSERT INTO `student_account` VALUES (3,'kagrawal3');
INSERT INTO `student_account` VALUES (4,'halraijal4');
INSERT INTO `student_account` VALUES (5,'ebueno5');
INSERT INTO `student_account` VALUES (6,'sahmed6');
/*!40000 ALTER TABLE `student_account` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `student_interests`
--

DROP TABLE IF EXISTS `student_interests`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `student_interests` (
  `student_id` int NOT NULL,
  `interest_id` int NOT NULL,
  PRIMARY KEY (`student_id`,`interest_id`),
  KEY `interest_id_idx` (`interest_id`),
  CONSTRAINT `interest_id` FOREIGN KEY (`interest_id`) REFERENCES `interests` (`interest_id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `student_id` FOREIGN KEY (`student_id`) REFERENCES `student` (`student_id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=13 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `student_interests`
--

LOCK TABLES `student_interests` WRITE;
/*!40000 ALTER TABLE `student_interests` DISABLE KEYS */;
INSERT INTO `student_interests` VALUES (1, 1);
INSERT INTO `student_interests` VALUES (1, 3);
INSERT INTO `student_interests` VALUES (1, 5);
INSERT INTO `student_interests` VALUES (1, 8);
INSERT INTO `student_interests` VALUES (2, 2);
INSERT INTO `student_interests` VALUES (2, 4);
INSERT INTO `student_interests` VALUES (3, 5);
INSERT INTO `student_interests` VALUES (3, 9);
INSERT INTO `student_interests` VALUES (4, 8);
INSERT INTO `student_interests` VALUES (5, 6);
INSERT INTO `student_interests` VALUES (6, 7);
INSERT INTO `student_interests` VALUES (6, 2);
/*!40000 ALTER TABLE `student_interests` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `professor_keyword`
--

DROP TABLE IF EXISTS `professor_keyword`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `professor_keyword` (
  `professorkey_id` int NOT NULL,
  `keyword_id` int NOT NULL,
  PRIMARY KEY (`keyword_id`,`professorkey_id`),
  CONSTRAINT `keyword_id` FOREIGN KEY (`keyword_id`) REFERENCES `keyword` (`keyword_id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `professorkey_id` FOREIGN KEY (`professorkey_id`) REFERENCES `professor` (`professor_id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `professor_keyword`
--

LOCK TABLES `professor_keyword` WRITE;
/*!40000 ALTER TABLE `professor_keyword` DISABLE KEYS */;
INSERT INTO `professor_keyword` VALUES (1, 2);
INSERT INTO `professor_keyword` VALUES (1, 3);
INSERT INTO `professor_keyword` VALUES (1, 5);
INSERT INTO `professor_keyword` VALUES (2, 1);
INSERT INTO `professor_keyword` VALUES (2, 4);
INSERT INTO `professor_keyword` VALUES (2, 7);
INSERT INTO `professor_keyword` VALUES (3, 9);
INSERT INTO `professor_keyword` VALUES (3, 8);
INSERT INTO `professor_keyword` VALUES (3, 7);
INSERT INTO `professor_keyword` VALUES (4, 2);
INSERT INTO `professor_keyword` VALUES (4, 5);
INSERT INTO `professor_keyword` VALUES (4, 8);
/*!40000 ALTER TABLE `professor_keyword` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `keyword`
--

DROP TABLE IF EXISTS `keyword`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `keyword` (
  `keyword_id` int NOT NULL AUTO_INCREMENT,
  `keyword` varchar(25) NOT NULL,
  PRIMARY KEY (`keyword_id`),
  KEY `keyword_id_idx` (`keyword_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `keyword`
--

LOCK TABLES `keyword` WRITE;
/*!40000 ALTER TABLE `keyword` DISABLE KEYS */;
INSERT INTO `keyword` VALUES (1, 'Python');
INSERT INTO `keyword` VALUES (2, 'SQL');
INSERT INTO `keyword` VALUES (3, 'Java');
INSERT INTO `keyword` VALUES (4, 'Linux');
INSERT INTO `keyword` VALUES (5, 'HTML');
INSERT INTO `keyword` VALUES (6, 'C');
INSERT INTO `keyword` VALUES (7, 'C++');
INSERT INTO `keyword` VALUES (8, 'Assembly');
INSERT INTO `keyword` VALUES (9, 'Java Script');
/*!40000 ALTER TABLE `keyword` ENABLE KEYS */;
UNLOCK TABLES;



/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2021-11-15 17:55:31
