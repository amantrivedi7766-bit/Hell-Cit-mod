# Hell-Cit-mod

Fabric based CIT (Custom Item Texture) mod scaffold for Minecraft **1.21 → 1.21.11**.

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
- CI pe core compile step **1.21.1** par hota hai (stable base build).
- Workflow us base output se **1.21 se 1.21.11** tak har version ke liye alag-alag jar artifact banata hai: `hell-cit-mod-jars-<mc_version>`.
- Har artifact ke andar jar ka naam version-specific hota hai: `hellcit-mc<mc_version>.jar` (jaise `hellcit-mc1.21.6.jar`).
- Download path: **Actions → Build Mod JAR (1.21.x Artifacts) → run → Artifacts**.

## Dev notes
- Loader: Fabric
- Java: 21
- Modular architecture: parser/registry/resolver/cache/conditions
