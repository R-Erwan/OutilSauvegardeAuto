package client.shell;

import utils.ConstantColors;

/**
 * Interface définissant un gestionnaire de commandes pour le shell.
 */
public interface CommandHandler extends ConstantColors {
    boolean handleCommand(String[] parts); // Méthode pour gérer une commande
    void displayHelp(int n); // Méthode pour afficher l'aide concernant les commandes

}
