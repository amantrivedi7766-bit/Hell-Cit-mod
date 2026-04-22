# Hell-Cit-mod

Fabric based CIT (Custom Item Texture) mod scaffold for Minecraft **1.21.4**.

## Features implemented
- Resource-pack CIT `.properties` parsing from:
  - `assets/<namespace>/cit/**/*.properties`
  - `assets/<namespace>/optifine/cit/**/*.properties`
- Rule matching by:
  - `matchItems` / `items`
  - `nbt.display.Name` (+ `matchCase=true|false`, `nameRegex=true|false`)
  - `ipattern:` and `pattern:` wildcard name matching (OptiFine style)
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
You can use either modern path or OptiFine-like path.

```text
assets/minecraft/cit/name/
  cyclone.properties
  cyclone.json
  cyclone.png
```

```text
assets/minecraft/optifine/cit/name/
  cyclone.properties
  cyclone.json
  cyclone.png
```

### Example (`ipattern` + NBT name)

`cyclone.properties`
```properties
type=item
items=wooden_sword
texture=cyclone
nbt.display.Name=ipattern:*CYCLONE FURY*
```

> `texture=cyclone` / `model=cyclone` resolves relative to the `.properties` folder.
> If you use only `png`, keep matching model JSON (`cyclone.json`) present for reliable rendering.

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
