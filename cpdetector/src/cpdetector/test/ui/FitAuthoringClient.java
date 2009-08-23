package cpdetector.test.ui;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.AbstractAction;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.KeyStroke;

import cpdetector.io.ClassFileFilterIsA;
import cpdetector.io.IClassFileFilter;
import cpdetector.test.ui.ClassFileChooser.URLFileSystemView;
import fit.ColumnFixture;

public class FitAuthoringClient extends JFrame implements ActionListener{

  
  public FitAuthoringClient() {

    this.addWindowListener(new WindowAdapter() {
      public void windowClosing(WindowEvent e) {
        System.exit(0);
      }
    });
    // The menu bar
    JMenuBar bar = new JMenuBar();

    //  Build the first menu.
    JMenu menu = new JMenu("File");
    menu.setMnemonic(KeyEvent.VK_A);
    menu.getAccessibleContext().setAccessibleDescription("The only menu in this program that has menu items");

    //  a group of JMenuItems
    JMenuItem menuItem = new JMenuItem("A text-only menu item");
    //menuItem.addActionListener(this);
    menuItem.setAction(new ActionLoadFixture("ActionName","Demo Action",KeyEvent.VK_0));
    menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_0, ActionEvent.ALT_MASK));
    menuItem.getAccessibleContext().setAccessibleDescription("This doesn't really do anything");

    menu.add(menuItem);
    bar.add(menu);
    this.setJMenuBar(bar);

  }
  /* (non-Javadoc)
   * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
   */
  public void actionPerformed(ActionEvent e) {
    System.out.println(this.getClass().getName() + ".actionPerformed()");
  }
  /**
   * <p>
   * This action opens a filechooser an requests a Fixture classfile that is 
   * instantiated and assigned to the client.
   * </p>
   * <p>
   * The given classfile has to be in the classpath, the filechooser will 
   * filter out every classfile that is a subclass of {@link fit.ColumnFixture}.
   * </p>
   * @author <a href="mailto:Achim.Westermann@gmx.de">Achim Westermann</a>
   *
   */
  class ActionLoadFixture extends AbstractAction {
    
    /**
     * @param name
     */
    public ActionLoadFixture(String name,String description,int mnemonic) {
      super(name);
      putValue(SHORT_DESCRIPTION, description);
      putValue(MNEMONIC_KEY, new Integer(mnemonic));
    }
    
    public void actionPerformed(ActionEvent e) {
      System.out.println(this.getClass().getName() + ".actionPerformed()");
      ClassFileChooser chooser = new ClassFileChooser(new URLFileSystemView());
      ClassFileFilterIsA filter = new ClassFileFilterIsA();
      //TODO: comment in the filter for fit client.
      //filter.addSuperClass(ColumnFixture.class);
      filter.addSuperClass(Throwable.class);
      chooser.addClassFileFilter(filter);
      chooser.showOpenDialog(FitAuthoringClient.this);
    }
  }

  public static void main(String[] args) {
    JFrame frame = new FitAuthoringClient();
    frame.setSize(new Dimension(800, 600));
    frame.setVisible(true);
  }
  
}