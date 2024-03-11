package fr.erwan.fileTools;

import java.io.*;
import java.util.ResourceBundle;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
/* TODO : Gérer la sérialisation de la fwq*/

/**
 * FWQ BlockingQueue
 * File d'attente de fichier qui doivent être sauvegardé.
 * Le FileChecker incrémente cette file d'attente
 * Les FileSaver consulte cette file pour sauvegarder les fichiers
 */
public class FileWaitingQueue implements Serializable {
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

    /**
     * Serialize la FWQ dans un fichier passé dans le fichier de propriété application.properties
     */
    public void write(String dest){
        try {
            FileOutputStream fos = new FileOutputStream(dest+".ser");
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(this);
            oos.close();
            fos.close();
        } catch (FileNotFoundException e){
            e.printStackTrace();
        } catch (IOException e){
            e.printStackTrace();
        }
    }

    /**
     *
     * @param src
     * @return
     */
    public static FileWaitingQueue read(String src) throws IOException{
        FileWaitingQueue fwq = null;
        try {
            FileInputStream fis = new FileInputStream(src+".ser");
            ObjectInputStream ois = new ObjectInputStream(fis);
            fwq = (FileWaitingQueue) ois.readObject();
            fis.close();
            ois.close();
        } catch (ClassNotFoundException e){
            e.printStackTrace();
        }
        return fwq;
    }
}
