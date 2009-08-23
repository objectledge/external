/**
 * TreeNodeUniqueChildren.java , an implementation
 * of the ITreeNode interface allowing only unique nodes at a level.
 * Copyright (C) 2002  Achim Westermann, Achim.Westermann@gmx.de
 *
 * This code hast been written long before my work @ IBM.
 * Therefore it does not fall under any restrictions of the labor agreement
 * between Achim Westermann and IBM Deutschland Entwicklungs GmbH.
 *
 * I hereby grant IBM to use the code for commercial purposes but without
 * any rights to put it under a license that would dissallow me to use it
 * in other commercial or non-commercial products.
 * I do not enforce IBM to distribute any sources for changes that have
 * been made (as the LGPL or GPL). I will not put this version of
 * the code under a license that will offend this grant towards IBM.
 *
 **/
package cpdetector.util.collections;

import java.util.Iterator;
import java.util.List;

/**
 * <p>
 * An {@link ItreeNode}implementation, that does not allow equal children of
 * one common parent node. Common elements in the path from an arbitrary node
 * (seen as the root) to different leaves will share the same <tt>ITreeNode</tt>
 * instances at runtime.
 * </p>
 * <p>
 * This behaviour may be used to create the smallest possible tree containing
 * all given serialized paths.
 * </p>
 * 
 * @author <a href="mailto:Achim.Westermann@gmx.de">Achim Westermann </a>
 */
public class TreeNodeUniqueChildren extends ITreeNode.DefaultTreeNode {

  /**
   *  
   */
  public TreeNodeUniqueChildren() {
    super();
  }

  /**
   * @param userObject
   */
  public TreeNodeUniqueChildren(Object userObject) {
    super(userObject);
  }

  /**
   * @param userObject
   * @param child
   */
  public TreeNodeUniqueChildren(Object userObject, ITreeNode child) {
    super(userObject, child);
  }

  /**
   * @param userObject
   * @param children
   */
  public TreeNodeUniqueChildren(Object userObject, ITreeNode[] children) {
    super(userObject, children);
  }

  /**
   * <p>
   * If the given argument is already a child node of this one (by the means of
   * the equals method), it will replace the old node but gets the childs of the
   * old node.
   * </p>
   * 
   * @see aw.util.collections.ITreeNode#addChildNode(aw.util.collections.ITreeNode)
   */
  public boolean addChildNode(ITreeNode node) {
    boolean ret = true;
    if (node == null) {
      throw new IllegalArgumentException("Argument node is null!");
    }
    Object nodeObject = node.getUserObject();
    Object childObject = null;
    ITreeNode child = null;
    Iterator childIt = this.getChilds();
    while (childIt.hasNext()) {
      child = (ITreeNode) childIt.next();
      childObject = child.getUserObject();

      if (child.equals(node)) {
        // add all childs of nodeObject to child:
        List childChilds = child.getAllChildren();
        node.addChildNodes((ITreeNode[]) childChilds.toArray(new ITreeNode[childChilds.size()]));
        node.setParent(this);
        // childIt.remove() throws concurrentmod...
        this.removeChild(child);
        break;
      }
    }
    ret = super.addChildNode(node);
    return ret;
  }

  /**
   * 
   * <p>
   * Construction of a tree with the user Objects (java.lang.Integer) and use
   * the toString() method.
   * 
   * <pre>
   * 
   * 
   *              0
   *             /|\
   *            / | \
   *           1  2  1
   *          / \    |\
   *         /   \   | \
   *        4     5  6  7
   *                /|\
   *               / | \
   *              8  9  10
   * 
   *  &lt;pre&gt;
   *  As only unique nodes are supported, the paths have to be flattended to:
   *  &lt;pre&gt;
   * 
   *              0
   *             / \
   *            /   \
   *           1     2
   *          /|\
   *         / | \
   *        /  | |\
   *       /   | | \
   *      4    5 6  7
   *            /|\
   *           / | \
   *          8  9  10
   * 
   *  &lt;pre&gt;
   * 
   * 
   * 
   * 
   */
  public static void main(String[] args) throws Exception {
    StringBuffer prettyPrint = new StringBuffer();
    prettyPrint.append("             0\n");
    prettyPrint.append("            /|\\\n");
    prettyPrint.append("           / | \\\n");
    prettyPrint.append("          1  2  1\n");
    prettyPrint.append("         / \\    |\\ \n");
    prettyPrint.append("        /   \\   | \\ \n");
    prettyPrint.append("      4     5   6  7 \n");
    prettyPrint.append("               /|\\ \n");
    prettyPrint.append("              / | \\ \n");
    prettyPrint.append("             8  9  10 \n");

    System.out.println("Constructing tree:\n" + prettyPrint.toString());

    prettyPrint.delete(0, prettyPrint.length());
    prettyPrint.append("             0 \n");
    prettyPrint.append("            / \\ \n");
    prettyPrint.append("           /   \\ \n");
    prettyPrint.append("          1     2 \n");
    prettyPrint.append("         /|\\  \n");
    prettyPrint.append("        / | \\ \n");
    prettyPrint.append("       /  | |\\ \n");
    prettyPrint.append("      /   | | \\ \n");
    prettyPrint.append("     4    5 6  7 \n");
    prettyPrint.append("           /|\\ \n");
    prettyPrint.append("          / | \\ \n");
    prettyPrint.append("         8  9  10 \n");

    System.out.println("Assuming tree:\n" + prettyPrint.toString());

    ITreeNode root = new TreeNodeUniqueChildren(new Integer(0), new ITreeNode[] {
        new DefaultTreeNode(new Integer(1), new ITreeNode[] {
            new DefaultTreeNode(new Integer(4)), new DefaultTreeNode(new Integer(5))
        }), new DefaultTreeNode(new Integer(2)), new DefaultTreeNode(new Integer(1), new ITreeNode[] {
            new DefaultTreeNode(new Integer(6), new ITreeNode[] {
                new DefaultTreeNode(new Integer(8)), new DefaultTreeNode(new Integer(9)), new DefaultTreeNode(new Integer(10))
            }), new DefaultTreeNode(new Integer(7))
        })

    });
    System.out.println("The tree:");
    System.out.println(root.toString());
  }

  /*
   * (non-Javadoc)
   * 
   * @see aw.util.collections.ITreeNode#newInstance()
   */
  public ITreeNode newInstance() {
    return new TreeNodeUniqueChildren();
  }

}