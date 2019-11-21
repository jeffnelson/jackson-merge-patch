
package com.github.jeffnelson.jackson.patch.validator;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import com.github.jeffnelson.jackson.patch.PatchField;
import com.github.jeffnelson.jackson.patch.validator.constraints.PatchNotNull;

public class PatchNotNullValidator implements ConstraintValidator<PatchNotNull,PatchField<?>> {

    @Override
    public void initialize(PatchNotNull constraintAnnotation) {
        // noop
    }

    @Override
    public boolean isValid(PatchField<?> value, ConstraintValidatorContext context) {
        if (value.shouldPatch()) {
            return value.getValue() != null;
        }
        return true;
    }

}
