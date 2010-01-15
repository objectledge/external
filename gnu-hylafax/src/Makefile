# gnu.hylafax Makefile
# $Id: Makefile,v 1.19 2002/07/13 03:54:18 jaiger Exp $
#
# $Author: jaiger $
#

# package version
VERSION=0.0.7

#
# Change these for your environment
#
JAVAC=javac
JAR=jar
JAVADOC=javadoc

FIND=/usr/bin/find
MV=/bin/mv
RM=/bin/rm
GREP=/bin/grep
LN=/bin/ln
TAR=/bin/tar
MKDIR=/bin/mkdir
#
# End of site-specific changes
#

all: jar docs

classes: gnu/hylafax/HylaFAXClient.class util

util: gnu/hylafax/util/SendFax.class gnu/hylafax/util/FaxStat.class

gnu/hylafax/util/SendFax.class: gnu/hylafax/util/SendFax.java
	$(JAVAC) gnu/hylafax/util/SendFax.java

gnu/hylafax/util/FaxStat.class: gnu/hylafax/util/FaxStat.java
	$(JAVAC) gnu/hylafax/util/FaxStat.java

gnu/hylafax/HylaFAXClient.class: gnu/hylafax/HylaFAXClient.java
	$(JAVAC) -deprecation gnu/hylafax/HylaFAXClient.java

gnu.hylafax.depend: classes
	$(FIND) . -name \*.class -print | $(GREP) -v "util/" >gnu.hylafax.depend

gnu.hylafax.util.depend: util
	$(FIND) gnu/hylafax/util/ -name \*.class -print >gnu.hylafax.util.depend

gnu-hylafax-$(VERSION).jar: gnu.hylafax.depend
	$(JAR) -cvf gnu-hylafax-$(VERSION).jar `cat gnu.hylafax.depend`

gnu-hylafax-util-$(VERSION).jar: gnu.hylafax.util.depend
	$(JAR) -cvf gnu-hylafax-util-$(VERSION).jar `cat gnu.hylafax.util.depend`

gnu-inet-ftp-$(VERSION).jar: gnu.inet.ftp.depend
	$(JAR) -cvf gnu-inet-ftp-$(VERSION).jar `cat gnu.inet.ftp.depend`

gnu.inet.ftp.depend: gnu/inet/ftp/FtpClient.class
	$(FIND) . -name \*.class -print >gnu.inet.ftp.depend

gnu/inet/ftp/FtpClient.class: gnu/inet/ftp/FtpClient.java
	$(JAVAC) -deprecation gnu/inet/ftp/FtpClient.java

jar: gnu-inet-ftp-$(VERSION).jar gnu-hylafax-$(VERSION).jar gnu-hylafax-util-$(VERSION).jar

dist-jar: jar
	if [ ! -d "../lib" ] ; then $(MKDIR) ../lib ; fi
	$(MV) gnu-hylafax-$(VERSION).jar ../lib/
	$(MV) gnu-hylafax-util-$(VERSION).jar ../lib/
	$(MV) gnu-inet-ftp-$(VERSION).jar ../lib/

dist: dist-jar docs clean
	cd .. ; $(FIND) . -print | $(GREP) -v CVS >./CONTENTS

docs:
	if [ ! -d ../docs/api ] ; then $(MKDIR) ../docs/api ; fi
	$(JAVADOC) -d ../docs/api/ gnu.hylafax gnu.hylafax.util gnu.inet.ftp

clean:
	$(FIND) . -name \*.class -exec $(RM) {} \;
	$(RM) -f *.jar
	$(RM) -f *.depend

dist-clean: clean
	$(FIND) .. -name \*.jar -exec $(RM) {} \;
	$(RM) -rf ../lib
	$(RM) -rf ../docs/api
	$(RM) -rf ../CONTENTS

# end makefile for gnu.hylafax
