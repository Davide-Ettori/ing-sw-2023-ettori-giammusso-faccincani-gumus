package objective.common_objective;

import app.model.Algo_CO_12;
import app.model.Card;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static app.model.Color.*;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
/**
 * <p>
 * class that test the twelfth algorithm, five columns of increasing height or descending:
 * starting from the first column left or right, each successive column it must be formed by an extra tile.
 * Tiles can be of any type.
 * <p>
 * test which are true are #1 #3 #5 #7
 * <p>
 * test which are false are #2 #4 #6 #8 #9 #10
 * @author Faccincani, Ettori , Gumus
 */
public class Algo12Test {
    Algo_CO_12 algoCo12 = null;

    Card[][] mat = new Card[6][5];

    @Before // eseguita prima dei test
    public void setUp() {
        this.algoCo12 = new Algo_CO_12();
    }
    @After // eseguita dopo i test
    public void tearDown() {
        return;
    }

    /**
     * <table border="1">
     * <tr><td>&nbsp;&nbsp;&nbsp;</td><td>&nbsp;&nbsp;&nbsp;</td><td>&nbsp;&nbsp;&nbsp;</td><td>&nbsp;&nbsp;&nbsp;</td><td>&nbsp;&nbsp;&nbsp;</td></tr>
     * <tr><td>&nbsp;&nbsp;&nbsp;</td><td>&nbsp;&nbsp;&nbsp;</td><td>&nbsp;&nbsp;&nbsp;</td><td>&nbsp;&nbsp;&nbsp;</td><td>P</td></tr>
     * <tr><td>&nbsp;&nbsp;&nbsp;</td><td>&nbsp;&nbsp;&nbsp;</td><td>&nbsp;&nbsp;&nbsp;</td><td>P</td><td>P</td></tr>
     * <tr><td>&nbsp;&nbsp;&nbsp;</td><td>&nbsp;&nbsp;&nbsp;</td><td>P</td><td>P</td><td>P</td></tr>
     * <tr><td>&nbsp;&nbsp;&nbsp;</td><td>P</td><td>P</td><td>P</td><td>P</td></tr>
     * <tr><td>P</td><td>P</td><td>P</td><td>P</td><td>P</td></tr>
     * </table>
     */
    @Test // test 1
    public void algo12_test1_general_decrescent() {
        mat[0][0] = new Card();
        mat[0][1] = new Card();
        mat[0][2] = new Card();
        mat[0][3] = new Card();
        mat[0][4] = new Card();

        mat[1][0] = new Card();
        mat[1][1] = new Card();
        mat[1][2] = new Card();
        mat[1][3] = new Card();
        mat[1][4] = new Card(PINK);

        mat[2][0] = new Card();
        mat[2][1] = new Card();
        mat[2][2] = new Card();
        mat[2][3] = new Card(PINK);
        mat[2][4] = new Card(PINK);

        mat[3][0] = new Card();
        mat[3][1] = new Card();
        mat[3][2] = new Card(PINK);
        mat[3][3] = new Card(PINK);
        mat[3][4] = new Card(PINK);

        mat[4][0] = new Card();
        mat[4][1] = new Card(PINK);
        mat[4][2] = new Card(PINK);
        mat[4][3] = new Card(PINK);
        mat[4][4] = new Card(PINK);

        mat[5][0] = new Card(PINK);
        mat[5][1] = new Card(PINK);
        mat[5][2] = new Card(PINK);
        mat[5][3] = new Card(PINK);
        mat[5][4] = new Card(PINK);

        assertTrue(algoCo12.checkMatch(mat));
    }
    /**
     * <table border="1">
     * <tr><td>&nbsp;&nbsp;&nbsp;</td><td>&nbsp;&nbsp;&nbsp;</td><td>&nbsp;&nbsp;&nbsp;</td><td>&nbsp;&nbsp;&nbsp;</td><td>&nbsp;&nbsp;&nbsp;</td></tr>
     * <tr><td>&nbsp;&nbsp;&nbsp;</td><td>&nbsp;&nbsp;&nbsp;</td><td>&nbsp;&nbsp;&nbsp;</td><td>&nbsp;&nbsp;&nbsp;</td><td>P</td></tr>
     * <tr><td>&nbsp;&nbsp;&nbsp;</td><td>&nbsp;&nbsp;&nbsp;</td><td>&nbsp;&nbsp;&nbsp;</td><td>P</td><td>P</td></tr>
     * <tr><td>&nbsp;&nbsp;&nbsp;</td><td>&nbsp;&nbsp;&nbsp;</td><td>&nbsp;&nbsp;&nbsp;</td><td>P</td><td>P</td></tr>
     * <tr><td>&nbsp;&nbsp;&nbsp;</td><td>P</td><td>P</td><td>P</td><td>P</td></tr>
     * <tr><td>P</td><td>P</td><td>P</td><td>P</td><td>P</td></tr>
     * </table>
     */
    @Test // test 1
    public void algo12_test2_general_F_decrescent() {
        mat[0][0] = new Card();
        mat[0][1] = new Card();
        mat[0][2] = new Card();
        mat[0][3] = new Card();
        mat[0][4] = new Card();

        mat[1][0] = new Card();
        mat[1][1] = new Card();
        mat[1][2] = new Card();
        mat[1][3] = new Card();
        mat[1][4] = new Card(PINK);

        mat[2][0] = new Card();
        mat[2][1] = new Card();
        mat[2][2] = new Card();
        mat[2][3] = new Card(PINK);
        mat[2][4] = new Card(PINK);

        mat[3][0] = new Card();
        mat[3][1] = new Card();
        mat[3][2] = new Card();
        mat[3][3] = new Card(PINK);
        mat[3][4] = new Card(PINK);

        mat[4][0] = new Card();
        mat[4][1] = new Card(PINK);
        mat[4][2] = new Card(PINK);
        mat[4][3] = new Card(PINK);
        mat[4][4] = new Card(PINK);

        mat[5][0] = new Card(PINK);
        mat[5][1] = new Card(PINK);
        mat[5][2] = new Card(PINK);
        mat[5][3] = new Card(PINK);
        mat[5][4] = new Card(PINK);

        assertFalse(algoCo12.checkMatch(mat));
    }

