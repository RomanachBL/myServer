package com.cgi.myCoyote.dtos;

public class RequestObject {

	private String request;
	
	private String methode;
	
	public RequestObject(String request, String methode) {
		super();
		this.request = request;
		this.methode = methode;
	}

	public String getRequest() {
		return request;
	}

	public void setRequest(String request) {
		this.request = request;
	}

	public String getMethode() {
		return methode;
	}

	public void setMethode(String methode) {
		this.methode = methode;
	}
	
}
