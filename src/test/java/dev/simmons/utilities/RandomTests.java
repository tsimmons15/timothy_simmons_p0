package dev.simmons.utilities;

import org.junit.jupiter.api.Test;
import org.postgresql.util.Base64;

import java.util.Random;

public class RandomTests {

    @Test
    public void stringyBytes() {
        byte[] bytes = new byte[16];
        Random rand = new Random();
        rand.nextBytes(bytes);

        for (int i = 0; i < bytes.length; i++) {
            bytes[i] *= ((bytes[i] < 0) ? -1 : 1);
        }

        String stringbytes = Base64.encodeBytes(bytes);

        byte[] rectified = Base64.decode(stringbytes);
    }
}
