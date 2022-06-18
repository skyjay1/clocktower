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

        @ObjectHolder("clock2")
        public static final Block CLOCK2 = null;

        @ObjectHolder("clock3")
        public static final Block CLOCK3 = null;

        @SubscribeEvent
        public static void register(final RegistryEvent.Register<Block> event) {
            event.getRegistry().register(new Clock2Block(BlockBehaviour.Properties.of(Material.WOOD)
                    .noCollission().noOcclusion().strength(2.0F, 3.0F).sound(SoundType.WOOD))
                    .setRegistryName(ClockTower.MODID, "clock2"));

            event.getRegistry().register(new Clock3Block(BlockBehaviour.Properties.of(Material.WOOD)
                    .noCollission().noOcclusion().strength(2.0F, 3.0F).sound(SoundType.WOOD))
                    .setRegistryName(ClockTower.MODID, "clock3"));
        }
    }

    @ObjectHolder(ClockTower.MODID)
    public static final class BlockEntityReg {

        @ObjectHolder("clock2")
        public static final BlockEntityType<Clock2BlockEntity> CLOCK2_TYPE = null;

        @ObjectHolder("clock3")
        public static final BlockEntityType<Clock3BlockEntity> CLOCK3_TYPE = null;

        @SubscribeEvent
        public static void register(final RegistryEvent.Register<BlockEntityType<?>> event) {
            BlockEntityType<Clock2BlockEntity> clockType = BlockEntityType.Builder.of(Clock2BlockEntity::new,
                    BlockReg.CLOCK2).build(null);
            event.getRegistry().register(clockType.setRegistryName(ClockTower.MODID, "clock2"));

            BlockEntityType<Clock3BlockEntity> bigClockBlockEntity = BlockEntityType.Builder.of(Clock3BlockEntity::new,
                    BlockReg.CLOCK3).build(null);
            event.getRegistry().register(bigClockBlockEntity.setRegistryName(ClockTower.MODID, "clock3"));
        }
    }

    @ObjectHolder(ClockTower.MODID)
    public static final class ItemReg {

        @SubscribeEvent
        public static void register(final RegistryEvent.Register<Item> event) {
            event.getRegistry().register(
                    new BlockItem(BlockReg.CLOCK2, new Item.Properties().tab(CreativeModeTab.TAB_DECORATIONS))
                            .setRegistryName(ClockTower.MODID, "clock2"));

            event.getRegistry().register(
                    new BlockItem(BlockReg.CLOCK3, new Item.Properties().tab(CreativeModeTab.TAB_DECORATIONS))
                            .setRegistryName(ClockTower.MODID, "clock3"));
        }
    }
}
