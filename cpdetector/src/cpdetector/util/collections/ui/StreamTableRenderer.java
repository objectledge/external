/*
 * 
 *  StreamTableRenderer.java  cpdetector
 *  Copyright (C) Achim Westermann, created on 21.10.2004, 23:22:23  
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

import java.io.IOException;
import java.io.Writer;

import javax.swing.table.TableModel;

/**
 * @author <a href="mailto:Achim.Westermann@gmx.de">Achim Westermann</a>
 *
 */
public abstract class StreamTableRenderer implements ITableRenderer {

  final static int FIRST_CELL_IN_ROW = 0;
  final static int LAST_CELL_IN_ROW = 1;
  
  protected Writer out; 
  public StreamTableRenderer(Writer out){
    this.out = out;
  }
  
  protected final void renderHeader(TableModel model) throws IOException{
    int cols = model.getColumnCount();
    for(int i=0;i<cols;i++){
      this.renderHeaderCell(model.getColumnName(i),(i==0)?FIRST_CELL_IN_ROW:(i==cols-1)?LAST_CELL_IN_ROW:2);
    }
  }

  /**
   * @throws IOException
   * 
   */
  private final void renderHeaderCell(String columnName, int firstOrLast) throws IOException {
    out.write(this.HeadCellStartTag((firstOrLast==FIRST_CELL_IN_ROW)));
    out.write(columnName);
    out.write(this.HeadCellStopTag((firstOrLast==LAST_CELL_IN_ROW)));
  }

  protected abstract String HeadRowStartTag();
  protected abstract String HeadRowStopTag();
  
  protected abstract String HeadCellStopTag(boolean firstOrLast);

  protected abstract String HeadCellStartTag(boolean firstOrLast);

  protected final void renderRow(TableModel model, int row) throws IOException{
    out.write(RowStartTag());
    int cols = model.getColumnCount();
    for(int i=0;i<cols;i++){
      this.renderCell(model.getValueAt(row,i),(i==0)?FIRST_CELL_IN_ROW:(i==cols-1)?LAST_CELL_IN_ROW:2);
    }
    out.write(RowStopTag());
  }
  
  protected abstract String RowStartTag();
  protected abstract String RowStopTag();

  protected final void renderCell(Object content,int firstOrLast) throws IOException{
    out.write(this.CellStartTag((firstOrLast==FIRST_CELL_IN_ROW)));
    if(content instanceof TableModel){
      this.render((TableModel)content);
    }
    else{
      out.write(content.toString());
    }
    out.write(this.CellStopTag((firstOrLast==LAST_CELL_IN_ROW)));
  }
  
  protected abstract String CellStartTag(boolean rowstart);
  protected abstract String CellStopTag(boolean rowend);
 
  /* (non-Javadoc)
   * @see cpdetector.util.collections.ui.ITableRenderer#render(javax.swing.table.TableModel)
   */
  public final void render(TableModel model) throws IOException {
    this.out.write(this.TableStartTag());
    int rows = model.getRowCount();
    // write header
    out.write(this.HeadRowStartTag());
    this.renderHeader(model);
    out.write(this.HeadRowStopTag());

    for(int i=0;i<rows;i++){
      this.renderRow(model,i);
    }
    this.out.write(this.TableStopTag());
  }
  
  protected abstract String TableStartTag();
  protected abstract String TableStopTag();
}
