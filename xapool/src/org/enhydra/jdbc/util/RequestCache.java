package org.enhydra.jdbc.util;

import java.util.Hashtable;
import java.util.Enumeration;
import java.io.FileInputStream;
import java.io.DataInputStream;
import java.util.regex.Pattern;
import java.util.StringTokenizer;

import org.enhydra.jdbc.util.Logger;

import org.apache.commons.logging.LogFactory;


public class RequestCache {
	// the cache objects
	// there are only the requests found in the configuration file
	private static Hashtable cache_ = null;

	private static Hashtable dsCache_ = null;

	// all requests , given by the application
	// be careful, this object may be large, but with a minimum of
	// object size (only String objects stored in the value part)
	private static Hashtable requests_ = null;

	// the current singleton object
	private static RequestCache theCache_ = null;

	private long timeToLive_ = 86400000; // 1 day

	private String path = null;

	private static Logger logger;

	/**
	 * This method is used to obtain the cache, if it does not exist, it creates
	 * it
	 */
	public static synchronized RequestCache getInstance() {
		if (theCache_ == null) {
			theCache_ = new RequestCache();
		}
		return theCache_;
	}

	public static synchronized RequestCache getInstance(String path) {
		if (theCache_ == null) {
			theCache_ = new RequestCache(path);
		}
		return theCache_;
	}

	public RequestCache() {
		if (logger == null)
			logger = new Logger(LogFactory.getLog("org.enhydra.jdbc.util"));

		if (cache_ == null)
			cache_ = new Hashtable();

		if (requests_ == null)
			requests_ = new Hashtable();

		if (dsCache_ == null)
			dsCache_ = new Hashtable();

		readConfigurationFile();
	}

	public RequestCache(String path) {
		if (cache_ == null)
			cache_ = new Hashtable();

		if (requests_ == null)
			requests_ = new Hashtable();

		this.path = path;

		readConfigurationFile();
	}

	public void readConfigurationFile() {
		if (path != null) {
			try {
				logger
						.debug("RequestCache: readConfigurationFile try to open file="
								+ path);
				FileInputStream fstream = new FileInputStream(path);
				DataInputStream in = new DataInputStream(fstream);
				logger
						.debug("RequestCache: readConfigurationFile open done file="
								+ path);

				while (in.available() != 0) {
					String req = ((String) in.readLine()).trim();
					logger.debug("RequestCache:readConfigurationFile line:"
							+ req);
					if (!req.startsWith("#")) {
						long ttl = timeToLive_;
						String newreq = null;
						if (req.startsWith("ttl")) {
							StringTokenizer st = new StringTokenizer(req);
							String ttls = st.nextToken();
							logger
									.debug("RequestCache:readConfigurationFile ttls:<"
											+ ttls + ">");
							ttl = Integer.parseInt(ttls.substring(4));
							newreq = req.substring(ttls.length()).trim();
						} else
							newreq = req;

						Pattern p = Pattern.compile(newreq);
						logger
								.debug("RequestCache:readConfigurationFile newreq:<"
										+ newreq + ">");
						logger.debug("RequestCache:readConfigurationFile ttl:<"
								+ ttl + ">");
						RequestCacheObject uco = new RequestCacheObject(newreq,
								p, ttl);
						cache_.put(newreq, uco);
					}
				}

				in.close();
			} catch (Exception e) {
				System.err.println("File input error");
				e.printStackTrace();
			}
		}
	}

	public boolean isInCache(String req) {
		if (cache_ != null)
			if (cache_.size() == 0)
				return false;

		if ((cache_ == null) || (requests_ == null) || (req == null)
				|| (req.compareTo("") == 0)) {
			return false;
		}

		// optimization for the real life, all application requests
		// may be found in the requests object, and we need to save time
		String redir = (String) requests_.get(req);
		if (redir != null) {
			if (redir.compareTo("null") != 0) {
				// logger.debug("RequestCache:isInCache requests_
				// contains(req)");
				return ((RequestCacheObject) (cache_.get(redir))).isAlive();
			} else
				// this is a non-cacheable request
				return false;

		} else {

			// if the cache contains the sql request
			if (cache_.containsKey(req)) {
				// logger.debug("RequestCache:isInCache contains(req)");
				return ((RequestCacheObject) (cache_.get(req))).isAlive();
			} else {
				// if the cache does not contain the sql request
				// we are going to test all items in cache to test the pattern
				for (Enumeration e = cache_.keys(); e.hasMoreElements();) {
					RequestCacheObject uco = (RequestCacheObject) (cache_.get(e
							.nextElement()));
					if (uco.getPattern().matcher(req).matches()) {
						// if it matches, return if the RequestCacheObject is
						// alive
						// logger.debug("RequestCache:isInCache
						// contains(pattern)");
						return uco.isAlive();
					}
				}
			}
		}

		return false;
	}

