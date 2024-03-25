package client.shell;

import client.fileTools.AppClient;

/**
 * Gère les commandes relatives à l'application client.
 */
public class AppCommandHandler implements CommandHandler{
    private AppClient app;

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
        if ( (parts.length !=1 && parts.length !=2 )|| !parts[0].equalsIgnoreCase("app")) {
            return false;
        }
        if(parts.length == 1){
            displayHelp(0);
            return false;
        } else {
            switch (parts[1]){
                case "stop" -> app.stopApp();
                case "start" -> app.startApp();
                case "check" -> app.getfCheck().check();
            }
        }
        return true;
    }

    /**
     * Affiche l'aide pour les commandes relatives à l'application client.
     *
     * @param n Contrôle quelles informations d'aide sont affichées en fonction du contexte.
     */
    @Override
    public void displayHelp(int n) {
        System.out.println("- "+CYAN+"app"+RESET+" : Affiche ces informations.");
        System.out.println("- "+CYAN+"app stop"+RESET+" : Arrête l'application correctement");
        System.out.println("- "+CYAN+"app check"+RESET+" : Lance un check des fichiers a sauvegarder");
        System.out.println("- "+CYAN+"app start"+RESET+" : @Deprecated lance l'application");

    }
}
