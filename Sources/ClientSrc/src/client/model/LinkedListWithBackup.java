package client.model;

import java.io.*;
import java.util.LinkedList;

/**
 * Une file d'attente basée sur LinkedList avec un mécanisme de double file.
 * Lorsqu'un objet est pris de la queue, il est ajouté à la backupQueue.
 * Dès que le traitement sur l'objet est finis, il faut le supprimer de la backupQueue.
 *
 * @param <E> le type des éléments dans la file d'attente
 */
public class LinkedListWithBackup<E> implements Serializable {
    @Serial
    private static final long serialVersionUID = 1175940438195421030L;
    private LinkedList<E> queue; //File principale
    private LinkedList<E> backUpQueue; //File de backup

    /**
     * Constructeur par défaut. Initialise la file d'attente principale et la file de sauvegarde.
     */
    public LinkedListWithBackup() {
        queue = new LinkedList<>();
        this.backUpQueue = new LinkedList<>();
    }

    /**
     * Ajoute un élément à la file d'attente principale et déclenche la sauvegarde.
     *
     * @param object   l'élément à ajouter
     */
    public synchronized void put(E object) {
        queue.add(object);
        notifyAll();
    }

    /**
     * Récupère et retire l'élément en tête de la file d'attente principale. Et l'ajoute dans la file de sauvegarde
     *
     * @return l'élément récupéré
     * @throws InterruptedException si le thread est interrompu pendant l'attente
     */
    public synchronized E get() throws InterruptedException {
        while (this.isEmpty()) {
            System.out.println(Thread.currentThread().getName()+" : en attente sur la FWQ");
            wait();
        }
        E element = queue.poll();
        backUpQueue.add(element);
        return element;
    }

    /**
     * Supprime un élément spécifié de la file de sauvegarde.
     * @param element l'élément à supprimer
     */
    public synchronized void remove(E element) {
        this.backUpQueue.remove(element);
    }

    /**
     * Vérifie si la file d'attente principale est vide.
     * @return true si la file est vide, sinon false
     */
    public synchronized boolean isEmpty(){
        return this.queue.isEmpty();
    }

    /**
     * Sérialise les files d'attente dans un fichier spécifié.
     * @param dest le chemin de destination pour la sérialisation
     */
    public synchronized void write(String dest) throws IOException {
        try {
            FileOutputStream fos = new FileOutputStream(dest);
            ObjectOutputStream oos = new ObjectOutputStream(fos);

            // Sérialise d'abord la file de sauvegarde
            oos.writeObject(backUpQueue);
            // Sérialise ensuite la file principale
            oos.writeObject(queue);

            oos.close();
            fos.close();
        } catch (IOException e) {
            throw new IOException("Erreur lors de la sérialisation de la LinkedListWithBackup");
        }
    }

    /**
     * Désérialise les files d'attente depuis un fichier spécifié.
     * @param source le chemin source pour la deserialization
     * @throws FileNotFoundException si le fichier source est introuvable
     * @throws IOException Erreur sur les flux
     */
    public synchronized void read(String source) throws IOException, ClassNotFoundException {
        try {
            FileInputStream fis = new FileInputStream(source);
            ObjectInputStream ois = new ObjectInputStream(fis);

            // Désérialise d'abord la file de sauvegarde
            backUpQueue = (LinkedList<E>) ois.readObject();
            // Désérialise ensuite la file principale
            queue = (LinkedList<E>) ois.readObject();
            queue.addAll(0,backUpQueue);

            backUpQueue.clear();

            ois.close();
            fis.close();
        } catch (FileNotFoundException e){
            throw new FileNotFoundException();
        }
    }

    @Override
    public String toString() {
        return "FileWaitingQueue{" +
                "queue=" + queue +
                ", backUpQueue=" + backUpQueue +
                '}';
    }
}
