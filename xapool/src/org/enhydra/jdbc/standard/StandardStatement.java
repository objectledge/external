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

import java.sql.Statement;
import java.sql.SQLException;
import org.enhydra.jdbc.core.CoreStatement;

public class StandardStatement extends CoreStatement {
    
    public boolean closed;
    private StandardConnectionHandle con;
    
    /**
     * Constructor.
     */
    StandardStatement(
		      StandardConnectionHandle con,
		      Statement stat) {
	this.con = con;
	statement = stat;
    }
    
    StandardStatement() {
	super();
	System.out.println("StandardStatement: never used");
    }
    
    /**
     * Exception management : catch or throw the exception
     */
    public void catchInvoke(SQLException sqlException) throws SQLException {
	throw (sqlException);
    }

}