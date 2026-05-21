# 3D Block Render Callsite

- file path: `src/main/java/jeresources/util/RenderHelper.java`
- old method: `renderBlock(GuiGraphicsExtractor guiGraphics, BlockState block, float x, float y, float z, float rotate, float scale)`
- expected visible behavior: JEI/JER block preview should display a true 3D block model (isometric), not a flat item icon.
- render context: JEI category draw path (client GUI rendering).
- current failure mode before this patch: fallback `guiGraphics.item(...)` path rendered flat 2D item icons instead of 3D block previews because removed 26.1.2 renderer APIs (`BlockRenderDispatcher`/`ItemRenderer`) were no longer available.

Primary callers:
- `src/main/java/jeresources/jei/plant/PlantWrapper.java`
- other JEI category draw wrappers that route through `RenderHelper.renderBlock`.
