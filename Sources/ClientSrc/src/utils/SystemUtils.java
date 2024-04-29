package utils;

import java.io.*;
import java.util.Properties;

/**
 * Classe utilitaire pour les opérations système.
 */
public class SystemUtils {

    /**
     * Méthode pour récupérer les propriétés d'un fichier de configuration
     *
     * @param pFile chemin d'accès au fichier
     * @return Properties
     * @throws RuntimeException si une erreur survient lors de la récupération des propriétés
     */
    public static Properties getProperties(String pFile) {
        Properties prop = new Properties();
        try {
            prop.load(new FileReader(pFile));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return prop;
    }

    /**
     * Méthode pour sauvegarder les propriétés dans le fichier de configuration.
     *
     * @param prop Les propriétés à sauvegarder
     * @return true si la sauvegarde réussit, false sinon
     */
    public static boolean storeAppProperties(Properties prop, String pFile) {
        try {
            prop.store(new FileWriter(new File(pFile)),null);
            return true;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
