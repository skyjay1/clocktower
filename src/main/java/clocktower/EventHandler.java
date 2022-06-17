package clocktower;

import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.tileentity.TileEntityType;
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
            event.getRegistry().register(new ClockBlock(AbstractBlock.Properties.of(Material.WOOD)
                    .noCollission().noOcclusion().strength(2.0F, 3.0F).sound(SoundType.WOOD))
                    .setRegistryName(ClockTower.MODID, "clock"));
        }
    }

    @ObjectHolder(ClockTower.MODID)
    public static final class BlockEntityReg {

        @ObjectHolder("clock")
        public static final TileEntityType<ClockBlockEntity> CLOCK_TYPE = null;

        @SubscribeEvent
        public static void register(final RegistryEvent.Register<TileEntityType<?>> event) {
            TileEntityType<ClockBlockEntity> tentDoorType = TileEntityType.Builder.of(ClockBlockEntity::new,
                    BlockReg.CLOCK).build(null);
            event.getRegistry().register(tentDoorType.setRegistryName(ClockTower.MODID, "clock"));
        }
    }

    @ObjectHolder(ClockTower.MODID)
    public static final class ItemReg {

        @SubscribeEvent
        public static void register(final RegistryEvent.Register<Item> event) {
            event.getRegistry().register(
                    new BlockItem(BlockReg.CLOCK, new Item.Properties().tab(ItemGroup.TAB_DECORATIONS))
                            .setRegistryName(ClockTower.MODID, "clock"));
        }
    }
}
