package fr.erwan.fileTools;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;

import static fr.erwan.utils.FileUtils.copyFolder;

/* TODO - au lieu d'écrire dans un dossier local, envoyé les fichiers a un serveur, écrire dans un socket*/
/**
 * Class qui instancie un thread de sauvegarde.
 * Les threads instanciés par cette class sont en attente sur une FWQ et s'occupent de sauvegarder les fichiers.
 */
public class FileSaver extends Thread{
    private FileWaitingQueue fileWaitingQueue;
    private boolean stop;
    private String destDirectory;

    /**
     * Constructeur de class
     * @param destDirectory emplacement du dossier de destination
     * @param fwq FileWaitingQueue
     */
    public FileSaver(FileWaitingQueue fwq,String destDirectory){
        this.fileWaitingQueue = fwq;
        this.destDirectory = destDirectory;
        this.stop = false;
    }


    /**
     * Cette méthode consulte la fwq et appelle la méthod copyFolder pour écrire
     * les fichiers dans le répertoire distant.
     */
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
                System.out.println(getName()+" s'occupe de copier le fichier : "+fileToSave.getName());

                // NEW PART
                File tmpFile = new File(destDirectory+fileToSave.getName());

                if(tmpFile.exists()){
                    String archivePath = "Archives/"+
                            LocalDate.now().getYear()+"/"
                            +LocalDate.now().getMonth().toString()+"/"
                            +LocalDate.now().getDayOfMonth()+"/";

                    File newDir = new File(destDirectory+archivePath);

                    newDir.mkdirs();

                    if(tmpFile.renameTo(new File(destDirectory+archivePath+fileToSave.getName()))){
                        System.out.println("Fichier : "+tmpFile.getName()+" déplacer -> "+archivePath);
                    } else {
                        System.out.println("ERREUR lors de l'archivage");
                    }
                }
                // END NEW PART

                copyFolder(fileToSave,new File(destDirectory+fileToSave.getName()));
            } catch (IOException e){
                /*
                @Warning que ce passe-t-il si le programme s'interrompt et que le fichier n'a pas finis de ce copier ?
                 */
                e.printStackTrace();
            }
        }
    }
}
