/*
 * XAPool: Open Source XA JDBC Pool
 * Copyright (C) 2003 Objectweb.org
 * Initial Developer: Lutris Technologies Inc.
 * Contact: xapool-public@lists.debian-sf.objectweb.org
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307
 * USA
 */
package org.enhydra.jdbc.standard;

import java.sql.SQLException;

public class StandardXAPreparedStatement extends StandardPreparedStatement {

	private StandardXAConnectionHandle con;
	// the StandardXAConnectionHandle that created this object
	public String sql;
	public int resultSetType;
	public int resultSetConcurrency;
        public int resultSetHoldability;
        public int autoGeneratedKeys;

	StandardXAPreparedStatement(
		StandardXAConnectionHandle con_,
		String sql_,
		int resultSetType_,
		int resultSetConcurrency_,
		int resultSetHoldability_)
		throws SQLException {
		this.con = con_;
		this.sql = sql_;
		this.key = sql_ + resultSetType_ + resultSetConcurrency_ + resultSetHoldability_;
		this.resultSetType = resultSetType_;
		this.resultSetConcurrency = resultSetConcurrency_;
		this.resultSetHoldability = resultSetHoldability_;

		log = con_.log;
		log.debug(
			"StandardXAPreparedStatement: Create an XAPreparedStatement with sql='"
				+ sql
				+ "'");

		key =
			sql
				+ resultSetType
				+ resultSetConcurrency
		                + resultSetHoldability
				+ ((con.tx != null) ? true : false);
		ps =
			con.checkPreparedCache(
				sql,
				resultSetType,
				resultSetConcurrency,
                                resultSetHoldability,
				key);
		// from cney
		// ps = con.checkPreparedCache(sql, resultSetType, resultSetConcurrency,key);
	}

	StandardXAPreparedStatement(
		StandardXAConnectionHandle con_,
		String sql_,
		int autoGeneratedKeys_)
		throws SQLException {
		this.con = con_;
		this.sql = sql_;
		this.key = sql_ + autoGeneratedKeys_;
		this.autoGeneratedKeys= autoGeneratedKeys_;

		log = con_.log;
		log.debug(
			"StandardXAPreparedStatement: Create an XAPreparedStatement with sql='"
				+ sql
				+ "'");

		key =
			sql
				+ autoGeneratedKeys
				+ ((con.tx != null) ? true : false);
		ps =
			con.checkPreparedCache(
				sql,
				autoGeneratedKeys,
				key);
		// from cney
		// ps = con.checkPreparedCache(sql, resultSetType, resultSetConcurrency,key);
	}


	/**
	 * Close this statement.
	 */
	public synchronized void close() throws SQLException {
		log.debug(
			"StandardXAPreparedStatement:close the XA prepared statement");
		// Note no check for already closed - some servers make mistakes
		closed = true;
		if (con.preparedStmtCacheSize == 0) {
			log.debug(
				"StandardXAPreparedStatement:close preparedStmtCacheSize == 0");
			if (ps != null) {
				ps.close(); // no cache, so we can close
			}
		} else {
			log.debug(
				"StandardXAPreparedStatement:close preparedStmtCacheSize="
					+ "'"
					+ con.preparedStmtCacheSize
					+ "'");
			con.returnToCache(key);
			// return the underlying statement to the cache
		}
	}


	/**
	 * Exception management : catch or throw the exception
	 */
	public void catchInvoke(SQLException sqlException) throws SQLException {
		//ConnectionEvent event = new ConnectionEvent (con.pooledCon);
		//con.pooledCon.connectionErrorOccurred(event);
		throw (sqlException);
	}

}