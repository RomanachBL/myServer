package com.cgi.myCoyote.dtos;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "servlet-mapping")
public class ServletMapping {
	
	@XmlElement(name = "servlet-name")
	private String name;
	
	@XmlElement(name = "url-pattern")
	private String url;

	public ServletMapping() {
		super();
		// TODO Auto-generated constructor stub
	}

	public ServletMapping(String servletName, String urlPattern) {
		super();
		this.name = servletName;
		this.url = urlPattern;
	}

	public String getServletName() {
		return name;
	}

	public void setServletName(String servletName) {
		this.name = servletName;
	}

	public String getUrlPattern() {
		return url;
	}

	public void setUrlPattern(String urlPattern) {
		this.url = urlPattern;
	}

	@Override
	public String toString() {
		return "ServletMapping [servletName=" + name + ", urlPattern=" + url + "]";
	}

}
