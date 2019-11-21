package enigma;

import org.junit.Test;
import org.junit.Rule;
import org.junit.rules.Timeout;
import static org.junit.Assert.*;

import static enigma.TestUtils.*;

/** The suite of all JUnit tests for the Permutation class.
 *  @author
 */
public class PermutationTest {

    /** Testing time limit. */
    @Rule
    public Timeout globalTimeout = Timeout.seconds(5);

    /* ***** TESTING UTILITIES ***** */

    private Permutation perm;
    private String alpha = UPPER_STRING;

    /** Check that perm has an alphabet whose size is that of
     *  FROMALPHA and TOALPHA and that maps each character of
     *  FROMALPHA to the corresponding character of FROMALPHA, and
     *  vice-versa. TESTID is used in error messages. */
    private void checkPerm(String testId,
                           String fromAlpha, String toAlpha) {
        int N = fromAlpha.length();
        assertEquals(testId + " (wrong length)", N, perm.size());
        for (int i = 0; i < N; i += 1) {
            char c = fromAlpha.charAt(i), e = toAlpha.charAt(i);
            assertEquals(msg(testId, "wrong translation of '%c'", c),
                         e, perm.permute(c));
            assertEquals(msg(testId, "wrong inverse of '%c'", e),
                         c, perm.invert(e));
            int ci = alpha.indexOf(c), ei = alpha.indexOf(e);
            assertEquals(msg(testId, "wrong translation of %d", ci),
                    ei, perm.permute(ci));
            assertEquals(msg(testId, "wrong inverse of %d", ei),
                    ci, perm.invert(ei));

        }
    }

    /* ***** TESTS ***** */

    @Test
    public void checkIdTransform() {
        perm = new Permutation("", UPPER);
        checkPerm("identity", UPPER_STRING, UPPER_STRING);
    }

    @Test
    public void checkWhiteSpace() {
        perm = new Permutation("(AZ)    (CD)", UPPER);
        checkPerm("White Space", UPPER_STRING,
                "ZBDCEFGHIJKLMNOPQRSTUVWXYA");
    }

    @Test
    public void checkPermSimple() {
        perm = new Permutation("(ABC)", UPPER);
        checkPerm("Simple Permutation", UPPER_STRING,
                "BCADEFGHIJKLMNOPQRSTUVWXYZ");
    }

    @Test
    public void checkPermMulti() {
        perm = new Permutation("(CSB)(DAT)", UPPER);
        checkPerm("Multiple Permutation", UPPER_STRING,
                "TCSAEFGHIJKLMNOPQRBDUVWXYZ");
    }

    @Test (expected = EnigmaException.class)
    public void checkInvalidCycles() {
        perm = new Permutation("(ZXC))(A)", UPPER);
    }

    @Test (expected = EnigmaException.class)
    public void checkInvalidCycles2() {
        perm = new Permutation("(ZXC)(1)", UPPER);
    }

    @Test
    public void checkDerangement() {
        perm = new Permutation("(ZXC)", UPPER);
        assertFalse(perm.derangement());
        perm = new Permutation("(BCDEFGHIJKLMNOPQRSTUVWXYZA)", UPPER);
        assertTrue(perm.derangement());
    }
}
