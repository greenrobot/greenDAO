package de.greenrobot.daotest.performance;

import java.util.Random;

/**
 * Helper class to generate a pre-determined set of random strings.
 */
public class StringGenerator {

    // Fixed seed so we generate the same set of strings every time.
    public static final long SEED = -2662502316022774L;
    private static final int MIN_LENGTH = 5;
    private static final int MAX_LENGTH = 500;
    // limit to a fixed set of chars
    private static final char[] CHARS = new char[]{'0', '1', '2', '3', '4', '5', '6', '7', '8',
            '9', 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p',
            'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z'};

    /**
     * Creates the same random sequence of strings.
     */
    public static String[] createFixedRandomStrings(int count) {
        String[] strings = new String[count];

        Random lengthRandom = new Random();
        lengthRandom.setSeed(SEED);

        Random stringRandom = new Random();
        stringRandom.setSeed(SEED);

        for (int i = 0; i < count; i++) {
            int nextLength = MIN_LENGTH + lengthRandom.nextInt(MAX_LENGTH - MIN_LENGTH - 1);
            char[] chars = new char[nextLength];
            for (int j = 0; j < nextLength; j++) {
                chars[j] = CHARS[stringRandom.nextInt(CHARS.length)];
            }
            strings[i] = new String(chars);
        }
        return strings;
    }

    /**
     * Creates the same random sequence of indexes. To be used to select strings by {@link
     * #createFixedRandomStrings(int)}.
     */
    public static int[] getFixedRandomIndices(int count, int maxIndex) {
        int[] indices = new int[count];

        Random random = new Random();
        random.setSeed(StringGenerator.SEED);

        for (int i = 0; i < count; i++) {
            indices[i] = random.nextInt(maxIndex + 1);
        }

        return indices;
    }
}
