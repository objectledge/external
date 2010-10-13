#
#
# Test that JNDI mechanism works correctly when binding/looking up (XA)DataSource
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

Database configuration are stored in properties file (spy.properties) 
which contains the following properties:
o realdriver - Name of the JDBC driver
o url - URL to connect to the data base

Compilation:
------------

Go to output/dist/examples/distributeddatasource

Type

   $ ant compile

to compile the example

Run the example:
----------------
o To run the example, first check that only RMI protocol will be
activated (in the ../../config/carol.properties, carol.rmi.activated
should be set to jrmp); then type in the lib/ directory:

   $ rmiregistry -J-classpath -Jjotm.jar:jotm_jrmp_stubs.jar:xapool.jar -J-Djava.security.policy=../config/java.policy &

to start a RMI registry on default port (i.e. 1099).

o Set the classpath

   $ export CLASSPATH=../../lib/jotm.jar:../../lib/jotm_jrmp_stubs.jar:../../config:.:../../lib/xapool.jar:../../lib/p6spy.jar:..:$JDBC_JARS

where  JDBC_JARS is the location of the JDBC driver jar file(s) you want to use.

o Modify ../../config/spy.properties to setup your jdbc properties

o Start the example

	* start the Database layer
	
  	   $ java DataBaseLayer
	
	* start the application

      $ java DistributedDataSourceExample commit 4

Usage:
------

   $ java DistributedDataSourceExample [completion] [value]

where
o completion is either commit or rollback
o value is an integer value
		
Enjoy!
