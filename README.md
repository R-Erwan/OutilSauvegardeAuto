# Projet Java de Sauvegarde de Fichiers

Ce projet Java vise à mettre en œuvre un système de sauvegarde de fichiers à l'aide de threads et de structures de données de base.

Ce projet s'inscrit dans le cadre de l'enseignement de l'UE Info4B **Principe des système d'exploitation** en licence 2.

## Fonctionnalités principales

- Gestion des utilisateurs : création, lecture et écriture des informations utilisateur.
- File d'attente de fichiers à sauvegarder (FWQ) : une file d'attente synchronisée pour stocker les fichiers en attente de sauvegarde.
- Threads pour la vérification des fichiers à sauvegarder et pour la sauvegarde effective des fichiers.
- Utilitaires de manipulation de fichiers : copie de dossiers et découpage de dossiers volumineux.

## Structure du Projet

Le projet est structuré en plusieurs packages :

- `fr.erwan` : contient la classe principale `Main` et les classes principales pour l'exécution du programme.
- `fr.erwan.fileTools` : contient les classes pour la gestion des fichiers, y compris la FWQ, les vérifications et les sauvegardes de fichiers.
- `fr.erwan.users` : contient les classes pour la gestion des utilisateurs et de leurs fichiers à sauvegarder.
- `fr.erwan.utils` : contient des classes utilitaires pour la manipulation de fichiers.

## Les class du projet

### Class **FileSaver**

**Threads** pour la sauvegarde effective des fichier dans le dossier `TestSourceDirectory`.

- Sauvegarde les fichiers de la FWQ, et procède a l'archivage des fichiers déja présenr.

### Class **FileWaitingQueue**

- Ressources concurrente, qui représente une file d'attente de fichiers a sauvegarder synchronisée (FWQ) pour stocker les fichiers a écrire pour la sauvegarde.

- **Sérialisation** pour sauvegarder la file d'attente la ou elle en était pour reprendre la sauvegarde en cas de déconnexion / redémarage. 

### Class **FileChecker**

**Thread** qui gère les fichiers a sauvegarder.
- En consultants les infos des fichiers utilisateur
- Compare les dates des fichiers utilsiateurs avec celles des fichiers sauvegarder
- Ajoute des fichier a la FWQ.

### Class **User**

Gestion d'utilisateurs

- Gère des infos utilisateurs (nom, mdp) et liste de fichiers.
- **Sérialisation** des infos utilisateurs sur le poste du client.
- Ajout de fichiers a la liste.

### Class **FileUtils**

Implémente des fonctions statics utilitaires sur les fichiers.

- `copyFolder` pour copier des dossiers et des fichiers et conserver l'arboressence du dossier donné.

- `splitFolder` Coupe un dossier en plusieurs dossier ne contenant que des dossier ne contenant que des fichiers.

- `containsFolder` Renvoie un booléens vérifiant l'éxistence d'au moin un dossier dans un dossier.

### Class **Main**

Simule le fonctionnement du programme.

- ` final static String sourceDir = "TestSourceDirectory/"; `
- ` final static String destDir = "TestDestDirectory/"; `
Simule deux système de stockage Client et Serveur

- `public static void simuTest(){ ... } `

Simule le fonctionnement du programme :
- Récupère des propriété d'un fichier application.properties en utilisant `RessourceBundle` en java.
- Créer / Récupère un utilisateur nommé _Erwan_.
- Ajoute des répertoires a sauvegarder et sérialise l'utilisateur.
- Initialise une **FWQ**.
- Initialise deux **FileSaver**.



## Comment exécuter le programme

1. Assurez-vous d'avoir Java installé sur votre système.
2. Clonez ce dépôt sur votre machine.
3. Compilez le code source à l'aide de votre IDE préféré ou en utilisant la commande `javac`.
4. Créer deux répertoire `TestSourceDirectory` et `TestDestDirectory` pour tester les Fonctionnalités.
5. Exécutez la classe `Main` pour lancer le programme.

```bash
$ java fr.erwan.Main
