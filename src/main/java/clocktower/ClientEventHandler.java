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
        event.enqueueWork(() -> RenderTypeLookup.setRenderLayer(EventHandler.BlockReg.CLOCK2, RenderType.cutout()));
        event.enqueueWork(() -> ClientRegistry.bindTileEntityRenderer(EventHandler.BlockEntityReg.CLOCK2_TYPE, Clock2BlockEntityRenderer::new));

        event.enqueueWork(() -> RenderTypeLookup.setRenderLayer(EventHandler.BlockReg.CLOCK3, RenderType.cutout()));
        event.enqueueWork(() -> ClientRegistry.bindTileEntityRenderer(EventHandler.BlockEntityReg.CLOCK3_TYPE, Clock3BlockEntityRenderer::new));
    }

    @SubscribeEvent
    public static void registerModel(final ModelRegistryEvent event) {
        ModelLoader.addSpecialModel(Clock2BlockEntityRenderer.HAND_2);
        ModelLoader.addSpecialModel(Clock3BlockEntityRenderer.HAND_3);
    }
}
