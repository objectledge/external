/*
 * 
 *  JarArchive.java  cpdetector
 *  Copyright (C) Achim Westermann, created on 30.10.2004, 01:39:02  
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

import java.io.File;
import java.io.FileFilter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.TreeSet;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import cpdetector.util.collections.ITreeNode;
import cpdetector.util.collections.TreeNodeUniqueChildren;

/**
 * <p>
 * A jar file that pretends to be a simple file by extending 
 * {@link java.io.File}. 
 * </p>
 * @author <a href="mailto:Achim.Westermann@gmx.de">Achim Westermann </a>
 *  
 */
public class JarArchive extends File {

  protected JarFile jar;

  protected Set childs = new TreeSet();

  /**
   * @param pathname
   * @throws IOException
   */
  public JarArchive(String pathname) throws IOException {
    super(pathname);
    this.jar = new JarFile(pathname);
    ITreeNode root = this.parseTree();
    System.out.println("tree:");
    System.out.println(root.toString() + '\n');
    this.buildTree(root, this);
  }

  /**
   * Only for the subclass to avoid double-parsing.
   */
  private JarArchive(File f) {
    super(f.getAbsolutePath());
  }

  /**
   * Build a Tree from the entries.
   *  
   */
  private ITreeNode parseTree() {
    ITreeNode root = new TreeNodeUniqueChildren();
    ITreeNode newnode, oldnode;
    Enumeration entries = this.jar.entries();
    String entry;
    while (entries.hasMoreElements()) {
      newnode = root;
      oldnode = root;
      entry = ((JarEntry) entries.nextElement()).getName();
      System.out.println("Entry: "+entry);
      StringTokenizer tokenizer = new StringTokenizer(entry, "/");
      while (tokenizer.hasMoreElements()) {
        String path = tokenizer.nextToken();
        newnode = new TreeNodeUniqueChildren(path);
        oldnode.addChildNode(newnode);
        oldnode = newnode;
      }
    }
    return root;
  }

  protected void buildTree(ITreeNode node, JarArchive element) throws IOException {
    List childNodes = node.getAllChildren();
    Iterator childNodesIt = childNodes.iterator();
    ITreeNode childNode;
    JarElement child;
    JarEntry entry;
    String search; 
    while (childNodesIt.hasNext()) {
      childNode = (ITreeNode) childNodesIt.next();
      search = getSearchPath(childNode);
      System.out.println("Searching for: " + search);
      entry = this.jar.getJarEntry(search);
      if(entry==null){
        System.err.println("Entry for "+search+" ("+ this.jar.getName()+")is null!!!");
      }
      else{
        System.out.println("Entry: "+entry.toString());
        child = new JarElement(entry, this);
        child.buildTree(childNode, this);
        this.childs.add(child);
      }
    
    }
    System.out.println(this+" has finished building...");
  }

  /**
   * Little helper that transforms (back) the nodes of the tree that was parsed
   * from the entries during initialization to canonical strings for entries.
   */
  private String getSearchPath(ITreeNode node) {

    List l = new LinkedList();
    node.getUserObjectPathFromRoot(l);
    Iterator it = l.iterator();
    StringBuffer ret = new StringBuffer();
    int i = 0;
    String token;
    while (it.hasNext()) {
      // avoid the "root" token.
      token = it.next().toString();
      if (i != 0) {
        ret.append(token);
        if (it.hasNext()) {
          ret.append('/');
        }
      }
      
      i++;
    }
    return ret.toString();
  }

  /*
   * (non-Javadoc)
   * 
   * @see java.io.File#canRead()
   */
  public boolean canRead() {
    return true;
  }

  /*
   * (non-Javadoc)
   * 
   * @see java.io.File#canWrite()
   */
  public boolean canWrite() {
    return false;
  }

  /*
   * (non-Javadoc)
   * 
   * @see java.io.File#compareTo(java.io.File)
   */
  public int compareTo(File pathname) {
    return super.compareTo(pathname);
  }

  /*
   * (non-Javadoc)
   * 
   * @see java.io.File#createNewFile()
   */
  public boolean createNewFile() throws IOException {
    return false;
  }

  /*
   * (non-Javadoc)
   * 
   * @see java.io.File#delete()
   */
  public boolean delete() {
    return false;
  }

  /*
   * (non-Javadoc)
   * 
   * @see java.io.File#deleteOnExit()
   */
  public void deleteOnExit() {

  }

