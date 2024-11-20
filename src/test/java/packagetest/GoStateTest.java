package packagetest;

import com.example.BoardSpace;
import com.example.GoState;
import com.example.Point;
import com.example.Stone;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ArgumentsSource;
import org.junit.jupiter.params.provider.CsvSource;

import java.io.File;

import static org.junit.jupiter.api.Assertions.*;

public class GoStateTest {

    @Test
    public void testBoardInitialization() {
        GoState state = new GoState(9);
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                assertEquals(BoardSpace.EMPTY, state.board[i][j]);
            }
        }
        assertEquals(0, state.getBlackCaptured());
        assertEquals(0, state.getWhiteCaptured());
        assertEquals(Stone.BLACK, state.getTurn());
    }

//    @ParameterizedTest
//    @ArgumentsSource(NeighborArgumentsProvider.class)
//    public void testGetNeighbors(Point point, Point[] expectedNeighbors) {
//        GoState state = new GoState(9);
//        Point[] neighbors = state.getNeighbors(point);
//        assertArrayEquals(expectedNeighbors, neighbors);
//    }

    @ParameterizedTest
    @CsvSource({
            "0, 0",   // Valid move
            "8, 8",   // Valid move
            "9, 9"    // Out of bounds
    })
    public void testIsLegalMove(int x, int y) {
        GoState state = new GoState(9);
        Point p = new Point(x, y);
        if (x < 9 && y < 9) {
            assertTrue(state.isLegalMove(p));
        } else {
            assertFalse(state.isLegalMove(p));
        }
    }

    @Test
    public void testIllegalMoveOnNonEmptySpace() {
        GoState state = new GoState(9);
        state.placeStone(new Point(0, 0));
        assertFalse(state.isLegalMove(new Point(0, 0)));
    }

    @Test
    public void testCheckCapture() {
        GoState state = new GoState(9);
        state.placeStone(new Point(1, 0));
        state.placeStone(new Point(0, 1));
        state.placeStone(new Point(1, 2));
        state.placeStone(new Point(2, 1));
        state.placeStone(new Point(1, 1)); // Capture

        assertEquals(BoardSpace.EMPTY, state.board[1][1]);
        assertEquals(1, state.getWhiteCaptured());
    }

    @Test
    public void testSaveLoadGame() {
        GoState state = new GoState(9);
        state.placeStone(new Point(0, 0));

        File file = new File("testGame.go");
        state.saveGame(file);

        GoState loadedState = GoState.loadGame(file);
        assertEquals(state.toString(), loadedState.toString());
    }

    @Test
    public void testDoublePassEndsGame() {
        GoState state = new GoState(9);
        state.makeMove(null); // First pass
        boolean isGameOver = state.makeMove(null); // Second pass

        assertTrue(isGameOver);
    }
}

