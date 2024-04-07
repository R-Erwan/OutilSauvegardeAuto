package client.interfaceUtilisateur;


/**
 * Interface définissant un gestionnaire de commandes pour le shell.
 */
public interface CommandHandler{
    /**
     * Gère une commande relative à la classe
     * @param parts Les différentes parties de la commande saisie par l'utilisateur.
     * @return true si la commande a été correctement traitée, false sinon.
     */
    boolean handleCommand(String[] parts); // Méthode pour gérer une commande

    /**
     * Affiche l'aide relative a la classe
     */
    void displayHelp(); // Méthode pour afficher l'aide concernant les commandes

}
