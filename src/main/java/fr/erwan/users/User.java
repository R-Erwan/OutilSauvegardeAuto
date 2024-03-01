package fr.erwan.users;

import java.io.*;
import java.util.ArrayList;
import java.util.Objects;

/**
 * Permet de gérer des utilisateurs et leur liste de fichiers à sauvegarder
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
     * Sérialize un User
     * @param destSer Chemin de dossier ou require le fichier de serialisation
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
     * @param srcSer Chemin du dossier du fichier sérializer
     * @param userName Nom de l'utilisateur
     * @return User
     */
    public static User read(String srcSer,String userName){
        User u = null;
        try {
            FileInputStream fis = new FileInputStream(srcSer + userName+".ser");
            ObjectInputStream ois = new ObjectInputStream(fis);
            u = (User) ois.readObject();
            fis.close();
            ois.close();
        } catch (ClassNotFoundException e) {
            System.out.println("User class not found");
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return u;
    }

    /**
     * Ajoute des fichiers à la liste des fichiers à garder en sauvegarde
     * Verifie l'existence du fichier
     * @param fileClient fichier à sauvegarder
     * @return boolean
     */
    public void addFileToSave(File fileClient) throws FileNotFoundException {
        if(!fileClient.exists()) {
            throw new FileNotFoundException("Fichier client introuvable");
        } else {
            this.listFile.add(fileClient);

        }
    }

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
