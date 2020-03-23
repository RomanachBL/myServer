package com.cgi;

import java.io.IOException;
import java.net.InetSocketAddress;

import com.cgi.myCatalina.Handler;
import com.sun.net.httpserver.HttpServer;

public class Server {
	public static void main(String[] args) throws IOException {
		HttpServer myServer = HttpServer.create(new InetSocketAddress(8080), 0);
		
		// Créer le context (handler facultatif dans le deuxième argument).
		myServer.createContext("/", new Handler());
		// Permet de retourner un getExecutor si le setExecutor existe.
		myServer.setExecutor(null);
		
		myServer.start();
		System.out.println("Serveur en écoute sur le port "+ myServer.getAddress().getPort());
	}
}
