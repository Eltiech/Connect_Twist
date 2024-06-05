import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class BoardTest {

    private PieceColor[][] testSlots1= {{PieceColor.NONE,PieceColor.NONE,PieceColor.NONE,PieceColor.NONE,PieceColor.NONE,PieceColor.NONE,PieceColor.NONE},
            {PieceColor.NONE,PieceColor.NONE,PieceColor.NONE,PieceColor.NONE,PieceColor.NONE,PieceColor.NONE,PieceColor.NONE},
            {PieceColor.NONE,PieceColor.NONE,PieceColor.NONE,PieceColor.NONE,PieceColor.NONE,PieceColor.NONE,PieceColor.NONE},
            {PieceColor.NONE,PieceColor.NONE,PieceColor.RED,PieceColor.YELLOW,PieceColor.RED,PieceColor.NONE,PieceColor.NONE},
            {PieceColor.NONE,PieceColor.RED,PieceColor.RED,PieceColor.RED,PieceColor.YELLOW,PieceColor.RED,PieceColor.NONE},
            {PieceColor.RED,PieceColor.YELLOW,PieceColor.RED,PieceColor.YELLOW,PieceColor.YELLOW,PieceColor.YELLOW,PieceColor.RED}};
    private PieceColor[][] testSlotsFull = {{PieceColor.YELLOW,PieceColor.RED,PieceColor.YELLOW,PieceColor.YELLOW,PieceColor.RED,PieceColor.RED,PieceColor.YELLOW},
            {PieceColor.RED,PieceColor.YELLOW,PieceColor.YELLOW,PieceColor.RED,PieceColor.RED,PieceColor.RED,PieceColor.RED},
            {PieceColor.RED,PieceColor.YELLOW,PieceColor.RED,PieceColor.RED,PieceColor.YELLOW,PieceColor.YELLOW,PieceColor.YELLOW},
            {PieceColor.RED,PieceColor.YELLOW,PieceColor.RED,PieceColor.YELLOW,PieceColor.RED,PieceColor.YELLOW,PieceColor.RED},
            {PieceColor.RED,PieceColor.RED,PieceColor.RED,PieceColor.RED,PieceColor.YELLOW,PieceColor.RED,PieceColor.YELLOW},
            {PieceColor.RED,PieceColor.YELLOW,PieceColor.RED,PieceColor.YELLOW,PieceColor.YELLOW,PieceColor.YELLOW,PieceColor.RED}};
    private Player player1;
    private Player player2;
    private Board testBoard1;
    private Board testBoardFull;
    BoardTest() {
        player1 = new Player("p1test",PieceColor.RED,PlayerNumber.PLAYER_1);
        player2 = new Player("p2test",PieceColor.YELLOW,PlayerNumber.PLAYER_2);
        testBoard1 = new Board(testSlots1, (byte)4);
        testBoardFull = new Board(testSlotsFull, (byte)4);
    }
    @Test
    void getRows() {
        assertEquals(6, testBoard1.getRows());
    }

    @Test
    void getCols() {
        assertEquals(7, testBoard1.getCols());
    }

    @Test
    void isFull() {
        assertFalse(testBoard1.isFull());
        assertTrue(testBoardFull.isFull());
    }

    //tests that two sets are found when red player places a piece in column 3
    @Test
    void addPiece() {
        testBoard1.addPiece(player1,(byte)3);
        assertEquals(2, player1.getSets().size());
    }

    @Test
    void getPiece() {
        assertEquals(PieceColor.YELLOW, testBoard1.getPiece(new Coord((byte)1,(byte)5)));
    }
}