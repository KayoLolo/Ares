import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Locale;

public class Main {
    public static void main(String[] args) {
        if (args.length != 0 && args.length != 2) {
            System.err.println("Usage : Main");
            System.exit(1);
        }

        String request = "/tmp/ares_req";
        String reply = "/tmp/ares_rep";
        if (args.length == 2) {
            request = args[0];
            reply = args[1];
        }

        String message = "ping";
        try {
            try (BufferedWriter writer = Files.newBufferedWriter(
                    Path.of(request), StandardCharsets.UTF_8, StandardOpenOption.WRITE)) {
                writer.write(message);
                writer.newLine();
            }
            System.out.println("envoyé : " + message);

            String response;
            try (BufferedReader reader = Files.newBufferedReader(Path.of(reply), StandardCharsets.UTF_8)) {
                response = reader.readLine();
            }
            System.out.println("reçu : " + response);

            if (message.toUpperCase(Locale.ROOT).equals(response)) {
                System.out.println("vérifié : OK");
            } else {
                System.err.println("vérifié : ECHEC");
                System.exit(1);
            }
        } catch (IOException | IllegalArgumentException exception) {
            System.err.println("Erreur : " + exception.getMessage());
            System.exit(1);
        }
    }
}
