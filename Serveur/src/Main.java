import server.Server;


/**
 * La classe Server représente le serveur principal qui écoute les connexions entrantes des clients.
 */
public class Main {


    /**
     * Méthode principale du serveur qui initialise le système et écoute les connexions des clients.
     * @param args Les arguments de la ligne de commande (non utilisés)
     * @throws Exception Si une erreur survient lors de l'exécution du serveur
     */
    public static void main(String[] args) throws Exception {
        Server server = new Server();
        server.start();

    }


}
