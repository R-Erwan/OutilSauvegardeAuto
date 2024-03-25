package client.shell;

import utils.SystemUtils;

import java.util.Properties;

/**
 * Gère les commandes utilisateur lié aux configurations de l'application.
 * config info // config update key val //
 */
public class ConfigCommandHandler implements CommandHandler{
    /**
     * La fonction handleCommand gère les commandes de configuration dans l'application.
     * Elle vérifie le nombre d'arguments et le contenu de la commande pour déterminer comment traiter la demande de l'utilisateur.
     * Si la commande est correcte, elle effectue les actions correspondantes telles que l'affichage de l'aide, l'affichage des informations de configuration, ou la mise à jour de la configuration.
     * Si la commande n'est pas valide, elle affiche un message d'erreur et l'aide correspondante pour guider l'utilisateur.
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
            displayHelp(0);
        } else if (nbArgs == 2) {
            if(parts[1].equalsIgnoreCase("info")){
                displayConfigInfo();
            } else {
                System.out.println("Argument : '"+parts[1]+"' incorrect.");
                displayHelp(1);
                return false;
            }
        } else if (parts[1].equalsIgnoreCase("update")){
            configUpdate(parts[2],parts[3]);
        } else {
            System.out.println("Argument : '"+parts[1]+"' incorrect.");
            displayHelp(2);
            return false;
        }

        return true;
    }

    /**
     * La fonction displayHelp affiche les informations d'aide relatives à la configuration de l'application.
     * Elle prend un paramètre `nbHelp` qui contrôle quelles informations d'aide sont affichées en fonction du contexte.
     * @param nbHelp contrôle quelles informations d'aide sont affiché en fonction du contexte.
     */
    @Override
    public void displayHelp(int nbHelp){
        System.out.println("- "+CYAN+"config"+RESET+": Affiche ces informations.");
        if(nbHelp == 0 || nbHelp == 1)
            System.out.println("- "+CYAN+"config info"+RESET+" : Affiche les config actuelles.");
        if(nbHelp == 0 || nbHelp == 2){
            System.out.println("- "+CYAN+"config update key newVal"+RESET+" : modifie une propriété de configuration, nécessite de redémarrer le programme");
            System.out.println("\t exemple : config update app.SaveFrequency 24.");
        }
    }

    /**
     * Affiche : les proprieties du fichier de config
     */
    private void displayConfigInfo(){
        Properties prop = SystemUtils.getProperties("application");
        System.out.print(CYAN);
        prop.list(System.out);
        System.out.print(RESET);
    }

    /**
     * Change une propriété de l'application dans le fichier application.properties
     * @param key Cle de la propriété a modifié
     * @param val Nouvelle valeur de la propriété
     */
    private void configUpdate(String key, String val){
        Properties prop = SystemUtils.getProperties("application");
        prop.setProperty(key,val);
        if(SystemUtils.storeAppProperties(prop)){
            System.out.println("Changement effectué");
        };
    }

}
