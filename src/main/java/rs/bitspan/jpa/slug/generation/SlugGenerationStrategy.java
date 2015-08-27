package rs.bitspan.jpa.slug.generation;

public interface SlugGenerationStrategy {

    String generateSlug(String source);
}
