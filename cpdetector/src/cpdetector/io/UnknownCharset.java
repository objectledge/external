/*
 * 
 *  UnknownCharset.java  cpdetector
 *  Copyright (C) Achim Westermann, created on 26.09.2004, 03:15:15  
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

import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CharsetEncoder;

/**
 * 
 * <p>
 * A singleton Charset indicating that no encoding could be detected 
 * at all (regardless wether supported by platform or not). 
 * Unlike the {@link UnsupportedCharset} singleton instances it will 
 * never equal anything. 
 * </p>
 * @author <a href="mailto:Achim.Westermann@gmx.de">Achim Westermann</a>
 *
 */
public class UnknownCharset extends Charset {

  private static Charset instance;
  /**
   * @param canonicalName
   * @param aliases
   */
  private UnknownCharset() {
    super("void",null);
  }
  
  public static Charset getInstance(){
    if(instance==null){
      instance = new UnknownCharset();
    }
    return instance;
  }

  /* (non-Javadoc)
   * @see java.nio.charset.Charset#contains(java.nio.charset.Charset)
   */
  public boolean contains(Charset cs) {
    return false;
  }

  /* (non-Javadoc)
   * @see java.nio.charset.Charset#newDecoder()
   */
  public CharsetDecoder newDecoder() {
   throw new UnsupportedOperationException("This is no real Charset but a flag you should test for!");
  }

  /* (non-Javadoc)
   * @see java.nio.charset.Charset#newEncoder()
   */
  public CharsetEncoder newEncoder() {
    throw new UnsupportedOperationException("This is no real Charset but a flag you should test for!");
  }
  
  // workaround for JDK 1.4 level compiler
  public int compareTo(Object object) {
	return super.compareTo((Charset)object);  
  }
}
