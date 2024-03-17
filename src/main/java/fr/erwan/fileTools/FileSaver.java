package fr.erwan.fileTools;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;

import static fr.erwan.utils.FileUtils.copyFolder;
import static fr.erwan.utils.SystemUtils.getAppProperties;

/**
 * Cette classe définit un thread de sauvegarde.
 * Les threads créés par cette classe sont en attente sur une FileWaitingQueue (FWQ) et sont responsables de sauvegarder les fichiers.
 */
public class FileSaver extends Thread {
    private FileWaitingQueue fwq;
    private boolean stop;
    private String destDirectory;

    /**
     * Constructeur de la classe FileSaver.
     * @param fwq La FileWaitingQueue sur laquelle le thread sera en attente.
     * @param destDirectory L'emplacement du dossier de destination pour sauvegarder les fichiers.
     */
    public FileSaver(FileWaitingQueue fwq, String destDirectory) {
        this.fwq = fwq;
        this.destDirectory = destDirectory;
        this.stop = false;
    }

    /**
     * Cette méthode consulte la FileWaitingQueue (FWQ) et utilise la méthode copyFolder pour écrire les fichiers dans le répertoire distant.
     */
    @Override
    public void run() {
        while (!stop) {
            // Récupération d'un fichier à traiter
            File fileToSave = null;
            try {
                fileToSave = this.fwq.get();
            } catch (InterruptedException e) {
                this.stop = true;
            }
            if (!stop) {
                try {
                    System.out.println(getName() + " s'occupe de copier le fichier : " + fileToSave.getName());

                    // Archivage
                    File tmpFile = new File(destDirectory + fileToSave.getName());
                    if (tmpFile.exists()) {
                        String archivePath = "Archives/" +
                                LocalDate.now().getYear() + "/" +
                                LocalDate.now().getMonth().toString() + "/" +
                                LocalDate.now().getDayOfMonth() + "/";

                        File newDir = new File(destDirectory + archivePath);
                        newDir.mkdirs();

                        if (tmpFile.renameTo(new File(destDirectory + archivePath + fileToSave.getName()))) {
                            System.out.println(this.getName()+ "Fichier : " + tmpFile.getName() + " archivé vers : " + archivePath);
                        } else {
                            System.out.println("ERREUR lors de l'archivage");
                        }
                    }

                    // Copie du fichier
                    copyFolder(fileToSave, new File(destDirectory + fileToSave.getName()));
                    //TODO : problème lorsque le programme s'interrompt au milieu de la copie d'un dossier
                    this.fwq.remove(fileToSave);
                    this.fwq.write(getAppProperties().getProperty("app.fwqSerializeFile"));

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * Méthode pour arrêter le thread.
     */
    public void arret() {
        this.stop = true;
        this.interrupt();
    }
}
