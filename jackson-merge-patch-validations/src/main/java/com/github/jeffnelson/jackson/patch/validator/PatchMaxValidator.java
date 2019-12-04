
package com.github.jeffnelson.jackson.patch.validator;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import com.github.jeffnelson.jackson.patch.PatchField;
import com.github.jeffnelson.jackson.patch.validator.constraints.PatchMax;

public class PatchMaxValidator implements ConstraintValidator<PatchMax,PatchField<? extends Number>> {

    Double max;

    @Override
    public void initialize(PatchMax anno) {
        max = Double.valueOf(anno.value());
    }

    @Override
    public boolean isValid(PatchField<? extends Number> value, ConstraintValidatorContext context) {
        if (value.shouldPatch()) {
            Number val = value.getValue();
            if (val != null) {
                return max.compareTo(val.doubleValue()) >= 0;
            }
        }
        return true;
    }

}
