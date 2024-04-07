package client.operator;

import client.model.FileWaitingQueue;
import client.model.SocketConnection;
import client.model.UserFiles;
import utils.Colors;
import utils.FileUtils;

import java.io.File;
import java.io.IOException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;



/**
 * Explore les répertoires locaux et demande au serveur les dates des fichiers sauvegardé.
 * Les fichiers qui nécessitent une sauvegarde sont alors ajouté à la file d'attente.
 */
public class FileChecker extends ThreadFileOperator{
    private final UserFiles userFiles; //Utilisateur courant utilisant l'application
    private final double freq; //La fréquence de sauvegarde en nombre de jours
    private final double refreshCheck; //Fréquence de rafraichissement du check en heures
    private final Timer timer;

    /**
     * Constructeur de classe
     *
     * @param fwq File d'attente des fichiers.
     * @param userFiles Utilisateur courant.
     * @param freq Fréquence de sauvegarde.
     * @param sc Socket Connection avec le serveur.
     * @param nbHeure Intervalle entre les sauvegardes.
     */
    public FileChecker(FileWaitingQueue fwq, UserFiles userFiles, double freq, SocketConnection sc, double nbHeure){
        super(fwq,sc);
        this.userFiles = userFiles;
        this.freq = freq;
        this.refreshCheck = nbHeure;
        this.timer = new Timer();
        this.setName(Colors.YELLOW+"FileChecker-"+Colors.RESET);
    }

    @Override
    public void run(){
        long delay = (long) (this.refreshCheck * 3600000L);
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                check();
            }
        },0,delay);
    }
    /**
     * Effectue un check des fichiers utilisateur à sauvegarder. Lance une sauvegarde.
     * Regarde l'ensemble des dossiers spécifié par l'utilisateur et vérifie s'ils doivent être sauvegardé.
     */
    public void check(){
        System.out.println(this.getName()+"Explore les fichiers utilisateur...");
        ArrayList<File> userFilesList = this.userFiles.getListFile(); //Liste des répertoires utilisateurs
        for (File file : userFilesList) {
            if(file.exists()){
                if(needSave(file)){
                    System.out.println(Colors.GREEN+file.getName()+Colors.RESET+" Ajouter a la FWQ");
                    try {
                        this.fwq.putAndSerialize(file);
                    } catch (IOException e){
                        System.err.println("Le fichier : "+file.getName()+" n'a pas pus être ajouter a la file d'attente");
                    }
                }
            } else {
                System.out.println(Colors.GREEN+file.getName()+Colors.RESET+" -> Pas trouvé sur le disque client");
            }
        }
    }

    /**
     * Arrête le fileChecker.
     */
    public void stopThread(){
        this.timer.cancel();
        this.interrupt();
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

            if(lastModifiedServer == -1) return true; // Indique que le fichier n'existe pas sur le serveur

            //Si c'est un dossier, regarde chaque fichier
            List<File> files;
            if(clientFile.isDirectory()){
                files = FileUtils.listFiles(clientFile);
                for (File file : files){
                    if( ( lastModifiedServer < file.lastModified()) &&
                            (Instant.now().toEpochMilli() - lastModifiedServer > this.freq*3.6e+6)){
                        return true;
                    }
                }
                return false;
            }
            //Sinon on regarde juste le fichier
            else {
                return (lastModifiedServer < clientFile.lastModified()) &&
                        (Instant.now().toEpochMilli() - lastModifiedServer > this.freq * 3.6e+6);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}

