package com.cgi.myCatalina;

import java.io.IOException;
import java.net.Socket;
import java.util.Map;

public class RootServlet {

	public String exec(Socket client, String header, String method, Map<String, String> mapParams) throws IOException {
		String res = "<h2>Serveur démarré</h2>" + "<h3>Port: "
				+ client.getLocalPort() + "</h3>";

		return "¤"+res+"¤";
	}
}