    /**
     * <table border="1">
     * <tr><td>&nbsp;&nbsp;&nbsp;</td><td>&nbsp;&nbsp;&nbsp;</td><td>&nbsp;&nbsp;&nbsp;</td><td>&nbsp;&nbsp;&nbsp;</td><td>W</td></tr>
     * <tr><td>&nbsp;&nbsp;&nbsp;</td><td>&nbsp;&nbsp;&nbsp;</td><td>&nbsp;&nbsp;&nbsp;</td><td>W</td><td>P</td></tr>
     * <tr><td>&nbsp;&nbsp;&nbsp;</td><td>&nbsp;&nbsp;&nbsp;</td><td>W</td><td>P</td><td>P</td></tr>
     * <tr><td>&nbsp;&nbsp;&nbsp;</td><td>W</td><td>P</td><td>P</td><td>P</td></tr>
     * <tr><td>W</td><td>P</td><td>P</td><td>P</td><td>P</td></tr>
     * <tr><td>P</td><td>P</td><td>P</td><td>P</td><td>P</td></tr>
     * </table>
     */
    @Test // test 1
    public void algo12_test3_general_digdecrescent() {
        mat[0][0] = new Card();
        mat[0][1] = new Card();
        mat[0][2] = new Card();
        mat[0][3] = new Card();
        mat[0][4] = new Card(WHITE);

        mat[1][0] = new Card();
        mat[1][1] = new Card();
        mat[1][2] = new Card();
        mat[1][3] = new Card(WHITE);
        mat[1][4] = new Card(PINK);

        mat[2][0] = new Card();
        mat[2][1] = new Card();
        mat[2][2] = new Card(WHITE);
        mat[2][3] = new Card(PINK);
        mat[2][4] = new Card(PINK);

        mat[3][0] = new Card();
        mat[3][1] = new Card(WHITE);
        mat[3][2] = new Card(PINK);
        mat[3][3] = new Card(PINK);
        mat[3][4] = new Card(PINK);

        mat[4][0] = new Card(WHITE);
        mat[4][1] = new Card(PINK);
        mat[4][2] = new Card(PINK);
        mat[4][3] = new Card(PINK);
        mat[4][4] = new Card(PINK);

        mat[5][0] = new Card(PINK);
        mat[5][1] = new Card(PINK);
        mat[5][2] = new Card(PINK);
        mat[5][3] = new Card(PINK);
        mat[5][4] = new Card(PINK);

        assertTrue(algoCo12.checkMatch(mat));
    }

