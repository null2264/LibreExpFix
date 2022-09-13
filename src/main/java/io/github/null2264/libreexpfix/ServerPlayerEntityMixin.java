package io.github.null2264.libreexpfix;

import net.minecraft.network.packet.s2c.play.EntityStatusEffectS2CPacket;
import net.minecraft.network.packet.s2c.play.ExperienceBarUpdateS2CPacket;
import net.minecraft.network.packet.s2c.play.PlayerAbilitiesS2CPacket;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerPlayerEntity.class)
public abstract class ServerPlayerEntityMixin
{
    @Shadow
    public ServerPlayNetworkHandler networkHandler;

    @Inject(method = "worldChanged(Lnet/minecraft/server/world/ServerWorld;)V", at = @At("TAIL"))
    private void afterWorldChanged(ServerWorld origin, CallbackInfo ci) {
        /* Tests:
         *
         * - /effect give @p minecraft:night_vision 99999 1 true
         * - /tp ~ 325 ~
         * - /execute in the_nether run tp @s ~ ~ ~
         * - /execute in overworld run tp @s ~ ~ ~
         */
        ServerPlayerEntity player = (ServerPlayerEntity) (Object) this;
        networkHandler.sendPacket(new ExperienceBarUpdateS2CPacket(
                player.experienceProgress,
                player.totalExperience,
                player.experienceLevel
        ));
        networkHandler.sendPacket(new PlayerAbilitiesS2CPacket(player.getAbilities()));
        player.getStatusEffects().forEach(instance -> networkHandler.sendPacket(new EntityStatusEffectS2CPacket(player.getId(), instance)));
    }
}