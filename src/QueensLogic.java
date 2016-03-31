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
    private int size = 0;
    private int[][] board;

    private BDD bdd = null;
    private BDDFactory fact = null;

    public QueensLogic() {
       //constructor
    }

    public void initializeGame(int size) {
        // column indexed
        int columns = size, rows = size;
        this.board = new int[columns][rows];
        this.fact = JFactory.init(2000000, 200000);
        this.bdd = initBDD(size, fact);
    }


    public int[][] getGameBoard() {
        return board;
    }

    private BDD initBDD(int size, BDDFactory fact) {

        fact.setVarNum(size * size);

        BDD bdd = fact.one();
        for (int c = 0; c < size; c++) {
            for (int r = 0; r < size; r++) {
                bdd = rowConstraint(fact, bdd, c, r, size);
            }
        }

        return bdd;

    }

    private BDD rowConstraint(BDDFactory fact, BDD bdd, int c, int r, int size) {
        BDD antecedent = fact.ithVar(c * size + r);
        BDD consequent = fact.one();

        for (int l = 0; l < size; l++) {
            if (l == c) continue;
            consequent = consequent.and(fact.nithVar(l * size + r));
        }
        BDD expr = antecedent.imp(consequent);
        return bdd.and(expr);
    }

    public boolean insertQueen(int column, int row) {

        if (board[column][row] == -1 || board[column][row] == 1) {
            return true;
        }

        bdd = bdd.restrict(fact.ithVar(column * size + row).biimp(fact.one()));

        board[column][row] = 1;

        // put some logic here..

        return true;
    }
}
