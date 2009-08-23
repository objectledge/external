/*
 * 
 *  ASCIIDetector.java, a simple implementation for cpdetector, 
 *  that may be used to detect plain ASCII.
 *  Copyright (C) Achim Westermann, created on 18.10.2004, 12:56:45  
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

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.charset.Charset;
import cpdetector.util.FileUtil;

/**
 * <p>
 * A simple detector that may be used to detect plain ASCII.
 * This instance should never be used as the first strategy of the 
 * {@link cpdetector.io.CodepageDetectorProxy}: Many different encodings 
 * are multi-byte and may be verified to be ASCII by this instance, because 
 * all their bytes are in the range from 0x00 to 0x7F. 
 * </p>
 * <p>
 *  It is recommended to use this as a fall-back, if all different strategies 
 *  (e.g. {@link cpdetector.io.JChardetFacade}, {@link cpdetector.io.ParsingDetector}) 
 *  fail. This is most often the case for ASCII data, as guessing and exclusion 
 *  based on the content is especially hard for ASCII: almost all character sets 
 *  define the ASCII range (compatibility). Therefore this is a good fall-back. 
 * </p>
 * <p>
 *  It is a singleton  for performance-reasons:
 *  The constructor is private. Use {@link #getInstance()} or 
 * <code>
 * {@link cpdetector.reflect.SingletonLoader#getInstance()} 
 * and
 * {@link cpdetector.reflect.SingletonLoader#newInstance(String)}
 * on the result.
 * </code>
 * </p>
 * 
 * @author <a href="mailto:Achim.Westermann@gmx.de">Achim Westermann</a>
 *
 */
public class ASCIIDetector extends AbstractCodepageDetector{
  private static ICodepageDetector instance;
  /**
   * Singleton constructor
   */
  private ASCIIDetector() {
    super();
    // TODO Auto-generated constructor stub
  }
  
  public static ICodepageDetector getInstance(){
    if(instance==null){
      instance = new ASCIIDetector();
    }
    return instance;
  }



  /* (non-Javadoc)
   * @see cpdetector.io.ICodepageDetector#detectCodepage(java.io.InputStream, int)
   */
  public Charset detectCodepage(InputStream in, int length) throws IOException {
    Charset ret = UnknownCharset.getInstance();
    if(!(in instanceof BufferedInputStream)){
      in = new BufferedInputStream(in,4096);
    }
    if(FileUtil.isAllASCII(in)){
      ret = Charset.forName("US-ASCII");
    }
    return ret;

  }
  /* (non-Javadoc)
   * @see cpdetector.io.ICodepageDetector#detectCodepage(java.net.URL)
   */
  public Charset detectCodepage(URL url) throws IOException {
    return this.detectCodepage(new BufferedInputStream(url.openStream()), Integer.MAX_VALUE);
  }
 
}
