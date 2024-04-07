package client.model;

import java.io.*;
import java.util.ArrayList;

/**
 * Représente un utilisateur client avec une liste de fichiers à sauvegarder.
 * Hérite de la classe User.
 */
public class UserFiles extends User {
    private final ArrayList<File> listFile; // Liste des fichiers à sauvegarder pour cet utilisateur

    /**
     * Constructeur de la classe UserClient.
     *
     * @param name     Nom de l'utilisateur.
     * @param password Mot de passe de l'utilisateur.
     */
    public UserFiles(String name, String password){
        super(name,password);
        this.listFile = new ArrayList<File>();
    }

    /**
     * Getter
     * @return la liste des repertoires a sauvegarder
     */
    public ArrayList<File> getListFile() {
        return listFile;
    }

    // Méthodes spécifiques à UserClient pour gérer la liste de fichiers à sauvegarder

    /**
     * Ajoute un fichier à la liste des fichiers à sauvegarder pour cet utilisateur.
     *
     * @param fileClient Fichier à ajouter.
     * @throws FileNotFoundException Si le fichier spécifié n'existe pas.
     */
    public void addFileToSave(File fileClient) throws FileNotFoundException {
        if(!fileClient.exists()) {
            throw new FileNotFoundException("Fichier client introuvable");
        } else {
            if(this.listFile.contains(fileClient)){
                System.out.println("Ce fichier est déja dans la liste des fichiers a sauvegardé");
            } else {
                this.listFile.add(fileClient);
            }
        }
    }

    /**
     * Ajoute une liste de fichiers à la liste des fichiers à sauvegarder pour cet utilisateur.
     *
     * @param filesClient Liste de fichiers à ajouter.
     * @throws FileNotFoundException Si l'un des fichiers de la liste n'existe pas.
     */
    public void addFileToSave(ArrayList<File> filesClient) throws FileNotFoundException {
        for(File file : filesClient){
            this.addFileToSave(file);
        }
    }

    /**
     * Nettoie la liste des fichiers à sauvegarder pour cet utilisateur.
     */
    public void clearFileToSave(){
        this.listFile.clear();
    }

    /**
     * Méthode permettant de sauvegarder l'utilisateur client dans un fichier sérialisé.
     * Surcharge la méthode write de la classe User.
     *
     * @param destSer Chemin de destination du fichier sérialisé.
     */
    @Override
    public void write(String destSer){
        try {
            FileOutputStream fos = new FileOutputStream(destSer+this.getName()+".ser");
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(this);
            oos.close();
            fos.close();
        } catch (FileNotFoundException e) {
            System.err.println("Fichier de destination not found");
            throw new RuntimeException(e);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Méthode permettant de désérialiser un utilisateur client à partir d'un fichier.
     * Surcharge la méthode read de la classe User.
     *
     * @param srcSer   Chemin du fichier sérialisé.
     * @param userName Nom de l'utilisateur à récupérer.
     * @return L'utilisateur client désérialisé.
     * @throws IOException En cas d'erreur lors de la lecture du fichier.
     */
    public static UserFiles read(String srcSer, String userName) throws IOException{
        UserFiles uc = null;
        try {
            FileInputStream fis = new FileInputStream(srcSer + userName+".ser");
            ObjectInputStream ois = new ObjectInputStream(fis);
            uc = (UserFiles) ois.readObject();
            fis.close();
            ois.close();

        } catch (ClassNotFoundException e){
            e.printStackTrace();
        }
        return uc;
    }

}
