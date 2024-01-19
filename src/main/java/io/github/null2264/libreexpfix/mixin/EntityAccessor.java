//#if FORGE>=1
package io.github.null2264.libreexpfix.mixin;

import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(Entity.class)
public interface EntityAccessor {
    @Accessor("id")
    int getEntityId();
}
//#endif