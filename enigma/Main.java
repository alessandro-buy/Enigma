package enigma;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.util.HashSet;
import java.util.Scanner;
import java.util.StringTokenizer;
import java.util.Set;
import java.util.NoSuchElementException;

import static enigma.EnigmaException.*;

/** Enigma simulator.
 *  @author Alessandro Buy
 */
public final class Main {

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
        Machine M = readConfig();
        try {
            String star = _input.next();
            String line = _input.nextLine();
            setUp(M, line);
            String nxt = "";
            while (_input.hasNextLine()) {
                nxt = _input.nextLine() + "\n";
                if (nxt.length() != 0 && !nxt.substring(0, 1).equals("*")) {
                    nxt = nxt.replaceAll("\\s", "");
                    nxt = nxt.toUpperCase().trim();
                    String rv = M.convert(nxt);
                    printMessageLine(rv);
                } else {
                    setUp(M, nxt.substring(1));
                }
            }
        } catch (NoSuchElementException excp) {
            throw error("configuration file truncated");
        }
    }

    /** Return an Enigma machine configured from the contents of configuration
     *  file _config. */
    private Machine readConfig() {
        try {
            String s = _config.next();
            if (!Character.isAlphabetic(s.charAt(0))
                    || !Character.isAlphabetic(s.charAt(2))) {
                throw error("Bad alphabet spec");
            }
            _alphabet = new CharacterRange(s.charAt(0), s.charAt(2));
            int rotors = _config.nextInt();
            int pawls = _config.nextInt();
            Set<Rotor> slots = new HashSet<>();
            while (_config.hasNext()) {
                slots.add(readRotor());
            }
            return new Machine(_alphabet, rotors, pawls, slots);
        } catch (NoSuchElementException excp) {
            throw error("configuration file truncated");
        }
    }

    /** Return a rotor, reading its description from _config. */
    private Rotor readRotor() {
        try {
            String name;
            if (_nextname != null) {
                name = _nextname;
                _nextname = null;
            } else {
                name = _config.next();
            }
            String mobility = _config.next();
            String permutations = "";
            while (_config.hasNext()) {
                String perm = _config.next();
                if (perm.charAt(0) == '(') {
                    permutations = permutations + perm;
                } else {
                    _nextname = perm;
                    break;
                }
            }
            Permutation p = new Permutation(permutations, _alphabet);
            Rotor retvalue;

            if (mobility.charAt(0) == 'M') {
                retvalue = new MovingRotor(name, p, mobility.substring(1));
            } else if (mobility.charAt((0)) == 'N') {
                retvalue = new FixedRotor(name, p);
            } else if (mobility.charAt(0) == 'R') {
                retvalue = new Reflector(name, p);
            } else {
                return null;
            }
            return retvalue;
        } catch (NoSuchElementException excp) {
            throw error("bad rotor description");
        }
    }

    /** Set M according to the specification given on SETTINGS,
     *  which must have the format specified in the assignment. */
    private void setUp(Machine M, String settings) {
        StringTokenizer st = new StringTokenizer(settings);
        String[] names = new String[M.numRotors()];
        try {
            for (int i = 0; i < M.numRotors(); i++) {
                names[i] = st.nextToken();
            }
        } catch (NoSuchElementException e) {
            throw error("not enough rotor names in file");
        }
        M.clearRotors();
        M.insertRotors(names);
        try {
            String set = st.nextToken();
            M.setRotors(set);
        } catch (NoSuchElementException e) {
            throw error("missing initial rotor setting");
        }
        String pb = "";
        try {
            while (st.hasMoreTokens()) {
                pb = pb + st.nextToken();
            }
        } catch (NoSuchElementException e) {
            throw error("bad plugboard");
        }
        Permutation plug = new Permutation(pb, _alphabet);
        M.setPlugboard(plug);
    }


    /** Print MSG in groups of five (except that the last group may
     *  have fewer letters). */
    private void printMessageLine(String msg) {
        for (int i = 0; i < msg.length(); i = i + 5) {
            if (i > 0) {
                _output.print(" ");
            }
            String out = msg.substring(i,
                    (i + 5 <= msg.length()) ? i + 5 : msg.length());
            _output.print(out);
        }
        _output.print("\n");
    }


    /** Alphabet used in this machine. */
    private Alphabet _alphabet;

    /** Source of input messages. */
    private Scanner _input;

    /** Source of machine configuration. */
    private Scanner _config;

    /** File for encoded/decoded messages. */
    private PrintStream _output;

    /** The name of the next String. */
    private String _nextname;
}
