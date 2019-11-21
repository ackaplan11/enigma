package enigma;

import java.util.ArrayList;

import static enigma.EnigmaException.*;

/** Class that represents a rotating rotor in the enigma machine.
 *  @author Andrew Kaplan
 */
class MovingRotor extends Rotor {

    /** Notches Instance. */
    private char[] _notches;
    /** Notches corresponding integers. */
    private ArrayList<Integer> _notchInts = new ArrayList<>();

    /** A rotor named NAME whose permutation in its default setting is
     *  PERM, and whose notches are at the positions indicated in NOTCHES.
     *  The Rotor is initally in its 0 setting (first character of its
     *  alphabet).
     */
    MovingRotor(String name, Permutation perm, String notches) {
        super(name, perm);
        _notches = notches.toCharArray();
        for (int i = 0; i < _notches.length; i += 1) {
            char c = _notches[i];
            if (!alphabet().contains(c)) {
                throw EnigmaException.error
                        ("Notch not valid");
            }
            _notchInts.add(alphabet().toInt(c));
        }
    }

    @Override
    void advance() {
        set(permutation().wrap(setting() + 1));
    }

    @Override
    boolean atNotch() {
        boolean atNotch = false;
        for (int i : _notchInts) {
            if (i == setting()) {
                atNotch = true;
                break;
            }
        }
        return atNotch;
    }

    @Override
    boolean rotates() {
        return true;
    }
}
