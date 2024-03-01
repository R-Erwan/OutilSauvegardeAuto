package fr.erwan.file.system.tools;


import fr.erwan.users.User;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.ResourceBundle;

/* TODO - permettre de faire des demande au serveur pour savoir si un fichier
*   doit être sauvegarder */
/**
 * Class qui a comme principale fonctionnalité de s'occuper d'ajouter des fichiers
 * à sauvegarder à la fwq.
 */
public class FileChecker extends Thread{
    private FileWaitingQueue fwq;
    private User user;
    private int frequence; // en nombres d'heure
    private boolean stop;
    private String server = "TestDestDirectory/"; //TODO : le chemin sera renvoyé par le serveur

    public FileChecker(FileWaitingQueue fwq,User user){
        this.fwq = fwq;
        this.user = user;
        ResourceBundle ressourceBundle = ResourceBundle.getBundle("application");
        this.frequence = Integer.parseInt(ressourceBundle.getString("application.saveFrequency"));
        this.stop = false;
    }

    @Override
    public void run(){
        ArrayList<File> userFilesList = this.user.getListFile();
        for (File file : userFilesList) {
            if(file.exists()){
                try {
                    if(needSave(file)){
                        this.fwq.putFile(file);
                    }
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            } else {
                /* TODO : Gérer l'erreur, peut être dus a une corrumption de la liste des fichier user
                *   Ou a un déplacement du fichier par l'utilisateur
                * */
                System.out.println(file+" -> Pas trouvé sur le disque client");
            }


        }

    }

    /**
     * Récupérer les infos sur un fichierServer en fonction d'un fichier client
     * @param file
     * @return
     * @throws FileNotFoundException
     */
    private File getFileInfoServer(File file) throws FileNotFoundException{
        File serverFile = new File(this.server + file.getName());
        if(serverFile.exists()){
            return serverFile;
        } else{
            throw new FileNotFoundException("Fichier non trouvé sur le serveur");
        }
    }
/*
Note à moi : Lorsque que l'on copie un fichier, on garde sa date de création
 */

    /**
     * Compare les dates de modification du fichier client avec celui sur le serveur
     * @param clientFile
     * @return boolean
     * @throws FileNotFoundException
     */
    private boolean needSave(File clientFile) {
        try {
            File serverFile = getFileInfoServer(clientFile);
            if ( ( serverFile.lastModified() < clientFile.lastModified() ) ||
                    ( serverFile.lastModified() - clientFile.lastModified() > this.frequence * 3.6e+6 )
            ) {
                return true;
            }
            else {
                return false;
            }
        } catch (FileNotFoundException e){
            System.out.println("Le fichier n'existe pas sur le serveur donc on le créer");
            return true;
        }
    }

}

