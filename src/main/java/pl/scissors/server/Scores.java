package pl.scissors.server;

public class Scores {

   private int winnings;
   private int losses;


    public Scores(int winnings, int losses) {
        this.winnings = winnings;
        this.losses = losses;
    }

    public int getWinnings() {
        return winnings;
    }

    public void addWin(int winnings) {
        this.winnings += winnings;
    }

    public int getLosses() {
        return losses;
    }

    public void addLose(int losses) {
        this.losses += losses;
    }

    @Override
    public String toString() {
        return "Scores{" +
                "winnings=" + winnings +
                ", losses=" + losses +
                '}';
    }
}
