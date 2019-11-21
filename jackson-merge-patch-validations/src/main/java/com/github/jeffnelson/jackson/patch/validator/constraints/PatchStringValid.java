
package com.github.jeffnelson.jackson.patch.validator.constraints;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.Payload;

import com.github.jeffnelson.jackson.patch.PatchField;
import com.github.jeffnelson.jackson.patch.validator.PatchStringValidator;

/**
 * Annotation to apply validation to {@literal PatchField<String>}
 * <p>
 * Default behavior. If {@link PatchField#shouldPatch()} returns true,
 * <ul>
 * <li>do not allow null
 * <li>do not allow empty
 * <li>do not allow blank
 * </ul>
 * 
 * @author jeff.nelson
 *
 */
@Constraint(validatedBy = PatchStringValidator.class)
@Retention(RUNTIME)
@Target(FIELD)
public @interface PatchStringValid {

    String message() default "Required";

    boolean allowNull() default false;

    boolean allowEmpty() default false;

    boolean allowBlank() default false;

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
