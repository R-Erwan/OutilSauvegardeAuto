package client.shell;

import client.fileTools.AppClient;
import utils.Colors;

import java.io.IOException;

/**
 * Gère les commandes relatives à l'application client.
 */
public class ServerCommandHandler implements CommandHandler{
    private final AppClient app;

    /**
     * Constructeur de la classe.
     *
     * @param app L'instance de l'application client.
     */
    public ServerCommandHandler(AppClient app){
        this.app = app;
    }

    /**
     * Gère une commande relative aux requêtes serveur.
     *
     * @param parts Les différentes parties de la commande saisie par l'utilisateur.
     * @return true si la commande a été correctement traitée, false sinon.
     */
    @Override
    public boolean handleCommand(String[] parts) {
        // Vérifier si la commande est valide
        if ( (parts.length !=1 && parts.length !=2 )|| !parts[0].equalsIgnoreCase("server")) {
            return false;
        }
        if(parts.length == 1){
            displayHelp();
            return true;
        } else {
            switch (parts[1]){
                case "listFile" -> {
                    try {
                        String response = app.getSc().getFileList();
                        System.out.println(response);
                    } catch (IOException e){
                        System.err.println("Erreur lors de la communication avec le serveur");
                    }
                }
            }
        }
        return true;
    }


    /**
     * Affiche l'aide pour les commandes relatives aux requêtes serveur.
     */
    @Override
    public void displayHelp() {
        System.out.println("- "+ Colors.CYAN+"server"+Colors.RESET+" : Affiche ces informations.");
        System.out.println("- "+Colors.CYAN+"server listFile"+Colors.RESET+" : Liste tout les fichiers personnel présent sur le serveur");
    }
}
