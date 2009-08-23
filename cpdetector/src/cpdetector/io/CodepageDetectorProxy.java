/*
 * Created on 03.06.2004
 *	
 */
package cpdetector.io;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * <p>
 * A proxy that delegate the codepage detection to all it's delegates. The first
 * one (added in code-order) that does not return a null {@link Charset}from
 * it's delegate method {@link #detectCodepage(URL)}wins the race and
 * determines the codpage of the document specified by the given URL.
 * </p>
 * <p>
 * If an underlying {@link ICodepageDetector}throws an
 * {@link java.io.IOException}, the delegation search will be terminated by
 * throwing this exception.
 * </p>
 * 
 * @author <a href="mailto:Achim.Westermann@gmx.de">Achim Westermann </a>
 *  
 */
public final class CodepageDetectorProxy extends AbstractCodepageDetector {
  /**
   * Singleton instance.
   */
  private static CodepageDetectorProxy instance = null;

  /**
   * The set of {@link ICodepageDetector}instances that this proxy will
   * delegate to. These instances will be invoked in order to find the codepage
   * until the first instance returns a valid codepage. If an
   * {@link IOException}is thrown the search will terminate early (assuming
   * that the execption is related to a general problem with the given URL.
   */
  private Set detectors = new  LinkedHashSet();

  /**
   * Singleton constructor. For internal use only.
   */
  private CodepageDetectorProxy() {
    super();
  }

  /**
   * Singleton retrieval method.
   *  
   */
  public static CodepageDetectorProxy getInstance() {
    if (instance == null) {
      instance = new CodepageDetectorProxy();
    }
    return instance;
  }

  /**
   * <p>
   * Adds the given instance to this proxie's detection capability. Remember
   * that the order of added ICodepageDetector instances is important for the
   * internal delegation (see class description).
   * </p>
   *  
   */
  public boolean add(ICodepageDetector detector) {
    return this.detectors.add(detector);
  }

  /**
   * @param url
   *          Should link to a file containing textual document. No check for
   *          images or other resources is made.
   * @throws IOException
   *           If a problem with the url - handling occurs.
   */
  public Charset detectCodepage(URL url) throws IOException {
     Charset ret = null;
     Iterator detectorIt = this.detectors.iterator();
     while (detectorIt.hasNext()) {
       ret = ((ICodepageDetector) detectorIt.next()).detectCodepage(url);
       if (ret != null) {
         if(ret != UnknownCharset.getInstance()){
           if(ret instanceof UnsupportedCharset){
             // TODO: Debug logging: found illegal charset tag or encoding declaration.
           }
           else{
             break;
           }
         }
       }
     }
     return ret;
  }

  /**
   * <p>
   * Detects the codepage by iteratively delegating the call 
   * to all internal {@link ICodepageDetector} instances added 
   * by {@link #add(ICodepageDetector)}.
   * </p>
   * <p>
   * The given InputStream has to support mark such that 
   * the call {@link InputStream#mark(int)} with argument length 
   * does not throw an exception. This is needed, as the stream 
   * has to be resetted to the beginning for each internal 
   * delegate that tries to detect.
   * </p>
   * <p>
   * If this is impossible (large documents), prefer using 
   * {@link #detectCodepage(URL)}.
   * </p>
   * @param in An InputStream for the document, that supports mark and a readlimit 
   * of argument length.
   * @param length The amount of bytes to take into account. This number shouls not 
   * be longer than the amount of bytes retrievable from the InputStream but should 
   * be as long as possible to give the fallback detection (chardet) more hints 
   * to guess. 
   * 
   * @see cpdetector.io.ICodepageDetector#detectCodepage(java.io.InputStream)
   */
  public Charset detectCodepage(InputStream in,int length) throws IOException {
    in.mark(length);
    Charset ret = null;
    Iterator detectorIt = this.detectors.iterator();
    while (detectorIt.hasNext()) {
      ret = ((ICodepageDetector) detectorIt.next()).detectCodepage(in,length);
      in.reset();
      if (ret != null) {
        if(ret != UnknownCharset.getInstance()){
          if(ret instanceof UnsupportedCharset){
            // TODO: Debug logging: found illegal charset tag or encoding declaration.
          }
          else{
            break;
          }
        }
      }
    }
    return ret;
  }
  public String toString() {
    StringBuffer ret = new StringBuffer();
    Iterator it = this.detectors.iterator();
    int i = 1;
    while (it.hasNext()) {
      ret.append(i);
      ret.append(") ");
      ret.append(it.next().getClass().getName());
      ret.append("\n");
      i++;
    }
    return ret.toString();
  }

}