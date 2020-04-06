package com.cgi.myCoyote.dtos;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "servlet")
public class Servlet {

	@XmlElement(name = "servlet-name")
	private String name;
	
	@XmlElement(name = "servlet-class")
	private String className;

	public Servlet() {
		super();
		// TODO Auto-generated constructor stub
	}

	public Servlet(String servletName, String servletClass) {
		super();
		this.name = servletName;
		this.className = servletClass;
	}

	public String getServletName() {
		return name;
	}

	public void setServletName(String servletName) {
		this.name = servletName;
	}

	public String getServletClass() {
		return className;
	}

	public void setServletClass(String servletClass) {
		this.className = servletClass;
	}

	@Override
	public String toString() {
		return "Servlet [servletName=" + name + ", servletClass=" + className + "]";
	}
	
}
