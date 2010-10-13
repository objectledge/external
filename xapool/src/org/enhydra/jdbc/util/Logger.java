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
package org.enhydra.jdbc.util;

import org.apache.commons.logging.Log;

import java.io.PrintWriter;

public class Logger extends PrintWriter{
    private Log log;

    public Logger(Log log) {
        super(new PrintWriter(System.err));
        this.log = log;
    }

    public void debug(Object o) {
        log.debug(o);
    }
    public void debug(Object o, Throwable t) {
        log.debug(o,t);
    }
    public void info(Object o) {
        log.info(o);
    }
    public void info(Object o, Throwable t) {
        log.info(o,t);
    }
    public void warn(Object o) {
        log.warn(o);
    }
    public void warn(Object o, Throwable t) {
        log.warn(o,t);
    }
    public void error(Object o) {
        log.error(o);
    }
    public void error(Object o, Throwable t) {
        log.error(o,t);
    }
    public void fatal(Object o) {
        log.fatal(o);
    }
    public void fatal(Object o, Throwable t) {
        log.fatal(o,t);
    }

}
