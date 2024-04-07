package utils;

/**
 * Interface définissant des constantes pour les couleurs utilisées dans les interfaces utilisateur en ligne de commande.
 * Les constantes de cette interface peuvent être utilisées pour formater le texte affiché dans la console avec différentes couleurs.
 */
public interface Colors {
    String RESET = "\u001B[0m"; // Réinitialise la couleur du texte
    String RED = "\u001B[31m";
    String GREEN = "\u001B[32m";
    String YELLOW = "\u001B[33m";
    String BLUE = "\u001B[34m";
    String CYAN = "\u001B[36m";
    String ORANGE = "\u001B[35m";
}
