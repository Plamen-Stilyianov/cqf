/*
 * Open Source Physics software is free software as described near the bottom of this code file.
 *
 * For additional information and documentation on Open Source Physics please see:
 * <http://www.opensourcephysics.org/>
 */

package org.opensourcephysics.davidson.applets;
import java.lang.reflect.*;
import java.util.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import org.opensourcephysics.display.*;
import org.opensourcephysics.controls.MessageFrame;
import org.opensourcephysics.controls.OSPLog;
import org.opensourcephysics.tools.ResourceLoader;
import org.opensourcephysics.tools.Resource;
import org.opensourcephysics.tools.Translator;
import org.opensourcephysics.controls.MainFrame;

/**
 * ApplicationApplet displays a button that invokes a static main method in a target class.
 * This Applet can be used to run an application's main method as an Applet.  Security restrictions may cause some
 * programs to malfunction if the jar file is not signed.
 *
 * @version    0.9 beta
 * @author     Wolfgang Chrstiain
 * @created    October 06, 2005
 */
public class ApplicationApplet extends JApplet {

   JFrame mainFrame = null;
   JButton showFramesButton = new JButton("Show");
   String targetClassName;
   ArrayList newFrames = new ArrayList();
   ArrayList existingFrames = new ArrayList();
   Class target;
   String[] args = null;
   boolean singleApp = false;
   String targetError=null;

   /**
    *  Gets the parameter attribute of the ApplicationApplet object
    *
    * @param  key  Description of Parameter
    * @param  def  Description of Parameter
    * @return      The parameter value
    */
   public String getParameter(String key, String def) {
      return((getParameter(key)!=null)
             ? getParameter(key)
             : def);
   }

   /**
    *  Initializes the applet
    */
   public void init() {
      super.init();
      OSPRuntime.applet = this;
      OSPRuntime.appletMode = false; // This insures that ALL frames are made visible when the applet is launched.
      if(getParameter("showLog", "false").toLowerCase().trim().equals("true")){
         OSPLog.showLog();
      }
      String arg0 = getParameter("xmldata", null);
      arg0 = getParameter("args[0]", arg0);
      if(arg0!=null) {
         args = new String[1];
      }
      String arg1 = getParameter("args[1]", null);
      String arg2 = getParameter("args[2]", null);
      if(arg2!=null) {
         args = new String[3];
         args[0] = arg0;
         args[1] = arg1;
         args[2] = arg2;
      }else if(arg1!=null) {
         args = new String[2];
         args[0] = arg0;
         args[1] = arg1;
      }else if(arg0!=null) {
         args = new String[1];
         args[0] = arg0;
      }
      targetClassName = getParameter("target", null);
      if(targetClassName==null) {
         targetClassName = getParameter("app", null);
      }
      if(targetClassName==null){
         readManifest();
      }
      String title = getParameter("title", null);
      singleApp = getParameter("singleapp", "false").trim().equalsIgnoreCase("true");
      if(title==null) {
         String[] s = targetClassName.split("[\056]"); // period character
         showFramesButton.setText(s[s.length-1]);
      } else {
         showFramesButton.setText(title);
      }
      getRootPane().getContentPane().add(showFramesButton, BorderLayout.CENTER);
      showFramesButton.addActionListener(new DisplayBtnListener());
   }

   /**
    * Determines whether the specified class is launchable.
    *
    * @param type the launch class to verify
    * @return <code>true</code> if the class is launchable
    */
   static boolean isLaunchable(Class type) {
      if(type==null) {
         return false;
      }
      try {
    	 // throws exception if method not found; return value not used
         type.getMethod("main", new Class[]{String[].class});
         return true;
      } catch(NoSuchMethodException ex) {
         return false;
      }
   }

   /**
    *  Destroys the applet's resources.
    */
   public void destroy() {
      disposeOwnedFrames();
      target = null;
      mainFrame = null;
      super.destroy();
   }

   private void readManifest(){
      //String launchJar = ResourceLoader.getLaunchJarPath();
      //System.out.println(launchJar);
      String archive = getParameter("archive", null);
      archive= archive.split(";")[0];
      OSPLog.finer("archive applet tag="+archive);
      if(archive==null) return;
      String name = "META-INF/MANIFEST.MF";
      Resource res = ResourceLoader.getResource(archive, name);
      if(res==null){
         OSPLog.fine("manifest not found in="+archive);
         return;
      }
      String manifest=res.getString();
      String[] lines= manifest.split("\n");
      for(int i=0, n=Math.min(10,lines.length); i<n; i++){
         int index=lines[i].indexOf("Main-Class:");
         if(index>=0){
         targetClassName=lines[i].substring("Main-Class:".length()+index).trim();
         OSPLog.fine("target class in manifest="+targetClassName);
         return;
         }
    }
   }


