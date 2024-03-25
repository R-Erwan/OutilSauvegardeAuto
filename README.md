
# Projet Java de Sauvegarde de Fichiers

Ce projet Java vise à mettre en œuvre un système de sauvegarde de fichiers à l'aide de threads et de structures de données de base.

Ce projet s'inscrit dans le cadre de l'enseignement de l'UE Info4B **Principe des système d'exploitation** en licence 2.

## Fonctionnalités principales

### Client : 
- Programme client qui permet a l'utilisateur de créer un utilisateur.
- Ajouter des répertoires a sauvegarder.
- Sauvegarder automatiquement les dossiers/fichier qu'il aura spécifié.
- Spécifié un intervalle de sauvegarde.

Au démarrage,  
Le programme client explore les dossiers renseigné.  
Envoie des requêtes au serveur pour obtenir la date des fichiers sauvegardé.  
Ajoute les fichiers a sauvegardé dans une file d'attente.  
Un thread envoie ces fichiers au serveur. 


### Shell Utilisateur
Thread  
Interface qui permet à l'utilisateur d'interagir avec l'application cliente  
et le serveur en saisissant des commandes.

### Server : 
- Programme server qui acceptent plusieurs connections de client.
- Gère les communications avec le client et effectue les opérations demandées.
- Authentification / Reception de fichier / Envoie d'informations.


## Packages

Le projet est structuré en plusieurs packages :

