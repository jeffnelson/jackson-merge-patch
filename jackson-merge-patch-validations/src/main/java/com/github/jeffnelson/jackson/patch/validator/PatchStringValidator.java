
package com.github.jeffnelson.jackson.patch.validator;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.apache.commons.lang3.StringUtils;

import com.github.jeffnelson.jackson.patch.PatchField;
import com.github.jeffnelson.jackson.patch.validator.constraints.PatchStringValid;

public class PatchStringValidator implements ConstraintValidator<PatchStringValid,PatchField<String>> {

    boolean allowNull, allowEmpty, allowBlank;

    @Override
    public void initialize(PatchStringValid anno) {
        this.allowNull = anno.allowNull();
        this.allowEmpty = anno.allowEmpty();
        this.allowBlank = anno.allowBlank();
    }

    @Override
    public boolean isValid(PatchField<String> value, ConstraintValidatorContext context) {
        if (value.shouldPatch()) {
            String val = value.getValue();
            if (!allowBlank) {
                // this involves the most checks. isNotBlank requires not blank, not empty, and not null
                return StringUtils.isNotBlank(val);
            }
            if (!allowEmpty) {
                // isNotEmpty requires not empty and not null
                return StringUtils.isNotEmpty(val);
            }
            if (!allowNull) {
                return val != null;
            }
        }
        return true;
    }

}
