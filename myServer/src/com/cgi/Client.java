package com.cgi;

import java.awt.Desktop;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.Socket;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.LinkedHashMap;
import java.util.Map.Entry;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

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
					System.out.println("GET ou POST \n");
					System.out.println("Votre réponse :");
	
					String methodRes = input.nextLine();
					if (methodRes.equalsIgnoreCase("get"))
						sendGetResponse(socket, query, params);
					if (methodRes.equalsIgnoreCase("post"))
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

	private static void sendGetResponse(Socket socket, String query, LinkedHashMap<String, String> params)
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
		
		seeInBrowser(content);

	}

	private static void sendPostResponse(Socket socket, String query, LinkedHashMap<String, String> params)
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
		
		seeInBrowser(content);

		// Ouverture du browser
//					if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
//						URI uri = new URI();
//						Desktop.getDesktop().browse(uri);
//					}
		
	}
	
	private static void seeInBrowser(String httpContent) {
		int firstIndex;
		int secIndex;
		String html = "";
		OutputStream os = null;
		
		// On récupère le HTML dans la réponse du serveur
		firstIndex = httpContent.indexOf("¤");
		secIndex = httpContent.lastIndexOf("¤");
		html = httpContent.substring(firstIndex + 1, secIndex);
		
		try {
			if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
				File tempFile = new File(System.getProperty("user.dir") + "/src/resources/tmp/result.html");
				
				// On écrit dans le fichier temporaire
				os = new FileOutputStream(tempFile);
				os.write(html.getBytes(), 0, html.length());
				// Obligé de fermer le stream pour pouvoir delete le fichier.
				os.close();
				
				// On l'ouvre dans le browser
				Desktop.getDesktop().open(tempFile);
				
				// On doit effectuer un léger 'sleep' pour fermer le fichier car sinon
				// il sera delete avant de s'ouvrir dans le browser.
				TimeUnit.SECONDS.sleep(1);
				if (tempFile.exists()) {
					tempFile.delete();
				}
			}
		} catch (IOException | InterruptedException e) {
			e.printStackTrace();
		}
	}
}
