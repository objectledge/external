/*
 * 
 *  AbstractCharsetdetector.java, provides the default implementation 
 *  for the high-level codepage detection method of interface ICodepageProcessor.
 * 
 *  Copyright (C) Achim Westermann, created on 19.07.2004, 21:45:47  
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
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.nio.charset.Charset;

/**
 * <p>
 * This implementation provides the default implementation 
 * for the high-level codepage detection method {@link #open(URL)} 
 * of the implemented interface ICodepageProcessor.
 * </p>
 * <p>
 * Also the Comparable interface implementation is provided here by 
 * comparing the class-name strings of the implementations.
 * </p>
 * 
 * @author <a href="mailto:Achim.Westermann@gmx.de">Achim Westermann</a>
 *
 */
public abstract class AbstractCodepageDetector implements ICodepageDetector
{
    /**
     * 
     */
    public AbstractCodepageDetector()
    {
        super();
        // TODO Auto-generated constructor stub
    }

 	
	/** 
	 * A default delegation to {@link #detectCodepage(URL)} 
	 * that opens the document specified by the given URL with 
	 * the detected codepage.
	 * 
	 * @see cpdetector.io.ICodepageDetector#open(java.net.URL)
	 */
	public final Reader open(URL url) throws IOException
	{
		Reader ret = null;
		Charset cs = this.detectCodepage(url);
		if(cs != null){
			ret = new InputStreamReader(
				new BufferedInputStream(url.openStream()),
				cs
			);
		}
		return ret;
	}
    /* (non-Javadoc)
     * @see java.lang.Comparable#compareTo(java.lang.Object)
     */
    public int compareTo(Object o)
    {
    	String other = o.getClass().getName();
    	String mine = this.getClass().getName();
    	return mine.compareTo(other);
    }
}
