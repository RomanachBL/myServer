package com.cgi;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;

import com.cgi.myCoyote.dtos.RequestObject;
import com.cgi.myCoyote.services.MyCoyoteService;
import com.sun.net.httpserver.HttpServer;

public class Server {
	
	private static MyCoyoteService myCoyoteService = new MyCoyoteService();
	
	public static void main(String[] args) throws IOException {
		List<RequestObject> listRequestObjects = new ArrayList<RequestObject>();
		
		HttpServer myServer = HttpServer.create(new InetSocketAddress(8080), 0);
		
		/********* myCoyote : Création de tous les contextes ************/
		
		listRequestObjects = myCoyoteService.getRequestObjectsFromSpringProject();
		
		myCoyoteService.createSpringProjectContexts(myServer, listRequestObjects);
		
		/*****************************************************/
		
		// Permet de retourner un getExecutor si le setExecutor existe.
		myServer.setExecutor(null);
		myServer.start();
		System.out.println("Serveur en écoute sur le port "+ myServer.getAddress().getPort());
	}
}
