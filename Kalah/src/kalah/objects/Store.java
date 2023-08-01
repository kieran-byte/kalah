package kalah.objects;

/*
Represents the store section of the kalah board
 */
public class Store extends Pit {


    public Store(int seeds, int playNum) {
        super(seeds, playNum);
    }

    public void addSeeds(int seeds){
        setSeeds(getSeeds() + seeds);
    }

    public boolean isSowable(int playerNum){

        return playerNum == getPlayNum();

    }

}
