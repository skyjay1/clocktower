package clocktower;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.HorizontalBlock;
import net.minecraft.block.material.PushReaction;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.state.EnumProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Clock3Block extends HorizontalBlock {

    public static final EnumProperty<Layer> LAYER = EnumProperty.create("layer", Clock3Block.Layer.class);

    public static final EnumProperty<Side> SIDE = EnumProperty.create("side", Clock3Block.Side.class);

    private static final VoxelShape SHAPE_NORTH = box(0, 0, 14, 16, 16, 16);
    private static final VoxelShape SHAPE_SOUTH = box(0, 0, 0, 16, 16, 2);
    private static final VoxelShape SHAPE_EAST = box(0, 0, 0, 2, 16, 16);
    private static final VoxelShape SHAPE_WEST = box(14, 0, 0, 16, 16, 16);

    private final Map<BlockState, VoxelShape> SHAPES = new HashMap<>();

    public Clock3Block(Properties properties) {
        super(properties);
        this.registerDefaultState(this.getStateDefinition().any()
                .setValue(SIDE, Side.MIDDLE)
                .setValue(LAYER, Layer.MIDDLE)
                .setValue(FACING, Direction.NORTH));
    }

    @Override
    protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> builder) {
        builder.add(SIDE).add(LAYER).add(FACING);
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockItemUseContext context) {
        BlockState state = defaultBlockState();
        // determine facing direction
        Direction facing = context.getHorizontalDirection().getOpposite();
        // determine if all block positions can be replaced
        for(BlockPos p : getSurrounding(context.getClickedPos(), facing)) {
            if(!context.getLevel().getBlockState(p).canBeReplaced(context)) {
                return null;
            }
        }
        // place with correct facing direction
        return state.setValue(FACING, facing);
    }

    @Override
    public void onPlace(BlockState stateIn, World level, BlockPos pos, BlockState oldState, boolean isMoving) {
        if (stateIn.getValue(LAYER) == Layer.MIDDLE && stateIn.getValue(SIDE) == Side.MIDDLE) {
            Direction facing = stateIn.getValue(FACING);
            // create blockstates in clockwise order starting at top
            BlockState topCenter = defaultBlockState().setValue(LAYER, Layer.TOP)
                    .setValue(SIDE, Side.MIDDLE).setValue(FACING, facing);
            BlockState topRight = defaultBlockState().setValue(LAYER, Layer.TOP)
                    .setValue(SIDE, Side.RIGHT).setValue(FACING, facing);
            BlockState centerRight = defaultBlockState().setValue(LAYER, Layer.MIDDLE)
                    .setValue(SIDE, Side.RIGHT).setValue(FACING, facing);
            BlockState bottomRight = defaultBlockState().setValue(LAYER, Layer.BOTTOM)
                    .setValue(SIDE, Side.RIGHT).setValue(FACING, facing);
            BlockState bottomCenter = defaultBlockState().setValue(LAYER, Layer.BOTTOM)
                    .setValue(SIDE, Side.MIDDLE).setValue(FACING, facing);
            BlockState bottomLeft = defaultBlockState().setValue(LAYER, Layer.BOTTOM)
                    .setValue(SIDE, Side.LEFT).setValue(FACING, facing);
            BlockState centerLeft = defaultBlockState().setValue(LAYER, Layer.MIDDLE)
                    .setValue(SIDE, Side.LEFT).setValue(FACING, facing);
            BlockState topLeft = defaultBlockState().setValue(LAYER, Layer.TOP)
                    .setValue(SIDE, Side.LEFT).setValue(FACING, facing);
            // set each block using a mutable blockpos
            BlockPos.Mutable mutable = new BlockPos.Mutable().set(pos);
            level.setBlock(mutable.move(Direction.UP), topCenter, Constants.BlockFlags.DEFAULT);
            level.setBlock(mutable.move(facing.getCounterClockWise()), topRight, Constants.BlockFlags.DEFAULT);
            level.setBlock(mutable.move(Direction.DOWN), centerRight, Constants.BlockFlags.DEFAULT);
            level.setBlock(mutable.move(Direction.DOWN), bottomRight, Constants.BlockFlags.DEFAULT);
            level.setBlock(mutable.set(pos.below()), bottomCenter, Constants.BlockFlags.DEFAULT);
            level.setBlock(mutable.move(facing.getClockWise()), bottomLeft, Constants.BlockFlags.DEFAULT);
            level.setBlock(mutable.move(Direction.UP), centerLeft, Constants.BlockFlags.DEFAULT);
            level.setBlock(mutable.move(Direction.UP), topLeft, Constants.BlockFlags.DEFAULT);
        }
    }

    @Override
    public void destroy(IWorld level, BlockPos pos, BlockState state) {
        Side side = state.getValue(SIDE);
        Layer layer = state.getValue(LAYER);
        BlockPos center = pos;
        // determine side
        Direction facing = state.getValue(FACING);
        if(side == Side.LEFT) {
            center = center.relative(facing.getCounterClockWise());
        } else if(side == Side.RIGHT) {
            center = center.relative(facing.getClockWise());
        }
        // determine layer
        if(layer == Layer.TOP) {
            center = center.below();
        } else if(layer == Layer.BOTTOM) {
            center = center.above();
        }
        // destroy surrounding blocks
        for(BlockPos p : getSurrounding(center, facing)) {
            level.destroyBlock(p, true);
        }
        // destroy center block
        level.destroyBlock(center, true);
    }

    @Override
    public VoxelShape getShape(BlockState state, IBlockReader reader, BlockPos pos, ISelectionContext context) {
        return SHAPES.computeIfAbsent(state, Clock3Block::computeShape);
    }

    private static VoxelShape computeShape(final BlockState state) {
        switch (state.getValue(FACING))
        {
            default:
            case NORTH: return SHAPE_NORTH;
            case SOUTH: return SHAPE_SOUTH;
            case WEST: return SHAPE_WEST;
            case EAST: return SHAPE_EAST;
        }
    }

    public PushReaction getPistonPushReaction(BlockState state) {
        return PushReaction.DESTROY;
    }

    @Override
    public boolean hasTileEntity(BlockState state) {
        return state.getValue(LAYER) == Layer.MIDDLE && state.getValue(SIDE) == Side.MIDDLE;
    }

    @Nullable
    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world) {
        return new Clock3BlockEntity();
    }

    /**
     * calculates surrounding block positions from the center
     * @param center center position
     * @param facing facing direction
     * @return array of positions in clockwise order, starting from top middle
     */
    private List<BlockPos> getSurrounding(final BlockPos center, final Direction facing) {
        List<BlockPos> list = new ArrayList<>();
        // start at center, go above, and continue in clockwise order
        BlockPos.Mutable mutable = new BlockPos.Mutable().set(center);
        list.add(mutable.move(Direction.UP).immutable());
        list.add(mutable.move(facing.getCounterClockWise()).immutable());
        list.add(mutable.move(Direction.DOWN).immutable());
        list.add(mutable.move(Direction.DOWN).immutable());
        list.add(mutable.set(center.below()).immutable());
        list.add(mutable.move(facing.getClockWise()).immutable());
        list.add(mutable.move(Direction.UP).immutable());
        list.add(mutable.move(Direction.UP).immutable());
        return list;
    }

    public static enum Layer implements IStringSerializable {
        TOP("top"),
        MIDDLE("middle"),
        BOTTOM("bottom");

        private final String name;

        Layer(String name) {
            this.name = name;
        }

        @Override
        public String getSerializedName() {
            return this.name;
        }
    }

    public static enum Side implements IStringSerializable {
        LEFT("left"),
        MIDDLE("middle"),
        RIGHT("right");

        private final String name;

        Side(String name) {
            this.name = name;
        }

        @Override
        public String getSerializedName() {
            return this.name;
        }
    }

}
