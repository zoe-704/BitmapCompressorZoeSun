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

import java.util.function.ObjIntConsumer;

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

    /**
     * Reads a sequence of bits from standard input, compresses them,
     * and writes the results to standard output.
     */
    public static void compress() {
        boolean cur = false; // Assume sequence starts with 0
        int count = 0;
        // Iterate over entire sequence of bits to compress
        while (!BinaryStdIn.isEmpty()) {
            boolean bit = BinaryStdIn.readBoolean();
            if (bit == cur) { // Part of current sequence
                count++;
                // Only using 8 bits to write out run length encoding
                if (count == 255) {
                    BinaryStdOut.write(count, 8);
                    count = 0;
                    cur = !cur; // Toggle between 0 and 1
                }
            } else { // Begin new sequence
                BinaryStdOut.write(count,8);
                count = 1; // Current bit is first one of new sequence
                cur = !cur;
            }
        }
        BinaryStdOut.write(count, 8);
        BinaryStdOut.close();
    }

    /**
     * Reads a sequence of bits from standard input, decodes it,
     * and writes the results to standard output.
     */
    public static void expand() {
        boolean cur = false; // Assume sequence starts with 0
        // Iterate over entire sequence of bits to expand
        while (!BinaryStdIn.isEmpty()) {
            // 8 bit run length encoding to write out n bits of cur
            int cur_length = BinaryStdIn.readByte();
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