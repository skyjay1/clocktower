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
        event.enqueueWork(() -> ItemBlockRenderTypes.setRenderLayer(EventHandler.BlockReg.CLOCK, RenderType.cutout()));
        event.enqueueWork(() -> BlockEntityRenderers.register(EventHandler.BlockEntityReg.CLOCK_TYPE, ClockBlockEntityRenderer::new));
    }

    @SubscribeEvent
    public static void registerModel(final ModelRegistryEvent event) {
        ForgeModelBakery.addSpecialModel(ClockBlockEntityRenderer.HAND_2);
    }
}
