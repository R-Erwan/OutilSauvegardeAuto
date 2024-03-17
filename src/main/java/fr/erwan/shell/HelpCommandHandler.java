package fr.erwan.shell;

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
        displayHelp(0);

        return true;
    }

    /**
     * Affiche l'aide pour les commandes disponibles.
     */
    @Override
    public void displayHelp(int n) {
        System.out.println("Liste des commandes disponibles :");
        System.out.println("- "+CYAN+"help"+RESET+" : Afficher l'aide.");
        System.out.println("- "+CYAN+"config"+RESET+" : Opérations sur la configuration de l'application.");
        System.out.println("- "+CYAN+"process"+RESET+" : Opérations sur les fichier à sauvegarder");
        System.out.println("- "+CYAN+"app stop"+RESET+" : Arrête le programme et le shell");
        System.out.println("- "+CYAN+"app start"+RESET+" : Lance le programme");
        System.out.println("- "+CYAN+"app check"+RESET+" : Lance un check des fichiers client");
        System.out.println("- "+CYAN+"dev"+RESET+" : Commande de tests");




        // Informations supplémentaires
        System.out.println("\nPour utiliser une commande, saisissez son nom suivi des arguments nécessaires.");
        System.out.println("Exemple : command1 arg1 arg2.");
    }
}

