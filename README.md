# DependencyMC
#### A simple dependency injection system for working with Bukkit, Velocity. Supports LiteCommands.

### Modules: 
- ⛏️ Bukkit: everything you need to work with DI in the Bukkit environment
- 🧠 Commons: the main DI brain, where everything used by Bukkit and Velocity modules is stored
- 🌐 Velocity: everything you need to work with DI in the Velocity environment

### importing to Maven:
TODO...

### Code Examples
#### Bukkit:
```java
package me.nyarikori.test;

import me.nyarikori.bukkit.injector.BukkitInjectorInitializer;
import me.nyarikori.commons.provider.BaseDependencyProvider;
import org.bukkit.plugin.java.JavaPlugin;

public final class Test extends JavaPlugin {

    @Override
    public void onEnable() {
        BukkitInjectorInitializer.setProvider(new BaseDependencyProvider());
        BukkitInjectorInitializer.init(this, "me.nyarikori"); // Your package
    }
}
```
#### Bukkit LiteCommands:
```java
package me.nyarikori.test;

import dev.rollczi.litecommands.annotations.async.Async;
import dev.rollczi.litecommands.annotations.command.Command;
import dev.rollczi.litecommands.annotations.context.Context;
import dev.rollczi.litecommands.annotations.execute.Execute;
import me.nyarikori.commons.annotation.Component;
import me.nyarikori.commons.annotation.command.CommandType;
import me.nyarikori.commons.annotation.command.NCommand;
import org.bukkit.entity.Player;

@Component
@NCommand(commandType = CommandType.LITE_COMMANDS)
@Command(name = "litecommandstest")
public class TestCommandOne {
    @Async
    @Execute
    void test(@Context Player sender) {
        sender.sendMessage("LiteCommands Test passed");
    }
}
```
#### Bukkit Base Commands:
```java
package me.nyarikori.test;

import me.nyarikori.commons.annotation.Component;
import me.nyarikori.commons.annotation.command.CommandType;
import me.nyarikori.commons.annotation.command.NCommand;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

@Component
@NCommand(commandType = CommandType.BASE_COMMANDS, commandName = "bukkitcommandstest")
public class TestCommandTwo implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        sender.sendMessage("BukkitCommands Test passed");
        return true;
    }
}
```

#### Velocity:
```java
// TODO
```

#### Velocity LiteCommands:
```java
// TODO
```

#### Velocity Base Commands:
```java
// TODO
```

### Other Code Examples

#### Providers:
```java
// TODO
```

#### LiteCommands Locale
```java
// TODO
```