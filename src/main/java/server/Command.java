package server;

import java.io.*;

/**
 * Contributeurs : Eric Leclercq, Annabelle Gillet
 */
public enum Command implements Serializable {

    CreateUser("CREATE USER"),
    ConnectUser("CONNECT USER"),
    SaveFile("SAVE FILE"),
    GetInfoFile("GET INFO FILE"),
    ListFiles("LIST FILES"),
    DownloadFile("DOWNLOAD FILE"),
    Stop("END");


    public String texte = "";

    private Command(String texte) {
        this.texte = texte;
    }
}