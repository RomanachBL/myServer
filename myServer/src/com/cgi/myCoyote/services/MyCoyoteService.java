package com.cgi.myCoyote.services;

import java.util.ArrayList;
import java.util.List;

import com.cgi.myCoyote.dtos.RequestObject;
import com.cgi.myCoyote.handlers.DisplayHeaderHandler;
import com.cgi.myCoyote.handlers.FirstDynamicHandler;
import com.cgi.myCoyote.handlers.RootHandler;
import com.cgi.myCoyote.handlers.SecDynamicHandler;
import com.cgi.myCoyote.handlers.ThirdDynamicHandler;
import com.sun.net.httpserver.HttpServer;

public class MyCoyoteService {
	
	public List<RequestObject> getRequestObjectsFromSpringProject(){
		List<RequestObject> listRequestObjects = new ArrayList<RequestObject>();
		
		// SIMULATION : récupération des @RequestMapping de Spring
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
		
		
		/********* (Simulation) Création des contextes récupérées de Spring *********/
		
		// 1er test : juste pour afficher les paramètres
		for(RequestObject obj : listRequestObjects) {
			myServer.createContext("/displayParams"+obj.getRequest(), new FirstDynamicHandler());
		}
		
		// 2e test : nous voulons renvoyer du code html
		// TODO
		for(RequestObject obj : listRequestObjects) {
			myServer.createContext("/displayJSP"+obj.getRequest(), new SecDynamicHandler());
		}
		
		// 2e test : nous voulons renvoyer du code html
		// TODO
		for(RequestObject obj : listRequestObjects) {
			myServer.createContext("/displayServlet"+obj.getRequest(), new ThirdDynamicHandler());
		}
	}

}
