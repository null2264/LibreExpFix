//#if FORGE>=1
package io.github.null2264.libreexpfix.mixin;

import net.minecraft.network.Connection;
//#if MC>=1.20.2
//$$ import net.minecraft.server.network.ServerCommonPacketListenerImpl;
//#else
import net.minecraft.server.network.ServerGamePacketListenerImpl;
//#endif
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

//#if MC>=1.20.2
//$$ @Mixin(ServerCommonPacketListenerImpl.class)
//#else
@Mixin(ServerGamePacketListenerImpl.class)
//#endif
public interface ServerGamePacketListenerAccessor {
    @Accessor("connection")
    Connection getConn();
}
//#endif