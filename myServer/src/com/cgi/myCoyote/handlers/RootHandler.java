package com.cgi.myCoyote.handlers;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

public class RootHandler {

	public String handle(Socket client) throws IOException {
		String res = "<h2>Serveur démarré" + "<h3>Port: "
				+ client.getLocalPort() + "</h3>";

		return res;
	}
}
