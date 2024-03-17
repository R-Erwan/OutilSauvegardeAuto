# Projet Java de Sauvegarde de Fichiers

Ce projet Java vise à mettre en œuvre un système de sauvegarde de fichiers à l'aide de threads et de structures de données de base.

Ce projet s'inscrit dans le cadre de l'enseignement de l'UE Info4B **Principe des système d'exploitation** en licence 2.

## Fonctionnalités principales

- Gestion des utilisateurs : création, lecture et écriture des informations utilisateur.
- File d'attente de fichiers à sauvegarder (FWQ) : une file d'attente synchronisée pour stocker les fichiers en attente de sauvegarde.
- Threads pour la vérification des fichiers à sauvegarder et pour la sauvegarde effective des fichiers.
- Utilitaires de manipulation de fichiers : copie de dossiers et découpage de dossiers volumineux.
- NEW Ajout du shell et des commandes principales


## Structure du Projet

Le projet est structuré en plusieurs packages :

- `fr.erwan` : contient la classe principale `Main` et les classes principales pour l'exécution du programme.
- `fr.erwan.fileTools` : contient les classes pour la gestion des fichiers, y compris la FWQ, les vérifications et les sauvegardes de fichiers.
- `fr.erwan.users` : contient les classes pour la gestion des utilisateurs et de leurs fichiers à sauvegarder.
- `fr.erwan.utils` : contient des classes utilitaires pour la manipulation de fichiers.
- `fr.erwan.shell` : contient des classes pour gérer les interactions client (shell)

# **Les class du projet**

## **Partie AppClient et gestion des fichiers**

Ensemble des modules lié a la gestion des fichiers.

### Class **AppClient** NEW

Class principale de l'application client. Gère l'initialisation et le démarrage de l'application.
Initialise tout modules de l'application.

- `startApp()` Démarre les threads de sauvegardes et lance un *check*.
- `stopApp()` Arrête l'application arrête tout les threads de sauvegarde et serialise la FWQ en l'état.

### Class **FileSaver**

**Threads** pour la sauvegarde effective des fichier dans le dossier `TestSourceDirectory`.

- Sauvegarde les fichiers de la FWQ, et procède a l'archivage des fichiers déja présenr.

### Class **FileWaitingQueue** UPDATE

- Implémente une *LinkedListWithBackup* de Fichier.

- Ressources concurrente, qui représente une file d'attente de fichiers a sauvegarder synchronisée (FWQ) pour stocker les fichiers a écrire pour la sauvegarde.

- **Sérialisation** pour sauvegarder la file d'attente la ou elle en était pour reprendre la sauvegarde en cas de déconnexion / redémarage.

#### Class **LinkedListWithBackup** NEW

Structure de donnée permettant de gérer une file d'attente avec validation du traitement.
- Queue synchronisée et thread-safe, qui permet a des threads de récupérer une donnée de la *queue principale* et la place dans une *backupQueue*.
- Lorsqu'un thread a finis sont traitement il supprime la donnée de la *bakcupQueue*.

### Class **FileChecker**

Classqui gère les fichiers a sauvegarder.
- En consultants les infos des fichiers utilisateur
- Compare les dates des fichiers utilisateurs avec celles des fichiers sauvegarder et la fréquence de sauvegarde.
- Ajoute des fichier a la FWQ.

### Class **FileUtils**

Implémente des fonctions statics utilitaires sur les fichiers.

- `copyFolder` pour copier des dossiers et des fichiers et conserver l'arboressence du dossier donné.

- `splitFolder` Coupe un dossier en plusieurs dossier ne contenant que des dossier ne contenant que des fichiers.

- `containsFolder` Renvoie un booléens vérifiant l'éxistence d'au moin un dossier dans un dossier.

### Class SystemUtils

Implémente des fonctions pour gérer le fichier de propriété : **application.properties**.

### Class **User**

Gestion d'utilisateurs

- Gère des infos utilisateurs (nom, mdp) et liste de fichiers.
- **Sérialisation** des infos utilisateurs sur le poste du client.
- Ajout de fichiers a la liste.

## **Partie shell NEW**

Ensemble des modules qui s'occupent des intéractions avec l'utilisateur au travers de la console.

