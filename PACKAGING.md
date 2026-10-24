# Project Exodus — Minecraft 1.21.1 NeoForge

Survival-focused Create: Cosmonautics modpack with Spore infection, Mekanism-scale power, Mars and Moon generation patches, zombie survival, and experimental asteroid-belt systems.

## Download the entire modpack

1. Open [Actions → Build downloadable Project Exodus modpack](https://github.com/StarTraveler24/Exodus---Minecraft/actions/workflows/package-modpack.yml).
2. Open a **successful** run and download **Project-Exodus-CurseForge-Import** under **Artifacts**.
3. Unzip that GitHub artifact; import the enclosed **Project-Exodus-CurseForge-Import.zip** into CurseForge Minecraft.

**Packaging requirement:** The repository must first contain `modpack/Project-Exodus-Profile.zip` (a genuine CurseForge export with `manifest.json` and `overrides/`). This file is not currently committed. Upload it using GitHub → Add file → Upload files, then run the workflow manually. Until that file exists, only the addon can be built.

The workflow compiles the NeoForge compatibility addon with Java 21, inserts the JAR into the export's `overrides/mods/` directory, checks the ZIP, and uploads one combined CurseForge import ZIP. CurseForge resolves the referenced managed mods on import. It does **not** guarantee a successful launch or verify compatibility in Minecraft.

## NeoForge addon build

[Build Project Exodus compatibility addon](https://github.com/StarTraveler24/Exodus---Minecraft/actions/workflows/build.yml) compiles the Java source separately.

## Known limitations

- The addon has compiled in CI, but planetary vacuum behavior and other in-game effects have not been tested.
- Martian Spore worldgen and ore modifications are datapack definitions, not verified in gameplay.
- The asteroid ring is a block-based prototype, not Cosmonautics spacecraft physics or full natural orbital travel.
- Individual world datapacks in `overrides/projectexodus-install/` still need placement into each world's `datapacks/` folder. Remove older conflicting datapack versions.
- Use a clean test world and back up any existing saves.
