/*
 * 
 *  ByteArrayInputStreamDebug.java  cpdetector
 *  Copyright (C) Achim Westermann, created on 19.10.2004, 22:04:36  
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
package cpdetector.util;

import java.io.IOException;
import java.io.InputStream;

/**
 * Prints out every character read. 
 *  Use as a proxy. Only needed for debugging the 
 * ANTLR Parser (ParsingDetector). Therefore the chronological 
 * order is preserved: sun's StreamDecoder.CharsetDS (nio) replaces 
 * InputStream.read() by buffer operations that fetch complete arrays. 
 * This is avoided by allowing to fetch only one char per method call.
 * 
 * @author <a href="mailto:Achim.Westermann@gmx.de">Achim Westermann</a>
 *
 */
public class InputStreamDebug extends InputStream{
  private InputStream delegate;
 
  public InputStreamDebug(InputStream delegate){
    this.delegate = delegate;
  }
  
  public synchronized int read() throws IOException {
    int ret = this.delegate.read();
    System.out.println((char)ret);
    return ret;
  }
  /**
   * 
   */
  public int available() throws IOException {
    return delegate.available();
  }
  /**
   * 
   */
  public void close() throws IOException {
    delegate.close();
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
  /**
   * 
   */
  public void mark(int readlimit) {
    delegate.mark(readlimit);
  }
  /**
   * 
   */
  public boolean markSupported() {
    return delegate.markSupported();
  }
  /**
   * 
   */
  public int read(byte[] b) throws IOException {
   return this.read(b,0,b.length);
  }
  /**
   * 
   */
  public int read(byte[] b, int off, int len) throws IOException {
    int ret = this.read();
    if(ret!=-1){
      b[off] = (byte) ret;
      ret = 1;
    }
    return ret;
  }
  /**
   * 
   */
  public void reset() throws IOException {
    delegate.reset();
  }
  /**
   * 
   */
  public long skip(long n) throws IOException {
    return delegate.skip(n);
  }
  /* (non-Javadoc)
   * @see java.lang.Object#toString()
   */
  public String toString() {
    return delegate.toString();
  }
}
