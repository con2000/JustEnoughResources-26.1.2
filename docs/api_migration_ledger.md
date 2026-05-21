# API Migration Ledger: JER 1.21.11 → 26.1.2

## Overview
Porting JustEnoughResources-Fabric-1.21.11-1.9.0.31 to Minecraft 26.1.2, Fabric Loader 0.19.2, Java 25.

| Area | Files | Old API | New API | Status |
|------|-------|---------|---------|--------|
| UI | 13 files | `net.minecraft.client.gui.GuiGraphics` | `net.minecraft.client.gui.DrawContext` | Needs fix |
| UI | 1 file | `GuiGraphicsExtractor` (custom) | `DrawContext` | Needs fix |
| Villager | 6 files | `VillagerTrades.ItemListing` | `net.minecraft.world.entity.npc.ItemListing` | Needs fix |
| Villager | 1 file | `VillagerTrades.TRADES` | `VillagerTrades` restructured | Needs fix |
| Villager | 1 file | `VillagerTrades.WANDERING_TRADER_TRADES` | Restructured API | Needs fix |
| Loot | 2 files | `DynamicLoot.name` (public field) | private field, needs getter | Needs fix |
| Loot | 1 file | `SetItemCountFunction.value` (public field) | private field, needs getter | Needs fix |
| World | 4 files | `ResourceKey.location()` | Record accessor (should work) | Investigate |
| World | 1 file | `ServerLevel` 11-param constructor | 10-param constructor | Needs fix |
| Render | 1 file | `Minecraft.getBlockRenderer()` | renamed | Needs fix |
| Command | 1 file | `CommandSourceStack.hasPermission()` | renamed/removed | Needs fix |
| JEI | 2 files | `IRecipeSlotTooltipCallback` import | Unused import, remove | Needs fix |
| JEI | 1 file | `class_437` → `Screen` type | Mappings issue | Needs fix |
| Entity | 1 file | `Husk` / `AbstractIllager` | Still exist, import issue | Investigate |

## Detailed Migration Entries
