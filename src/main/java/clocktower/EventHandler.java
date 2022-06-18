package clocktower;

import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.Material;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.registries.ObjectHolder;

public class EventHandler {

    @ObjectHolder(ClockTower.MODID)
    public static final class BlockReg {

        @ObjectHolder("clock")
        public static final Block CLOCK = null;

        @SubscribeEvent
        public static void register(final RegistryEvent.Register<Block> event) {
            event.getRegistry().register(new ClockBlock(BlockBehaviour.Properties.of(Material.WOOD)
                    .noCollission().noOcclusion().strength(2.0F, 3.0F).sound(SoundType.WOOD))
                    .setRegistryName(ClockTower.MODID, "clock"));
        }
    }

    @ObjectHolder(ClockTower.MODID)
    public static final class BlockEntityReg {

        @ObjectHolder("clock")
        public static final BlockEntityType<ClockBlockEntity> CLOCK_TYPE = null;

        @SubscribeEvent
        public static void register(final RegistryEvent.Register<BlockEntityType<?>> event) {
            BlockEntityType<ClockBlockEntity> clockType = BlockEntityType.Builder.of(ClockBlockEntity::new,
                    BlockReg.CLOCK).build(null);
            event.getRegistry().register(clockType.setRegistryName(ClockTower.MODID, "clock"));
        }
    }

    @ObjectHolder(ClockTower.MODID)
    public static final class ItemReg {

        @SubscribeEvent
        public static void register(final RegistryEvent.Register<Item> event) {
            event.getRegistry().register(
                    new BlockItem(BlockReg.CLOCK, new Item.Properties().tab(CreativeModeTab.TAB_DECORATIONS))
                            .setRegistryName(ClockTower.MODID, "clock"));
        }
    }
}
