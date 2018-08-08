package server;

import java.io.IOException;
import java.io.BufferedReader;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.lang.Integer;
import java.io.File;
import java.io.FileReader;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.StringBuilder;

public class BrowserServer {
	
	/**********************************************************
				MAIN METHOD
	**********************************************************/
	public static void main(String[] args) throws IOException {
		if (args.length != 1) {
			System.err.println("No arguments being passed");
		}
		int port = Integer.parseInt(args[0]);
		ServerSocket listener = new ServerSocket(port);
		try {

			while (true) {
				/*PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
				out.println("Hitting this socket");*/
				Handler h = new Handler(listener.accept());
				System.out.println("Handler created.");
				h.run();
				System.out.println("Handler Finished. Exiting listener socket.");
			}		

		} finally {
			listener.close();
		}
	}
}

class Handler extends Thread {

	Socket socket;
	String page = "index";
	BufferedReader input;
	PrintWriter output;
	String content;
	String previous;

	public Handler(Socket socket) {
		this.socket = socket;
		try {
			input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			output = new PrintWriter(socket.getOutputStream(), true);
			output.println("HTML Handler connected");
		} catch (IOException e) {
			System.out.println("Server not connecting...");
		}
	} //end of Handler

	// html input reader
	public void htmlReader(String file) {
		previous = content;
		String fileHtml = file + ".html";
		String filePath = "";
		switch (file) {
			case "index": filePath = "src/server/www/Ser321/" + fileHtml;
				      filePath = new File(filePath).getAbsolutePath();
				      System.out.println("Path: " + filePath);
				      break;
			case "syllabus": filePath = "src/server/www/Ser321/Syllabus/" + fileHtml;
				     filePath = new File(filePath).getAbsolutePath();
				     System.out.println("Path: " + filePath);
				     break;
			default: System.out.println("not input");
				 break;
		}
		StringBuilder contentBuilder = new StringBuilder();
		try {
			BufferedReader in = new BufferedReader(new FileReader(filePath));
			String str;
			while ((str = in.readLine()) != null) {
				contentBuilder.append(str);
			}
			in.close();
		} catch (IOException e) {
			System.err.println(e);
		}
		content = contentBuilder.toString();
	}


	//run method of Handler
	public void run() {
		try {
			while (true) {
				page = input.readLine();
				System.out.println("Client asking for " + page);
				if (page.equals("index")) {
					//If statements for getting pages
					htmlReader(page);
					System.out.println("Getting page: " + page);
					output.println(content);	
				} else if (page.equals("syllabus")) {
					//If statements for getting pages
					htmlReader(page);
					System.out.println("Getting page: " + page);
					output.println(content);
				} else if (page.equals("back")) {
					//If statements for getting pages
					htmlReader(page);
					System.out.println("Getting page: " + page);
					output.println(content);
				} else {
					System.out.println("Client disconnected. Closing socket.");
					return;
				}
			}
		} catch (IOException e) {
			System.out.println("Client disconnected");
		} finally {
			try {socket.close();} catch (IOException e) {}
		}
	}
}




	/*public static void main(String[] args) throws IOException {
		if (args.length != 1) {
			System.err.println("No arguments being passed");
		}
		int port = Integer.parseInt(args[0]);
		ServerSocket listener = new ServerSocket(port);
		try {

			while (true) {
				Socket socket = listener.accept();
				try {
					BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
					PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
					out.println("Hitting this socket");

					String page = in.readLine();
					System.out.println("Client request for " + page);
				} finally {
					//socket.close();
				}
			}		

		} finally {
			listener.close();
		}
	}*/
