package it.polimi.ingsw.model.match;

public class PawnCounts {
    private int playerCount;

    public PawnCounts(int playerCount) {
        this.playerCount = playerCount;
    }

    public int getCloudTileCount(){
        return  playerCount;
    }

    public int getTowerPerPlayerCount() throws InvalidPlayerCountException{
        return switch (playerCount) {
            case 2 -> 8;
            case 3 -> 6;
            case 4 -> 8;  //should be different?
            default -> throw new InvalidPlayerCountException("Unexpected value: " + playerCount);
        };
    }

    public int getStudentsDrawnForCloud() throws InvalidPlayerCountException {
        return switch (playerCount) {
            case 2 -> 3;
            case 3 -> 4;
            case 4 -> 3;
            default -> throw new InvalidPlayerCountException("Unexpected value: " + playerCount);
        };
    }

    public int getStudentsPickedFromBag() {
        return 8;
    }

    public int getStudentsMovedToRoom() throws InvalidPlayerCountException {
        return switch (playerCount) {
            case 2 -> 7;
            case 3 -> 9;
            case 4 -> 7;
            default -> throw new InvalidPlayerCountException("Unexpected value: " + playerCount);
        };
    }
}
