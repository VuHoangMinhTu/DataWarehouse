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
-- Table structure for table `dm_filter_cameras`
--

DROP TABLE IF EXISTS `dm_filter_cameras`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
USE `dm`;

CREATE TABLE `dm_filter_cameras` (
   `sk` bigint NOT NULL AUTO_INCREMENT,
  `id` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `name` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL,
  `brand` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL,
  `actual_price` double DEFAULT NULL,
  `sale_price` double DEFAULT NULL,
  `images` mediumtext,
   `video_url_description` mediumtext CHARACTER SET utf8 COLLATE utf8_general_ci,
  `date_sk` bigint NOT NULL,
  PRIMARY KEY (`sk`),
  KEY `date_sk` (`date_sk`),
  CONSTRAINT `dm_filter_cameras_ibfk_1` FOREIGN KEY (`date_sk`) REFERENCES `date_dim` (`sk`)
  ) ENGINE=InnoDB AUTO_INCREMENT=64 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `dm_filter_cameras`
--

LOCK TABLES `dm_filter_cameras` WRITE;
/*!40000 ALTER TABLE `dm_filter_cameras` DISABLE KEYS */;
/*!40000 ALTER TABLE `dm_filter_cameras` ENABLE KEYS */;
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

DELIMITER //

CREATE PROCEDURE LoadDmFilterCameras()
BEGIN
    -- Xóa dữ liệu cũ trong dm_filter_cameras nếu cần
    DELETE FROM dm_filter_cameras WHERE sk > 0;

    -- Chèn dữ liệu từ aggregate_filter_cameras vào dm_filter_cameras
    INSERT INTO dm_filter_cameras (
        id,
        name,
        brand,
        actual_price,
        sale_price,
        images,
        video_url_description,
        date_sk
    )
    SELECT 
        id,
        name,
        brand,
        actual_price,
        sale_price,
        images,
        video_url_description,
        date_sk
    FROM dw.aggregate_filter_cameras;

    -- Xác nhận thay đổi
    COMMIT;
END //

DELIMITER ;


