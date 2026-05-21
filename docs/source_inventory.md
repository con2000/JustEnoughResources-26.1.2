# JER 1.21.11 Fabric Source Inventory

## Overview
- **Mod ID**: jeresources
- **Version**: 1.9.0.31
- **Minecraft**: 1.21.11
- **Source**: upstream clone (1.21.10 branch) + reference jar decompilation
- **Upstream Repo**: https://github.com/way2muchnoise/JustEnoughResources

## Package Structure

### CommonApi (jeresources.api.*)
| Package | Files |
|---------|-------|
| api/ | IDungeonRegistry, IJERAPI, IJERPlugin, IMobRegistry, IPlantRegistry, IWorldGenRegistry, JERPlugin (annotation), package-info |
| api/conditionals/ | Conditional, ExtendedConditional, ICustomLootFunction, LightLevel |
| api/distributions/ | DistributionBase, DistributionCustom, DistributionHelpers, DistributionSquare, DistributionTriangular, DistributionUnderWater |
| api/drop/ | LootDrop, PlantDrop |
| api/render/ | ColorHelper, IMobRenderHook, IScissorHook, TextModifier |
| api/restrictions/ | BiomeRestriction, DimensionRestriction, Restriction |
| api/util/ | BiomeHelper, ItemHelper, LootConditionHelper, LootFunctionHelper |

### Common (jeresources.*)
| Package | Files |
|---------|-------|
| collection/ | TradeList |
| compatibility/ | CompatBase, Compatibility |
| compatibility/api/ | DungeonRegistryImpl, JERAPI, MobRegistryImpl, PlantRegistryImpl, WorldGenRegistryImpl |
| compatibility/minecraft/ | ExperienceRange, MinecraftCompat, MobCompat, RenderHooks |
| config/ | Settings |
| entry/ | AbstractVillagerEntry, DungeonEntry, EnchantmentEntry, MobEntry, PlantEntry, VillagerEntry, WanderingTraderEntry, WorldGenEntry |
| jei/ | BackgroundDrawable, BlankJEIRecipeCategory, JEIConfig |
| jei/dungeon/ | DungeonCategory, DungeonTooltip, DungeonWrapper |
| jei/enchantment/ | EnchantmentCategory, EnchantmentMaker, EnchantmentWrapper |
| jei/mob/ | MobCategory, MobTooltip, MobWrapper |
| jei/plant/ | PlantCategory, PlantTooltip, PlantWrapper |
| jei/villager/ | VillagerCategory, VillagerWrapper |
| jei/worldgen/ | WorldGenCategory, WorldGenTooltip, WorldGenWrapper |
| json/ | ProfilingAdapter, WorldGenAdapter |
| platform/ | ILootTableHelper, IModInfo, IModList, IPlatformHelper, Services |
| profiling/ | ChunkGetter, ChunkProfiler, DummyWorld, EmptyChunkJER, ProfileCommand, ProfiledDimensionData, Profiler, ProfilingBlacklist, ProfilingExecutor, ProfilingTimer |
| proxy/ | ClientProxy, CommonProxy |
| reference/ | Reference, Resources, Textures |
| registry/ | DungeonRegistry, EnchantmentRegistry, MobRegistry, PlantRegistry, VillagerRegistry, WorldGenRegistry |
| util/ | ClassScraper, CollectionHelper, DimensionHelper, FakeClientLevel, Font, LogHelper, LootTableFetcher, LootTableHelper, MapKeys, MobHelper, MobTableBuilder, PlantHelper, ReflectionHelper, RegistryHelper, RenderHelper, TranslationHelper, VillagersHelper |

### Fabric-specific (jeresources.fabric.*)
| Package | Files |
|---------|-------|
| fabric/ | FabricPlatformHelper, JEResources (main), ModInfo, ModList |
| fabric/config/ | Config, ConfigFileHandler, ConfigValues, JEResourcesModMenu |

## Entrypoints
| Entrypoint | Class |
|-----------|-------|
| client | jeresources.fabric.JEResources |
| modmenu | jeresources.fabric.config.JEResourcesModMenu |
| jei_mod_plugin | jeresources.jei.JEIConfig |

## Mixins
- Mixin config: `jeresources.mixins.json`
- Package: `jeresources.fabric.mixin`
- Status: EMPTY (no mixins defined, only access widener used)

## Access Widener
- File: `jeresources.accesswidener`
- Namespace: `named` (upstream source)
- Targets: LootTable internals, LootPoolSingletonContainer, LootItem, LootPoolEntryContainer, DynamicLoot, UniformGenerator, BinomialDistributionGenerator, SetItemCountFunction, LootContext constructor, Mob.xpReward, BlockBehaviour.getCloneItemStack

## JEI Plugin
- Plugin class: `jeresources.jei.JEIConfig`
- Categories: dungeon, enchantment, mob, plant, villager, worldgen
- Uses `@JeiPlugin` annotation pattern

## Config
- File-based config via ConfigFileHandler
- Cloth Config screen via JEResourcesModMenu
- Settings class for runtime config state
- ConfigValues for serialized config

## Dependencies (original upstream)
| Dependency | Version |
|-----------|---------|
| Minecraft | 1.21.10 |
| Fabric Loader | 0.18.3 |
| Fabric API | 0.138.4+1.21.10 |
| JEI | 26.2.0.30 |
| Cloth Config | 20.0.149 |
| ModMenu | 16.0.1 |
| Parchment Mappings | 2025.10.12 for 1.21.10 |

## Resources
- Lang files: 16 languages (de_de, en_us, es_ar, es_es, fr_fr, it_it, ja_jp, ko_kr, nl_nl, pt_br, pt_pt, ru_ru, uk_ua, zh_cn, zh_tw)
- Textures: dungeon.png, enchantment.png, mob.png, plant.png, tabs.png, villager.png, world_gen.png
- Service file: META-INF/services/jeresources.platform.IPlatformHelper -> jeresources.fabric.FabricPlatformHelper
