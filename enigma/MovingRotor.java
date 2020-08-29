package enigma;

import java.util.ArrayList;
import java.util.List;

import static enigma.EnigmaException.*;

/** Class that represents a rotating rotor in the enigma machine.
 *  @author Alessandro Buy
 */
class MovingRotor extends Rotor {

    /** A rotor named NAME whose permutation in its default setting is
     *  PERM, and whose notches are at the positions indicated in NOTCHES.
     *  The Rotor is initially in its 0 setting (first character of its
     *  alphabet).
     */
    MovingRotor(String name, Permutation perm, String notches) {
        super(name, perm);
        _notches = new ArrayList<>();
        for (int i = 0; i < notches.length(); i++) {
            _notches.add(notches.charAt(i));
        }
        _permutation = perm;
    }

    @Override
    int convertForward(int p) {
        int input = (setting() + p) % alphabet().size();
        int perm = _permutation.permute(input);
        int res = (perm - setting() + alphabet().size()) % alphabet().size();
        return res;
    }

    @Override
    int convertBackward(int e) {
        int input = (setting() + e) % alphabet().size();
        int perm = _permutation.invert(input);
        int res = (perm - setting() + alphabet().size()) % alphabet().size();
        return res;
    }

    @Override
    void advance() {
        int r = (setting() + 1) % alphabet().size();
        set(r);
    }

    @Override
    boolean rotates() {
        return true;
    }

    @Override
    boolean atNotch() {
        return _notches.contains(alphabet().toChar(setting()));
    }

    /** A list of characters representing the notches passed to us. */
    private List<Character> _notches;

    /** The permutation implemented by this rotor in its 0 position. */
    private Permutation _permutation;

}
