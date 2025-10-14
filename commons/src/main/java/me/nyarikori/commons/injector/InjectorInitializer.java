package me.nyarikori.commons.injector;

import lombok.Setter;
import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;
import me.nyarikori.commons.Lifecycle;
import me.nyarikori.commons.annotation.Autowired;
import me.nyarikori.commons.annotation.Component;
import me.nyarikori.commons.annotation.Service;
import me.nyarikori.commons.container.DependencyContainer;
import me.nyarikori.commons.provider.DependencyProvider;
import org.jetbrains.annotations.NotNull;
import org.reflections.Reflections;
import org.reflections.scanners.Scanners;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Objects;
import java.util.Set;

/**
 * @author NyariKori
 */
@UtilityClass
public class InjectorInitializer {
    @Setter
    private DependencyProvider provider;

    @SneakyThrows
    public void init(@NotNull String basePackage) {
        provider.register();

        Reflections reflections = new Reflections(new ConfigurationBuilder()
                        .setUrls(ClasspathHelper.forPackage(basePackage))
                        .setScanners(Scanners.TypesAnnotated));

        Set<Class<?>> serviceSet = reflections.getTypesAnnotatedWith(Service.class);
        Set<Class<?>> componentSet = reflections.getTypesAnnotatedWith(Component.class);
        componentSet.removeAll(serviceSet);

        for (Class<?> clazz : serviceSet) {
            Object instance = clazz.getDeclaredConstructor().newInstance();
            DependencyContainer.registerService(clazz, instance);
        }

        for (Class<?> clazz: componentSet) {
            Object instance = clazz.getDeclaredConstructor().newInstance();
            DependencyContainer.registerDependency(clazz, instance);
        }

        serviceSet.stream()
                .map(DependencyContainer::getDependency)
                .filter(Objects::nonNull)
                .forEach(service -> {
                    process(service);
                    ((Lifecycle) service).enable();
                });

        componentSet.stream()
                .map(DependencyContainer::getDependency)
                .filter(Objects::nonNull)
                .forEach(InjectorInitializer::process);
    }

    private void process(Object object) {
        Class<?> clazz = object.getClass();
        while (clazz != null) {
            processFields(object, clazz.getDeclaredFields());
            clazz = clazz.getSuperclass();
        }
    }

    private void processFields(Object object, Field[] fields) {
        Arrays.stream(fields)
                .filter(field -> field.isAnnotationPresent(Autowired.class))
                .forEach(field -> injectField(object, field));
    }

    @SneakyThrows
    private void injectField(Object object, Field field) {
        if (!field.isAnnotationPresent(Autowired.class)) {
            return;
        }

        field.setAccessible(true);
        Class<?> fieldType = field.getType();

        field.set(object, DependencyContainer.getDependency(fieldType));
    }
}
