package fr.erwan.shell;

import fr.erwan.AppClient;
import fr.erwan.utils.FileUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Properties;
import java.util.Scanner;

import static fr.erwan.utils.SystemUtils.getAppProperties;

/**
 * Gère les commandes utilisateur lié aux objets User de l'application.
 * Ajouts de fichiers à sauvegarder. 'process addFile pathFile'
 * Suppression de fichiers à sauvegarder. 'process delFile fileName'
 * Vider la liste des fichiers à sauvegarder. 'process clearFile'
 * Liste des fichiers utilisateur. 'process listFile'
 */
public class UserCommandHandler implements CommandHandler {

    private AppClient app;

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
            displayHelp(0);
        } else {
            if(parts.length == 2){
                switch (parts[1]){
                    case "listFile" -> printListFile(true);
                    case "clearFile" -> clearUserFile();
                    default -> {
                        System.out.println("Argument : '"+parts[1]+"' incorrect.");
                        displayHelp(0);
                        return false;
                    }

                }
            } else {
                switch (parts[1]){
                    case "addFile" -> addUserFile(parts[2]);
                    case "delFile" -> delUserFile(parts[2]);
                    case "showFile" -> showFile(parts[2]);
                    default -> {
                        System.out.println("Argument : '"+parts[1]+"' incorrect.");
                        displayHelp(0);
                        return false;
                    }
                }
            }
        }

        return true;
    }

    private void showFile(String part) {
        if(app.getUser().getListFile().contains(new File(part))){
            System.out.println(FileUtils.stringifyDirectory(new File(part)));
        } else {
            System.out.println("Le fichier : "+part+" n'existe pas ou est renseigner differemment.\n Essayer"+GREEN+"'process listFile'. "+RESET);
        }
    }

    private void printListFile(boolean full){
        System.out.println("Liste fichier :");

        if(app.getUser().getListFile().isEmpty()){
            System.out.println(CYAN+"[vide]"+RESET);
        } else {
            if(!full){

                System.out.println(CYAN+app.getUser().getListFile().toString()+RESET);
            } else {
                for (File file : app.getUser().getListFile()){
                    System.out.println(FileUtils.stringifyDirectory(file));
                }
            }
        }

    }

    private void delUserFile(String part) {
        File file = new File(part);
        Properties prop = getAppProperties();
        if(file.exists()){
            if(app.getUser().getListFile().remove(file)){
                app.getUser().write(prop.getProperty("app.userSerializeFile"));
                System.out.println("Le fichier a été supprimer de la liste !");
            } else {
                System.out.println("Le chemin spécifié existe mais n'est pas dans la liste");
            }
        } else {
            System.out.println("chemin spécifié introuvable  ");
        }

    }

    private void addUserFile(String part) {
         File file = new File(part);
         if(file.exists()){
             if(app.getUser().getListFile().contains(file)){
                 System.out.println("Le fichier spécifié existe déja dans la liste");
                 //TODO : Vérifié si le fichier existe aussi dans une autre arborescence plus général
             } else {
                 try {
                     app.getUser().addFileToSave(file);
                     app.getUser().write(getAppProperties().getProperty("app.userSerializeFile"));
                     //TODO : print l'ensemble des sous-fichier ajouter avec une fonction FileUtils
                     System.out.println("Le fichier : "+CYAN+file.getName()+RESET+" a été ajouter a la liste");
                 } catch (FileNotFoundException e){
                     //Normalement déja traité plus haut
                     System.out.println("Fichier introuvable");
                 }
             }
         } else {
             System.out.println("Le chemin spécifié est introuvable");
         }
         app.getfCheck().check();
    }

    private void clearUserFile() {
        Scanner sc = new Scanner(System.in);
        System.out.println("Cette action " + ORANGE + "est IRREVERSIBLE ! " + RESET + "L'ensemble des fichier a garder en sauvegarde sera supprimer");
        String userResp;
        do {
            System.out.print("> Confirmer ? o/n");
            userResp = sc.nextLine().trim().toLowerCase();
        } while (!userResp.equals("o") && !userResp.equals("n"));

        if(userResp.equals("o")){
            app.getUser().clearFileToSave();
            app.getUser().write(getAppProperties().getProperty("app.userSerializeFile"));
            System.out.println("List de fichier réinitialisé");
        } else {
            System.out.println("Opération annulé");
        }

    }

    @Override
    public void displayHelp(int n) {
        System.out.println("- "+CYAN+"process"+RESET+" : Affiche ces informations.");
        System.out.println("- "+CYAN+"process listFile"+RESET+" : Détails des fichiers déja enregistrer");
        System.out.println("- "+CYAN+"process clearFile"+RESET+" : Vide totalement la liste de fichier");
        System.out.println("- "+CYAN+"process addFile filePath"+RESET+" : Ajoute le fichier a la liste de fichier");
        System.out.println("- "+CYAN+"process delFile filePath"+RESET+" : Supprime un fichier de la liste de fichier");
        System.out.println("- "+CYAN+"process showFile filePath"+RESET+" : Détails sur un fichier");
    }
    
}
