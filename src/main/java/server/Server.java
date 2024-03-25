package server;

import java.io.File;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Properties;

import static utils.SystemUtils.getProperties;

/**
 * La classe Server représente le serveur principal qui écoute les connexions entrantes des clients.
 */
public class Server {
    /** Le port d'écoute du serveur */
    static int port;

    /** Liste des clients connectés au serveur */
    static ArrayList<Client> clients;

    /** Nombre maximal de clients autorisés à se connecter */
    static int maxClients;

    /**
     * Méthode principale du serveur qui initialise le système et écoute les connexions des clients.
     * @param args Les arguments de la ligne de commande (non utilisés)
     * @throws Exception Si une erreur survient lors de l'exécution du serveur
     */
    public static void main(String[] args) throws Exception{
        // Initialise la structure du système de fichiers du serveur
        initFileSystem();

        // Récupère les propriétés du serveur à partir du fichier de configuration
        Properties prop = getProperties("server");
        maxClients = Integer.parseInt(prop.getProperty("server.maxClient"));
        port = Integer.parseInt(prop.getProperty("server.port"));
        clients = new ArrayList<>(maxClients);

        // Crée un socket serveur pour écouter les connexions entrantes
        ServerSocket serverS = new ServerSocket(port);
        System.out.println("Socket écoute : "+ serverS);

        // Accepte les connexions des clients jusqu'à atteindre le nombre maximal autorisé
        while (clients.size() < maxClients){
            Socket socket = serverS.accept(); // Attend une connexion entrante
            Client client = new Client(socket,clients); // Crée un nouveau client pour gérer la connexion
            System.out.println("Nouvelle connection - Socket :" + socket);
            System.out.println("Nombres de clients : "+clients.size());
            clients.add(client); // Ajoute le client à la liste des clients connectés
            client.start(); // Démarre le thread du client pour gérer la communication avec lui
        }
    }

    /**
     * Méthode pour initialiser l'architecture du système de fichier du serveur.
     * Cette méthode crée les répertoires nécessaires pour le fonctionnement du serveur.
     */
    public static void initFileSystem(){
        // Récupère les propriétés du serveur à partir du fichier de configuration
        Properties prop = getProperties("server");

        // Crée le répertoire principal du serveur s'il n'existe pas
        File serverRep = new File("Server");
        serverRep.mkdir();

        // Crée le répertoire des utilisateurs du serveur s'il n'existe pas
        File serUser = new File(prop.getProperty("server.serUser"));
        serUser.mkdirs();

        // Crée le répertoire des données des utilisateurs du serveur s'il n'existe pas
        File usersData = new File(prop.getProperty("server.usersData"));
        usersData.mkdirs();

        // Crée le répertoire des journaux (logs) du serveur s'il n'existe pas
        File logs = new File(prop.getProperty("server.logs"));
        logs.mkdirs();
    }
}