  /*
   * (non-Javadoc)
   * 
   * @see java.lang.Object#equals(java.lang.Object)
   */
  public boolean equals(Object obj) {
    return super.equals(obj);
  }

  /*
   * (non-Javadoc)
   * 
   * @see java.io.File#exists()
   */
  public boolean exists() {
    return true;
  }

  /*
   * (non-Javadoc)
   * 
   * @see java.io.File#getAbsoluteFile()
   */
  public File getAbsoluteFile() {
    return super.getAbsoluteFile();
  }

  /*
   * (non-Javadoc)
   * 
   * @see java.io.File#getAbsolutePath()
   */
  public String getAbsolutePath() {
    String ret =  super.getAbsolutePath();
    return ret;
  }

  /*
   * (non-Javadoc)
   * 
   * @see java.io.File#getCanonicalFile()
   */
  public File getCanonicalFile() throws IOException {
    return super.getCanonicalFile();
  }

  /*
   * (non-Javadoc)
   * 
   * @see java.io.File#getCanonicalPath()
   */
  public String getCanonicalPath() throws IOException {
    return super.getCanonicalPath();
  }

  /*
   * (non-Javadoc)
   * 
   * @see java.io.File#getName()
   */
  public String getName() {
    return super.getName();

  }

  /*
   * (non-Javadoc)
   * 
   * @see java.io.File#getParent()
   */
  public String getParent() {
    return super.getParent();
  }

  /*
   * (non-Javadoc)
   * 
   * @see java.io.File#getParentFile()
   */
  public File getParentFile() {
    return super.getParentFile();
  }

  /*
   * (non-Javadoc)
   * 
   * @see java.io.File#getPath()
   */
  public String getPath() {
    return super.getPath();
  }

  /*
   * (non-Javadoc)
   * 
   * @see java.lang.Object#hashCode()
   */
  public int hashCode() {
    return super.hashCode();
  }

  /*
   * (non-Javadoc)
   * 
   * @see java.io.File#isAbsolute()
   */
  public boolean isAbsolute() {
    return super.isAbsolute();
  }

  /*
   * (non-Javadoc)
   * 
   * @see java.io.File#isDirectory()
   */
  public boolean isDirectory() {
    return true;
  }

  /*
   * (non-Javadoc)
   * 
   * @see java.io.File#isFile()
   */
  public boolean isFile() {
    return false;
  }

  /*
   * (non-Javadoc)
   * 
   * @see java.io.File#isHidden()
   */
  public boolean isHidden() {
    return false;
  }

  /*
   * (non-Javadoc)
   * 
   * @see java.io.File#lastModified()
   */
  public long lastModified() {
    return super.lastModified();
  }

  /*
   * (non-Javadoc)
   * 
   * @see java.io.File#length()
   */
  public long length() {
    return super.length();
  }

  /*
   * (non-Javadoc)
   * 
   * @see java.io.File#list()
   */
  public String[] list() {
    String[] ret = new String[this.childs.size()];
    Iterator it = this.childs.iterator();
    for (int i = 0; it.hasNext(); i++) {
      ret[i] = it.next().toString();
    }
    return ret;
  }

  /*
   * (non-Javadoc)
   * 
   * @see java.io.File#list(java.io.FilenameFilter)
   */
  public String[] list(FilenameFilter filter) {
    List ret = new LinkedList();
    Iterator it = this.childs.iterator();
    File next;
    while (it.hasNext()) {
      next = (File) it.next();
      if (filter.accept(next.getParentFile(), next.getName())) {
        ret.add(next.toString());
      }
    }
    return (String[]) ret.toArray(new String[ret.size()]);
  }

  /*
   * (non-Javadoc)
   * 
   * @see java.io.File#listFiles()
   */
  public File[] listFiles() {
    File[] ret = new File[this.childs.size()];
    Iterator it = this.childs.iterator();
    for (int i = 0; it.hasNext(); i++) {
      ret[i] = (File) it.next();
    }
    return ret;
  }

  /*
   * (non-Javadoc)
   * 
   * @see java.io.File#listFiles(java.io.FileFilter)
   */
  public File[] listFiles(FileFilter filter) {
    List ret = new LinkedList();
    Iterator it = this.childs.iterator();
    File next;
    while (it.hasNext()) {
      next = (File) it.next();
      if (filter.accept(next)) {
        ret.add(next);
      }
    }
    return (File[]) ret.toArray(new File[ret.size()]);
  }

