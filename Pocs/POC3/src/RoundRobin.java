import java.util.ArrayList;
import java.util.List;

public class RoundRobin {
    public List<Match> generateMatches(List<Bot> bots) {
        List<Match> matches = new ArrayList<>();

        for (int i = 0; i < bots.size(); i++) {
            for (int j = i + 1; j < bots.size(); j++) {
                Match match = new Match(bots.get(i), bots.get(j));
                matches.add(match);
            }
        }
        return matches;
    }
}
