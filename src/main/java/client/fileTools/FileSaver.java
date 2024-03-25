package client.fileTools;

import java.io.File;
import java.io.IOException;

import static utils.SystemUtils.getProperties;

/**
 * Cette classe définit un thread de sauvegarde.
 * Les threads créés par cette classe sont en attente sur une FileWaitingQueue (FWQ)
 * et envoient les fichiers au serveur.
 */
public class FileSaver extends Thread {
    private FileWaitingQueue fwq; //File d'attente
    private SocketConnection sc; //Connexion serveur pour envoyé les fichiers
    private boolean stop;

    /**
     * Constructeur de la classe FileSaver.
     * @param fwq La File d'attente
     * @param sc La connexion au serveur
     */
    public FileSaver(FileWaitingQueue fwq, SocketConnection sc) {
        this.fwq = fwq;
        this.sc=sc;
        this.stop = false;
    }

    /**
     * Consule la file d'attente (FWQ) et envoie les fichiers au serveur.
     */
    @Override
    public void run() {
        while (!stop) {
            File fileToSave = null;
            try {
                fileToSave = this.fwq.get(); //Collecte un fichier dans la file d'attente
            } catch (InterruptedException e) {
                this.stop = true;
            }
            if (!stop) {
                try {
                    System.out.println(getName() + " s'occupe de copier le fichier : " + fileToSave);

                    sc.sendFile(fileToSave); //Envoie d'un fichier au serveur.

                    this.fwq.remove(fileToSave); //Supprime le fichier de la backupQueue
                    this.fwq.write(getProperties("application").getProperty("app.fwqSerFile")); //Serialize la File d'attente
                    System.out.println(getName()+" fini de copier "+fileToSave.getName());

                    sleep(5000); // Pour tester la simulation 2, rajoute du temps lors de la copie

                } catch (IOException | InterruptedException e) {
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
