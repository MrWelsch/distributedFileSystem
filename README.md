# Virtuelles Filesystem

Ziel des Projekts ist es die Dateisysteme mehrerer unterschiedlicher Systeme gemeinsam als ein logisches Dateisystem zu behandeln und darzustellen.

## Architektur

Wir haben das Projekt in 3 Applications gesplittet. Die Kommunikation verläuft über REST Schnittstellen am Server und Dataclient.

#### Userclient

Der Userclient bietet eine UI, mit der ein Benutzer interagieren kann.
So werden dort neue Dateisysteme dem Virtuellen Filesystem hinzugefügt, sowie mit bereits bestehenden Dateisystemen interagiert.
Die Interaktion beschränkt sich auf die folgenden Operationen:

- browse
- search
- create
- delete
- rename

#### Dataclient

Der Dataclient befindet sich auf dem Dateisystem, dass Teil des Virtuellen Filesystems wird.
Bei der Initalisierung durch den Userclient, wird der Pfad zu dem Ordner mitgegeben, den der Besitzer des Dateisystems bereit ist zu teilen.
So wird verhindert, dass aus Versehen private Daten mit dem Virtuellen Filesystem geteilt werden.
Intern erstellt der Dataclient einen Tree des ausgewählten Ordners, den er mit dem Server teilt und auf dem verschiedene Operationen ausgeführt werden können, wie zum Beispiel create und delete.

#### Server

Der Server dient als Vermittler zwischen den Userclients und den Dataclients. Es gibt nur genau einen Server.
Auf dem Server werden die Dateisysteme der Dataclients zusammengeführt und in einer Datenbank gespeichert.
Ein Userclient kann über den Server auf das Virtuelle Filesystem zugreifen, wobei Operationen wie browse und search direkt vom Server wieder zurück an den Userclient gehen. Operationen wie create und delete werden vom Server an die entsprechenden Dataclients weitergeleitet.

## Use Cases 

- Ein Userclient möchte die Dateisysteme aller Dataclients sehen und folgende Befehle ausführen können: browse, search, create, delete, rename
- Ein Dataclient möchte ein Abbild seines eigenen Dateisystems an den Server schicken können, wo dieses mit den anderen Dateisystemen zusammen gesetzt wird
-  Der Server möchte die Schnittstelle zwischen Userclient und Server darstellen und alle Operationen bedienen können

## Anforderungen

- Programmiersprache: Java
- keine Webanwendung
- Maven-Projekt
- Nutzung von GitHub
- JUnit Tests

## Lösungsstrategie

Kernidee:
- Dataclient:
  * Wählt Ordner aus, erstellt einen Tree und schickt ihn zum Server
  * Empfängt und bearbeitet delete, create und rename Operationen vom Server

- Userclient:
  * Initalisiert den Dataclient
  * Hat Zugriff auf alle Operationen (delete, create, rename, browse, search)

- Server:
  * Fügt die Trees von den Dataclients zusammen und speichert diese in einer Datenbank
  * Verwaltet Anfragen von den Userclients

## Getting Started

#### Vorraussetzungen

- Java 11
- Maven
- Docker

#### Installation und Deployment

Beschreiben Sie die Installation und das Starten ihrer Software Schritt für Schritt.
TODO

## Dokumentation

### Java-Dokumentation



### API-Dokumentation
Um die API-Dokumentation des Servers einsehen zu können, muss zur Adresse des Server lediglich 
```/swagger-ui/index.html``` hinzugefügt werden. Nun wird man automatisch zur Swagger-UI weitergeleitet in der die 
einzelnen Endpunkte aufgelistet und beschrieben werden.