package utils;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Properties;

/**
 * Classe utilitaire pour les opérations système.
 */
public class SystemUtils {

    /**
     * Méthode pour récupérer les propriétés d'un fichier de configuration'
     *
     * @return Properties
     * @throws RuntimeException si une erreur survient lors de la récupération des propriétés
     */
    public static Properties getProperties(String pFile){
        String rootPath = Thread.currentThread().getContextClassLoader().getResource("").getPath();
        String appConfigPath = rootPath +pFile+".properties";
        Properties prop = new Properties();
        try {
            prop.load(new FileInputStream(appConfigPath));
        } catch (FileNotFoundException e){
            System.err.println("Erreur lors de la récupération du fichier de propriété :"+appConfigPath);
            throw new RuntimeException();
        } catch (IOException e){
            e.printStackTrace();
            System.err.println("Erreur lors du chargement du fichier de config");
            throw new RuntimeException();
        }

        return prop;
    }

    /**
     * Méthode pour sauvegarder les propriétés dans le fichier de configuration.
     *
     * @param prop Les propriétés à sauvegarder
     * @return true si la sauvegarde réussit, false sinon
     */
    public static boolean storeAppProperties(Properties prop){
        String rootPath = Thread.currentThread().getContextClassLoader().getResource("").getPath();
        String appConfigPath = rootPath +"application.properties";
        try {
            prop.store(new FileWriter(appConfigPath),null);
            return true;
        } catch (IOException e){
            System.err.println("Erreur lors de la sauvegarde des configuration de l'app.");
            return false;
        }
    }

}
