import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        List<Bot> roundRobinBots = createBots();
        RoundRobin roundRobin = new RoundRobin();
        List<Match> roundRobinMatches = roundRobin.generateMatches(roundRobinBots);
        playTournament("Round-robin", roundRobinBots, roundRobinMatches);

        List<Bot> eliminationBots = createBots();
        Elimination elimination = new Elimination();
        List<Match> eliminationMatches = elimination.generateMatches(eliminationBots);
        playTournament("Élimination directe", eliminationBots, eliminationMatches);
        Match finalMatch = eliminationMatches.get(eliminationMatches.size() - 1);
        System.out.println("Vainqueur : " + finalMatch.getWinner().id);
    }

    private static List<Bot> createBots() {
        List<Bot> bots = new ArrayList<>();
        for (int i = 1; i <= 8; i++) {
            bots.add(new Bot("b" + i));
        }

        return bots;
    }

    private static void playTournament(String name, List<Bot> bots, List<Match> matches) {
        System.out.println(name);
        Elo elo = new Elo();
        for (Match match : matches) {
            Bot winner = match.getWinner();
            elo.update(match);
            System.out.println(match.first.id + " vs " + match.second.id + " : " + winner.id);
        }
        System.out.println("Nombre de matchs : " + matches.size());

        List<Bot> ranking = elo.getRanking(bots);
        for (int i = 0; i < ranking.size(); i++) {
            Bot bot = ranking.get(i);
            System.out.println("#" + (i + 1) + " " + bot.id + " " + Math.round(bot.score));
        }
    }
}
