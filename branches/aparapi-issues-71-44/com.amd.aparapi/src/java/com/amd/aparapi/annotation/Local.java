package com.amd.aparapi.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 *  We can use this Annotation to 'tag' intended local buffers. 
 *  
 *  So we can either annotate the buffer
 *  <pre><code>
 *  &#64Local int[] buffer = new int[1024];
 *  </code></pre>
 *   Or use a special suffix 
 *  <pre><code>
 *  int[] buffer_$local$ = new int[1024];
 *  </code></pre>
 *  
 *  @see LOCAL_SUFFIX
 * 
 * 
 */
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
public @interface Local {
   String value();
}
