package kalah.game;

import com.qualitascorpus.testsupport.IO;
import kalah.objects.Cup;
import kalah.objects.Pit;
import kalah.objects.Store;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/*
GameState maintains the state of the game, players and all individual components of the game
 */
public class GameState {

    private boolean _finished = false;
    private int _turn = 1;

    private List<Cup> cups;
    private final int DEFAULT_SEEDS = 4;
    private final int DEFAULT_LENGTH = 6;
    private final int PLAY_1 = 1;
    private final int PLAY_2 = 2;

    public List<Pit> _board;

    private List<Store> _stores = new LinkedList<>();
    private IO _io;

    //Constructor
    public GameState(IO io) {

        _io = io;
        LinkedList<Cup> cups1 = buildBoard(PLAY_1, DEFAULT_SEEDS, DEFAULT_LENGTH);
        LinkedList<Cup> cups2 = buildBoard(PLAY_2, DEFAULT_SEEDS, DEFAULT_LENGTH);
        linkOpposites(cups1, cups2);

        _stores.add(new Store(0, PLAY_1));
        _stores.add(new Store(0, PLAY_2));

        cyclize(cups1, _stores.get(0), cups2, _stores.get(1));
        _board = new ArrayList<Pit>(cups1);
        _board.addAll(cups2);
    }


    //completely links all object in a circular linked list
    private void cyclize(LinkedList<Cup> cups1, Store store1, LinkedList<Cup> cups2, Store store2) {

        cups1.getLast().setNext(store1);
        store1.setNext(cups2.getFirst());

        cups2.getLast().setNext(store2);
        store2.setNext(cups1.getFirst());
    }

    //links opposite cups
    private void linkOpposites(LinkedList<Cup> cups1, LinkedList<Cup> cups2) {

        Cup top;
        Cup bot;
        for (int i = 0; i < cups1.size(); i++) {
            top = cups1.get(i);
            bot = cups2.get(cups2.size() - i - 1);

            top.setOpposite(bot);
            bot.setOpposite(top);
        }

    }

    private LinkedList<Cup> buildBoard(int playNum, int seeds, int length) {

        LinkedList<Cup> retArray = new LinkedList<>();
        retArray.addLast(new Cup(seeds, playNum));

        for (int i = 0; i < length - 1; i++) {

            Cup nextCup = new Cup(seeds, playNum);
            retArray.get(i).setNext(nextCup);
            retArray.add(nextCup);

        }

        return retArray;
    }

    /*
	Prints the entire board every turn as well as the players "bank" values
	 */
    public void printBoard() {

        _io.println("+----+-------+-------+-------+-------+-------+-------+----+");

        int pos = 6;
        String out = "| P2 | ";
        for (int i = _board.size() - 1; i > _board.size() / 2 - 1; i--) {

            if (_board.get(i).getSeeds() > 9) {
                out += String.valueOf(pos) + "[" + _board.get(i).getSeeds() + "] | ";
            } else {
                out += String.valueOf(pos) + "[ " + _board.get(i).getSeeds() + "] | ";
            }
            pos--;
        }

        if (_stores.get(0).getSeeds() > 9) {
            out += "" + _stores.get(0).getSeeds() + " |";
        } else {
            out += " " + _stores.get(0).getSeeds() + " |";
        }

        _io.println(out);

        _io.println("|    |-------+-------+-------+-------+-------+-------|    |");

        if (_stores.get(1).getSeeds() > 9) {
            out = "| " + _stores.get(1).getSeeds() + " | ";

        } else {
            out = "|  " + _stores.get(1).getSeeds() + " | ";
        }
        int count = 0;
        for (int i = 0; i < _board.size() / 2; i++) {

            if (_board.get(i).getSeeds() > 9) {
                out += String.valueOf(count + 1) + "[" + _board.get(i).getSeeds() + "] | ";
            } else {
                out += String.valueOf(count + 1) + "[ " + _board.get(i).getSeeds() + "] | ";
            }
            count++;
        }
        out += "P1" + " |";
        _io.println(out);

        _io.println("+----+-------+-------+-------+-------+-------+-------+----+");
    }

