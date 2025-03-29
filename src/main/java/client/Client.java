package client;

import java.io.*;
import java.net.*;

public class Client {

    public static void main(String[] args) {
        System.out.println("🏷️  Client Médiathèque");

        try (Socket socket = new Socket("localhost", 2000);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
             BufferedReader clavier = new BufferedReader(new InputStreamReader(System.in))) {

            // Lire TOUS les messages du serveur avant d'envoyer les inputs
            String ligne;
            while ((ligne = in.readLine()) != null) {
                System.out.println(ligne);
                if (ligne.contains("Entrez")) {
                    String input = clavier.readLine().trim();
                    out.println(input);
                }
            }

        } catch (IOException e) {
            System.err.println("Erreur client: " + e.getMessage());
        }
    }
}