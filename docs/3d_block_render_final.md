# 3D Block Render Final

## Old callsite
- `src/main/java/jeresources/util/RenderHelper.java`
- method: `renderBlock(GuiGraphicsExtractor, BlockState, float x, float y, float z, float rotate, float scale)`
- before patch: item-icon fallback (`guiGraphics.item`) instead of true 3D preview.

## New 26.1.2 API used
Implemented client-only adapter:
- `src/client/java/jeresources/client/render/Jer3DBlockRenderer26.java`

Adapter path uses render-state submission via GUI entity rendering:
- `GuiGraphicsExtractor.entity(...)`
- `FallingBlockRenderState`
- `MovingBlockRenderState`
- `EntityType.FALLING_BLOCK`
- world context from `ClientLevel` (`getBiome`, `cardinalLighting`, `getLightEngine`)

## Implementation type
- Uses **render-state path** (entity render-state submission) via `FallingBlockRenderState`.
- Does **not** use `BlockRenderDispatcher` / `ItemRenderer` / `renderSingleBlock`.
- Does **not** use `ModelBlockRenderer#tesselateBlock` in this patch.

## Wiring
- `RenderHelper.renderBlock(...)` now routes to `Jer3DBlockRenderer26.renderBlockPreview(...)`.
- For no-world context (`mc.level == null`), adapter falls back to item icon render only for safety.

## Mixins / accessors
- None added.
- See `docs/3d_block_render_mixin_access.md`.

## Build / runtime observations
- Build command passed:
  - `./gradlew clean build --stacktrace --info` -> `logs/3d_block_render_build.log`
- Client run command boots and loads JER:
  - `./gradlew runClient --stacktrace --info` -> `logs/3d_block_render_runclient.log`
  - `jeresources Loaded` observed.
- `runClient` ended with code 143 because process was intentionally terminated for smoke-run completion.

## Fallback behavior
- If client world is unavailable during GUI draw, block preview falls back to item icon for stability.
- Normal in-world client sessions use full 3D preview path.

## Known limitations
- This patch does not add block-entity special renderer previews.
- Visual parity for every exotic state (fluids/technical-only states) still depends on runtime availability in `ClientLevel`.
