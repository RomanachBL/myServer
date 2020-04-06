package com.cgi;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.awt.Desktop;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Scanner;

import com.fasterxml.jackson.databind.ObjectMapper;

public class Client {
	public static void main(String[] args) throws URISyntaxException {
		try {
			int port = 9090;

			Socket socket = new Socket("localhost", port);

			// Création du menu client
			String racine = null;
			String query = "/";
			LinkedHashMap<String, String> params = new LinkedHashMap<String, String>();
			String nom = "";
			String prenom = "";
			Scanner input = new Scanner(System.in);

			System.out.println("\n#################################");
			System.out.println("Construisons notre request HTTP !");
			System.out.println("#################################\n");

			System.out.println("Racine ? (o/n)\n");
			System.out.println("Votre réponse :");

			racine = input.nextLine();

			if (racine.equals("o")) {
				// On envoie la réponse avec les attributs vide et query = "/"
				sendGetResponse(socket, query, params);

			} else if (racine.equals("n")) {
				// Saisi de la query
				System.out.println("--------------------------------");
				System.out.println("Query ? (ne pas saisir '\\' ni les paramètres potentiels)");
				System.out.println("Possibilités : une du web.xml !\n");
				System.out.println("Votre réponse :");

				query += input.nextLine();
				
				if(query.equals("/displayHeader")) {
					sendGetResponse(socket, query, params);
					
				} else {
					// Saisi des params
					System.out.println("--------------------------------");
					System.out.println("Paramètres (Si vide, appuyez sur 'Entrer') \n");
	
					System.out.println("Nom :");
					nom = input.nextLine();
	
					System.out.println("Prénom :");
					prenom = input.nextLine();
	
					params.put("nom", nom);
					params.put("prenom", prenom);
	
					// Saisi de la méthode GET/POST
					System.out.println("--------------------------------");
					System.out.println("GET ou POST (1/2) \n");
					System.out.println("1 - GET");
					System.out.println("2 - POST\n");
					System.out.println("Votre réponse :");
	
					int methodRes = input.nextInt();
					if (methodRes == 1)
						sendGetResponse(socket, query, params);
					if (methodRes == 2)
						sendPostResponse(socket, query, params);
				}
			} else {
				System.out.println("Erreur de saisi ...");
			}

		} catch (IOException e) {
			e.printStackTrace();
		}
		
		System.out.println("\n---------- FIN ----------\n");
	}

	public static void sendGetResponse(Socket socket, String query, LinkedHashMap<String, String> params)
			throws IOException {

		// On build rapidement la query
		if (!params.isEmpty()) {
			int count = 0;
			query += "?";
			for (Entry<String, String> elem : params.entrySet()) {
				if (count != 0)
					query += "&";
				query += elem.getKey() + "=" + elem.getValue();
				count++;
			}
		}

		// Header de la requête http personnalisé
		// Pas de "Accept-language" / "User-Agent" / "Connection" etc ...
		String request = "GET " + query + " HTTP/1.1\r\n";
		request += "Host: localhost\r\n";

		System.out.println("#################################\n");
		System.out.println("Ce que j'envoie : \n");
		System.out.println(request);

		BufferedOutputStream sending = new BufferedOutputStream(socket.getOutputStream());

		sending.write(request.getBytes());
		sending.flush();

		// On récupère maintenant la réponse du serveur
		// dans un flux, comme pour les fichiers...
		BufferedInputStream bis = new BufferedInputStream(socket.getInputStream());

		// Il ne nous reste plus qu'à le lire
		String content = "";
		int stream;
		byte[] b = new byte[1024];
		while ((stream = bis.read(b)) != -1) {
			content += new String(b, 0, stream);
		}

		System.out.println("\nRéponse du serveur : \n");
		System.out.println(content);

		// Ouverture du browser
//					if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
//						URI uri = new URI();
//						Desktop.getDesktop().browse(uri);
//					}
	}

	public static void sendPostResponse(Socket socket, String query, LinkedHashMap<String, String> params)
			throws IOException {

		ObjectMapper om = new ObjectMapper();

		// Construction d'un JSON en fonction des params
		String json = om.writeValueAsString(params);

		// Header de la requête http personnalisé
		// Pas de "Accept-language" / "User-Agent" / "Connection" etc ...
		String request = "POST " + query + " HTTP/1.1\r\n";
		request += "Host: localhost\r\n";
		request += json + "\r\n";

		System.out.println("#################################\n");
		System.out.println("Ce que j'envoie : \n");
		System.out.println(request);

		BufferedOutputStream sending = new BufferedOutputStream(socket.getOutputStream());

		sending.write(request.getBytes());
		sending.flush();

		// On récupère maintenant la réponse du serveur
		// dans un flux, comme pour les fichiers...
		BufferedInputStream bis = new BufferedInputStream(socket.getInputStream());

		// Il ne nous reste plus qu'à le lire
		String content = "";
		int stream;
		byte[] b = new byte[1024];
		while ((stream = bis.read(b)) != -1) {
			content += new String(b, 0, stream);
		}

		System.out.println("\nRéponse du serveur : \n");
		System.out.println(content);

		// Ouverture du browser
//					if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
//						URI uri = new URI();
//						Desktop.getDesktop().browse(uri);
//					}
	}
}
