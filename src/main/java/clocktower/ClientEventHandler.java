package clocktower;

import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.RenderTypeLookup;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

public class ClientEventHandler {

    @SubscribeEvent
    public static void setup(final FMLClientSetupEvent event) {
        event.enqueueWork(() -> RenderTypeLookup.setRenderLayer(EventHandler.BlockReg.CLOCK, RenderType.cutout()));
        event.enqueueWork(() -> ClientRegistry.bindTileEntityRenderer(EventHandler.BlockEntityReg.CLOCK_TYPE, ClockBlockEntityRenderer::new));
    }

    @SubscribeEvent
    public static void registerModel(final ModelRegistryEvent event) {
        ModelLoader.addSpecialModel(ClockBlockEntityRenderer.HAND_2);
    }
}