  /*
   * (non-Javadoc)
   * 
   * @see java.io.File#listFiles(java.io.FilenameFilter)
   */
  public File[] listFiles(FilenameFilter filter) {
    List ret = new LinkedList();
    Iterator it = this.childs.iterator();
    File next;
    while (it.hasNext()) {
      next = (File) it.next();
      if (filter.accept(next.getParentFile(), next.getName())) {
        ret.add(next);
      }
    }
    return (File[]) ret.toArray(new File[ret.size()]);
  }

  /*
   * (non-Javadoc)
   * 
   * @see java.io.File#mkdir()
   */
  public boolean mkdir() {
    return false;
  }

  /*
   * (non-Javadoc)
   * 
   * @see java.io.File#mkdirs()
   */
  public boolean mkdirs() {
    return false;
  }

  /*
   * (non-Javadoc)
   * 
   * @see java.io.File#renameTo(java.io.File)
   */
  public boolean renameTo(File dest) {
    return false;
  }

  /*
   * (non-Javadoc)
   * 
   * @see java.io.File#setLastModified(long)
   */
  public boolean setLastModified(long time) {
    return super.setLastModified(time);
  }

  /*
   * (non-Javadoc)
   * 
   * @see java.io.File#setReadOnly()
   */
  public boolean setReadOnly() {
    return super.setReadOnly();
  }

  /*
   * (non-Javadoc)
   * 
   * @see java.lang.Object#toString()
   */
  public String toString() {
    return super.toString();
  }

  /*
   * (non-Javadoc)
   * 
   * @see java.io.File#toURI()
   */
  public URI toURI() {
    return super.toURI();
  }

  /*
   * (non-Javadoc)
   * 
   * @see java.io.File#toURL()
   */
  public URL toURL() throws MalformedURLException {
    return super.toURL();
  }

  class JarElement extends JarArchive {
    private File parent;

    private JarEntry entry;

    JarElement(JarEntry entry, File parent) throws IOException {
      super(new File(JarArchive.this.jar.getName()));
      this.parent = parent;
      if (entry == null) {
        System.err.println("Entry is null.");
      }
      this.entry = entry;
      this.jar = JarArchive.this.jar;
    }

    /*
     * protected void buildTree(ITreeNode node,JarArchive element) throws
     * IOException{ List childNodes = node.getAllChildren(); Iterator
     * childNodesIt = childNodes.iterator(); ITreeNode childNode; JarElement
     * child; JarEntry entry; String search; while(childNodesIt.hasNext()){
     * childNode = (ITreeNode)childNodesIt.next(); search =
     * this.entry.getName()+"/"+childNode.getUserObject().toString(); entry =
     * this.jar.getJarEntry(search); child = new JarElement( entry, this );
     * this.childs.add(child); child.buildTree(childNode,this); } }
     *  
     */
    /*
     * (non-Javadoc)
     * 
     * @see java.io.File#getAbsoluteFile()
     */
    public File getAbsoluteFile() {
      return this;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.io.File#getAbsolutePath()
     */
    public String getAbsolutePath() {
      String prefix = JarArchive.this.jar.getName();
      String postfix = this.entry.getName();
      return prefix + "/" + postfix;
    }

  

   

    /*
     * (non-Javadoc)
     * 
     * @see java.io.File#getName()
     */
    public String getName() {
      return this.entry.getName();
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.io.File#getParent()
     */
    public String getParent() {
      return this.parent.getAbsolutePath();
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.io.File#getParentFile()
     */
    public File getParentFile() {
      return this.parent;
    }

   

    /*
     * (non-Javadoc)
     * 
     * @see java.io.File#isDirectory()
     */
    public boolean isDirectory() {
      // would not work, don't know why (false, even if is a dir):
      //this.entry.isDirectory();
      return this.childs.size()!=0;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.io.File#isFile()
     */
    public boolean isFile() {
      return !this.isDirectory();
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.io.File#lastModified()
     */
    public long lastModified() {
      return this.entry.getTime();
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.io.File#length()
     */
    public long length() {
      return this.entry.getSize();
    }

    public String toString() {
      return this.entry.toString();
    }
 
    /* (non-Javadoc)
     * @see java.io.File#getPath()
     */
    public String getPath() {
      return this.entry.getName();
    }
  }

}