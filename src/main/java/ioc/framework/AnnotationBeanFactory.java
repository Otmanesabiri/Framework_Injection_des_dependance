package ioc.framework;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import org.reflections.Reflections;
import java.util.Set;
import org.reflections.scanners.SubTypesScanner;
import org.reflections.scanners.TypeAnnotationsScanner;
import org.reflections.util.ConfigurationBuilder;

public class AnnotationBeanFactory implements BeanFactory {
    private Map<String, Object> beans = new HashMap<>();

    public AnnotationBeanFactory(String... packageNames) {
        try {
            ConfigurationBuilder config = new ConfigurationBuilder()
                    .forPackages(packageNames)
                    .addScanners(new TypeAnnotationsScanner(), new SubTypesScanner());
            Reflections reflections = new Reflections(config);

            Set<Class<?>> annotatedClasses = reflections.getTypesAnnotatedWith(Component.class);

            for (Class<?> clazz : annotatedClasses) {
                Component componentAnnotation = clazz.getAnnotation(Component.class);
                String beanName = componentAnnotation.value().isEmpty() ? clazz.getSimpleName() : componentAnnotation.value();
                Object instance = clazz.getDeclaredConstructor().newInstance();
                beans.put(beanName, instance);
            }

            injectDependencies();
        } catch (Exception e) {
            throw new RuntimeException("Error creating annotation-based bean factory", e);
        }
    }

    private void injectDependencies() {
        beans.forEach((beanName, bean) -> {
            Class<?> clazz = bean.getClass();
            Field[] fields = clazz.getDeclaredFields();
            for (Field field : fields) {
                if (field.isAnnotationPresent(Inject.class)) {
                    Inject injectAnnotation = field.getAnnotation(Inject.class);
                    String dependencyName = injectAnnotation.value().isEmpty() ? field.getType().getSimpleName() : injectAnnotation.value();
                    Object dependency = getBean(dependencyName);
                    if (dependency != null) {
                        field.setAccessible(true);
                        try {
                            field.set(bean, dependency);
                        } catch (IllegalAccessException e) {
                            throw new RuntimeException("Error injecting dependency " + dependencyName + " into " + beanName, e);
                        }
                    }
                }
            }
        });
    }

    @Override
    public Object getBean(String beanName) {
        return beans.get(beanName);
    }
}
