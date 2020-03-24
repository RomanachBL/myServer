package com.cgi.myCoyote.services;

import java.util.ArrayList;
import java.util.List;

import com.cgi.myCoyote.dtos.RequestObject;
import com.cgi.myCoyote.handlers.DisplayHeaderHandler;
import com.cgi.myCoyote.handlers.DynamicHandler;
import com.cgi.myCoyote.handlers.RootHandler;
import com.sun.net.httpserver.HttpServer;

public class MyCoyoteService {
	
	public List<RequestObject> getRequestObjectsFromSpringProject(){
		List<RequestObject> listRequestObjects = new ArrayList<RequestObject>();
		
		// Simulation : récupération des @RequestMapping de Spring
		RequestObject req1 = new RequestObject("/reqGET", "GET");
		RequestObject req2 = new RequestObject("/reqPOST", "POST");
		listRequestObjects.add(req1);
		listRequestObjects.add(req2);
		
		return listRequestObjects;
	}
	
	public void createSpringProjectContexts(HttpServer myServer, List<RequestObject> listRequestObjects) {
		// Créer le context (un root et un permettant d'afficher le Header).
		myServer.createContext("/", new RootHandler());
		myServer.createContext("/displayHeader", new DisplayHeaderHandler());
		
		// (Simulation) Création des contextes récupérées de Spring
		for(RequestObject obj : listRequestObjects) {
			myServer.createContext(obj.getRequest(), new DynamicHandler());
		}
	}

}
