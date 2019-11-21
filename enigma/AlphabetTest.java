package enigma;

import org.junit.Test;
import static org.junit.Assert.*;

public class AlphabetTest {

    @Test
    public void testAlphabet() {
        char[] checkA = {'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K'};
        Alphabet alphabet = new Alphabet("ABCDEFGHIJK");
        assertEquals(2, alphabet.toInt('C'));
        assertEquals(checkA[0], alphabet.toChar(0));
    }

    @Test (expected = EnigmaException.class)
    public void testInvalidAlphabet() {
        Alphabet invalid = new Alphabet("ABCDEFGHIJKA");
    }
}
