package fr.erwan;

import fr.erwan.fileTools.FileChecker;
import fr.erwan.fileTools.FileSaver;
import fr.erwan.fileTools.FileWaitingQueue;
import fr.erwan.users.User;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.ResourceBundle;

/*
TODO/ Système pour répartir les dossier trop volumineux pour un thread ?
TODO/ Sérialisation de la FWQ
TODO/ Review du code + gen javadoc
    */

/**
 * @author Erwan Roussel
 * @version 1.0
 */
public class Main {

    final static String sourceDir = "TestSourceDirectory/";
    final static String destDir = "TestDestDirectory/";

    public static void main(String[] args) {
        simuTest();
    }

    /**
     * Simulation de test
     */
    public static void simuTest(){
        System.out.println("Initialisation simulation\nRécupération des paramètres");

        ResourceBundle resourceBundle = ResourceBundle.getBundle("application");
        String userFile = resourceBundle.getString("application.userSerializeFile");
        String fwqFile = resourceBundle.getString("application.fwqSerializeFile");

        /*
        * ==================== Création / Récupération de l'utilisateur ====================
        * */

        User user;
        try{
            System.out.println("Tentative de récupérer les infos utilisateurs");
            user = User.read(userFile,"Erwan");
            System.out.println("Fichier trouver, utilisateur récupérer");
        } catch (IOException e){
            System.out.println("Pas d'infos utilisateur trouvé");
            System.out.println("Création de l'utilisateur");
            user = new User("Erwan","1234");
            user.write(userFile);
        }

        /*
        * ==================== Ajout des répertoires à sauvegarder ====================
        *  */
        ArrayList<File> files = new ArrayList<File>();
        files.add(new File(sourceDir+"Chocolat") );
        files.add(new File(sourceDir+"TestSplit") ) ;
        files.add(new File(sourceDir+"fichier500Mo.txt") );
        files.add(new File(sourceDir+"fichier1Go.txt") );
        try {
            user.addFileToSave(files);
        } catch (FileNotFoundException e){
            throw new RuntimeException();
        }
        user.write(userFile);
        System.out.println("Ajouts des fichiers et sérialisation de l'utilisateur");
        System.out.println(user);
        System.out.println(user.getListFile());

        /*
        * ==================== Initialisation FWQ, FileChecker et FileSaver ====================
        *  */


        FileWaitingQueue fwq;
        try {
            fwq = FileWaitingQueue.read(fwqFile);
        } catch (IOException e){
            System.out.println("Pas de fwq trouvé");
            fwq = new FileWaitingQueue();
            fwq.write(fwqFile);
        }

        /*
         * ==================== Initialisation FileChecker et FileSaver ====================
         *  */

        FileChecker fCheck = new FileChecker(fwq,user);
        fCheck.start();

        FileSaver fSave1 = new FileSaver(fwq,destDir+user.getName()+"/");
        FileSaver fSave2 = new FileSaver(fwq,destDir+user.getName()+"/");

        fSave1.start();
        fSave2.start();
    }

}