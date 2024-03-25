package client.fileTools;

import java.io.File;

/**
 * Une file d'attente pour les objets de type File avec un mécanisme de sauvegarde.
 */
public class FileWaitingQueue extends LinkedListWithBackup<File>{
    private String serFile; //Fichier de serialisation

    /**
     * Constructeur prenant en paramètre le chemin de destination pour la sérialisation.
     *
     * @param serFile le chemin de destination pour la sérialisation
     */
    public FileWaitingQueue(String serFile){
        super();
        this.serFile = serFile;
    }

    /**
     * Ajoute un fichier à la file d'attente et déclenche la sérialisation.
     * @param file le fichier à ajouter
     */
    public synchronized void putAndSerialize(File file){
        this.put(file,this.serFile);
    }
}