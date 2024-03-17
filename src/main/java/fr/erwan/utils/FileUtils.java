package fr.erwan.utils;

import java.io.*;
import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Objects;
/*
* TODO/ Fonction pour découper un dossier volumineux en plusieurs fichier
* */

/**
 * Class de fonction utilitaire personalisé effectuant des opérations sur les fichiers
 */
public class FileUtils {

    /**
     * Fonction récursive pour copier des dossiers et des fichiers,
     * la fonction conserve l'intégrité de l'arborescence du dossier copié.
     * @param src fichier source client
     * @param dest fichier de destination
     * @throws IOException problème de chemin de fichier, ou problème lors de l'écriture
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
            System.out.println("Fini de copier le fichier : "+src+" -> dans :"+dest);
        }
    }

    /**
     * Coupe un dossier contenant plusieurs dossiers en plusieurs dossiers avec
     * uniquement des fichiers.
     * @param src Doit être un dossier
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
     * Divise un dossier en liste de fichiers
     * @param srcFolder fichier source client
     */
    public static void splitToFiles(File srcFolder,ArrayList<File> listFiles){
        if(srcFolder.list() != null) {
            for(File file : Objects.requireNonNull(srcFolder.listFiles())){
                if(file.isDirectory()){
                    splitToFiles(file,listFiles);
                } else {
                    listFiles.add(file);
                }
            }
        }
    }

    /**
     * Teste si un fichier contient au moin un dossier
     * @param src fichier a tester
     * @return true si le dossier contient au moin un dossier false sinon
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

    /*
        Affichage dossier en console
     */

    public static String stringifyDirectory(File file) {
        StringBuilder stringBuilder = new StringBuilder();
        stringifyDirectoryHelper(file, stringBuilder, 0, true);
        return stringBuilder.toString();
    }

    private static void stringifyDirectoryHelper(File file, StringBuilder stringBuilder, int depth, boolean isFirst) {
        if (file.isDirectory()) {
            if (isFirst) {
                stringBuilder.append("\u001B[32m"); // Green color ANSI escape code
                isFirst = false;
            }
            stringBuilder.append(getIndent(depth)).append("[+] ").append(file.getName()).append("\u001B[0m\n"); // Reset color
            File[] files = file.listFiles();
            if (files != null) {
                for (File f : files) {
                    stringifyDirectoryHelper(f, stringBuilder, depth + 1, false);
                }
            }
        } else {
            stringBuilder.append(getIndent(depth)).append("    ").append("|-- ").append(file.getName()).append("\n");
        }
    }

    private static String getIndent(int depth) {
        StringBuilder indent = new StringBuilder();
        for (int i = 0; i < depth; i++) {
            indent.append("    ");
        }
        return indent.toString();
    }

}
