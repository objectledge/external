/*
 * 
 *  PerformanceTest.java  cpdetector
 *  Copyright (C) Achim Westermann, created on 22.09.2004, 16:13:15  
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

import jargs.gnu.CmdLineParser;

import java.io.File;
import java.io.FileFilter;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.SortedMap;
import java.util.StringTokenizer;

import cpdetector.CmdLineArgsInheritor;
import cpdetector.io.ASCIIDetector;
import cpdetector.io.CodepageDetectorProxy;
import cpdetector.io.FileFilterExtensions;
import cpdetector.io.ICodepageDetector;
import cpdetector.io.JChardetFacade;
import cpdetector.io.ParsingDetector;
import cpdetector.io.UnknownCharset;
import cpdetector.io.UnsupportedCharset;
import cpdetector.reflect.SingletonLoader;
import cpdetector.util.FileUtil;

/**
 * @author <a href="mailto:Achim.Westermann@gmx.de">Achim Westermann </a>
 *  
 */
public class PerformanceTest extends CmdLineArgsInheritor {
  /**
   * The root folder (directory) under which all files for the collection are
   * found.
   */
  protected File collectionRoot = null;
  /**
   * Just caching for performance reasons (hack).
   */
  private String collectionRootName;

  /**
   * The codepage detection proxy that will be used. Is optionally configured by
   * argument \"-c\".
   */
  protected CodepageDetectorProxy detector;

  /**
   * A list of all Charset implementations of this java version. Used for debug
   * output.
   */
  private Charset[] parseCodepages;

  private static String fileseparator = System.getProperty("file.separator");

  /**
   * Needed for searching the collection root directory recursively for
   * extensions.
   */
  private FileFilter extensionFilter;

  /**
   * Amount of detections repeatedly performed on every document. A high number
   * minimizes the time measurement error (but causes the test to take longer).
   */
  private int iterations = 10;
  
  private int filecount = 0;
  
  
  /**
   * All documents that could not be detected in the test.
   */
  private List unDetected = new LinkedList();
  /**
   * All documents that were detected to have a different codepage than the
   * human - verified one.
   */
  private List unEqual = new LinkedList();
  
  IStopWatch stopWatch = new StopWatchSimple(false);

  public PerformanceTest() {
    super();
    this.detector = CodepageDetectorProxy.getInstance();
    // adding the options:
    this.addCmdLineOption("documents", new CmdLineParser.Option.StringOption('r', "documents"));
    this.addCmdLineOption("extensions", new CmdLineParser.Option.StringOption('e', "extensions"));
    this.addCmdLineOption("iterations", new CmdLineParser.Option.IntegerOption('i', "iterations"));
    this.addCmdLineOption("detectors", new CmdLineParser.Option.StringOption('c', "detectors"));
  }

  public void parseArgs(String[] cmdLineArgs) throws Exception {
    // Has to be first call!!
    super.parseArgs(cmdLineArgs);
    Object collectionOption = this.getParsedCmdLineOption("documents");
    Object extensionsOption = this.getParsedCmdLineOption("extensions");
    Object iterationsOption = this.getParsedCmdLineOption("iterations");
    Object detectorOption = this.getParsedCmdLineOption("detectors");

    if (collectionOption == null) {
      usage();
      throw new MissingResourceException("Parameter for collection root directory is missing.", "String", "-r");
    }
    this.collectionRoot = new File(collectionOption.toString());
    this.collectionRootName = this.collectionRoot.getAbsolutePath();
    if (extensionsOption != null) {
      this.extensionFilter = new FileFilterExtensions(this.parseCSVList(extensionsOption.toString()));
    } else {
      // Anonymous dummy:
      this.extensionFilter = new FileFilter() {
        public boolean accept(File f) {
          return true;
        }
      };
    }
    if (detectorOption != null) {
      String[] detectors = this.parseCSVList((String) detectorOption);
      if (detectors.length == 0) {
        StringBuffer msg = new StringBuffer();
        msg.append("You specified the codepage detector argument \"-c\" but ommited any comma-separated fully qualified class-name.");
        throw new IllegalArgumentException(msg.toString());
      }

      // try to instantiate and cast:
      ICodepageDetector cpDetector = null;
      for (int i = 0; i < detectors.length; i++) {
        try {
          cpDetector = (ICodepageDetector) SingletonLoader.getInstance().newInstance(detectors[i]);
          if (cpDetector != null) {
            this.detector.add(cpDetector);
          }
        } catch (InstantiationException ie) {
          System.err.println("Could not instantiate custom ICodepageDetector: " + detectors[i] + " (argument \"-c\"): " + ie.getMessage());
        }

      }
    }
    // default detector initialization:
    else {
      this.detector.add(new ParsingDetector(false));
      this.detector.add(JChardetFacade.getInstance());
      this.detector.add(ASCIIDetector.getInstance());
    
    }
    // Iterations
    if(iterationsOption!=null){
      this.iterations = ((Integer)iterationsOption).intValue();
    }
    this.loadCodepages();
  }

