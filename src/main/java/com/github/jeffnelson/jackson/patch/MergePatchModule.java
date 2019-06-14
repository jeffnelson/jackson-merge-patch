
package com.github.jeffnelson.jackson.patch;

import com.fasterxml.jackson.databind.module.SimpleModule;
import com.github.jeffnelson.jackson.patch.deser.PatchFieldDeserializer;

/**
 * Jackson databind module containing the necessary deserializers and serializers for {@link PatchField}
 * 
 * @author jeff.nelson
 * @since 1.0.0
 *
 */
public class MergePatchModule extends SimpleModule {

    private static final long serialVersionUID = 1L;

    public MergePatchModule() {
        addDeserializer(IPatchField.class, new PatchFieldDeserializer());
        addDeserializer(PatchField.class, new PatchFieldDeserializer());
    }

}
