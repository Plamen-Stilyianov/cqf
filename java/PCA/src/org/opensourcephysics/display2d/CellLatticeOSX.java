/*
 * Open Source Physics software is free software as described near the bottom of this code file.
 *
 * For additional information and documentation on Open Source Physics please see:
 * <http://www.opensourcephysics.org/>
 */

package org.opensourcephysics.display2d;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.Shape;
import java.util.Random;
import javax.swing.JFrame;
import org.opensourcephysics.display.DrawingPanel;
import org.opensourcephysics.display.Grid;
import org.opensourcephysics.display.InteractivePanel;
import org.opensourcephysics.display.Measurable;
import org.opensourcephysics.display.axes.XAxis;
import org.opensourcephysics.display.axes.XYAxis;

/**
 *  A Mac version of CellLattice that displays an array where each array element can assume one of 256
 *  values.
 *
 * Early versions of Java on Mac OSX were not able to set pixels in an image raster.  This class implements an alternate CellLattice
 * that does not use an image raster.
 *
 * Values can be set between -128 and 127. Because byte values larger
 *  than 127 overflow to negative, values can also be set between 0 and 255. The
 *  lattice is drawn as an array of rectangles to distinguish between the two
 *  possible values.
 *
 * @author     Wolfgang Christian
 * @author     Joshua Gould
 * @created    May 21, 2003
 * @version    1.0
 */
public class CellLatticeOSX extends Grid implements Measurable, ByteLattice {
  boolean visible = true; // shadow super.visible
  Color[] colors = new Color[256];
  byte[][] data;
  private JFrame legendFrame;

  /**
   * Constructs a cell lattice.
   *
   * Cell values are -128 to 127.
   *
   */
  public CellLatticeOSX() {
    this(1, 1);
  }

  /**
   *  Constructs a Cell lattice with the given size. Site values are -128 to 127.
   *
   * @param nx  sites in x dirction
   * @param ny  sites in y direction
   */
  public CellLatticeOSX(int nx, int ny) {
    super(nx, ny); // number of cells in one less than number of sites
    createDefaultColors();
    data = new byte[nx][ny]; // site array
    color = Color.lightGray;
  }

  /**
   * Creates a new SiteLattice containing the same data as this lattice.
   */
  public SiteLattice createSiteLattice() {
    SiteLattice lattice = new SiteLattice(nx, ny);
    lattice.setBlock(data);
    lattice.setMinMax(getXMin(), getXMax(), getYMin(), getYMax());
    lattice.setColorPalette(colors);
    return lattice;
  }

  public void resizeLattice(int _nx, int _ny) {
    nx = _nx;
    ny = _ny;
    setMinMax(xmin, xmax, ymin, ymax);
    data = new byte[nx][ny]; // site array
  }

  /**
   * Gets the number of x entries.
   * @return nx
   */
  public int getNx() {
    return nx;
  }

  /**
   * Gets the number of y entries.
   * @return ny
   */
  public int getNy() {
    return ny;
  }

  /**
   * Determines the lattice index (row-major order) from given x and y world coordinates
   * Returns -1 if the world coordinates are outside the lattice.
   *
   * @param x
   * @param y
   * @return index
   */
  public int indexFromPoint(double x, double y) {
    int nx = getNx();
    int ny = getNy();
    double xMin = getXMin();
    double xMax = getXMax();
    double yMin = getYMin();
    double yMax = getYMax();
    double deltaX = (x-xMin)/(xMax-xMin);
    double deltaY = (y-yMin)/(yMax-yMin);
    int ix = (int) (deltaX*nx);
    int iy = (int) (deltaY*ny);
    if((ix<0)||(iy<0)||(ix>=nx)||(iy>=ny)) {
      return -1;
    }
    return iy*nx+ix;
  }

  /**
   * Gets closest index from the given x world coordinate.
   *
   * @param x double the coordinate
   * @return int the index
   */
  public int xToIndex(double x) {
    int nx = getNx();
    double xMin = getXMin();
    double xMax = getXMax();
    double deltaX = (x-xMin)/(xMax-xMin);
    int ix = (int) (deltaX*nx);
    if(ix<0) {
      return 0;
    }
    if(ix>=nx) {
      return nx-1;
    }
    return ix;
  }