	public synchronized void setResult(String req, Object rset) {

		// if the cache exists and the sql request (or pattern) is not null
		if ((cache_ != null) && (req != null)) {
			// first remove the RequestCacheObject to the cache
			RequestCacheObject uco = (RequestCacheObject) (cache_.remove(req));
			uco.setResult(rset);
			// second, add the new changed object
			cache_.put(req, uco);
		}
	}

	public Object getResult(String req) {

		// if the cache exists and it contains the pattern or sql request
		if (cache_ != null) {
			// return the stored object
			String link = (String) (requests_.get(req));
			if (link == null) {
				// logger.debug("RequestCache:getResult link is null");
				String rsql = getSqlPattern(req);
				RequestCacheObject uco = (RequestCacheObject) (cache_.get(rsql));

				return uco;
			}
			RequestCacheObject uco = (RequestCacheObject) (cache_.get(link));
			if (uco == null) {
				// logger.debug("RequestCache:getResult uco is null");
				return null;
			}
			Object obj = uco.getResult();

			if (obj != null)
				return obj;

		}
		return null;
	}

	public String getSqlPattern(String sql) {
		// try to parse all the cache to determine if the sql statement matches
		// the defined pattern
		if (cache_ != null)
			if (cache_.size() == 0)
				return null;

		for (Enumeration e = cache_.keys(); e.hasMoreElements();) {
			RequestCacheObject uco = (RequestCacheObject) (cache_.get(e
					.nextElement()));

			// if it matches, we return it
			if (uco.getPattern().matcher(sql).matches()) {
				// logger.debug("RequestCache:getSqlPattern yes, it matches
				// req=<"+sql+">");
				return uco.getRequest();
			}
		}
		return null;
	}

	/**
	 * make the link between a application request (sql) and the pattern found
	 * in the key of the cache_ object
	 * 
	 * @param pattern :
	 *            may be "null" String
	 */
	public void setLink(String sql, String pattern) {

		if (cache_ != null)
			if (cache_.size() != 0)
				if ((requests_ != null) && (sql != null) && (pattern != null)) {
					requests_.put(sql, pattern);
				}
	}

	public synchronized void reset() {
		// we need to remove all elements in cache
		for (Enumeration e = cache_.keys(); e.hasMoreElements();) {
			RequestCacheObject uco = (RequestCacheObject) (cache_.get(e
					.nextElement()));
			uco.close();
			uco = null;
		}

		// and now, to create again all elements from the configuration file
		cache_.clear();
		cache_ = null;
		cache_ = new Hashtable();
		requests_.clear();
		requests_ = null;
		requests_ = new Hashtable();
		readConfigurationFile();
	}

	public String toString() {
		StringBuffer sbuf = new StringBuffer();
		sbuf.append("=== DUMP REQUESTS ===\n");
		for (Enumeration e = requests_.keys(); e.hasMoreElements();) {
			String s = (String) e.nextElement();
			sbuf.append("key=<" + s + "> value=<" + (String) requests_.get(s)
					+ ">\n");
		}
		sbuf.append("=== DUMP CACHE ===\n");
		for (Enumeration e = cache_.keys(); e.hasMoreElements();) {
			String s = (String) e.nextElement();
			RequestCacheObject uco = (RequestCacheObject) cache_.get(s);
			sbuf.append("key(pattern)=<" + s + "> value(RequestCacheObject)=<"
					+ uco.toString() + ">\n");
		}
		return sbuf.toString();
	}
}
