/*
 * Created on 03.06.2004
 *	
 */
package cpdetector.io;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.charset.UnsupportedCharsetException;

import org.mozilla.intl.chardet.nsDetector;
import org.mozilla.intl.chardet.nsICharsetDetectionObserver;
import org.mozilla.intl.chardet.nsPSMDetector;

/**
 * <p>
 * A façade for jchardet codepage detection. <a href="http://www.i18nfaq.com/"
 * target="_blank">JChardet </a> is the java port of Frank Yung-Fong Tang's
 * Mozilla charset detector.
 * </p>
 * <p>
 * This charset detector works on guessing the codepage. <i>"The algorithm looks
 * into the byte sequence and based on the values of each byte uses a
 * elimination logic to narrow down to the final charset. If there is a tie
 * between EUC charsets, it uses the second logic to narrow down. This logic
 * uses the frequency statistics of characters in a given language." </i>( <a
 * href="http://www.i18nfaq.com/chardet.html#8">source of description </a>).
 * </p>
 * <p>
 * It is a singleton for performance reasons (buffer allocation). Because it is
 * stateful (internal buffer) the method
 * {@link #detectCodepage(InputStream, int)}(delegated to by
 * {@link #detectCodepage(URL)}has to be synchronized.
 * </p>
 * 
 * 
 * @author <a href="mailto:Achim.Westermann@gmx.de">Achim Westermann </a>
 *  
 */
public final class JChardetFacade extends AbstractCodepageDetector implements nsICharsetDetectionObserver {
  private static JChardetFacade instance = null;

  private static nsDetector det;

  private static byte[] buf = new byte[4096];

  private Charset codpage = null;
  
  private boolean guessing = true;
  
  private int amountOfVerifiers =0;

  /**
   *  
   */
  private JChardetFacade() {
    super();
    det = new nsDetector(nsPSMDetector.ALL);
    det.Init(this);
    this.amountOfVerifiers = det.getProbableCharsets().length;
  }

  public static JChardetFacade getInstance() {
    if (instance == null) {
      instance = new JChardetFacade();
    }
    return instance;
  }

  /**
   * 
   * @param url
   *          Should link to a file containing textual document. No check for
   *          images or other resources is made.
   * @throws IOException
   *           If a problem with the url - handling occurs.
   */
  public Charset detectCodepage(URL url) throws IOException {
    return this.detectCodepage(new BufferedInputStream(url.openStream()), Integer.MAX_VALUE);
  }

  /*
   * (non-Javadoc)
   * 
   * @see cpdetector.io.ICodepageDetector#detectCodepage(java.io.InputStream)
   */
  public synchronized Charset detectCodepage(InputStream in, int length) throws IOException {
    this.Reset();
    int len;
    int read = 0;
    boolean done = false;
    boolean isAscii = true;
    Charset ret = null;
    do {
      len = in.read(buf, 0, Math.min(buf.length, length - read));
      if (len > 0) {
        read += len;
      }
      if (!done)
        done = det.DoIt(buf, len, false);
    } while (len != -1 && !done);
    det.DataEnd();
    if (this.codpage == null) {
      if(this.guessing){
        ret = guess();
      }
      else{
        ret = UnknownCharset.getInstance();
      }
    }
    else {
      ret = this.codpage;
    }
    return ret;

  }

  /**
   * 
   */
  private Charset guess() {
    Charset ret = null;
    //TODO: remove output.
    String[] possibilities = det.getProbableCharsets(); 
    System.out.println("Possible Charsets: ");
    for(int i=0;i<possibilities.length;i++){
      System.out.println(possibilities[i]);
    }
    /*
     * Detect US-ASCII by the fact, that no exclusion 
     * of any Charset was possible.
     */
    if(possibilities.length == this.amountOfVerifiers){
      ret = Charset.forName("US-ASCII");
    }
    else{
      // He should better return an Array of length zero!
      String check = possibilities[0];
      if(check.equalsIgnoreCase("nomatch")){
        ret = UnknownCharset.getInstance();
      }
      else{
        for(int i=0;ret == null && i<possibilities.length;i++){
          try{
          ret = Charset.forName(possibilities[i]);         
          }catch(UnsupportedCharsetException uce){
            ret = UnsupportedCharset.forName(possibilities[i]);
          }
        }
      }
    }
    return ret;

  }

  /*
   * (non-Javadoc)
   * 
   * @see org.mozilla.intl.chardet.nsICharsetDetectionObserver#Notify(java.lang.String)
   */
  public void Notify(String charset) {
    this.codpage = Charset.forName(charset);
  }

  public void Reset() {
    det.Reset();
    this.codpage = null;
  }
  /**
   * @return Returns the guessing.
   */
  public boolean isGuessing() {
    return guessing;
  }
  /**
   * <p>
   * If it was impossible to narrow down possible results to one, 
   * an internal set of possible character encodings exists. 
   * By setting guessing to true, the call to {@link #detectCodepage(InputStream, int)} 
   * and {@link #detectCodepage(URL)} will return an arbitrary 
   * possible Charset.  
   * </p>
   * <p>
   *  Currently the following precedence is implemented to choose the 
   *  possible Charset: 
   *  <ol>
   *   <li>
   *   If US-ASCII is possible, it is chosen. 
   *   <li>
   *   If US-ASCII is not possible, the first supported  one in the set of possible 
   *   charsets is returned. No information about the semantics of the order 
   *   in that list is available. If no possibility is supported, 
   *   an instance of {@link UnsupportedCharset} is returned.
   *  </ol>
   *   ASCII indeed is never detected as possible: No internal 
   *   verifier exists for ASCII, as all Charsets support ASCII. The 
   *   possibility of ASCII is detected, when no Charset has been excluded: 
   *   The amount of possible Charsets is equal to the amount of all 
   *   detectable Charsets.  
   * </p>
   * 
   * @param guessing The guessing to set.
   */
  public synchronized void setGuessing(boolean guessing) {
    this.guessing = guessing;
  }
}