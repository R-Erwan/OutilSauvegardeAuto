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
     * @param pFile nom du fichier de configuration (server ou application)
     * @return Properties
     * @throws RuntimeException si une erreur survient lors de la récupération des propriétés
     */
    public static Properties getProperties(String pFile) {
        Properties prop = new Properties();
        try (InputStream input = SystemUtils.class.getResourceAsStream("/" + pFile + ".properties")) {
            if (input == null) {
                System.err.println("Le fichier de propriété " + pFile + ".properties n'a pas été trouvé.");
                throw new RuntimeException();
            }
            prop.load(input);
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Erreur lors du chargement du fichier de configuration.");
            throw new RuntimeException();
        }
        return prop;
    }

}
