package com.cgi.myCatalina;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

public class Handler implements HttpHandler {

	public void handle(HttpExchange echange) throws IOException {
		// "GET" ou "POST"
		String requete = echange.getRequestMethod();
		if (requete.equalsIgnoreCase("GET")) {
			// On configure l'entête de la réponse
			Headers reponseEntete = echange.getResponseHeaders();
			reponseEntete.set("Content-Type", "text/plain");
			// On renvoie une réponse 200 
			echange.sendResponseHeaders(200, 0);

			OutputStream reponse = echange.getResponseBody();
			Headers requeteEntete = echange.getRequestHeaders();
			Set<String> keySet = requeteEntete.keySet();
			Iterator<String> iter = keySet.iterator();
			while (iter.hasNext()) {
				String key = iter.next();
				List values = requeteEntete.get(key);
				String s = key + " = " + values.toString() + "\n";
				reponse.write(s.getBytes());
			}
			reponse.close();
			System.out.println("Fin handler !");
		}
	}
}
