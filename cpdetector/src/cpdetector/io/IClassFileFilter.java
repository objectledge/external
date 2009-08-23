/*
 * 
 *  IClassFileFilter.java  cpdetector
 *  Copyright (C) Achim Westermann, created on 28.10.2004, 12:27:19  
 *
 *  This program is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation; either version 2 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program; if not, write to the Free Software
 *  Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 *  
 *  If you modify or optimize the code in a useful way please let me know.
 *  Achim.Westermann@gmx.de
 *	
 */
package cpdetector.io;

import java.io.FileFilter;

/**
 * <p>
 * An acceptance filter based on classfiles. Implementations 
 * may use filtering criteria as inheritance or pattern matching 
 * upon class names (eg. for packages, substrings like "test") and 
 * any information provided by a {@link java.lang.Class} instance.
 * </p>
 * @see java.io.FileFilter
 * @author <a href="mailto:Achim.Westermann@gmx.de">Achim Westermann</a>
 *
 */
public interface IClassFileFilter extends FileFilter {
  public boolean accept(Class c);
}
