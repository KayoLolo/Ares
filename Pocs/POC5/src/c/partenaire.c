#include <ctype.h>
#include <stdio.h>
#include <string.h>

int main(int argc, char *argv[]) {
    if (argc != 1 && argc != 3) {
        fprintf(stderr, "Usage : partenaire\n");
        return 1;
    }

    const char *request = "/tmp/ares_req";
    const char *reply = "/tmp/ares_rep";
    if (argc == 3) {
        request = argv[1];
        reply = argv[2];
    }

    FILE *input = fopen(request, "r");
    if (input == NULL) {
        perror("Ouverture requete");
        return 1;
    }

    char message[1024];
    if (fgets(message, sizeof(message), input) == NULL) {
        fprintf(stderr, "Impossible de lire un message\n");
        fclose(input);
        return 1;
    }
    if (fclose(input) == EOF) {
        perror("Fermeture requete");
        return 1;
    }
    if (strchr(message, '\n') == NULL) {
        fprintf(stderr, "Retour à la ligne obligatoire\n");
        return 1;
    }

    for (int i = 0; message[i] != '\0'; i++) {
        message[i] = (char) toupper((unsigned char) message[i]);
    }

    FILE *output = fopen(reply, "w");
    if (output == NULL) {
        perror("Ouverture reponse");
        return 1;
    }
    if (fputs(message, output) == EOF) {
        perror("Ecriture reponse");
        fclose(output);
        return 1;
    }
    if (fclose(output) == EOF) {
        perror("Fermeture reponse");
        return 1;
    }
    return 0;
}
