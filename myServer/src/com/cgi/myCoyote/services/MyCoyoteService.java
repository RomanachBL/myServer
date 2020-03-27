package com.cgi.myCoyote.services;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import com.cgi.myCoyote.dtos.RequestObject;
import com.cgi.myCoyote.enumerations.Status;
import com.cgi.myCoyote.handlers.FirstDynamicHandler;
import com.cgi.myCoyote.handlers.RootHandler;
import com.cgi.myCoyote.handlers.SecDynamicHandler;
import com.cgi.myCoyote.handlers.ThirdDynamicHandler;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

public class MyCoyoteService {

	private BufferedReader inClient = null;
	private DataOutputStream outClient = null;

	private static RootHandler rootHandler = new RootHandler();
	private static FirstDynamicHandler firstDynamicHandler = new FirstDynamicHandler();
	private static SecDynamicHandler secDynamicHandler = new SecDynamicHandler();
	private static ThirdDynamicHandler thirdDynamicHandler = new ThirdDynamicHandler();

	/**
	 * Initialisation de myCoyote
	 * 
	 * @param client
	 * @throws Exception
	 */
	public void initCoyote(Socket client) throws Exception {
		List<RequestObject> listRequestObjects = new ArrayList<RequestObject>();

		// SIMULATION : récupération des @RequestMapping de Spring
		listRequestObjects = getRequestObjectsFromSpringProject();

		// Création des urls
		createSpringProjectContexts(client, listRequestObjects);
	}

	/**
	 * Permet de simuler la récupération des requêtes d'un projet Spring
	 * 
	 * @return une liste d'objets {@link RequestObject}
	 */
	private List<RequestObject> getRequestObjectsFromSpringProject() {
		List<RequestObject> listRequestObjects = new ArrayList<RequestObject>();

		RequestObject req1 = new RequestObject("/reqGET", "GET");
		RequestObject req2 = new RequestObject("/reqPOST", "POST");
		listRequestObjects.add(req1);
		listRequestObjects.add(req2);

		return listRequestObjects;
	}

	/**
	 * Créer les contextes selon la requête qu'envoie le client.
	 * 
	 * @param client
	 * @param listRequestObjects
	 * @throws Exception
	 */
	private void createSpringProjectContexts(Socket client, List<RequestObject> listRequestObjects) throws Exception {

		String response = "";
		boolean requestExist = false;
		Map<String, String> mapParams = new HashMap<>();

		inClient = new BufferedReader(new InputStreamReader(client.getInputStream()));
		outClient = new DataOutputStream(client.getOutputStream());

		String requestString = inClient.readLine();
		String headerLine = requestString;

		// Si pas de header, on stop.
		if (headerLine == null)
			return;

		// On récupère les infos du header
 		StringTokenizer tokenizer = new StringTokenizer(headerLine);
		String httpMethod = tokenizer.nextToken();
		String httpQuery = tokenizer.nextToken();
		
		// On commence par regarder si la request existe dans le projet Spring.
		for (RequestObject obj : listRequestObjects) {
			if (httpQuery.startsWith(obj.getRequest()))
				requestExist = true;
		}

		// Puis on récupère les paramètres (que ce soit en GET ou en POST).
		if (httpMethod.equals("GET"))
			mapParams = parseGET(httpQuery);
		else if (httpMethod.equals("POST"))
			mapParams = parsePOST(inClient);

		// Home page du serveur
		if (httpQuery.equals("/")) {
			response = rootHandler.handle(client);
		} else if (httpQuery.equals("/displayHeader")) {
			response = headerLine;
		}
		// Si la request existe dans le projet Spring, on utilise un DynamicHandler
		else if (requestExist == true) {
			response = firstDynamicHandler.handle(mapParams, httpMethod);

			/*
			 * TODO : 
			 * response = secDynamicHandler.handle(client, httpMethod);
			 * response = thirdDynamicHandler.handle(client, httpMethod);
			 */
		}
		// Sinon, erreur !!
		else {
			sendResponse(404, "<b>Erreur : requete non trouvée !</b>");
		}

		// On envoie finalement la réponse créé par le handler concerné.
		if (response != "" && response != null) {
			sendResponse(200, response);
		} else {
			sendResponse(404, response);
		}
	}

	public Map<String, String> parseGET(String request) throws IOException {
		Map<String, String> paramMap = new HashMap<String, String>();
		String paramStr = "";
		String[] paramPairs;
		String[] paramKv;

		if (request.contains("?")) {
			paramStr = request.split("\\?", 2)[1];
			paramPairs = paramStr.trim().split("&");

			for (String paramPair : paramPairs) {
				if (paramPair.contains("=")) {
					paramKv = paramPair.split("=");
					paramMap.put(paramKv[0], paramKv[1]);
				}
			}
		}

		return paramMap;
	}

	public Map<String, String> parsePOST(BufferedReader bf) throws IOException {
		
		Map<String, String> paramMap = new HashMap<String, String>();
		String str;
		StringBuilder strB = new StringBuilder();
		String json;
		
		while ((str = inClient.readLine()) != null){
			strB.append(str);
		}
		
		int firstIndex = strB.toString().indexOf("{");
		int secIndex = strB.toString().indexOf("}");
		
		json = strB.toString().substring(firstIndex, secIndex+1);
		
		ObjectMapper objMapper = new ObjectMapper();
		paramMap = objMapper.readValue(json, new TypeReference<HashMap<String,String>>(){});
		
		return paramMap;
	}

	/**
	 * Envoie la réponse.
	 * 
	 * @param statusCode
	 * @param responseString
	 * @throws Exception
	 */
	public void sendResponse(int statusCode, String responseString) throws Exception {

		String statusLine = null;
		String serverdetails = "Server: Java Server \r\n";
		String contentLengthLine = null;
		String contentTypeLine = "Content-Type: text/html \r\n";

		if (statusCode == 200)
			statusLine = Status.HTTP_200.toString() + "\r\n";
		else
			statusLine = Status.HTTP_404.toString() + "\r\n";

		contentLengthLine = "Content-Length" + responseString.length() + "\r\n";

		outClient.writeBytes(statusLine);
		outClient.writeBytes(serverdetails);
		outClient.writeBytes(contentTypeLine);
		outClient.writeBytes(contentLengthLine);
		outClient.writeBytes("Connection: close \r\n");
		outClient.writeBytes("\r\n");

		// La réponse
		outClient.writeBytes(responseString);
		outClient.close();
	}

}
