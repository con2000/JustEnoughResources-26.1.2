# Modpack Compatibility Sweep Status (2026-05-21)

## JER hardening applied
- Added safe 3D preview guardrails in JER:
  - `enable3DBlockPreview`
  - `fallbackTo2DBlockPreviewOnError`
  - `force2DPreviewNamespaces`
  - `force2DPreviewBlocks`
- On render exceptions, JER now falls back to item icon instead of crashing JEI/category draw.

## Latest sweep outcomes
1. With `BetterAnimationsCollection` enabled:
- Crash owner: `betteranimationscollection` / `puzzleslib` internals
- Error: missing `net.fabricmc.fabric.impl.datagen.TagBuilderHooks`
- Crash report: `run/crash-reports/crash-2026-05-21_17.40.45-client.txt`

2. With `BetterAnimationsCollection` disabled and `VisualWorkbench` enabled:
- Crash owner: `puzzleslib` internals
- Error: missing `net.fabricmc.fabric.api.event.client.player.ClientHotbarScrollEvents`
- Crash report: `run/crash-reports/crash-2026-05-21_17.41.32-client.txt`

## JER status in both runs
- `jeresources` loads before crash.
- Crash stack does not originate from JER.

## Current compatibility conclusion
- JER is no longer the startup crash source in this modpack.
- Remaining blockers are external mod/API mismatches in the PuzzlesLib family.
