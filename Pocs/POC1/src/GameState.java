import com.google.gson.annotations.SerializedName;
import java.util.List;
import java.util.ArrayList;

public class GameState {
    World world;

    @SerializedName("move_speed")
    double moveSpeed;

    List<Robot> robots;
    List<Crystal> crystals = new ArrayList<>();
}
