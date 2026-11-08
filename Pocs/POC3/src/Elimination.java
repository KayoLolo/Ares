import java.util.ArrayList;
import java.util.List;

public class Elimination {
    public List<Match> generateMatches(List<Bot> bots) {
        if (bots.isEmpty()) {
            throw new IllegalArgumentException("Il faut au moins un bot.");
        }

        List<Match> matches = new ArrayList<>();
        List<Bot> remaining = new ArrayList<>(bots);

        while (remaining.size() > 1) {
            List<Bot> nextRound = new ArrayList<>();
            for (int i = 0; i < remaining.size(); i += 2) {
                if (i + 1 == remaining.size()) {
                    nextRound.add(remaining.get(i));
                } else {
                    Match match = new Match(remaining.get(i), remaining.get(i + 1));
                    matches.add(match);
                    nextRound.add(match.getWinner());
                }
            }
            remaining = nextRound;
        }
        return matches;
    }
}
