/**
 * This class implements the logic behind the BDD for the n-queens problem
 * You should implement all the missing methods
 *
 * @author Stavros Amanatidis
 *
 */
import java.util.*;

import net.sf.javabdd.*;

public class QueensLogic {
    private int rows = 0;
    private int cols = 0;
    private int[][] board;


    public QueensLogic() {
       //constructor
    }

    public void initializeGame(int size) {
        this.rows = size;
        this.cols = size;
        this.board = new int[rows][cols];
    }


    public int[][] getGameBoard() {
        return board;
    }

    private BDD initBDD(int size) {

        BDDFactory fact = JFactory.init(2000000, 200000);
        fact.setVarNum(size * size);


        BDD bdd = fact.one();
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                bdd = rowConstraint(fact, bdd, i, j, size);
            }
        }

        return bdd;

    }

    private BDD rowConstraint(BDDFactory fact, BDD bdd, int i, int j, int size) {
        BDD antecedent = fact.ithVar(i * size + j);
        BDD consequent = fact.one();

        for (int l = 0; l < size; l++) {
            if (l == j) continue;
            consequent = consequent.and(fact.nithVar(i * size + l));
        }
        BDD expr = antecedent.imp(consequent);
        return bdd.and(expr);
    }

    public boolean insertQueen(int column, int row) {

        if (board[column][row] == -1 || board[column][row] == 1) {
            return true;
        }

        board[column][row] = 1;

        // put some logic here..

        return true;
    }
}
