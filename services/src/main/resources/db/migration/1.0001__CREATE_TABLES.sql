-- MySQL dump 10.13  Distrib 5.5.29, for Win64 (x86)
--
-- Host: localhost    Database: digout
-- ------------------------------------------------------
-- Server version	5.5.29

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `application_versions`
--

DROP TABLE IF EXISTS `application_versions`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `application_versions` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `client_platform_type` varchar(255) NOT NULL,
  `client_platform_version` varchar(255) NOT NULL,
  `downloadUrl` varchar(255) NOT NULL,
  `server_platform_version` varchar(255) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `application_versions`
--

LOCK TABLES `application_versions` WRITE;
/*!40000 ALTER TABLE `application_versions` DISABLE KEYS */;
/*!40000 ALTER TABLE `application_versions` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `audit_logs`
--

DROP TABLE IF EXISTS `audit_logs`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `audit_logs` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `created_datetime` datetime NOT NULL,
  `input_data` varchar(255) DEFAULT NULL,
  `operation_name` varchar(255) DEFAULT NULL,
  `output_data` varchar(255) DEFAULT NULL,
  `username` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `audit_logs`
--

LOCK TABLES `audit_logs` WRITE;
/*!40000 ALTER TABLE `audit_logs` DISABLE KEYS */;
/*!40000 ALTER TABLE `audit_logs` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `bank_info`
--

DROP TABLE IF EXISTS `bank_info`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `bank_info` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `cardholder_present_code` varchar(255) DEFAULT NULL,
  `installmentCnt` varchar(255) DEFAULT NULL,
  `merchant_ID` varchar(255) DEFAULT NULL,
  `mode` varchar(255) DEFAULT NULL,
  `moto_ind` varchar(255) DEFAULT NULL,
  `name` varchar(255) DEFAULT NULL,
  `original_retref_num` varchar(255) DEFAULT NULL,
  `password` varchar(255) DEFAULT NULL,
  `prov_user_ID` varchar(255) DEFAULT NULL,
  `terminal_ID` varchar(255) DEFAULT NULL,
  `transaction_type` varchar(255) DEFAULT NULL,
  `bank_uri` varchar(255) DEFAULT NULL,
  `user_ID` varchar(255) DEFAULT NULL,
  `version` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `bank_info`
--

LOCK TABLES `bank_info` WRITE;
/*!40000 ALTER TABLE `bank_info` DISABLE KEYS */;
/*!40000 ALTER TABLE `bank_info` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `bank_transaction_fail`
--

DROP TABLE IF EXISTS `bank_transaction_fail`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `bank_transaction_fail` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `error_message` varchar(255) DEFAULT NULL,
  `request_xml` varchar(1500) NOT NULL,
  `response_xml` varchar(1500) NOT NULL,
  `transaction_date` datetime NOT NULL,
  `buyer_id` bigint(20) NOT NULL,
  `product_id` bigint(20) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FK6FA6CBA2295C6267` (`buyer_id`),
  KEY `FK6FA6CBA2C30AE38D` (`product_id`),
  CONSTRAINT `FK6FA6CBA2C30AE38D` FOREIGN KEY (`product_id`) REFERENCES `products` (`id`),
  CONSTRAINT `FK6FA6CBA2295C6267` FOREIGN KEY (`buyer_id`) REFERENCES `users` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `bank_transaction_fail`
--

LOCK TABLES `bank_transaction_fail` WRITE;
/*!40000 ALTER TABLE `bank_transaction_fail` DISABLE KEYS */;
/*!40000 ALTER TABLE `bank_transaction_fail` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `favourite_products`
--

DROP TABLE IF EXISTS `favourite_products`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `favourite_products` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `owner_id` bigint(20) NOT NULL,
  `product_id` bigint(20) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `product_id` (`product_id`,`owner_id`),
  KEY `FKFE0CCC5AC30AE38D` (`product_id`),
  KEY `FKFE0CCC5AC6D2D167` (`owner_id`),
  CONSTRAINT `FKFE0CCC5AC6D2D167` FOREIGN KEY (`owner_id`) REFERENCES `users` (`id`),
  CONSTRAINT `FKFE0CCC5AC30AE38D` FOREIGN KEY (`product_id`) REFERENCES `products` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `favourite_products`
--

LOCK TABLES `favourite_products` WRITE;
/*!40000 ALTER TABLE `favourite_products` DISABLE KEYS */;
/*!40000 ALTER TABLE `favourite_products` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `images`
--

DROP TABLE IF EXISTS `images`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `images` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `imgs_group` varchar(255) NOT NULL,
  `image_path` varchar(255) NOT NULL,
  `uploaded_datetime` datetime NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `images`
--

LOCK TABLES `images` WRITE;
/*!40000 ALTER TABLE `images` DISABLE KEYS */;
/*!40000 ALTER TABLE `images` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `order_issues`
--

DROP TABLE IF EXISTS `order_issues`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `order_issues` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `issue_details` varchar(255) DEFAULT NULL,
  `issue_type` varchar(255) NOT NULL,
  `report_date` datetime NOT NULL,
  `order_id` bigint(20) NOT NULL,
  `user_id` bigint(20) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FK53E204EB5AEC214F` (`user_id`),
  KEY `FK53E204EB2E3E740` (`order_id`),
  CONSTRAINT `FK53E204EB2E3E740` FOREIGN KEY (`order_id`) REFERENCES `user_orders` (`id`),
  CONSTRAINT `FK53E204EB5AEC214F` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `order_issues`
--

LOCK TABLES `order_issues` WRITE;
/*!40000 ALTER TABLE `order_issues` DISABLE KEYS */;
/*!40000 ALTER TABLE `order_issues` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `product_comments`
--

DROP TABLE IF EXISTS `product_comments`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `product_comments` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `comment` varchar(1024) NOT NULL,
  `published_date` datetime DEFAULT NULL,
  `posted_by_id` bigint(20) NOT NULL,
  `product_id` bigint(20) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FK122F1F64C30AE38D` (`product_id`),
  KEY `FK122F1F648A12DA43` (`posted_by_id`),
  CONSTRAINT `FK122F1F648A12DA43` FOREIGN KEY (`posted_by_id`) REFERENCES `users` (`id`),
  CONSTRAINT `FK122F1F64C30AE38D` FOREIGN KEY (`product_id`) REFERENCES `products` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `product_comments`
--

LOCK TABLES `product_comments` WRITE;
/*!40000 ALTER TABLE `product_comments` DISABLE KEYS */;
/*!40000 ALTER TABLE `product_comments` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `product_main_images`
--

DROP TABLE IF EXISTS `product_main_images`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `product_main_images` (
  `format` int(11) NOT NULL,
  `id` bigint(20) NOT NULL,
  `product_id` bigint(20) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `product_id` (`product_id`,`format`),
  KEY `FKFBE03C4EC31CEA7` (`id`),
  KEY `FKFBE03C4EC30AE38D` (`product_id`),
  CONSTRAINT `FKFBE03C4EC30AE38D` FOREIGN KEY (`product_id`) REFERENCES `products` (`id`),
  CONSTRAINT `FKFBE03C4EC31CEA7` FOREIGN KEY (`id`) REFERENCES `images` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `product_main_images`
--

LOCK TABLES `product_main_images` WRITE;
/*!40000 ALTER TABLE `product_main_images` DISABLE KEYS */;
/*!40000 ALTER TABLE `product_main_images` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `product_secondary_images`
--

DROP TABLE IF EXISTS `product_secondary_images`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `product_secondary_images` (
  `format` int(11) NOT NULL,
  `id` bigint(20) NOT NULL,
  `product_id` bigint(20) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FK46816D33C31CEA7` (`id`),
  KEY `FK46816D33C30AE38D` (`product_id`),
  CONSTRAINT `FK46816D33C30AE38D` FOREIGN KEY (`product_id`) REFERENCES `products` (`id`),
  CONSTRAINT `FK46816D33C31CEA7` FOREIGN KEY (`id`) REFERENCES `images` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `product_secondary_images`
--

LOCK TABLES `product_secondary_images` WRITE;
/*!40000 ALTER TABLE `product_secondary_images` DISABLE KEYS */;
/*!40000 ALTER TABLE `product_secondary_images` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `product_tag`
--

DROP TABLE IF EXISTS `product_tag`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `product_tag` (
  `tag` varchar(255) NOT NULL,
  PRIMARY KEY (`tag`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `product_tag`
--

LOCK TABLES `product_tag` WRITE;
/*!40000 ALTER TABLE `product_tag` DISABLE KEYS */;
/*!40000 ALTER TABLE `product_tag` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `product_tags`
--

DROP TABLE IF EXISTS `product_tags`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `product_tags` (
  `product_id` bigint(20) NOT NULL,
  `tag_id` varchar(255) NOT NULL,
  PRIMARY KEY (`product_id`,`tag_id`),
  UNIQUE KEY `product_id` (`product_id`,`tag_id`),
  KEY `FKC85AB589C30AE38D` (`product_id`),
  KEY `FKC85AB58945B6965E` (`tag_id`),
  CONSTRAINT `FKC85AB58945B6965E` FOREIGN KEY (`tag_id`) REFERENCES `product_tag` (`tag`),
  CONSTRAINT `FKC85AB589C30AE38D` FOREIGN KEY (`product_id`) REFERENCES `products` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `product_tags`
--

LOCK TABLES `product_tags` WRITE;
/*!40000 ALTER TABLE `product_tags` DISABLE KEYS */;
/*!40000 ALTER TABLE `product_tags` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `products`
--

DROP TABLE IF EXISTS `products`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `products` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `currency` varchar(255) DEFAULT NULL,
  `information` varchar(200) DEFAULT NULL,
  `name` varchar(255) NOT NULL,
  `price` double DEFAULT NULL,
  `published_date` datetime DEFAULT NULL,
  `is_purchasable` bit(1) NOT NULL,
  `sell_type` varchar(255) NOT NULL,
  `shipment_id` varchar(255) DEFAULT NULL,
  `shipment_type` varchar(255) DEFAULT NULL,
  `sold_date` datetime DEFAULT NULL,
  `status` varchar(255) NOT NULL,
  `address_id` bigint(20) DEFAULT NULL,
  `owner_id` bigint(20) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FKF2D1C164C8CCA440` (`address_id`),
  KEY `FKF2D1C164C6D2D167` (`owner_id`),
  CONSTRAINT `FKF2D1C164C6D2D167` FOREIGN KEY (`owner_id`) REFERENCES `users` (`id`),
  CONSTRAINT `FKF2D1C164C8CCA440` FOREIGN KEY (`address_id`) REFERENCES `user_addresses` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `products`
--

LOCK TABLES `products` WRITE;
/*!40000 ALTER TABLE `products` DISABLE KEYS */;
/*!40000 ALTER TABLE `products` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `user_addresses`
--

DROP TABLE IF EXISTS `user_addresses`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `user_addresses` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `address_name` varchar(255) DEFAULT NULL,
  `address_line` varchar(255) DEFAULT NULL,
  `assignment` varchar(255) DEFAULT NULL,
  `city` varchar(255) NOT NULL,
  `district` varchar(255) DEFAULT NULL,
  `latitude` varchar(255) DEFAULT NULL,
  `longitude` varchar(255) DEFAULT NULL,
  `postal_code` varchar(255) DEFAULT NULL,
  `region` varchar(255) DEFAULT NULL,
  `user_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK98BB680E5AEC214F` (`user_id`),
  CONSTRAINT `FK98BB680E5AEC214F` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `user_addresses`
--

LOCK TABLES `user_addresses` WRITE;
/*!40000 ALTER TABLE `user_addresses` DISABLE KEYS */;
/*!40000 ALTER TABLE `user_addresses` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `user_comments`
--

DROP TABLE IF EXISTS `user_comments`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `user_comments` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `comment` varchar(1024) NOT NULL,
  `for_user_id` bigint(20) NOT NULL,
  `posted_by_id` bigint(20) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FK21672FE82CEC7BD9` (`for_user_id`),
  KEY `FK21672FE88A12DA43` (`posted_by_id`),
  CONSTRAINT `FK21672FE88A12DA43` FOREIGN KEY (`posted_by_id`) REFERENCES `users` (`id`),
  CONSTRAINT `FK21672FE82CEC7BD9` FOREIGN KEY (`for_user_id`) REFERENCES `users` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `user_comments`
--

LOCK TABLES `user_comments` WRITE;
/*!40000 ALTER TABLE `user_comments` DISABLE KEYS */;
/*!40000 ALTER TABLE `user_comments` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `user_conversation`
--

DROP TABLE IF EXISTS `user_conversation`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `user_conversation` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `interlocutor_id` bigint(20) NOT NULL,
  `last_message_id` bigint(20) DEFAULT NULL,
  `user_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FKB4BC0C7734821B94` (`interlocutor_id`),
  KEY `FKB4BC0C775AEC214F` (`user_id`),
  KEY `FKB4BC0C7743399669` (`last_message_id`),
  CONSTRAINT `FKB4BC0C7743399669` FOREIGN KEY (`last_message_id`) REFERENCES `user_messages` (`id`),
  CONSTRAINT `FKB4BC0C7734821B94` FOREIGN KEY (`interlocutor_id`) REFERENCES `users` (`id`),
  CONSTRAINT `FKB4BC0C775AEC214F` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `user_conversation`
--

LOCK TABLES `user_conversation` WRITE;
/*!40000 ALTER TABLE `user_conversation` DISABLE KEYS */;
/*!40000 ALTER TABLE `user_conversation` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `user_follower`
--

DROP TABLE IF EXISTS `user_follower`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `user_follower` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `is_following` bit(1) NOT NULL,
  `followed_id` bigint(20) NOT NULL,
  `follower_id` bigint(20) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `followed_id` (`followed_id`,`follower_id`),
  KEY `FK574C7212C32F281C` (`follower_id`),
  KEY `FK574C7212C328CAEA` (`followed_id`),
  CONSTRAINT `FK574C7212C328CAEA` FOREIGN KEY (`followed_id`) REFERENCES `users` (`id`),
  CONSTRAINT `FK574C7212C32F281C` FOREIGN KEY (`follower_id`) REFERENCES `users` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `user_follower`
--

LOCK TABLES `user_follower` WRITE;
/*!40000 ALTER TABLE `user_follower` DISABLE KEYS */;
/*!40000 ALTER TABLE `user_follower` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `user_friends`
--

DROP TABLE IF EXISTS `user_friends`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `user_friends` (
  `user_id` bigint(20) NOT NULL,
  `friend_id` bigint(20) NOT NULL,
  UNIQUE KEY `user_id` (`user_id`,`friend_id`),
  KEY `FK372AE1215AEC214F` (`user_id`),
  KEY `FK372AE1211E402D9C` (`friend_id`),
  CONSTRAINT `FK372AE1211E402D9C` FOREIGN KEY (`friend_id`) REFERENCES `users` (`id`),
  CONSTRAINT `FK372AE1215AEC214F` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `user_friends`
--

LOCK TABLES `user_friends` WRITE;
/*!40000 ALTER TABLE `user_friends` DISABLE KEYS */;
/*!40000 ALTER TABLE `user_friends` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `user_image`
--

DROP TABLE IF EXISTS `user_image`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `user_image` (
  `format` int(11) NOT NULL,
  `id` bigint(20) NOT NULL,
  `user_id` bigint(20) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `user_id` (`user_id`,`format`),
  KEY `FKC647B3C7C31CEA7` (`id`),
  KEY `FKC647B3C75AEC214F` (`user_id`),
  CONSTRAINT `FKC647B3C75AEC214F` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`),
  CONSTRAINT `FKC647B3C7C31CEA7` FOREIGN KEY (`id`) REFERENCES `images` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `user_image`
--

LOCK TABLES `user_image` WRITE;
/*!40000 ALTER TABLE `user_image` DISABLE KEYS */;
/*!40000 ALTER TABLE `user_image` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `user_messages`
--

DROP TABLE IF EXISTS `user_messages`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `user_messages` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `is_deleted_by_receiver` bit(1) NOT NULL,
  `is_deleted_by_sender` bit(1) NOT NULL,
  `is_read` bit(1) NOT NULL,
  `sent_data` datetime NOT NULL,
  `text` varchar(255) DEFAULT NULL,
  `receiver_id` bigint(20) NOT NULL,
  `sender_id` bigint(20) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FK29C45340E65EC0AB` (`receiver_id`),
  KEY `FK29C45340659C62A5` (`sender_id`),
  CONSTRAINT `FK29C45340659C62A5` FOREIGN KEY (`sender_id`) REFERENCES `users` (`id`),
  CONSTRAINT `FK29C45340E65EC0AB` FOREIGN KEY (`receiver_id`) REFERENCES `users` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `user_messages`
--

LOCK TABLES `user_messages` WRITE;
/*!40000 ALTER TABLE `user_messages` DISABLE KEYS */;
/*!40000 ALTER TABLE `user_messages` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `user_orders`
--

DROP TABLE IF EXISTS `user_orders`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `user_orders` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `canceled_date` datetime DEFAULT NULL,
  `carrier_code` varchar(255) DEFAULT NULL,
  `order_date` datetime DEFAULT NULL,
  `is_paid` bit(1) DEFAULT NULL,
  `start_shipping_date` datetime DEFAULT NULL,
  `tracking_code` varchar(255) DEFAULT NULL,
  `bank_order_id` varchar(255) DEFAULT NULL,
  `xml_response` varchar(1500) DEFAULT NULL,
  `address_id` bigint(20) DEFAULT NULL,
  `buyer_id` bigint(20) DEFAULT NULL,
  `product_id` bigint(20) DEFAULT NULL,
  `seller_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `bank_order_id` (`bank_order_id`),
  KEY `FKD33A5D9295C6267` (`buyer_id`),
  KEY `FKD33A5D9C8CCA440` (`address_id`),
  KEY `FKD33A5D9976B11B` (`seller_id`),
  KEY `FKD33A5D9C30AE38D` (`product_id`),
  CONSTRAINT `FKD33A5D9C30AE38D` FOREIGN KEY (`product_id`) REFERENCES `products` (`id`),
  CONSTRAINT `FKD33A5D9295C6267` FOREIGN KEY (`buyer_id`) REFERENCES `users` (`id`),
  CONSTRAINT `FKD33A5D9976B11B` FOREIGN KEY (`seller_id`) REFERENCES `users` (`id`),
  CONSTRAINT `FKD33A5D9C8CCA440` FOREIGN KEY (`address_id`) REFERENCES `user_addresses` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `user_orders`
--

LOCK TABLES `user_orders` WRITE;
/*!40000 ALTER TABLE `user_orders` DISABLE KEYS */;
/*!40000 ALTER TABLE `user_orders` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `user_phones`
--

DROP TABLE IF EXISTS `user_phones`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `user_phones` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `is_primary` bit(1) NOT NULL,
  `phone_name` varchar(255) DEFAULT NULL,
  `phone_number` varchar(255) NOT NULL,
  `user_id` bigint(20) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `id` (`id`,`is_primary`),
  KEY `FKE60B3B95AEC214F` (`user_id`),
  CONSTRAINT `FKE60B3B95AEC214F` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `user_phones`
--

LOCK TABLES `user_phones` WRITE;
/*!40000 ALTER TABLE `user_phones` DISABLE KEYS */;
/*!40000 ALTER TABLE `user_phones` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `user_session_tokens`
--

DROP TABLE IF EXISTS `user_session_tokens`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `user_session_tokens` (
  `token_id` varchar(255) NOT NULL,
  `expire_time` datetime NOT NULL,
  `last_action_time` datetime NOT NULL,
  `user_id` bigint(20) NOT NULL,
  PRIMARY KEY (`token_id`),
  KEY `FKEAB372775AEC214F` (`user_id`),
  CONSTRAINT `FKEAB372775AEC214F` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `user_session_tokens`
--

LOCK TABLES `user_session_tokens` WRITE;
/*!40000 ALTER TABLE `user_session_tokens` DISABLE KEYS */;
/*!40000 ALTER TABLE `user_session_tokens` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `users`
--

DROP TABLE IF EXISTS `users`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `users` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `external_id` varchar(255) DEFAULT NULL,
  `fullname` varchar(255) NOT NULL,
  `iban` varchar(255) DEFAULT NULL,
  `is_following_notification` bit(1) NOT NULL,
  `is_friends_notification` bit(1) NOT NULL,
  `is_shortlists_notification` bit(1) NOT NULL,
  `is_system_user` bit(1) NOT NULL,
  `locale` varchar(255) NOT NULL,
  `mobile_number` varchar(255) DEFAULT NULL,
  `origin` int(11) NOT NULL,
  `rating` int(11) DEFAULT NULL,
  `role` int(11) NOT NULL,
  `email` varchar(255) DEFAULT NULL,
  `password` varchar(255) NOT NULL,
  `username` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `email` (`email`,`is_system_user`,`origin`),
  UNIQUE KEY `username` (`username`,`is_system_user`,`origin`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `users`
--

LOCK TABLES `users` WRITE;
/*!40000 ALTER TABLE `users` DISABLE KEYS */;
/*!40000 ALTER TABLE `users` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2014-03-31 12:56:43
