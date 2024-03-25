package client;

import client.shell.ShellClient;

import utils.ConstantColors;
import client.fileTools.AppClient;

import java.util.*;

import static utils.SystemUtils.getProperties;

//TODO : Chargement sauvegarde d'un fichier // Comment installer le pgme // REAMDE.MD file // Scenario de Test
/**
 * La classe principale de l'application.
 * Elle initialise l'application et lance le shell utilisateur.
 *
 * @author Erwan Roussel
 * @version 1.0
 */
public class ProgrammeClient implements ConstantColors {


    /**
     * La méthode principale de l'application.
     * Elle initialise l'application, détermine si c'est le premier lancement,
     * puis démarre l'application et le shell utilisateur.
     *
     * @param args Les arguments de la ligne de commande.
     */
    public static void main(String[] args) throws Exception {

        // Récupère les paramètres de l'application
        Properties prop = getProperties("application");
        boolean isFirstLaunch = Boolean.parseBoolean(prop.getProperty("app.firstLaunch"));

        //Si c'est le premier lancement, lance le firstLaunch
        if (isFirstLaunch) {
            AppClient.createFirstApp();
        }
        //Initialise l'application
        AppClient app = AppClient.InitApp();

        app.startApp();


        //Lance le shell
        ShellClient shellC = new ShellClient(app);
        shellC.start();

    }

}