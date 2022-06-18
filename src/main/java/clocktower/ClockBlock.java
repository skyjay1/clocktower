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
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.Half;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;

public class ClockBlock extends HorizontalDirectionalBlock implements EntityBlock {

    public static final EnumProperty<Half> HALF = BlockStateProperties.HALF;

    public static final EnumProperty<Side> SIDE = EnumProperty.create("side", ClockBlock.Side.class);

    private static final VoxelShape SHAPE_NORTH = box(0, 0, 14, 16, 16, 16);
    private static final VoxelShape SHAPE_SOUTH = box(0, 0, 0, 16, 16, 2);
    private static final VoxelShape SHAPE_EAST = box(0, 0, 0, 2, 16, 16);
    private static final VoxelShape SHAPE_WEST = box(14, 0, 0, 16, 16, 16);

    private final Map<BlockState, VoxelShape> SHAPES = new HashMap<>();

    public ClockBlock(BlockBehaviour.Properties properties) {
        super(properties);
        this.registerDefaultState(this.getStateDefinition().any()
                .setValue(SIDE, Side.LEFT)
                .setValue(HALF, Half.BOTTOM)
                .setValue(FACING, Direction.NORTH));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(SIDE).add(HALF).add(FACING);
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        BlockState state = defaultBlockState();
        // determine facing direction
        Direction facing = context.getHorizontalDirection().getOpposite();
        // determine additional block positions
        BlockPos topLeft = context.getClickedPos().above();
        BlockPos bottomRight = context.getClickedPos().relative(facing.getCounterClockWise());
        BlockPos topRight = bottomRight.above();
        // check if all of the block positions can be replaced
        if(!context.getLevel().getBlockState(topLeft).canBeReplaced(context)
            || !context.getLevel().getBlockState(bottomRight).canBeReplaced(context)
            || !context.getLevel().getBlockState(topRight).canBeReplaced(context)) {
            return null;
        }
        // place with correct facing direction
        return state.setValue(FACING, facing);
    }

    @Override
    public void onPlace(BlockState stateIn, Level level, BlockPos pos, BlockState oldState, boolean isMoving) {
        if (stateIn.getValue(HALF) == Half.BOTTOM && stateIn.getValue(SIDE) == Side.LEFT) {
            Direction facing = stateIn.getValue(FACING);
            // create blockstates
            BlockState topLeft = defaultBlockState().setValue(HALF, Half.TOP)
                    .setValue(SIDE, Side.LEFT).setValue(FACING, facing);
            BlockState bottomRight = defaultBlockState().setValue(HALF, Half.BOTTOM)
                    .setValue(SIDE, Side.RIGHT).setValue(FACING, facing);
            BlockState topRight = defaultBlockState().setValue(HALF, Half.TOP)
                    .setValue(SIDE, Side.RIGHT).setValue(FACING, facing);
            // set blocks
            level.setBlock(pos.above(), topLeft, Block.UPDATE_ALL);
            level.setBlock(pos.relative(facing.getCounterClockWise()), bottomRight, Block.UPDATE_ALL);
            level.setBlock(pos.above().relative(facing.getCounterClockWise()), topRight, Block.UPDATE_ALL);
        }
    }

    @Override
    public void destroy(LevelAccessor level, BlockPos pos, BlockState state) {
        BlockPos half;
        BlockPos side;
        BlockPos diag;
        // remove opposite side when destroyed
        Direction facing = state.getValue(FACING);
        if(state.getValue(SIDE) == Side.LEFT) {
            side = pos.relative(facing.getCounterClockWise());
        } else {
            side = pos.relative(facing.getClockWise());
        }
        // remove opposite half and diagonal when destroyed
        if (state.getValue(HALF) == Half.BOTTOM) {
            half = pos.above();
            diag = side.above();
        } else {
            half = pos.below();
            diag = side.below();
        }
        // remove blocks
        level.destroyBlock(half, true);
        level.destroyBlock(side, true);
        level.destroyBlock(diag, true);
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter reader, BlockPos pos, CollisionContext context) {
        return SHAPES.computeIfAbsent(state, ClockBlock::computeShape);
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

    @Override
    public PushReaction getPistonPushReaction(BlockState state) {
        return PushReaction.DESTROY;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        if(state.getValue(HALF) == Half.BOTTOM && state.getValue(SIDE) == Side.LEFT) {
            return new ClockBlockEntity(pos, state);
        }
        return null;
    }

    public static enum Side implements StringRepresentable {
        LEFT("left"),
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
