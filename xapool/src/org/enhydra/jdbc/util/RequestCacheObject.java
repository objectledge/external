package org.enhydra.jdbc.util;

import java.util.regex.Pattern;

import org.enhydra.jdbc.util.Logger;
import org.apache.commons.logging.impl.Log4JLogger;
import org.apache.commons.logging.Log;

/**
 * one RequestCacheObject for one sql request found in the 
 * configuration file
 */
public class RequestCacheObject {

    // the sql request (sql or pattern)
    private String request_ = null;

    // time is the beginning of the first ResulSet computing
    private long time_ = 0;

    // the Object to cache
    private Object result_ = null;

    // pattern, compiled from the configuration source file
    private Pattern pattern_ = null;

    // the current object must be live timeToLive_
    private long timeToLive_ = 0;

    private static Logger logger;

    public RequestCacheObject(long ttl) {
	logger = new Logger((Log)(new Log4JLogger("org.enhydra.jdbc.util")));
	timeToLive_ = ttl;
    }
    
    public RequestCacheObject(String req, Pattern pattern, long ttl) {
	logger = new Logger((Log)(new Log4JLogger("org.enhydra.jdbc.util")));
	logger.debug("RequestCacheObject:RequestCacheObject req=<"+req+"> ttl=<"+ttl+">");
	request_ = req;
	pattern_ = pattern;
	timeToLive_ = ttl;
    }

    public void setResult(Object rset) {
	logger.debug("RequestCacheObject:setResult for=<"+request_+">");
	result_ = rset;
	time_= System.currentTimeMillis();
    }
    

    public boolean isAlive() {
	long now = System.currentTimeMillis();
	if (now - time_ > timeToLive_) {
	    logger.debug("RequestCacheObject:isAlive out of live");
	    return false;
	} else {
	    if (result_ == null) {
		logger.debug("RequestCacheObject:isAlive Object is null ");
		return false;
	    } else {
		logger.debug("RequestCacheObject:isAlive Object is not null");
		return true;
	    }
	}
    }

    public Object getResult() {
	if (result_ == null)
	    logger.debug("RequestCacheObject:getResult result_ is null");

	return result_;
    }

    public Pattern getPattern() {
	return pattern_;
    }

    public String getRequest() {
	return request_;
    }
    
    public String toString() {
	StringBuffer sbuf = new StringBuffer();
	sbuf.append("request=<" + request_ +
		    "> time=<"+ time_ +
		    "> time to live=<"+ timeToLive_ + 
		    "> pattern=<"+ pattern_.pattern() +">");
	return sbuf.toString();
    }
    
    public void close() {
	request_ = null;
	/* TBD
	   try {
	   result_.close();
	   } catch (Exception e) {
	    e.printStackTrace();
	    }
	*/
	result_ = null;
	pattern_ = null;
    }
}
