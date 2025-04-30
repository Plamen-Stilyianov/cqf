package org.opensourcephysics.tools;

import javax.swing.JFrame;

public class FourierToolApp {

	/**
	 * Creates a stand alone data tool.
	 * @param args
	 */
	public static void main(String[] args) {
		DataTool dt = new FourierTool();
		dt.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		dt.setVisible(true);
	}
}
