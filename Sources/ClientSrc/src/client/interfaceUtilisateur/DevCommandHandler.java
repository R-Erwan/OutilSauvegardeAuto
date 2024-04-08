package client.interfaceUtilisateur;

import client.AppClient;
import utils.Colors;

import java.io.IOException;
import java.util.Objects;

import static utils.SystemUtils.getProperties;

/**
 * Gère les commandes utilisateur lié aux opérations de test.
 */
public class DevCommandHandler implements CommandHandler{
    private final AppClient app;

    /**
     * Constructeur
     * @param app Application courante
     */
    public DevCommandHandler(AppClient app){
        this.app = app;
    }

    @Override
    public boolean handleCommand(String[] parts) {
        // Vérifier si la commande est valide
        if ( (parts.length !=1 && parts.length != 2 && parts.length !=3 ) || !parts[0].equalsIgnoreCase("dev")) {
            // La commande n'est pas correcte, ne pas traiter
            return false;
        }
        if(parts.length == 1){
            displayHelp();
            return true;
        } else if (parts.length == 2){
            switch (parts[1]) {
                case "serFwq" -> {
                    try {
                        app.getFwq().write(getProperties("Config/application.properties").getProperty("app.fwqSerFile"));
                    } catch (IOException e) {
                        System.err.println("Erreur lors de la sauvegarde de la FWQ");
                    }
                }
                case "showFwq" -> System.out.println(app.getFwq().toString());
                default -> {
                    return false;
                }
            }
            return true;
        } else {
            if(Objects.equals(parts[1], "pauseSave")){
                app.pauseSave(Integer.parseInt(parts[2]) * 1000);
                return true;
            }
        }
        return false;
    }

    /**
     * Affiche l'aide relative aux commandes de test.
     */
    @Override
    public void displayHelp() {
        System.out.println("- "+Colors.CYAN+"dev"+Colors.RESET+" : Affiche ces informations.");
        System.out.println("- "+Colors.CYAN+"dev pauseSave n_Secondes"+Colors.RESET+" : Met en pause le thread de sauvegarde pdt n secondes ");
        System.out.println("- "+Colors.CYAN+"dev serFwq"+Colors.RESET+" : Force la serialisation de la FWQ");
        System.out.println("- "+Colors.CYAN+"dev showFwq"+Colors.RESET+" : Affiche l'état de la FWQ");

    }
}
