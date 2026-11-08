public class Match {
    Bot first;
    Bot second;

    public Match(Bot first, Bot second) {
        this.first = first;
        this.second = second;
    }

    public Bot getWinner() {
        if (first.id.compareTo(second.id) < 0) {
            return first;
        }
        return second;
    }
}
