package dev.akorovai.backend.product.converter;

import com.github.javafaker.Faker;
import dev.akorovai.backend.product.Gender;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
class StringToGenderConverterTest {

	@InjectMocks
	private StringToGenderConverter stringToGenderConverter;

	private Faker faker;

	@BeforeEach
	void setUp() {
		faker = new Faker();
	}

	@Test
	void testConvertValidGender() {
		// Arrange
		String maleInput = "Men";
		String femaleInput = "Women";
		String otherInput = "Unisex";

		// Act
		Gender maleResult = stringToGenderConverter.convert(maleInput);
		Gender femaleResult = stringToGenderConverter.convert(femaleInput);
		Gender otherResult = stringToGenderConverter.convert(otherInput);

		// Assert using AssertJ
		assertThat(maleResult).isEqualTo(Gender.MEN);
		assertThat(femaleResult).isEqualTo(Gender.WOMEN);
		assertThat(otherResult).isEqualTo(Gender.UNISEX);

		// Assert using Hamcrest
		assertThat(maleResult, is(Gender.MEN));
		assertThat(femaleResult, is(Gender.WOMEN));
		assertThat(otherResult, is(Gender.UNISEX));
	}

	@Test
	void testConvertCaseInsensitive() {
		// Arrange
		String mixedCaseInput = "WoMen";

		// Act
		Gender result = stringToGenderConverter.convert(mixedCaseInput);

		// Assert using AssertJ
		assertThat(result).isEqualTo(Gender.WOMEN);

		// Assert using Hamcrest
		assertThat(result, is(Gender.WOMEN));
	}

	@Test
	void testConvertInvalidGender() {
		// Arrange
		String invalidInput = faker.lorem().word(); // Random invalid gender string

		// Act & Assert
		IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> stringToGenderConverter.convert(invalidInput));

		// Assert using AssertJ
		assertThat(exception).isNotNull();
		assertThat(exception.getMessage()).contains("No enum constant");

		// Assert using Hamcrest
		assertThat(exception, is(notNullValue()));
		assertThat(exception.getMessage(), containsString("No enum constant"));
	}

	@Test
	void testConvertNullInput() {
		// Arrange
		String nullInput = null;

		// Act & Assert
		NullPointerException exception = assertThrows(NullPointerException.class, () -> stringToGenderConverter.convert(nullInput));

		// Assert using AssertJ
		assertThat(exception).isNotNull();

		// Assert using Hamcrest
		assertThat(exception, is(notNullValue()));
	}
}