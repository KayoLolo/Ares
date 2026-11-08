# POC 5 — Communication

Première manche : Java envoie `ping`, le partenaire TypeScript renvoie `PING`.
Le partenaire C reste à ajouter. Exécuter sous Linux (devContainer ou WSL),
avec Java 21, Node.js et npm, depuis `Pocs/POC5`.

```bash
npm install
npm run build
mkdir -p target/classes
javac -encoding UTF-8 -d target/classes src/java/Main.java
mkfifo /tmp/ares_req /tmp/ares_rep
```

Dans un premier terminal :

```bash
node dist/partenaire.js
```

Dans un second terminal, depuis le même dossier :

```bash
java -cp target/classes Main
```

Java vérifie la réponse et affiche `vérifié : OK`. Chaque programme traite un
échange puis termine ; relancer le partenaire pour un nouvel échange.
Les deux programmes sont lancés séparément et ne se connaissent que par les FIFO.
Supprimer les tubes après utilisation : `rm /tmp/ares_req /tmp/ares_rep`.

Difficultés : respecter l'ordre d'ouverture des FIFO pour éviter un blocage, lire les octets jusqu'au retour à la ligne puis reconstruire le texte
UTF-8, fermer les tubes même en cas d'erreur et préparer les outils
Linux sous Windows avec WSL. Java écrit la requête puis lit la réponse ;
le partenaire fait l'inverse.
