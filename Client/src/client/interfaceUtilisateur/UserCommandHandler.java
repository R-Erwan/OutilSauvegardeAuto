package client.interfaceUtilisateur;

import client.AppClient;
import utils.Colors;
import utils.FileUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Properties;
import java.util.Scanner;

import static utils.SystemUtils.getProperties;

/**
 * Gère les commandes utilisateur lié aux objets User de l'application.
 */
public class UserCommandHandler implements CommandHandler {

    private final AppClient app;

    /**
     * Constructeur
     * @param app application courante.
     */
    public UserCommandHandler(AppClient app){
        this.app = app;
    }
    @Override
    public boolean handleCommand(String[] parts) {
        // Vérifier si la commande est valide
        if ( (parts.length != 1 && parts.length != 2 && parts.length != 3) || !parts[0].equalsIgnoreCase("process")) {
            // La commande n'est pas correcte, ne pas traiter
            return false;
        }

        if(parts.length == 1){
            displayHelp();
            return true;
        } else {
            if(parts.length == 2){
                switch (parts[1]){
                    case "listFile" -> printListFile(true);
                    case "clearFile" -> clearUserFile();
                    default -> {
                        return false;
                    }

                }
            } else {
                switch (parts[1]){
                    case "addFile" -> addUserFile(parts[2]);
                    case "delFile" -> delUserFile(parts[2]);
                    case "showFile" -> showFile(parts[2]);
                    default -> {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    /**
     * Affiche des informations sur un dossier enregistre dans la liste utilisateur.
     * @param part Nom du fichier.
     */
    private void showFile(String part) {
        if(app.getUser().getListFile().contains(new File(part))){
            System.out.println(FileUtils.stringifyDirectory(new File(part)));
        } else {
            System.out.println("Le fichier : "+part+" n'existe pas ou est renseigner différemment.\n Essayer"+Colors.GREEN+"'process listFile'. "+Colors.RESET);
        }
    }

    /**
     * Affiche la liste des dossiers enregistrés.
     * @param full true, affiche toute l'arborescence, false affiche juste les noms de dossier parent.
     */
    private void printListFile(boolean full){
        System.out.println("Liste fichier :");

        if(app.getUser().getListFile().isEmpty()){
            System.out.println(Colors.CYAN+"[vide]"+Colors.RESET);
        } else {
            if(!full){

                System.out.println(Colors.CYAN+app.getUser().getListFile().toString()+Colors.RESET);
            } else {
                for (File file : app.getUser().getListFile()){
                    System.out.println(FileUtils.stringifyDirectory(file));
                }
            }
        }

    }

    /**
     * Supprime un dossier enregistré de la liste.
     * @param part Chemin du dossier.
     */
    private void delUserFile(String part) {
        File file = new File(part);
        Properties prop = getProperties("application");
        if(file.exists()){
            if(app.getUser().getListFile().remove(file)){
                app.getUser().write(prop.getProperty("app.userSerFile"));
                System.out.println("Le fichier a été supprimer de la liste !");
            } else {
                System.out.println("Le chemin spécifié existe mais n'est pas dans la liste");
            }
        } else {
            System.out.println("chemin spécifié introuvable  ");
        }

    }


    /**
     * Ajoute un dossier dans la liste.
     * @param part Chemin du dossier.
     */
    private void addUserFile(String part) {
         File file = new File(part);
         if(file.exists()){
             if(app.getUser().getListFile().contains(file)){
                 System.out.println("Le fichier spécifié existe déja dans la liste");
                 //TODO : Vérifié si le fichier existe aussi dans une autre arborescence plus général
             } else {
                 try {
                     app.getUser().addFileToSave(file);
                     app.getUser().write(getProperties("application").getProperty("app.userSerFile"));
                     //TODO : print l'ensemble des sous-fichier ajouter avec une fonction FileUtils
                     System.out.println("Le fichier : "+Colors.CYAN+file.getName()+Colors.RESET+" a été ajouter a la liste");
                 } catch (FileNotFoundException e){
                     //Normalement déja traité plus haut
                     System.out.println("Fichier introuvable");
                 }
             }
         } else {
             System.out.println("Le chemin spécifié est introuvable");
         }
//         app.getfCheck().check();
    }

    /**
     * Réinitialise la liste de dossier.
     */
    private void clearUserFile() {
        Scanner sc = new Scanner(System.in);
        System.out.println("Cette action " + Colors.ORANGE + "est IRREVERSIBLE ! " + Colors.RESET + "L'ensemble des fichier a garder en sauvegarde sera supprimer");
        String userResp;
        do {
            System.out.print("> Confirmer ? o/n");
            userResp = sc.nextLine().trim().toLowerCase();
        } while (!userResp.equals("o") && !userResp.equals("n"));

        if(userResp.equals("o")){
            app.getUser().clearFileToSave();
            app.getUser().write(getProperties("application").getProperty("app.userSerFile"));
            System.out.println("List de fichier réinitialisé");
        } else {
            System.out.println("Opération annulé");
        }

    }

    /**
     * Affiche la liste des commandes
     */
    @Override
    public void displayHelp() {
        System.out.println("- "+Colors.CYAN+"process"+Colors.RESET+" : Affiche ces informations.");
        System.out.println("- "+Colors.CYAN+"process listFile"+Colors.RESET+" : Détails des fichiers déja enregistrer");
        System.out.println("- "+Colors.CYAN+"process clearFile"+Colors.RESET+" : Vide totalement la liste de fichier");
        System.out.println("- "+ Colors.CYAN+"process showFile filePath"+Colors.RESET+" : Détails sur un fichier");
        System.out.println("- "+Colors.CYAN+"process addFile filePath"+Colors.RESET+" : Ajoute le fichier a la liste de fichier");
        System.out.println("- "+Colors.CYAN+"process delFile filePath"+Colors.RESET+" : Supprime un fichier de la liste de fichier");
    }
    
}
