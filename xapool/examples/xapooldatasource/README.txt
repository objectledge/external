#
#
# StandardXAPoolDataSource example
#
# (this example comes from JOTM project, thanks to Jeff Mesnil)
#


Scenario:
---------

The JDBC is a simple example showing how
to use JTA transactions with JDBC.

Setup:
------

o A RMI registry
o JOTM is the Transaction Manager
o A database:
	- MySQL
	- PostgreSQL

o DatabaseHelper setups the JDBC objects (i.e., the JDBC Connection) with Enhydra wrappers and 
  associates them to JOTM as their transaction manager.

o JdbcExample then will ask the DatabaseHelper for a connection, update a table within a transaction 
   and complete this transaction thanks to arguments given by the user.

Database Setup:
---------------

The example expects:
o a database named javatest
o a user of login "mojo" and password "jojo"
o a *transactional* table named testdata which is like 

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

Type

   $ ant compile

to compile the example

Run the example:
----------------

o To run the example, first check that only RMI protocol will be
activated (in the ../../config/carol.properties, carol.rmi.activated
should be set to jrmp); then type 

   $ rmiregistry -J-classpath -Jjotm.jar:jotm_jrmp_stubs.jar -J-Djava.security.policy=../config/java.policy &

to start a RMI registry on default port (i.e. 1099).

o Set the classpath

   $ export CLASSPATH=../../lib/jotm.jar:../../lib/jotm_jrmp_stubs.jar:../../lib/xapool.jar:../../config:.:../../lib/commons-logging.jar:../../lib/commons-logging-api.jar:../../lib/p6spy.jar:../../lib/connector-1_5.jar:../../lib/howl.jar:$JDBC_JARS
   
where  JDBC_JARS is the location of the JDBC driver jar file(s) you want to use.

o Modify ../../config/spy.properties to setup your jdbc properties

o Start the example

   $ java StandardXAPoolDataSourceExample commit 2 // set table value to 2 and commit the transaction 
   $ java StandardXAPoolDataSourceExample rollback 0 // set table value to 0 but rollback the transaction
   $ java StandardXAPoolDataSourceExample ...   

Usage:
------

   $ java StandardXAPoolDataSourceExample [completion] [number]

where
o completion can be:
	- commit
	- rollback
o number has to be a integer
		
Enjoy!


