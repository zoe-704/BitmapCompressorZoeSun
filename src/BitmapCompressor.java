/******************************************************************************
 *  Compilation:  javac BitmapCompressor.java
 *  Execution:    java BitmapCompressor - < input.bin   (compress)
 *  Execution:    java BitmapCompressor + < input.bin   (expand)
 *  Dependencies: BinaryIn.java BinaryOut.java
 *  Data files:   q32x48.bin
 *                q64x96.bin
 *                mystery.bin
 *
 *  Compress or expand binary input from standard input.
 *
 *  % java DumpBinary 0 < mystery.bin
 *  8000 bits
 *
 *  % java BitmapCompressor - < mystery.bin | java DumpBinary 0
 *  1240 bits
 ******************************************************************************/

/**
 *  The {@code BitmapCompressor} class provides static methods for compressing
 *  and expanding a binary bitmap input.
 *
 *  @author Robert Sedgewick
 *  @author Kevin Wayne
 *  @author Zach Blick
 *  @author Zoe Sun
 */
public class BitmapCompressor {
    // bit sizes of compressed sequences for various values of BYTES
    // 4: 816. 3048
    // 5: 792, 2008 (2800)
    // 6: 920, 1832 (2752)

    // Length of encoded sequences
    public static int BYTES = 5;
    public static int BITS = (int) Math.pow(2,BYTES)-1;
    /**
     * Reads a sequence of bits from standard input, compresses them,
     * and writes the results to standard output.
     */
    public static void compress() {
        boolean cur = false; // Assume sequence starts with 0
        int count = 0;

        // Temporary storage of length of 0/1 sequences
        java.util.ArrayList<Integer> seq = new java.util.ArrayList<>();

        // Iterate over entire sequence of bits to compress
        while (!BinaryStdIn.isEmpty()) {
            boolean bit = BinaryStdIn.readBoolean();
            if (bit == cur) { // Part of current sequence
                count++;
                // Only using 8 bits to write out run length encoding
                if (count == BITS) {
                    seq.add(count);
                    count = 0;
                    cur = !cur;
                }
            } else { // Begin new sequence
                seq.add(count);
                count = 1; // Current bit is first one of new sequence
                cur = !cur;
            }
        }
        seq.add(count); // Add final sequence
        int encodedBits = seq.size() * BYTES;

        // 32 bit int header of encoded bits
        BinaryStdOut.write(encodedBits, 32);

        // Write out all sequence lengths
        for (int s : seq) {
            BinaryStdOut.write(s, BYTES);
        }
        BinaryStdOut.close();
    }

    /**
     * Reads a sequence of bits from standard input, decodes it,
     * and writes the results to standard output.
     */
    public static void expand() {
        boolean cur = false; // Assume sequence starts with 0

        // Read in header of the number of encoded bits
        int encodedBits = BinaryStdIn.readInt(32);

        // Iterate over entire sequence of bits to expand
        for (int f = 0; f < encodedBits; f+= BYTES) {
            // Get length of 0/1 sequence
            int cur_length = BinaryStdIn.readInt(BYTES);
            for (int i = 0; i < cur_length; i++) {
                BinaryStdOut.write(cur); // 0 or 1
            }
            cur = !cur; // Toggle between 0 and 1
        }
        BinaryStdOut.close();
    }

    /**
     * When executed at the command-line, run {@code compress()} if the command-line
     * argument is "-" and {@code expand()} if it is "+".
     *
     * @param args the command-line arguments
     */
    public static void main(String[] args) {
        if      (args[0].equals("-")) compress();
        else if (args[0].equals("+")) expand();
        else throw new IllegalArgumentException("Illegal command line argument");
    }
}