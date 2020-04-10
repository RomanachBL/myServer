package com.cgi.myJasper;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import bsh.EvalError;
import bsh.Interpreter;

public class MyJasper {
	
	public String execJasper(String jspContent, Map<String, String> mapParams) throws EvalError {
		
		/* 
		 * Algorithme loin d'être opti !! Trop d'erreurs potentielles.
		 * 
		 * L'objectif ici est simplement de montrer que le parsing d'une JSP passe par Jasper.
		 * Ici, nous voulons simplement faire quelques modifications mineures.
		 */
		
		// Premier parsing
		jspContent = firstELParsing(jspContent);
		
		// Deuxième parsing
		jspContent = secondELParsing(jspContent, mapParams);
		
		return jspContent;
	}
	
	/**
	 * Parse les éléments de type '<%= java>'
	 * Encore une fois, c'est très simplifié ! L'algorithme ne peut gérer que des expressions simple tel qu'une nouvelle date.
	 * Il ne gérera pas des expressions conditionnelles ou des boucles par exemple.
	 * 
	 * @param jspContent
	 * @param mapParams
	 * @return
	 * @throws EvalError 
	 */
	private String firstELParsing(String jspContent) throws EvalError {
		int firstIndex;
		List<Integer> firstIndexList = new ArrayList<>();
		
		// Je récupère la liste des index de '<%='
		firstIndex = jspContent.indexOf("<%=");
		firstIndexList.add(firstIndex);
		while (firstIndex >= 0) {
			firstIndex = jspContent.indexOf("<%=", firstIndex + 3);
			if(firstIndex >= 0)
				firstIndexList.add(firstIndex);
		}
		
		// Ensuite je regarde si, juste après, il y une '>'
		// Si oui, je récupère l'accolade } d'après.
		for(int i : firstIndexList) {
			String code = "";
			String totalExpression = "";
			String codeResult = "";
			int start = i + 3;
			int end = jspContent.indexOf("%>", start);
			Interpreter interpreter = new Interpreter();

			if (end != -1) {
				code = jspContent.substring(start + 1, end).trim();

				// Si la variable récupérée n'est pas vide,
				// alors on execute le code qu'il représente
				if (code != null && code != "") {
					totalExpression = jspContent.substring(i, end + 2);
					
					codeResult = interpreter.eval(code).toString();
					jspContent = jspContent.replace(totalExpression, codeResult);
				}
			}
		}
		
		return jspContent;
	}
	
	/**
	 * Parse les éléments de type '${var}'
	 * 
	 * @param jspContent
	 * @param mapParams
	 * @return
	 */
	private String secondELParsing(String jspContent, Map<String, String> mapParams) {
		int dollarIndex;
		List<Integer> dollarIndexList = new ArrayList<>();
		
		// Je récupère la liste des index de $
		dollarIndex = jspContent.indexOf("${");
		dollarIndexList.add(dollarIndex);
		while (dollarIndex >= 0) {
			dollarIndex = jspContent.indexOf("${", dollarIndex + 2);
			if(dollarIndex >= 0)
				dollarIndexList.add(dollarIndex);
		}
		
		// Ensuite je regarde si, juste après, il y une {
		// Si oui, je récupère l'accolade } d'après.
		for(int i : dollarIndexList) {
			String var = "";
			String totalVar = "";
			int startAccolade = i + 1;
			int endAccolade = jspContent.indexOf('}', startAccolade);

			if (endAccolade != -1) {
				// Nous partons du principe que la variable est juste un mot.
				var = jspContent.substring(startAccolade + 1, endAccolade).trim();

				for (String key : mapParams.keySet()) {
					if (var.equals(key)) {
						// Si la variable récupérée correspond à une key dans les params
						// Alors on remplace
						totalVar = jspContent.substring(i, endAccolade + 1);
						jspContent = jspContent.replace(totalVar, mapParams.get(key));
					}

				}
			}
		}
		
		return jspContent;
	}

}
