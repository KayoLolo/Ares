# POC 3 — League

Avec Java 21 et Maven, depuis `Pocs/POC3` :

```bash
mvn -q compile exec:java
```

Le programme joue deux tournois avec huit bots, de `b1` à `b8` :
28 matchs en round-robin et 7 en élimination directe.
Le plus petit identifiant gagne, par comparaison alphabétique.
`RoundRobin` et `Elimination` génèrent les matchs, sans modifier les scores.
`Match` détermine le vainqueur ; `Bot` conserve
l'identifiant et le score initial de 1000. `Elo` met à jour les scores après chaque
match avec K = 32 et produit le classement. Les scores ne sont arrondis qu'à
l'affichage. Chaque format repart de 1000 avec de nouveaux bots.
En élimination directe, les gagnants passent au tour suivant ; un bot sans
adversaire est qualifié directement, sans match ni modification de score.

Difficulté : calculer les deux nouveaux scores avec les valeurs d'avant le match.
