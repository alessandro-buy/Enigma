package enigma;

import static enigma.EnigmaException.*;

/** Class that represents a rotor that has no ratchet and does not advance.
 *  @author Alessandro Buy
 */
class FixedRotor extends Rotor {

    /** A non-moving rotor named NAME whose permutation at the 0 setting
     * is given by PERM. */
    FixedRotor(String name, Permutation perm) {
        super(name, perm);
    }

    @Override
    int convertForward(int p) {
        int input = (setting() + p) % alphabet().size();
        int perm = permutation().permute(input);
        int res = (perm - setting() + alphabet().size()) % alphabet().size();
        return res;
    }

    @Override
    int convertBackward(int e) {
        int input = (setting() + e) % alphabet().size();
        int perm = permutation().invert(input);
        int res = (perm - setting() + alphabet().size()) % alphabet().size();
        return res;
    }
}
