package com.github.jeffnelson.http;

/**
 * 
 * @author jeff.nelson
 * @since 1.0.0
 * @see <a href="http://tools.ietf.org/html/rfc7231#section-3.1.1.1"> HTTP 1.1:
 *      Semantics and Content, section 3.1.1.1</a>
 * @see <a href="https://tools.ietf.org/html/rfc7396"> JSON Merge Patch</a>
 *
 */
public interface PatchMediaType {

	/**
	 * public constant media type for {@code application/merge-patch+json}
	 */
	String APPLICATION_MERGE_PATCH_JSON = "application/merge-patch+json";
}
