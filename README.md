# Project Title
LOGIN UI

## Installation
to INSTALL the PROJECT
## 
Firstly, you need to CREATE the DATABASE 
##
<pre><code>
DROP DATABASE account_management;
CREATE DATABASE account_management;
USE account_management;
--- create the giangvien TABLE
CREATE TABLE giangvien (
  username NCHAR(20) ALLOWS NULL,
  password NCHAR(20) ALLOWS NULL,
  email NCHAR(50) ALLOWS NULL,
);
--- create the sinhvien TABLE
CREATE TABLE sinhvien (
  username NCHAR(20) ALLOWS NULL,
  password NCHAR(20) ALLOWS NULL,
  email NCHAR(50) ALLOWS NULL,
  name NCHAR(50) ALLOWS NULL,
  mssv NCHAR(20) ALLOWS NULL,
  avatar VARBINARY(MAX) ALLOWS NULL,
);
</code></pre>


