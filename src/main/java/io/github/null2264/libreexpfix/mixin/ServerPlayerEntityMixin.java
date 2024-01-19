package io.github.null2264.libreexpfix.mixin;

import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundPlayerAbilitiesPacket;
import net.minecraft.network.protocol.game.ClientboundSetExperiencePacket;
import net.minecraft.network.protocol.game.ClientboundUpdateMobEffectPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerPlayer.class)
public abstract class ServerPlayerEntityMixin
{
    @Unique
    private void sendPacket(Packet<?> packet) {
        ServerPlayer player = (ServerPlayer) (Object) this;
        player.connection.connection.send(packet);
    }

    @Inject(method = "triggerDimensionChangeTriggers", at = @At("TAIL"))
    private void afterWorldChanged(ServerLevel origin, CallbackInfo ci) {
        /* Tests:
         *
         * - /effect give @p minecraft:night_vision 99999 1 true
         * - /execute in the_nether run tp @s ~ 10 ~
         * - /execute in overworld run tp @s ~ 10 ~
         */
        ServerPlayer player = (ServerPlayer) (Object) this;
        sendPacket(new ClientboundSetExperiencePacket(
                player.experienceProgress,
                player.totalExperience,
                player.experienceLevel
        ));
        sendPacket(new ClientboundPlayerAbilitiesPacket(player.getAbilities()));
        player.getActiveEffects().forEach(instance -> sendPacket(
                new ClientboundUpdateMobEffectPacket(
                        //#if FABRIC>=1
                        player.getId(),
                        //#else
                        //$$ ((EntityAccessor) player).getEntityId(),
                        //#endif
                        instance
                ))
        );
    }
}