    /**
     * <table border="1">
     * <tr><td>&nbsp;&nbsp;&nbsp;</td><td>&nbsp;&nbsp;&nbsp;</td><td>&nbsp;&nbsp;&nbsp;</td><td>P</td><td>W</td></tr>
     * <tr><td>&nbsp;&nbsp;&nbsp;</td><td>&nbsp;&nbsp;&nbsp;</td><td>&nbsp;&nbsp;&nbsp;</td><td>W</td><td>P</td></tr>
     * <tr><td>&nbsp;&nbsp;&nbsp;</td><td>&nbsp;&nbsp;&nbsp;</td><td>W</td><td>P</td><td>P</td></tr>
     * <tr><td>&nbsp;&nbsp;&nbsp;</td><td>W</td><td>P</td><td>P</td><td>P</td></tr>
     * <tr><td>W</td><td>P</td><td>P</td><td>P</td><td>P</td></tr>
     * <tr><td>P</td><td>P</td><td>P</td><td>P</td><td>P</td></tr>
     * </table>
     */
    @Test // test 1
    public void algo12_test4_general_F_digdecrescent() {
        mat[0][0] = new Card();
        mat[0][1] = new Card();
        mat[0][2] = new Card();
        mat[0][3] = new Card(PINK);
        mat[0][4] = new Card(WHITE);

        mat[1][0] = new Card();
        mat[1][1] = new Card();
        mat[1][2] = new Card();
        mat[1][3] = new Card(WHITE);
        mat[1][4] = new Card(PINK);

        mat[2][0] = new Card();
        mat[2][1] = new Card();
        mat[2][2] = new Card(WHITE);
        mat[2][3] = new Card(PINK);
        mat[2][4] = new Card(PINK);

        mat[3][0] = new Card();
        mat[3][1] = new Card(WHITE);
        mat[3][2] = new Card(PINK);
        mat[3][3] = new Card(PINK);
        mat[3][4] = new Card(PINK);

        mat[4][0] = new Card(WHITE);
        mat[4][1] = new Card(PINK);
        mat[4][2] = new Card(PINK);
        mat[4][3] = new Card(PINK);
        mat[4][4] = new Card(PINK);

        mat[5][0] = new Card(PINK);
        mat[5][1] = new Card(PINK);
        mat[5][2] = new Card(PINK);
        mat[5][3] = new Card(PINK);
        mat[5][4] = new Card(PINK);

        assertFalse(algoCo12.checkMatch(mat));
    }

