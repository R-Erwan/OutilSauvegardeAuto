package fr.erwan.users;

import java.io.*;
import java.util.ArrayList;
import java.util.Objects;

/**
 * Permet de gérer des utilisateurs et leur liste de fichiers à sauvegarder
 * name : nom d'utilisateur qui unique*
 * listFile : peut contenir des dossiers de n'importe quel structure, des fichiers, des dossiers de dossier...
 */
public class User implements Serializable {
    private String name;
    private String password;
    private ArrayList<File> listFile;

    public User(String name, String password, ArrayList<File> listFile) {
        this.name = name;
        this.password = password;
        this.listFile = listFile;
    }

    /**
     * Constructeur qui instancie la liste des fichiers
     * @param name nom unique
     * @param password mot de passe pour se connecter
     */
    public User(String name, String password){
        ArrayList<File> listFiles = new ArrayList<File>();
        this.name = name;
        this.password = password;
        this.listFile = listFiles;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public ArrayList<File> getListFile() {
        return listFile;
    }

    public void setListFile(ArrayList<File> listFile) {
        this.listFile = listFile;
    }

    /**
     * Sérialize un objet user
     * @param destSer emplacement du fichier de sérialisation
     */
    public void write(String destSer){
        try {
            FileOutputStream fos = new FileOutputStream(destSer+this.getName()+".ser");
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(this);
            oos.close();
            fos.close();
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Deserialize un User
     * @param srcSer emplacement du fichier sérialisé
     * @param userName nom de l'utilisateur
     * @return objet User désérialisé
     */
    public static User read(String srcSer,String userName) throws IOException{
        User u = null;
        try {
            FileInputStream fis = new FileInputStream(srcSer + userName+".ser");
            ObjectInputStream ois = new ObjectInputStream(fis);
            u = (User) ois.readObject();
            fis.close();
            ois.close();
        } catch (ClassNotFoundException e){
            e.printStackTrace();
        }
        return u;
    }

    /**
     * Ajoute un fichier à la liste des fichiers à garder en sauvegarde.
     * Vérifie l'existence du fichier.
     * @param fileClient fichier à sauvegarder
     * @throws FileNotFoundException si le fichier spécifié n'existe pas
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
     * Ajoute une liste de fichier à sauvegarder
     * @param filesClient ArrayList de plusieurs fichiers à garder en sauvegarde
     * @throws FileNotFoundException si un des fichiers de la liste n'existe pas
     */
    public void addFileToSave(ArrayList<File> filesClient) throws FileNotFoundException {
        for(File file : filesClient){
          this.addFileToSave(file);
        }
    }

    /**
     * Nettoie la liste des fichiers utilisateurs à sauvegarder.
     */
    public void clearFileToSave(){
        this.listFile.clear();
    }

    @Override
    public String toString() {
        return "User{" +
                "name='" + name + '\'' +
                ", password='" + password + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        User user = (User) o;

        if (!Objects.equals(name, user.name)) return false;
        return Objects.equals(password, user.password);
    }

    @Override
    public int hashCode() {
        int result = name != null ? name.hashCode() : 0;
        result = 31 * result + (password != null ? password.hashCode() : 0);
        return result;
    }
}
