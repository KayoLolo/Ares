# POC 1 — Engine

Le moteur charge le JSON et applique `MOVE`, `WAIT`, `MINE` et `SHOOT` au clavier.
L'extension `TELEPORT` reste à réaliser dans un commit séparé.

Avec Java 21 et Maven, depuis `Pocs/POC1` :

```bash
mvn -q compile exec:java
```

Gson lit le JSON ; Maven télécharge les dépendances au premier lancement.
Le JSON fourni par l'enseignant est nécessaire en local et reste exclu de Git.
Saisir par exemple `A MOVE 0.0` : A passe de (40, 50) à (42, 50).
`A WAIT` lui permet de récupérer de l'énergie et produit `ROBOT_WAITED`.
`A MINE` récolte un cristal proche ou produit `NO_CRYSTAL` s'il n'y en a pas.
`B SHOOT 3.14` tire vers A : `ROBOT_SHOT`, puis `ROBOT_DAMAGED` si A est touché.
Chaque action produit une ligne JSON avec un numéro `seq` croissant.
Les erreurs d'action produisent `ACTION_REJECTED` sans modifier l'état.
Terminer avec Ctrl+D sous Linux ou Ctrl+Z puis Entrée sous Windows.

`Main` charge le fichier ; `GameState` regroupe le monde, la vitesse et les robots.
`World` et `Robot` représentent ces données dans des fichiers séparés.
`Engine` applique les actions et écrit les événements.
`Crystal` représente un cristal ; le champ facultatif `crystals` du JSON contient
une liste de positions, par exemple `"crystals": [{"x": 40, "y": 50}]` à la racine.
Le fichier fourni reste inchangé et ne contient aucun cristal.

Difficulté : déterminer le premier robot touché par un tir. Il faut distinguer
la distance dans la direction du tir de l'écart latéral, puis utiliser Pythagore
pour calculer le point d'impact sur le bord du robot, et pas seulement son centre.
