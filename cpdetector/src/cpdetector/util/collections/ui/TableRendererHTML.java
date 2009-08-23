/*
 * 
 *  TableRendererHTML.java  cpdetector
 *  Copyright (C) Achim Westermann, created on 21.10.2004, 23:18:10  
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
package cpdetector.util.collections.ui;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;

import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;

/**
 * Only one instance per call to render, or output will be appended. 
 * No time...
 * 
 * @author <a href="mailto:Achim.Westermann@gmx.de">Achim Westermann</a>
 *
 */
public class TableRendererHTML extends StreamTableRenderer{
 
  public TableRendererHTML(Writer out){
    super(out);
  }
  /* (non-Javadoc)
   * @see cpdetector.util.collections.ui.StreamTableRenderer#CellStartTag(boolean)
   */
  protected String CellStartTag(boolean rowstart) {
    return "<TD>";
  }
  /* (non-Javadoc)
   * @see cpdetector.util.collections.ui.StreamTableRenderer#CellStopTag(boolean)
   */
  protected String CellStopTag(boolean rowend) {
    return "</TD>";
  }
  /* (non-Javadoc)
   * @see cpdetector.util.collections.ui.StreamTableRenderer#HeadCellStartTag(boolean)
   */
  protected String HeadCellStartTag(boolean firstOrLast) {
    return "<TH>";
  }
  /* (non-Javadoc)
   * @see cpdetector.util.collections.ui.StreamTableRenderer#HeadCellStopTag(boolean)
   */
  protected String HeadCellStopTag(boolean firstOrLast) {
    return "</TH>";
  }
  /* (non-Javadoc)
   * @see cpdetector.util.collections.ui.StreamTableRenderer#HeadRowStartTag()
   */
  protected String HeadRowStartTag() {
    return "<TR>";
  }
  /* (non-Javadoc)
   * @see cpdetector.util.collections.ui.StreamTableRenderer#HeadRowStopTag()
   */
  protected String HeadRowStopTag() {
    return "</TR>";
  }
  /* (non-Javadoc)
   * @see cpdetector.util.collections.ui.StreamTableRenderer#RowStartTag()
   */
  protected String RowStartTag() {
    return "<TR>";
  }
  /* (non-Javadoc)
   * @see cpdetector.util.collections.ui.StreamTableRenderer#RowStopTag()
   */
  protected String RowStopTag() {
    return "</TR>";
  }
  /* (non-Javadoc)
   * @see cpdetector.util.collections.ui.StreamTableRenderer#TableStartTag()
   */
  protected String TableStartTag() {
    return "<TABLE>";
  }
  /* (non-Javadoc)
   * @see cpdetector.util.collections.ui.StreamTableRenderer#TableStopTag()
   */
  protected String TableStopTag() {
    return "</TABLE>";
  }
  
  public static void main(String[]args)throws Throwable{
    TableModel model = new DefaultTableModel(new String[]{"One","Two","Three"},10);
    Writer out = new OutputStreamWriter(new FileOutputStream(new File("test.html")));
    for(int rows=0;rows<10;rows++){
      for(int cols = 0;cols<3;cols++){
        model.setValueAt("Test("+rows+","+cols+")",rows,cols);
      }
    }
    new TableRendererHTML(out).render(model);
    out.close();
    
  }
}
