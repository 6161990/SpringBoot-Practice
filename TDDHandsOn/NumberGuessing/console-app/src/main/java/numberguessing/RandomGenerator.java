package numberguessing;

import java.util.Random;

public final class RandomGenerator implements PositiveIntegerGenerator {
    private final Random random = new Random();

    @Override
    public int generateLessThanOrEqualToHundred() {
        return random.nextInt(100) + 1;
    }
}
