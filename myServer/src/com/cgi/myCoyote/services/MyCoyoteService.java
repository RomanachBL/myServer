package com.cgi.myCoyote.services;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import com.cgi.myCoyote.dtos.Servlet;
import com.cgi.myCoyote.dtos.ServletMapping;
import com.cgi.myCoyote.dtos.WebAppObject;
import com.cgi.myCoyote.enumerations.Status;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

public class MyCoyoteService {

	private BufferedReader inClient = null;
	private DataOutputStream outClient = null;

	/**
	 * Traite la requête qu'envoie le client.
	 * 
	 * @param client
	 * @throws Exception
	 */
	public void launchMyCoyote(Socket client) throws Exception {

		StringBuilder headerBuilder = new StringBuilder();
		String header = "";
		String webXmlResult = "";
		StringTokenizer tokenizer = null;
		String httpMethod = "";
		String httpQuery = "";
		String response = "";
		Map<String, String> mapParams = new HashMap<>();

		inClient = new BufferedReader(new InputStreamReader(client.getInputStream()));
		outClient = new DataOutputStream(client.getOutputStream());

		/********* Récupération du header *********/

		headerBuilder.append(inClient.readLine() + "\r\n");
		headerBuilder.append(inClient.readLine() + "\r\n");

		header = headerBuilder.toString();

		// On récupère les infos du header
		tokenizer = new StringTokenizer(header);
		// GET ou POST
		httpMethod = tokenizer.nextToken();
		// La query just après
		httpQuery = tokenizer.nextToken();

		// On rajoute le JSON si on est en méthode POST
		if (httpMethod.equals("POST")) {
			headerBuilder.append(inClient.readLine() + "\r\n");
			header = headerBuilder.toString();
		}

		/******************************************/

		// On récupère les paramètres (que ce soit en GET ou en POST).
		if (httpMethod.equals("GET"))
			mapParams = parseGET(httpQuery);
		else if (httpMethod.equals("POST"))
			mapParams = parsePOST(header);

		// On récupère le nom de la classe servlet (venant du web.xml)
		webXmlResult = getServletFromWebXml(httpQuery);
		
		// Si on a trouvé un resultat, on l'exécute
		if (webXmlResult != null && webXmlResult != "") {
			// Si c'est une servlet, on invoke la servlet correspondante
			if(webXmlResult.startsWith("com.cgi.myCatalina")) {
				response = invokeServlet(webXmlResult, client, header, httpMethod, mapParams);
			}
			// Si c'est une JSP, on invoke le parseur de JSP correspondant
			else if (webXmlResult.startsWith("/src/resources/jsp")) {
				response = invokeServletWithJsp(webXmlResult, client, header, httpMethod, mapParams);
			}
		}
		// Si on n'a pas de webXmlResult -> 404
		else {
			sendResponse(404, "<b>Erreur : requete non trouvée !</b>");
		}

		// On envoie finalement la réponse créée par la servlet concerné.
		if (response != "" && response != null) {
			sendResponse(200, response);
		} else {
			sendResponse(404, response);
		}
	}

	/**
	 * Parse les params si on a un GET
	 * 
	 * @param request
	 * @return Map<String, String> des paramètres
	 * @throws IOException
	 */
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

	/**
	 * Parse les params si on a un POST
	 * 
	 * @param header
	 * @return Map<String, String> des paramètres
	 * @throws IOException
	 */
	public Map<String, String> parsePOST(String header) throws IOException {

		Map<String, String> paramMap = new HashMap<String, String>();
		String json;

		int firstIndex = header.toString().indexOf("{");
		int secIndex = header.toString().indexOf("}");

		json = header.toString().substring(firstIndex, secIndex + 1);

		ObjectMapper objMapper = new ObjectMapper();
		paramMap = objMapper.readValue(json, new TypeReference<HashMap<String, String>>() {
		});

		return paramMap;
	}

