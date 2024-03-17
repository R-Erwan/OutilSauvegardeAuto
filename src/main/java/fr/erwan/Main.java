package fr.erwan;

import fr.erwan.fileTools.FileWaitingQueue;
import fr.erwan.shell.ShellClient;
import fr.erwan.utils.ConstantColors;
import fr.erwan.fileTools.FileChecker;
import fr.erwan.users.User;
import fr.erwan.utils.SystemUtils;

import java.io.*;
import java.util.*;

import static fr.erwan.utils.SystemUtils.getAppProperties;

/*
TODO/ Système pour répartir les dossier trop volumineux pour un thread ?
*/

/**
 * La classe principale de l'application.
 * Elle initialise l'application et lance le shell utilisateur.
 *
 * @author Erwan Roussel
 * @version 1.0
 */
public class Main implements ConstantColors {


    /**
     * La méthode principale de l'application.
     * Elle initialise l'application, détermine si c'est le premier lancement,
     * puis démarre l'application et le shell utilisateur.
     *
     * @param args Les arguments de la ligne de commande.
     */
    public static void main(String[] args) throws FileNotFoundException {
        ResourceBundle resourceBundle;
        try {
            resourceBundle = ResourceBundle.getBundle("application");
        } catch (MissingResourceException e){
            System.err.println("Le fichier de configuration est manquant");
            throw new RuntimeException();
        }
        boolean isFirstLaunch = Boolean.parseBoolean(resourceBundle.getString("app.firstLaunch"));

        if (isFirstLaunch) {
            firstLaunch();
        }
        AppClient app = init();
        app.startApp();

        ShellClient shellC = new ShellClient(app);
        shellC.start();



//        simuTest();
    }

    /**
     * Initialise l'application client en récupérant les configurations et les données utilisateur.
     *
     * @return L'application client initialisée.
     */
    public static AppClient init(){
        System.out.println(BLUE+"   ___            _     _   _         _                                                                            _        \n" +
                "  / _ \\   _   _  | |_  (_) | |     __| |   ___     ___    __ _   _   _  __   __   ___    __ _    __ _   _ __    __| |   ___ \n" +
                " | | | | | | | | | __| | | | |    / _` |  / _ \\   / __|  / _` | | | | | \\ \\ / /  / _ \\  / _` |  / _` | | '__|  / _` |  / _ \\\n" +
                " | |_| | | |_| | | |_  | | | |   | (_| | |  __/   \\__ \\ | (_| | | |_| |  \\ V /  |  __/ | (_| | | (_| | | |    | (_| | |  __/\n" +
                "  \\___/   \\__,_|  \\__| |_| |_|    \\__,_|  \\___|   |___/  \\__,_|  \\__,_|   \\_/    \\___|  \\__, |  \\__,_| |_|     \\__,_|  \\___|\n" +
                "                                                                                        |___/                               "+RESET);
        System.out.println(YELLOW+"==========Initialisation de l'application=========="+RESET);

        /*
         * Récupération des configurations
         */
        Properties prop = getAppProperties();

        /*
         * Récupération de l'utilisateur
         */
        String userFile = prop.getProperty("app.userSerializeFile");
        String userName = prop.getProperty("app.userName");
        User user;
        try{
            user = User.read(userFile,userName);
            System.out.println(GREEN+"Informations utilisateurs récupéré"+RESET);
            System.out.println("Bienvenue "+user.getName());
        } catch (IOException e){
            System.err.println("Erreur lors de la récupération du fichier utilisateur, relancer l'application");
            prop.setProperty("app.firstLaunch","true");
            SystemUtils.storeAppProperties(prop);
            // TODO proposer a l'utilisateur de re-installer le programme
            throw new RuntimeException();
        }

        /*
         * Récupération / Création de 'FWQ'
         */
        String fwqFile = prop.getProperty("app.fwqSerializeFile");
        FileWaitingQueue fwq = new FileWaitingQueue(fwqFile);

        try {
            fwq.read(fwqFile);
        } catch (FileNotFoundException e){
            System.err.println("fws ser file note found");
        }

        /*
         * Création des 'fileTools' et de 'appClient'
         */
        FileChecker fCheck = new FileChecker(fwq,user);
        int nbThreads = Integer.parseInt(prop.getProperty("app.nbFileSaver"));

        AppClient app = new AppClient(fwq,fCheck,nbThreads,user);

        System.out.println("Tout c'est bien passé, application prête");
        System.out.println(YELLOW+"==================================================="+RESET);

        return app;
    }

    /**
     * Amorce le premier démarrage de l'application en invitant l'utilisateur à créer un compte,
     * puis en entrant les premiers paramètres de configuration.
     */
    public static void firstLaunch(){
        Scanner sc = new Scanner(System.in);
        boolean isOk = false;

        System.out.println(YELLOW+"Commençons la configuration de l'application"+RESET);

        /*
                                        Properties
         */

        Properties prop = getAppProperties();
        int freq = 24;
        int nbThreads = 2;

        /*
                                    User
         */
        User user = null;

        //Shell USER
        while(!isOk){
            System.out.println("Création d'un nouvel utilisateur");
            System.out.print("> nom : ");
            String userName = sc.nextLine().trim();

            System.out.print("> mot de passe :");
            String password = sc.nextLine().trim();

            System.out.println("Valider vous ces informations : "+YELLOW+userName+" -> "+password+RESET+" ?");
            System.out.print("> O/N : ");
            String input = sc.nextLine().trim().toUpperCase();
            isOk = input.equals("O");

            user = new User(userName,password);
            user.write(prop.getProperty("app.userSerializeFile"));
            //FIN Shell USER
        }

        /*
                        Création des dossiers utilisateur distant
         */

        File userDir = new File(getAppProperties().getProperty("app.destFile")+user.getName()+"/"+"Archives/");
        if(!userDir.exists()){
            userDir.mkdirs();
        }

        /*
                                    Config
         */

        isOk = false;
        while (!isOk){
            System.out.println("Configurations :");
            System.out.print("> Fréquence de sauvegarde (jours): ");
            freq = Integer.parseInt(sc.nextLine().trim()) * 24;
            //TODO : vérifier que c'est un mutiple de 24
            System.out.print("> Nombre threads de sauvegarde : ");
            nbThreads = Integer.parseInt(sc.nextLine().trim());
            System.out.print("> Valider ces paramètres ? (O/N):");
            String input = sc.nextLine().trim().toUpperCase();
            isOk = input.equals("O");
        }

        prop.setProperty("app.userName", user.getName());
        prop.setProperty("app.saveFrequency", String.valueOf(freq));
        prop.setProperty("app.nbFileSaver", String.valueOf(nbThreads));
        prop.setProperty("app.firstLaunch","false");

        SystemUtils.storeAppProperties(prop);
    }

    /**
     * TODO : Permettre de réinitialisé les properties a partir d'un fichier de récupération
     * TODO : Création des Répertoires de test Src et Dest
     */
    public static void install(){

    }

}