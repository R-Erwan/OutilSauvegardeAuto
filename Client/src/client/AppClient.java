package client;

import client.operator.FileChecker;
import client.operator.FileSaver;
import client.model.FileWaitingQueue;
import client.model.LinkedListWithBackup;
import client.model.User;
import client.model.UserFiles;
import client.model.SocketConnection;
import utils.Colors;
import utils.SystemUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;
import java.util.Scanner;

import static utils.SystemUtils.getProperties;

/**
 * La classe principale de l'application client.
 * Elle gère l'initialisation et le démarrage de l'application.
 * Pour lancer une application : InitApp → StartApp
 */
public class AppClient{
    private final UserFiles userFiles; // L'utilisateur de l'application
    private final FileWaitingQueue fwq; // La file d'attente des fichiers à sauvegarder
    private final FileChecker fCheck; // Le vérificateur de fichiers
    private final FileSaver fSaver; // Le thread de sauvegarde
    private final SocketConnection sc; // La connexion au serveur
    private boolean running;

    /**
     * Constructeur de l'application client.
     *
     * @param fwq            La file d'attente des fichiers à sauvegarder
     * @param fCheck         Le vérificateur de fichiers
     * @param userFiles     L'utilisateur de l'application
     * @param sc             La connexion au serveur
     */
    public AppClient(FileWaitingQueue fwq, FileChecker fCheck, UserFiles userFiles, SocketConnection sc) {
        this.userFiles = userFiles;
        this.fwq = fwq;
        this.fCheck = fCheck;
        this.fSaver = new FileSaver(fwq,sc);
        this.running = false;
        this.sc=sc;
    }

    /**
     * Getter Utilisateur courant
     * @return UserClient
     * @see User
     * @see UserFiles
     */
    public UserFiles getUser() {
        return userFiles;
    }

    public FileChecker getfCheck(){return fCheck;}

    /**
     * Getter File d'attente
     * @return FileWaitingQueue
     * @see LinkedListWithBackup
     */
    public FileWaitingQueue getFwq() {
        return fwq;
    }

