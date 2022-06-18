package clocktower;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Clock3Block extends HorizontalDirectionalBlock implements EntityBlock {

    public static final EnumProperty<Layer> LAYER = EnumProperty.create("layer", Layer.class);

    public static final EnumProperty<Side> SIDE = EnumProperty.create("side", Side.class);

    private static final VoxelShape SHAPE_NORTH = box(0, 0, 14, 16, 16, 16);
    private static final VoxelShape SHAPE_SOUTH = box(0, 0, 0, 16, 16, 2);
    private static final VoxelShape SHAPE_EAST = box(0, 0, 0, 2, 16, 16);
    private static final VoxelShape SHAPE_WEST = box(14, 0, 0, 16, 16, 16);

    private final Map<BlockState, VoxelShape> SHAPES = new HashMap<>();

    public Clock3Block(BlockBehaviour.Properties properties) {
        super(properties);
        this.registerDefaultState(this.getStateDefinition().any()
                .setValue(SIDE, Side.MIDDLE)
                .setValue(LAYER, Layer.MIDDLE)
                .setValue(FACING, Direction.NORTH));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(SIDE).add(LAYER).add(FACING);
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        BlockState state = defaultBlockState();
        // determine facing direction
        Direction facing = context.getHorizontalDirection().getOpposite();
        // determine if all block positions can be replaced
        for (BlockPos p : getSurrounding(context.getClickedPos(), facing)) {
            if (!context.getLevel().getBlockState(p).canBeReplaced(context)) {
                return null;
            }
        }
        // place with correct facing direction
        return state.setValue(FACING, facing);
    }

    @Override
    public void onPlace(BlockState stateIn, Level level, BlockPos pos, BlockState oldState, boolean isMoving) {
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
            BlockPos.MutableBlockPos mutable = new BlockPos.MutableBlockPos().set(pos);
            level.setBlock(mutable.move(Direction.UP), topCenter, Block.UPDATE_ALL);
            level.setBlock(mutable.move(facing.getCounterClockWise()), topRight, Block.UPDATE_ALL);
            level.setBlock(mutable.move(Direction.DOWN), centerRight, Block.UPDATE_ALL);
            level.setBlock(mutable.move(Direction.DOWN), bottomRight, Block.UPDATE_ALL);
            level.setBlock(mutable.set(pos.below()), bottomCenter, Block.UPDATE_ALL);
            level.setBlock(mutable.move(facing.getClockWise()), bottomLeft, Block.UPDATE_ALL);
            level.setBlock(mutable.move(Direction.UP), centerLeft, Block.UPDATE_ALL);
            level.setBlock(mutable.move(Direction.UP), topLeft, Block.UPDATE_ALL);
        }
    }

    @Override
    public void onRemove(BlockState oldState, Level level, BlockPos pos, BlockState newState, boolean isMoving) {
        super.onRemove(oldState, level, pos, newState, isMoving);
        destroy(level, pos, oldState);
    }

    @Override
    public void destroy(LevelAccessor level, BlockPos pos, BlockState state) {
        Side side = state.getValue(SIDE);
        Layer layer = state.getValue(LAYER);
        BlockPos center = pos;
        // determine side
        Direction facing = state.getValue(FACING);
        if (side == Side.LEFT) {
            center = center.relative(facing.getCounterClockWise());
        } else if (side == Side.RIGHT) {
            center = center.relative(facing.getClockWise());
        }
        // determine layer
        if (layer == Layer.TOP) {
            center = center.below();
        } else if (layer == Layer.BOTTOM) {
            center = center.above();
        }
        // destroy surrounding blocks
        for (BlockPos p : getSurrounding(center, facing)) {
            level.destroyBlock(p, true);
        }
        // destroy center block
        level.destroyBlock(center, true);
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter reader, BlockPos pos, CollisionContext context) {
        return SHAPES.computeIfAbsent(state, Clock3Block::computeShape);
    }

    private static VoxelShape computeShape(final BlockState state) {
        switch (state.getValue(FACING)) {
            default:
            case NORTH:
                return SHAPE_NORTH;
            case SOUTH:
                return SHAPE_SOUTH;
            case WEST:
                return SHAPE_WEST;
            case EAST:
                return SHAPE_EAST;
        }
    }

    @Override
    public PushReaction getPistonPushReaction(BlockState state) {
        return PushReaction.DESTROY;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        if (state.getValue(LAYER) == Layer.MIDDLE && state.getValue(SIDE) == Side.MIDDLE) {
            return new Clock3BlockEntity(pos, state);
        }
        return null;
    }

    /**
     * calculates surrounding block positions from the center
     *
     * @param center center position
     * @param facing facing direction
     * @return array of positions in clockwise order, starting from top middle
     */
    private List<BlockPos> getSurrounding(final BlockPos center, final Direction facing) {
        List<BlockPos> list = new ArrayList<>();
        // start at center, go above, and continue in clockwise order
        BlockPos.MutableBlockPos mutable = new BlockPos.MutableBlockPos().set(center);
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

    public static enum Layer implements StringRepresentable {
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

    public static enum Side implements StringRepresentable {
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
