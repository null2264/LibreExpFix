package io.github.null2264.libreexpfix.mixin;

import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundPlayerAbilitiesPacket;
import net.minecraft.network.protocol.game.ClientboundSetExperiencePacket;
import net.minecraft.network.protocol.game.ClientboundUpdateMobEffectPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerPlayer.class)
public abstract class ServerPlayerEntityMixin
{
    @Unique
    private void sendPacket(ServerGamePacketListenerImpl connection, Packet<?> packet) {
        //#if FABRIC>=1
        connection.send(packet);
        //#else
        // Workaround for Forge
        String majorVersion = net.minecraft.DetectedVersion.BUILT_IN.getName().split("\\.")[1];
        String func;
        if (majorVersion.equals("18"))
            func = "m_141995_";
        else
            func = "m_9829_";

        java.lang.reflect.Method method;
        try {
            method = connection.getClass().getMethod(func, Packet.class);
            method.invoke(connection, packet);
        } catch (NoSuchMethodException | java.lang.reflect.InvocationTargetException | IllegalAccessException ignored) {
            // Welp, nothing we can do
        }
        //#endif
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
        sendPacket(player.connection, new ClientboundSetExperiencePacket(
                player.experienceProgress,
                player.totalExperience,
                player.experienceLevel
        ));
        sendPacket(player.connection, new ClientboundPlayerAbilitiesPacket(player.getAbilities()));
        player.getActiveEffects().forEach(instance -> sendPacket(player.connection, new ClientboundUpdateMobEffectPacket(player.getId(), instance)));
    }
}