    /**
     * Getter Connection serveur
     * @return SocketConnection
     * @see SocketConnection
     */
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
            this.fCheck.start();
            this.running = true;
        }
    }

    /**
     * Méthode pour arrêter l'application.
     * Elle arrête le thread de Sauvegarde, la connexion au serveur et sauvegarde la file d'attente.
     */
    public void stopApp() {
        System.out.println("Arrêt de l'application");
        Properties prop = getProperties("application");

        System.out.println("Arrêt du FileChecker");
        this.fCheck.stopThread();

        System.out.println("Arrêt du FileSaver "+this.fSaver.getName());
        fSaver.stopThread();
        synchronized (fwq) {
            fwq.notifyAll();
        }

        try {
            this.fwq.write(prop.getProperty("app.fwqSerFile"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        System.out.println("File d'attente sauvegardé : "+ Colors.BLUE+fwq.toString()+Colors.RESET);

        try {
            this.fSaver.join();
        } catch (InterruptedException e) {
            System.err.println(fSaver.getName()+" interrupted !");
        }

        System.out.println("Arrêt de la connexion au serveur");
        this.sc.stopConnection();

        this.running = false;
        System.out.println("Arrêt de l'application Client...");
    }

    /**
     * Met en pause le thread de sauvegarde.
     *
     * @param nbPause temps de pause du thread en seconde.
     * @see FileSaver
     */
    public void pauseSave(int nbPause){
        this.fSaver.setPause(nbPause);
        this.fSaver.interrupt();
    }


    /**
     * Initialise l'application client en récupérant les configurations et les données utilisateur.
     * @return L'application client initialisée.
     */
    public static AppClient InitApp(){
        System.out.println(Colors.YELLOW+"==========Initialisation de l'application=========="+Colors.RESET);
        Properties prop = getProperties("application");

        //USER
        String userFile = prop.getProperty("app.userSerFile");
        String userName = prop.getProperty("app.userName");
        UserFiles userFiles;
        try{
            userFiles = UserFiles.read(userFile,userName);
            System.out.println(Colors.GREEN+"Informations utilisateurs récupéré"+Colors.RESET);
            System.out.println("Bienvenue "+ userFiles.getName());
        } catch (IOException e){
            System.err.println("Le fichier utilisateur a été supprimer, relancer l'application");
            prop.setProperty("app.firstLaunch","true");
            SystemUtils.storeAppProperties(prop); //Enregistre les config
            throw new RuntimeException();
        }

        //SOCKET CONNECTION
        SocketConnection sc;
        try {
            sc = new SocketConnection(
                    prop.getProperty("app.host"),
                    Integer.parseInt(prop.getProperty("app.port"))
            );
        } catch (IOException e) {
            System.err.println("Erreur lors de la création de la SocketConnection");
            throw new RuntimeException(e);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }

        boolean authResponse;
        try {
            authResponse = sc.logUser(userFiles);
            if(!authResponse) {
                authResponse = sc.signUser(userFiles);
            }
        } catch (IOException | ClassNotFoundException e){
            throw new RuntimeException(e);
        }

        if(!authResponse) {
            sc.stopConnection();
            throw new RuntimeException("Impossible de connecter l'utilisateur, essayer de réinitialisé l'application");
        }


        //FWQ
        String fwqFile = prop.getProperty("app.fwqSerFile");
        FileWaitingQueue fwq = new FileWaitingQueue(fwqFile);

        try {
            fwq.read(fwqFile);
        } catch (FileNotFoundException e){
            System.err.println("Pas de file d'attente précédemment sauvegardé");
        } catch (Exception e){
            throw new RuntimeException();
        }

        //FILE CHECKER
        double freq = Double.parseDouble(prop.getProperty("app.freq"));
        double refresh = Double.parseDouble(prop.getProperty("app.refresh"));
        FileChecker fCheck = new FileChecker(fwq, userFiles,freq,sc,refresh);

        //APP.

        AppClient app = new AppClient(fwq,fCheck, userFiles,sc);

        System.out.println("Tout c'est bien passé, application prête");
        System.out.println(Colors.YELLOW+"==================================================="+Colors.RESET);

        return app;
    }

    /**
     * Amorce le premier démarrage de l'application en invitant l'utilisateur à créer un compte,
     * puis en entrant les premiers paramètres de configuration.
     */
    public static void createFirstApp(){
        installFile(); //Créer les dossiers nécessaires.

        Scanner sc = new Scanner(System.in);
        boolean isOk = false;

        System.out.println(Colors.YELLOW+"Configuration de l'application"+Colors.RESET);
        //PROP
        Properties prop = getProperties("application");
        int freq = 24;

        //USER
        UserFiles userFiles = null;
        while(!isOk){
            System.out.println("Création d'un nouvel utilisateur");
            System.out.print("> nom : ");
            String userName = sc.nextLine().trim();

            System.out.print("> mot de passe :");
            String password = sc.nextLine().trim();

            System.out.println("Valider vous ces informations : "+Colors.YELLOW+userName+" -> "+password+Colors.RESET+" ?");
            System.out.print("> O/N : ");
            String input = sc.nextLine().trim().toUpperCase();
            isOk = input.equals("O");

            userFiles = new UserFiles(userName,password);
            userFiles.write(prop.getProperty("app.userSerFile")); //Serialize le fichier utilisateur
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

        prop.setProperty("app.userName", userFiles.getName());
        prop.setProperty("app.freq", String.valueOf(freq));
        prop.setProperty("app.firstLaunch","false");

        SystemUtils.storeAppProperties(prop); //Sauvegarde les Configs
    }

    /**
     * Créer les dossiers nécessaires au fonctionnement de l'application
     */
    private static void installFile(){
        Properties prop = getProperties("application");
        File serFiles = new File(prop.getProperty("app.userSerFile"));
        if(!serFiles.exists()) serFiles.mkdir();
    }

}

