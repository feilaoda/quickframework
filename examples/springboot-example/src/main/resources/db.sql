
DROP TABLE IF EXISTS `sys_department`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8 */;
CREATE TABLE `sys_department` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `name` varchar(255) DEFAULT NULL,
  `title` varchar(255) DEFAULT NULL,
  `num` int DEFAULT NULL,
  `parent_id` bigint DEFAULT NULL,
  `parent_ids` varchar(255) DEFAULT NULL,
  `tips` varchar(255) DEFAULT NULL,
  `version` int DEFAULT NULL,
  `created_at` datetime DEFAULT NULL COMMENT '创建时间/注册时间',
  `updated_at` datetime DEFAULT NULL COMMENT '最后更新时间',
  `created_by` bigint DEFAULT NULL COMMENT '创建人',
  `updated_by` bigint DEFAULT NULL COMMENT '最后更新人',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1481469216795242499 DEFAULT CHARSET=utf8 COMMENT='部门';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `sys_menu`
--

DROP TABLE IF EXISTS `sys_menu`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8 */;
CREATE TABLE `sys_menu` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `name` varchar(255) NOT NULL COMMENT '名称',
  `path` varchar(255) NOT NULL COMMENT '编号',
  `hidden` tinyint DEFAULT NULL COMMENT '是否隐藏',
  `parent_id` bigint NOT NULL COMMENT '父菜单编号',
  `icon` varchar(32) DEFAULT NULL COMMENT '图标',
  `is_menu` int NOT NULL COMMENT '是否是菜单1:菜单,0:按钮',
  `is_open` int DEFAULT NULL COMMENT '是否默认打开1:是,0:否',
  `levels` int NOT NULL COMMENT '级别',
  `sort` int NOT NULL COMMENT '顺序',
  `status` int NOT NULL COMMENT '状态1:启用,0:禁用',
  `created_at` datetime DEFAULT NULL COMMENT '创建时间/注册时间',
  `updated_at` datetime DEFAULT NULL COMMENT '最后更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK_s37unj3gh67ujhk83lqva8i1t` (`path`)
) ENGINE=InnoDB AUTO_INCREMENT=89 DEFAULT CHARSET=utf8 COMMENT='菜单';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `sys_menu_item`
--

DROP TABLE IF EXISTS `sys_menu_item`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8 */;
CREATE TABLE `sys_menu_item` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `name` varchar(255) DEFAULT NULL,
  `path` varchar(255) DEFAULT NULL,
  `created_at` datetime DEFAULT NULL,
  `updated_at` datetime DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `sys_permission`
--

DROP TABLE IF EXISTS `sys_permission`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8 */;
CREATE TABLE `sys_permission` (
  `id` bigint DEFAULT NULL,
  `name` varchar(255) DEFAULT NULL,
  `resource` varchar(255) DEFAULT NULL,
  `operation` int DEFAULT '0',
  `created_at` datetime DEFAULT NULL,
  `updated_at` datetime DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `sys_role`
--

DROP TABLE IF EXISTS `sys_role`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8 */;
CREATE TABLE `sys_role` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `name` varchar(255) DEFAULT NULL,
  `idx` int DEFAULT NULL,
  `tips` varchar(255) DEFAULT NULL,
  `version` int DEFAULT NULL,
  `created_at` datetime DEFAULT NULL COMMENT '创建时间/注册时间',
  `updated_at` datetime DEFAULT NULL COMMENT '最后更新时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1480809758444236802 DEFAULT CHARSET=utf8 COMMENT='角色';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `sys_role_sys_menu`
--

DROP TABLE IF EXISTS `sys_role_sys_menu`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8 */;
CREATE TABLE `sys_role_sys_menu` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `menu_id` bigint DEFAULT NULL,
  `role_id` bigint DEFAULT NULL,
  `created_at` datetime DEFAULT NULL COMMENT '创建时间/注册时间',
  `updated_at` datetime DEFAULT NULL COMMENT '最后更新时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=103 DEFAULT CHARSET=utf8 COMMENT='菜单角色关系';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `sys_role_sys_permission`
--

DROP TABLE IF EXISTS `sys_role_sys_permission`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8 */;
CREATE TABLE `sys_role_sys_permission` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `role_id` bigint DEFAULT NULL,
  `permission_id` bigint DEFAULT NULL,
  `created_at` datetime DEFAULT NULL,
  `updated_at` datetime DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1552934712106840067 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `sys_user`
--

DROP TABLE IF EXISTS `sys_user`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8 */;
CREATE TABLE `sys_user` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `account` varchar(255) DEFAULT NULL,
  `password` varchar(255) DEFAULT NULL,
  `avatar` varchar(255) DEFAULT NULL,
  `department_id` bigint DEFAULT NULL,
  `email` varchar(255) DEFAULT NULL,
  `name` varchar(255) DEFAULT NULL,
  `phone` varchar(255) DEFAULT NULL,
  `salt` varchar(255) DEFAULT NULL,
  `gender` int DEFAULT '0',
  `status` int DEFAULT '1',
  `version` int DEFAULT NULL,
  `created_at` datetime DEFAULT NULL COMMENT '创建时间/注册时间',
  `updated_at` datetime DEFAULT NULL COMMENT '最后更新时间',
  `ts` timestamp NULL DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `idx_account` (`account`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=1482982767321952259 DEFAULT CHARSET=utf8 COMMENT='账号';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `sys_user_sys_role`
--

DROP TABLE IF EXISTS `sys_user_sys_role`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8 */;
CREATE TABLE `sys_user_sys_role` (
  `id` bigint NOT NULL,
  `user_id` bigint DEFAULT NULL,
  `role_id` bigint DEFAULT NULL,
  `created_at` datetime DEFAULT NULL,
  `updated_at` datetime DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;
