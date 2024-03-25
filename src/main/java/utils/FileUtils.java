package utils;

import java.io.*;
import java.util.AbstractList;
import java.util.ArrayList;
import java.util.List;
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
                byte[] buffer = new byte[8192];
                int len;
                while((len = in.read(buffer)) > 0){
                    out.write(buffer, 0, len);
                }
            }
        }
    }

    /**
     * Convertit une structure de répertoire en une chaîne de caractères formatée.
     * Chaque dossier est préfixé d'un signe [+] et affiché en vert, et chaque fichier est préfixé d'une barre verticale et affiché en blanc.
     * @param file Le fichier représentant le répertoire racine.
     * @return Une chaîne de caractères représentant la structure du répertoire.
     */
    public static String stringifyDirectory(File file) {
        StringBuilder stringBuilder = new StringBuilder();
        stringifyDirectoryHelper(file, stringBuilder, 0, true);
        return stringBuilder.toString();
    }

    /**
     * Méthode auxiliaire pour convertir la structure de répertoire en une chaîne de caractères formatée.
     * @param file Le fichier ou répertoire actuellement traité.
     * @param stringBuilder Le StringBuilder pour construire la chaîne de caractères.
     * @param depth La profondeur du fichier ou répertoire dans la structure du répertoire.
     * @param isFirst Indique si le répertoire est le premier dans son niveau de profondeur.
     */
    private static void stringifyDirectoryHelper(File file, StringBuilder stringBuilder, int depth, boolean isFirst) {
        if (file.isDirectory()) {
            if (isFirst) {
                stringBuilder.append("\u001B[32m"); // Green color ANSI escape code
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

    /**
     * Retourne une chaîne de caractères représentant l'indentation pour un niveau de profondeur donné.
     * @param depth Le niveau de profondeur dans la structure du répertoire.
     * @return Une chaîne de caractères représentant l'indentation correspondante.
     */
    private static String getIndent(int depth) {
        StringBuilder indent = new StringBuilder();
        for (int i = 0; i < depth; i++) {
            indent.append("    ");
        }
        return indent.toString();
    }



}
