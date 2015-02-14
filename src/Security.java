
import java.util.Arrays;
import java.util.Random;

public class Security {

    static int[] primes = {2, 3, 5, 7, 11, 13, 17, 19, 23, 29, 31, 37, 41, 43, 47, 53, 59, 61, 67, 71, 73, 79, 83, 89, 97, 101, 103, 107, 109, 113, 127, 131, 137, 139, 149, 151, 157, 163, 167, 173, 179, 181, 191, 193, 197, 199, 211, 223, 227, 229, 233, 239, 241, 251, 257, 263, 269, 271, 277, 281, 283, 293, 307, 311, 313, 317, 331, 337, 347, 349, 353, 359, 367, 373, 379, 383, 389, 397, 401, 409, 419, 421, 431, 433, 439, 443, 449, 457, 461, 463, 467, 479, 487, 491, 499, 503, 509, 521, 523, 541, 547};

    public static String genKey(long... seed) {
        Random rand;
        if (seed.length > 1) {
            System.err.println("Invalid key generation attempt with seed: " + Arrays.toString(seed));
            return null;
        } else if (seed.length == 1) {
            rand = new Random(seed[0]);
        } else {
            rand = new Random();
        }
        int fA = (10 * (int) (rand.nextInt(10))) + (int) (rand.nextInt(10));
        int p1 = primes[fA];
        int p2 = primes[fA * p1 % primes.length];
        int n = p1 * p2;
        int k = n % 10000;
        int nfA = fA * k % 100;
        int cs = nfA * k % 1000;
        String key = String.format("%02d-%04d-%03d", nfA, k, cs);
        return key;
    }

    /**
     * Method to verify the key being used.
     * @param key - key used for encryption/decryption. Assumed to be in the form XX-XXXX-XXX, where the last three X's are
     * @return
     */
    public static boolean verify(String key) {
        String[] data = key.split("-");
        int nfA = Integer.parseInt(data[0]);
        int k = Integer.parseInt(data[1]);
        int cs = Integer.parseInt(data[2]);
        return cs == nfA * k % 1000;
    }

    public static String caesar(String message, int shift) {
        String newString = "";
        for (int i = 0; i < message.length(); i++) {
            newString += increment(message.charAt(i), shift);
        }
        return newString;
    }

    public static String reverseCaesar(String message, int shift) {
        String newString = "";
        for (int i = 0; i < message.length(); i++) {
            newString += decrement(message.charAt(i), shift);
        }
        return newString;
    }

    private static char decrement(char k, int s) {
        return k - s >= 32 ? (char) (k - s) : (char) (95 + k - s);
    }

    private static char increment(char k, int s) {
        return (char) (((k - 32 + s) % 95) + 32);

    }

    /**
     * Encryption algorithm
     * 1. Verify the key.
     * 2.
     * @param key - Key to use for encryption
     * @param value - Value to encrypt
     * @return
     */
    public static String encode(String key, String value) {
        String[] data = key.split("-");
        int nfA = Integer.parseInt(data[0]);
        int k = Integer.parseInt(data[1]);
        int s = k % nfA;

        return caesar(value, s);
    }

    /**
     * Decryption algorithm, essentially the reverse of the encryption algorithm
     * @param key - Key to use for decryption
     * @param value - Value to decrypt
     * @return
     */
    public static String decode(String key, String value) {
        String[] data = key.split("-");
        int nfA = Integer.parseInt(data[0]);
        int k = Integer.parseInt(data[1]);
        int s = k % nfA;

        return reverseCaesar(value, s);
    }
}
