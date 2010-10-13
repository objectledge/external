#
#
# StandardPoolDataSource example
#
#

Scenario:
---------

Setup:
------

o A database:
	- MySQL
	- PostgreSQL

Database Setup:
---------------

The example expects:
o a database named javatest
o a user of login "mojo" and password "jojo"
o a table named testdata which is like 

+----+-------+
| ID | FOO   |
+----+-------+
|  1 | 1     |
+----+-------+
	
	o id being an int (primary key)
	o foo being an int
	
For example on MySQL:
mysql> GRANT ALL PRIVILEGES ON *.* TO mojo
    ->   IDENTIFIED BY 'jojo' WITH GRANT OPTION;
mysql> create database javatest;
mysql> use javatest;
mysql> create table testdata (
    ->   id int not null auto_increment primary key,
    ->   foo int)type=bdb;
mysql> insert into testdata values(null, 1);

Database configuration are stored in properties file (mysql.properties) 
which contains the following properties:
o realdriver - Name of the JDBC driver
o url - URL to connect to the data base

Compilation:
------------

Go to output/dist/examples/pooldatasource

Type

   $ ant compile

to compile the example

Run the example:
----------------

o Set the classpath

   $ export CLASSPATH=../../lib/xapool.jar:../../config:.:../../lib/log4j.jar:../../lib/commons-logging.jar:../../lib/commons-logging-api.jar:../../lib/p6spy.jar:../../lib/connector-1_5.jar:../../lib/howl.jar:$JDBC_JARS
   
where  JDBC_JARS is the location of the JDBC driver jar file(s) you want to use.

o Modify ../../config/spy.properties to setup your jdbc properties

o Start the example

   $ java StandardPoolDataSourceExample

Usage:
------

   $ java StandardPoolDataSourceExample

Enjoy!
