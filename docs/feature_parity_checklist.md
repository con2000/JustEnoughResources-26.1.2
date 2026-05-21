# Feature Parity Checklist: JER Port to Minecraft 26.1.2

This checklist tracks the porting of features from JustEnoughResources-Fabric-1.21.11-1.9.0.31 to Minecraft 26.1.2 with Fabric Loader 0.19.2 and Java 25.

## Core Features

### [ ] JEI Integration
- [ ] JEI categories display correctly
- [ ] Item tooltip integration works
- [ ] JEI search integration functional
- [ ] Recipe viewers and handlers registered

### [ ] Resource Scanning
- [ ] Mob drop scanning functional
- [ ] Dungeon loot table scanning works
- [ ] Villager trade scanning operational
- [ ] World generation feature scanning active
- [ ] Crop growth scanning implemented
- [ ] Block state scanning for all registries

### [ ] Data Collection & Storage
- [ ] Drop chance calculation accurate
- [ ] Silk touch detection working
- [ ] Fortune level handling
- [ ] Data persistence across sessions
- [ ] Configuration saving/loading

### [ ] User Interface
- [ ] Main JER GUI renders correctly
- [ ] Category tabs functional
- [ ] Search/filter system operational
- [ ] Item entries display with proper icons
- [ ] Tooltip information accurate
- [ ] Configuration screen accessible (ModMenu remap issue pending)
- [ ] Settings adjustable and persisting

### [ ] Compatibility & Integration
- [ ] Works with Fabric Loader 0.19.2
- [ ] Compatible with Java 25
- [ ] No conflicts with common modpacks
- [ ] Proper initialization order
- [ ] Correct cleanup on world unload

## Technical Implementation

### [ ] Build System
- [x] Gradle build passes without errors
- [ ] Correct Fabric Loom version (1.15.5)
- [ ] Proper dependency resolution
- [ ] Access widener functioning
- [ ] Mixin configuration valid

### [ ] Core Architecture
- [ ] Services abstraction layer working
- [ ] Platform-specific adapters functional
- [ ] Event handling system operational
- [ ] Resource reloading support
- [ ] Thread safety maintained

### [ ] Rendering System
- [ ] Entity rendering in GUI functional
- [ ] Block rendering in GUI operational
- [ ] Texture rendering correct
- [ ] Line drawing functional
- [ ] Chest rendering (TODO: revisit later)
- [ ] Render hook system intact

## Known Issues & Limitations

### [ ] Stubs Requiring Implementation
- [x] ModMenu integration tracked as blocked by Loom remap issue
- [ ] RenderHelper.entity() stub
- [ ] RenderHelper.block() stub

### [ ] Deprecation Warnings (Acceptable)
- [ ] ChunkAccess API usage (being addressed)
- [ ] Other 1.21.x -> 26.1.2 API changes

## Verification Criteria

For each feature to be considered complete:
- [ ] Compiles without errors
- [ ] Functions identically to 1.21.11 version
- [ ] No silent failures or fallback behaviors
- [ ] Proper error handling and logging
- [ ] Performance characteristics maintained
- [ ] Memory usage within reasonable bounds

## Progress Tracking

Last updated: 2026-05-21
Target completion: All features ported and verified

## Definition of Done

A feature is considered "Done" when:
1. It compiles cleanly (no new errors beyond existing deprecation warnings)
2. It provides the same functionality as the upstream 1.21.11 version
3. It has been manually tested in a development environment
4. Any required stubs or TODOs have been addressed or documented
