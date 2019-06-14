
package com.github.jeffnelson.jackson.patch;

import java.util.function.Consumer;

/**
 * 
 * @author jeff.nelson
 * @since 1.0.0
 *
 * @param <T>
 *            the type of value this patch field represents
 */
public interface IPatchField<T> {

    boolean shouldPatch();

    T getValue();

    default void patch(Consumer<T> setter) {
        if (shouldPatch()) {
            setter.accept(getValue());
        }
    }
}
