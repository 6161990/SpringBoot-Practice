package productImporter;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

public class ListPriceFilter_specs {

    @Test
    void sut_implements_ProductValidator(){
    }

    @ParameterizedTest
    @CsvSource(value = {"100000, 90000, false", "100000, 100000, true", "100000, 110000, true"})
    void sut_correctly_works(int lowerBound, int listPrice, boolean expected){

    }
}
