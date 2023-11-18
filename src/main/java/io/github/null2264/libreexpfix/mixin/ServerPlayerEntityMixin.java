package io.github.null2264.libreexpfix.mixin;

import net.minecraft.network.protocol.game.ClientboundPlayerAbilitiesPacket;
import net.minecraft.network.protocol.game.ClientboundSetExperiencePacket;
import net.minecraft.network.protocol.game.ClientboundUpdateMobEffectPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerPlayer.class)
public abstract class ServerPlayerEntityMixin
{
    @Shadow
    public ServerGamePacketListenerImpl connection;

    @Inject(method = "triggerDimensionChangeTriggers", at = @At("TAIL"))
    private void afterWorldChanged(ServerLevel origin, CallbackInfo ci) {
        /* Tests:
         *
         * - /effect give @p minecraft:night_vision 99999 1 true
         * - /execute in the_nether run tp @s ~ 10 ~
         * - /execute in overworld run tp @s ~ 10 ~
         */
        ServerPlayer player = (ServerPlayer) (Object) this;
        connection.send(new ClientboundSetExperiencePacket(
                player.experienceProgress,
                player.totalExperience,
                player.experienceLevel
        ));
        connection.send(new ClientboundPlayerAbilitiesPacket(player.getAbilities()));
        player.getActiveEffects().forEach(instance -> connection.send(new ClientboundUpdateMobEffectPacket(player.getId(), instance)));
    }
}