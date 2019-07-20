package model;
public class DogStat {
    private String breedName;
    private int wins;

    private int losses;

    public DogStat() {

    }
    public DogStat(String breedName, int wins, int losses) {
        this.breedName = breedName;
        this.wins = wins;
        this.losses = losses;
    }

    public String getBreedName() {
        return breedName;
    }

    public void setBreedName(String breedName) {
        this.breedName = breedName;
    }

    public int getWins() {
        return wins;
    }

    public void setWins(int wins) {
        this.wins = wins;
    }

    public int getLosses() {
        return losses;
    }

    public void setLosses(int losses) {
        this.losses = losses;
    }


}