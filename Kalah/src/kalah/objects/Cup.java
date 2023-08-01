package kalah.objects;

import kalah.objects.Store;

/*
Cups represent the entities that are not banks. They are initlized with four marbles
 */
public class Cup extends Pit {

    private Cup _opposite;

    //Constructor
    public Cup(int seeds, int playNum) {

        super(seeds, playNum);
    }


    //houses are always sowable, overrides parent class function as method call goes by class hierarchy
    public boolean isSowable(int playerNum){
        return true;
    }

    public Cup getOpposite(){
        return _opposite;
    }
    public void setOpposite(Cup opposite){
        _opposite = opposite;
    }

}
