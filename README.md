# Automatic Backup Tool

> Academic project developed as part of the **Operating Systems Principles** course unit.

A client-server backup application written in Java that automates file monitoring and synchronization between a local client and a remote server.

---

## Table of Contents

- [Requirements](#requirements)
- [Getting Started](#getting-started)
  - [Running the Pre-built JARs](#running-the-pre-built-jars)
  - [Building from Source](#building-from-source)
- [Usage](#usage)
  - [Initial Setup](#initial-setup)
  - [Shell Commands](#shell-commands)
- [Testing Utilities](#testing-utilities)

---

## Requirements

- Java JDK 8 or higher

---

## Getting Started

### Running the Pre-built JARs

1. Clone the [Executable](Executable) directory to your machine.
2. Open two separate terminal windows — one for the server and one for the client.
3. Start the **server**:

```bash
java -jar ServerSauvegardeAuto.jar
```

4. Start the **client**:

```bash
java -jar ClientSauvegardeAuto.jar
```

### Building from Source

1. Clone the [Sources](Sources) directory to your machine.

2. **Compile the server** — from the `ServerSrc` directory:

```bash
javac -d classes src/*.java src/server/*.java src/serverProtocol/*.java src/utils/*.java
```

3. **Compile the client** — from the `ClientSrc` directory:

```bash
javac -d classes src/*.java src/client/interfaceUtilisateur/*.java src/client/model/*.java src/client/operator/*.java src/client/*.java src/serverProtocol/*.java src/utils/*.java
```

4. Copy the server properties file:

```bash
cp src/server.properties classes
```

5. **Run** either program:

```bash
java -cp classes Main
```

---

## Usage

### Initial Setup

On first launch, the client application will prompt you to configure a user account and backup settings. Below is an example initialization session:

```
Application Configuration
Create a new user
> username: toto
> password: 0000
Confirm credentials: toto -> 0000?
> Y/N: y
Settings:
> Backup frequency (days): 0
> Confirm settings? (Y/N): y

==========Application Initialization==========
User information retrieved
Welcome toto
SERVER: authentication response - Incorrect user information
SERVER: authentication response - CREATE USER
No previously saved queue found
Setup complete — application ready
===============================================

Launching Client App
FileSaver- : waiting on FWQ
FileChecker- Scanning user files...
Welcome to the application shell
>
```

The application is now ready to use.

---

### Shell Commands

| Command | Description |
|---|---|
| `help` | Display available commands. |
| `config info` | Show current application configuration. |
| `config update <key> <value>` | Update a configuration parameter. |
| `app stop` | Stop the application. |
| `app check` | Trigger a manual file check for pending backups. |
| `process listFile` | List all files currently registered for backup. |
| `process clearFile` | Clear (reset) the file list. |
| `process addFile <filePath>` | Add a file or directory to the backup list. |
| `process delFile <filePath>` | Remove a file or directory from the backup list. |
| `process showFile` | Display details about a directory in the list. |
| `dev showFwq` | Display the current state of the file waiting queue. |
| `dev pauseSave <ms>` | Pause the backup thread for `<ms>` milliseconds. |
| `server listFile` | List all personal files stored on the server. |
| `server download <filePath> <destDir>` | Download a file from the server to a local directory. |

---

## Testing Utilities

The following shell commands can be used to generate test data:

```bash
# Generate a large ~500 MB test file
dd if=/dev/zero of=file500MB bs=100M count=5

# Create a nested directory structure with dummy files
mkdir -p Test/Split/Split1 && touch Test/Split/Split1/{t1.txt,t2.txt,t3.txt}
mkdir -p Test/Split/Split2 && touch Test/Split/Split2/{t1.txt,t2.txt,t3.txt}
```
