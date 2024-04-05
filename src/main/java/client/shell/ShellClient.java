package client.shell;

import utils.Colors;
import client.fileTools.AppClient;

import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
/**
 * Représente le shell de l'application client.
 * Ce shell permet à l'utilisateur d'interagir avec l'application en saisissant des commandes.
 */
public class ShellClient extends Thread{
    private final AppClient app;
    private final Scanner scanner;
    private final Map<String, CommandHandler> commandHandlers;

    /**
     * Constructeur de la classe ShellClient.
     *
     * @param app L'instance de l'application client.
     */
    public ShellClient(AppClient app){
        this.app=app;
        this.scanner = new Scanner(System.in);
        this.commandHandlers = new HashMap<>();
        this.setName("ShellClient");
        initializeCommandHandlers();
    }

    /**
     * Initialise les gestionnaires de commandes disponibles dans le shell.
     */
    private void initializeCommandHandlers(){
        commandHandlers.put("help",new HelpCommandHandler());
        commandHandlers.put("config",new ConfigCommandHandler());
        commandHandlers.put("app",new AppCommandHandler(this.app));
        commandHandlers.put("process", new UserCommandHandler(this.app));
        commandHandlers.put("server", new ServerCommandHandler(this.app));
        commandHandlers.put("dev",new DevCommandHandler(this.app));
    }

    /**
     * Méthode exécutée lorsque le thread ShellClient démarre.
     * Cette méthode gère la boucle principale du shell, en attendant les commandes de l'utilisateur.
     */
    public void run(){
        System.out.println(Colors.YELLOW + "Bienvenue dans le shell de la l'application"+Colors.RESET);
        boolean running = true;
        while(running){
            System.out.print("> ");
            String input = scanner.nextLine().trim();

            String[] parts = input.split("\\s+");
            String command = parts[0].toLowerCase();

            CommandHandler handler = commandHandlers.get(command);
            if(handler != null){
                boolean commandOut = handler.handleCommand(parts);
                if(!commandOut){
                    handler.displayHelp();
                    System.out.println("Arguments incorrect. Tapez 'help' pour afficher la liste des commandes disponible");
                }
            } else {
                System.out.println("Commande inconnue. Tapez 'help' pour afficher la liste des commandes disponibles");
            }

            if(input.equals("app stop")){
                running = false;
                System.out.println("Arrêt du shell correctement effectué");
            }
        }
    }

}
