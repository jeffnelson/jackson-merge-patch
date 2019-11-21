
package com.github.jeffnelson.jackson.patch.validator;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

import com.github.jeffnelson.jackson.patch.PatchField;

public class PatchStringValidatorTest {

    private PatchStringValidator validator;

    @Before
    public void setup() {
        validator = new PatchStringValidator();
    }

    @Test
    public void testNoPatch() {
        PatchField<String> value = PatchField.<String> builder().shouldPatch(false).build();

        validator.allowBlank = false;
        validator.allowEmpty = false;
        validator.allowNull = false;

        assertTrue(validator.isValid(value, null));

        validator.allowBlank = true;

        assertTrue(validator.isValid(value, null));

        validator.allowEmpty = true;

        assertTrue(validator.isValid(value, null));

        validator.allowNull = true;

        assertTrue(validator.isValid(value, null));
    }

    @Test
    public void testPatch_helloWorld() {
        PatchField<String> value = PatchField.<String> builder().shouldPatch(true).value("hello, world").build();

        validator.allowBlank = false;
        validator.allowEmpty = false;
        validator.allowNull = false;

        assertTrue(validator.isValid(value, null));

        validator.allowBlank = true;

        assertTrue(validator.isValid(value, null));

        validator.allowEmpty = true;

        assertTrue(validator.isValid(value, null));

        validator.allowNull = true;

        assertTrue(validator.isValid(value, null));
    }

    @Test
    public void testPatch_null() {
        PatchField<String> value = PatchField.<String> builder().shouldPatch(true).value(null).build();

        validator.allowBlank = false;
        validator.allowEmpty = false;
        validator.allowNull = false;

        assertFalse(validator.isValid(value, null));

        validator.allowBlank = true;

        assertFalse(validator.isValid(value, null));

        validator.allowEmpty = true;

        assertFalse(validator.isValid(value, null));

        validator.allowNull = true;

        assertTrue(validator.isValid(value, null));
    }

    @Test
    public void testPatch_empty() {
        PatchField<String> value = PatchField.<String> builder().shouldPatch(true).value("").build();

        validator.allowBlank = false;
        validator.allowEmpty = false;
        validator.allowNull = false;

        assertFalse(validator.isValid(value, null));

        validator.allowBlank = true;

        assertFalse(validator.isValid(value, null));

        validator.allowEmpty = true;

        assertTrue(validator.isValid(value, null));

        validator.allowNull = true;

        assertTrue(validator.isValid(value, null));
    }

    @Test
    public void testPatch_blank() {
        PatchField<String> value = PatchField.<String> builder().shouldPatch(true).value(" ").build();

        validator.allowBlank = false;
        validator.allowEmpty = false;
        validator.allowNull = false;

        assertFalse(validator.isValid(value, null));

        validator.allowBlank = true;

        assertTrue(validator.isValid(value, null));

        validator.allowEmpty = true;

        assertTrue(validator.isValid(value, null));

        validator.allowNull = true;

        assertTrue(validator.isValid(value, null));
    }
}
