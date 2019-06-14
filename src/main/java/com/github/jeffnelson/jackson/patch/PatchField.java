
package com.github.jeffnelson.jackson.patch;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.Accessors;

/**
 * Wrapper class to put around fields in classes serialized by jackson where you wish to know whether or not a node in
 * the JSON payload was:
 * <ul>
 * <li>present with a value
 * <li>present with a null value
 * <li>completely absent
 * </ul>
 * 
 * @author jeff.nelson
 * @since 1.0.0
 *
 * @param <T>
 *            the type of value this patch field represents
 */
@AllArgsConstructor
@Builder
@Getter
@ToString
@EqualsAndHashCode
public class PatchField<T> implements IPatchField<T> {

    @Accessors(fluent = true)
    private final boolean shouldPatch;
    private final T value;

}
