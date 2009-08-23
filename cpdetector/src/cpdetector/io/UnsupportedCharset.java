/*
 *  UnsupportedCharset.java  a dummy codepage for cpdetector's CodepageProcessor 
 *  executable.
 * 
 *  Copyright (C) Achim Westermann, created on 20.07.2004, 12:17:13  
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
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * <p>
 * A dummy charset that indicates an unknown charset. 
 * Nevertheless some indication of a encoding definition 
 * has been found that is contained here. The system just 
 * does not support that codepage. 
 * </p>
 * <p>
 *  These instances are obtained by the static singleton retrieval 
 *  call {@link #forName(String)} which allows unique instances for 
 *  a detected string.
 * </p>
 * @author <a href="mailto:Achim.Westermann@gmx.de">Achim Westermann</a>
 *
 */
public class UnsupportedCharset extends Charset
{

    private static Map singletons = new HashMap();
    
    private UnsupportedCharset(String name)
    {
        super(name, null);
        
    }
    
    public static Charset forName(String name){
      Charset ret = (Charset)singletons.get(name);
      if(ret == null){
        ret = new UnsupportedCharset(name);
        singletons.put(name,ret);
      }
      return ret;
    }
    

    /* (non-Javadoc)
     * @see java.nio.charset.Charset#contains(java.nio.charset.Charset)
     */
    public boolean contains(Charset cs)
    {
        return false;
    }

    /* (non-Javadoc)
     * @see java.nio.charset.Charset#newDecoder()
     */
    public CharsetDecoder newDecoder()
    {
      throw new UnsupportedOperationException("This is no real Charset but a flag you should test for!");
    }

    /* (non-Javadoc)
     * @see java.nio.charset.Charset#newEncoder()
     */
    public CharsetEncoder newEncoder()
    {
      throw new UnsupportedOperationException("This is no real Charset but a flag you should test for!");
    }
    
    

    /* (non-Javadoc)
     * @see java.nio.charset.Charset#displayName()
     */
    public String displayName()
    {
        return super.displayName();
    }

    /* (non-Javadoc)
     * @see java.nio.charset.Charset#displayName(java.util.Locale)
     */
    public String displayName(Locale locale)
    {
        return super.displayName(locale);
    }
  
}