  private final String[] parseCSVList(String listLiteral) {
    if (listLiteral == null)
      return null; // bounce bad callee.
    List tmpList = new LinkedList();
    StringTokenizer tok = new StringTokenizer(listLiteral, ";,");
    while (tok.hasMoreElements()) {
      tmpList.add(tok.nextToken());
    }
    return (String[]) tmpList.toArray(new String[tmpList.size()]);
  }

  /**
   * <p>
   * Recursive depth first search for all documents with .txt - ending
   * (case-insensitive).
   * </p>
   * <p>
   * The given list is filled with all files with a ".txt" extension that were
   * found in the directory subtree of the argument f.
   * </p>
   * <p>
   * No check for null or existance of f is made here, so keep it private.
   * </p>
   * 
   * @param f
   *          The current directory or file (if we visit a leaf).
   */
  private void processRecursive(File f) throws Exception {
    if (f == null) {
      throw new IllegalArgumentException("File argument is null!");
    }
    if (!f.exists()) {
      throw new IllegalArgumentException(f.getAbsolutePath() + " does not exist.");
    }
    if (f.isDirectory()) {
      File[] childs = f.listFiles();
      for (int i = childs.length - 1; i >= 0; i--) {
        processRecursive(childs[i]);
      }
    } else if (this.extensionFilter.accept(f)) {
      this.process(f);
    }
  }

  public final void process() throws Exception {
    this.verifyFiles();
    this.describe();
    this.processRecursive(this.collectionRoot);
    // reporting, maybe to a separate method...
    System.out.println("Processed "+this.filecount+" * "+this.iterations+" = "+(this.filecount*this.iterations)+" in "+this.stopWatch.getPureMilliSeconds()+" ms.");
    if(this.unDetected.size()>0){
      System.out.println(this.unDetected.size()+ " undetected documents:");
      Iterator unDetectedIt = this.unDetected.iterator();
      while(unDetectedIt.hasNext()){
      	System.out.println(unDetectedIt.next().toString());
      }
    }
    else{
      System.out.println("No undetected codepages.");
    }
	if(this.unEqual.size()>0){
	  System.out.println(this.unEqual.size()+ " documents differ between detected an human verified:");
	  Iterator unEqualIt = this.unEqual.iterator();
	  while(unEqualIt.hasNext()){
		System.out.println(unEqualIt.next().toString());
	  }
	}
	else{
	  System.out.println("All documents were detected as human-verified.");
	}
    
  }

  /**
   * All three Files are validated if null, existant and the right type
   * (directory vs. file).
   * 
   * @throws Exception
   *           Sth. does not seem to be valid.
   */
  protected void verifyFiles() throws IllegalArgumentException {
    StringBuffer msg = new StringBuffer();
    /*
     * Manual copy and paste for two file members. Not beautiful but formal
     * correctness (all cases);
     */

    // collectionRoot:
    if (this.collectionRoot == null) {
      msg.append("-> Collection root directory is null!\n");
    } else {
      if (!this.collectionRoot.exists()) {
        msg.append("-> Collection root directory:\"");
        msg.append(this.collectionRoot.getAbsolutePath());
        msg.append("\" does not exist!\n");
      }
    }
    // Uhmmkeh or not:
    if (msg.length() > 0) {
      throw new IllegalArgumentException(msg.toString());
    } else {
      System.out.println("All parameters are valid.");
    }
  }