  /**
   * Gets closest index from the given y world coordinate.
   *
   * @param y double the coordinate
   * @return int the index
   */
  public int yToIndex(double y) {
    int ny = getNy();
    double yMin = getYMin();
    double yMax = getYMax();
    double deltaY = (y-yMin)/(yMax-yMin);
    int iy = (int) (deltaY*ny);
    if(iy<0) {
      return 0;
    }
    if(iy>=ny) {
      return ny-1;
    }
    return iy;
  }

  /**
   * Sets the visibility of the lattice.
   * Drawing will be disabled if visible is false.
   *
   * @param isVisible
   */
  public void setVisible(boolean isVisible) {
    visible = isVisible;
  }

  private Rectangle getBounds(DrawingPanel panel) {
    int x1 = panel.xToPix(xmin);
    int x2 = panel.xToPix(xmax);
    int y1 = panel.yToPix(ymin);
    int y2 = panel.yToPix(ymax);
    return new Rectangle(Math.min(x1, x2), Math.min(y1, y2), Math.abs(x2-x1), Math.abs(y2-y1));
  }

  /**
   *  Draws the lattice and the grid.
   *
   * @param  panel
   * @param  g
   */
  public void draw(DrawingPanel panel, Graphics g) {
    if(!visible) {
      return;
    }
    double ymax = this.ymax;
    double xmin = this.xmin;
    if(panel.getXMax()<panel.getXMin()) { // x axis is flipped
      xmin = (dx<0) ? this.xmin-dx : this.xmin+dx;
    }
    if(panel.getYMax()<panel.getYMin()) { // yaxis is flipped
      ymax = (dy<0) ? this.ymax+dy : this.ymax-dy;
    }
    double x = (dx<0) ? xmin+dx : xmin;
    double y = (dy<0) ? ymax-dy : ymax;
    int x1pix = panel.xToPix(x);
    int y1pix = panel.yToPix(y);
    int x2pix, y2pix;
    Shape clipShape = g.getClip();
    Rectangle r = getBounds(panel);
    g.clipRect(r.x, r.y, r.width, r.height);
    for(int ix = 0;ix<nx;ix++) {
      x += dx;
      x2pix = panel.xToPix(x);
      for(int iy = ny-1;iy>=0;iy--) { // start at top
        y -= dy;
        y2pix = panel.yToPix(y);
        int val = data[ix][iy]&0xFF;
        g.setColor(colors[val]);
        g.fillRect(x1pix, y1pix, Math.abs(x2pix-x1pix)+1, Math.abs(y1pix-y2pix)+1);
        y1pix = y2pix;
      }
      x1pix = x2pix;
      y = (dy<0) ? ymax-dy : ymax;
      y1pix = panel.yToPix(y);
    }
    g.setClip(clipShape);
    super.draw(panel, g); // draw the grid
  }

  /**
   *  Sets a block of cells using byte values.
   *
   * @param ix_offset int
   * @param iy_offset int
   * @param val byte[][]
   */
  public void setBlock(int ix_offset, int iy_offset, byte val[][]) {
    if((iy_offset<0)||(iy_offset+val[0].length-1>ny)) {
      throw new IllegalArgumentException("Row offset "+iy_offset+" out of range.");
    }
    if((ix_offset<0)||(ix_offset+val.length-1>nx)) {
      throw new IllegalArgumentException("Column offset "+ix_offset+" out of range.");
    }
    for(int iy = iy_offset, my = val[0].length+iy_offset;iy<my;iy++) {
      for(int ix = ix_offset, mx = val.length+ix_offset;ix<mx;ix++) {
        data[ix][iy] = val[ix-ix_offset][iy-iy_offset];
      }
    }
  }

  /**
   * Sets a block of data to new values.
   *
   * The lattice is resized to fit the new data if needed.
   *
   * @param val
   */
  public void setAll(byte val[][]) {
    if((getNx()!=val.length)||(getNy()!=val[0].length)) {
      resizeLattice(val.length, val[0].length);
    }
    setBlock(0, 0, val);
  }

