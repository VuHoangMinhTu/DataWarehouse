-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Máy chủ: 127.0.0.1
-- Thời gian đã tạo: Th12 11, 2024 lúc 07:58 AM
-- Phiên bản máy phục vụ: 10.4.32-MariaDB
-- Phiên bản PHP: 8.2.12

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Cơ sở dữ liệu: `control`
--
CREATE DATABASE IF NOT EXISTS `control` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
USE `control`;

-- --------------------------------------------------------

--
-- Cấu trúc bảng cho bảng `configs`
--

DROP TABLE IF EXISTS `configs`;
CREATE TABLE `configs` (
  `id` bigint(20) NOT NULL,
  `source_file_location` varchar(255) DEFAULT NULL,
  `format` varchar(255) DEFAULT NULL,
  `columns_count` int(11) DEFAULT NULL,
  `column_names` mediumtext DEFAULT NULL,
  `dest_temp_table_staging` varchar(255) DEFAULT NULL,
  `dest_trans_table_staging` varchar(255) DEFAULT NULL,
  `dest_table_dw` varchar(255) DEFAULT NULL,
  `flag` bit(1) DEFAULT NULL,
  `seperator` varchar(255) DEFAULT NULL,
  `created_at` datetime DEFAULT NULL,
  `created_by` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL,
  `updated_at` datetime DEFAULT NULL,
  `updated_by` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL,
  `fields_terminated_by` varchar(10) DEFAULT NULL,
  `optionally_enclosed_by` varchar(10) DEFAULT NULL,
  `lines_terminated_by` varchar(10) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Đang đổ dữ liệu cho bảng `configs`
--

INSERT INTO `configs` (`id`, `source_file_location`, `format`, `columns_count`, `column_names`, `dest_temp_table_staging`, `dest_trans_table_staging`, `dest_table_dw`, `flag`, `seperator`, `created_at`, `created_by`, `updated_at`, `updated_by`, `fields_terminated_by`, `optionally_enclosed_by`, `lines_terminated_by`) VALUES
(1, 'E:\\\\CrawlMayAnhTemp', 'csv', 12, 'id, name, link, actual_price, sale_price, brand, images, text_description, video_url_description, outstanding_features, image_spec, lighting_spec, video_spec, focus_spec, screen_spec, viewfinder_spec, storage_connect_spec, flash_spec, physic_spec, lens_spec, date, date_sk', 'temp_kyma_cameras', 'daily_kyma_cameras', 'daily_kyma_cameras', b'1', ',', '2024-11-29 14:35:22', 'Tu', '2024-12-10 07:09:14', 'Tú', ',', '\"', '\\r\\n');

-- --------------------------------------------------------

--
-- Cấu trúc bảng cho bảng `logs`
--

DROP TABLE IF EXISTS `logs`;
CREATE TABLE `logs` (
  `id` bigint(20) NOT NULL,
  `config_id` bigint(20) DEFAULT NULL,
  `file_name` varchar(255) DEFAULT NULL,
  `file_size` varchar(255) DEFAULT NULL,
  `records_count` int(11) DEFAULT NULL,
  `message` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL,
  `created_at` datetime DEFAULT NULL,
  `update_at` datetime DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Đang đổ dữ liệu cho bảng `logs`
--

INSERT INTO `logs` (`id`, `config_id`, `file_name`, `file_size`, `records_count`, `message`, `created_at`, `update_at`) VALUES
(30, 1, 'camera_details_crawled_2024-11-29_09-58-18.csv', '205 KB', 52, 'Load data từ bảng temp_dw  vào main_dw thành công', '2024-12-09 20:56:41', '2024-12-09 21:30:56'),
(31, 1, 'camera_details_crawled_2024-12-02_15-28-48.csv', '205 KB', 52, 'Load data từ bảng temp_dw  vào main_dw thành công', '2024-12-09 20:56:41', '2024-12-09 21:30:56'),
(32, 1, 'camera_details_crawled_2024-12-09_22-17-46.csv', '205 KB', 52, 'Load data từ bảng temp_dw  vào main_dw thành công', '2024-12-09 22:19:04', '2024-12-09 22:19:04'),
(33, 1, 'camera_details_crawled_2024-12-10_07-07-52.csv', '205 KB', 52, 'Load data từ bảng temp_dw  vào main_dw thành công', '2024-12-10 07:09:13', '2024-12-10 07:09:14');

-- --------------------------------------------------------

--
-- Cấu trúc bảng cho bảng `processes`
--

DROP TABLE IF EXISTS `processes`;
CREATE TABLE `processes` (
  `id` int(11) NOT NULL,
  `status` enum('EXTRACT_READY','EXTRACT_TO_TEMP_STAGING','EXTRACT_TO_MAINSTAGING','EXTRACT_TO_TEMP_DW','EXTRACT_TO_DW') DEFAULT NULL,
  `description` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL,
  `log_id` bigint(20) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Đang đổ dữ liệu cho bảng `processes`
--

INSERT INTO `processes` (`id`, `status`, `description`, `log_id`) VALUES
(34, 'EXTRACT_TO_DW', 'Load data từ bảng temp_dw  vào main_dw thành công', 30),
(35, 'EXTRACT_TO_DW', 'Load data từ bảng temp_dw  vào main_dw thành công', 31),
(36, 'EXTRACT_TO_DW', 'Load data từ bảng temp_dw  vào main_dw thành công', 32),
(37, 'EXTRACT_TO_DW', 'Load data từ bảng temp_dw  vào main_dw thành công', 33);

--
-- Chỉ mục cho các bảng đã đổ
--

--
-- Chỉ mục cho bảng `configs`
--
ALTER TABLE `configs`
  ADD PRIMARY KEY (`id`);

--
-- Chỉ mục cho bảng `logs`
--
ALTER TABLE `logs`
  ADD PRIMARY KEY (`id`),
  ADD KEY `logs_configs_id_fk` (`config_id`);

--
-- Chỉ mục cho bảng `processes`
--
ALTER TABLE `processes`
  ADD PRIMARY KEY (`id`),
  ADD KEY `log_id_of_proccess_idx` (`log_id`);

--
-- AUTO_INCREMENT cho các bảng đã đổ
--

--
-- AUTO_INCREMENT cho bảng `configs`
--
ALTER TABLE `configs`
  MODIFY `id` bigint(20) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=5;

--
-- AUTO_INCREMENT cho bảng `logs`
--
ALTER TABLE `logs`
  MODIFY `id` bigint(20) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=34;

--
-- AUTO_INCREMENT cho bảng `processes`
--
ALTER TABLE `processes`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=38;

--
-- Các ràng buộc cho các bảng đã đổ
--

--
-- Các ràng buộc cho bảng `logs`
--
ALTER TABLE `logs`
  ADD CONSTRAINT `logs_configs_id_fk` FOREIGN KEY (`config_id`) REFERENCES `configs` (`id`);

--
-- Các ràng buộc cho bảng `processes`
--
ALTER TABLE `processes`
  ADD CONSTRAINT `log_id_of_proccess` FOREIGN KEY (`log_id`) REFERENCES `logs` (`id`);
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
