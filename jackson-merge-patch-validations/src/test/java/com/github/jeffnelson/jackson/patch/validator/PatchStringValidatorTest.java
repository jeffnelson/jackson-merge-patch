
package com.github.jeffnelson.jackson.patch.validator;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

import com.github.jeffnelson.jackson.patch.PatchField;
import com.github.jeffnelson.jackson.patch.validator.constraints.PatchStringRequired;

public class PatchStringValidatorTest {

    private PatchStringValidator validator;

    @Before
    public void setup() {
        validator = new PatchStringValidator();
    }

    @Test
    public void testNoPatch() throws NoSuchFieldException, SecurityException {
        PatchField<String> value = PatchField.<String> builder().shouldPatch(false).build();

        PatchStringRequired anno = DefaultAnno.class.getDeclaredField("myStr").getAnnotation(PatchStringRequired.class);
        validator.initialize(anno);

        assertTrue(validator.isValid(value, null));

        anno = AllowBlankAnno.class.getDeclaredField("myStr").getAnnotation(PatchStringRequired.class);
        validator.initialize(anno);

        assertTrue(validator.isValid(value, null));

        anno = AllowEmptyAnno.class.getDeclaredField("myStr").getAnnotation(PatchStringRequired.class);
        validator.initialize(anno);

        assertTrue(validator.isValid(value, null));

        anno = AllowNullAnno.class.getDeclaredField("myStr").getAnnotation(PatchStringRequired.class);
        validator.initialize(anno);

        assertTrue(validator.isValid(value, null));
    }

    @Test
    public void testPatch_helloWorld() throws NoSuchFieldException, SecurityException {
        PatchField<String> value = PatchField.<String> builder().shouldPatch(true).value("hello, world").build();

        PatchStringRequired anno = DefaultAnno.class.getDeclaredField("myStr").getAnnotation(PatchStringRequired.class);
        validator.initialize(anno);

        assertTrue(validator.isValid(value, null));

        anno = AllowBlankAnno.class.getDeclaredField("myStr").getAnnotation(PatchStringRequired.class);
        validator.initialize(anno);

        assertTrue(validator.isValid(value, null));

        anno = AllowEmptyAnno.class.getDeclaredField("myStr").getAnnotation(PatchStringRequired.class);
        validator.initialize(anno);

        assertTrue(validator.isValid(value, null));

        anno = AllowNullAnno.class.getDeclaredField("myStr").getAnnotation(PatchStringRequired.class);
        validator.initialize(anno);

        assertTrue(validator.isValid(value, null));
    }

    @Test
    public void testPatch_null() throws NoSuchFieldException, SecurityException {
        PatchField<String> value = PatchField.<String> builder().shouldPatch(true).value(null).build();

        PatchStringRequired anno = DefaultAnno.class.getDeclaredField("myStr").getAnnotation(PatchStringRequired.class);
        validator.initialize(anno);

        assertFalse(validator.isValid(value, null));

        anno = AllowBlankAnno.class.getDeclaredField("myStr").getAnnotation(PatchStringRequired.class);
        validator.initialize(anno);

        assertFalse(validator.isValid(value, null));

        anno = AllowEmptyAnno.class.getDeclaredField("myStr").getAnnotation(PatchStringRequired.class);
        validator.initialize(anno);

        assertFalse(validator.isValid(value, null));

        anno = AllowNullAnno.class.getDeclaredField("myStr").getAnnotation(PatchStringRequired.class);
        validator.initialize(anno);

        assertTrue(validator.isValid(value, null));
    }

    @Test
    public void testPatch_empty() throws NoSuchFieldException, SecurityException {
        PatchField<String> value = PatchField.<String> builder().shouldPatch(true).value("").build();

        PatchStringRequired anno = DefaultAnno.class.getDeclaredField("myStr").getAnnotation(PatchStringRequired.class);
        validator.initialize(anno);

        assertFalse(validator.isValid(value, null));

        anno = AllowBlankAnno.class.getDeclaredField("myStr").getAnnotation(PatchStringRequired.class);
        validator.initialize(anno);

        assertFalse(validator.isValid(value, null));

        anno = AllowEmptyAnno.class.getDeclaredField("myStr").getAnnotation(PatchStringRequired.class);
        validator.initialize(anno);

        assertTrue(validator.isValid(value, null));

        anno = AllowNullAnno.class.getDeclaredField("myStr").getAnnotation(PatchStringRequired.class);
        validator.initialize(anno);

        assertTrue(validator.isValid(value, null));
    }

    @Test
    public void testPatch_blank() throws NoSuchFieldException, SecurityException {
        PatchField<String> value = PatchField.<String> builder().shouldPatch(true).value(" ").build();

        PatchStringRequired anno = DefaultAnno.class.getDeclaredField("myStr").getAnnotation(PatchStringRequired.class);
        validator.initialize(anno);

        assertFalse(validator.isValid(value, null));

        anno = AllowBlankAnno.class.getDeclaredField("myStr").getAnnotation(PatchStringRequired.class);
        validator.initialize(anno);

        assertTrue(validator.isValid(value, null));

        anno = AllowEmptyAnno.class.getDeclaredField("myStr").getAnnotation(PatchStringRequired.class);
        validator.initialize(anno);

        assertTrue(validator.isValid(value, null));

        anno = AllowNullAnno.class.getDeclaredField("myStr").getAnnotation(PatchStringRequired.class);
        validator.initialize(anno);

        assertTrue(validator.isValid(value, null));
    }

    static class DefaultAnno {

        @PatchStringRequired
        PatchField<String> myStr;
    }

    static class AllowBlankAnno {

        @PatchStringRequired(allowBlank = true)
        PatchField<String> myStr;
    }

    static class AllowEmptyAnno {

        @PatchStringRequired(allowBlank = true, allowEmpty = true)
        PatchField<String> myStr;
    }

    static class AllowNullAnno {

        @PatchStringRequired(allowBlank = true, allowEmpty = true, allowNull = true)
        PatchField<String> myStr;
    }
}