    /**
     * <table border="1">
     * <tr><td>&nbsp;&nbsp;&nbsp;</td><td>&nbsp;&nbsp;&nbsp;</td><td>&nbsp;&nbsp;&nbsp;</td><td>&nbsp;&nbsp;&nbsp;</td><td>W</td></tr>
     * <tr><td>&nbsp;&nbsp;&nbsp;</td><td>&nbsp;&nbsp;&nbsp;</td><td>&nbsp;&nbsp;&nbsp;</td><td>W</td><td>B</td></tr>
     * <tr><td>&nbsp;&nbsp;&nbsp;</td><td>&nbsp;&nbsp;&nbsp;</td><td>W</td><td>W</td><td>P</td></tr>
     * <tr><td>&nbsp;&nbsp;&nbsp;</td><td>W</td><td>P</td><td>P</td><td>Y</td></tr>
     * <tr><td>W</td><td>P</td><td>P</td><td>P</td><td>P</td></tr>
     * <tr><td>P</td><td>P</td><td>P</td><td>P</td><td>P</td></tr>
     * </table>
     */
    @Test // test 1
    public void algo12_test5_general_crescent() {
        mat[0][0] = new Card();
        mat[0][1] = new Card();
        mat[0][2] = new Card();
        mat[0][3] = new Card();
        mat[0][4] = new Card(WHITE);

        mat[1][0] = new Card();
        mat[1][1] = new Card();
        mat[1][2] = new Card();
        mat[1][3] = new Card(WHITE);
        mat[1][4] = new Card(BLUE);

        mat[2][0] = new Card();
        mat[2][1] = new Card();
        mat[2][2] = new Card(WHITE);
        mat[2][3] = new Card(WHITE);
        mat[2][4] = new Card(PINK);

        mat[3][0] = new Card();
        mat[3][1] = new Card(WHITE);
        mat[3][2] = new Card(PINK);
        mat[3][3] = new Card(PINK);
        mat[3][4] = new Card(YELLOW);

        mat[4][0] = new Card(WHITE);
        mat[4][1] = new Card(PINK);
        mat[4][2] = new Card(PINK);
        mat[4][3] = new Card(PINK);
        mat[4][4] = new Card(PINK);

        mat[5][0] = new Card(PINK);
        mat[5][1] = new Card(PINK);
        mat[5][2] = new Card(PINK);
        mat[5][3] = new Card(PINK);
        mat[5][4] = new Card(PINK);

        assertTrue(algoCo12.checkMatch(mat));
    }

    /**
     * <table border="1">
     * <tr><td>&nbsp;&nbsp;&nbsp;</td><td>&nbsp;&nbsp;&nbsp;</td><td>&nbsp;&nbsp;&nbsp;</td><td>&nbsp;&nbsp;&nbsp;</td><td>W</td></tr>
     * <tr><td>&nbsp;&nbsp;&nbsp;</td><td>&nbsp;&nbsp;&nbsp;</td><td>&nbsp;&nbsp;&nbsp;</td><td>W</td><td>B</td></tr>
     * <tr><td>&nbsp;&nbsp;&nbsp;</td><td>G</td><td>W</td><td>W</td><td>P</td></tr>
     * <tr><td>&nbsp;&nbsp;&nbsp;</td><td>W</td><td>P</td><td>P</td><td>Y</td></tr>
     * <tr><td>W</td><td>P</td><td>P</td><td>P</td><td>P</td></tr>
     * <tr><td>P</td><td>P</td><td>P</td><td>P</td><td>P</td></tr>
     * </table>
     */
    @Test // test 1
    public void algo12_test6_general_F_crescent() {
        mat[0][0] = new Card();
        mat[0][1] = new Card();
        mat[0][2] = new Card();
        mat[0][3] = new Card();
        mat[0][4] = new Card(WHITE);

        mat[1][0] = new Card();
        mat[1][1] = new Card();
        mat[1][2] = new Card();
        mat[1][3] = new Card(WHITE);
        mat[1][4] = new Card(BLUE);

        mat[2][0] = new Card();
        mat[2][1] = new Card(GREEN);
        mat[2][2] = new Card(WHITE);
        mat[2][3] = new Card(WHITE);
        mat[2][4] = new Card(PINK);

        mat[3][0] = new Card();
        mat[3][1] = new Card(WHITE);
        mat[3][2] = new Card(PINK);
        mat[3][3] = new Card(PINK);
        mat[3][4] = new Card(YELLOW);

        mat[4][0] = new Card(WHITE);
        mat[4][1] = new Card(PINK);
        mat[4][2] = new Card(PINK);
        mat[4][3] = new Card(PINK);
        mat[4][4] = new Card(PINK);

        mat[5][0] = new Card(PINK);
        mat[5][1] = new Card(PINK);
        mat[5][2] = new Card(PINK);
        mat[5][3] = new Card(PINK);
        mat[5][4] = new Card(PINK);

        assertFalse(algoCo12.checkMatch(mat));
    }

