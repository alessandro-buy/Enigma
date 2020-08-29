package enigma;

import java.util.Collection;
import java.util.HashSet;

import static enigma.EnigmaException.*;

/** Class that represents a complete enigma machine.
 *  @author Alessandro Buy
 */
class Machine {


    /** A new Enigma machine with alphabet ALPHA, 1 < NUMROTORS rotor slots,
     *  and 0 <= PAWLS < NUMROTORS pawls.  ALLROTORS contains all the
     *  available rotors. */
    Machine(Alphabet alpha, int numRotors, int pawls,
            Collection<Rotor> allRotors) {
        _alphabet = alpha;
        _numRotors = numRotors;
        _pawls = pawls;
        _slots = new Rotor[numRotors];
        _rotors = new HashSet<>(allRotors);
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
        int mrotors = 0;
        for (int i = 0; i < _numRotors; i++) {
            String name = rotors[i];
            for (Rotor r : _rotors) {
                if (r.name().toUpperCase().equals(name.toUpperCase())) {
                    _slots[i] = r;
                    r.set(0);
                    if (r.rotates()) {
                        mrotors += 1;
                    }
                    break;
                }
            }
        }
        if (mrotors != numPawls()) {
            throw error("number of moving rotors does not match pawls");
        }
    }

    /** A function that clears all the rotors for a new line. */
    void clearRotors() {
        _slots = new Rotor[numRotors()];
    }

    /** Set my rotors according to SETTING, which must be a string of
     *  numRotors()-1 upper-case letters. The first letter refers to the
     *  leftmost rotor setting (not counting the reflector).  */
    void setRotors(String setting) {
        for (int i = 0; i < setting.length(); i++) {
            String n = _slots[i + 1].name();
            _slots[i + 1].set(setting.charAt(i));
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
        advanceMachine();
        int temp = 0;
        if (_plugboard != null) {
            int size = _alphabet.size();
            char out = _plugboard.permute(_alphabet.toChar(c % size));
            temp = _alphabet.toInt(out);
        } else {
            temp = c;
        }
        for (int j = numRotors() - 1; j >= 1; j--) {
            temp = _slots[j].convertForward(temp);
        }
        temp = _slots[0].convertForward(temp);
        for (int i = 1; i < numRotors(); i++) {
            temp = _slots[i].convertBackward(temp);
        }
        if (_plugboard != null) {
            temp = _plugboard.invert(temp);
        }
        return temp;
    }


    /**
     * AdvanceMachine advances the rotors that must me advanced in a machine.
     */
    void advanceMachine() {
        boolean[] needs2move = new boolean[numRotors()];
        needs2move[0] = false;
        needs2move[numRotors() - 1] = true;
        for (int i = numRotors() - numPawls(); i < numRotors() - 1; i++) {
            if (_slots[i].rotates() && _slots[i + 1].atNotch()) {
                needs2move[i + 1] = true;
                needs2move[i] = true;
            }
        }
        for (int i = 0; i < numRotors(); i++) {
            if (needs2move[i]) {
                _slots[i].advance();
            }
        }
    }

    /** Returns the encoding/decoding of MSG, updating the state of
     *  the rotors accordingly. */
    String convert(String msg) {
        char[] ret = new char[msg.length()];
        for (int i = 0; i < msg.length(); i++) {
            char c = msg.charAt(i);
            int j = _alphabet.toInt(c);
            ret[i] = _alphabet.toChar(convert(j));
        }
        String retval = new String(ret);
        return retval;
    }

    /** Common alphabet of my rotors. */
    private final Alphabet _alphabet;

    /** The number of rotors in a Machine. */
    private int _numRotors;

    /** The number of pawls in a given machine. */
    private int _pawls;

    /** The rotors of a given machine. */
    private Rotor[] _slots;

    /** The plugboard of a Machine. */
    private Permutation _plugboard;

    /** All the rotors available to the machine. */
    private Collection<Rotor> _rotors;

}
