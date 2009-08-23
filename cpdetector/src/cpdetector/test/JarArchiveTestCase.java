package cpdetector.test;

import junit.framework.*;
import java.io.*;
import cpdetector.io.*;

/*
 * 
 *  JarArchiveTestCase.java  cpdetector
 *  Copyright (C) Achim Westermann, created on 30.10.2004, 14:46:32  
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

/**
 * @author <a href="mailto:Achim.Westermann@gmx.de">Achim Westermann </a>
 *  
 */
public class JarArchiveTestCase extends TestCase {
  JarArchive test;

  public JarArchiveTestCase(String name) throws IOException {
    super(name);
  }

  
 
  
  /* (non-Javadoc)
   * @see junit.framework.TestCase#runTest()
   */
  protected void runTest() throws Throwable {
    this.test = new JarArchive("ext/jargs.jar");
    this.list(test, 0);
  }

  private void list(File f, int indent) {
    File[] subdirs = f.listFiles();
    for (int space = 0; space < indent; space++) {
      System.out.print(" ");
    }
    System.out.println(f.getAbsolutePath());
    for (int i = 0; i < subdirs.length; i++) {
      list(subdirs[i], indent++);
    }
  }

  public static Test suite() throws IOException {
    TestSuite suite = new TestSuite();
    suite.addTest(new JarArchiveTestCase(JarArchiveTestCase.class.getName()));
    return suite;
  }
  
  public static void main(String[]args)throws IOException{
    try{
    suite();
    }catch(Throwable f){
      try{
      Thread.sleep(1000);
      }catch(InterruptedException ie){
        
      }
      f.printStackTrace(System.err);
    }
  }
}