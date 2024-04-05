package client.shell;

import client.fileTools.AppClient;
import utils.Colors;

/**
 * Gère les commandes relatives à l'application client.
 */
public class AppCommandHandler implements CommandHandler{
    private final AppClient app;
    /**
     * Constructeur de la classe AppCommandHandler.
     *
     * @param app L'instance de l'application client.
     */
    public AppCommandHandler(AppClient app){
        this.app = app;
    }

    /**
     * Gère une commande relative à l'application client.
     *
     * @param parts Les différentes parties de la commande saisie par l'utilisateur.
     * @return true si la commande a été correctement traitée, false sinon.
     */
    @Override
    public boolean handleCommand(String[] parts) {
        // Vérifier si la commande est valide
        if ( (parts.length !=1 && parts.length !=2 ) || !parts[0].equalsIgnoreCase("app")) {
            return false;
        }
        if(parts.length == 1){
            displayHelp();
            return true;
        } else {
            switch (parts[1]){
                case "stop" -> app.stopApp();
                case "check" -> app.getfCheck().check();
            }
        }
        return true;
    }

    /**
     * Affiche l'aide pour les commandes relatives à l'application client.
     */
    @Override
    public void displayHelp() {
        System.out.println("- "+Colors.CYAN+"app"+Colors.RESET+" : Affiche ces informations.");
        System.out.println("- "+Colors.CYAN+"app stop"+Colors.RESET+" : Arrête l'application correctement");
        System.out.println("- "+Colors.CYAN+"app check"+Colors.RESET+" : Lance un check des fichiers a sauvegarder");

    }
}
