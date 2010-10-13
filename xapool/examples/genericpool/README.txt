#
#
# GenericPool example
#
#

Scenario:
---------

genericpool is a simple example to use the generic pool.
The generic pool can pool various objects and in the 
example, 'cars' are pooled !!!

Sources files:
--------------

- SimpleGenericPool.java: a simple example how to use GenericPool.java
- SimplePoolKeeper.java: is an object to create, test and destroy pooled objects
- Car.java: is object to pool

How to compile:
---------------

Go to ouput/dist/examples/genericpool

o Type

  $ ant compile

to compile the example

Run the example:
----------------
o Set the classpath

  $ export CLASSPATH=../../lib/xapool.jar:.:../../lib/commons-logging.jar:../../lib/commons-logging-api.jar

o Start the example:
  
  $ java SimpleGenericPool

Enjoy!

