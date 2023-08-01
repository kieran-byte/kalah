package kalah.objects;


public abstract class Pit {

    //variables
    private Pit _opposite;
    private int _seeds;
    private int _playNum;
    private Pit _next;


    Pit(int seeds, int playNum){
        _seeds = seeds;
        _playNum = playNum;
    }

    public void sow(){
        _seeds++;
    }

    //Determines if the Pit is sowable, this function is overidden
    public boolean isSowable(int playerNum){
        if(_playNum == playerNum){
            return true;
        }
        else{
            return false;
        }
    }


    //getters and setters
    public int getSeeds(){
        return _seeds;
    }

    public void setSeeds(int seeds){
        _seeds = seeds;
    }

    public Pit getNext(){
        return _next;
    }
    public Pit setNext(Pit next){
        _next = next;
        return next;
    }


    public int getPlayNum(){
        return _playNum;
    }

    public void SetOpposite(Pit opposite){
        _opposite = opposite;
    }

    public Pit getOpposite(){
        return _opposite;
    }
}
