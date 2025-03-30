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

            String ligne;
            while ((ligne = in.readLine()) != null) {
                System.out.println(ligne);

                // Gestion de la déconnexion serveur
                if (ligne.contains("Serveur arrêté")) break;

                if (ligne.contains("Entrez")) {
                    String input = clavier.readLine().trim();

                    // Commande QUIT
                    if ("QUIT".equalsIgnoreCase(input)) {
                        out.println("QUIT");
                        System.out.println("🚪 Déconnexion...");
                        break;
                    }

                    out.println(input);
                }
            }

        } catch (IOException e) {
            System.err.println("Erreur client: " + e.getMessage());
        }
    }
}