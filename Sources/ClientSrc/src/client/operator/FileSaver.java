package client.operator;

import client.model.FileWaitingQueue;
import client.model.SocketConnection;
import utils.Colors;

import java.io.File;
import java.io.IOException;

import static utils.SystemUtils.getProperties;

/**
 * Cette classe définit un thread de sauvegarde.
 * Les threads créés par cette classe sont en attente sur une FileWaitingQueue (FWQ)
 * et envoient les fichiers au serveur.
 */
public class FileSaver extends ThreadFileOperator{
    /**
     * Constructeur de la classe FileSaver.
     * @param fwq La File d'attente
     * @param sc La connexion au serveur
     */
    public FileSaver(FileWaitingQueue fwq, SocketConnection sc) {
        super(fwq,sc);
        this.setName(Colors.YELLOW+"FileSaver-"+ Colors.RESET);
    }

    /**
     * Consule la file d'attente (FWQ) et envoie les fichiers au serveur.
     */
    @Override
    public void run() {
        loop : while (!stop) {

            if(this.pause != -1) {
               this.pause();
            }

            File fileToSave = null;
            try {
                fileToSave = this.fwq.get(); //Collecte un fichier dans la file d'attente
            } catch (InterruptedException e) {
                //Interruption lorsque le thread est en 'wait' sur la file.
                if(this.pause !=-1){
                    this.pause();
                    continue loop;
                } else {
                    this.stop = true;
                }
            }

            if (!stop) {
                try {
                    System.out.println(getName() + " s'occupe de copier le fichier : " + fileToSave);

                    sc.sendFile(fileToSave); //Envoie d'un fichier au serveur.

                    this.fwq.remove(fileToSave); //Supprime le fichier de la backupQueue
                    this.fwq.write(getProperties("Config/application.properties").getProperty("app.fwqSerFile")); //Serialize la File d'attente
                    System.out.println(getName()+" fini de copier "+fileToSave.getName());


                } catch (IOException e) {
                    //Le fichier a mal ou pas été envoyé au serveur.
                    System.err.println("Erreur lors de l'envoie du fichier : "+fileToSave.getName());
                    try {
                        this.fwq.write(getProperties("Config/application.properties").getProperty("app.fwqSerFile")); //Serialize la File d'attente
                    } catch (IOException ex) {
                        System.err.println("Erreur lors de la sérialisation de la FWQ");
                    }
                }
            }
        }
    }

    /**
     * Méthode pour arrêter le thread.
     */
    public void stopThread() {
        this.stop = true;
        this.interrupt();
    }

}
