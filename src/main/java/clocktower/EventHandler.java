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

        @ObjectHolder("clock2")
        public static final Block CLOCK2 = null;

        @ObjectHolder("clock3")
        public static final Block CLOCK3 = null;

        @SubscribeEvent
        public static void register(final RegistryEvent.Register<Block> event) {
            event.getRegistry().register(new Clock2Block(AbstractBlock.Properties.of(Material.WOOD)
                    .noCollission().noOcclusion().strength(2.0F, 3.0F).sound(SoundType.WOOD))
                    .setRegistryName(ClockTower.MODID, "clock2"));

            event.getRegistry().register(new Clock3Block(AbstractBlock.Properties.of(Material.WOOD)
                    .noCollission().noOcclusion().strength(2.0F, 3.0F).sound(SoundType.WOOD))
                    .setRegistryName(ClockTower.MODID, "clock3"));
        }
    }

    @ObjectHolder(ClockTower.MODID)
    public static final class BlockEntityReg {

        @ObjectHolder("clock2")
        public static final TileEntityType<Clock2BlockEntity> CLOCK2_TYPE = null;

        @ObjectHolder("clock3")
        public static final TileEntityType<Clock3BlockEntity> CLOCK3_TYPE = null;


        @SubscribeEvent
        public static void register(final RegistryEvent.Register<TileEntityType<?>> event) {
            TileEntityType<Clock2BlockEntity> clockBlockEntity = TileEntityType.Builder.of(Clock2BlockEntity::new,
                    BlockReg.CLOCK2).build(null);
            event.getRegistry().register(clockBlockEntity.setRegistryName(ClockTower.MODID, "clock2"));

            TileEntityType<Clock3BlockEntity> bigClockBlockEntity = TileEntityType.Builder.of(Clock3BlockEntity::new,
                    BlockReg.CLOCK3).build(null);
            event.getRegistry().register(bigClockBlockEntity.setRegistryName(ClockTower.MODID, "clock3"));
        }
    }

    @ObjectHolder(ClockTower.MODID)
    public static final class ItemReg {

        @SubscribeEvent
        public static void register(final RegistryEvent.Register<Item> event) {
            event.getRegistry().register(
                    new BlockItem(BlockReg.CLOCK2, new Item.Properties().tab(ItemGroup.TAB_DECORATIONS))
                            .setRegistryName(ClockTower.MODID, "clock2"));

            event.getRegistry().register(
                    new BlockItem(BlockReg.CLOCK3, new Item.Properties().tab(ItemGroup.TAB_DECORATIONS))
                            .setRegistryName(ClockTower.MODID, "clock3"));
        }
    }
}