	/**
	 * Regarde dans le web.xml et récupère la servlet correspondant à la requête
	 * http que l'on reçoit
	 * 
	 * @param query
	 * @return String - le nom de la class servlet (de la forme
	 *         com.cgi.myCatalina.leNomDeLaServletClass).
	 * @throws ClassNotFoundException
	 */
	private String getServletFromWebXml(String query) throws ClassNotFoundException {

		String request = "";
		String servletName = "";
		String servletClass = "";
		String jspPath = "";
		JAXBContext jaxbContext;
		Unmarshaller jaxbUnmarshaller = null;
		WebAppObject webAppObj = null;
		List<ServletMapping> servletMappingList = new ArrayList<ServletMapping>();
		List<Servlet> servletList = new ArrayList<Servlet>();

		if (query.contains("?")) {
			int index = query.indexOf("?");
			request = query.substring(0, index);
		} else {
			request = query;
		}

		try {
			File xmlFile = new File(System.getProperty("user.dir") + "/src/com/cgi/myCoyote/web.xml");

			// On récupère le web.xml dans l'objet 'WebAppObject'
			jaxbContext = JAXBContext.newInstance(WebAppObject.class);
			jaxbUnmarshaller = jaxbContext.createUnmarshaller();
			webAppObj = (WebAppObject) jaxbUnmarshaller.unmarshal(xmlFile);

		} catch (JAXBException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// Puis on parcourt les 'ServletMapping' pour retrouver la bonne servletClass
		// dans l'objet 'Servlet'
		servletMappingList = webAppObj.getServletMappings();
		servletList = webAppObj.getServlets();

		for (ServletMapping sMap : servletMappingList) {
			// TODO :
			// A remplacer par une regex pour les cas ' /toto/* ' par exemple
			if (request.equals(sMap.getUrlPattern())) {
				servletName = sMap.getServletName();
			}
		}

		if (servletName != null && servletName != "") {
			for (Servlet s : servletList) {
				if (servletName.equals(s.getServletName())) {
					if(s.getServletClass() != null && s.getServletClass() != "") {
						servletClass = s.getServletClass();
						return servletClass;
					}
					else { 
						jspPath = s.getJspFile();
						return jspPath;
					}
				}
			}
		}

		return null;
	}

	/**
	 * Permet d'exécuter une servlet
	 * 
	 * @param servletClassName
	 * @param client
	 * @param header
	 * @param httpMethod
	 * @param mapParams
	 * @return
	 */
	public String invokeServlet(String servletClassName, Socket client, String header, String httpMethod,
			Map<String, String> mapParams) {
		
		String response = "";
		Class<?> servletClass;
		
		try {
			servletClass = Class.forName(servletClassName);

			Object servletInstance = servletClass.newInstance();

			Method execMethod = servletInstance.getClass().getMethod("exec", Socket.class, String.class, String.class, Map.class);

			response = (String) execMethod.invoke(servletInstance, client, header, httpMethod, mapParams);
			
		} catch (ClassNotFoundException | InstantiationException | IllegalAccessException | NoSuchMethodException
				| SecurityException | IllegalArgumentException | InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return response;
	}
	
	/**
	 * Permet d'exécuter Jasper si on a une JSP
	 * 
	 * @param servletClassName
	 * @param client
	 * @param header
	 * @param httpMethod
	 * @param mapParams
	 * @return
	 */
	public String invokeServletWithJsp(String jspPath, Socket client, String header, String httpMethod,
			Map<String, String> mapParams) {
		
		String response = "";
		Class<?> servletClass;
		
		try {
			servletClass = Class.forName("com.cgi.myCatalina.GenericJspServlet");

			Object servletInstance = servletClass.newInstance();

			Method execMethod = servletInstance.getClass().getMethod("exec", Socket.class, String.class, String.class, Map.class, String.class);

			response = (String) execMethod.invoke(servletInstance, client, header, httpMethod, mapParams, jspPath);
			
		} catch (ClassNotFoundException | InstantiationException | IllegalAccessException | NoSuchMethodException
				| SecurityException | IllegalArgumentException | InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return response;
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

		contentLengthLine = "Content-Length: " + responseString.length() + "\r\n";

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
