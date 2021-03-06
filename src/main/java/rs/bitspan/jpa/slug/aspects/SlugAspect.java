package rs.bitspan.jpa.slug.aspects;

import org.apache.commons.beanutils.PropertyUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Component;
import rs.bitspan.jpa.slug.annotations.Slug;
import rs.bitspan.jpa.slug.generation.SlugGenerationStrategy;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

@Aspect
@Component
public class SlugAspect {

    /**
     * Find a source from which to generate the slug
     */
    private String slugOrigin(Object entity) throws IllegalAccessException {
        String origin = (String)PropertyUtils.getProperty(entity, "name");
        
        return origin;
    }

    /**
     * Set the slug on the entity
     */
    private void setSlug(Object entity, String slug) throws IllegalAccessException {
        PropertyUtils.setProperty(entity, "slug", slug);
        
        return;
    }

    /**
     * Find the id of the entity, if it exists (it may not exist if the entity is not persisted yet)
     */ 
    private Long id(Object entity) {
        Long id;
        try {
            id = (Long)PropertyUtils.getProperty(entity, "id");
        } catch (Exception e) {
            id = null;
        }

        return id;
    }

    /**
     * Save slug on CrudRepository.save()
     */
    @Before("execution(* org.springframework.data.repository.CrudRepository+.save(*))")
    public void adviseRepoSave(JoinPoint joinPoint) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        Object entity = joinPoint.getArgs()[0];

        for (Field field: entity.getClass().getDeclaredFields()) {
            Slug annotation = field.getAnnotation(Slug.class);

            if (annotation != null) {
                CrudRepository repository = (CrudRepository) joinPoint.getTarget();

                Long count = 0L;
                SlugGenerationStrategy generator = (SlugGenerationStrategy)annotation.strategy().newInstance();
                String slug = generator.generateSlug(slugOrigin(entity));

                if (id(entity) != null) {
                    Method method = repository.getClass().getMethod("countBySlugAndIdNot", String.class, Long.class);
                    count = (Long)method.invoke(repository, slug, id(entity));
                } else {
                    Method method = repository.getClass().getMethod("countBySlug", String.class);
                    count = (Long)method.invoke(repository, slug);
                }

                setSlug(entity, slug);
            }
        }
    }
}
