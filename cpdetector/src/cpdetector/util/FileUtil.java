/*
 * FileUtil.java
 *
 * Created on 13. Oktober 2001, 11:04
 */
package cpdetector.util;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.Map;
import java.util.StringTokenizer;

/**
 * 
 * @author Achim Westermann
 * @version 1.1
 */
public class FileUtil extends Object {
  /** Creates new FileUtil */
  public FileUtil() {
  }

  /**
   * Be careful with big files: In order to avoid having to write a tmpfile
   * (cannot read and directly write to the same file) a StringBuffer is used
   * for manipulation. Big files will cost all RAM and terminate VM hard.
   */
  public static void removeDuplicateLineBreaks(File f) {
    String sep = StringUtil.getNewLine();
    if (!f.exists()) {
      System.err.println("FileUtil.removeDuplicateLineBreak(File f): " + f.getAbsolutePath() + " does not exist!");
      return;
    }
    if (f.isDirectory()) {
      System.err.println("FileUtil.removeDuplicateLineBreak(File f): " + f.getAbsolutePath() + " is a directory!");
      return;
    }
    // real file
    try {
      BufferedInputStream in = new BufferedInputStream(new FileInputStream(f), 1024);
      StringBuffer result = new StringBuffer();
      int tmpread;
      while ((tmpread = in.read()) != -1) {
        result.append((char) tmpread);
      }
      String tmpstring;
      StringTokenizer toke = new StringTokenizer(result.toString(), sep, true);
      result = new StringBuffer();
      int breaks = 0;
      while (toke.hasMoreTokens()) {
        tmpstring = toke.nextToken().trim();
        if (tmpstring.equals("") && breaks > 0) {
          breaks++;
          //if(breaks<=2)result.append(sep);
          continue;
        }
        if (tmpstring.equals("")) {
          tmpstring = sep;
          breaks++;
        } else {
          breaks = 0;
        }
        result.append(tmpstring);
      }
      // delete original file and write it new from tmpfile.
      f.delete();
      f.createNewFile();
      FileWriter out = new FileWriter(f);
      out.write(result.toString());
      out.flush();
    } catch (FileNotFoundException e) {
      // does never happen.
    } catch (IOException g) {
      g.printStackTrace(System.err);
    }
  }

  /**
   * <p>
   * Finds a filename based on the given name. If a file with the given name
   * does not exist, <tt>name</tt> will be returned.
   * </p>
   * <p>
   * Else:
   * 
   * <pre>
   * 
   *  		&quot;myFile.out&quot;       --&gt; &quot;myFile_0.out&quot;
   *  		&quot;myFile_0.out&quot;   --&gt; &quot;myFile_1.out&quot;
   *  		&quot;myFile_1.out&quot;   --&gt; &quot;myFile_2.out&quot;
   *          ....
   *  	
   * </pre>
   * 
   * The potential extension is preserved, but a number is appended to the
   * prefix name.
   * </p>
   * 
   * @param name
   *          A desired file name.
   * @return A String that sticks to the naming convention of the given String
   *         but is unique in the directory scope of argument <tt>name</tt>.
   */
  public static String getDefaultFileName(String name) {
    File f = new File(name);
    if (!f.exists()) {
      return f.getAbsolutePath();
    }
    java.util.Map.Entry cut = cutExtension(name);
    String prefix = (String) cut.getKey();
    String suffix = (String) cut.getValue();
    int num = 0;
    while (f.exists()) {
      f = new File(prefix+'_'+num+'.'+suffix);
      num++;
    }
    return f.getAbsolutePath();
  }