   private Class createTarget() {
      Class type = null;
      targetError=null;
      // create the class loader
      // ClassLoader classLoader = URLClassLoader.newInstance(new URL[] {getCodeBase()});
      ClassLoader classLoader = getClass().getClassLoader();
      try {
         type = classLoader.loadClass(targetClassName);
      } catch(ClassNotFoundException ex1) {
         System.err.println("Class not found: "+targetClassName);
         targetError="Class not found: "+targetClassName;
         return null;
      }
      if(!isLaunchable(type)) {
         System.err.println("Main method not found in "+targetClassName);
         targetError="Main method not found in "+targetClassName;
         return null;
      }
      // get the existing frames
      Frame[] frames = Frame.getFrames();
      existingFrames.clear();
      for(int i = 0, n = frames.length; i<n; i++) {
         existingFrames.add(frames[i]);
      }
      // load html data file
      String htmldata = getParameter("htmldata", null);
      if(htmldata!=null) {
         TextFrame htmlframe = new TextFrame(htmldata, type);
         htmlframe.setVisible(true);
      }
      // launch the app by invoking main method
      try {
         Method m = type.getMethod("main", new Class[]{String[].class});
         m.invoke(type, new Object[]{args});
         frames = Frame.getFrames();
         for(int i = 0, n = frames.length; i<n; i++) {
            if(frames[i] instanceof MainFrame){
              mainFrame=(JFrame)frames[i];
            }
            if((frames[i] instanceof JFrame)&&((JFrame) frames[i]).getDefaultCloseOperation()==JFrame.EXIT_ON_CLOSE) {
               ((JFrame) frames[i]).setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
               if(mainFrame==null) {
                  mainFrame = (JFrame) frames[i]; // assume this is the main application frame
               }
            }
            if(!existingFrames.contains(frames[i])) {
               // manage new frames
               newFrames.add(frames[i]);
            }
         }
      } catch(NoSuchMethodException ex) {
         System.err.println(ex);
         targetError=ex.toString();
      } catch(InvocationTargetException ex) {
         System.err.println(ex);
         System.err.println(ex.getStackTrace());
         targetError=ex.toString();
      } catch(IllegalAccessException ex) {
         System.err.println(ex);
         targetError=ex.toString();
      }
      return type;
   }

   private class DisplayBtnListener implements ActionListener {

      public void actionPerformed(ActionEvent e) {
         if(singleApp&&(OSPRuntime.applet!=ApplicationApplet.this)) {
            disposeOwnedFrames();
            target = null;
            mainFrame = null;
            OSPRuntime.applet = ApplicationApplet.this;
         }
         if(target==null) {
            target = createTarget();
            if(targetError!=null) target=null;
            return;
         }
         Frame[] frames = Frame.getFrames();
         for(int i = 0, n = frames.length; i<n; i++) {
           if(!frames[i].isDisplayable()){
             existingFrames.remove(frames[i]);
             newFrames.remove(frames[i]);
           }else if(!existingFrames.contains(frames[i])) {
               newFrames.add(frames[i]);
            }
            if (frames[i] instanceof MainFrame && frames[i].isDisplayable()) {
              mainFrame = (JFrame) frames[i];
            }
         }
         Iterator it = newFrames.iterator();
         while(it.hasNext()) {
            Frame frame = ((Frame) it.next());
            if(frame.isDisplayable()&&!(frame instanceof OSPLog)&&!(frame instanceof MessageFrame) && !(frame instanceof Translator)) {
               frame.setVisible(true);
            }
         }
         if(mainFrame!=null) {
            mainFrame.setState(Frame.NORMAL);
            mainFrame.setVisible(true);
            mainFrame.toFront();
         }
      }
   }

   private void disposeOwnedFrames() {
      Frame frame[] = Frame.getFrames();
      for(int i = 0, n = frame.length; i<n; i++) {
         if(frame[i].getClass().getName().startsWith("sun.plugin")) {
            continue; // don't mess with plugin
         }
         if((frame[i] instanceof JFrame)&&((JFrame) frame[i]).getDefaultCloseOperation()==JFrame.EXIT_ON_CLOSE) {
            ((JFrame) frame[i]).setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
         }
         if(!existingFrames.contains(frame[i])) {
            frame[i].setVisible(false);
            frame[i].dispose();
         }
      }
      newFrames.clear();
   }
}
/*
 * Open Source Physics software is free software; you can redistribute
 * it and/or modify it under the terms of the GNU General Public License (GPL) as
 * published by the Free Software Foundation; either version 2 of the License,
 * or(at your option) any later version.
 *
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
