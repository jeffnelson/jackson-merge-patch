
package com.github.jeffnelson.jackson.patch.validator.constraints;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.Payload;

import com.github.jeffnelson.jackson.patch.PatchField;
import com.github.jeffnelson.jackson.patch.validator.PatchNotNullValidator;

/**
 * Annotation to apply validation to {@literal PatchField<String>}
 * <p>
 * Default behavior. If {@link PatchField#shouldPatch()} returns true, value cannot be null
 * 
 * @author jeff.nelson
 *
 */
@Constraint(validatedBy = PatchNotNullValidator.class)
@Retention(RUNTIME)
@Target(FIELD)
public @interface PatchNotNull {

    String message() default "Required";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
