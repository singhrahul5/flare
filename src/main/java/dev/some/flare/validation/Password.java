package dev.some.flare.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = {})
@NotNull(message = "Password cannot be null")
@Pattern(regexp = "\\p{Graph}{8,100}", message = "Password length should be greater than equal to 8 and less then " +
        "equal to 100")
@Target({ElementType.FIELD, ElementType.METHOD, ElementType.PARAMETER, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface Password {
    String message() default "Invalid password format";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
