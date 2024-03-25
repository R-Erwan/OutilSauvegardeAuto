package client.users;

import java.io.*;
import java.util.Objects;

/**
 * Représente un utilisateur avec un nom et un mot de passe.
 * Cette classe est sérializable pour permettre la sauvegarde et la récupération des utilisateurs.
 */
public class User implements Serializable {
    private String name; //Nom de l'utilisateur
    private String password; //Mot de passe a déclaré transient si on ne veut pas sauvegarder le mdp sur le pc de l'utilisateur

    /**
     * Constructeur de la classe User.
     *
     * @param name     Nom de l'utilisateur.
     * @param password Mot de passe de l'utilisateur.
     */
    public User(String name, String password){
        this.name = name;
        this.password = password;
    }

    public String getName() {
        return name;
    }

    public String getPassword() {
        return password;
    }

    /**
     * Méthode permettant de sauvegarder l'utilisateur dans un fichier sérialisé.
     *
     * @param destSer Chemin de destination du fichier sérialisé.
     * @throws FileNotFoundException Si le fichier de destination est introuvable.
     */
    public void write(String destSer) throws FileNotFoundException{
        try{
            FileOutputStream fos = new FileOutputStream(destSer+this.getName()+".ser");
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(this);
            oos.close();
            fos.close();
        } catch (FileNotFoundException e) {
            System.err.println("Fichier de destination not found");
            throw new FileNotFoundException();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Méthode permettant de désérialiser un utilisateur à partir d'un fichier.
     *
     * @param srcSer   Chemin du fichier sérialisé.
     * @param userName Nom de l'utilisateur à récupérer.
     * @return L'utilisateur désérialisé.
     * @throws IOException En cas d'erreur lors de la lecture du fichier.
     */
    public static User read(String srcSer, String userName) throws IOException{
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

    // Méthodes equals, hashCode et toString surchargées

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

        if (!name.equals(user.name)) return false;
        return Objects.equals(password, user.password);
    }

    @Override
    public int hashCode() {
        int result = name.hashCode();
        result = 31 * result + (password != null ? password.hashCode() : 0);
        return result;
    }
}
