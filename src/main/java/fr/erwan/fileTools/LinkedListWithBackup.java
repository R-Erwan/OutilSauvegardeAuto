package fr.erwan.fileTools;

import java.io.*;
import java.util.LinkedList;

/**
 * Une file d'attente basée sur LinkedList avec un mécanisme de sauvegarde.
 *
 * @param <E> le type des éléments dans la file d'attente
 */
public class LinkedListWithBackup<E> implements Serializable {
    private LinkedList<E> queue;
    private LinkedList<E> backUpQueue;

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
     * @param destSer  le chemin de destination pour la sérialisation
     */
    public synchronized void put(E object,String destSer) {
        queue.add(object);
        this.write(destSer);
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
     *
     * @param element l'élément à supprimer
     */
    public synchronized void remove(E element) {
        this.backUpQueue.remove(element);
    }

    /**
     * Vérifie si la file d'attente principale est vide.
     *
     * @return true si la file est vide, sinon false
     */
    public synchronized boolean isEmpty(){
        return this.queue.isEmpty();
    }

    /**
     * Sérialise les files d'attente dans un fichier spécifié.
     *
     * @param dest le chemin de destination pour la sérialisation
     */
    public synchronized void write(String dest) {
        try {
            FileOutputStream fos = new FileOutputStream(dest + "fwq.ser");
            ObjectOutputStream oos = new ObjectOutputStream(fos);

            // Sérialise d'abord la file de sauvegarde
            oos.writeObject(backUpQueue);
            // Sérialise ensuite la file principale
            oos.writeObject(queue);

            oos.close();
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Désérialise les files d'attente depuis un fichier spécifié.
     *
     * @param source le chemin source pour la désérialisation
     * @throws FileNotFoundException si le fichier source est introuvable
     */
    public synchronized void read(String source) throws FileNotFoundException {
        try {
            FileInputStream fis = new FileInputStream(source + "fwq.ser");
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
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public String toString() {
        return "FileWaitingQueue2{" +
                "queue=" + queue +
                ", backUpQueue=" + backUpQueue +
                '}';
    }
}
