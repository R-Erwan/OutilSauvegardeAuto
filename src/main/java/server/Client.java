package server;

import client.users.User;
import utils.Colors;
import utils.FileUtils;

import java.io.*;
import java.net.Socket;
import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;

import static utils.SystemUtils.getProperties;

/**
 * La classe Client représente un client connecté au serveur.
 * Elle gère les communications avec le client et effectue les opérations demandées.
 */
public class Client extends Thread {
    private final Socket socket; // Le socket du client
    private final ObjectInputStream sisr; // Flux d'entrée pour les objets
    private final ObjectOutputStream sisw; // Flux de sortie pour les objets
    private final ArrayList<Client> serverClients; // Liste des clients connectés au serveur
    private User user; // Utilisateur associé à ce client
    private final LogHandler logHandler; // Gestionnaire de journaux pour ce client

    /**
     * Constructeur de la classe Client.
     * @param socket Le socket du client
     * @param serverClients Liste des clients connectés au serveur
     * @throws IOException Si une erreur survient lors de la création des flux d'entrée/sortie
     */
    public Client(Socket socket, ArrayList<Client> serverClients) throws IOException {
        this.socket = socket;
        this.sisr = new ObjectInputStream(socket.getInputStream());
        this.sisw = new ObjectOutputStream(socket.getOutputStream());
        this.serverClients = serverClients;
        this.logHandler = new LogHandler();
    }

    /**
     * Méthode exécutée par le thread du client.
     */
    @Override
    public void run() {
        try {
            // Le client doit d'abord authentifier un utilisateur pour effectuer d'autres requêtes
            if (authenticateUser()) {
                logHandler.logMessage(user.getName(), "CONNECTED");
                while (true) {
                    Message message = (Message) sisr.readObject(); //Attend un message
                    System.out.println(Colors.YELLOW+user.getName()+ Colors.RESET+" -> "+message.command+" : "+message.params);
                    logHandler.logMessage(user.getName(), message.command + "  "+message.params); //Log le message
                    handleCommand(message); // Execute l'opération associée
                    if (message.command == Command.Stop) break;
                }
                logHandler.logMessage(user.getName(), "DISCONNECTED");
            }
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        } finally {
            closeResources();
        }
    }

    /**
     * Gère une commande envoyée par le client.
     * @param message La commande envoyée par le client
     * @throws IOException Si une erreur survient lors de la gestion de la commande
     */
    private void handleCommand(Message message) throws IOException {
        switch (message.command) {
            case Stop:
                break;
            case SaveFile:
                saveFile(message.params);
                break;
            case GetInfoFile:
                sendFileInfo(message.params);
                break;
            case ListFiles:
                sendFileList();
                break;
            case DownloadFile:
                sendFileToClient(message.params);
                break;
            default:
                break;
        }
    }

    /**
     * Authentifie l'utilisateur en vérifiant les informations d'identification.
     * @return True si l'utilisateur est authentifié avec succès, False sinon
     * @throws IOException Si une erreur survient lors de la communication avec le client
     * @throws ClassNotFoundException Si une classe requise pour la deserialization n'est pas trouvée
     */
    private boolean authenticateUser() throws IOException, ClassNotFoundException {
        while (true) {
            Message message = (Message) sisr.readObject(); //Attend un message
            if (message.command == Command.Stop) return false;

            boolean authenticated = switch (message.command) {
                case CreateUser -> signInUser(message.name, message.params);
                case ConnectUser -> loginUser(message.name, message.params);
                default -> false;
            };
            sisw.writeBoolean(authenticated); //Renvoie si l'opération s'est bien passé
            sisw.flush();
            if (authenticated) return true;
        }
    }

