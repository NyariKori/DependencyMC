package me.nyarikori.velocity.injector;

import com.velocitypowered.api.command.Command;
import com.velocitypowered.api.command.CommandManager;
import com.velocitypowered.api.command.CommandMeta;
import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.proxy.ProxyServer;
import dev.rollczi.litecommands.LiteCommandsBuilder;
import dev.rollczi.litecommands.argument.resolver.ArgumentResolverBase;
import dev.rollczi.litecommands.velocity.LiteVelocityFactory;
import dev.rollczi.litecommands.velocity.LiteVelocityMessages;
import dev.rollczi.litecommands.velocity.LiteVelocitySettings;
import lombok.Setter;
import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;
import me.nyarikori.commons.annotation.command.CommandType;
import me.nyarikori.commons.annotation.command.NCommand;
import me.nyarikori.commons.annotation.command.litecommands.NArgument;
import me.nyarikori.commons.container.DependencyContainer;
import me.nyarikori.commons.injector.InjectorInitializer;
import me.nyarikori.commons.provider.DependencyProvider;
import me.nyarikori.velocity.locale.litecommands.LiteCommandsLocale;
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
public class VelocityInjectorInitializer {
    @Setter
    private LiteCommandsLocale liteCommandsLocale;

    public void setProvider(@NotNull DependencyProvider provider) {
        InjectorInitializer.setProvider(provider);
    }

    @SneakyThrows
    @SuppressWarnings("all")
    public void init(@NotNull ProxyServer proxy, @NotNull Object plugin, @NotNull String basePackage) {
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

            Object instance = DependencyContainer.getDependency(clazz);
            if (commandAnnotation.commandType() == CommandType.LITE_COMMANDS) {
                liteCommandsArray.add(instance);
            } else {
                String commandName = commandAnnotation.commandName();
                CommandManager commandManager = proxy.getCommandManager();
                CommandMeta commandMeta = commandManager.metaBuilder(commandName)
                        .aliases(commandAnnotation.aliases())
                        .plugin(plugin)
                        .build();
                commandManager.register(commandMeta, (Command) instance);
            }
        });

        if (liteCommandsArray.isEmpty()) {
            return;
        }

        LiteCommandsBuilder<CommandSource, LiteVelocitySettings, ?> builder =
                LiteVelocityFactory.builder(proxy).commands(liteCommandsArray.toArray());

        if (liteCommandsLocale != null) {
            builder.message(LiteVelocityMessages.INVALID_USAGE, liteCommandsLocale.getInvalidUsage());
            builder.message(LiteVelocityMessages.PLAYER_ONLY, liteCommandsLocale.getPlayerOnly());
            builder.message(LiteVelocityMessages.PLAYER_NOT_FOUND, liteCommandsLocale.getPlayerNotFound());
            builder.message(LiteVelocityMessages.MISSING_PERMISSIONS, liteCommandsLocale.getMissingPermissions());
            builder.message(LiteVelocityMessages.UUID_INVALID_FORMAT, liteCommandsLocale.getUuidInvalidFormat());
            builder.message(LiteVelocityMessages.INVALID_NUMBER, liteCommandsLocale.getInvalidNumber());
            builder.message(LiteVelocityMessages.INSTANT_INVALID_FORMAT, liteCommandsLocale.getInstantInvalidFormat());
            builder.message(LiteVelocityMessages.SERVER_NOT_FOUND, liteCommandsLocale.getServerNotFound());
            builder.message(LiteVelocityMessages.NOT_CONNECTED_TO_ANY_SERVER, liteCommandsLocale.getNotConnectedToAnyServer());
            builder.message(LiteVelocityMessages.COMMAND_COOLDOWN, liteCommandsLocale.getCommandCooldown());
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
