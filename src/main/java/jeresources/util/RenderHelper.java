package jeresources.util;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import jeresources.api.render.IMobRenderHook;
import jeresources.compatibility.api.MobRegistryImpl;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.lang.Math;

public class RenderHelper {
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
        // RenderType rendertype = RenderType.guiTextured(Resources.Vanilla.CHEST);
        // VertexConsumer buffer = Minecraft.getInstance().renderBuffers().bufferSource().getBuffer(rendertype);
        // TODO: Reimplement
        // ChestModel modelchest = new ChestModel();

        PoseStack poseStack = new PoseStack();
        // RenderSystem.enableRescaleNormal();
        // RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        poseStack.translate(x, y, 50.0F);
        poseStack.mulPose(new Quaternionf(-160.0F, 1.0F, 0.0F, 0.0F));
        poseStack.scale(scale, -scale, -scale);
        poseStack.translate(0.5F, 0.5F, 0.5F);
        poseStack.mulPose(new Quaternionf(rotate, 0.0F, 1.0F, 0.0F));
        poseStack.translate(-0.5F, -0.5F, -0.5F);

        float lidAngleF = lidAngle / 180;
        lidAngleF = 1.0F - lidAngleF;
        lidAngleF = 1.0F - lidAngleF * lidAngleF * lidAngleF;
        // modelchest.getLid().rotateAngleX = -(lidAngleF * (float) Math.PI / 2.0F);
        // modelchest.field_78233_c.offsetX += 0.1F;
        // modelchest.field_78233_c.offsetZ += 0.12F; // chestKnob
        // modelchest.field_78232_b.offsetX -= 0.755F; // chestBelow
        // modelchest.field_78232_b.offsetY -= 0.4F; // chestBelow
        // modelchest.field_78232_b.offsetZ -= 0.9F; // chestBelow
        // modelchest.renderAll();
        // RenderSystem.disableRescaleNormal();
    }

    public static void renderBlock(GuiGraphicsExtractor guiGraphics, BlockState block, float x, float y, float z, float rotate, float scale) {
        // Simplest approach for GUI: render block as item
        // This preserves the visual appearance while being much simpler than full block rendering
        ItemStack stack = new ItemStack(block.getBlock());
        
        // Apply the same transformations as the original code for consistency
        PoseStack poseStack = new PoseStack();
        poseStack.translate(x, y, z);
        poseStack.scale(-scale, -scale, -scale);
        poseStack.translate(-0.5F, -0.5F, 0);
        poseStack.mulPose(Axis.XP.rotationDegrees(-30F));
        poseStack.translate(0.5F, 0, -0.5F);
        poseStack.mulPose(Axis.YP.rotationDegrees(rotate));
        poseStack.translate(-0.5F, 0, 0.5F);
        
        // Convert pose to GUI coordinates for item rendering
        // Note: This is an approximation - the original code did 3D block rendering
        // For GUI purposes, we'll render as an item at the specified 2D position
        guiGraphics.item(stack, Math.round(x), Math.round(y + z));
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
