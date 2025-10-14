package me.nyarikori.commons.container;

import lombok.experimental.UtilityClass;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author NyariKori
 */
@UtilityClass
public class DependencyContainer {
    private final Map<Class<?>, Object> serviceMap = new ConcurrentHashMap<>();
    private final Map<Class<?>, Object> dependencyMap = new ConcurrentHashMap<>();

    public void registerDependency(@NotNull Class<?> clazz, @NotNull Object object) {
        dependencyMap.put(clazz, object);
    }

    public void registerService(@NotNull Class<?> clazz, @NotNull Object object) {
        serviceMap.put(clazz, object);
    }

    public Object getDependency(@NotNull Class<?> clazz) {
        Object service = serviceMap.get(clazz);
        if (service != null) return service;
        return dependencyMap.get(clazz);
    }

    public boolean isDependencyRegistered(@NotNull Class<?> clazz) {
        return serviceMap.containsKey(clazz) || dependencyMap.containsKey(clazz);
    }
}
