/*
 * Open Source Physics software is free software as described near the bottom of this code file.
 *
 * For additional information and documentation on Open Source Physics please see:
 * <http://www.opensourcephysics.org/>
 */

package org.opensourcephysics.tools;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

import org.opensourcephysics.analysis.FourierSinCosAnalysis;
import org.opensourcephysics.display.*;
import org.opensourcephysics.controls.XMLControlElement;

/**
 * This tab displays a Dataset and its Fourier spectra in a FourierTool.
 *
 * @author Douglas Brown
 * @version 1.0
 */
public class FourierToolTab extends DataToolTab {

  // instance fields
	protected Action sourceAction;
  protected JCheckBox sourceCheckbox;
  protected Dataset source;
  protected PlottingPanel sourcePlot;
  protected DataTable sourceTable;
  JSplitPane sourceSplitPane;
  
  /**
   * Constructs a DataToolTab for the specified Data and DataTool.
   *
   * @param data the Data object
   * @param tool the DataTool
   */
  public FourierToolTab(Dataset data, FourierTool tool) {
  	super(createFourierData(data), tool);
  	XMLControlElement xml = new XMLControlElement(data);
  	source = new Dataset();
  	xml.loadObject(source);
  	source.setMarkerColor(Color.red.darker());
  	source.setConnected(true);
  }

  protected static Data createFourierData(Dataset dataset){
    double[] x = dataset.getXPoints();
    double[] y = dataset.getYPoints();
	  if (y.length%2 == 1) { // odd number of points
	    double[] xnew = new double[y.length - 1];
	    double[] ynew = new double[xnew.length];
      System.arraycopy(x, 0, xnew, 0, xnew.length);
      System.arraycopy(y, 0, ynew, 0, ynew.length);
	  	dataset.clear();
	  	dataset.append(xnew, ynew);
	  	x = xnew; y = ynew;
	  }
    FourierSinCosAnalysis fft = new FourierSinCosAnalysis();
    fft.doAnalysis(x,y,0);
    return fft;
  }
  
  /**
   * Initializes this panel.
   */
  protected void init() {
    super.init();
    setMarkersVisible("power", true);
    setMarkersVisible("sin", false);
    setMarkersVisible("cos", false);
  }

  /**
  /**
   * Creates the GUI.
   */
  protected void createGUI() {
  	super.createGUI();
    // create source action and checkbox
  	sourceTable = new DataTable();
  	sourceAction = new AbstractAction() {
      public void actionPerformed(ActionEvent e) {
        // hide/remove source panel
        splitPanes[1].setDividerSize(splitPanes[2].getDividerSize());
        splitPanes[1].setDividerLocation(1.0);
        // restore if checked
        boolean vis = sourceCheckbox.isSelected();
        splitPanes[1].setEnabled(vis);
        if (vis) {
          int max = splitPanes[1].getDividerLocation();
          int h = 150;
          splitPanes[1].setDividerSize(splitPanes[0].getDividerSize());
          splitPanes[1].setDividerLocation(max-h-10);
          if (sourcePlot == null) {
          	sourcePlot = new PlottingPanel(source.getXColumnName(), 
          				source.getYColumnName(), "Source Data");
          	sourceSplitPane.setLeftComponent(sourcePlot);
          	sourceTable.add(source);
            sourceSplitPane.setDividerLocation(0.7);
          }
        	sourceTable.refreshTable();
          sourcePlot.addDrawable(source);
        }
        refresh();
      }
    };
    sourceCheckbox = new JCheckBox("Source Data");
    sourceCheckbox.setSelected(false);
    sourceCheckbox.setOpaque(false);
    sourceCheckbox.addActionListener(sourceAction);
    // assemble components
    int n = toolbar.getComponentIndex(fitCheckbox);
    toolbar.remove(fitCheckbox);
    toolbar.add(sourceCheckbox, n);
    sourceSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
  	sourceSplitPane.setResizeWeight(1);
    JScrollPane scroller = new JScrollPane(sourceTable);
    sourceSplitPane.setRightComponent(scroller);
    splitPanes[1].setBottomComponent(sourceSplitPane);
  }

  /**
   * Refreshes the GUI.
   */
  protected void refreshGUI() {
  	super.refreshGUI();
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