    /**
     * <table border="1">
     * <tr><td>&nbsp;&nbsp;&nbsp;</td><td>&nbsp;&nbsp;&nbsp;</td><td>&nbsp;&nbsp;&nbsp;</td><td>&nbsp;&nbsp;&nbsp;</td><td>P</td></tr>
     * <tr><td>&nbsp;&nbsp;&nbsp;</td><td>&nbsp;&nbsp;&nbsp;</td><td>&nbsp;&nbsp;&nbsp;</td><td>P</td><td>B</td></tr>
     * <tr><td>&nbsp;&nbsp;&nbsp;</td><td>&nbsp;&nbsp;&nbsp;</td><td>P</td><td>W</td><td>P</td></tr>
     * <tr><td>&nbsp;&nbsp;&nbsp;</td><td>P</td><td>P</td><td>P</td><td>Y</td></tr>
     * <tr><td>P</td><td>P</td><td>P</td><td>P</td><td>P</td></tr>
     * <tr><td>P</td><td>P</td><td>P</td><td>P</td><td>P</td></tr>
     * </table>
     */
    @Test // test 1
    public void algo12_test7_general_digcrescent() {
        mat[0][0] = new Card();
        mat[0][1] = new Card();
        mat[0][2] = new Card();
        mat[0][3] = new Card();
        mat[0][4] = new Card(PINK);

        mat[1][0] = new Card();
        mat[1][1] = new Card();
        mat[1][2] = new Card();
        mat[1][3] = new Card(PINK);
        mat[1][4] = new Card(BLUE);

        mat[2][0] = new Card();
        mat[2][1] = new Card();
        mat[2][2] = new Card(PINK);
        mat[2][3] = new Card(WHITE);
        mat[2][4] = new Card(PINK);

        mat[3][0] = new Card();
        mat[3][1] = new Card(PINK);
        mat[3][2] = new Card(PINK);
        mat[3][3] = new Card(PINK);
        mat[3][4] = new Card(YELLOW);

        mat[4][0] = new Card(PINK);
        mat[4][1] = new Card(PINK);
        mat[4][2] = new Card(PINK);
        mat[4][3] = new Card(PINK);
        mat[4][4] = new Card(PINK);

        mat[5][0] = new Card(PINK);
        mat[5][1] = new Card(PINK);
        mat[5][2] = new Card(PINK);
        mat[5][3] = new Card(PINK);
        mat[5][4] = new Card(PINK);

        assertTrue(algoCo12.checkMatch(mat));
    }

