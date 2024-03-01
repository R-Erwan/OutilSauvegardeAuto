package fr.erwan.file.system.tools;

import java.io.File;
import java.io.IOException;

import static fr.erwan.utils.FileUtils.copyFolder;

/* TODO - au lieu d'écrire dans un dossier local, envoyé les fichiers a un serveur*/
/**
 * Class qui instancie du thread de sauvegarde. Les threads instanciés par cette class
 * sont en attente sur la fileWaitingQueue et s'occupent de sauvegarder les fichiers.
 */
public class FileSaver extends Thread{
    private FileWaitingQueue fileWaitingQueue;
    private boolean stop;
    private String destDirectory = "TestDestDirectory/";

    /**
     * @param destDirectory Path du dossier de destination
     * @param fwq File d'attente des fichiers a traité
     */
    public FileSaver(FileWaitingQueue fwq,String destDirectory){
        this.fileWaitingQueue = fwq;
        this.destDirectory = destDirectory;
        this.stop = false;
    }

    @Override
    public void run(){
        while(!stop){
            File fileToSave = null;
            try {
                fileToSave = this.fileWaitingQueue.takeFile();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            try {
                System.out.println(getName()+" s'occupe de copier le fichier : "+fileToSave);
                copyFolder(fileToSave,new File(destDirectory+fileToSave.getName()));
            } catch (IOException e){
                e.printStackTrace();
            }
        }
    }
}
