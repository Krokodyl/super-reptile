package compression.algorithms;

import compression.LzAlgorithm;
import compression.REPEAT_ALGORITHM;

public class DefaultAlgorithm extends LzAlgorithm {

    public DefaultAlgorithm() {
        super(0x00, 0x01, REPEAT_ALGORITHM.REPEAT_ALGORITHM_SIZE_5BITS);
        headerSize = 2;
    }
}
