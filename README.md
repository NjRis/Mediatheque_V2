# 📚 Médiathèque - Système de Gestion de Bibliothèque

Application client-serveur Java pour gérer les emprunts, réservations et retours de documents (livres, DVD) avec certifications métier.

---

## 🚀 Fonctionnalités

- **Services Multi-Ports** : 
  - `2000` : Réservation de documents  
  - `3000` : Emprunt de documents  
  - `4000` : Retour de documents  
- **Gestion des Utilisateurs** : 
  - Bannissement automatique pour retard/dégradation
  - Contrôle d'âge pour les DVD adultes (+16 ans)
- **Notifications** :
  - Alertes email (via SMTP)
  - Musique d'attente pendant les conflits de réservation

---

## ⚙️ Prérequis

- **Java 17** ([Téléchargement](https://www.oracle.com/java/technologies/javase-jdk17-downloads.html))
- **Maven** ([Installation](https://maven.apache.org/install.html))
- **Base de Données MySQL** (Optionnel - Voir [Configuration](#base-de-données))

---

## 🛠 Installation

1. **Cloner le dépôt** :
   ```bash
   git clone https://github.com/NjRis/Mediatheque_V2.git
   cd mediatheque

   Compiler avec Maven :

## Compiler avec Maven :
mvn clean install


🖥 Utilisation
## 1. Démarrer le Serveur

mvn exec:java -Dexec.mainClass="serveur.ServeurApplication"

Sortie attendue :

🟢 Service Réservation actif sur port 2000
🟢 Service Emprunt actic sur port 3000
🟢 Service Retour actif sur port 4000

## 2. Modifier le Port Client (⚠️ Important)
Ouvrez Client.java et changez le port dans le code :


# Avant
try (Socket socket = new Socket("localhost", 2000);  // Port par défaut

# Après (ex: pour l'emprunt)
try (Socket socket = new Socket("localhost", 3000);

## 3. Lancer le Client
```bash
mvn exec:java -Dexec.mainClass="client.Client"
