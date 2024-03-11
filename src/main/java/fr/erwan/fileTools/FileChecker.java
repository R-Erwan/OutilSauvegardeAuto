package fr.erwan.fileTools;

import fr.erwan.users.User;

import java.io.File;
import java.io.FileNotFoundException;
import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.ResourceBundle;

/* TODO - permettre de faire des demande au serveur pour savoir si un fichier
*   doit être sauvegarder */
/**
 * Class qui représente un sous-programme qui va gérer les programmes a sauvegardé en consultant
 * la liste de fichier de l'utilisateur et comparant ces fichiers avec ceux déja sauvegarder
 */
public class FileChecker extends Thread{
    private FileWaitingQueue fwq;
    private User user;
    private double frequence; // en nombres d'heure
    private boolean stop;
    private String server = "TestDestDirectory/"; //TODO : le chemin sera renvoyé par le serveur

    /**
     * Constructeur de class
     * Récupère des informations depuis un utilisateur courant, une FWQ, et le fichier de configuration
     * application.properties
     * @param fwq FileWaitingQueue courante
     * @param user User courant
     */
    public FileChecker(FileWaitingQueue fwq,User user){
        this.fwq = fwq;
        this.user = user;
        ResourceBundle ressourceBundle = ResourceBundle.getBundle("application");
        this.frequence = Float.parseFloat(ressourceBundle.getString("application.saveFrequency"));
        this.stop = false;
    }

    /**
     * La run du thread doit :
     * - Récupérer la liste des dossiers utilisateur à garder en sauvegarde
     * - Pour chaque dossier, on vérifie si les fichiers qu'il contient doivent être sauvegardé
     * en consultant les sauvegarde éffectué.
     */
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
     * Récupérer les infos sur un fichierServer en fonction d'un fichierClient
     * @param file fichierClient
     * @return Objet File depuis le répertoire de sauvegarde
     * @throws FileNotFoundException si le fichier n'est pas trouvé sur le répertoire de sauvegarde
     */
    private File getFileInfoServer(File file) throws FileNotFoundException{
        File serverFile = new File(this.server + user.getName()+"/"+file.getName());
        if(serverFile.exists()){
            return serverFile;
        } else{
            throw new FileNotFoundException("Fichier non trouvé sur le serveur");
        }
    }

    /**
     * Compare les dates de modification du fichier client avec celui sur le serveur
     * @param clientFile fichier client
     * @return true si le fichier a une date de modification supérieure à celui précédemment sauvegarder,
     * ou si par rapport à la fréquence de sauvegarde le fichier a besoin d'une sauvegarde,
     * false sinon.
     */
    private boolean needSave(File clientFile) {
        try {
            File serverFile = getFileInfoServer(clientFile);

            if ( ( serverFile.lastModified() < clientFile.lastModified() ) &&
                    (Instant.now().toEpochMilli() - serverFile.lastModified() > this.frequence*3.6e+6 )
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

