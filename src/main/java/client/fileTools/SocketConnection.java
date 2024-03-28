package client.fileTools;

import client.users.UserClient;
import server.Command;
import server.Message;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Objects;

import static utils.FileUtils.listerFichiers;

/**
 * Class pour gérer les accès au socket entre le client et le server.
 * Propose des méthodes pour communiquer avec le serveur.
 */
public class SocketConnection {
    private final UserClient user; //Utilisateur courant
    private final Socket socket;
    private final ObjectOutputStream soos; //Flux d'écriture
    private final ObjectInputStream sisr; //Flux de lecture

    /**
     * Constructeur, établie la connection entre le client et le serveur.
     * Authentifie l'utilisateur avec le serveur.
     * @param user Utilisateur courant
     * @param host nom d'hôte
     * @param port numéro de port
     * @throws IOException Peut-être provoquer lors de la création du socket, lors de l'utilisation des flux.
     */
    public SocketConnection(UserClient user, String host, int port) throws IOException {
        this.user=user;
        this.socket = new Socket(host,port);
        this.soos = new ObjectOutputStream(socket.getOutputStream());
        this.sisr = new ObjectInputStream(socket.getInputStream());

        //Essaie de se connecter avec les infos utilisateurs.
        Message message = new Message(user.getName(),Command.ConnectUser, user.getPassword());
        soos.writeObject(message);
        boolean response = sisr.readBoolean();
        if(!response){
            //Essaie de créer un nouvel utilisateur si la connection échoue.
            Message message1 = new Message(user.getName(),Command.CreateUser,user.getPassword());
            soos.writeObject(message1);
            response = sisr.readBoolean();
        }
        System.out.println("SERVER : réponse suite a l'authentification "+response);

    }

    //Méthode PUBLIC

    /**
     * Envoie un message de déconnexion au serveur.
     * Coupe les flux et le socket.
     */
    public synchronized void stopConnection(){
        try {
            Message endMessage = new Message(user.getName(),Command.Stop,"");
            soos.writeObject(endMessage);

            soos.close();
            sisr.close();
            socket.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Envoie une request pour récupérer des informations sur la date d'un fichier
     * Attend un long de la part du serveur.
     * @param fileName nom du fichier.
     * @return date de modification du fichier renvoyé par le serveur.
     * @throws IOException Lors de l'utilisation des flux.
     */
    public synchronized long getFileInfo(String fileName) throws IOException {
        Message message = new Message(user.getName(),Command.GetInfoFile,fileName);
        soos.writeObject(message);
        return sisr.readLong();
    }

    /**
     * Envoie une requête au serveur pour envoyer un fichier.
     * @param file Fichier a envoyé
     * @throws IOException Lors de l'utilisation des flux.
     */
    public synchronized void sendFile(File file) throws IOException {
        ArrayList<Object[]> list =  listerFichiers(file); //Tableaux de couples : CheminRelatif - Fichier
        for (Object[] pair : list) {
            String chemin = (String) pair[0];
            File fichier = (File) pair[1];

            Message saveFileMessage = new Message(user.getName(), Command.SaveFile,chemin);
            soos.writeObject(saveFileMessage); // Envoie un message avant d'envoyer le fichier

            try (FileInputStream fis = new FileInputStream(fichier)) {
                byte[] buffer = new byte[8192];
                int bytesRead;
                while ((bytesRead = fis.read(buffer)) != -1) {
                    soos.write(buffer,0,bytesRead);
                }
                soos.flush();
            }
        }
    }

    /**
     * Envoie une requête au serveur pour récupérer la liste des fichiers personnel.
     * @return Un string de l'arborescence de la liste des fichiers.
     * @throws IOException erreur sur les flux d'I/O
     */
    public synchronized String getFileList() throws IOException {
        Message message = new Message(user.getName(),Command.ListFiles,"");
        soos.writeObject(message); //Envoie un message au serveur.

        BufferedReader br = new BufferedReader(new InputStreamReader(this.sisr));

        StringBuilder responseBuilder = new StringBuilder();
        String line;
        //Lit la réponse du serveur.
        while(!Objects.equals(line = br.readLine(), "END")) {
            responseBuilder.append(line).append("\n");
        }
        return responseBuilder.toString();
    }

    /**
     *
     * @param fileName Sous la forme Archives/xx/xx/file.txt ou xx/xx/file.txt
     * @param dest Emplacement ou copier le fichier.
     * @return true si le fichier a bien été télécharger et bien copier.
     * @throws IOException erreur sur les flux
     */
    public synchronized boolean downloadFile(String fileName,String dest) throws IOException {
        File destFile = new File(dest);
        if(!destFile.exists()) return false;

        File copyFile = new File(dest+File.separator+new File(fileName).getName());
        copyFile.createNewFile();

        Message message = new Message(user.getName(),Command.DownloadFile,fileName);
        soos.writeObject(message);

        try (FileOutputStream fos = new FileOutputStream(copyFile)) {
            byte[] buffer = new byte[8192];
            int bytesRead;
            while ((bytesRead = sisr.read(buffer)) != -1) {
                fos.write(buffer, 0, bytesRead);
            }
            return true;
        }
    }

}
