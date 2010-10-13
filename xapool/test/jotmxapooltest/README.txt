#
# Complete test from Andy Zeneski
# this test is used with HSQL by Andy
# and can be used with PostgreSQL or MySQL
# this test has been modified to be used with postgres.
# ORIGINAL test is available at:
# http://xapool.experlog.com/pub/test/jotmxapooltest.zip
#

Scenario:
---------
o Various different tests to make sure XAPool and JOTM are working properly. 
o Used before updating OFBiz.

Setup:
------
o A database:
    - HSQLDB
	- MySQL
	- PostgreSQL
	- Oracle

Database Setup:
---------------
o configure the database properties in the config directory
o configure the properties file to use in config.properties
o configure the table name and other options there as well
o configure the log4j settings in config/log4j.properties


Compilation:
------------
Go to output/dist/test/jotmxapooltest

Type

   $ ant 
   
to compile and run the test

Run the test:
----------------
o Just run ant; all the tests will run. There will be a log
  directory left behind with raw text and html output from the test


Usage:
------

   $ ant

Enjoy!

