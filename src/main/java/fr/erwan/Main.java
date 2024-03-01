package fr.erwan;

import fr.erwan.file.system.tools.FileChecker;
import fr.erwan.file.system.tools.FileSaver;
import fr.erwan.file.system.tools.FileWaitingQueue;
import fr.erwan.users.User;

import java.io.File;
import java.io.FileNotFoundException;


public class Main {

    final static String sourceDir = "TestSourceDirectory/";
    final static String destDir = "TestDestDirectory/";

    public static void main(String[] args) {
//        User userCourant = new User("Erwan","1234");
//        userCourant.write("");

        User userCourant = User.read("","Erwan");

//        try {
//            userCourant.addFileToSave(new File(sourceDir+"chocolat"));
//            userCourant.addFileToSave(new File(sourceDir+"fichier500Mo.txt"));
//            userCourant.write("");
//        } catch (FileNotFoundException e){
//            e.printStackTrace();
//        }
        try {
            userCourant.addFileToSave(new File("C:/Users/erwan/OneDrive - Université de Bourgogne/Fac/L2/S4/Info4B/Ancien CC"));
        } catch (FileNotFoundException e){
            e.printStackTrace();
        }
        System.out.println(userCourant.getListFile());
        System.out.println(userCourant);

        FileWaitingQueue fwq = new FileWaitingQueue();

        FileChecker fileCheck = new FileChecker(fwq,userCourant);
        fileCheck.start();


        FileSaver fileSaver = new FileSaver(fwq,destDir);
        fileSaver.start();

    }

}