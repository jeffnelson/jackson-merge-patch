
package com.github.jeffnelson.jackson.patch.validator;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

import com.github.jeffnelson.jackson.patch.PatchField;

public class PatchNotNullValidatorTest {

    private PatchNotNullValidator validator;

    @Before
    public void setup() {
        validator = new PatchNotNullValidator();
        validator.initialize(null);
    }

    @Test
    public void testNoPatch() {
        PatchField<String> value = PatchField.<String> builder().shouldPatch(false).build();
        assertTrue(validator.isValid(value, null));
    }

    @Test
    public void testPatch_helloWorld() {
        PatchField<String> value = PatchField.<String> builder().shouldPatch(true).value("hello, world").build();
        assertTrue(validator.isValid(value, null));
    }

    @Test
    public void testPatch_null() {
        PatchField<String> value = PatchField.<String> builder().shouldPatch(true).value(null).build();
        assertFalse(validator.isValid(value, null));
    }
}
