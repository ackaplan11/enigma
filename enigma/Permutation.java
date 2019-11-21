package enigma;

import java.util.HashMap;
import static enigma.EnigmaException.*;

/** Represents a permutation of a range of integers starting at 0 corresponding
 *  to the characters of an alphabet.
 *  @author Andrew Kaplan
 */
class Permutation {

    /** Cycles Instance.*/
    private String _cycles;
    /** Permutation Map Instance.*/
    private HashMap<Integer, Integer> _permMap = new HashMap<>();
    /** Inverse Map Instance.*/
    private HashMap<Integer, Integer> _invMap = new HashMap<>();

    /** Set this Permutation to that specified by CYCLES, a string in the
     *  form "(cccc) (cc) ..." where the c's are characters in ALPHABET, which
     *  is interpreted as a permutation in cycle notation.  Characters in the
     *  alphabet that are not included in any cycle map to themselves.
     *  Whitespace is ignored. */
    Permutation(String cycles, Alphabet alphabet) {
        _alphabet = alphabet;
        _cycles = cycles;

        String c1 = _cycles.replaceAll("\\s", "");
        String c2 = c1.replace("(", "");
        String c3 = c2.replaceAll("\\)", ".");
        String[] parsedCycles = c3.split("\\.");

        for (int i = 0; i < parsedCycles.length; i += 1) {
            if (parsedCycles.length > 1 && parsedCycles[i].length() == 0) {
                throw EnigmaException.error
                        ("Cycle chars invalid");
            }
            addCycle(parsedCycles[i]);
        }
    }

    /** Add the cycle c0->c1->...->cm->c0 to the permutation, where CYCLE is
     *  c0c1...cm. */
    void addCycle(String cycle) {
        for (int j = 0; j < cycle.length(); j += 1) {
            if (!_alphabet.contains(cycle.charAt(j))) {
                throw EnigmaException.error
                        ("Cycle chars invalid");
            }
        }

        char[] charAR = cycle.toCharArray();
        int len = charAR.length - 1;

        if (charAR.length > 1) {
            _permMap.put(_alphabet.toInt(charAR[0]),
                    _alphabet.toInt(charAR[1]));
            _permMap.put(_alphabet.toInt(charAR[len]),
                    _alphabet.toInt(charAR[0]));
            _invMap.put(_alphabet.toInt(charAR[0]),
                    _alphabet.toInt(charAR[len]));
            _invMap.put(_alphabet.toInt(charAR[len]),
                    _alphabet.toInt(charAR[len - 1]));
        }

        for (int i = 1; i < len; i += 1) {
            int keyInt = _alphabet.toInt(charAR[i]);
            _permMap.put(keyInt, _alphabet.toInt(charAR[i + 1]));
            _invMap.put(keyInt, _alphabet.toInt(charAR[i - 1]));
        }
    }

    /** Return the value of P modulo the size of this permutation. */
    final int wrap(int p) {
        int r = p % size();
        if (r < 0) {
            r += size();
        }
        return r;
    }

    /** Returns the size of the alphabet I permute. */
    int size() {
        return _alphabet.size();
    }

    /** Return the result of applying this permutation to P modulo the
     *  alphabet size. */
    int permute(int p) {
        p = wrap(p);
        if (_permMap.containsKey(p)) {
            return (_permMap.get(p));
        }
        return p;
    }

    /** Return the result of applying the inverse of this permutation
     *  to  C modulo the alphabet size. */
    int invert(int c) {
        c = wrap(c);
        if (_invMap.containsKey(c)) {
            return _invMap.get(c);
        }
        return c;
    }

    /** Return the result of applying this permutation to the index of P
     *  in ALPHABET, and converting the result to a character of ALPHABET. */
    char permute(char p) {
        int i = wrap(_alphabet.toInt(p));
        if (_permMap.containsKey(i)) {
            return _alphabet.toChar(_permMap.get(i));
        }
        return p;
    }

    /** Return the result of applying the inverse of this permutation to C. */
    char invert(char c) {
        int i = wrap(_alphabet.toInt(c));
        if (_invMap.containsKey(i)) {
            return _alphabet.toChar(_invMap.get(i));
        }
        return c;
    }

    /** Return the alphabet used to initialize this Permutation. */
    Alphabet alphabet() {
        return _alphabet;
    }

    /** Return true iff this permutation is a derangement (i.e., a
     *  permutation for which no value maps to itself). */
    boolean derangement() {
        return _permMap.size() == _alphabet.size();
    }

    /** Alphabet of this permutation. */
    private Alphabet _alphabet;
}
