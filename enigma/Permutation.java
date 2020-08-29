package enigma;
import java.util.HashMap;

import static enigma.EnigmaException.*;

/** Represents a permutation of a range of integers starting at 0 corresponding
 *  to the characters of an alphabet.
 *  @author Alessandro Buy
 */
class Permutation {

    /** Set this Permutation to that specified by CYCLES, a string in the
     *  form "(cccc) (cc) ..." where the c's are characters in ALPHABET, which
     *  is interpreted as a permutation in cycle notation.  Characters in the
     *  alphabet that are not included in any cycle map to themselves.
     *  Whitespace is ignored. */
    Permutation(String cycles, Alphabet alphabet) {
        _alphabet = alphabet;
        for (int i = 0; i < cycles.length(); i++) {
            if (cycles.charAt(i) == '(') {
                for (int j = i; j < cycles.length(); j++) {
                    if (cycles.charAt(j) == ')') {
                        addCycle(cycles.substring(i + 1, j));
                        i = j;
                        break;
                    } else {
                        if (j == cycles.length() - 1) {
                            throw error("No closing parenthesis");
                        }
                    }
                }
            }
        }
    }

    /** Add the cycle c0->c1->...->cm->c0 to the permutation, where CYCLE is
     *  c0c1...cm. */
    private void addCycle(String cycle) {
        for (int i = 0; i < cycle.length() - 1; i++) {
            _cypher.put(cycle.charAt(i), cycle.charAt(i + 1));
            _decypher.put(cycle.charAt(i + 1), cycle.charAt(i));
        }
        if (cycle.length() >= 1) {
            _cypher.put(cycle.charAt(cycle.length() - 1), cycle.charAt(0));
            _decypher.put(cycle.charAt(0), cycle.charAt(cycle.length() - 1));
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
        char car = _alphabet.toChar(p % _alphabet.size());
        char temp = _cypher.getOrDefault(car, car);
        int i = _alphabet.toInt(temp);
        return i;
    }

    /** Return the result of applying the inverse of this permutation
     *  to  C modulo the alphabet size. */
    int invert(int c) {
        char car = _alphabet.toChar(c % _alphabet.size());
        char temp = _decypher.getOrDefault(car, car);
        int i = _alphabet.toInt(temp);
        return i;
    }

    /** Return the result of applying this permutation to the index of P
     *  in ALPHABET, and converting the result to a character of ALPHABET. */
    char permute(char p) {
        return _cypher.getOrDefault(p, p);
    }

    /** Return the result of applying the inverse of this permutation to C. */
    char invert(char c) {
        return _decypher.getOrDefault(c, c);
    }

    /** Return the alphabet used to initialize this Permutation. */
    Alphabet alphabet() {
        return _alphabet;
    }

    /** Return true iff this permutation is a derangement (i.e., a
     *  permutation for which no value maps to itself). */
    boolean derangement() {
        for (int i = 0; i < _alphabet.size() - 1; i++) {
            if (!_cypher.containsKey(_alphabet.toChar(i))) {
                return false;
            }
        }
        return true;
    }

    /** Alphabet of this permutation. */
    private Alphabet _alphabet;

    /** A Hashmap storing the current value to the character it maps to. */
    private HashMap<Character, Character> _cypher = new HashMap<>();

    /** A HashMap storing the reverse value of what is mapped. */
    private HashMap<Character, Character> _decypher = new HashMap<>();
}
