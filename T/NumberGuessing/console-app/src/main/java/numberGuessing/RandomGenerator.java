package numberGuessing;

import java.util.Random;

public class RandomGenerator implements PositiveIntegerGenerator {

    private Random random = new Random();

    @Override
    public int generateLessThanEqualToHundred() {
        return random.nextInt(100) + 1;
    }
}