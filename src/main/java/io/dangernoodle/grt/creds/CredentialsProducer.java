package io.dangernoodle.grt.creds;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.enterprise.inject.Alternative;
import javax.enterprise.inject.Stereotype;

/**
 * Annotation used on a <code>Credentials</code> implementation to override the default.
 * 
 * @since 0.8.0
 */
@Alternative
@Stereotype
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface CredentialsProducer
{
    // empty
}
