package enigma;

import org.junit.Test;
import java.util.ArrayList;
import static enigma.TestUtils.*;
import static org.junit.Assert.*;

public class MachineTest {

    private Permutation reflectPerm = new Permutation(UPPER_STRING, UPPER);
    private Permutation id = new Permutation("", UPPER);
    private Permutation simple = new Permutation("(ABCW)", UPPER);

    private Rotor reflector = new Reflector("reflect", reflectPerm);
    private Rotor idFixedRotor = new FixedRotor("idFixed", id);
    private Rotor simpleFixedRotor = new FixedRotor("simpleFixed", simple);
    private Rotor entranceRotor = new MovingRotor("entrance", id, UPPER_STRING);
    private Rotor idMovingRotor = new MovingRotor("idMoving", id, "Z");
    private Rotor simpleMovingRotor =
            new MovingRotor("simpleMoving", simple, "Z");


    private Machine errorMachine() {
        ArrayList<Rotor> rotors = new ArrayList<>();
        rotors.add(reflector);
        rotors.add(idFixedRotor);
        rotors.add(idFixedRotor);
        rotors.add(simpleFixedRotor);
        rotors.add(simpleMovingRotor);
        return new Machine(UPPER, 4, 1, rotors);
    }

    private Machine simpleMachine() {
        ArrayList<Rotor> rotors = new ArrayList<>();
        rotors.add(reflector);
        rotors.add(idFixedRotor);
        rotors.add(entranceRotor);
        return new Machine(UPPER, 3, 1, rotors);
    }

    @Test(expected = EnigmaException.class)
    public void errorMachineTest() {
        Machine errorMachine = errorMachine();
    }

    @Test
    public void simpleMachineTest() {
        Machine simpleMachine = simpleMachine();
        simpleMachine.setPlugboard(id);

        String enigmaMessage = simpleMachine.convert("A");
        String enigmaCheck = "A";
        assertEquals(enigmaCheck, enigmaMessage);
    }
}

