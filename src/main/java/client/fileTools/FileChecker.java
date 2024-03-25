package client.fileTools;

import utils.ConstantColors;
import client.users.UserClient;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.ResourceBundle;


/**
 * Explore les répertoires locaux et demande au serveur les dates des fichiers suavegardé.
 * Les fichiers qui nécessite une sauvegarde sont alors ajouté à la file d'attente.
 */
public class FileChecker{
    private final FileWaitingQueue fwq; //File d'attente
    private final UserClient userClient; //Utilisateur courant utilisant l'application
    private double freq; //La fréquence de sauvegarde en nombres d'heures
    private SocketConnection sc; //La connexion au serveur pour faire des requires sur les fichiers

    /**
     * Constructeur de class
     * @param fwq File d'attente des fichiers
     * @param userClient Utilisateur courant
     */
    public FileChecker(FileWaitingQueue fwq, UserClient userClient, double freq, SocketConnection sc){
        this.fwq = fwq;
        this.userClient = userClient;
        this.freq = freq;
        this.sc = sc;
    }

    /**
     * Effectue un check des fichiers utilisateur à sauvegarder. Lance une sauvegarde.
     * Regarde l'ensemble des dossiers spécifié par l'utilisateur et vérifie s'ils doivent être sauvegardé.
     */
    public void check(){
        System.out.println("Initiation d'une sauvegarde, checking...");
        ArrayList<File> userFilesList = this.userClient.getListFile(); //Liste des répertoires utilisateurs
        for (File file : userFilesList) {
            if(file.exists()){
                if(needSave(file)){
                    System.out.println(ConstantColors.GREEN+file.getName()+ ConstantColors.RESET+" Ajouter a la FWQ");
                    this.fwq.putAndSerialize(file);
                }
            } else {
                System.out.println(file+" -> Pas trouvé sur le disque client");
            }
        }
    }


    /**
     * Compare les dates de modification du fichier client avec celui sur le serveur
     * @param clientFile fichier client
     * @return true si le fichier a une date de modification supérieure à celui précédemment sauvegarder,
     * si par rapport à la fréquence de sauvegarde le fichier a besoin d'une sauvegarde,
     * si le fichier n'existe pas sur le serveur.
     * False sinon.
     */
    private boolean needSave(File clientFile) {
        try{
            long lastModifiedServer = sc.getFileInfo(clientFile.getName()); // Requête au serveur.
            if(lastModifiedServer==-1) return true; // Indique que le fichier n'existe pas sur le serveur

            if( ( lastModifiedServer < clientFile.lastModified() ) &&
                    (Instant.now().toEpochMilli() - lastModifiedServer > this.freq*3.6e+6 )
            ) {
                return true;
            }
            else {
                return false;
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}

