# POC 4 — Spectator

Avec Node.js et npm, depuis `Pocs/POC4` :

```bash
npm install
npm test
```

`src/project.ts` exporte `project(events: Event[]): MatchState` et renvoie l'état
final sans affichage ni graphisme. `src/types.ts` définit les événements et l'état.
Le journal doit être ordonné par `seq` et déclarer les robots avec `MATCH_STARTED`.
Ils démarrent à (0, 0), avec 100 HP ; les événements actualisent ensuite ces valeurs.
Les tests utilisent le JSON fourni, conservé localement et exclu de Git.

Difficulté : distinguer un tir des dégâts. `ROBOT_SHOT` ne retire aucun HP :
seul `ROBOT_DAMAGED` change les points de vie, pour ne pas compter deux fois le tir.