    /*
	Prints out the current players turn, also recieves input from the user
	Function will always return a char of either q or a valid number of length one, between the ranges of 1 and 6 inclusive
	 */
    public char printTurn() {

        //declarations and initializations
        String out = "Player P";

        if (_turn == 1) {
            out += "1";
        } else {
            out += "2";
        }
        out += "'s turn - Specify house number or 'q' to quit: ";


        //input parsing, while loop until valid input
        String input = "";
        int inputVal = 0;
        while (true) {

            input = _io.readFromKeyboard(out);
            char val = input.charAt(0);

            //removes white space
            input = input.replaceAll("\\s+", "");
            if (input.charAt(0) == 'q') {
                _io.println("Game over");
                printBoard();
                return 'q';
            }

            if (input.length() != 1) {
                _io.println("Number is too large, please try again");
            }
            //determines if the number given is acceptable or not, if not loop repeats, else exits
            else if ((Integer.parseInt(input) > 6 || Integer.parseInt(input) < 1)) {

                _io.println("Number is outside of bounds, please try again");
            } else if ((_board.get(Character.getNumericValue(val) - 1).getSeeds() == 0 && _turn == 1) || (_board.get(Character.getNumericValue(val) - 1 + 6).getSeeds() == 0 && _turn == 2)) {

                _io.println("House is empty. Move again.");

            } else {
                return input.charAt(0);
            }

            printBoard();
        }
    }

    public boolean isFinished() {
        return _finished;
    }

    //determines if capture is possible
    public boolean capture(Pit currPit) {

        //fail capture conditions
        if (currPit.getOpposite() == null) {
            return false;
        }
        if (currPit.getPlayNum() != _turn) {
            return false;
        }

        //current pit must have only one seed, must be same number as player, opposite must have seeds
        if (currPit.getSeeds() == 1 && currPit.getOpposite().getSeeds() > 0) {
            return true;
        }
        return false;
    }

    //Determines if the game is over, if it is over prints required
    public boolean checkOver(){

        if (gameOver()) {
            captureAll();
            return true;
        }
        return false;
    }


    /*
    Given a move, execute the move and change the turn indicator appropriately
     */
    public void doMove(int move) {

        int marbles;

        move--; // accounts for zero indexing
        if (_turn != 1) {
            move += 6;
        }

        marbles = _board.get(move).getSeeds();
        _board.get(move).setSeeds(0);

        //function that distributes marbles to banks and cups accordingly, determining if a new turn is required
        boolean newTurn = true;
        int capSeeds;
        Pit currPit = _board.get(move);
        while (marbles > 0) {
            currPit = currPit.getNext();

            if (currPit.isSowable(_turn)) {
                currPit.sow();
                marbles--;
            }

            //capture logic
            if (capture(currPit) && marbles == 0) {

                if (_turn == 1) {
                    capSeeds = currPit.getSeeds();
                    currPit.setSeeds(0);

                    capSeeds += currPit.getOpposite().getSeeds();
                    currPit.getOpposite().setSeeds(0);

                    _stores.get(0).addSeeds(capSeeds);
                } else {
                    capSeeds = currPit.getSeeds();
                    currPit.setSeeds(0);

                    capSeeds += currPit.getOpposite().getSeeds();
                    currPit.getOpposite().setSeeds(0);

                    _stores.get(1).addSeeds(capSeeds);
                }
            }
        }

        //Only possible if the last marble was sown into the players store
        if (currPit instanceof Store) {
            newTurn = false;//gives player another turn
        }


        //switches the player turn when applicable
        if (newTurn) {
            if (_turn == 1) {
                _turn = 2;
            } else {
                _turn = 1;
            }
        }

    }

    //prints out the winner of the game
    public void printWinner(int[] vals) {

        _io.println("Game over");
        printBoard();

        _io.println("\tplayer 1:" + vals[0]);
        _io.println("\tplayer 2:" + vals[1]);

        if(vals[0] > vals[1]){
            _io.println("Player 1 wins!");
        }
        else if(vals[0] < vals[1]){
            _io.println("Player 2 wins!");
        }
        else{
            _io.println("A tie!");
        }

        _finished = true;
    }


    private void captureAll() {

        int[] vals = new int[2];

        //if this method occurs, the current player is out of moves
        //so all marbles of the other player are taken
        int sum = 0;


        //sums all of player2s remaining marbles and adds to bank
        for (int i = 0; i < _board.size(); i++) {
            if (_board.get(i).getPlayNum() == 1) {
                sum += _board.get(i).getSeeds();
            }
        }
        vals[0] = sum + _stores.get(0).getSeeds();
        vals[1] = 48 - vals[0];

        printWinner(vals);

        _finished = true;
    }

    //determines if the current player is out of moves
    private boolean gameOver() {

        //if the player has no marbles on their side
        int sum = 0;
        for (int i = 0; i < _board.size(); i++) {

            if (_board.get(i).getPlayNum() == _turn) {
                sum += _board.get(i).getSeeds();
            }
        }

        //the game is over
        if (sum == 0) {
            return true;
        }
        return false;
    }

}