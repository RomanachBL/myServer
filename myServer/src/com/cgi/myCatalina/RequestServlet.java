package com.cgi.myCatalina;

import java.io.IOException;
import java.net.Socket;
import java.util.Map;

public class RequestServlet {

	public String exec(Socket client, String header, String method, Map<String, String> mapParams) throws IOException {
		// send response
		StringBuilder response = new StringBuilder("");
		response.append("<h2>"+method+" - Affichage des paramètres</h2><br>");
		for (String key : mapParams.keySet()) {
			response.append("<span>" + key + " = " + mapParams.get(key) + "</span><br>");
		}
		return response.toString();
	}
}
