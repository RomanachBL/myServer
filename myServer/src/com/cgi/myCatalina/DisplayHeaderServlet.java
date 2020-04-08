package com.cgi.myCatalina;

import java.io.IOException;
import java.net.Socket;
import java.util.Map;

public class DisplayHeaderServlet {
	
	public String exec(Socket client, String header, String method, Map<String, String> mapParams) throws IOException {
		
		return "¤"+header+"¤";
		
	}
}
