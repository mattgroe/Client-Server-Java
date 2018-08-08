package client;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.IOException;
import java.net.Socket;
import java.lang.Integer;

import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.event.TreeSelectionListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.tree.TreeSelectionModel;
import javax.swing.JOptionPane;
import java.net.URL;
import java.io.IOException;
import java.io.FileWriter;
import java.io.FileOutputStream;
import java.io.FileInputStream;
import javax.swing.JEditorPane;
import javax.swing.text.EditorKit;
import javax.swing.text.rtf.RTFEditorKit;
import javax.swing.JScrollPane;
import javax.swing.JFrame;
import javax.swing.event.HyperlinkListener;
import javax.swing.event.HyperlinkEvent;
import javax.swing.tree.TreePath;
import java.awt.event.WindowListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.ByteArrayInputStream;
import java.io.StringBufferInputStream;
/**
 * Copyright (c) 2018 Tim Lindquist,
 * Software Engineering,
 * Arizona State University at the Polytechnic campus
 * <p/>
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation version 2
 * of the License.
 * <p/>
 * This program is distributed in the hope that it will be useful,
 * but without any warranty or fitness for a particular purpose.
 * <p/>
 * Please review the GNU General Public License at:
 * http://www.gnu.org/licenses/gpl-2.0.html
 * see also: https://www.gnu.org/licenses/gpl-faq.html
 * so you are aware of the terms and your rights with regard to this software.
 * Or, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301,USA
 * <p/>
 * Purpose: Sample Java Swing controller class. BrowserGUI constructs the view components
 * for a simple broswer GUI. This class is extends the GUI to provide the controller.
 * It contains sample control functions that respond to button clicks and hotline clicks
 *
 * This software is meant to run on Debian Wheezy Linux
 * <p/>
 * Ser321 Principles of Distributed Software Systems
 * see http://pooh.poly.asu.edu/Ser321
 * @author Tim Lindquist (Tim.Lindquist@asu.edu) CIDSE - Software Engineering,
 *                       IAFSE, ASU at the Polytechnic campus
 * @file    BrowserStudent.java
 * @date    February, 2018
 **/
public class BrowserStudent extends BrowserGUI
                            implements ActionListener,HyperlinkListener {

   private static final boolean debugOn = true;

   private URL helpURL;

   private Socket socket;
   private BufferedReader input;
   private PrintWriter output;

   public BrowserStudent(String arg1, String arg2) throws IOException{
      super(".");

      //Setup Network
      System.out.println("syntax: java -cp classes client.BrowserStudent");
      String ipadd = arg1;
      System.out.println("Connecting to Server IP Address: " + ipadd);
      int port = Integer.parseInt(arg2);
      System.out.println("Connecting to Port: " + port);
      socket = new Socket(ipadd, port);
      //creating a reader for input from the server
      input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
      //creating a writer for output to the server
      output = new PrintWriter(socket.getOutputStream(), true);
      //line reader for input from server
      String answer = input.readLine();
      //printing data from server
      System.out.println("Server answer: " + answer);

      //layout GUI
      WindowListener wl = new WindowAdapter() {
         public void windowClosing(WindowEvent e) {
	     System.out.println("Socket has been closed.\n->Terminating program.");
	     output.println("exit");
	     System.exit(0);
	 }
      };
      this.addWindowListener(wl);
      displayButt.addActionListener(this);
      homeButt.addActionListener(this);
      backButt.addActionListener(this);
      initHelp();
      htmlPane.setEditable(false);
      htmlPane.addHyperlinkListener(this);
   }

   public void hyperlinkUpdate(HyperlinkEvent e) {
      if(HyperlinkEvent.EventType.ACTIVATED.equals(e.getEventType())) {
         debug("activated a hyperlink.");
         // Do something with e.getURL() here
      }else if(HyperlinkEvent.EventType.ENTERED.equals(e.getEventType())) {
         debug("entered a hyperlink.");
         // Do something?
      }else if(HyperlinkEvent.EventType.EXITED.equals(e.getEventType())) {
         debug("exited a hyperlink.");
         // Do something?
      }
   }

   /**
    * actionPerformed is defined by the ActionListener interface.
    * An object of Browser registers itself to hear about action events
    * caused by the <b>Button Clicks</b> and <b>Menu selecions (none here)</b>.
    * @param ActionEvent the event object created by the source of the
    * button push (the JButton object.)
    */
   public void actionPerformed(ActionEvent e) {
      try{
         if (e.getActionCommand().equals("Show/Refresh")){
            debug("Show/Refresh Page "+urlTF.getText());
            String urlStr = "http://pooh.poly.asu.edu/Ser321/index.html";
            //Sending 'index' to server for response
            String page = "index";
	    output.println(page);
	    //server gives response in html	
	    String resp = input.readLine();
	    System.out.println("Response from server: " + resp);

	    htmlPane.setText(resp);
         }else if(e.getActionCommand().equals("Home")){
            debug("Go to home page clicked");
	    //Sending 'home' to server for response
	    String page = "syllabus";
	    output.println(page);
	    //server gives response in html
	    String resp = input.readLine();
	    System.out.println("Response from server: " + resp);

	    htmlPane.setText(resp);
         }else if(e.getActionCommand().equals("Back")){
            debug("Back Button clicked");
	    //Sending 'back' to server for response
	    String page = "back";
	    output.println(page);
	    //server gives response in html
	    String resp = input.readLine();
	    System.out.println("Response from server: " + resp);
	
	    htmlPane.setText(resp);
         }
      }catch (Exception ex) {
         JOptionPane.showMessageDialog(this, "Exception: "+ex.getMessage());
         ex.printStackTrace();
      }
   }

   private void initHelp() {
      String s = null;
      try {
         s = "file://" 
            + System.getProperty("user.dir")
            + System.getProperty("file.separator")
            + "TreeDemoHelp.html";
         debug("Help URL is " + s);
         helpURL = new URL(s);
         displayURL(helpURL);
      } catch (Exception e) {
         System.err.println("Couldn't create help URL: " + s + " exception: "
                            +e.getMessage());
      }
   }

   private void displayURL(URL url) {
      try {
         htmlPane.setPage(url);
      } catch (IOException e) {
         System.err.println("Attempted to read a bad URL: " + url);
      }
   }
   private void debug(String message) {
      if (debugOn)
         System.out.println("debug: "+message);
   }


   /********************************************************
			MAIN METHOD
   ********************************************************/
   //Takes in IP Address of Rasperry Pie and Port number as command line arguments
   public static void main(String[] args) throws IOException{
      /*System.out.println("syntax: java -cp classes client.BrowserStudent");
      String ipadd = args[0];
      System.out.println(ipadd);
      int port = Integer.parseInt(args[1]);
      socket = new Socket(ipadd, port);
      input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
      output = new PrintWriter();
      String answer = input.readLine();
      System.out.println(answer);*/
      new BrowserStudent(args[0], args[1]);
   }
}
