package me.nyarikori.velocity.locale.litecommands;

import dev.rollczi.litecommands.velocity.LiteVelocityMessages;
import lombok.Builder;
import lombok.Data;

/**
 * @author NyariKori
 */
@Data
@Builder
public class LiteCommandsLocale {
   private LiteVelocityMessages invalidUsage;
   private LiteVelocityMessages playerOnly;
   private LiteVelocityMessages playerNotFound;
   private LiteVelocityMessages missingPermissions;
   private LiteVelocityMessages uuidInvalidFormat;
   private LiteVelocityMessages invalidNumber;
   private LiteVelocityMessages instantInvalidFormat;
   private LiteVelocityMessages serverNotFound;
   private LiteVelocityMessages notConnectedToAnyServer;
   private LiteVelocityMessages commandCooldown;
}