  /**
   * Sets the lattice values and scale.
   *
   * The lattice is resized to fit the new data if needed.
   *
   * @param val int[][] the new values
   * @param xmin double
   * @param xmax double
   * @param ymin double
   * @param ymax double
   */
  public void setAll(byte val[][], double xmin, double xmax, double ymin, double ymax) {
    setAll(val);
    setMinMax(xmin, xmax, ymin, ymax);
  }

  /**
   *  Sets a block of cells using integer values.
   *
   * @param ix_offset int
   * @param iy_offset int
   * @param val int[][]
   */
  public void setBlock(int ix_offset, int iy_offset, int val[][]) {
    if((iy_offset<0)||(iy_offset+val[0].length-1>ny)) {
      throw new IllegalArgumentException("Row offset "+iy_offset+" out of range.");
    }
    if((ix_offset<0)||(ix_offset+val.length-1>nx)) {
      throw new IllegalArgumentException("Column offset "+ix_offset+" out of range.");
    }
    for(int iy = iy_offset, my = val[0].length+iy_offset;iy<my;iy++) {
      for(int ix = ix_offset, mx = val.length+ix_offset;ix<mx;ix++) {
        data[ix][iy] = (byte) val[ix-ix_offset][iy-iy_offset];
      }
    }
  }

  /**
   *  Sets a block of cells to new values.
   *
   * @param  val
   */
  public void setBlock(byte val[][]) {
    setBlock(0, 0, val);
  }

  /**
   * Sets a column to new values.
   *
   * @param ix the x index of the column
   * @param iy_offset the y offset in the column
   * @param val values in column
   */
  public void setCol(int ix, int iy_offset, byte val[]) {
    if((iy_offset<0)||(iy_offset+val.length>ny)) {
      throw new IllegalArgumentException("Row offset "+iy_offset+" out of range.");
    }
    if((ix<0)||(ix>=nx)) {
      throw new IllegalArgumentException("Column index "+ix+" out of range.");
    }
    for(int iy = iy_offset, my = val.length+iy_offset;iy<my;iy++) {
      data[ix][iy] = val[iy-iy_offset];
    }
  }

  /**
   * Sets a row to new values.
   *
   * @param iy  the y index of the row
   * @param ix_offset the x offset in the row
   * @param val
   */
  public void setRow(int iy, int ix_offset, byte val[]) {
    if((iy<0)||(iy>=ny)) {
      throw new IllegalArgumentException("Y index out of range in binary lattice setRow.");
    }
    if((ix_offset<0)||(ix_offset+val.length>nx)) {
      throw new IllegalArgumentException("X offset out of range in binary lattice setRow.");
    }
    for(int xindex = ix_offset, mx = val.length+ix_offset;xindex<mx;xindex++) {
      data[xindex][iy] = val[xindex-ix_offset];
    }
  }

  /**
   * Sets the given x,y location to a new value.
   *
   * @param ix
   * @param iy
   * @param val
   */
  public void setValue(int ix, int iy, byte val) {
    if((iy<0)||(iy>=ny)) {
      throw new IllegalArgumentException("Row index "+iy+" out of range.");
    }
    if((ix<0)||(ix>=nx)) {
      throw new IllegalArgumentException("Column index "+ix+" out of range.");
    }
    data[ix][iy] = val;
  }

  /**
   *  Gets a lattice site value.
   *
   * @param  row
   * @param  col
   * @return      the cell value.
   */
  public byte getValue(int col, int row) {
    return data[col][row];
  }

  /**
   * Sets the visibility of the sites.
   *
   * Drawing will be disabled if visible is false.
   *
   * @param isVisible
   */
  public void setShowVisible(boolean isVisible) {
    visible = isVisible; // note that we are shadowing super.visible
  }

  /**
   * Sets the visibility of the grid connecting the sites.
   *
   * @param  showGridLines
   */
  public void setShowGridLines(boolean showGridLines) {
    super.visible = showGridLines;
  }

