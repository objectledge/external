/*
 * 
 *  Encoding.java, a facade to an ANTLR grammar based 
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

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.Charset;

import antlr.ANTLRException;
import cpdetector.io.parser.EncodingLexer;
import cpdetector.io.parser.EncodingParser;

/**
 * <p>
 * A <a target="_blank" href="http://wiki.cs.uiuc.edu/PatternStories/FacadePattern">Façade</a> 
 * that internally uses an 
 * <a href="http://www.antlr.org">ANTLR </a>- based parser / lexer.<br>
 * </p>
 * <p>
 * The underlying lexer is more a filter: It does not verify lexical correctness
 * by the means of matching a defined order of tokens, but just filters out
 * certain tokens. By now the following tokens are filtered: 
 * <table border="0" id="userstyle">
 *  <colgroup>
 *    <col width="20%"> 
 *    <col width="30%"> 
 *    <col width="8%"> 
 *    <col width="42%"> 
 *  </colgroup>
 *  <tr>
 *    <th>Token Name</th>
 *    <th>Match</th>
 *    <th>Lang.</th>
 *    <th>Specification</th>
 *  </tr>
 *  <tr>
 *    <td><em>META_CONTENT_TYPE</em></td>
 *    <td>
 *      <tt>"meta" "http-equiv" "=" '"Content-Type"' "content" "=" '"' IDENTIFIER "charset" "=" <b>&lt;EncName&gt;</b> '"'></tt>
 *    </td>
 *    <td>HTML</td>
 *    <td>
 *      <a href="http://www.w3.org/TR/1999/REC-html401-19991224/charset.html#h-5.2.2" target="_blank">
 *      W3C HTML 4.01 Specification
 *      </a>
 *      Chapter 5.2.2
 *    </td>
 *  </tr>
 *  <tr>
 *    <td>
 *      <em>XML_ENCODING_DECL</em>
 *    </td>
 *    <td>
 *      <tt>"&lt;?xml" VersionInfo  "encoding" "=" <b>&lt;EncName&gt;</b></tt>
 *    </td>
 *    <td>
 *     XML
 *    </td>
 *    <td>
 *      <a target="_blank" href="http://www.w3.org/TR/2004/REC-xml-20040204/#sec-prolog-dtd">
 *      Extensible Markup Language (XML) 1.0 (Third Edition)</a>
 *      Chapter 2.8
 *    </td>
 *  </tr>
 * </table>
 * </p>
 * 
 * @author <a href="mailto:Achim.Westermann@gmx.de">Achim Westermann </a>
 *  
 */
public class ParsingDetector extends AbstractCodepageDetector {
   private boolean verbose = false;

   public ParsingDetector() {
      this(false);
   }

   public ParsingDetector(boolean verbose) {
      super();
      this.verbose = verbose;
   }

   /*
    * (non-Javadoc)
    * 
    * @see aw.io.ICodepageDetector#detectCodepage(java.net.URL)
    */
   public Charset detectCodepage(URL url) throws IOException {
      return this.detectCodepage(new BufferedInputStream(url.openStream()), Integer.MAX_VALUE);
   }

   /*
    * (non-Javadoc)
    * 
    * @see cpdetector.io.ICodepageDetector#detectCodepage(java.io.InputStream)
    */
   public Charset detectCodepage(InputStream in, int length) throws IOException {
      EncodingLexer lexer;
      EncodingParser parser;
      Charset charset = null;
      String csName = null;
      if (this.verbose) {
         System.out.println("  parsing for html-charset/xml-encoding attribute with codepage: US-ASCII");
      }
      try {
         lexer = new EncodingLexer(new InputStreamReader(in, "US-ASCII"));
         parser = new EncodingParser(lexer);
         csName = parser.htmlDocument();
         if (csName != null) {
           // TODO: prepare document with illegal value, then test: Decide to catch exception and return UnsupportedCharset.
            charset = Charset.forName(csName);
         }
         else{
           charset = UnknownCharset.getInstance();
         }
      } catch (ANTLRException ae) {
         if (this.verbose) {
            System.out.println("  ANTLR parser exception: " + ae.getMessage());
         }
      } catch (Exception deepdown) {
         if (this.verbose) {
            System.out.println("  Decoding Exception: " + deepdown.getMessage() + " (unsupported java charset).");
         }
         if(charset==null){
           if(csName!=null){
             charset=UnsupportedCharset.forName(csName);
           }
           else{
             charset = UnknownCharset.getInstance();
           }
         }
      }
      return charset;
   }

}