package org.opensourcephysics.tools;

import javax.swing.JFrame;

public class DataToolApp {

	/**
	 * Creates a stand alone data tool.
	 * @param args
	 */
	public static void main(String[] args) {
		DataTool dt = new DataTool();
		dt.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		dt.setVisible(true);
	}
}
