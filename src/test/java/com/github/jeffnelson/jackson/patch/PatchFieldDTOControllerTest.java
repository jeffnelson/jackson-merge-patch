
package com.github.jeffnelson.jackson.patch;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.beans.ConstructorProperties;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;

import org.apache.commons.io.IOUtils;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.github.jeffnelson.http.PatchMediaType;
import com.github.jeffnelson.jackson.patch.deser.PatchFieldDeserializer;
import com.google.common.io.Resources;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * Unit test utilizing spring's mvc test framework showing multiple ways of using {@link PatchField}
 * <p>
 * Refer to the json resource files under {@code src/test/resources/json}
 * 
 * @author jeff.nelson
 *
 */
public class PatchFieldDTOControllerTest {

    @InjectMocks
    TestRestController controller;

    MockMvc mvc;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);

        // setup the HttpMessage converter to use ObjectMapper with the MergePatchModule
        ObjectMapper om = new ObjectMapper();
        om.registerModule(new MergePatchModule());
        MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter();
        converter.setObjectMapper(om);

        // setup the mvc test harness
        mvc = MockMvcBuilders.standaloneSetup(controller)
                .setMessageConverters(converter)
                .setControllerAdvice(new WebExceptionHandler())
                .build();
    }

    @Test
    public void testPatchFoo_allPresent() throws Exception {
        String content = getFileContent("json/allPresent.json");

        mvc.perform(patch("/foo")
                .content(content)
                .contentType(PatchMediaType.APPLICATION_MERGE_PATCH_JSON))
                .andExpect(status().is2xxSuccessful())
                .andReturn();

        assertTestDTO(controller.foo,
                new PatchField<String>(true, "hello"),
                new PatchField<Integer>(true, 123),
                new PatchField<Double>(true, 23.45),
                new PatchField<Boolean>(true, Boolean.TRUE));
        assertTestDTO("bar.", controller.foo.getBar().getValue(),
                new PatchField<String>(true, "helloBar"),
                new PatchField<Integer>(true, 1234),
                new PatchField<Double>(true, 23.456),
                new PatchField<Boolean>(true, Boolean.FALSE));

        assertNull(controller.lombokAllArgs);
        assertNull(controller.manualAllArgsWithCtorProps);
    }

    @Test
    public void testPatchLombokAllArgs_allPresent() throws Exception {
        String content = getFileContent("json/allPresent.json");

        mvc.perform(patch("/lombokAllArgs")
                .content(content)
                .contentType(PatchMediaType.APPLICATION_MERGE_PATCH_JSON))
                .andExpect(status().is2xxSuccessful())
                .andReturn();

        assertTestDTO(controller.lombokAllArgs,
                new PatchField<String>(true, "hello"),
                new PatchField<Integer>(true, 123),
                new PatchField<Double>(true, 23.45),
                new PatchField<Boolean>(true, Boolean.TRUE));
        assertTestDTO("bar.", controller.lombokAllArgs.getBar().getValue(),
                new PatchField<String>(true, "helloBar"),
                new PatchField<Integer>(true, 1234),
                new PatchField<Double>(true, 23.456),
                new PatchField<Boolean>(true, Boolean.FALSE));

        assertNull(controller.foo);
        assertNull(controller.manualAllArgsWithCtorProps);
    }

    @Test
    public void testPatchManualAllArgsCtorProps_allPresent() throws Exception {
        String content = getFileContent("json/allPresent.json");

        mvc.perform(patch("/manualAllArgs/ctorProps")
                .content(content)
                .contentType(PatchMediaType.APPLICATION_MERGE_PATCH_JSON))
                .andExpect(status().is2xxSuccessful())
                .andReturn();

        assertTestDTO(controller.manualAllArgsWithCtorProps,
                new PatchField<String>(true, "hello"),
                new PatchField<Integer>(true, 123),
                new PatchField<Double>(true, 23.45),
                new PatchField<Boolean>(true, Boolean.TRUE));
        assertTestDTO("bar.", controller.manualAllArgsWithCtorProps.getBar().getValue(),
                new PatchField<String>(true, "helloBar"),
                new PatchField<Integer>(true, 1234),
                new PatchField<Double>(true, 23.456),
                new PatchField<Boolean>(true, Boolean.FALSE));

        assertNull(controller.foo);
        assertNull(controller.lombokAllArgs);
    }

    // all absent tests

    @Test
    public void testPatchFoo_allAbsent() throws Exception {
        String content = getFileContent("json/allAbsent.json");

        mvc.perform(patch("/foo")
                .content(content)
                .contentType(PatchMediaType.APPLICATION_MERGE_PATCH_JSON))
                .andExpect(status().is2xxSuccessful())
                .andReturn();

        assertTestDTO(controller.foo,
                new PatchField<String>(false, null),
                new PatchField<Integer>(false, null),
                new PatchField<Double>(false, null),
                new PatchField<Boolean>(false, null));
        assertPatchField("bar", new PatchField<BarDTO>(false, null), controller.foo.getBar());

        assertNull(controller.lombokAllArgs);
        assertNull(controller.manualAllArgsWithCtorProps);
    }

    // now things start to get interesting

    @Test
    public void testPatchLombokAllArgs_allAbsent() throws Exception {
        String content = getFileContent("json/allAbsent.json");

        mvc.perform(patch("/lombokAllArgs")
                .content(content)
                .contentType(PatchMediaType.APPLICATION_MERGE_PATCH_JSON))
                .andExpect(status().is2xxSuccessful())
                .andReturn();

        assertTestDTO(controller.lombokAllArgs,
                new PatchField<String>(false, null),
                new PatchField<Integer>(false, null),
                new PatchField<Double>(false, null),
                new PatchField<Boolean>(false, null));
        assertPatchField("bar", new PatchField<BarDTO>(false, null), controller.lombokAllArgs.getBar());

        assertNull(controller.foo);
        assertNull(controller.manualAllArgsWithCtorProps);
    }

    @Test
    public void testPatchManualAllArgsCtorProps_allAbsent() throws Exception {
        String content = getFileContent("json/allAbsent.json");

        mvc.perform(patch("/manualAllArgs/ctorProps")
                .content(content)
                .contentType(PatchMediaType.APPLICATION_MERGE_PATCH_JSON))
                .andExpect(status().is2xxSuccessful())
                .andReturn();

        // note that shouldPatch is true on the below...
        // this is because jackson constructs the object using the all args ctor
        // since it has to come up with some kind of value for each arg, it uses the null value
        // would be great if jackson could have a way for custom deserializers to specify a different "absent" value
        // instead of just using the null value
        assertTestDTO(controller.manualAllArgsWithCtorProps,
                new PatchField<String>(true, null),
                new PatchField<Integer>(true, null),
                new PatchField<Double>(true, null),
                new PatchField<Boolean>(false, null)); // note this is correctly false because the
                                                       // @ConstructorProperties
                                                       // ctor does not set this value
        assertPatchField("bar", new PatchField<BarDTO>(true, null), controller.manualAllArgsWithCtorProps.getBar());

        assertNull(controller.foo);
        assertNull(controller.lombokAllArgs);
    }

    // now test mixed json merge payload
    @Test
    public void testPatchFoo_mix() throws Exception {
        String content = getFileContent("json/mix.json");

        mvc.perform(patch("/foo")
                .content(content)
                .contentType(PatchMediaType.APPLICATION_MERGE_PATCH_JSON))
                .andExpect(status().is2xxSuccessful())
                .andReturn();

        assertTestDTO(controller.foo,
                new PatchField<String>(true, "hello"),
                new PatchField<Integer>(true, null), // payload indicated to set myInt to null
                new PatchField<Double>(false, null), // payload did not have the myDbl field at all
                new PatchField<Boolean>(true, Boolean.TRUE));
        assertTestDTO("bar.", controller.foo.getBar().getValue(),
                new PatchField<String>(false, null), // payload did not have the bar.myStr field at all
                new PatchField<Integer>(true, 1234),
                new PatchField<Double>(true, null), // payload indicated to set bar.myDbl to null
                new PatchField<Boolean>(false, null)); // payload did not have the bar.myBool field at all

        assertNull(controller.lombokAllArgs);
        assertNull(controller.manualAllArgsWithCtorProps);
    }

    @Test
    public void testPatchLombokAllArgs_mix() throws Exception {
        String content = getFileContent("json/mix.json");

        mvc.perform(patch("/lombokAllArgs")
                .content(content)
                .contentType(PatchMediaType.APPLICATION_MERGE_PATCH_JSON))
                .andExpect(status().is2xxSuccessful())
                .andReturn();

        // again, notice with the lombok all args ctor, things are broken
        assertTestDTO(controller.lombokAllArgs,
                new PatchField<String>(true, "hello"),
                new PatchField<Integer>(true, null), // payload indicated to set myInt to null
                new PatchField<Double>(false, null), // payload did not have the myDbl field at all
                new PatchField<Boolean>(true, Boolean.TRUE));
        assertTestDTO("bar.", controller.lombokAllArgs.getBar().getValue(),
                new PatchField<String>(false, null), // payload did not have the bar.myStr field at all
                new PatchField<Integer>(true, 1234),
                new PatchField<Double>(true, null), // payload indicated to set bar.myDbl to null
                new PatchField<Boolean>(false, null)); // payload did not have the bar.myBool field at all

        assertNull(controller.foo);
        assertNull(controller.manualAllArgsWithCtorProps);
    }

    @Test
    public void testPatchManualAllArgsCtorProps_mix() throws Exception {
        String content = getFileContent("json/mix.json");

        mvc.perform(patch("/manualAllArgs/ctorProps")
                .content(content)
                .contentType(PatchMediaType.APPLICATION_MERGE_PATCH_JSON))
                .andExpect(status().is2xxSuccessful())
                .andReturn();

        // again, notice with the lombok all args ctor, things are broken
        assertTestDTO(controller.manualAllArgsWithCtorProps,
                new PatchField<String>(true, "hello"),
                new PatchField<Integer>(true, null), // payload indicated to set myInt to null
                new PatchField<Double>(true, null), // payload did not have the myDbl field at all, but it got set to
                                                    // null value
                new PatchField<Boolean>(true, Boolean.TRUE));
        assertTestDTO("bar.", controller.manualAllArgsWithCtorProps.getBar().getValue(),
                new PatchField<String>(false, null), // payload did not have the bar.myStr field at all
                new PatchField<Integer>(true, 1234),
                new PatchField<Double>(true, null), // payload indicated to set bar.myDbl to null
                new PatchField<Boolean>(false, null)); // payload did not have the bar.myBool field at all

        assertNull(controller.foo);
        assertNull(controller.lombokAllArgs);
    }

    @Test
    public void testPatchFoo_mixBarAbsent() throws Exception {
        String content = getFileContent("json/mix_barAbsent.json");

        mvc.perform(patch("/foo")
                .content(content)
                .contentType(PatchMediaType.APPLICATION_MERGE_PATCH_JSON))
                .andExpect(status().is2xxSuccessful())
                .andReturn();

        assertTestDTO(controller.foo,
                new PatchField<String>(true, "hello"),
                new PatchField<Integer>(true, null), // payload indicated to set myInt to null
                new PatchField<Double>(false, null), // payload did not have the myDbl field at all
                new PatchField<Boolean>(true, Boolean.TRUE));
        assertPatchField("bar", new PatchField<BarDTO>(false, null), controller.foo.getBar());

        assertNull(controller.lombokAllArgs);
        assertNull(controller.manualAllArgsWithCtorProps);
    }

    @Test
    public void testPatchLombokAllArgs_mixBarAbsent() throws Exception {
        String content = getFileContent("json/mix_barAbsent.json");

        mvc.perform(patch("/lombokAllArgs")
                .content(content)
                .contentType(PatchMediaType.APPLICATION_MERGE_PATCH_JSON))
                .andExpect(status().is2xxSuccessful())
                .andReturn();

        assertTestDTO(controller.lombokAllArgs,
                new PatchField<String>(true, "hello"),
                new PatchField<Integer>(true, null), // payload indicated to set myInt to null
                new PatchField<Double>(false, null), // payload did not have the myDbl field at all
                new PatchField<Boolean>(true, Boolean.TRUE));
        assertPatchField("bar", new PatchField<BarDTO>(false, null), controller.lombokAllArgs.getBar()); // payload did
                                                                                                         // not have
        // bar at all

        assertNull(controller.foo);
        assertNull(controller.manualAllArgsWithCtorProps);
    }

    @Test
    public void testPatchManualAllArgsCtorProps_mixBarAbsent() throws Exception {
        String content = getFileContent("json/mix_barAbsent.json");

        mvc.perform(patch("/manualAllArgs/ctorProps")
                .content(content)
                .contentType(PatchMediaType.APPLICATION_MERGE_PATCH_JSON))
                .andExpect(status().is2xxSuccessful())
                .andReturn();

        assertTestDTO(controller.manualAllArgsWithCtorProps,
                new PatchField<String>(true, "hello"),
                new PatchField<Integer>(true, null), // payload indicated to set myInt to null
                new PatchField<Double>(true, null), // payload did not have the myDbl field at all, but it got set to
                                                    // null value
                new PatchField<Boolean>(true, Boolean.TRUE));
        assertPatchField("bar", new PatchField<BarDTO>(true, null), controller.manualAllArgsWithCtorProps.getBar()); // payload
                                                                                                                     // did
        // not have
        // bar at all, but it got
        // set to null value

        assertNull(controller.foo);
        assertNull(controller.lombokAllArgs);
    }

    void assertTestDTO(
            TestDTO actual,
            PatchField<String> expectedMyStr,
            PatchField<Integer> expectedMyInt,
            PatchField<Double> expectedMyDbl,
            PatchField<Boolean> expectedMyBool) {
        assertTestDTO("", actual, expectedMyStr, expectedMyInt, expectedMyDbl, expectedMyBool);
    }

    void assertTestDTO(String fieldNamePrefix,
            TestDTO actual,
            PatchField<String> expectedMyStr,
            PatchField<Integer> expectedMyInt,
            PatchField<Double> expectedMyDbl,
            PatchField<Boolean> expectedMyBool) {
        assertPatchField(combine(fieldNamePrefix, "myStr"), expectedMyStr, actual.getMyStr());
        assertPatchField(combine(fieldNamePrefix, "myInt"), expectedMyInt, actual.getMyInt());
        assertPatchField(combine(fieldNamePrefix, "myDbl"), expectedMyDbl, actual.getMyDbl());
        assertPatchField(combine(fieldNamePrefix, "myBool"), expectedMyBool, actual.getMyBool());
    }

    <T> void assertPatchField(String fieldName, PatchField<T> expected, PatchField<T> actual) {
        assertEquals(combine(fieldName, " shouldPatch incorrect"), expected.shouldPatch(), actual.shouldPatch());
        assertEquals(combine(fieldName, " value incorrect"), expected.getValue(), actual.getValue());
    }

    String combine(String prefix, String name) {
        return String.format("%s%s", prefix, name);
    }

    @RestController
    public static class TestRestController {

        FooDTO foo;
        LombokAllArgsDTO lombokAllArgs;
        ManualAllArgsConstructorPropertiesDTO manualAllArgsWithCtorProps;

        @RequestMapping(value = "/foo", method = RequestMethod.PATCH, consumes = PatchMediaType.APPLICATION_MERGE_PATCH_JSON)
        public void patchFoo(@RequestBody FooDTO foo) {
            this.foo = foo;
        }

        @RequestMapping(value = "/lombokAllArgs", method = RequestMethod.PATCH, consumes = PatchMediaType.APPLICATION_MERGE_PATCH_JSON)
        public void patchLombokAllArgs(@RequestBody LombokAllArgsDTO lombokAllArgs) {
            this.lombokAllArgs = lombokAllArgs;
        }

        @RequestMapping(value = "/manualAllArgs/ctorProps", method = RequestMethod.PATCH, consumes = PatchMediaType.APPLICATION_MERGE_PATCH_JSON)
        public void patchManualAllArgsWithConstructorProperties(@RequestBody ManualAllArgsConstructorPropertiesDTO manualAllArgsWithCtorProps) {
            this.manualAllArgsWithCtorProps = manualAllArgsWithCtorProps;
        }
    }

    public static class WebExceptionHandler extends ResponseEntityExceptionHandler {

        Exception caught;

        @ExceptionHandler({Exception.class})
        public final ResponseEntity<Object> handleUnknownException(Exception ex, WebRequest request) throws Exception {
            caught = ex;
            return new ResponseEntity<>(String.format("unhandled exception: %s", ex.getMessage()), HttpStatus.BAD_REQUEST);
        }

        @Override
        protected ResponseEntity<Object> handleExceptionInternal(
                Exception ex, Object body, HttpHeaders headers, HttpStatus status, WebRequest request) {
            caught = ex;
            return super.handleExceptionInternal(ex, body, headers, status, request);
        }
    }

    interface TestDTO {

        PatchField<String> getMyStr();

        void setMyStr(PatchField<String> myStr);

        PatchField<Integer> getMyInt();

        void setMyInt(PatchField<Integer> myInt);

        PatchField<Double> getMyDbl();

        void setMyDbl(PatchField<Double> myDbl);

        PatchField<Boolean> getMyBool();

        void setMyBool(PatchField<Boolean> myBool);

    }

    interface TestWithBarDTO extends TestDTO {

        PatchField<BarDTO> getBar();

        void setBar(PatchField<BarDTO> bar);
    }

    @Getter
    @Setter
    @ToString
    public static class FooDTO implements TestWithBarDTO {

        private PatchField<String> myStr = PatchField.<String> builder().shouldPatch(false).build();
        private PatchField<Integer> myInt = PatchField.<Integer> builder().shouldPatch(false).build();
        private PatchField<Double> myDbl = PatchField.<Double> builder().shouldPatch(false).build();
        private PatchField<Boolean> myBool = PatchField.<Boolean> builder().shouldPatch(false).build();
        private PatchField<BarDTO> bar = PatchField.<BarDTO> builder().shouldPatch(false).build();

    }

    @Getter
    @Setter
    @ToString
    public static class BarDTO implements TestDTO {

        private PatchField<String> myStr = PatchField.<String> builder().shouldPatch(false).build();
        private PatchField<Integer> myInt = PatchField.<Integer> builder().shouldPatch(false).build();
        private PatchField<Double> myDbl = PatchField.<Double> builder().shouldPatch(false).build();
        private PatchField<Boolean> myBool = PatchField.<Boolean> builder().shouldPatch(false).build();
    }

    /**
     * a DTO illustrating that things break when you use lombok's AllArgsConstructor
     *
     */
    @Getter
    @Setter
    @ToString
    @AllArgsConstructor
    @NoArgsConstructor
    public static class LombokAllArgsDTO implements TestWithBarDTO {

        private PatchField<String> myStr = PatchField.<String> builder().shouldPatch(false).build();
        private PatchField<Integer> myInt = PatchField.<Integer> builder().shouldPatch(false).build();
        private PatchField<Double> myDbl = PatchField.<Double> builder().shouldPatch(false).build();
        private PatchField<Boolean> myBool = PatchField.<Boolean> builder().shouldPatch(false).build();
        private PatchField<BarDTO> bar = PatchField.<BarDTO> builder().shouldPatch(false).build();
    }

    /**
     * a DTO illustrating that things break when you have a constructor annotated with {@link ConstructorProperties}
     *
     */
    @Getter
    @Setter
    @ToString
    public static class ManualAllArgsConstructorPropertiesDTO implements TestWithBarDTO {

        @JsonDeserialize(using = PatchFieldDeserializer.class)
        private PatchField<String> myStr = PatchField.<String> builder().shouldPatch(false).build();
        private PatchField<Integer> myInt = PatchField.<Integer> builder().shouldPatch(false).build();
        private PatchField<Double> myDbl = PatchField.<Double> builder().shouldPatch(false).build();
        private PatchField<Boolean> myBool = PatchField.<Boolean> builder().shouldPatch(false).build();
        private PatchField<BarDTO> bar = PatchField.<BarDTO> builder().shouldPatch(false).build();

        /**
         * default noarg ctor
         */
        public ManualAllArgsConstructorPropertiesDTO() {
        }

        /**
         * ctor with {@link ConstructorProperties} anno on it - jackson will use this...
         */
        @ConstructorProperties({"myStr", "myInt", "myDbl", "bar"})
        public ManualAllArgsConstructorPropertiesDTO(
                PatchField<String> myStr,
                PatchField<Integer> myInt,
                PatchField<Double> myDbl,
                PatchField<BarDTO> bar) {
            this.myStr = myStr;
            this.myInt = myInt;
            this.myDbl = myDbl;
            this.bar = bar;
        }
    }

    String getFileContent(String filePath) throws IOException {
        try (InputStream stream = Resources.getResource(filePath).openStream()) {
            return IOUtils.toString(stream, Charset.defaultCharset());
        }
    }
}