- `client` : contient la classe principale `ProgrammeClient` et les classes principales pour l'exécution du programme client.
- `client.fileTools` : contient les classes pour lancer une application, toutes les classes lié a la gestion des fichiers (thread de sauvegarde, file d'attente, checker) et a la communication avec le serveur.
- `client.users` : contient les classes pour la gestion d'utilisateurs.
- `client.shell` : contient des classes pour gérer les interactions client (shell)
- `server` : Programme server avec la classe principale `Server` et des classes relatives au protocole de communication, aux threads client, au système de logs.

# **Les class du projet**

## **Partie AppClient et gestion des fichiers**

Ensemble des modules lié a la gestion des fichiers.

### Class **AppClient** NEW

Class principale de l'application client. Gère l'initialisation et le démarrage de l'application.
Initialise tout les modules de l'application.

- `startApp()` Démarre les threads de sauvegardes, initialise la connection au serveur et lance un *check*.
- `stopApp()` Arrête l'application arrête tout les threads de sauvegarde et serialise la FWQ en l'état, déconnecte du serveur.
- `public static void createFirstApp{ ... } ` Amorce le 1er démarage de l'application en invitant l'utilisateur à créer une utilisateur et en entrant les premiers paramètres.

- `public static AppClient init(){ ... } ` Initialise l'application client en récupérant les configurations et les données utilisateur..

### Class **FileSaver**

**Threads** pour envoyé les fichiers au  `Server`.

- Récupère des fichiers depuis la file d'attente et les envoie au serveur.

### Class **FileWaitingQueue** UPDATE
**`FWQ`**

- Implémente une *LinkedListWithBackup* de Fichier.

- Ressources concurrente, qui représente une file d'attente de fichiers a sauvegarder synchronisée (FWQ) pour stocker les fichiers a écrire pour la sauvegarde.

- **Sérialisation** pour sauvegarder la file d'attente la ou elle en était pour reprendre la sauvegarde en cas de déconnexion / redémarage.

#### Class **LinkedListWithBackup** NEW

Structure de donnée permettant de gérer une file d'attente avec validation du traitement.
- Queue synchronisée et thread-safe, qui permet a des threads de récupérer une donnée de la *queue principale* et la place dans une *backupQueue*.
- Lorsqu'un thread a finis sont traitement il supprime la donnée de la *bakcupQueue*.

### Class **FileChecker**

Class qui explore les répertoires utilisateurs
- Explore les fichiers utilisateurs.
- Interogge le **server** la date des fichier déja sauvegardé.
- Compare les dates des fichiers avec la fréquence de sauvegarde.
- Ajoute des fichier a la **FWQ**.


### Class **SocketConnection**
Class qui gère les accès au socket entre le client et le server.
- Ouvre le socket avec le serveur
- Authentifie l'utilisateur
- Propose des méthodes pour communiqué de façon `synchronized` avec le socket.
- `sendFile` , `getFileInfo`, `getFileList`, `downloadFile`.


### Class **FileUtils**

Implémente des fonctions statics utilitaires sur les fichiers.

- `copyFolder` pour copier des dossiers et des fichiers et conserver l'arboressence du dossier donné.

- `splitFolder` Coupe un dossier en plusieurs dossier ne contenant que des dossier ne contenant que des fichiers.

- `containsFolder` Renvoie un booléens vérifiant l'éxistence d'au moin un dossier dans un dossier.

- `stringifyDirectory` Convertit une structure de répertoire en une chaîne de caractères formatée.

### Class **SystemUtils**

Implémente des fonctions pour gérer les fichiers de properties :
- `application.properties` et `server.properties`. 

### Class **User** et **UserClient**

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
- **ServerCommandHandler** -> commandes utiles pour tester l'application en phase de test.
    - **server listFile**


### Class **ProgrammeClient** UPDATE

#### Fonction main :
```
    public static void main(String[] args) throws Exception {

        // Récupère les paramètres de l'application
        Properties prop = getProperties("application");
        boolean isFirstLaunch = Boolean.parseBoolean(prop.getProperty("app.firstLaunch"));

        //Si c'est le premier lancement, lance le firstLaunch
        if (isFirstLaunch) {
            AppClient.createFirstApp();
        }
        //Initialise l'application
        AppClient app = AppClient.InitApp();
        
        app.startApp();
        

        //Lance le shell
        ShellClient shellC = new ShellClient(app);
        shellC.start();

    }
```

Au lancement de l'application, la fonction main regarde si c'est le premier lancement et appelle alors la fonction **createFisrtApp** si besoin.
Lance l'**application** et le **shell**.

## Partie **Serveur**

### Class **Server**
Représente le programme principal du serveur qui écoute les connexions entrantes des clients.
- `initFileSystem` -> Mkdirs l'architecture des dossiers du serveur.

### Class **Client**
Représente un client connecté au server. Gère les communications avec un client et effectue les opérations.

### Enum **Command**
Enumération des constantes des commandes possibles pour communiquer via des objets **message**.

### Class **Message**
Objets serializable qui permettent des echanges client-server  
`Name - Command - Params`

### Class **LogHandler**
Gère l'enregistrement de toutes les requetes de message et connection/déconnexion passant sur le server, dans un fichier logs du jour.

## Les **properties**
```
app.freq=0 -> frequence de sauvegarde.
app.fwqSerFile=SerFiles/ -> Dossier de serialisation de la File d'attente.
app.userSerFile=SerFiles/ -> Idem pour les donnée utilisateurs
app.firstLaunch=true -> Boolean de 1er démarage de l'application
app.userName= -> Nom de l'utilisateur courant
app.host=localhost -> nom d'hote du server
app.port=8080 -> port de connexion au server
```
``` 
server.port=8080 -> Port d'écoute
server.maxClient=10 -> Nombre max de client simultané
server.serUser=Server/Properties/UserProperties/ -> Dossier infos utilisateur (nom et mdp).
server.usersData=Server/Data/ -> Data des utilisateurs.
server.logs=Server/Properties/Logs/ -> Logs
```
## Comment exécuter le programme

1. Assurez-vous d'avoir Java installé sur votre système.

2. Clonez ce dépôt sur votre machine dans un répertoire.
3. Compilez le code source depuis le répertoire :

```bash
javac -d classes src/main/java/client/*.java src/main/java/client/fileTools/*.java src/main/java/client/shell/*.java src/main/java/client/users/*.java src/main/java/utils/*.java src/main/java/server/*.java
```
Un dossier `classes` est créer avec tout les binaires.

4. Copier les fichier _.properties_ :
- **windows**
```bash
copy .\src\main\resources\application.properties classes
copy .\src\main\resources\server.properties classes
```
- **linux**
```bash
cp src/main/resources/application.properties classes
cp src/main/resources/server.properties classes
```

5. Lancer le serveur : 
```bash
java -cp classes server.Server
```
Le programme va créer l'architecture de dossier :
```
[+]Server
    [+]Data
        |--
        |--
    [+]Properties
        [+]Logs
            |--
        [+]UserProperties
            |--

```

6. Lancer un client :  
```bash
java -cp classes client.ProgrammeClient
``` 
Au premier lancement l'utilisateur est invité a créer un premier compte :
![Capture d'écran 2024-03-25 203605](https://github.com/R-Erwan/OutilSauvegardeAuto/assets/115042801/cf541150-9758-4e40-b752-2309d012c0a1)

Puis l'application s'initialise :  
![Capture d'écran 2024-03-25 203924](https://github.com/R-Erwan/OutilSauvegardeAuto/assets/115042801/eb0380e5-5339-420b-9e87-d908dab901ee)

Coté serveur, on remarque la connection :
![image](https://github.com/R-Erwan/OutilSauvegardeAuto/assets/115042801/9096aba4-0e46-47dc-af30-f32e9e14030e)

L'application est prête.

# Exemple : 
Un exemple de simulation pour tester les fonctionnalité :  
Pour créer des fichiers de tailles fixe :
- linux `dd if=dev/zero of=fichier500Mo bs=100M count=100`
Créer un fichier de 500Mo.








