
package com.github.jeffnelson.jackson.patch.validator.constraints;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.Payload;

import com.github.jeffnelson.jackson.patch.validator.PatchMaxValidator;

/**
 * The annotated element must be a number whose value must be lower or
 * equal to the specified maximum.
 * <p/>
 * Supported types are:
 * <ul>
 * <li>{@code BigDecimal}</li>
 * <li>{@code BigInteger}</li>
 * <li>{@code byte}, {@code short}, {@code int}, {@code long}, and their respective
 * wrappers</li>
 * </ul>
 * Note that {@code double} and {@code float} are not supported due to rounding errors
 * (some providers might provide some approximative support).
 * <p/>
 *
 * @author jeff.nelson
 */
@Constraint(validatedBy = PatchMaxValidator.class)
@Retention(RUNTIME)
@Target(FIELD)
public @interface PatchMax {

    long value();

    String message() default "{javax.validation.constraints.Max.message}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
