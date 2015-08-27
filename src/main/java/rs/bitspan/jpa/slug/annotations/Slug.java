package rs.bitspan.jpa.slug.annotations;

import rs.bitspan.jpa.slug.generation.DefaultSlugGenerationStrategy;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface Slug {

    String source() default "title";
    Class strategy() default DefaultSlugGenerationStrategy.class;
}
