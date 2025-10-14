package me.nyarikori.commons.provider;

import me.nyarikori.commons.container.DependencyContainer;
import org.jetbrains.annotations.NotNull;

/**
 * @author NyariKori
 */
public abstract class DependencyProvider {
    public abstract void register();

    public void registerDependency(@NotNull Class<?> clazz, @NotNull Object instance) {
        DependencyContainer.registerDependency(clazz, instance);
    }
}
