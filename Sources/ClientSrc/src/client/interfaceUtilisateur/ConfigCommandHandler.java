package client.interfaceUtilisateur;

import utils.Colors;
import utils.SystemUtils;

import java.util.Properties;

/**
 * Gère les commandes utilisateur lié aux configurations de l'application.
 */
public class ConfigCommandHandler implements CommandHandler{
    /**
     * Gère les commandes de configuration dans l'application.
     *
     * @param parts saisi utilisateur
     * @return true si la command demander existe, false sinon
     */
    @Override
    public boolean handleCommand(String[] parts) {
        int nbArgs = parts.length;
        if ( (nbArgs != 1 && nbArgs != 2 && nbArgs !=4 ) || !parts[0].equalsIgnoreCase("config")) {
            // La commande n'est pas correcte, ne pas traiter
            return false;
        }
        if (nbArgs == 1){
            displayHelp();
            return true;
        } else if (nbArgs == 2) {
            if(parts[1].equalsIgnoreCase("info")){
                displayConfigInfo();
            } else {
                return false;
            }
        } else if (parts[1].equalsIgnoreCase("update")){
            configUpdate(parts[2],parts[3]);
        } else {
            return false;
        }
        return true;
    }

    /**
     * La fonction displayHelp affiche les informations d'aide relatives à la configuration de l'application.
     */
    @Override
    public void displayHelp(){
        System.out.println("- "+Colors.CYAN+"config"+ Colors.RESET+": Affiche ces informations.");
        System.out.println("- "+Colors.CYAN+"config info"+Colors.RESET+" : Affiche les config actuelles.");
        System.out.println("- "+Colors.CYAN+"config update key newVal"+Colors.RESET+" : modifie une propriété de configuration, nécessite de redémarrer le programme");
        System.out.println("\t exemple : config update app.SaveFrequency 24.");
    }

    /**
     * Affiche : les proprieties du fichier de config
     */
    private void displayConfigInfo(){
        Properties prop = SystemUtils.getProperties("Config/application.properties");
        System.out.print(Colors.CYAN);
        prop.list(System.out);
        System.out.print(Colors.RESET);
    }

    /**
     * Change une propriété de l'application dans le fichier application.properties
     * @param key Cle de la propriété a modifié
     * @param val Nouvelle valeur de la propriété
     */
    private void configUpdate(String key, String val){
        Properties prop = SystemUtils.getProperties("Config/application.properties");
        prop.setProperty(key,val);
        if(SystemUtils.storeAppProperties(prop,"Config/application.properties")){
            System.out.println("Changement effectué");
        };
    }
}
