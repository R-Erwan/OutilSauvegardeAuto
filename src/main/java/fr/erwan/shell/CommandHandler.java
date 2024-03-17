package fr.erwan.shell;

import fr.erwan.utils.ConstantColors;

public interface CommandHandler extends ConstantColors {
    boolean handleCommand(String[] parts);
    void displayHelp(int n);

}
