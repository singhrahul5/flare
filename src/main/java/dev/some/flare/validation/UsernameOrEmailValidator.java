package dev.some.flare.validation;

import jakarta.validation.ConstraintValidatorContext;
import org.hibernate.validator.internal.constraintvalidators.AbstractEmailValidator;

import java.util.regex.Pattern;

public class UsernameOrEmailValidator extends AbstractEmailValidator<UsernameOrEmail> {

    private static final String USERNAME = "^[a-z_][a-z_0-9]{1,19}$";
    private static final Pattern USERNAME_PATTERN = Pattern.compile(USERNAME);

    @Override
    public boolean isValid(CharSequence value, ConstraintValidatorContext context) {
        if (value == null)
            return false;

        return super.isValid(value, context) || USERNAME_PATTERN.matcher(value).matches();
    }
}
