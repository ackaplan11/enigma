package enigma;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.Collection;


import static enigma.EnigmaException.*;

/** Class that represents a complete enigma machine.
 *  @author Andrew Kaplan
 */
class Machine {

    /** Number of rotors in machine instance. */
    private int _numRotors;
    /** Number of pawls in machine instance. */
    private int _pawls;
    /** HashMap instance mapping rotor names to rotor objects. */
    private HashMap<String, Rotor> _allRotors = new HashMap<>();
    /** String Array Instance of all the names of the rotors in _allRotors. */
    private String[] _rotorNames;
    /** String Array Instance of all the names of the rotors in _machine. */
    private String[] _activeRotorNames;
    /** Array Instance of the collection of rotors in the Machine. */
    private ArrayList<Rotor> _machine = new ArrayList<>();
    /** Plugboard permutation instance. */
    private Permutation _plugboard;
    /** Boolean array instance containing whether
     * the rotor will rotate during a given input. */
    private boolean[] _willRotate;

    /** A new Enigma machine with alphabet ALPHA, 1 < NUMROTORS rotor slots,
     *  and 0 <= PAWLS < NUMROTORS pawls.  ALLROTORS contains all the
     *  available rotors. */
    Machine(Alphabet alpha, int numRotors, int pawls,
            Collection<Rotor> allRotors) {
        _alphabet = alpha;
        _numRotors = numRotors;
        _pawls = pawls;
        if (_numRotors < 2 || _pawls < 0
                || _pawls > _numRotors - 1 || _numRotors > allRotors.size()) {
            throw EnigmaException.error
                    ("Machine Settings Invalid");
        }

        StringBuilder rotorNameBuilder = new StringBuilder();
        for (Rotor r : allRotors) {
            _allRotors.put(r.name(), r);
            int copies = 0;
            for (Rotor rCheck : allRotors) {
                if (rCheck == r) {
                    copies += 1;
                }
            }
            if (copies > 1) {
                throw EnigmaException.error
                        ("Duplicate Rotors Invalid");
            }
            String n = r.name() + ".";
            rotorNameBuilder.append(n);
        }

        String rotorNameString = rotorNameBuilder.toString();
        _rotorNames = rotorNameString.split("\\.");
    }

    /** Return Array containing names of the rotors in _allRotors. */
    String[] rotorNames() {
        return _rotorNames;
    }

    /** Return Array containing names of the rotors in _machines. */
    String[] activeRotorNames() {
        _activeRotorNames = new String[numRotors()];
        int i = 0;
        for (Rotor r: _machine) {
            _activeRotorNames[i] = r.name();
            i += 1;
        }
        return _activeRotorNames;
    }

    /** Return the number of rotor slots I have. */
    int numRotors() {
        return _numRotors;
    }

    /** Return the number pawls (and thus rotating rotors) I have. */
    int numPawls() {
        return _pawls;
    }

    /** Set my rotor slots to the rotors named ROTORS from my set of
     *  available rotors (ROTORS[0] names the reflector).
     *  Initially, all rotors are set at their 0 setting. */
    void insertRotors(String[] rotors) {
        _machine.clear();
        for (String rotorName : rotors) {
            if (!_allRotors.containsKey(rotorName)) {
                throw EnigmaException.error
                        ("Rotor not contained in _allRotors");
            }
            _machine.add(_allRotors.get(rotorName));
        }
        if (!_machine.get(0).reflecting()) {
            throw EnigmaException.error("First rotor must be reflector");
        } else if (!_machine.get(rotors.length - 1).rotates()) {
            throw EnigmaException.error("Last rotor must be moving");
        }
    }

    /** Set my rotors according to SETTING, which must be a string of
     *  numRotors()-1 characters in my alphabet. The first letter refers
     *  to the leftmost rotor setting (not counting the reflector).  */
    void setRotors(String setting) {
        if (setting.length() == 0) {
            StringBuilder noSetting = new StringBuilder();
            for (int i = 0; i < numRotors() - 1; i += 1) {
                noSetting.append('A');
            }
            setRotors(noSetting.toString());
        }
        if (setting.length() != _numRotors - 1) {
            throw EnigmaException.error
                    ("Invalid Settings, Wrong Number of Arguments");
        }

        char[] setArray = setting.toCharArray();
        int i = 0;
        for (Rotor r : _machine) {
            if (!r.reflecting()) {
                r.set(setArray[i]);
                i += 1;
            }
        }
    }

    /** Set the plugboard to PLUGBOARD. */
    void setPlugboard(Permutation plugboard) {
        _plugboard = plugboard;
    }

    /** Returns the result of converting the input character C (as an
     *  index in the range 0..alphabet size - 1), after first advancing
     *  the machine. */
    int convert(int c) {
        c = _plugboard.permute(c);
        for (int i = _machine.size() - 1; i >= 0; i -= 1) {
            c = _machine.get(i).convertForward(c);
        }
        for (int j = 1; j < _machine.size(); j += 1) {
            c = _machine.get(j).convertBackward(c);
        }
        c = _plugboard.invert(c);
        return c;
    }

    /** Returns the encoding/decoding of MSG, updating the state of
     *  the rotors accordingly. */
    String convert(String msg) {
        char[] message = msg.toCharArray();
        StringBuilder encrypted = new StringBuilder();
        _willRotate = new boolean[_numRotors];
        for (char c : message) {
            for (int r = _machine.size() - 1; r > 0; r -= 1) {
                Rotor current = _machine.get(r);
                Rotor nextLeft = _machine.get(r - 1);
                if (r == _machine.size() - 1) {
                    _willRotate[r] = true;
                }
                if (current.atNotch() && nextLeft.rotates()) {
                    _willRotate[r] = true;
                    _willRotate[r - 1] = true;
                }
            }
            for (int r = _machine.size() - 1; r > 0; r -= 1) {
                if (_willRotate[r]) {
                    _machine.get(r).advance();
                }
            }
            _willRotate = new boolean[_numRotors];
            if (!_alphabet.contains(c)) {
                throw EnigmaException.error
                        ("Character %s not in alphabet", c);
            } else {
                int encryptInt = convert(_alphabet.toInt(c));
                char encryptChar = _alphabet.toChar(encryptInt);
                encrypted.append(encryptChar);
            }
        }
        return encrypted.toString();
    }

    /** Common alphabet of my rotors. */
    private final Alphabet _alphabet;

}
