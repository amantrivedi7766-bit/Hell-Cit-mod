# Hell-Cit-mod

Fabric based CIT (Custom Item Texture) mod scaffold for Minecraft **1.21.4**.

## Features implemented
- Resource-pack CIT `.properties` parsing from `assets/<namespace>/cit/**/*.properties`
- Rule matching by:
  - `matchItems`
  - `nbt.display.Name` (+ `matchCase=true|false`, `nameRegex=true|false`)
  - `nbt.display.Lore` (`loreRegex`, `loreCaseSensitive`)
  - `nbt.CustomModelData` / `CustomModelData`
  - `enchantments` or `enchantment`
  - `stackSize`
  - `damage`
  - `damagePercent`
  - generic `nbt.<path>` style checks
- Runtime model override via `ItemRenderer` mixin
- LRU cache for repeated stack lookups + debug stats
- Safe fallback (invalid files are skipped, default model remains)
- Commands:
  - `/cit reload`
  - `/cit debug`
  - `/cit clearCache`
- Mod Menu integration entrypoint added (`modmenu`)

## Resource-pack layout
Place properties, model JSON, and texture in one logical CIT folder and connect through the `.properties` file:

```text
assets/<namespace>/
  cit/
    swords/
      fire_blade.properties
      fire_blade.json
      fire_blade.png
```

Example `fire_blade.properties`:

```properties
type=item
matchItems=minecraft:diamond_sword
nbt.display.Name=Fire Blade
matchCase=false
nameRegex=false
enchantments=minecraft:fire_aspect,minecraft:sharpness
enchantmentLevel=1
damagePercent=0-60
model=fire_blade
```

> `model=fire_blade` resolves relative to the properties folder (`cit/swords/fire_blade`).

## GitHub Actions compiled JARs
- Workflow file: `.github/workflows/build-jar.yml`
- CI build ab **sirf 1.21.4** ke liye configured hai.
- Artifact name: `hell-cit-mod-jars-1.21.4`
- Jar file name: `hellcit-mc1.21.4.jar`
- Download path: **Actions → Build Mod JAR (1.21.4) → run → Artifacts**.

## Dev notes
- Crash-safe render fallback added (exceptions in resolver/mixin now safely fallback to vanilla model).
- Lightweight cache key strategy added to reduce per-frame allocations and avoid FPS drops.
- CI Gradle pinned to `8.10.2` with Fabric Loom `1.7.4` (stable compatibility pair).
- Loader: Fabric
- Java: 21
- Modular architecture: parser/registry/resolver/cache/conditions


## Repo hygiene
- Branch/diff safety: `.gitattributes` enforces text normalization (LF) for source/config files to prevent branch-update/diff tooling issues.
