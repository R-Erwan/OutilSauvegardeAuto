package utils;

import java.io.File;
import java.io.IOException;

/**
 * Implémente des méthodes permettant de réaliser du scénario de test
 */
public class Simulation implements ConstantColors {

    /**
     * Créer des fichiers txt vide
     * @param dossier Dossier où créer les fichiers
     * @param nombreFichiers Nombre de fichiers à créer.
     */
    public static void creerFichiersTexte(String dossier, int nombreFichiers) {
        File dossierFichiers = new File(dossier);
        if (!dossierFichiers.exists()) {
            dossierFichiers.mkdirs(); // Crée le dossier s'il n'existe pas
        }

        try {
            for (int i = 1; i <= nombreFichiers; i++) {
                String nomFichier = "fichier" + i + ".txt";
                File fichier = new File(dossierFichiers, nomFichier);
                if (fichier.createNewFile()) {
                    // Vous pouvez ajouter du contenu aux fichiers ici si nécessaire
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
