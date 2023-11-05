**to install the PROJECT
firstly, you need to create the DATABASE**

DROP DATABASE account_management;
CREATE DATABASE account_management;
USE account_management;
--- create the giangvien TABLE
CREATE TABLE giangvien (
    username NCHAR(20) NOT NULL,
    password NCHAR(20) NOT NULL,
    email NCHAR(50) NOT NULL,
);
--- create the sinhvien TABLE
CREATE TABLE sinhvien (
    	username NCHAR(20) NOT NULL,
    	password NCHAR(20) NOT NULL,
    	email NCHAR(50) NOT NULL,
	name NCHAR(50) NOT NULL,
	mssv NCHAR(20) NOT NULL,
	avatar VARBINARY(MAX) NOT NULL,
);