### Class **ShellClient**

**Thread** qui représente le shell.
- Initialise les gestionnaires de commandes disponible (**CommandHandler**).
- **run()** Ecoute les entrée utilisateur et appelles les gestionnaires de commandes correspondant.

### Interface **CommandHandler**
Interface implémenter par tout les CommandHandler spécifique.
- `boolean handleCommand(String[] parts)` méthode appeler par le shellClient.
- `void displayHelp(int n)` méthode qui affiche les commandes d'aide lié au gestionnaire de commande spécifique.

### List des gestionnaires de commandes spécifiques :
- **AppCommandHandler** ->  commandes lié aux démarage et l'arrret de l'application.
    - **app stop**
    - **app start**
    - **app check**
- **ConfigCommandHandler** -> commandes lié a la gestion des paramètres de l'application.
    - **config info**
    - **config update key val**
- **HelpCommandHandler** -> commandes d'aides.
    - **help**
- **UserCommandHandler** -> commandes lié aux interaction avec les donné utilisateurs, principalement les fichiers.
    - **process listFile**
    - **process cleaFile**
    - **process addFile pathFile**
    - **process delFile pathFile**
- **DevCommandHandler** -> commandes utiles pour tester l'application en phase de test.
    - **dev stopSave**
    - **dev serFwq**
    - **dev showFwq**
## Partie **Main**

### Class **Main** UPDATE

#### Fonction main :
```
  ResourceBundle resourceBundle;
        try {
            resourceBundle = ResourceBundle.getBundle("application");
        } catch (MissingResourceException e){
            System.err.println("Le fichier de configuration est manquant");
            throw new RuntimeException();
        }
        boolean isFirstLaunch = Boolean.parseBoolean(resourceBundle.getString("app.firstLaunch"));

        if (isFirstLaunch) {
            firstLaunch();
        }
        AppClient app = init();
        app.startApp();

        ShellClient shellC = new ShellClient(app);
        shellC.start();
```

Au lancement de l'application, la fonction main regarde si c'est le premier lancement et appelle alors la fonction **firstLaunch** si besoin.
Lance l'**application** et le **shell**.


- `public static void firstLaunch(){ ... } ` Amorce le 1er démarage de l'application en invitant l'utilisateur à créer une utilisateur et en entrant les premiers paramètres.

- `public static void init(){ ... } ` Initialise l'application a chaque démarage à l'aide des properties.
    - Lit un **User** a partir du *.ser* .
    - Récupère la **FWQ**, la file d'attente des fichier a sauvegarder.
    - Instancie l'**AppClient** avec un nombre de **FileThreads**.

## Les **properties**
```
app.saveFrequency=24 //-> frequence de sauvegarde
app.fwqSerializeFile= //-> chemin du fichier de serialisation de la FWQ.
app.userSerializeFile= //-> paraille pour le fichier utilisateur
app.firstLaunch=true //-> boolean du 1er lancement 
app.nbFileSaver= //-> nombre de threads de sauvegarde
app.destFile =TestDestDirectory/ //-> simulation d'un stockage distant
app.srcFile =TestSourceDirectory/ //-> simulation stockage client
app.userName= //-> nom d'utilisateur courant
```

## Comment exécuter le programme

1. Assurez-vous d'avoir Java installé sur votre système.
2. Clonez ce dépôt sur votre machine dans un répertoire.
3. Compilez le code source depuis le répertoire :
```bash
javac -d classes src/main/java/fr/erwan/*.java src/main/java/fr/erwan/fileTools/*.java src/main/java/fr/erwan/users/*.java src/main/java/fr/erwan/utils/*.java
```
4. Copier le fichier _.properties_ :
- **windows**
```bash
copy .\src\main\resources\application.properties classes
```
- **linux**
```bash
copy cp src/main/resources/application.properties classes
```
5. Créer deux répertoire `TestSourceDirectory` et
   `TestDestDirectory` pour tester les Fonctionnalités.
- Le dossier _Source_ doit contenir des fichiers a copié
- Le dossier _Dest_contiendra les fichier sauvegardé
6. Exécutez la classe `Main` pour lancer le programme.
```bash
java -cp classes fr.erwan.Main
```
