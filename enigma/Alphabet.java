package enigma;

import static enigma.EnigmaException.*;

/** An alphabet of encodable characters.  Provides a mapping from characters
 *  to and from indices into the alphabet.
 *  @author Andrew Kaplan
 */
class Alphabet {

    /** Char Array instance used to store the alphabet string.*/
    private char[] _chars;

    /**
     * A new alphabet containing CHARS.  Character number #k has index
     * K (numbering from 0). No character may be duplicated.
     */
    Alphabet(String chars) {
        _chars = chars.toCharArray();
        for (int i = 0; i < size(); i += 1) {
            for (int j = i + 1; j < size(); j += 1) {
                if (_chars[i] == _chars[j]) {
                    throw EnigmaException.error
                            ("Alphabet may not have duplicate characters");
                }
            }
        }
    }

    /**
     * A default alphabet of all upper-case characters.
     */
    Alphabet() {
        this("ABCDEFGHIJKLMNOPQRSTUVWXYZ");
    }

    /**
     * Returns the size of the alphabet.
     */
    int size() {
        return _chars.length;
    }

    /**
     * Returns true if (CH) is in this alphabet.
     */
    boolean contains(char ch) {
        for (int i = 0; i < size(); i += 1) {
            if (ch == toChar(i)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Returns character number INDEX in the alphabet, where
     * 0 <= INDEX < size().
     */
    char toChar(int index) {
        return _chars[index];
    }

    /**
     * Returns the index of character (CH), which must be in
     * the alphabet. This is the inverse of toChar().
     */
    int toInt(char ch) {
        int found = -1;
        if (!contains(ch)) {
            throw EnigmaException.error("Char not in alphabet");
        }
        for (int i = 0; i < size(); i += 1) {
            if (_chars[i] == ch) {
                found = i;
                break;
            }
        }
        return found;
    }
}
