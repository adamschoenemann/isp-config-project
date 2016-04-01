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

    private boolean fillRestOfBoard = true;

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
                bdd = sw_neConstraint(consequent, antecendent, bdd, c, r, size);
                bdd = nw_seConstraint(consequent, antecendent, bdd, c, r, size);
                bdd = bdd.and(allQueensConstraint());
            }
        }

        return bdd;

    }

    private BDD sw_neConstraint(BDD consequent, BDD antecedent, BDD bdd, int c, int r, int size) {

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

    private BDD nw_seConstraint(BDD consequent, BDD antecedent, BDD bdd, int c, int r, int size) {

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

    private BDD allQueensConstraint() {
        BDD constraint = fact.one();
        for (int c = 0; c < size; c++) {
            BDD mustHaveQueen = fact.zero();
            for (int r = 0; r < size; r++) {
                mustHaveQueen = mustHaveQueen.or(fact.ithVar(c * size + r));
            }
            constraint = constraint.and(mustHaveQueen);
        }
        return constraint;
    }

    private void placeForcedQueens(){

      for (int c = 0; c < size; c++) {

          int sum = 0;
          int whereZero = 0;

          for (int r = 0; r < size; r++) {

              // skip row if there is already a queen in it
              if(board[c][r] == 1){
                break;
              }

              // count how many fields are empty
              if(board[c][r] == 0){
                whereZero = r;
                sum++;
              }

          }

          // place queen where it is only one choice left
          if(sum == 1){
            board[c][whereZero] = 1;
          }

      }

    }

    public boolean insertQueen(int column, int row) {

        if (board[column][row] == -1 || board[column][row] == 1) {
            return true;
        }

        bdd = bdd.restrict(fact.ithVar(column * size + row).biimp(fact.one()));
        // System.out.println(bdd.isZero());

        for (int c = 0; c < size; c++) {
            for (int r = 0; r < size; r++) {
                int index = c * size + r;
                // can this be optimized? Is there a variable assignment method?
                BDD withQueenHere = bdd.restrict(fact.ithVar(c * size + r).biimp(fact.one()));

                if (board[c][r] == 0 && withQueenHere.isZero()) {
                    board[c][r] = -1;
                }
            }
        }

        board[column][row] = 1;

        if(fillRestOfBoard){
          placeForcedQueens();
        }

        boolean rememberMe = true;

        for (int c = 0; c < size; c++) {

            for (int r = 0; r < size; r++) {

                if (board[c][r] != 0 && rememberMe){
                  rememberMe = true;
                }
                else {
                  rememberMe = false;
                }
            }
        }

        if (rememberMe){
          System.out.println("Done!");
        }


        return true;
    }
}
