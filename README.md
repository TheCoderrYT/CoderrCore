# CoderrCore
Dieses Plugin bildet die Basis für deinen Server.
## Inhalt ##
- Multiworldsystem (inklusive Lobby und Testwelt)
- Rangsystem (inklusive Permissions)
- Anpassbares Farbtheme (Chat, Tablist)
- Anpassbare Tablist (inklusive Serverperformanceanzeige (Laganzeige) und Tageszeit im Spiel)
- Speichern von Positionen
- Automatisches Herunterfahren
- Lebensanzeige über Spielerkopf
- Farbcodes auf Schildern
- Verbesserte Schlafensanzeige
- Einstellbarer Creeperblockschaden
## Commands ##
<> bedeutet, dass das Argument erforderlich ist.<br>
[] bedeutet, dass das Argument angegeben werden kann.
### /defaultgamemode ###
Benutzung: `/defaultgamemode`<br>
Setzt den Spielmodus des Spielers auf den Standardspielmodus der Welt in der er sich befindet.
### /friend ###
Benutzung: `/friend`<br>
Öffnet ein Inventar zum Konfigurieren der Freunde. (Freundemenu)
### /invsee ###
Benutzung: `/invsee <Spielername>`<br>
Zeigt das aktuelle Inventar des angegebenen Spielers. Zum öffnen des Inventars wird die Berechtigung `coderrcore.command.invsee` oder `coderrcore.command.invact` vorrausgesetzt.<br>
Mit dem Inventar kann interagiert werden, wenn der Spieler die Berechtigung `coderrcore.command.invact` besitzt.
### /l ###
Benutzung: `/l`<br>
Teleportiert den Spieler zurück in die Lobby.
### /lobby ###
Benutzung: `/lobby`<br>
Teleportiert den Spieler zurück in die Lobby.
### /maintrance ###
Benutzung: `/maintrance`<br>
Stellt den in den Wartungsmodus. Nur Admins können den Server betreten und alle anderen Spieler werden gekickt.
### /permissions ###
Benutzung: `/permissions`<br>
Zeigt alle Berechtigungen (Permissions) des Spielers welcher den Command ausführt.
### /pos ###
Benutzung: `/pos <get|set|remove|Positionsname> [Positionsname]`<br>
Schreibt die Position des Spielers in den Chat, speichert Positionen in einer Datei oder ruft Positionen aus der Datei ab.
### /settings ###
Benutzung: `/settings`<br>
Öffnet ein Inventar zum Einsehen und Einstellen der Welteinstellungen. Bearbeiten der Einstellung nur mit `coderrcore.rank.admin` Berechtigung.
### /teleporter ###
Benutzung: `/teleporter <Zielwelt|x> [y] [z]`<br>
Setzt einen Teleporter an die Position des Spielers, welcher in eine Welt oder an eine Position der aktuellen Welt teleportiert.
### /testworld ###
Benutzung: `/testworld`<br>
Teleportiert Spieler in die Testwelt. Wenn die testwelt noch nicht geladen wurde, wird sie nun geladen.
### /world ###
Benutzung: `/world [Weltname|create|info|undo] [Weltname|Spielername] [Spielmodus] [Welttyp] [Schwierigkeitsgrad] [Hardcore-Modus] [Nether] [End] [Schaden] [Hunger]`<br>
`/world` Teleportiert den Spieler in eine bestimmte Welt oder erzeugt eine Welt.<br>
Mehr unter [Mehrere Welten](#mehrere-welten)
### /worlds ###
Benutzung: `/worlds [edit|reset]`<br>
Öffnet ein Inventar indem durch Klicken auf ein Item der Spieler in eine Welt teleportiert werden kann. Inventar kann bearbeitet oder auf eine Standardanordnung zurückgesetzt werden. Zum Bearbeiten und Zurücksetzen ist die Berechtigung `coderrcore.rank.admin` erforderlich.
### /rank ###
Benutzung: `/rank <set|remove> <Spielername> <Ranglevel>`<br>
Setzt den Serverrang eines Spielers auf das entsprechende Level oder löscht den Rang, sodass dieser zu `Spieler` wird.


## Mehrere Welten
Das Plugin macht es möglich mehr als eine Welt auf dem Server zu betreten. Standardmäßig wird der Spieler beim ersten Betreten in die Lobby teleportiert, welche immer geladen ist.<br>
Für jede Welt werden Spielstände wie Position, Spielmodus, Inventar, Herzen, Hunger, XP/Level, Respawnpunkt und aktiven Effekte separat gespeichert. Das bedeutet, dass bei jedem Teleport in eine andere Welt sich diese Spielstände anpassen.<br>
### Teleportieren in Welten ###
Durch den Command `/world` kann ein Spieler sich in verschiedene Welten teleportieren.<br>
`/world` teleportiert den Spieler in die standard Überleben-Welt des Servers.<br>
`/world [Weltname]` teleportiert den Spieler in die als Argument angegebene Welt.<br>
### Bearbeiten und Erzeugen von Welten ###
Das Bearbeiten der Welteinstellungen kann über die `worlds.yml` erfolgen oder durch den `/settings` Command für die aktuelle Welt.<br>
Das Erzeugen einer Welt kann durch den `/world` Command oder über die `worlds.yml` erfolgen.<br>
#### Erzeugen einer Welt durch den `/world` Command ####
`/world create [Weltname] [Spielmodus] [Welttyp] [Schwierigkeitsgrad] [Hardcore] [Nether-Dimension] [End-Dimension] [Schaden] [Sättigkeit]`<br>
Argumente
- `Weltname` Namen der Welt
- `Spielmodus` Standardspielmodus, welcher beim ersten Betreten der Welt dem Spieler gesetzt wird. <br>`SURVIVAL|CREATIVE|SPECTATOR|ADVENTURE`
- `Welttyp` Welttyp, welcher beim Generieren der Welt angewendet wird. <br>`DEFAULT|EMPTY|REDSTONE`
- `Schwierigkeitsgrad` Schwierigkeitsgrad der Welt <br>`PEACEFUL|EASY|NORMAL|HARD`
- `Hardcore` Ob der Spieler nach dem Tod in den `SPECTATOR` Modus gesetzt wird. <br>`true|false`
- `Nether-Dimension` Ob die Nether-Dimension mit geladen werden soll. Andernfalls kann sie nicht betreten werden. <br>`true|false`
- `End-Dimension` Ob die End-Dimension mit geladen werden soll. Andernfalls kann sie nicht betreten werden. <br>`true|false`
- `Schaden` Ob die Spieler Schäden bekommen können. <br>`true|false`
- `Sättigung` Ob die Spieler keinen Hunger bekommen können. <br>`true|false
#### Erzeugen einer Welt durch die `worlds.yml` Datei ####
```
weltname:
  enabled: true
  teleport: true
  slot: 13
  datafile: weltname_data.yml
  gamemode: SURVIVAL
  difficulty: NORMAL
  hardcore: false
  generator: DEFAULT
  damage: true
  saturation: false
  nether: false
  the_end: false
```
Nicht benötigte Eigenschaften können weggelassen werden und werden automatisch auf den Standardwert gesetzt.
- `enabled: true|false` Welt wird geladen oder nicht. Standard: `true`
- `teleport: true|false` In die Welt kann per Command teleportiert werden oder nicht. Spieler mit Operator-Rechten können die Welt weiterhin betreten. Standard: `true`
- `slot: 0-26` Gibt den Slot an des Items im Welteninventar (`/worlds` oder Kompass in Lobby), welcher zum teleportieren angeklickt werden muss. Standard: `1`
- `datafile: weltname_data.yml` Name der Speicherdatei der Spielstände. Standard: `weltname_data.yml`
- `gamemode: SURVIVAL|CREATIVE|SPECTATOR|ADVENTURE` Standardspielmodus, welcher beim ersten Betreten der Welt dem Spieler gesetzt wird. Standard: `SURVIVAL`
- `difficulty: PEACEFUL|EASY|NORMAL|HARD` Schwierigkeitsgrad, welcher der Welt gesetzt wird. Standard: `NORMAL`
- `hardcore: true|false` Ob Spieler, wenn er stirbt in den `SPECTATOR` Modus gesetzt wird oder nicht. Standard: `false`
- `generator: DEFAULT|EMPTY|REDSTONE` Typ der Weltengeneration bei neuen Welten. Funktioniert nur wenn Welt noch nicht vorhanden ist. Standard: `DEFAULT`
- `damage: true|false` Ob Spieler Schaden bekommen können. Standard: `true`
- `saturation: true|false` Ob Spieler keinen Hunger bekommen können. Standard: `false`
- `nether: true|false` Ob die Nether-Dimension mit geladen werden soll. Andernfalls kann sie nicht betreten werden. Standard: `false`
- `the_end: true|false` Ob die End-Dimension mit geladen werden soll. Andernfalls kann sie nicht betreten werden. Standard: `false`


## Ränge ##
Spieler können 4 verschiedene Ränge auf dem Server tragen: `Spieler` `Premium` `VIP` `Admin`<br>
Verschiedene Ränge ermöglichen es den Spielern verschiedene Berechtigungen zu geben. Die Berechtigungen, welche eine bestimmte Spielergruppe haben soll kann in der `permissions.yml` Datei eingestellt werden.<br>
Ränge sind in der Tablist (Spielerliste, welche sich beim Drücken der Taste `TAB` erscheint), als auch als farbig markierte Namen über den Spielerköpfen und im Chat zu erkennen.<br>
Die Farben und Ranglevel der Ränge sind:
1. Spieler: Hellgrün
2. Premium: Gold
3. VIP: Violett
4. Admin: Dunkelrot
### permissions.yml ###
In der `permissions.yml` werden die Berechtigungen (Permissions) der Spielergruppen festgelegt.
Der Aufbau der Datei wird automatisch, wenn die Datei nicht vorhanden ist, erstellt und mit Standardberechtigungen gefüllt. Diese Standardberechtigungen können entfernt, bearbeitet und ergänzt werden. Wichtig ist dabei die Syntax der Datei beizubehalten.<br>
Standardaufbau der Datei:
```
admin:
  minecraft-command-gamemode: true
  minecraft-command-give: true
  coderrcore-command-rank: true
  coderrcore-command-update: true
  coderrcore-command-permissions: true
  coderrcore-rank-admin: true
  coderrcore-command-testworld: true
  coderrcore-command-invact: true
vip:
  coderrcore-command-update: true
  coderrcore-command-permissions: true
  coderrcore-rank-vip: true
  coderrcore-command-testworld: true
  coderrcore-command-invsee: true
premium:
  coderrcore-command-permissions: true
  coderrcore-rank-premium: true
player:
  coderrcore-command-permissions: true
  coderrcore-rank-player: true
```
Berechtigungen mit dem Wert `false` nach dem Doppelpunkt werden nicht berücksichtigt.
