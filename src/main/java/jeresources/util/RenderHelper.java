package jeresources.util;

import com.mojang.blaze3d.vertex.PoseStack;
import jeresources.api.render.IMobRenderHook;
import jeresources.client.render.Jer3DBlockRenderer26;
import jeresources.config.Settings;
import jeresources.compatibility.api.MobRegistryImpl;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.util.HashSet;
import java.lang.Math;
import java.util.Set;

public class RenderHelper {
    private static final Set<String> logged3dPreviewFailures = new HashSet<>();

    public static void drawLine(GuiGraphicsExtractor guiGraphics, int xBegin, int yBegin, int xEnd, int yEnd, int color) {
        xEnd += xBegin == xEnd ? 1 : 0;
        yEnd += yBegin == yEnd ? 1 : 0;
        guiGraphics.fill(xBegin, yBegin, xEnd, yEnd, color);
    }

    public static void renderEntity(GuiGraphicsExtractor guiGraphics, int x1, int y1, int x2, int y2, double scale, double yaw, double pitch, LivingEntity livingEntity) {
        // 1. Apply render hooks (gets x/y/scale/yaw/pitch adjustments)
        PoseStack mobPoseStack = new PoseStack();
        IMobRenderHook.RenderInfo renderInfo = MobRegistryImpl.applyRenderHooks(
            mobPoseStack, livingEntity,
            new IMobRenderHook.RenderInfo(0, 0, scale, yaw, pitch)
        );
        int x = renderInfo.x;
        int y = renderInfo.y;
        double finalScale = renderInfo.scale;
        double finalYaw = renderInfo.yaw;
        double finalPitch = renderInfo.pitch;

        // 2. Calculate angles from yaw/pitch (matching old JER behavior)
        float yRotAngle = (float) Math.atan(finalYaw / 40.0F) * 40.0F;
        float xRotAngle = -((float) Math.atan(finalPitch / 40.0F)) * 20.0F;
        float bodyRotOffset = (float) Math.atan(finalYaw / 40.0F) * 20.0F;

        // 3. Extract render state from the entity
        Minecraft mc = Minecraft.getInstance();
        EntityRenderDispatcher dispatcher = mc.getEntityRenderDispatcher();
        EntityRenderState renderState = dispatcher.extractEntity(livingEntity, 1.0f);
        renderState.shadowPieces.clear();
        renderState.outlineColor = 0;

        // 4. Configure LivingEntityRenderState fields
        if (renderState instanceof LivingEntityRenderState livingState) {
            livingState.bodyRot = 180.0F + bodyRotOffset;
            livingState.yRot = yRotAngle;
            livingState.xRot = xRotAngle;

            // Undo the entity's built-in scale; we apply our own scale via guiGraphics.entity()
            livingState.boundingBoxWidth /= livingState.scale;
            livingState.boundingBoxHeight /= livingState.scale;
            livingState.scale = 1.0F;
        }

        // 5. Build camera and rotation quaternions (matching old JER behavior)
        Quaternionf cameraQuat = new Quaternionf().rotateZ((float) Math.PI);
        cameraQuat.mul(new Quaternionf().rotateY((float) Math.PI));

        float pitchAngle = (float) Math.atan(finalPitch / 40.0F);
        Quaternionf entityQuat = new Quaternionf().rotateX(
            pitchAngle * 20.0F * ((float) Math.PI / 180.0F)  // DEG2RAD
        );
        // Combine: cameraQuat applies the entity pitch rotation on top of the camera transform
        cameraQuat.mul(entityQuat);

        // 6. Build translation vector (incorporating render hook x/y offsets)
        float heightOffset = livingEntity.getBbHeight() / 2.0F;
        Vector3f translationVec = new Vector3f((float) x, heightOffset + (float) y, 0.0F);

        // 7. Apply any additional PoseStack transforms from render hooks
        //    (extract composite transform from the hook-provided PoseStack)
        if (!mobPoseStack.isEmpty()) {
            Vector3f hookTranslation = new Vector3f();
            mobPoseStack.last().pose().getTranslation(hookTranslation);
            translationVec.add(hookTranslation);
        }

        // 8. Render via the new API
        guiGraphics.entity(
            renderState,
            (float) finalScale,
            translationVec,
            cameraQuat,        // rotation (camera transform)
            entityQuat,        // overrideCameraAngle (entity-facing override)
            x1, y1, x2, y2     // screen bounding box
        );
    }