    /**
     * <table border="1">
     * <tr><td>&nbsp;&nbsp;&nbsp;</td><td>G</td><td>&nbsp;&nbsp;&nbsp;</td><td>&nbsp;&nbsp;&nbsp;</td><td>P</td></tr>
     * <tr><td>&nbsp;&nbsp;&nbsp;</td><td>&nbsp;&nbsp;&nbsp;</td><td>&nbsp;&nbsp;&nbsp;</td><td>P</td><td>B</td></tr>
     * <tr><td>&nbsp;&nbsp;&nbsp;</td><td>&nbsp;&nbsp;&nbsp;</td><td>P</td><td>W</td><td>P</td></tr>
     * <tr><td>&nbsp;&nbsp;&nbsp;</td><td>P</td><td>P</td><td>P</td><td>Y</td></tr>
     * <tr><td>P</td><td>P</td><td>P</td><td>P</td><td>P</td></tr>
     * <tr><td>P</td><td>P</td><td>P</td><td>P</td><td>P</td></tr>
     * </table>
     */
    @Test // test 1
    public void algo12_test8_general_F_digcrescent(){
        mat[0][0] = new Card();
        mat[0][1] = new Card(GREEN);
        mat[0][2] = new Card();
        mat[0][3] = new Card();
        mat[0][4] = new Card(PINK);

        mat[1][0] = new Card();
        mat[1][1] = new Card();
        mat[1][2] = new Card();
        mat[1][3] = new Card(PINK);
        mat[1][4] = new Card(BLUE);

        mat[2][0] = new Card();
        mat[2][1] = new Card();
        mat[2][2] = new Card(PINK);
        mat[2][3] = new Card(WHITE);
        mat[2][4] = new Card(PINK);

        mat[3][0] = new Card();
        mat[3][1] = new Card(PINK);
        mat[3][2] = new Card(PINK);
        mat[3][3] = new Card(PINK);
        mat[3][4] = new Card(YELLOW);

        mat[4][0] = new Card(PINK);
        mat[4][1] = new Card(PINK);
        mat[4][2] = new Card(PINK);
        mat[4][3] = new Card(PINK);
        mat[4][4] = new Card(PINK);

        mat[5][0] = new Card(PINK);
        mat[5][1] = new Card(PINK);
        mat[5][2] = new Card(PINK);
        mat[5][3] = new Card(PINK);
        mat[5][4] = new Card(PINK);

        assertFalse(algoCo12.checkMatch(mat));
    }

    /**
     * <table border="1">
     * <tr><td>&nbsp;&nbsp;&nbsp;</td><td>&nbsp;&nbsp;&nbsp;</td><td>&nbsp;&nbsp;&nbsp;</td><td>&nbsp;&nbsp;&nbsp;</td><td>&nbsp;&nbsp;&nbsp;</td></tr>
     * <tr><td>&nbsp;&nbsp;&nbsp;</td><td>&nbsp;&nbsp;&nbsp;</td><td>&nbsp;&nbsp;&nbsp;</td><td>&nbsp;&nbsp;&nbsp;</td><td>&nbsp;&nbsp;&nbsp;</td></tr>
     * <tr><td>B</td><td>&nbsp;&nbsp;&nbsp;</td><td>&nbsp;&nbsp;&nbsp;</td><td>&nbsp;&nbsp;&nbsp;</td><td>&nbsp;&nbsp;&nbsp;</td></tr>
     * <tr><td>W</td><td>W</td><td>&nbsp;&nbsp;&nbsp;</td><td>&nbsp;&nbsp;&nbsp;</td><td>&nbsp;&nbsp;&nbsp;</td></tr>
     * <tr><td>B</td><td>P</td><td>P</td><td>&nbsp;&nbsp;&nbsp;</td><td>&nbsp;&nbsp;&nbsp;</td></tr>
     * <tr><td>P</td><td>P</td><td>P</td><td>P</td><td>&nbsp;&nbsp;&nbsp;</td></tr>
     * </table>
     */
    @Test // QUESTO TEST PUO ESSERE SBAGLIATO IN BASE ALL'INTERPRETAZIONE DELL'OBIETTIVO COMUNE-->CHIEDERE PROF
    public void algo12_test9_general_dicrescent2(){
        mat[0][0] = new Card();
        mat[0][1] = new Card();
        mat[0][2] = new Card();
        mat[0][3] = new Card();
        mat[0][4] = new Card();

        mat[1][0] = new Card();
        mat[1][1] = new Card();
        mat[1][2] = new Card();
        mat[1][3] = new Card();
        mat[1][4] = new Card();

        mat[2][0] = new Card(BLUE);
        mat[2][1] = new Card();
        mat[2][2] = new Card();
        mat[2][3] = new Card();
        mat[2][4] = new Card();

        mat[3][0] = new Card(WHITE);
        mat[3][1] = new Card(WHITE);
        mat[3][2] = new Card();
        mat[3][3] = new Card();
        mat[3][4] = new Card();

        mat[4][0] = new Card(BLUE);
        mat[4][1] = new Card(PINK);
        mat[4][2] = new Card(PINK);
        mat[4][3] = new Card();
        mat[4][4] = new Card();

        mat[5][0] = new Card(PINK);
        mat[5][1] = new Card(PINK);
        mat[5][2] = new Card(PINK);
        mat[5][3] = new Card(PINK);
        mat[5][4] = new Card();

        assertFalse(algoCo12.checkMatch(mat));
    }