  /** Randomizes the lattice values. */
  public void randomize() {
    Random random = new Random();
    for(int rindex = 0, nr = data[0].length;rindex<nr;rindex++) {
      for(int cindex = 0, nc = data.length;cindex<nc;cindex++) {
        data[cindex][rindex] = (byte) random.nextInt(256);
      }
    }
  }

  /**
   * Shows the color associated with each value.
   * @return the JFrame containing the legend
   */
  public JFrame showLegend() {
    InteractivePanel dp = new InteractivePanel();
    dp.setPreferredSize(new java.awt.Dimension(300, 66));
    dp.setPreferredGutters(0, 0, 0, 35);
    dp.setClipAtGutter(false);
    if(legendFrame==null ||!legendFrame.isDisplayable()) {
      legendFrame = new JFrame("Legend");
    }
    legendFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    legendFrame.setResizable(false);
    legendFrame.setContentPane(dp);
    CellLattice lattice = new CellLattice(256, 1);
    lattice.setMinMax(-128, 127, 0, 1);
    byte[][] data = new byte[256][1];
    for(int i = 0;i<256;i++) {
      data[i][0] = (byte) (-128+i);
    }
    lattice.setBlock(0, 0, data);
    dp.addDrawable(lattice);
    XAxis xaxis = new XAxis("");
    xaxis.setLocationType(XYAxis.DRAW_AT_LOCATION);
    xaxis.setLocation(-0.5);
    xaxis.setEnabled(true);
    dp.addDrawable(xaxis);
    legendFrame.pack();
    legendFrame.setVisible(true);
    return legendFrame;
  }

  /**
   *  Sets the color palette.
   *
   * @param  _colors
   */
  public void setColorPalette(Color[] _colors) {
    int n = Math.min(256, _colors.length);
    for(int i = 0;i<n;i++) {
      colors[i] = _colors[i];
    }
    for(int i = n;i<256;i++) {
      colors[i] = Color.black;
    }
  }

  /**
   *  Sets the grid line color.
   *
   * @param  _color
   */
  public void setGridLineColor(Color _color) {
    color = _color;
  }

  /**
   *  Sets the color for a single index.
   *
   * @param  i
   * @param  color
   */
  public void setIndexedColor(int i, Color color) {
    // i         = i % colors.length;
    i = (i+256)%colors.length;
    colors[i] = color;
  }

  /**
   * Method isMeasured
   *
   *
   * @return
   */
  public boolean isMeasured() {
    return true; // we always have data
  }

  public void setXMin(double _value) {
    xmin = _value;
  }

  public void setXMax(double _value) {
    xmax = _value;
  }

  public void setYMin(double _value) {
    ymin = _value;
  }

  public void setYMax(double _value) {
    ymax = _value;
  }

  /**
   * Creates the default palette.
   */
  public void createDefaultColors() {
    for(int i = 0;i<256;i++) {
      double x = (i<128) ? (i-100)/255.0 : -1;
      double val = Math.exp(-x*x*8);
      int red = (int) (255*val);   // red
      x = (i<128) ? i/255.0 : (255-i)/255.0;
      val = Math.exp(-x*x*8);
      int green = (int) (255*val); // green
      x = (i<128) ? -1 : (i-156)/255.0;
      val = Math.exp(-x*x*8);
      int blue = (int) (255*val);  // blue
      colors[i] = new Color(red, green, blue);
    }
  }
}

/*
 * Open Source Physics software is free software; you can redistribute
 * it and/or modify it under the terms of the GNU General Public License (GPL) as
 * published by the Free Software Foundation; either version 2 of the License,
 * or(at your option) any later version.

 * Code that uses any portion of the code in the org.opensourcephysics package
 * or any subpackage (subdirectory) of this package must must also be be released
 * under the GNU GPL license.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston MA 02111-1307 USA
 * or view the license online at http://www.gnu.org/copyleft/gpl.html
 *
 * Copyright (c) 2007  The Open Source Physics project
 *                     http://www.opensourcephysics.org
 */
