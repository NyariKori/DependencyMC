package me.nyarikori.bukkit.injector;

import dev.rollczi.litecommands.bukkit.LiteBukkitFactory;
import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;
import me.nyarikori.bukkit.annotation.command.BukkitCommand;
import me.nyarikori.bukkit.annotation.command.Commands;
import me.nyarikori.commons.container.DependencyContainer;
import me.nyarikori.commons.injector.InjectorInitializer;
import me.nyarikori.commons.provider.DependencyProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.reflections.Reflections;
import org.reflections.scanners.Scanners;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * @author NyariKori
 */
@UtilityClass
public final class BukkitInjectorInitializer extends JavaPlugin {
    public void setProvider(@NotNull DependencyProvider provider) {
        InjectorInitializer.setProvider(provider);
    }

    @SneakyThrows
    @SuppressWarnings("all")
    public void init(@NotNull String basePackage) {
        InjectorInitializer.init(basePackage);

        Reflections reflections = new Reflections(new ConfigurationBuilder()
                .setUrls(ClasspathHelper.forPackage(basePackage))
                .setScanners(Scanners.TypesAnnotated));

        Set<Class<?>> commandClassesSet = reflections.getTypesAnnotatedWith(BukkitCommand.class);
        List<Object> litecommandsArray = new ArrayList<>();

        commandClassesSet.forEach(clazz -> {
            if (!DependencyContainer.isDependencyRegistered(clazz)) {
                return;
            }

            BukkitCommand commandAnnotation = clazz.getAnnotation(BukkitCommand.class);
            assert commandAnnotation != null;

            if (commandAnnotation.commandType() == Commands.LITECOMMANDS) {
                litecommandsArray.add(DependencyContainer.getDependency(clazz));
            } else {
                // TODO: Логика обычных команд
            }
        });

        LiteBukkitFactory.builder().settings(settings ->
                settings.nativePermissions(true).fallbackPrefix("di")).commands(litecommandsArray);
    }
}
