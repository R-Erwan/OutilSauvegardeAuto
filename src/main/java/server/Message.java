package server;

import java.io.*;

/**
 * Contributeurs : Eric Leclercq, Annabelle Gillet
 */
public class Message implements Serializable {
    public String name; //Nom de l'utilisateur emitter de la commande
    public Command command; // Action à effectuer
    public String params; //Paramètre supplémentaire

    /**
     * Constructeur de class
     * @see Command
     * @param name Nom utilisateur
     * @param command Action
     * @param param Paramètres
     */
    public Message(String name, Command command, String param) {
        this.name = name;
        this.command = command;
        this.params = param;
    }
}