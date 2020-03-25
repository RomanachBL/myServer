package com.cgi.myCoyote.handlers;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

public class SecDynamicHandler implements HttpHandler {

	public void handle(HttpExchange echange) throws IOException {
		// "GET" ou "POST"
		String requete = echange.getRequestMethod();
		if (requete.equalsIgnoreCase("GET")) {
			this.handleGET(echange);
		} else if (requete.equalsIgnoreCase("POST")) {
			this.handlePOST(echange);
		}
	}

	public void handleGET(HttpExchange echange) throws IOException {
		System.out.println("Start handleGET");

		// parse request
		Map<String, Object> parameters = new HashMap<String, Object>();
		URI requestedUri = echange.getRequestURI();
		String query = requestedUri.getRawQuery();
		this.parseQuery(query, parameters);

		// send response
		StringBuilder response = new StringBuilder("");
		response.append("<h2>GET - Affichage de la JSP</h2><br>");
		
		
		
		response.append("                                la JSP                            ");
		
		
		
		echange.sendResponseHeaders(200, response.length());
		OutputStream os = echange.getResponseBody();
		os.write(response.toString().getBytes());

		os.close();
	}

	public void handlePOST(HttpExchange echange) throws IOException {
		System.out.println("Start handlePOST");

		// parse request
		Map<String, Object> parametersMap = new HashMap<String, Object>();
		InputStreamReader isr = new InputStreamReader(echange.getRequestBody(), "utf-8");
		BufferedReader br = new BufferedReader(isr);
		
		// Disons qu'on capte du JSON
		StringBuilder jsonBuilder = new StringBuilder();
		String str;
		
		while ((str = br.readLine()) != null){
			jsonBuilder.append(str);                 
		}
		
		ObjectMapper objMapper = new ObjectMapper();
		parametersMap = objMapper.readValue(jsonBuilder.toString(), new TypeReference<HashMap<String,Object>>(){});

		// send response
		StringBuilder response = new StringBuilder("");
		response.append("<h2>POST - Affichage de la JSP</h2><br>");
		
		
		
		response.append("                                la JSP                            ");
		
		
		
		
		echange.sendResponseHeaders(200, response.length());
		OutputStream os = echange.getResponseBody();
		os.write(response.toString().getBytes());
		os.close();
	}

	// Permet de parser les paramètres envoyés
	public void parseQuery(String query, Map<String, Object> parameters) throws UnsupportedEncodingException {

		if (query != null) {
			String pairs[] = query.split("[&]");
			for (String pair : pairs) {
				String param[] = pair.split("[=]");
				String key = null;
				String value = null;
				if (param.length > 0) {
					key = URLDecoder.decode(param[0], System.getProperty("file.encoding"));
				}
				if (param.length > 1) {
					value = URLDecoder.decode(param[1], System.getProperty("file.encoding"));
				}
				if (parameters.containsKey(key)) {
					Object obj = parameters.get(key);
					if (obj instanceof List<?>) {
						List<String> values = (List<String>) obj;
						values.add(value);

					} else if (obj instanceof String) {
						List<String> values = new ArrayList<String>();
						values.add((String) obj);
						values.add(value);
						parameters.put(key, values);
					}
				} else {
					parameters.put(key, value);
				}
			}
		}
	}
}
