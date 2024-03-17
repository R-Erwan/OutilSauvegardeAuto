package fr.erwan;

import fr.erwan.fileTools.FileChecker;
import fr.erwan.fileTools.FileSaver;
import fr.erwan.fileTools.FileWaitingQueue;
import fr.erwan.users.User;

import java.util.ResourceBundle;

import static fr.erwan.utils.ConstantColors.RESET;
import static fr.erwan.utils.ConstantColors.YELLOW;

/**
 * La classe principale de l'application client.
 * Elle gère l'initialisation et le démarrage de l'application.
 */
public class AppClient {
    private User user; // L'utilisateur de l'application
    private FileWaitingQueue fwq; // La file d'attente des fichiers à sauvegarder
    private FileChecker fCheck; // Le vérificateur de fichiers
    private FileSaver[] fSavers; // Les sauvegardeurs de fichiers
    private boolean running;

    /**
     * Constructeur de l'application client.
     *
     * @param fwq            La file d'attente des fichiers à sauvegarder
     * @param fCheck         Le vérificateur de fichiers
     * @param nbFileSavers   Le nombre de sauvegardes de fichiers
     * @param user           L'utilisateur de l'application
     */
    public AppClient(FileWaitingQueue fwq, FileChecker fCheck, int nbFileSavers, User user) {
        ResourceBundle resourceBundle = ResourceBundle.getBundle("application");
        String destFile = resourceBundle.getString("app.destFile");

        this.user = user;
        this.fwq = fwq;
        this.fCheck = fCheck;
        this.fSavers = new FileSaver[nbFileSavers];
        this.running = false;

        for (int i = 0; i < nbFileSavers; i++) {
            FileSaver fs = new FileSaver(fwq, destFile + user.getName() + '/');
            fs.setName(YELLOW + "FileSaver-" + i + " " + RESET);
            this.fSavers[i] = fs;
        }
    }

    public User getUser() {
        return user;
    }

    public FileWaitingQueue getFwq() {
        return fwq;
    }

    public FileChecker getfCheck() {
        return fCheck;
    }

    /**
     * Méthode pour démarrer l'application.
     * Elle lance le vérificateur de fichiers et toutes les sauvegardes de fichiers.
     */
    public void startApp() {
        System.out.println("Lancement de l'app Client");
        if (!running) {
            for (FileSaver fileSaver : this.fSavers) {
                fileSaver.start();
            }
            this.fCheck.check();
            this.running = true;
        }
    }

    /**
     * Méthode pour arrêter l'application.
     * Elle arrête tous les threads de sauvegarde et sauvegarde la file d'attente.
     */
    public void stopApp() {
        System.out.println("Arrêt de l'application...");
        ResourceBundle resourceBundle = ResourceBundle.getBundle("application");
        String fwqFile = resourceBundle.getString("app.fwqSerializeFile");

        for (FileSaver fileSaver : this.fSavers) {
            System.out.println("Arrêt du thread :" + fileSaver.getName());
            fileSaver.arret();
        }

        synchronized (fwq) {
            fwq.notifyAll();
        }

        // Serialisation FWQ
        this.fwq.write(fwqFile);
        System.out.println("File d'attente sauvegardé");

        try {
            System.out.println("Attente des threads FileSavers");
            for (FileSaver fileSaver : this.fSavers) {
                fileSaver.join();
            }
        } catch (InterruptedException e) {
            System.err.println("Erreur lors de l'attente des threads de sauvegarde");
            e.printStackTrace();
        }

        this.running = false;

        System.out.println("Arrêt de l'application Client correcte !");
    }
}
