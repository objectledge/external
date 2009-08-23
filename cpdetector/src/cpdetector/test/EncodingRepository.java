/*
 * 
 *  EncodingRepository.java a proprietary singleton that 
 *  reads and caches encoding.verifiy files from directories. 
 *  
 *  Copyright (C) Achim Westermann, created on 25.09.2004, 23:13:01  
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
package cpdetector.test;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.TreeSet;

import cpdetector.io.UnknownCharset;
import cpdetector.util.FileUtil;

/**
 * <p>
 * A proprietary hardcoded singleton that provides information 
 * about the verified codepage of a document by reading (and caching) 
 * a file "encoding.verify" from the directory the requested file 
 * resides. 
 * </p>
 * 
 * @author <a href="mailto:Achim.Westermann@gmx.de">Achim Westermann</a>
 *
 */
public class EncodingRepository {
  /**
   * Singleton instance.
   */
  private static EncodingRepository instance = null;
  /**
   * Flags already parsed encoding verification files.
   */
  private Set scannedDirs= new TreeSet();
  
  /**
   * Map from absolute URL Strings to the encoding String.
   */
  private Map url2encoding = new HashMap();
 
  /**
   * Private singleton constructor.
   */
  private EncodingRepository() {
    super();
    // TODO Auto-generated constructor stub
  }
  
  public static EncodingRepository getInstance(){
    if(instance==null){
      instance = new EncodingRepository();
    }
    return instance;
  }
  
  /**
   * @return An instance of {@link UnsupportedCharset}, if no verification 
   * record could be found or the Charset instance corresponding to the 
   * verified codepage of the document.
   *
   */
  public Charset getVerifiedEncoding(URL document)throws IOException{
    Charset ret = null;
    Map.Entry dirSep = FileUtil.cutDirectoryInformation(document);
    //System.out.println("URL: "+document.toExternalForm()+"\n pre: "+dirSep.getKey()+"\n suf: "+dirSep.getValue());
    // look, if we have to load encoding.verify:
    if(!this.scannedDirs.contains(dirSep.getKey())){
      this.parseVerfificationFile(new URL(dirSep.getKey().toString()));
      this.scannedDirs.add(dirSep.getKey().toString());
    }
    String enc = (String)this.url2encoding.get(document.toExternalForm().toLowerCase());
    if(enc == null){
      ret = UnknownCharset.getInstance();
    }
    else{
      ret = Charset.forName(enc);
    }
    return ret;
  }
  
  private void parseVerfificationFile(URL dir)throws IOException{
      System.out.println("Trying to load encoding.verify from: "+dir.toExternalForm());
      URL load = new URL(dir,"encoding.verify");
      Properties props = new Properties();
      try{
      props.load(load.openStream());
      Iterator it = props.entrySet().iterator();
      Map.Entry entry;
      String url,encoding;
      while(it.hasNext()){
        entry = (Map.Entry)it.next();
        url = new URL(dir,entry.getKey().toString()).toExternalForm();
        encoding = entry.getValue().toString().trim();
        System.out.println("key: "+url+"\nvalue  "+encoding);
        this.url2encoding.put(url.toLowerCase(),encoding);
      }
    }catch(IOException ioex){
      throw new IOException("Unable to verify the character encodings for documents under: "+dir.toExternalForm()+". Missing file: encoding.verify");
    }
  }
}
