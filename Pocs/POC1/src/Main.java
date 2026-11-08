import com.google.gson.Gson;
import com.google.gson.JsonParseException;
import java.io.IOException;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

public class Main {
    public static void main(String[] args) {
        if (args.length > 1) {
            System.err.println("Usage : Main [fichier.json]");
            System.exit(1);
        }

        String filename = "poc1-init.json";
        if (args.length == 1) {
            filename = args[0];
        }

        try {
            String json = Files.readString(Path.of(filename), StandardCharsets.UTF_8);
            Gson gson = new Gson();
            GameState state = gson.fromJson(json, GameState.class);

            if (state == null || state.world == null || state.robots == null) {
                throw new IllegalArgumentException("Etat initial mal géré");
            }
            if (!Double.isFinite(state.world.w) || !Double.isFinite(state.world.h)
                    || state.world.w <= 0 || state.world.h <= 0
                    || !Double.isFinite(state.moveSpeed) || state.moveSpeed <= 0) {
                throw new IllegalArgumentException("Erreurs sur les dimensions et la vitesse");
            }
            for (Robot robot : state.robots) {
                if (robot == null || robot.id == null || robot.id.isBlank()) {
                    throw new IllegalArgumentException("Robots sans ids");
                }
                if (!Double.isFinite(robot.x) || !Double.isFinite(robot.y)
                        || robot.x < 0 || robot.x > state.world.w
                        || robot.y < 0 || robot.y > state.world.h
                        || !Double.isFinite(robot.heading) || robot.energy < 0 || robot.energy > 20) {
                    throw new IllegalArgumentException("problems au niveau ids robots " + robot.id);
                }
            }

            Engine engine = new Engine(state);
            BufferedReader input = new BufferedReader(new InputStreamReader(System.in, StandardCharsets.UTF_8));
            String line;
            while ((line = input.readLine()) != null) {
                engine.apply(line);
            }
        } catch (IOException | JsonParseException | IllegalArgumentException exception) {
            System.err.println("Erreur : " + exception.getMessage());
            System.exit(1);
        }
    }
}
