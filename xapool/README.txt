2006/12/19 - xavier.spengler@experlog.com

XAPool: XAPool is Another Pool

Description:
------------
XAPool is a project which allows to use a pool, connection pooling,
and XA connection pooling.
A generic pool is used to store various of objects (see 
org.enhydra.jdbc.pool.GenericPool).
Pools of JDBC connections, and XA connections are presents in
org.enhydra.jdbc.pool.StandardPoolDataSource and StandardXAPoolDataSource.

Package contents:
-----------------
src: source files (java files) under org.enhydra.jdbc
doc: the documentation of the project
archive: files to build xapool archive
externals: jar files need to compile source files
ext: external and useful files
examples: examples to test and show functionalities

1.6.beta:
---------
bug fixes:
- 300747 Null pointer in StandardPooledConnection due to missing log init
- 300774 NullPointerException in StandardXAPoolDataSource.toString()
- 301151 getGeneratedKeys not supported
- 303275 Problem with connection testing if test level is 3 or higher
- 303571 null pointer in multi thread.
- 303865 enum is a reserved word in JDK 1.5 and higher
- 304103 Problem with using Sybase JDBC driver in XAPool
- 304380 NullPointerException in GenericPool due to thread safety
- 304381 NullPointerException in PreparedStatementCache cleanupObject method
patchs:
- 303672 Fix for #303571
- 303673 Fix for #303462
- 304251 StandardXAConnectionHandle.close() too early release of Connection
- 304285 Fix for #304265: usage of StandardXA(Pool)DataSource with JNDI
- 305508 LRUCache has to check the old object before overwrite one

1.5.0:
-----
- #303280: XAPool does not cooperate with transaction manager when testing
  connections: this bug is fixed
- #303239: IllegalMonitorStateException in PoolKeeper: this bug is fixed
- #303191 (and #303192): unable to excute new connection after expiration: this bug is fixed
- #303302 StandardXADataSource.toString() fails with NullPointerException:
  this bug is fixed
- #300994: NullPointerException occurred when multi-thread access: this bug is
  fixed
- added jdbc 1.4 methods for Statement, PreparedStatement and
  CallableStatement objects
- added methods and fixed bugs about Statement and PreparedStatement objects
  which were not included into transactions 
- added the last version of JOTM (2.0.8), new version of Carol,
  commons-logging, jotm_jrmp_stubs. And added connector-1_5.jar and howl.jar

1.4.2:
------
- bug fix, contribution by Miro. Bug found with SAPdb in his
test suite.

1.4.1:
------
- bug fix for poolkeeper in while loop
- important submission from Tuomas Kantonen about
synchronisation problem into the pool
- Hanging tomcat problem, poolkeeper object was not properly created
- added methods in CoreStatement for jdk 1.4
- bug found by Miro about TM which was not yet initialized (worked into
  1.3.2), this bug has been fixed

1.3.3:
------
- changed license from EPL to LGPL

1.3.2:
------
- remove printStackTrace call in GenericPool class
- remove .error(...) calls, and replaced them by .debug(...)
- save autocommit status for CallableStatement object now
- restored autocommit after transaction (maybe a regression)
- restored autocommit for createStatement (never done)
- propagated the log object inside StandardConnectionPoolDataSource
- removed e.printStackTrace, and only use error level, with +e inside
  the message. 
- added toString() method on several objects, to dump internal
  states of XAPool's objects
- added log.debug message into close() method (StandardXAConnectionHandle)
- change the status of isReallyUsed in the close() method
  (StandardXAConnectionHandle)
- set up the autocommit flag in the close() method when autocommit 
  is false, and tx=null, just after the rollback call to the connection
  (StandardXAConnectionHandle)
- added a comment into CoreConnection object (setAutoCommit)
- removed unused code in StandardXAPreparedStatement and StandardXAStatement,
  preInvoke methods are no longer used.
- added toString methods on many objects to print out internal objects

1.3.1:
------
- added logging information
- changed Andy last name (sorry)
- made a lot of changes in jotmxapooltest by Andy
- added example HSQL config
- added sapdb example in spy.properties
- prepareStatement(String, int, int) was not getting associated
  with the transaction
- get the last version of jotm (1.4.3)

1.3:
----
- added comments in GenericPool
- added a lot of method comments
- fixed a bug with multithreaded process (was a regression)
- removed a wrong test, which made a bug, in XAConnection
- added 2 tests, a multithreaded test and a contribution from A. Zeneski
- added the last version of JOTM (1.4.3), and carol
- removed TraceTm uses with JOTM
- changed the XAPool way of log (log4j.properties)

1.2.2:
------
- removed the limit for used objects inside the same transaction (limit was 100)
- fixed a bug in the XADataSource object, to check if the current connection
  is not already in the freeConnection Vector (bug #319)
- fixed a bug when the pool size decreased and connection lifetime is
  reached. This bug appears when the pool activity is not important, and
  the number of used connections is lesser than the max size of the pool.
- switch to off the boolean status of the pool

1.2.1:
------
- added a console appender to the log4j configuration file, to 
  log with the complete name (org.enhydra.jdbc.xapool)
- fixed a bug when using multiple connection inside the same transaction
- changed the logger name xapool, and now use the complete name
  org.enhydra.jdbc.xapool

1.2:
----
- bug fixes in JNDI mechanism
- added logs with commons-logging
- added several examples

1.1:
----
This version includes major changes, and correct a lot of bugs. Main changes are:
- AutoCommit value is now saved before the begin of the transaction and restored
  to the commit or rollback of the transaction
- when no transaction is defined, and the AutoCommit flag sets to false, when the
  user calls close() on the connection, the connection is rollbacked
- fixed bug when the connection is opened before the begin of the transaction
  and close after the commit or rollback (connection enrolled a posteriori)
- fixed bugs in the cache prepared statement mechanism
- added commons logging to log inside XAPool
- added 5 more examples (tests) to demonstrate the XAPool functionalities

To do list:
-----------
- validate with 2.0 JDBC driver
- validate CallableStatement



