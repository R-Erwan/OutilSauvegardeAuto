package server;

import java.io.*;

/**
 * Contributeurs : Eric Leclercq, Annabelle Gillet
 */
public class Message implements Serializable {
    public String name;
    public Command command;
    public String params;


    public Message(String name, Command command, String param) {
        this.name = name;
        this.command = command;
        this.params = param;
    }
}