package me.nyarikori.bukkit.injector;

import dev.rollczi.litecommands.LiteCommandsBuilder;
import dev.rollczi.litecommands.argument.resolver.ArgumentResolverBase;
import dev.rollczi.litecommands.bukkit.LiteBukkitFactory;
import dev.rollczi.litecommands.bukkit.LiteBukkitMessages;
import dev.rollczi.litecommands.bukkit.LiteBukkitSettings;
import lombok.Setter;
import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;
import me.nyarikori.bukkit.locale.litecommands.LiteCommandsLocale;
import me.nyarikori.commons.annotation.command.CommandType;
import me.nyarikori.commons.annotation.command.NCommand;
import me.nyarikori.commons.annotation.command.litecommands.NArgument;
import me.nyarikori.commons.container.DependencyContainer;
import me.nyarikori.commons.injector.InjectorInitializer;
import me.nyarikori.commons.provider.DependencyProvider;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
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
public final class BukkitInjectorInitializer {
    @Setter
    private LiteCommandsLocale liteCommandsLocale;

    public void setProvider(@NotNull DependencyProvider provider) {
        InjectorInitializer.setProvider(provider);
    }

    @SneakyThrows
    @SuppressWarnings("all")
    public void init(@NotNull JavaPlugin plugin, @NotNull String basePackage) {
        InjectorInitializer.init(basePackage);

        Reflections reflections = new Reflections(new ConfigurationBuilder()
                .setUrls(ClasspathHelper.forPackage(basePackage))
                .setScanners(Scanners.TypesAnnotated));

        Set<Class<?>> commandClassesSet = reflections.getTypesAnnotatedWith(NCommand.class);
        List<Object> liteCommandsArray = new ArrayList<>();

        commandClassesSet.forEach(clazz -> {
            if (!DependencyContainer.isDependencyRegistered(clazz)) {
                return;
            }

            NCommand commandAnnotation = clazz.getAnnotation(NCommand.class);
            assert commandAnnotation != null;

            if (commandAnnotation.commandType() == CommandType.LITE_COMMANDS) {
                liteCommandsArray.add(DependencyContainer.getDependency(clazz));
            } else {
                String commandName = commandAnnotation.commandName();
                plugin.getCommand(commandName).setExecutor((CommandExecutor) DependencyContainer.getDependency(clazz));
            }
        });

        if (liteCommandsArray.isEmpty()) {
            return;
        }

        LiteCommandsBuilder<CommandSender, LiteBukkitSettings, ?> builder =
                LiteBukkitFactory.builder(plugin.getName(), plugin).commands(liteCommandsArray.toArray());

        if (liteCommandsLocale != null) {
            builder.message(LiteBukkitMessages.INVALID_USAGE, liteCommandsLocale.getInvalidUsage());
            builder.message(LiteBukkitMessages.PLAYER_ONLY, liteCommandsLocale.getPlayerOnly());
            builder.message(LiteBukkitMessages.PLAYER_NOT_FOUND, liteCommandsLocale.getPlayerNotFound());
            builder.message(LiteBukkitMessages.MISSING_PERMISSIONS, liteCommandsLocale.getMissingPermissions());
            builder.message(LiteBukkitMessages.WORLD_NOT_EXIST, liteCommandsLocale.getWorldNotExist());
            builder.message(LiteBukkitMessages.WORLD_PLAYER_ONLY, liteCommandsLocale.getWorldPlayerOnly());
            builder.message(LiteBukkitMessages.UUID_INVALID_FORMAT, liteCommandsLocale.getUuidInvalidFormat());
            builder.message(LiteBukkitMessages.LOCATION_INVALID_FORMAT, liteCommandsLocale.getLocationInvalidFormat());
            builder.message(LiteBukkitMessages.INVALID_NUMBER, liteCommandsLocale.getInvalidNumber());
            builder.message(LiteBukkitMessages.INSTANT_INVALID_FORMAT, liteCommandsLocale.getInstantInvalidFormat());
        }

        Set<Class<?>> liteCommandsArgumentClassesSet = reflections.getTypesAnnotatedWith(NArgument.class);

        liteCommandsArgumentClassesSet.forEach(clazz -> {
            if (!DependencyContainer.isDependencyRegistered(clazz)) {
                return;
            }

            NArgument argumentAnnotation = clazz.getAnnotation(NArgument.class);
            assert argumentAnnotation != null;

            builder.argument(argumentAnnotation.argument(), (ArgumentResolverBase) DependencyContainer.getDependency(clazz));
        });

        builder.build();
    }
}