  /**
   * @param files
   */
  private void process(File document) throws Exception {
    Charset detected=null,verified=null;
    String filename = document.getAbsolutePath();
    int length = (int)document.length();
    // Test may run for a single file only...
    if(!this.collectionRoot.equals(document)){
      filename = filename.substring(this.collectionRootName.length());
    }
    System.out.println("Processing: "+filename);
    //InputStream cache = new InputStreamDebug(FileUtil.readCache(document));
    InputStream cache = FileUtil.readCache(document);
    this.stopWatch.start();
    for(int i=this.iterations;i>0;i--){
      cache.reset();
      detected = this.detector.detectCodepage(cache,length);
    }
    this.stopWatch.stop();
    System.out.println("Detected: "+String.valueOf(detected));
	verified = EncodingRepository.getInstance().getVerifiedEncoding(document.toURL());
    System.out.println("Human verified: "+String.valueOf(verified));
    if(detected == UnknownCharset.getInstance()){
    	this.unDetected.add(filename);
    }
    if(verified != detected){
      if(! FileUtil.isAllASCII(document)){
        // don't call FileUtil.isEqual(..) with bad args.
        if((detected!=UnknownCharset.getInstance()) && !(detected.getClass() == UnsupportedCharset.class)){
          if(!FileUtil.isEqual(document,detected,verified)){
            this.unEqual.add(filename);
          }
          else{
            // TODO: log that difference is ok due to codpage compatibilty
            // within content range.
          }
        }
        else{
          this.unEqual.add(filename);
        }
      }
      else{
        // TODO: log that difference is ok due to ascii.
      }
    }
    this.filecount++;
  }

  protected void describe() {
    StringBuffer msg = new StringBuffer();
    msg.append("Setup:\n");
    msg.append("  Collection-Root         : ");
    msg.append(this.collectionRoot.getAbsolutePath());
    msg.append("\n");
    msg.append("  iterations per document : ");
    msg.append(this.iterations);
    msg.append("n");
    msg.append("  detection algorithm     : ");
    msg.append("\n");
    msg.append(this.detector.toString());
    System.out.println(msg.toString());
  }
  /*
   * (non-Javadoc)
   * 
   * @see com.ibm.es.qa.tests.tokenizer.CmdLineArgsInheritor#usage()
   */
  protected void usage() {
    StringBuffer tmp = new StringBuffer();
    tmp.append("usage: java -jar codepageProcessor.jar [options]");
    tmp.append("\n");
    tmp.append("options: \n");
    tmp.append("\n  Optional:\n");
    tmp.append("  -e <extensions> : A comma- or semicolon- separated string for document extensions like \"-e txt,dat\" (without dot or space!).\n");
    tmp.append("  -i              : Iterations per document. A high value decreases time measurement error \n");
    tmp.append("                     but causes the test to take longer.\n");
    tmp.append("  -c              : Semicolon-separated list of fully qualified classnames. \n");
    tmp.append("                    These classes will be casted to ICodepageDetector instances \n");
    tmp.append("                    and used in the order specified.\n");
    tmp.append("                    If this argument is ommited, a HTMLCodepageDetector followed by .\n");
    tmp.append("                    a JChardetFacade is used by default.\n");
    tmp.append("  Mandatory:\n");
    tmp.append("  -r            : Root directory containing the collection (recursive).\n");
    tmp.append("  -o            : Output directory containing the sorted collection.\n");
    System.out.print(tmp.toString());
  }
  
  // TODO: remove this method: useless
  void loadCodepages() {
	System.out.println("Loading available codepages...");
    SortedMap charSets = Charset.availableCharsets();
    Iterator csIt = charSets.entrySet().iterator();
    Map.Entry entry;
    Charset cs;
    Iterator aliases;
    this.parseCodepages = new Charset[charSets.size()];
    int index = 0;
    while (csIt.hasNext()) {
      entry = (Map.Entry) csIt.next();
      cs = (Charset) entry.getValue();
	  System.out.println("  "+cs.name());
	  System.out.println("   aliases: ");
	  aliases = cs.aliases().iterator();
	  while(aliases.hasNext()){
	  	System.out.println("      "+aliases.next().toString());
	  }
      this.parseCodepages[index] = cs;
      index++;
    }
  }

  public static void main(String[] args) {
    PerformanceTest detector = new PerformanceTest();
    try {
      detector.parseArgs(args);
      try {
        detector.process();
      } catch (Exception e) {
        e.printStackTrace(System.err);
      }
    } catch (Exception e) {

      System.err.println("\nError: " + e.getMessage());
      e.printStackTrace(System.err);
    }
  }
}