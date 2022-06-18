package clocktower;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.config.ModConfigEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(ClockTower.MODID)
public final class ClockTower {

    public static final String MODID = "clocktower";

    private static final ForgeConfigSpec.Builder CONFIG_BUILDER = new ForgeConfigSpec.Builder();
    public static ClockConfig CONFIG = new ClockConfig(CONFIG_BUILDER);
    private static final ForgeConfigSpec CONFIG_SPEC = CONFIG_BUILDER.build();

    public ClockTower() {
        // mod bus events
        FMLJavaModLoadingContext.get().getModEventBus().register(EventHandler.BlockReg.class);
        FMLJavaModLoadingContext.get().getModEventBus().register(EventHandler.BlockEntityReg.class);
        FMLJavaModLoadingContext.get().getModEventBus().register(EventHandler.ItemReg.class);
        // config events
        ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, CONFIG_SPEC);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(ClockTower::loadConfig);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(ClockTower::reloadConfig);
        // forge bus events
        MinecraftForge.EVENT_BUS.register(EventHandler.class);
        // client-only events
        DistExecutor.runWhenOn(Dist.CLIENT, () -> () -> {
            FMLJavaModLoadingContext.get().getModEventBus().register(clocktower.ClientEventHandler.class);
        });
    }

    public static void loadConfig(final ModConfigEvent.Loading event) {
        CONFIG.bake();
    }

    public static void reloadConfig(final ModConfigEvent.Reloading event) {
        CONFIG.bake();
    }
}
