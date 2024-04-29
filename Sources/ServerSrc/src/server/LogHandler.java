package server;

import java.io.*;
import java.time.LocalDate;
import java.util.Date;

import static utils.SystemUtils.getProperties;
/**
 * La classe LogHandler gère l'enregistrement des messages dans un fichier journal.
 */
public class LogHandler {
    private final File LOG_FILE;

    /**
     * Constructeur de la classe LogHandler.
     * Initialise le fichier journal avec un nom basé sur la date actuelle.
     * @throws IOException Si une erreur survient lors de la création du fichier journal
     */
    public LogHandler(String destDirPath) throws IOException {
        String logFileName = "logs_file_"+
                LocalDate.now().getYear() +"_"+
                LocalDate.now().getMonth().toString() + "_"+
                LocalDate.now().getDayOfMonth();
        File logFile = new File(destDirPath+logFileName);
        logFile.createNewFile();
        this.LOG_FILE = logFile;
    }

    /**
     * Enregistre un message dans le fichier journal.
     * @param clientName Le nom du client à l'origine du message
     * @param message Le message à enregistrer
     */
    public synchronized void logMessage(String clientName, String message) {
        try (PrintWriter writer = new PrintWriter(new BufferedWriter(new FileWriter(LOG_FILE, true)))) {
            writer.println("[" + new Date() + "] " + clientName +" -> "+ message);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
