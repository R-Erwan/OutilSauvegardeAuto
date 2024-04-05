package server;

import java.io.*;

public class Message implements Serializable {
    public Command command; // Action à effectuer

    public String[] params; //Paramètre supplémentaire

    /**
     * Constructeur de class
     *
     * @see Command
     * @param command Action
     * @param params Paramètres
     */
    public Message(Command command, String ... params) {
        this.command = command;
        this.params = params;
    }

}