    /**
     * Créer un nouvel utilisateur.
     * @param name Nom de l'utilisateur UNIQUE.
     * @param password Mot de passe.
     * @return True si un nouvel utilisateur a été créer, False si l'utilisateur existe déja sur le server.
     */
    private boolean signInUser(String name, String password) {
        User user = new User(name,password);
        File userFile = new File(getProperties("server").getProperty("server.serUser")+user.getName());
        File userData = new File(getProperties("server").getProperty("server.usersData")+name+"/"+"Archives/");

        try {
            //Si un utilisateur avec le même nom existe déja
            if(userFile.exists()){
                return false;
            }

            //Créer les dossiers utilisateurs
            if(!userData.mkdirs()) return false;

            //Serialize un objet User
            user.write(getProperties("server").getProperty("server.serUser"));

            this.user = user;
            System.out.println("New User create : "+name);
            return true;
        } catch (FileNotFoundException e){
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Connecte un utilisateur en vérifiant les informations.
     * @param name Nom d'utilisateur.
     * @param password Mot de passe.
     * @return True si les informations correspondent et existent, False sinon.
     */
    private boolean loginUser(String name, String password) {
        try{
            User userRec = User.read(getProperties("server").getProperty("server.serUser"),name);
            if (userRec.getPassword().equals(password)) {
                this.user = userRec;
                System.out.println("Connection validé de : "+name);
                return true;
            } else {
                return false;
            }
        } catch (IOException e){
            return false;
        }
    }

    /**
     * Sauvegarde un fichier envoyé par l'utilisateur dans son répertoire correspondant sur le server
     * @param fileName Chemin complet du fichier depuis son répertoire parent.
     * @throws IOException Si une erreur survient lors de l'écriture sur les flux
     */
    private void saveFile(String fileName) throws IOException {
        String repDeSauvegarde = getProperties("server").getProperty("server.usersData")+user.getName()+"/";

        File file = new File(repDeSauvegarde+fileName);
        //Si le fichier n'existe pas sur le serveur, on le crée
        if(!file.exists()){
            file.getParentFile().mkdirs();
            file.createNewFile();

        }
        //Sinon c'est que le fichier existe, donc on doit l'archivé
        else {
            archiveFile(new File(fileName)); //On passe uniquement un chemin relatif au dossier parent
        }

        //Écriture du fichier
        try (FileOutputStream fos = new FileOutputStream(repDeSauvegarde+fileName)) {
            byte[] buffer = new byte[8192];
            int bytesRead;
            while ((bytesRead = sisr.read(buffer)) != -1) {
                fos.write(buffer,0,bytesRead);
            }
        }
    }

    /**
     * Archive un fichier utilisateur déja existent sur le serveur.
     * @param file Fichier server à archivé.
     */
    private void archiveFile(File file){
        // file -> DossierX/../fichier.txt

        String userPath = getProperties("server").getProperty("server.usersData")+
                user.getName()+
                File.separator;
        //Server/Data/toto

        String archivePath = "Archives/" +
                LocalDate.now().getYear() + "/" +
                LocalDate.now().getMonth().toString() + "/" +
                LocalDate.now().getDayOfMonth() + "/";
        // archivePath -> Archives/2024/March/24

        if(file.getParent() != null){
            File firstParent = new File(userPath + file.getParent().substring(0,file.getParent().indexOf('/')+1));
            if(firstParent.exists()){
                firstParent.setLastModified(Instant.now().toEpochMilli());
            }
        }

        String newPath = userPath + archivePath;
        // newPath -> Server/Data/toto/Archives/2024/March/24/

        File newDir = new File(newPath + file.getParent()+"/");
        newDir.mkdirs();
        // newDir -> Server/Data/toto/Archives/2024/March/24/DossierX/dossierY/...

        File fileToRename = new File(getProperties("server").getProperty("server.usersData")+
                user.getName()+
                File.separator+
                file.getPath()
        );
        // Server/Data/toto/.../file.txt

        if (fileToRename.renameTo(new File(newDir.getPath()+"/"+file.getName() ) ) ) {
            System.out.println(user.getName()+ " -> Fichier : " + file.getName() + " archivé vers : " + archivePath);
        } else {
            System.err.println("ERREUR lors de l'archivage");
        }
    }

    /**
     * Envoie la date de dernière modification du fichier.
     * @param fileName nom du fichier a retourner l'information.
     * @throws IOException Si une erreur survient lors de l'écriture sur les flux
     */
    private void sendFileInfo(String fileName) throws IOException {
        String repDeSauvegarde = getProperties("server").getProperty("server.usersData")+user.getName()+"/";
        File file = new File(repDeSauvegarde+fileName);
        if(file.exists()){
            long date = file.lastModified();
            sisw.writeLong(date);
            sisw.flush();
        } else {
            sisw.writeLong(-1);
            sisw.flush();
        }
    }

    /**
     * Renvoie la liste de tous les Dossiers/Fichiers appartenant à l'utilisateur sauvegardé.
     * @throws IOException Si une erreur survient lors de l'écriture sur les flux
     */
    private void sendFileList() throws IOException {
        String repPath = getProperties("server").getProperty("server.usersData") + File.separator + user.getName();
        String fileString = FileUtils.stringifyDirectory(new File(repPath));
        PrintWriter pw = new PrintWriter(new BufferedWriter(new OutputStreamWriter(this.sisw)));
        pw.println(fileString);
        pw.println("END");
        pw.flush();
    }

    /**
     * Envoie un fichier au client. Pour l'instant ne fonctionne pas coté client.
     * @param fileName Nom du fichier.
     * @throws IOException Erreur lors de l'écriture sur les flux
     */
    private void sendFileToClient(String fileName) throws IOException {
        File fileToReSend = new File(
                getProperties("server").getProperty("server.usersData")+
                        user.getName()+
                        File.separator+
                        fileName
        );

        if(fileToReSend.exists()){
            try (FileInputStream fis = new FileInputStream(fileToReSend)) {
                byte[] buffer = new byte[8192];
                int bytesRead;
                while ((bytesRead = fis.read(buffer)) != -1) {
                    sisw.write(buffer,0,bytesRead);
                }
                sisw.flush();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            System.out.println("fichier : "+fileName+" envoyé");
        }
    }

    /**
     * Ferme tous les flux liés au socket, coupe la connexion avec le client.
     */
    private void closeResources() {
        try {
            if (this.sisr != null) this.sisr.close();
            if (this.sisw != null) this.sisw.close();
            if (socket != null) socket.close();
            serverClients.remove(this);
            System.out.println("Déconnexion : " + user.getName());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
