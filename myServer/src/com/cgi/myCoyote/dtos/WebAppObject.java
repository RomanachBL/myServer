package com.cgi.myCoyote.dtos;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "web-app")
public class WebAppObject {

	@XmlElement(name = "servlet")
	private List<Servlet> servletList;
	
	@XmlElement(name = "servlet-mapping")
	private List<ServletMapping> servletMappingList;

	public WebAppObject() {
		super();
		// TODO Auto-generated constructor stub
	}

	public WebAppObject(List<Servlet> servletList, List<ServletMapping> servletMappingList) {
		super();
		this.servletList = servletList;
		this.servletMappingList = servletMappingList;
	}
	
	public List<Servlet> getServlets() {
		return servletList;
	}

	public void setServlets(List<Servlet> servlets) {
		this.servletList = servlets;
	}

	public List<ServletMapping> getServletMappings() {
		return servletMappingList;
	}

	public void setServletMappings(List<ServletMapping> servletMappings) {
		this.servletMappingList = servletMappings;
	}

	@Override
	public String toString() {
		return "WebAppObject [servletList=" + servletList + ", servletMappingList=" + servletMappingList + "]";
	}
	
}
