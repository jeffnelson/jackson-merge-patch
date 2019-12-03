
package com.github.jeffnelson.jackson.patch.validator;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.List;
import java.util.stream.Collectors;

import javax.validation.Valid;

import org.apache.commons.io.IOUtils;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.jeffnelson.http.PatchMediaType;
import com.github.jeffnelson.jackson.patch.MergePatchModule;
import com.github.jeffnelson.jackson.patch.PatchField;
import com.github.jeffnelson.jackson.patch.validator.constraints.PatchMax;
import com.github.jeffnelson.jackson.patch.validator.constraints.PatchMin;
import com.github.jeffnelson.jackson.patch.validator.constraints.PatchNotNull;
import com.google.common.io.Resources;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.Value;

/**
 * Unit test utilizing spring's mvc test framework showing multiple ways of using {@link PatchField}
 * <p>
 * Refer to the json resource files under {@code src/test/resources/json}
 * 
 * @author jeff.nelson
 *
 */
public class PatchNumberValidationsControllerTest {

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
                .setControllerAdvice(new WebExceptionHandler(new ResourceBundleMessageSource()))
                .build();
    }

    @Test
    public void testPatchFoo_allValid() throws Exception {
        String content = getFileContent("json/allValid.json");

        mvc.perform(patch("/foo")
                .content(content)
                .contentType(PatchMediaType.APPLICATION_MERGE_PATCH_JSON))
                .andExpect(status().is2xxSuccessful())
                .andReturn();

        assertTestDTO(controller.foo,
                new PatchField<Integer>(true, 23),
                new PatchField<Double>(true, 45.67),
                new PatchField<Long>(true, 6789L));
    }

    @Test
    public void testPatchFoo_someInvalid() throws Exception {
        String content = getFileContent("json/invalidNumbers.json");

        mvc.perform(patch("/foo")
                .content(content)
                .contentType(PatchMediaType.APPLICATION_MERGE_PATCH_JSON))
                .andExpect(status().is4xxClientError())

                .andExpect(jsonPath("[*].field", is("myDbl")))
                .andExpect(jsonPath("[*].error", is("must be less than or equal to 50")))
                .andExpect(jsonPath("[*].field", is("myLong")))
                .andExpect(jsonPath("[*].error", is("Required")))
                .andExpect(jsonPath("[*].field", is("myInt")))
                .andExpect(jsonPath("[*].error", is("must be greater than or equal to 12")))
                .andReturn();

        assertNull(controller.foo);
    }

    void assertTestDTO(
            TestDTO actual,
            PatchField<Integer> expectedMyInt,
            PatchField<Double> expectedMyDbl,
            PatchField<Long> expectedMyLong) {
        assertTestDTO("", actual, expectedMyInt, expectedMyDbl, expectedMyLong);
    }

    void assertTestDTO(String fieldNamePrefix,
            TestDTO actual,
            PatchField<Integer> expectedMyInt,
            PatchField<Double> expectedMyDbl,
            PatchField<Long> expectedMyLong) {
        assertPatchField(combine(fieldNamePrefix, "myInt"), expectedMyInt, actual.getMyInt());
        assertPatchField(combine(fieldNamePrefix, "myDbl"), expectedMyDbl, actual.getMyDbl());
        assertPatchField(combine(fieldNamePrefix, "myLong"), expectedMyLong, actual.getMyLong());
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

        @RequestMapping(value = "/foo", method = RequestMethod.PATCH, consumes = PatchMediaType.APPLICATION_MERGE_PATCH_JSON)
        public Void patchFoo(@Valid @RequestBody FooDTO foo) {
            this.foo = foo;
            return null;
        }

    }

    @ControllerAdvice
    @RequiredArgsConstructor
    public static class WebExceptionHandler extends ResponseEntityExceptionHandler {

        final MessageSource messageSource;

        @Override
        protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
                HttpHeaders headers, HttpStatus status, WebRequest request) {
            List<ErrorDTO> errors = ex.getBindingResult().getFieldErrors().stream()
                    .map(fieldError -> new ErrorDTO(fieldError.getField(), messageSource.getMessage(fieldError, LocaleContextHolder.getLocale())))
                    .collect(Collectors.toList());
            return new ResponseEntity<>(errors, status);
        }

    }

    @Value
    public static class ErrorDTO {

        String field, error;
    }

    interface TestDTO {

        PatchField<Integer> getMyInt();

        void setMyInt(PatchField<Integer> myInt);

        PatchField<Double> getMyDbl();

        void setMyDbl(PatchField<Double> myDbl);

        PatchField<Long> getMyLong();

        void setMyLong(PatchField<Long> myLong);

    }

    @Getter
    @Setter
    @ToString
    public static class FooDTO implements TestDTO {

        @PatchMin(12)
        @PatchMax(30)
        private PatchField<Integer> myInt = PatchField.<Integer> builder().shouldPatch(false).build();
        @PatchMin(23)
        @PatchMax(50)
        private PatchField<Double> myDbl = PatchField.<Double> builder().shouldPatch(false).build();
        @PatchMin(670)
        @PatchMax(7000)
        @PatchNotNull
        private PatchField<Long> myLong = PatchField.<Long> builder().shouldPatch(false).build();

    }

    String getFileContent(String filePath) throws IOException {
        try (InputStream stream = Resources.getResource(filePath).openStream()) {
            return IOUtils.toString(stream, Charset.defaultCharset());
        }
    }
}
