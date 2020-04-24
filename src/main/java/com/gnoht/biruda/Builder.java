package com.gnoht.biruda;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation that is used to auto generate a <a href="https://en.wikipedia.org/wiki/Builder_pattern">builder</a>
 * for a target class, constructor, or method. The process does not modify
 * existing code, the generated builder class will be in the same package as
 * the target class.
 * <p>
 * If this annotation is applied to a target class, keep in the mind the following:
 * <ul>
 *   <li>a non-private, non-empty constructor is required</li>
 *   <li>if multiple non-private, non-empty constructors are defined, the first to match parameter types and count with the target class fields will be used</li>
 *   <li>@{@link Ignored} can be applied to the target class fields, to fine tune which fields get compared to the list of constructor parameters</li>
 * </ul>
 * <p>
 * If this annotation is applied to a method, the method must be accessible to
 * the generated builder class (e.g, "public" or in same package of return type
 * if "package" or "protected" modifier).
 *
 * @author ikumen@gnoht.com
 */
@Target({ElementType.TYPE, ElementType.CONSTRUCTOR, ElementType.METHOD})
@Retention(RetentionPolicy.SOURCE)
public @interface Builder {

    /**
     * Customize the generated builder class name with this optional prefix.
     * By default the generated builder class name is TargetClassBuilder.
     * @return prefix or ""
     */
    String prefix() default "";

    /**
     * Customize the generated builder class name with this optional suffix.
     * By default the generated builder class name is TargetClassBuilder.
     * @return suffix or "Builder"
     */
    String suffix() default "Builder";

    /**
     * Allows generating an additional builder "of" method from an existing
     * target class instance.
     * @return true if we should generate the additional "of" method
     */
    boolean allowOfExisting() default true;

    /**
     * Can be used in conjunction with {@link Builder} when Builder is annotated
     * at the target class. It helps with fine tuning which fields are included
     * during constructor resolution. Simply add this to the target classes fields
     * that should be ignored by the auto-generation process.
     */
    @Target(ElementType.FIELD)
    @Retention(RetentionPolicy.SOURCE)
    @interface Ignored {
    }
}
