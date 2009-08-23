/*
 * 
 *  HTMLCodepageDetector.java, a facade to an ANTLR grammar based 
 *  parser / lexer that searches for the "charset" attribute of a 
 *  html page.
 *  Copyright (C) Achim Westermann, created on 20.07.2004, 10:35:46  
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

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.charset.Charset;

/**
 * 
 * <p>
 * <b>
 * This class has been replaced by {@link cpdetector.io.ParsingDetector} 
 * and only exists for backward-compatibility.
 * </b>
 * The name simply would not match any more, as parsing 
 * is not limited to html (1.1 includes xml as well).
 * New code should stick to the replacement. 
 * This class has been modified with version 1.1 and now 
 * is delegating all calls to an instance of the replacement 
 * class (5 minutes with eclipse and a common interface), which 
 * introduces a small overhead (minimal, as one invokevirtual 
 * is nothing compared to codepage detection by parsing). 
 * </p>
 * 
 * <p>
 * Documentation may be found in the class {@link cpdetector.io.ParsingDetector}. 
 * It is valid for this class.
 * </p>
 * 
 * @version 1.1
 * @deprecated This class should not be used in new code. It has been 
 * replaced by {@link cpdetector.io.ParsingDetector}. Future versions may 
 * drop this class.
 * @see ParsingDetector
 * @author <a href="mailto:Achim.Westermann@gmx.de">Achim Westermann </a>
 *  
 */
public class HTMLCodepageDetector extends AbstractCodepageDetector {
  /**
   * Deprecation support. Instances of this 
   * class stick to the replacement.
   */
  private ParsingDetector delegate;
  
  public HTMLCodepageDetector() {
    this(false);
  }

  public HTMLCodepageDetector(boolean verbose) {
    super();
    this.delegate = new ParsingDetector(verbose);
  }
  /* (non-Javadoc)
   * @see cpdetector.io.AbstractCodepageDetector#compareTo(java.lang.Object)
   */
  public int compareTo(Object o) {
    return delegate.compareTo(o);
  }
  /**
   * 
   */
  public Charset detectCodepage(InputStream in, int length) throws IOException {
    return delegate.detectCodepage(in, length);
  }
  /**
   * 
   */
  public Charset detectCodepage(URL url) throws IOException {
    return delegate.detectCodepage(url);
  }
  /* (non-Javadoc)
   * @see java.lang.Object#equals(java.lang.Object)
   */
  public boolean equals(Object obj) {
    return delegate.equals(obj);
  }
  /* (non-Javadoc)
   * @see java.lang.Object#hashCode()
   */
  public int hashCode() {
    return delegate.hashCode();
  }
  /* (non-Javadoc)
   * @see java.lang.Object#toString()
   */
  public String toString() {
    return delegate.toString();
  }
}