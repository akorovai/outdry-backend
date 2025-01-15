package dev.akorovai.backend.product.converter;

import com.github.javafaker.Faker;
import dev.akorovai.backend.color.Color;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;


class StringToColorConverterTest {

	@InjectMocks
	private StringToColorConverter stringToColorConverter;

	private Faker faker;

	@BeforeEach
	void setUp() {
		MockitoAnnotations.openMocks(this);
		faker = new Faker();
	}

	@Test
	void testConvert() {
		// Arrange
		String colorName = faker.color().name();

		// Act
		Color result = stringToColorConverter.convert(colorName);

		// Assert using AssertJ
		assertThat(result).isNotNull();
		assertThat(result.getName()).isEqualTo(colorName);

		// Assert using Hamcrest
		assertThat(result, is(notNullValue()));
		assertThat(result.getName(), is(equalTo(colorName)));
	}

	@Test
	void testConvertWithEmptyString() {
		// Arrange
		String emptyString = "";

		// Act
		Color result = stringToColorConverter.convert(emptyString);

		// Assert using AssertJ
		assertThat(result).isNotNull();
		assertThat(result.getName()).isEmpty();

		// Assert using Hamcrest
		assertThat(result, is(notNullValue()));
		assertThat(result.getName(), is(""));
	}

	@Test
	void testConvertWithNull() {
		// Arrange
		String nullString = null;

		// Act
		Color result = stringToColorConverter.convert(nullString);

		// Assert using AssertJ
		assertThat(result).isNotNull();
		assertThat(result.getName()).isNull();

		// Assert using Hamcrest
		assertThat(result, is(notNullValue()));
		assertThat(result.getName(), is(nullValue()));
	}
}