    /**
     * <table border="1">
     * <tr><td>&nbsp;&nbsp;&nbsp;</td><td>&nbsp;&nbsp;&nbsp;</td><td>&nbsp;&nbsp;&nbsp;</td><td>&nbsp;&nbsp;&nbsp;</td><td>&nbsp;&nbsp;&nbsp;</td></tr>
     * <tr><td>&nbsp;&nbsp;&nbsp;</td><td>&nbsp;&nbsp;&nbsp;</td><td>&nbsp;&nbsp;&nbsp;</td><td>&nbsp;&nbsp;&nbsp;</td><td>&nbsp;&nbsp;&nbsp;</td></tr>
     * <tr><td>&nbsp;&nbsp;&nbsp;</td><td>&nbsp;&nbsp;&nbsp;</td><td>&nbsp;&nbsp;&nbsp;</td><td>&nbsp;&nbsp;&nbsp;</td><td>&nbsp;&nbsp;&nbsp;</td></tr>
     * <tr><td>W</td><td>&nbsp;&nbsp;&nbsp;</td><td>&nbsp;&nbsp;&nbsp;</td><td>&nbsp;&nbsp;&nbsp;</td><td>&nbsp;&nbsp;&nbsp;</td></tr>
     * <tr><td>B</td><td>P</td><td>&nbsp;&nbsp;&nbsp;</td><td>&nbsp;&nbsp;&nbsp;</td><td>&nbsp;&nbsp;&nbsp;</td></tr>
     * <tr><td>P</td><td>P</td><td>P</td><td>&nbsp;&nbsp;&nbsp;</td><td>&nbsp;&nbsp;&nbsp;</td></tr>
     * </table>
     */
    @Test //falso in ogni interpretazione
    public void algo12_test10_general_dicrescent3(){
        mat[0][0] = new Card();
        mat[0][1] = new Card();
        mat[0][2] = new Card();
        mat[0][3] = new Card();
        mat[0][4] = new Card();

        mat[1][0] = new Card();
        mat[1][1] = new Card();
        mat[1][2] = new Card();
        mat[1][3] = new Card();
        mat[1][4] = new Card();

        mat[2][0] = new Card();
        mat[2][1] = new Card();
        mat[2][2] = new Card();
        mat[2][3] = new Card();
        mat[2][4] = new Card();

        mat[3][0] = new Card(WHITE);
        mat[3][1] = new Card();
        mat[3][2] = new Card();
        mat[3][3] = new Card();
        mat[3][4] = new Card();

        mat[4][0] = new Card(BLUE);
        mat[4][1] = new Card(PINK);
        mat[4][2] = new Card();
        mat[4][3] = new Card();
        mat[4][4] = new Card();

        mat[5][0] = new Card(PINK);
        mat[5][1] = new Card(PINK);
        mat[5][2] = new Card(PINK);
        mat[5][3] = new Card();
        mat[5][4] = new Card();

        assertFalse(algoCo12.checkMatch(mat));
    }
}
