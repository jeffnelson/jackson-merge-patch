
package com.github.jeffnelson.jackson.patch.validator;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import com.github.jeffnelson.jackson.patch.PatchField;
import com.github.jeffnelson.jackson.patch.validator.constraints.PatchMin;

public class PatchMinValidator implements ConstraintValidator<PatchMin,PatchField<? extends Number>> {

    Double min;

    @Override
    public void initialize(PatchMin anno) {
        min = Double.valueOf(anno.value());
    }

    @Override
    public boolean isValid(PatchField<? extends Number> value, ConstraintValidatorContext context) {
        if (value.shouldPatch()) {
            Number val = value.getValue();
            if (val != null) {
                return min.compareTo(val.doubleValue()) <= 0;
            }
        }
        return true;
    }

}
