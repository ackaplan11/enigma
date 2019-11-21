package enigma;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;

import java.util.ArrayList;
import java.util.NoSuchElementException;
import java.util.Scanner;

import static enigma.EnigmaException.*;

/** Enigma simulator.
 *  @author Andrew Kaplan
 */
public final class Main {

    /** Enigma machine instance. */
    private Machine _enigma;

    /** Process a sequence of encryptions and decryptions, as
     *  specified by ARGS, where 1 <= ARGS.length <= 3.
     *  ARGS[0] is the name of a configuration file.
     *  ARGS[1] is optional; when present, it names an input file
     *  containing messages.  Otherwise, input comes from the standard
     *  input.  ARGS[2] is optional; when present, it names an output
     *  file for processed messages.  Otherwise, output goes to the
     *  standard output. Exits normally if there are no errors in the input;
     *  otherwise with code 1. */
    public static void main(String... args) {
        try {
            new Main(args).process();
            return;
        } catch (EnigmaException excp) {
            System.err.printf("Error: %s%n", excp.getMessage());
        }
        System.exit(1);
    }

    /** Check ARGS and open the necessary files (see comment on main). */
    Main(String[] args) {
        if (args.length < 1 || args.length > 3) {
            throw error("Only 1, 2, or 3 command-line arguments allowed");
        }

        _config = getInput(args[0]);

        if (args.length > 1) {
            _input = getInput(args[1]);
        } else {
            _input = new Scanner(System.in);
        }

        if (args.length > 2) {
            _output = getOutput(args[2]);
        } else {
            _output = System.out;
        }
    }

    /** Return a Scanner reading from the file named NAME. */
    private Scanner getInput(String name) {
        try {
            return new Scanner(new File(name));
        } catch (IOException excp) {
            throw error("could not open %s", name);
        }
    }

    /** Return a PrintStream writing to the file named NAME. */
    private PrintStream getOutput(String name) {
        try {
            return new PrintStream(new File(name));
        } catch (IOException excp) {
            throw error("could not open %s", name);
        }
    }

    /** Configure an Enigma machine from the contents of configuration
     *  file _config and apply it to the messages in _input, sending the
     *  results to _output. */
    private void process() {
        _enigma = readConfig();
        if (!_input.hasNext("\\*")) {
            throw EnigmaException.error
                    ("Invalid input file");
        }
        while (_input.hasNext()) {
            if (_input.hasNext("\\*")) {
                String settingLine = _input.nextLine();
                if (settingLine.equals("")) {
                    System.out.println(settingLine);
                } else {
                    setUp(_enigma, settingLine);
                }
            } else {
                printMessageLine(_input.nextLine());
            }
        }
        while (_input.hasNextLine()) {
            System.out.print(_input.nextLine());
        }

    }

    /** Return an Enigma machine configured from the contents of configuration
     *  file _config. */
    private Machine readConfig() {
        int numRotors = 0;
        int pawls = 0;
        ArrayList<Rotor> allRotors = new ArrayList<>();
        try {
            while (_config.hasNext()) {
                _config.hasNext("(\\s?)+([\\w\\.]+)");
                String alpha = _config.next("(\\s?)+([\\w\\.]+)");
                _alphabet = new Alphabet(alpha);
                _config.hasNext("(\\s?)+(\\d)");
                String numR = _config.next("(\\s?)+(\\d)");
                numRotors = Integer.parseInt(numR);
                _config.hasNext("(\\s?)+(\\d)");
                String numP = _config.next("(\\s?)+(\\d)");
                pawls = Integer.parseInt(numP);
                while (_config.hasNext()) {
                    allRotors.add(readRotor());
                }
            }
            _config.close();
        } catch (NoSuchElementException excp) {
            throw error("configuration file truncated");
        }
        return new Machine(_alphabet, numRotors, pawls, allRotors);
    }

    /** Return a rotor, reading its description from _config. */
    private Rotor readRotor() {
        String name = "noName";
        String notches = "noNotches";
        Permutation perm;
        StringBuilder cycles = new StringBuilder();
        char type = '0';
        Rotor r;

        try {
            while (_config.hasNext()) {
                if (_config.hasNext("(\\s?)+([\\w\\.]+)")) {
                    if (name.equals("noName")) {
                        name = _config.next("(\\s?)+([\\w\\.]+)");
                        name = name.replaceAll("\\s", "");
                    } else if (type == '0') {
                        String s = _config.next("(\\s?)+([\\w\\.]+)");
                        s = s.replaceAll("\\s", "");
                        type = s.charAt(0);
                        if (type == 'M') {
                            notches = s.substring(1);
                        } else if (s.length() > 1) {
                            throw EnigmaException.error
                                    ("Fixed Rotors cannot have notches");
                        }
                    } else {
                        break;
                    }
                } else if (_config.hasNext
                        ("((\\s?)+(\\([\\w\\.]+\\))(\\s?)+)+")) {
                    cycles.append(_config.next
                            ("((\\s?)+(\\([\\w\\.]+\\))(\\s?)+)+"));

                }
            }
        } catch (NoSuchElementException excp) {
            throw error("bad rotor description");
        }
        perm = new Permutation(cycles.toString(), _alphabet);
        if (type == 'M') {
            r  = new MovingRotor(name, perm, notches);
        } else if (type == 'N') {
            r  = new FixedRotor(name, perm);
        } else if (type == 'R') {
            r  = new Reflector(name, perm);
        } else {
            throw EnigmaException.error
                    ("Cannot initialize rotor " + type);
        }
        return r;
    }

    /** Set M according to the specification given on SETTINGS,
     *  which must have the format specified in the assignment. */
    private void setUp(Machine M, String settings) {
        String[] sets = settings.split("\\s");
        String[] activeRotors = new String[M.numRotors()];
        StringBuilder permString = new StringBuilder();
        Permutation perm;
        String rotorSetting = "";

        for (int i = 1; i < sets.length; i += 1) {
            if (i < M.numRotors() + 1) {
                activeRotors[i - 1] = sets[i];
            } else {
                if (sets[i].charAt(0) != '(' && i == M.numRotors() + 1) {
                    rotorSetting = sets[M.numRotors() + 1];
                } else {
                    permString.append(sets[i]);
                }

            }
        }
        for (char c: rotorSetting.toCharArray()) {
            if (!_alphabet.contains(c)) {
                throw EnigmaException.error("Settings invalid");
            }
        }

        M.insertRotors(activeRotors);
        M.setRotors(rotorSetting);
        perm = new Permutation(permString.toString(), _alphabet);
        M.setPlugboard(perm);
    }

    /** Print MSG in groups of five (except that the last group may
     *  have fewer letters). */
    private void printMessageLine(String msg) {
        String stripMsg = msg.replaceAll("\\s", "");
        StringBuilder encrypted = new StringBuilder();
        encrypted.append(_enigma.convert(stripMsg));
        int len = encrypted.length();
        int offset = 0;
        for (int i = 0; i < len; i += 1) {
            if (i % 5 == 0 && i > 0) {
                encrypted.insert(i + offset, " ");
                offset += 1;
            }
        }
        System.out.println(encrypted);
    }

    /** Alphabet used in this machine. */
    private Alphabet _alphabet;

    /** Source of input messages. */
    private Scanner _input;

    /** Source of machine configuration. */
    private Scanner _config;

    /** File for encoded/decoded messages. */
    private PrintStream _output;
}