    public static void renderChest(GuiGraphicsExtractor guiGraphics, float x, float y, float rotate, float scale, float lidAngle) {
        // Chest model rendering API changed in 26.1.2; keep chest visible via item render.
        PoseStack poseStack = new PoseStack();
        poseStack.translate(x, y, 50.0F);
        poseStack.mulPose(new Quaternionf(-160.0F, 1.0F, 0.0F, 0.0F));
        poseStack.scale(scale, -scale, -scale);
        poseStack.translate(0.5F, 0.5F, 0.5F);
        poseStack.mulPose(new Quaternionf(rotate, 0.0F, 1.0F, 0.0F));
        poseStack.translate(-0.5F, -0.5F, -0.5F);

        ItemStack stack = new ItemStack(Blocks.CHEST);
        guiGraphics.item(stack, Math.round(x), Math.round(y));
    }

    public static void renderBlock(GuiGraphicsExtractor guiGraphics, BlockState block, float x, float y, float z, float rotate, float scale) {
        if (block == null) {
            return;
        }

        ItemStack fallbackStack = new ItemStack(block.getBlock());
        Identifier blockId = BuiltInRegistries.BLOCK.getKey(block.getBlock());
        String blockIdString = blockId.toString();
        boolean force2d = !Settings.enable3DBlockPreview
            || containsIgnoreCase(Settings.force2DPreviewNamespaces, blockId.getNamespace())
            || containsIgnoreCase(Settings.force2DPreviewBlocks, blockIdString);

        int size = Math.max(12, Math.round(scale * 1.6F));
        int drawX = Math.round(x - size / 2.0F);
        int drawY = Math.round(y + z - size / 2.0F);
        if (force2d) {
            guiGraphics.item(fallbackStack, drawX, drawY);
            return;
        }

        try {
            Jer3DBlockRenderer26.renderBlockPreview(guiGraphics, block, drawX, drawY, size, rotate, -30.0F);
        } catch (Throwable t) {
            String failureKey = blockIdString + "|" + t.getClass().getName();
            if (logged3dPreviewFailures.add(failureKey)) {
                LogHelper.warn("JER 3D preview failed for {} ({}); using 2D fallback", blockIdString, t.getClass().getSimpleName());
                LogHelper.debug("JER 3D preview exception details for " + blockIdString, t);
            }
            if (Settings.fallbackTo2DBlockPreviewOnError) {
                guiGraphics.item(fallbackStack, drawX, drawY);
            }
        }
    }

    private static boolean containsIgnoreCase(String[] values, String query) {
        if (values == null || query == null) {
            return false;
        }
        for (String value : values) {
            if (value != null && value.equalsIgnoreCase(query)) {
                return true;
            }
        }
        return false;
    }

    public static void drawTexture(GuiGraphicsExtractor guiGraphics, Identifier resource, int x, int y, int u, int v, int width, int height) {
        drawTexturedModalRect(guiGraphics, resource, x, y, u, v, width, height);
    }

    public static void drawTexturedModalRect(GuiGraphicsExtractor guiGraphics, Identifier resource, int x, int y, int u, int v, int width, int height) {
        guiGraphics.blit(
                RenderPipelines.GUI_TEXTURED,
                resource,
                x,
                y,
                u,
                v,
                width,
                height,
                256,
                256
        );
    }
}
