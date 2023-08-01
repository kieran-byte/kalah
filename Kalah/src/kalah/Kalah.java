package kalah;

import com.qualitascorpus.testsupport.IO;
import com.qualitascorpus.testsupport.MockIO;
import kalah.game.GameState;
import kalah.objects.Cup;
import kalah.objects.Pit;


/**
 * This class is the starting point for a Kalah implementation using
 * the test infrastructure. Remove this comment (or rather, replace it
 * with something more appropriate)
 */
public class Kalah {
	public static void main(String[] args) {
		new Kalah().play(new MockIO());
	}
	public void play(IO io) {

		//starts game with four marbles in each cup, to avoid hard coding
		int defaultStart = 4;

		//main gameplay loop
		char out;
		int move = 0;

		GameState game = new GameState(io);

		//runs until completion of the game

		while(!game.isFinished()){

			game.printBoard();

			//if its your turn and you have no moves left, game is over
			if(game.checkOver()){
				break;
			}


			out = game.printTurn();

			//exit condition
			if(out == 'q'){
				break;
			}
			game.doMove(Character.getNumericValue(out));



		}




	}





}