  /**
   * <p>
   * Cuts a String into the part before the last dot and after the last dot. If
   * only one dot is contained on the first position, it will completely be used
   * as prefix part.
   * </p>
   * <p>
   * 
   * <pre>
   * Map.Entry entry = FileUtil.getPotentialExtension(&quot;A.Very.Strange.Name.txt&quot;);
   * String prefix = (String) entry.getKey(); // prefix is &quot;A.Very.Strange.Name&quot;.
   * String suffix = (String) entry.getValue(); // suffix is &quot;txt&quot;;
   * 
   * entry = FileUtil.getPotentialExtension(&quot;.profile&quot;);
   * String prefix = (String) entry.getKey(); // prefix is &quot;.profile&quot;.
   * String suffix = (String) entry.getValue(); // suffix is &quot;&quot;;
   * 
   * entry = FileUtil.getPotentialExtension(&quot;bash&quot;);
   * String prefix = (String) entry.getKey(); // prefix is &quot;bash&quot;.
   * String suffix = (String) entry.getValue(); // suffix is &quot;&quot;;
   * 
   * </pre>
   * 
   * </p>
   * 
   * @param filename
   *          A String that is interpreted to be a file name: The last dot ('.')
   *          is interpreted to be the extension delimiter.
   * @return A <tt> java.util.Map.Entry</tt> instance containing a String for
   *         the filename at the key side and a String for the extension at the
   *         value side.
   */
  public static java.util.Map.Entry cutExtension(String filename) {
    String prefix;
    String suffix = null;
    StringTokenizer tokenizer = new StringTokenizer(filename, ".");
    int tokenCount = tokenizer.countTokens();
    if (tokenCount > 1) {
      StringBuffer prefCollect = new StringBuffer();
      while (tokenCount > 1) {
        tokenCount--;
        prefCollect.append(tokenizer.nextToken());
        if (tokenCount > 1) {
          prefCollect.append(".");
        }
      }
      prefix = prefCollect.toString();
      suffix = tokenizer.nextToken();
    } else {
      prefix = filename;
      suffix = "";
    }
    return new Entry(prefix, suffix);
  }

  /**
   * <p>
   * Cuts the path information of the String that is interpreted as a filename
   * into the directory part and the file part. The current operating system's
   * path separator is used to cut all path information from the String.
   * </p>
   * <p>
   * 
   * <pre>
   * 
   *  	&quot;c:/work/programming/anyfile.jar --&gt; Map.Entry(&quot;c:/work/programming/&quot;,&quot;anyfile.jar&quot;);
   *   &quot;anyfile.jar&quot;                    --&gt; Map.Entry(new File(&quot;.&quot;).getAbsolutePath(),&quot;anyfile.jar&quot;);
   *   &quot;c:/directory1/directory2/&quot;      --&gt; Map.Entry(&quot;c:/directory1/directory2/&quot;,&quot;&quot;);
   *   &quot;c:/directory1/directory2&quot;       --&gt; Map.Entry(&quot;c:/directory1/directory2/&quot;,&quot;&quot;); // directory2 is a directory!
   *   &quot;c:/directory1/file2&quot;            --&gt; Map.Entry(&quot;c:/directory1/&quot;,&quot;file2&quot;);       // file2 is a file!
   *   &quot;c:/&quot;                            --&gt; Map.Entry(&quot;c:/&quot;,&quot;&quot;);
   *  
   * </pre>
   * 
   * Assuming, that '/' is the current file separator character.
   * </p>
   * <p>
   * <b>If your string is retrieved from an <tt>URL</tt> instance, use
   * <tt>cutDirectoryInformation(URL path)</tt> instead, because URL's do not
   * depend on the operating systems file separator! </b>
   * </p>
   * 
   * @param path
   * @return
   */
  public static Map.Entry cutDirectoryInformation(String path) {
    StringBuffer dir = new StringBuffer();
    String file = "";
    String fileseparator = System.getProperty("file.separator");
    StringTokenizer tokenizer = new StringTokenizer(path, fileseparator);
    int size = tokenizer.countTokens();
    switch (size) {
    case 0: {
      dir.append(new File(".").getAbsolutePath());
      break;
    }
    case 1: {
      File test = new File(tokenizer.nextToken());
      if (new File(path).isDirectory()) {
        dir.append(test.getAbsolutePath());
      } else {
        dir.append(new File(".").getAbsolutePath());
        file = path;
      }
      break;
    }
    default: {
      String token;
      while (tokenizer.hasMoreElements()) {
        // reuse String filesparator: bad style...
        token = tokenizer.nextToken();
        if (tokenizer.hasMoreTokens()) {
          dir.append(token);
          dir.append(fileseparator);
        } else {
          if (new File(path).isFile()) {
            file = token;
          } else {
            dir.append(token);
          }
        }
      }
    }
    }
    return new Entry(dir.toString(), file);
  }

  
  /**
   * <p>
   * Cuts all path information of the String representation of the given URL.
   * </p>
   * <p>
   * 
   * <pre>
   * 
   *  	&quot;file//c:/work/programming/anyfile.jar --&gt; &quot;anyfile.jar&quot;
   *   &quot;http://jamwg.de&quot;                      --&gt; &quot;&quot; // No file part.
   *   &quot;ftp://files.com/directory2/           --&gt; &quot;&quot; // File part of URL denotes a directory.
   *  
   * </pre>
   * 
   * Assuming, that '/' is the current file separator character.
   * </p>
   * 
   * @param path
   * @return
   */
  public static Map.Entry cutDirectoryInformation(java.net.URL path) {
    Map.Entry ret = null;
    String pre,suf,parse;
    StringBuffer tmp = new StringBuffer();
    parse = path.toExternalForm();
    if (parse.endsWith("/")) {
        pre = parse;
        suf = "";
    } else {
      StringTokenizer tokenizer = new StringTokenizer(path.getFile(), "/");
      tmp.append(path.getProtocol());
      tmp.append(":");
      tmp.append(path.getHost());
      pre="";
      while (tokenizer.hasMoreElements()) {
        tmp.append(pre);
        pre = tokenizer.nextToken();
        tmp.append("/");
      }
      suf = pre;
      pre = tmp.toString();
    }
    ret = new Entry(pre,suf);
    return ret;
  }
  
