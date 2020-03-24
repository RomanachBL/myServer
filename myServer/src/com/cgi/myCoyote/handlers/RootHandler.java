package com.cgi.myCoyote.handlers;

import java.io.IOException;
import java.io.OutputStream;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

public class RootHandler implements HttpHandler {

	public void handle(HttpExchange echange) throws IOException {
		String requete = "<h2>Serveur démarré" + "<h3>Port: "
				+ echange.getLocalAddress().getPort() + "</h3>";

		// On renvoie une réponse 200
		echange.sendResponseHeaders(200, 0);
		
		OutputStream reponse = echange.getResponseBody();
		reponse.write(requete.getBytes());
		reponse.close();
		System.out.println("Fin RootHanlder !");
	}
}
