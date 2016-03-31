/**
 * This class implements the logic behind the BDD for the n-queens problem
 * You should implement all the missing methods
 *
 * @author Stavros Amanatidis
 *
 */

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
        this.size = size;
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
                BDD antecendent = fact.ithVar(c * size + r);
                BDD consequent = fact.one();
                bdd = columnConstraint(consequent, antecendent, bdd, c, r, size);
                bdd = rowConstraint(consequent, antecendent, bdd, c, r, size);
                bdd = diagonal1Constraint(consequent, antecendent, bdd, c, r, size);
                bdd = diagonal2Constraint(consequent, antecendent, bdd, c, r, size);
            }
        }

        return bdd;

    }

    private BDD diagonal1Constraint(BDD consequent, BDD antecedent, BDD bdd, int c, int r, int size) {

        int minCol = c;
        int minRow = r;
        int start = minCol * size + minRow;
        while (minCol >= 0 && minRow >= 0) {
            start = minCol * size + minRow;
            minCol = minCol - 1;
            minRow = minRow - 1;
        }

        int maxCol = c;
        int maxRow = r;
        int end = maxCol * size + maxRow;
        while (!(maxCol > (size - 1)) && !(maxRow > (size - 1))) {
            end = maxCol * size + maxRow;
            maxCol = maxCol + 1;
            maxRow = maxRow + 1;
        }
        for (int i = start; i < end; i = i + (size + 1)) {
            if (i == c * size + r) continue;
            consequent = consequent.and(fact.nithVar(i));
        }
        BDD expr = antecedent.imp(consequent);
        return bdd.and(expr);
    }

    private BDD diagonal2Constraint(BDD consequent, BDD antecedent, BDD bdd, int c, int r, int size) {

        int minCol = c;
        int maxRow = r;
        int start = minCol * size + maxRow;
        while (minCol >= 0 && !(maxRow > (size - 1))) {
            start = minCol * size + maxRow;
            minCol = minCol - 1;
            maxRow = maxRow + 1;
        }

        int maxCol = c;
        int minRow = r;
        int end = maxCol * size + minRow;
        while (!(maxCol > (size - 1)) && minRow >= 0) {
            end = maxCol * size + minRow;
            maxCol = maxCol + 1;
            minRow = minRow - 1;
        }

        for (int i = start; i < end; i = i + (size - 1)) {
            if (i == c * size + r) continue;
            consequent = consequent.and(fact.nithVar(i));
        }
        BDD expr = antecedent.imp(consequent);
        return bdd.and(expr);
    }

    private BDD columnConstraint(BDD consequent, BDD antecedent, BDD bdd, int c, int r, int size) {

        int start = c * size;
        int end   = start + size;
        for (int l = start; l < end; l++) {
            if (l == start + r) continue;
            consequent = consequent.and(fact.nithVar(l));
        }
        BDD expr = antecedent.imp(consequent);
        return bdd.and(expr);
    }

    private BDD rowConstraint(BDD consequent, BDD antecedent, BDD bdd, int c, int r, int size) {

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

        for (int c = 0; c < size; c++) {
            for (int r = 0; r < size; r++) {
                BDD withQueenHere = bdd.restrict(fact.ithVar(c * size + r).biimp(fact.one()));
                if (board[c][r] == 0 && withQueenHere.isZero()) {
                    board[c][r] = -1;
                }
            }
        }

        board[column][row] = 1;

        // put some logic here..

        return true;
    }
}
