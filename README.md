
# Projet outil de sauvegarde automatique

Projet dans le cadre de l'UE Principe des systèmes d'exploitation.


# Installation

## Comment exécuter les jar

1. Assurez-vous d'avoir Java installé sur votre système.

2. Clonez le dépôt [Executable](Executable) sur votre machine

3. Ouvrez deux terminal, un pour le serveur et un pour le client.

4. Exécuter les .jar

```bash
java -jar ServerSauvegardeAuto.jar
```
```bash
java -jar ClientSauvegardeAuto.jar
```

5. Initialisé l'application cliente : 
```java
Configuration de l'application
Création d'un nouvel utilisateur
> nom : toto
> mot de passe :0000
Valider vous ces informations : toto -> 0000 ?
> O/N : o
Configurations :
> Fréquence de sauvegarde (jours): 0
> Valider ces paramètres ? (O/N):o
==========Initialisation de l'application==========
Informations utilisateurs récupéré
Bienvenue toto
SERVER : réponse suite a l'authentification Informations utilisateur incorrecte
SERVER : réponse suite a l'authentification CREATE USER
Pas de file d'attente précédemment sauvegardé
Tout c'est bien passé, application prête
===================================================
Lancement de l'app Client
FileSaver- : en attente sur la FWQ
FileChecker-Explore les fichiers utilisateur...
Bienvenue dans le shell de la l'application
>
```

L'application est prête a être utilisé. Les programmes client et serveur sont installer.

## Comment compiler les codes sources :

1. Assurez-vous d'avoir Java installé sur votre système.

2. Clonez les dépôt [Sources](Sources) sur votre machine

3. Compiler les programmes : 
Depuis le dossier ServerSrc
```bash
javac -d classes src/*.java src/server/*.java src/serverProtocol/*.java src/utils/*.java
```

Depuis le dossier ClientSrc
```bash
javac -d classes src/*.java src/client/interfaceUtilisateur/*.java src/client/model/*.java src/client/operator/*.java src/client/*.java src/serverProtocol/*.java src/utils/*.java
```
4. Coté serveur penser a copié le fichier de properties
```bash
cp src/server.properties classes
```

5. Lancer les programmes : 
```bash
java -cp classes Main
```

## Command utile pour faire des tests :
```bash
dd if=dev/zero of=fichier500Mo bs=100M count=10

mkdir -p Test/Split/Split1 && touch Test/Split/Split1/{t1.txt,t2.txt,t3.txt}
mkdir -p Test/Split/Split2 && touch Test/Split/Split2/{t1.txt,t2.txt,t3.txt}
```

## Documentation des commandes utilisateurs : 
**help** -> Afficher les commandes d'aides.  

**config info** -> Affiche les configurations de l'application.  
**config update key val** -> Modifie une configuration.  

**app stop** -> Arrête l'application.
**app check** -> Lance un check des fichiers a sauvegardé.  

**process listFile** -> Détails des fichiers répertorié a sauvegardé.  
**process clearFile** -> Rénitialise (vide) la liste de fichier.  
**process addFile filePath** -> Ajoute un fichier ou un répertoire a la liste de fichier.  
**process delFile filePath** -> Supprime un fichier ou un répertoire de la liste de fichier.  
**process showFile** -> Affiche des détails sur un répertoire de la liste.  

**dev showFwq** -> Affiche l'état de la file d'attente de fichier.  
**dev pauseSave nbPause** -> Met en pause le thread de sauvegarde pendant 'nbPause' milisecondes.  

**server listFile** -> Affiche la liste de tout les fichiers personnel présent sur le serveur.  
**server download filePath copyFolderPath** -> Télécharge un fichier sur le serveur et le copy dans un repertoire.  
