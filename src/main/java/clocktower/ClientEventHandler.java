package clocktower;

import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ForgeModelBakery;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

public class ClientEventHandler {

    @SubscribeEvent
    public static void setup(final FMLClientSetupEvent event) {
        event.enqueueWork(() -> ItemBlockRenderTypes.setRenderLayer(EventHandler.BlockReg.CLOCK2, RenderType.cutout()));
        event.enqueueWork(() -> BlockEntityRenderers.register(EventHandler.BlockEntityReg.CLOCK2_TYPE, Clock2BlockEntityRenderer::new));

        event.enqueueWork(() -> ItemBlockRenderTypes.setRenderLayer(EventHandler.BlockReg.CLOCK3, RenderType.cutout()));
        event.enqueueWork(() -> BlockEntityRenderers.register(EventHandler.BlockEntityReg.CLOCK3_TYPE, Clock3BlockEntityRenderer::new));
    }

    @SubscribeEvent
    public static void registerModel(final ModelRegistryEvent event) {
        ForgeModelBakery.addSpecialModel(Clock2BlockEntityRenderer.HAND_2);
        ForgeModelBakery.addSpecialModel(Clock3BlockEntityRenderer.HAND_3);
    }
}