  /**
   * Reads the content of the given File 
   * into an array. 
   * This method currently does not check for maximum length and 
   * might cause a java.lang.OutOfMemoryError. 
   * It is only intended for performance-measurements of data-based 
   * algorithms that want to exclude I/O-usage.
   * @throws IOException
   *
   */
  public static byte[] readRAM(File f) throws IOException{
    int total = (int)f.length();
    byte[] ret = new byte[total];
    InputStream in = new FileInputStream(f);
    int offset = 0;
    int read = 0;
    do{
      read = in.read(ret,offset,total-read);
      if(read > 0){
        offset += read;
      }
    }while(read!=-1 && offset != total);
    return ret;
  }
  
  /**
   * Invokes {@link #readRAM(File)}, but 
   * decorates the result with a {@link java.io.ByteArrayInputStream}.
   * This means: The complete content of the given File has been loaded before 
   * using the returned InputStream. There are no IO-delays afterwards but 
   * OutOfMemoryErrors may occur.
   */
  public static InputStream readCache(File f) throws IOException{
    return new ByteArrayInputStream(readRAM(f));
  }
  
  public static boolean isAllASCII(File f) throws IOException{
    return isAllASCII(new FileInputStream(f));
  }
  
  public static boolean isAllASCII(InputStream in) throws IOException{
    boolean ret = true;
    int read = -1;
    do{
      read = in.read();
      if(read>0x7F){
        ret = false;
        break;
      }
      
    }while(read != -1);
    return ret;
  }
  
  /**
   * Tests, wether the content of the given file is identical 
   * at character level, when it is opened with both different 
   * Charsets. This is most often the case, if the given file only 
   * contains ASCII codes but may also occur, when both codepages cover 
   * common ranges and the document only contains values out of those 
   * ranges (like the EUC-CN charset contains all mappings from BIG5). 
   * @throws IOException
   */
  public static boolean isEqual(File document,Charset a, Charset b) throws IOException{
    boolean ret = true;
    InputStreamReader aReader = new InputStreamReader(new FileInputStream(document),a);
    InputStreamReader bReader = new InputStreamReader(new FileInputStream(document),b);
    int readA = -1,readB=-1;
    do{
      readA = aReader.read();
      readB = bReader.read();
      if(readA!=readB){
        // also the case, if one is at the end earlier...
        ret = false;
        break;
      }
    }while(readA!=-1 && readB!=-1);
    return ret;
  }
}