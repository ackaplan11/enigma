package enigma;

import org.junit.Test;
import static enigma.TestUtils.*;
import static org.junit.Assert.assertEquals;

public class RotorTest {

    private Rotor rotor;
    private String alpha = UPPER_STRING;
    private Alphabet alphabet = new Alphabet(alpha);
    private Permutation permID = new Permutation("", alphabet);
    private Permutation permSimple = new Permutation("(ABC)", alphabet);
    private Permutation permComplex =
            new Permutation("(ACEGI)(BDF)(JK)", alphabet);
    private Permutation permDerangment =
            new Permutation("(ABCDEFGHIJKLMNOPQRSTUVWXYZ)", alphabet);

    /** Check that rotor has an alphabet whose size is that of
     *  FROMALPHA and TOALPHA and that maps each character of
     *  FROMALPHA to the corresponding character of FROMALPHA, and
     *  vice-versa. TESTID is used in error messages. */
    private void checkRotor(String testId,
                            String fromAlpha, String toAlpha) {
        int N = fromAlpha.length();
        assertEquals(testId + " (wrong length)", N, rotor.size());
        for (int i = 0; i < N; i += 1) {
            char c = fromAlpha.charAt(i), e = toAlpha.charAt(i);
            int ci = alphabet.toInt(c), ei = alphabet.toInt(e);
            assertEquals(msg(testId, "wrong translation of %d (%c)", ci, c),
                    ei, rotor.convertForward(ci));
            assertEquals(msg(testId, "wrong inverse of %d (%c)", ei, e),
                    ci, rotor.convertBackward(ei));
        }
    }
    /** Set the rotor to the one with given NAME and PERMUTATION*/
    private void setRotor(String name, Permutation perm) {
        rotor = new Rotor(name, perm);
    }

    @Test
    public void checkIDRotor() {
        setRotor("ID",  permID);
        checkRotor("ID Rotor Test", UPPER_STRING, UPPER_STRING);
    }

    @Test
    public void checkSimpleRotor() {
        setRotor("Simple",  permSimple);
        checkRotor("Simple Rotor Test", UPPER_STRING,
                "BCADEFGHIJKLMNOPQRSTUVWXYZ");
        checkRotor("Simple Rotor Test",
                "BCDEFGHIJKLMNOPQRSTUVWXYZA",
                "CADEFGHIJKLMNOPQRSTUVWXYZB");
    }

    @Test
    public void checkComplexRotor() {
        setRotor("Complex",  permComplex);
        checkRotor("Complex Rotor Test", UPPER_STRING,
                "CDEFGBIHAKJLMNOPQRSTUVWXYZ");
        checkRotor("Complex Rotor Test",
                "BCDEFGHIJKLMNOPQRSTUVWXYZA",
                "DEFGBIHAKJLMNOPQRSTUVWXYZC");

    }

    @Test
    public void checkDerangementRotor() {
        setRotor("Derangement",  permDerangment);
        checkRotor("Derangement Rotor Test",
                UPPER_STRING,
                "BCDEFGHIJKLMNOPQRSTUVWXYZA");
    }
}
