package fr.erwan.shell;

import fr.erwan.AppClient;

import static fr.erwan.utils.SystemUtils.getAppProperties;

/**
 * dev stopSave
 */
public class DevCommandHandler implements CommandHandler{


    private AppClient app;

    public DevCommandHandler(AppClient app){
        this.app = app;
    }

    @Override
    public boolean handleCommand(String[] parts) {
        // Vérifier si la commande est valide
        if ( (parts.length !=1 && parts.length != 2 ) || !parts[0].equalsIgnoreCase("dev")) {
            // La commande n'est pas correcte, ne pas traiter
            return false;
        }
        if(parts.length == 1){
            displayHelp(0);
            return true;
        } else {
            switch (parts[1]) {
                case "stopSave" -> app.stopApp();
                case "serFwq" -> app.getFwq().write(getAppProperties().getProperty("app.fwqSerializeFile"));
                case "showFwq" -> System.out.println(app.getFwq().toString());
                default -> displayHelp(0);
            }
            return true;
        }
    }

    @Override
    public void displayHelp(int n) {
        System.out.println("- "+CYAN+"dev"+RESET+" : Affiche ces informations.");
        System.out.println("- "+CYAN+"dev stopSave"+RESET+" :Arrête uniquement la partie application (les threads de sauvegarde) ");
        System.out.println("- "+CYAN+"dev serFwq"+RESET+" : Force la serialisation de la FWQ");
        System.out.println("- "+CYAN+"dev showFwq"+RESET+" : Affiche l'état de la FWQ");

    }
}
