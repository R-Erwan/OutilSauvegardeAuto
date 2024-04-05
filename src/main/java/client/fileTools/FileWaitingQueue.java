package client.fileTools;

import java.io.File;
import java.io.IOException;

/**
 * Une file d'attente pour les objets de type File avec un mécanisme de sauvegarde.
 */
public class FileWaitingQueue extends LinkedListWithBackup<File>{
    private final String serFile; //Fichier de serialisation

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
    public synchronized void putAndSerialize(File file) throws IOException {
        this.put(file);
        this.write(serFile);
    }
}