package com.cgi.myCoyote.enumerations;

public enum Status {
	HTTP_200("HTTP/1.1 200 OK"), 
	HTTP_404("HTTP/1.1 404 Not Found");

	private String status = "";

	// Constructeur
	Status(String status){
	    this.status = status;
	  }

	public String toString() {
		return status;
	}
}
