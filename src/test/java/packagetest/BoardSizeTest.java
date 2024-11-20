package packagetest;

import com.example.BoardSize;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

public class BoardSizeTest {

    @ParameterizedTest
    @ValueSource(strings = {"9x9", "13x13", "19x19"})
    public void testFromString(String size) {
        assertNotNull(BoardSize.fromString(size));
    }

    @Test
    public void testFailingFromString() {
        assertThrows(IllegalArgumentException.class, () -> BoardSize.fromString("5x5"));
    }

    @Test
    public void testToString() {
        assertEquals("9x9", BoardSize.NINE.toString());
        assertEquals("13x13", BoardSize.THIRTEEN.toString());
        assertEquals("19x19", BoardSize.NINETEEN.toString());
    }

    @Test
    public void testGetSize() {
        assertEquals(9, BoardSize.NINE.getSize());
        assertEquals(13, BoardSize.THIRTEEN.getSize());
        assertEquals(19, BoardSize.NINETEEN.getSize());
    }

    @Test
    public void testGetStringValues() {
        String[] expected = {"9x9", "13x13", "19x19"};
        assertArrayEquals(expected, BoardSize.getStringValues());
    }
}
