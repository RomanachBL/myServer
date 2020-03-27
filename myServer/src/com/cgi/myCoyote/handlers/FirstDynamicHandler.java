package com.cgi.myCoyote.handlers;

import java.io.IOException;
import java.util.Map;

public class FirstDynamicHandler {

	public String handle(Map<String, String> mapParams, String method) throws IOException {
		// send response
		StringBuilder response = new StringBuilder("");
		response.append("<h2>"+method+" - Affichage des paramètres</h2><br>");
		for (String key : mapParams.keySet()) {
			response.append("<span>" + key + " = " + mapParams.get(key) + "</span><br>");
		}
		return response.toString();
	}
}
