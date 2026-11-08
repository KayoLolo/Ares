import java.util.ArrayList;
import java.util.List;

public class Elo {
    public void update(Match match) {
        double firstScore = match.first.score;
        double secondScore = match.second.score;
        double firstProbability = 1.0 / (1.0 + Math.pow(10, (secondScore - firstScore) / 400.0));
        double secondProbability = 1.0 - firstProbability;

        double firstResult = 0;
        if (match.getWinner() == match.first) {
            firstResult = 1;
        }
        double secondResult = 1.0 - firstResult;

        match.first.score = firstScore + 32 * (firstResult - firstProbability);
        match.second.score = secondScore + 32 * (secondResult - secondProbability);
    }

    public List<Bot> getRanking(List<Bot> bots) {
        List<Bot> ranking = new ArrayList<>(bots);
        for (int i = 0; i < ranking.size(); i++) {
            int best = i;
            for (int j = i + 1; j < ranking.size(); j++) {
                if (ranking.get(j).score > ranking.get(best).score) {
                    best = j;
                }
            }
            Bot current = ranking.get(i);
            ranking.set(i, ranking.get(best));
            ranking.set(best, current);
        }
        return ranking;
    }
}
