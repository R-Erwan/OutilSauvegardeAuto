package fr.erwan.shell;

import fr.erwan.AppClient;
import fr.erwan.utils.ConstantColors;

import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
/**
 * Représente le shell de l'application client.
 */


public class ShellClient extends Thread implements ConstantColors {
    private AppClient app;
    private Scanner scanner;
    private Map<String, CommandHandler> commandHandlers;

    /**
     * Constructeur de la classe ShellClient.
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
        commandHandlers.put("dev",new DevCommandHandler(this.app));
    }

    /**
     * Méthode exécutée lorsque le thread ShellClient démarre.
     */
    public void run(){
        System.out.println(YELLOW + "Bienvenue dans le shell de la l'application"+RESET);
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
