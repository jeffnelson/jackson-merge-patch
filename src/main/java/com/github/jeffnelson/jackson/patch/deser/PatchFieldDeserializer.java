
package com.github.jeffnelson.jackson.patch.deser;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.deser.ContextualDeserializer;
import com.github.jeffnelson.jackson.patch.PatchField;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

/**
 * Jackson {@link JsonDeserializer} for {@link PatchField} objects
 * 
 * @author jeff.nelson
 * @since 1.0.0
 *
 */
@NoArgsConstructor
@AllArgsConstructor
public class PatchFieldDeserializer extends JsonDeserializer<PatchField<?>> implements ContextualDeserializer {

    private JavaType innerType;

    @Override
    public PatchField<?> deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JsonProcessingException {
        return PatchField.builder()
                .shouldPatch(true)
                .value(ctxt.readValue(p, innerType))
                .build();
    }

    @Override
    public PatchField<?> getNullValue(DeserializationContext ctxt) throws JsonMappingException {
        return PatchField.builder()
                .shouldPatch(true)
                .build();
    }

    @Override
    public JsonDeserializer<?> createContextual(DeserializationContext ctxt, BeanProperty property) throws JsonMappingException {
        return new PatchFieldDeserializer(property.getType().containedType(0));
    }
}
