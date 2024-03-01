package fr.erwan.file.system.tools;

import java.io.File;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
/* TODO : Gérer la sérialisation de la fwq*/

/**
 * File d'attente de fichier à sauvegarder, des objets File sont ajouté à la file,
 * les threads FileSaver sont en attente sur cette file.
 * Le fichier ajouté à la file ont passé la vérification d'avoir besoin d'être sauvegardé.
 */
public class FileWaitingQueue {
    private BlockingQueue<File> files;

    public FileWaitingQueue(){
        this.files = new LinkedBlockingQueue<File>();
    }

    public void putFile(File file) throws InterruptedException {
        files.put(file);
    }

    public File takeFile() throws InterruptedException {
        return this.files.take();
    }

}
