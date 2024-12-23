CREATE DATABASE  IF NOT EXISTS `dm` /*!40100 DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci */ /*!80016 DEFAULT ENCRYPTION='N' */;
USE `dm`;
-- MySQL dump 10.13  Distrib 8.0.36, for Linux (x86_64)
--
-- Host: localhost    Database: data-mart
-- ------------------------------------------------------
-- Server version	8.0.40-0ubuntu0.24.04.1

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
-- Table structure for table `dm_price_change_cameras`
--

DROP TABLE IF EXISTS `price_change_cameras`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `price_change_cameras` (
  `sk` bigint NOT NULL AUTO_INCREMENT,
  `id` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL,
  `name` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci DEFAULT NULL,
  `actual_price` double DEFAULT NULL,
  `sale_price` double DEFAULT NULL,
--   `resource_id` bigint DEFAULT NULL,
--   `resource_name` bigint DEFAULT NULL,
  `change_date` date DEFAULT NULL,
  `date_sk` bigint NOT NULL,
  PRIMARY KEY (`sk`),
  KEY `date_sk` (`date_sk`),
  CONSTRAINT `price_change_cameras_ibfk_1` FOREIGN KEY (`date_sk`) REFERENCES `date_dim` (`sk`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `dm_price_change_cameras`
--

LOCK TABLES `price_change_cameras` WRITE;
/*!40000 ALTER TABLE `price_change_cameras` DISABLE KEYS */;
/*!40000 ALTER TABLE `price_change_cameras` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2024-12-03 11:07:56
-- INSERT INTO `dm_price_change_cameras` 
-- (`id`, `name`, `actual_price`, `sale_price`, `change_date`, `date_sk`)
-- SELECT 
--     a.id,
--     a.name,
--     a.regular_price AS actual_price,
--     a.discounted_price AS sale_price,
--     a.change_date,
--     d.sk AS date_sk
-- FROM `dw`.`aggregate_price_change_cameras` a
-- JOIN `date_dim` d
-- ON a.change_date = d.date;


DELIMITER $$

CREATE PROCEDURE loadDataFromDWtoDataMartChangePrice()
BEGIN

    INSERT INTO date_dim (`date`, `day`, `month`, `year`, `week`)
    SELECT DISTINCT 
        a.change_date AS `date`,
        DAY(a.change_date) AS `day`,
        MONTH(a.change_date) AS `month`,
        YEAR(a.change_date) AS `year`,
        WEEK(a.change_date, 1) AS `week`
    FROM dw.aggregate_price_change_cameras a
    LEFT JOIN dw.date_dim d
        ON a.change_date = d.date
    WHERE d.date IS NULL;

  
    INSERT INTO price_change_cameras 
        (id, name, actual_price, sale_price, change_date, date_sk)
    SELECT 
        a.id,
        a.name,
        a.actual_price AS actual_price,
        a.actual_price AS sale_price,
        a.change_date,
        d.sk AS date_sk
    FROM dw.aggregate_price_change_cameras a
    JOIN dw.date_dim d
        ON a.change_date = d.date
    WHERE NOT EXISTS (
        SELECT 1
        FROM price_change_cameras dm
        WHERE dm.id = a.id
          AND dm.change_date = a.change_date
    );

END$$

DELIMITER ;

