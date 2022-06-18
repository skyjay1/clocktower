package clocktower;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.RenderTypeLookup;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraftforge.client.model.data.EmptyModelData;

import java.util.EnumMap;
import java.util.Map;
import java.util.Random;

public class Clock2BlockEntityRenderer extends TileEntityRenderer<Clock2BlockEntity> {

    public static final ResourceLocation HAND_2 = new ResourceLocation(ClockTower.MODID, "block/clock_hand2");

    private final Map<Direction, Vector3d> rotations = new EnumMap<>(Direction.class);

    public Clock2BlockEntityRenderer(TileEntityRendererDispatcher rendererDispatcher) {
        super(rendererDispatcher);
        rotations.put(Direction.NORTH, new Vector3d(0, 0, 11.0D / 16.0D));
        rotations.put(Direction.SOUTH, new Vector3d(1, 0, 5.0D / 16.0D));
        rotations.put(Direction.WEST, new Vector3d(11.0D / 16.0D, 0, 1.0D));
        rotations.put(Direction.EAST, new Vector3d(5.0D / 16.0D, 0, 0));
    }

    @Override
    public void render(Clock2BlockEntity blockEntity, float partialTicks, MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn,
                       int packedLightIn, int packedOverlayIn) {
        // values
        BlockState blockState = blockEntity.getBlockState();
        BlockPos blockPos = blockEntity.getBlockPos();
        boolean checkSides = false;
        Direction facing = blockState.getValue(Clock2Block.FACING);
        float time = blockEntity.getHour();
        float rotation = time * 30.0F;

        // create vertex builder
        Minecraft mc = Minecraft.getInstance();
        IBakedModel model = mc.getModelManager().getModel(HAND_2);
        RenderType rendertype = RenderTypeLookup.getMovingBlockRenderType(blockState);
        IVertexBuilder vertexBuilder = bufferIn.getBuffer(rendertype);

        // transforms
        matrixStackIn.pushPose();
        Vector3d vector3d = rotations.getOrDefault(facing, Vector3d.ZERO);
        matrixStackIn.translate(vector3d.x(), vector3d.y() + 1.0D, vector3d.z());
        matrixStackIn.mulPose(Vector3f.YP.rotationDegrees(180.F - facing.toYRot()));
        matrixStackIn.mulPose(Vector3f.ZP.rotationDegrees(rotation));

        // render block
        mc.getBlockRenderer().getModelRenderer().renderModel(blockEntity.getLevel(), model,
                blockState, blockPos, matrixStackIn, vertexBuilder,
                checkSides, new Random(), blockState.getSeed(blockPos), packedOverlayIn, EmptyModelData.INSTANCE);
        matrixStackIn.popPose();
    }
}
