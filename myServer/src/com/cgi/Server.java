package com.cgi;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import com.cgi.myCoyote.services.MyCoyoteService;

public class Server extends Thread {
	
	private Socket client = null;

    public Server(Socket cl) {
    	client = cl;
    }
	
	private static MyCoyoteService myCoyoteService = new MyCoyoteService();
	
	public static void main(String[] args) throws IOException {
		int port = 9090;
		
		// La queue de connexion prendra maximum 10 clients.
		ServerSocket myServer = new ServerSocket(port, 10);

		System.out.println("Serveur en �coute sur le port "+ myServer.getLocalPort());

		/**
		 * Une loop qui �coute en continu sur le port de notre serveur.
		 * Un objet Server sera cr�� pour chaque client.
		 */
		while (true) {
		    Socket connection = myServer.accept();
		    Server httpServer = new Server(connection);
		    httpServer.start();
		}
	}
	
	public void run() {
		/********* myCoyote : Cr�ation de tous les contextes ************/
		try {
			myCoyoteService.launchMyCoyote(client);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
