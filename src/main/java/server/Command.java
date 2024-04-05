package server;

import java.io.*;

public enum Command implements Serializable {

    CreateUser("CREATE USER"),
    ConnectUser("LOG USER"),
    SaveFile("SEND FILE"),
    GetDateFile("GET DATE FILE"),
    GetList("GET LIST FILES"),
    GetFile("GET FILE"),
    State("STATE"),
    Error("ERROR"),
    Stop("END");


    public String texte = "";

    private Command(String texte) {
        this.texte = texte;
    }
}