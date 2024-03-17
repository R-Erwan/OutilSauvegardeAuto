package fr.erwan.shell;

import fr.erwan.AppClient;

public class AppCommandHandler implements CommandHandler{
    private AppClient app;
    public AppCommandHandler(AppClient app){
        this.app = app;
    }
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

    @Override
    public void displayHelp(int n) {
        System.out.println("- "+CYAN+"app"+RESET+" : Affiche ces informations.");
        System.out.println("- "+CYAN+"app stop"+RESET+" : Arrête l'application correctement");
        System.out.println("- "+CYAN+"app check"+RESET+" : Lance un check des fichiers a sauvegarder");
        System.out.println("- "+CYAN+"app start"+RESET+" : @Deprecated lance l'application");

    }
}
