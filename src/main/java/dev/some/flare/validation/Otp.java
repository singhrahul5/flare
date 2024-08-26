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
@NotNull(message = "otp cannot be null")
@Pattern(regexp = "^[1-9][0-9]{5}$", message = "otp must match the pattern ^[1-9][0-9]{5}$")
@Target({ElementType.FIELD, ElementType.METHOD, ElementType.PARAMETER, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface Otp {

    String message() default "Invalid otp format";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}