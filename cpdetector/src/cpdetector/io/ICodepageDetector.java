/*
 * 
 *  ICodepageDetector.java  cpdetector
 *  Copyright (C) Achim Westermann, created on 19.07.2004, 20:13:44  
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
import java.io.Reader;
import java.io.Serializable;
import java.net.URL;
import java.nio.charset.Charset;

/**
 * @author <a href="mailto:Achim.Westermann@gmx.de">Achim Westermann </a>
 *  
 */
public interface ICodepageDetector extends Serializable, Comparable {
   /**
    * <p>
    * High level method to open documents in the correct codepage.
    * Implementations of this method should delegate to the low-level method
    * {@link #detectCodepage(URL)}.
    * </p>
    * <p>
    * Detect the codepage of the document pointed to by the URL argument. If the
    * codepage could not be detected, null has to be returned. If the given URL
    * does not point to a document or it is not possible to open the document
    * specified by the given URL, an IOException is thrown.
    * </p>
    * 
    * @exception IOException
    *               thrown to indicate that it is was not possible to open the
    *               document specified by the given URL.
    * @return null, if the codepage of the document specified by the given URL
    *         was not detected or a Reader that reads the document in the
    *         detected codepage.
    *  
    */
   public Reader open(URL url) throws IOException;

   /**
    * <p>
    * Low-level method that detects the codepage (charset) of the document
    * specified by the given URL.
    * </p>
    * 
    * @exception IOException
    *               thrown to indicate that it is was not possible to open the
    *               document specified by the given URL.
    * @return null, if the codepage of the document specified by the given URL
    *         was not detected or the {@link java.io.Charset}that represents
    *         the document's codepage.
    *  
    */
   public Charset detectCodepage(URL url) throws IOException;

   /**
    * <p>
    * This method allows to detect the charset encoding from every source (even
    * a String, which an URL does not decorate!).
    * </p>
    * <p>
    * Note that you cannot reuse the given InputStream unless it supports 
    * marking ({@link InputStream#markSupported()} == true), you mark the 
    * initial position with a sufficient readlimit and invoke reset afterwards 
    * (without getting any exception).
    * </p>
    * 
    * @param in
    *           An InputStream for the document, that supports mark and a
    *           readlimit of argument length.
    * @param length
    *           The amount of bytes to take into account. This number should not
    *           be longer than the amount of bytes retrievable from the
    *           InputStream but should be as long as possible to give the
    *           fallback detection (chardet) more hints to guess. 
    */
   public Charset detectCodepage(InputStream in, int length) throws IOException;
}