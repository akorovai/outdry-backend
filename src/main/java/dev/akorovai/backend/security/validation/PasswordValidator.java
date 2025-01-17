package dev.akorovai.backend.security.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.Set;
import java.util.function.IntPredicate;

public class PasswordValidator implements ConstraintValidator<ValidPassword, String> {

	private int min;
	private int max;
	private static final Set<Integer> ALLOWED_SPECIAL_CHARACTERS = Set.of(
			(int) '!', (int) '@', (int) '#', (int) '$', (int) '%', (int) '^', (int) '&', (int) '*', (int) '(', (int) ')',
			(int) '_', (int) '+', (int) '-', (int) '=', (int) '[', (int) ']', (int) '{', (int) '}', (int) '|', (int) ';',
			(int) ':', (int) '"', (int) ',', (int) '.', (int) '<', (int) '>', (int) '?', (int) '/', (int) '`', (int) '~'
	);

	@Override
	public void initialize(ValidPassword constraintAnnotation) {
		this.min = constraintAnnotation.min();
		this.max = constraintAnnotation.max();
	}

	@Override
	public boolean isValid(String password, ConstraintValidatorContext context) {

		if (password == null || password.isBlank() || password.contains(" ")) {
			return false;
		}


		if (!isValidLength(password)) {
			return false;
		}


		if (!password.chars().allMatch(ch -> ch <= 127)) {
			return false;
		}


		return password.chars().anyMatch(Character::isUpperCase) &&
				       password.chars().anyMatch(Character::isLowerCase) &&
				       password.chars().anyMatch(ALLOWED_SPECIAL_CHARACTERS::contains);
	}

	private boolean isValidLength(String password) {
		return password.length() >= min && password.length() <= max;
	}
}