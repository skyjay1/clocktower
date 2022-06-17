package clocktower;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.HorizontalBlock;
import net.minecraft.block.material.PushReaction;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.state.EnumProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.state.properties.Half;
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
import java.util.HashMap;
import java.util.Map;

public class ClockBlock extends HorizontalBlock {

    public static final EnumProperty<Half> HALF = BlockStateProperties.HALF;

    public static final EnumProperty<Side> SIDE = EnumProperty.create("side", ClockBlock.Side.class);

    private static final VoxelShape SHAPE_NORTH = box(0, 0, 14, 16, 16, 16);
    private static final VoxelShape SHAPE_SOUTH = box(0, 0, 0, 16, 16, 2);
    private static final VoxelShape SHAPE_EAST = box(0, 0, 0, 2, 16, 16);
    private static final VoxelShape SHAPE_WEST = box(14, 0, 0, 16, 16, 16);

    private final Map<BlockState, VoxelShape> SHAPES = new HashMap<>();

    public ClockBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.getStateDefinition().any()
                .setValue(SIDE, Side.LEFT)
                .setValue(HALF, Half.BOTTOM)
                .setValue(FACING, Direction.NORTH));
    }

    @Override
    protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> builder) {
        builder.add(SIDE).add(HALF).add(FACING);
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockItemUseContext context) {
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
    public void onPlace(BlockState stateIn, World level, BlockPos pos, BlockState oldState, boolean isMoving) {
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
            level.setBlock(pos.above(), topLeft, Constants.BlockFlags.DEFAULT);
            level.setBlock(pos.relative(facing.getCounterClockWise()), bottomRight, Constants.BlockFlags.DEFAULT);
            level.setBlock(pos.above().relative(facing.getCounterClockWise()), topRight, Constants.BlockFlags.DEFAULT);
        }
    }

    @Override
    public void destroy(IWorld level, BlockPos pos, BlockState state) {
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
    public VoxelShape getShape(BlockState state, IBlockReader reader, BlockPos pos, ISelectionContext context) {
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

    public PushReaction getPistonPushReaction(BlockState state) {
        return PushReaction.DESTROY;
    }

    @Override
    public boolean hasTileEntity(BlockState state) {
        return state.getValue(HALF) == Half.BOTTOM && state.getValue(SIDE) == Side.LEFT;
    }

    @Nullable
    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world) {
        return new ClockBlockEntity();
    }

    public static enum Side implements IStringSerializable {
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
