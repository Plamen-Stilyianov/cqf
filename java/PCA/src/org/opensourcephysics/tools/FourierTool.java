/*
 * Open Source Physics software is free software as described near the bottom of this code file.
 *
 * For additional information and documentation on Open Source Physics please see:
 * <http://www.opensourcephysics.org/>
 */

package org.opensourcephysics.tools;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.util.*;

import org.opensourcephysics.controls.*;
import org.opensourcephysics.display.*;

/**
 * This provides a GUI for Fourier analysis.
 *
 * @author Douglas Brown
 * @version 1.0
 */
public class FourierTool extends DataTool {

  /**
   * A shared Fourier tool.
   */
  final static FourierTool FOURIER_TOOL = new FourierTool();

  /**
   * Gets the shared FourierTool.
   *
   * @return the shared FourierTool
   */
  public static DataTool getTool() {
    return FOURIER_TOOL;
  }

  /**
   * Constructs a blank FourierTool.
   */
  public FourierTool() {
    super(ToolsRes.getString("FourierTool.Frame.Title"), "FourierTool"); //$NON-NLS-1$ //$NON-NLS-2$
  }

  /**
   * Constructs a FourierTool and opens the specified xml file.
   *
   * @param fileName the name of the xml file
   */
  public FourierTool(String fileName) {
    super(fileName);
  }

  /**
   * Constructs a FourierTool and opens data in the specified xml control.
   *
   * @param control the xml control
   */
  public FourierTool(XMLControl control) {
    super(control);
  }

  /**
   * Constructs a FourierTool and loads the specified data object.
   *
   * @param data the data
   */
  public FourierTool(Data data) {
    super(data);
  }

  /**
   * Adds tabs for the specified Data object and proposes a name
   * for the tab. The name will be modified if not unique.
   *
   * @param data the Data
   * @param name a proposed tab name
   * @return the last added tab, if any
   */
  public DataToolTab addTab(Data data, String name) {
  	FourierToolTab tab = null;
	  ArrayList datasets = data.getDatasets();
	  if (datasets != null) {
	  	for (Iterator it = datasets.iterator(); it.hasNext();) {
	  		Dataset next = (Dataset)it.next();
	  		tab = new FourierToolTab(next, this);
	  		tab.setName(next.getName());
	      addTab(tab);
	  	}
	  }
    return tab;
  }

// ______________________________ protected methods _____________________________
  
  /**
   * Adds a tab. The tab should be named before calling this method.
   *
   * @param tab a DataToolTab
   */
  protected void addTab(DataToolTab tab) {
  	super.addTab(tab);
  	if (tab instanceof FourierToolTab) {
  		tab.fitCheckbox = null;
  		final FourierToolTab fTab = (FourierToolTab)tab;
      tab.addComponentListener(new ComponentAdapter() {
        public void componentResized(ComponentEvent e) {
          if (!fTab.sourceCheckbox.isSelected()) {
          	fTab.splitPanes[1].setDividerLocation(1.0);
          }
        }
      });
  	}
  }

  /**
   * Refreshes the GUI.
   */
  protected void refreshGUI() {
  	super.refreshGUI();
    setTitle(ToolsRes.getString("FourierTool.Frame.Title")); //$NON-NLS-1$
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
