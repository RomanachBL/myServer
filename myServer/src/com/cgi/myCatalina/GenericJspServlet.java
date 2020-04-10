package com.cgi.myCatalina;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.Socket;
import java.util.Map;

public class GenericJspServlet {

	public String exec(Socket client, String header, String method, Map<String, String> mapParams, String jspPath) throws IOException {
		
		/*
		 * Normalement, JASPER parse les JSP afin de les compiler en code JAVA en tant que servlets.
		 * La manoeuvre étant très compliquée, cette servlet ne servira que de passerelle vers MyJasper.
		 * MyJasper va récupérer la JSP, la parser, puis renvoyer la réponse (String en HTML).
		 */
		
		String response = "";
		Class<?> servletClass;
		String jspContent = "";
		StringBuilder contentBuilder = new StringBuilder();
		
		// Tout d'abord on récupère le content de la jsp
		FileReader fr = new FileReader(System.getProperty("user.dir") + jspPath);
		BufferedReader br = new BufferedReader(fr);
		
		String sCurrentLine;
        while ((sCurrentLine = br.readLine()) != null) 
        {
            contentBuilder.append(sCurrentLine).append("\n");
        }
        
        jspContent = contentBuilder.toString();
		
		try {
			servletClass = Class.forName("com.cgi.myJasper.MyJasper");

			Object servletInstance = servletClass.newInstance();

			Method execMethod = servletInstance.getClass().getMethod("execJasper", String.class, Map.class);

			response = (String) execMethod.invoke(servletInstance, jspContent , mapParams);
			
		} catch (ClassNotFoundException | InstantiationException | IllegalAccessException | NoSuchMethodException
				| SecurityException | IllegalArgumentException | InvocationTargetException e) {
			e.printStackTrace();
		}
		
		return "¤"+response+"¤";
	}
}
