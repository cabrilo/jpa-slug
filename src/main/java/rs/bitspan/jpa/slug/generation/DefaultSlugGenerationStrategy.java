package rs.bitspan.jpa.slug.generation;

import java.text.Normalizer;
import java.util.Locale;
import java.util.regex.Pattern;

public class DefaultSlugGenerationStrategy implements SlugGenerationStrategy {

    private static final Pattern NONLATIN = Pattern.compile("[^\\w-]");
    private static final Pattern WHITESPACE = Pattern.compile("[\\s]");

    public String generateSlug(String source) {
        String nowhitespace = WHITESPACE.matcher(source).replaceAll("-");
        String normalized = Normalizer.normalize(nowhitespace, Normalizer.Form.NFD);
        String slug = NONLATIN.matcher(normalized).replaceAll("");
        return slug.toLowerCase(Locale.ENGLISH);
    }
}
