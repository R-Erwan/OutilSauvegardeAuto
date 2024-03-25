package client.fileTools;


import utils.ConstantColors;
import client.users.UserClient;
import utils.SystemUtils;

import java.io.*;
import java.util.Properties;
import java.util.Scanner;

import static utils.SystemUtils.getProperties;

/**
 * La classe principale de l'application client.
 * Elle gère l'initialisation et le démarrage de l'application.
 * Pour lancer une application : InitApp -> StartApp
 */
public class AppClient implements ConstantColors{
    private UserClient userClient; // L'utilisateur de l'application
    private final FileWaitingQueue fwq; // La file d'attente des fichiers à sauvegarder
    private FileChecker fCheck; // Le vérificateur de fichiers
    private FileSaver fSaver; // Les sauvegardeurs de fichiers
    private SocketConnection sc; // La connexion au serveur
    private boolean running;

    /**
     * Constructeur de l'application client.
     *
     * @param fwq            La file d'attente des fichiers à sauvegarder
     * @param fCheck         Le vérificateur de fichiers
     * @param userClient     L'utilisateur de l'application
     * @param sc             La connexion au serveur
     */
    public AppClient(FileWaitingQueue fwq, FileChecker fCheck, UserClient userClient,SocketConnection sc) {
        this.userClient = userClient;
        this.fwq = fwq;
        this.fCheck = fCheck;
        this.fSaver = new FileSaver(fwq,sc);
        this.running = false;
        this.sc=sc;
        FileSaver fs = new FileSaver(fwq,sc);
    }

    public UserClient getUser() {
        return userClient;
    }

    public FileWaitingQueue getFwq() {
        return fwq;
    }

    public FileChecker getfCheck() {
        return fCheck;
    }

    public void setSc(SocketConnection sc) {
        this.sc = sc;
    }

    public SocketConnection getSc() {
        return sc;
    }

    /**
     * Méthode pour démarrer l'application.
     * Elle lance le vérificateur de fichiers et le thread de sauvegarde
     */
    public void startApp() {
        System.out.println("Lancement de l'app Client");
        if (!running) {
            this.fSaver.start();
            this.fCheck.check();
            this.running = true;
        }
    }

    /**
     * Méthode pour arrêter l'application.
     * Elle arrête le thread de Sauvegarde, la connexion au serveur et sauvegarde la file d'attente.
     */
    public void stopApp() {
        System.out.println("Arrêt de l'application...");
        Properties prop = getProperties("application");

        System.out.println("Arrêt du thread"+this.fSaver.getName());
        fSaver.arret();

        synchronized (fwq) {
            fwq.notifyAll();
        }

        // Serialisation FWQ
        this.fwq.write(prop.getProperty("app.fwqSerFile"));
        System.out.println("File d'attente sauvegardé");

        try {
            this.fSaver.join();
        } catch (InterruptedException e) {
            System.err.println("Erreur lors de l'attente des threads de sauvegarde");
            e.printStackTrace();
        }
        System.out.println("Arrêt connexion serveur");
        this.sc.stopConnection();

        this.running = false;
        System.out.println("Arrêt de l'application Client correcte !");
    }


    /**
     * Initialise l'application client en récupérant les configurations et les données utilisateur.
     * @return L'application client initialisée.
     */
    public static AppClient InitApp(){
        System.out.println(YELLOW+"==========Initialisation de l'application=========="+RESET);
        Properties prop = getProperties("application");

        //USER
        String userFile = prop.getProperty("app.userSerFile");
        String userName = prop.getProperty("app.userName");
        UserClient userClient;
        try{
            userClient = UserClient.read(userFile,userName);
            System.out.println(GREEN+"Informations utilisateurs récupéré"+RESET);
            System.out.println("Bienvenue "+ userClient.getName());
        } catch (IOException e){
            System.err.println("Erreur lors de la récupération du fichier utilisateur, relancer l'application");
            prop.setProperty("app.firstLaunch","true");
            SystemUtils.storeAppProperties(prop); //Enregistre les config
            throw new RuntimeException();
        }

        //SOCKET CONNECTION
        SocketConnection sc;
        try {
            sc = new SocketConnection(
                    userClient,
                    prop.getProperty("app.host"),
                    Integer.parseInt(prop.getProperty("app.port"))
            );
        } catch (IOException e) {
            System.err.println("Erreur lors de la création de la SocketConnection");
            throw new RuntimeException(e);
        }

        //FWQ
        String fwqFile = prop.getProperty("app.fwqSerFile");
        FileWaitingQueue fwq = new FileWaitingQueue(fwqFile);

        try {
            fwq.read(fwqFile);
        } catch (FileNotFoundException e){
            System.err.println("pas de file d'attente sauvegardé précédemment");
        }

        //FILE CHECKER
        double freq = Double.parseDouble(prop.getProperty("app.freq"));
        FileChecker fCheck = new FileChecker(fwq,userClient,freq,sc);

        //APP

        AppClient app = new AppClient(fwq,fCheck,userClient,sc);

        System.out.println("Tout c'est bien passé, application prête");
        System.out.println(YELLOW+"==================================================="+RESET);

        return app;
    }

    /**
     * Amorce le premier démarrage de l'application en invitant l'utilisateur à créer un compte,
     * puis en entrant les premiers paramètres de configuration.
     */
    public static void createFirstApp(){
        installFile();

        Scanner sc = new Scanner(System.in);
        boolean isOk = false;

        System.out.println(YELLOW+"Commençons la configuration de l'application"+RESET);
        //PROP
        Properties prop = getProperties("application");
        int freq = 24;

        //USER
        UserClient userClient = null;
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

            userClient = new UserClient(userName,password);
            userClient.write(prop.getProperty("app.userSerFile")); //Serialize le fichier utilisateur
        }

        //CONFIG
        isOk = false;
        while (!isOk){
            System.out.println("Configurations :");
            System.out.print("> Fréquence de sauvegarde (jours): ");
            freq = Integer.parseInt(sc.nextLine().trim()) * 24;
            System.out.print("> Valider ces paramètres ? (O/N):");
            String input = sc.nextLine().trim().toUpperCase();
            isOk = input.equals("O");
        }

        prop.setProperty("app.userName", userClient.getName());
        prop.setProperty("app.freq", String.valueOf(freq));
        prop.setProperty("app.firstLaunch","false");

        SystemUtils.storeAppProperties(prop); //Sauvegarde les Configs
    }

    private static void installFile(){
        Properties prop = getProperties("application");
        File serFiles = new File(prop.getProperty("app.userSerFile"));
        if(!serFiles.exists()) serFiles.mkdir();
    }

}

