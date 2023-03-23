package app.model;

import java.util.ArrayList;

public class Algo_CO_9 extends Strategy { // quinto prima colonna
    @Override
    public boolean checkMatch(Card[][] board) {
        int count = 0;
        ArrayList<Color> colors;
        for (int j = 0; j < COLS; j++) {
            colors = new ArrayList<>();
            for (int i = 0; i < ROWS; i++) {
                if (!colors.contains(board[i][j].color))
                    colors.add(board[i][j].color);
            }
            if (colors.size() <= 3)
                count++;
        }
        return count >= 3;
    }
}