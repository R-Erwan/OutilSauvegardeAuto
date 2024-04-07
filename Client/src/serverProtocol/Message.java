package serverProtocol;

import java.io.Serial;
import java.io.Serializable;

public class Message implements Serializable {
    @Serial
    private static final long serialVersionUID = -1561957147213392842L;
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