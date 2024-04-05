package client.shell;

import utils.Colors;

/**
 * Cette classe implémente l'interface CommandHandler pour gérer la commande "help".
 */
public class HelpCommandHandler implements CommandHandler {
    /**
     * Gère la commande "help".
     * @param parts Les différentes parties de la commande saisie par l'utilisateur.
     * @return true si la commande a été correctement traitée, false sinon.
     */
    @Override
    public boolean handleCommand(String[] parts) {
        // Vérifier si la commande est valide
        if (parts.length !=1|| !parts[0].equalsIgnoreCase("help")) {
            // La commande n'est pas correcte, ne pas traiter
            return false;
        }
        displayHelp();
        return true;
    }

    /**
     * Affiche l'aide pour les commandes disponibles.
     */
    @Override
    public void displayHelp() {
        System.out.println("Liste des commandes disponibles :");
        System.out.println("- "+ Colors.CYAN+"help"+Colors.RESET+" : Afficher l'aide.");
        System.out.println("- "+Colors.CYAN+"config"+Colors.RESET+" : Opérations sur la configuration de l'application.");
        System.out.println("- "+Colors.CYAN+"process"+Colors.RESET+" : Opérations sur les fichier à sauvegarder");
        System.out.println("- "+Colors.CYAN+"server"+Colors.RESET+" : Opérations nécessitant des requires au serveur");
        System.out.println("- "+Colors.CYAN+"dev"+Colors.RESET+" : Opérations de tests");
        System.out.println("- "+Colors.CYAN+"app stop"+Colors.RESET+" : Arrête le programme et le shell");
        System.out.println("- "+Colors.CYAN+"app check"+Colors.RESET+" : Lance un check des fichiers client");

        // Informations supplémentaires
        System.out.println("\nPour utiliser une commande, saisissez son nom suivi des arguments nécessaires.");
        System.out.println("Exemple : command1 arg1 arg2.");
    }
}

