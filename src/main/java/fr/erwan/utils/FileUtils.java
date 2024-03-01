package fr.erwan.utils;

import java.io.*;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Objects;
import java.util.Queue;

/**
 * Class de fonction utilitaire personalisé effectuant des opérations
 * sur les fichiers
 */
public class FileUtils {

    /**
     * Fonction static récursive pour copier des Folder et des Fichiers,
     * la fonction gère l'intégrité de l'arborescence du folder.
     * @param src Fichier ou Dossier source
     * @param dest Dossier de destination
     * @throws IOException
     */
    public static void copyFolder(File src, File dest) throws IOException {
        if(src.isDirectory()){
            if (!dest.exists()){
                dest.mkdirs();
            }
            String[] files = src.list();

            for (String file : files){
                File srcFile = new File(src, file);
                File destFile = new File(dest, file);

                copyFolder(srcFile,destFile);
            }
        }
        else {
            try (InputStream in = new FileInputStream(src);
                 OutputStream out = new FileOutputStream(dest)) {
                byte[] buffer = new byte[1024];
                int len;
                while((len = in.read(buffer)) > 0){
                    out.write(buffer, 0, len);
                }
            }
            System.out.println("Fini de copier le fichier : "+src);
        }
    }

    /**
     * Coupe un dossier contenant plusieurs dossiers en plusieurs dossiers avec
     * uniquement des fichiers.
     * @param src Doit être un Directory
     * @return Liste de dossier
     */
    public static void splitFoldersRec(File src, ArrayList<File> files){
        for(File file : Objects.requireNonNull(src.listFiles())) {
            if(file.isDirectory() && containsFolder(file)){
                splitFoldersRec(file,files);
            } else {
                files.add(file);
            }
        }
    }

    /**
     * Teste si un fichier contient au moin un dossier
     * @param src fichier a tester
     * @return boolean
     */
    private static boolean containsFolder(File src){
        File[] list = src.listFiles();
        for(File file : list){
            if(file.isDirectory()){
                return true;
            }
        }
        return false;
    }
}
