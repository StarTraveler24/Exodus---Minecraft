# Project Exodus — Minecraft 1.21.1 NeoForge

Create/Cosmonautics survival modpack with Spore infection, MCA settlements, PatientZero research, Mekanism automation, TaCZ weapons, and a quest-driven economy.

## Latest pack prepared: v3.7 (not yet uploaded to GitHub)

The complete v3.7 CurseForge ZIP currently exists as a downloadable conversation artifact, **but GitHub still contains the older v2.9 source profile**. The repository's packaging workflow now refuses to publish a misleading v2.9 artifact as current.

### v3.0–v3.7 pack changes awaiting source-profile upload

- v3.0–v3.1: questbook layouts and polish
- v3.2: TaCZ/MCA gun addon
- v3.3: expanded gunpack selection
- v3.4: Spore spawn and Paxi datapack loading for zombie drops
- v3.5: compact four-group questbook
- v3.6: expanded shop selection and extra supply depots
- v3.7: Hostile Neural Networks, Placebo and energy-intensive zombie-model loot fabrication

These are pack-profile changes and **will not be included in GitHub Actions artifacts** until the v3.7 ZIP replaces the build input.

## Updated compatibility addon

The source under `src/main/java` contains the PatientZero/MCA interaction change recognizing **both empty syringe and virus dose**. Download the compiled addon from the latest successful [addon build](../../actions/workflows/build.yml), and remove older copies of the addon JAR.

## Publish v3.7 correctly

1. Upload the full v3.7 CurseForge export as `modpack/Project-Exodus-Profile.zip` to this repository.
2. Run [Build downloadable Project Exodus modpack](../../actions/workflows/package-modpack.yml).
3. Download and import the output in a clean CurseForge profile.
4. Test Spore spawning, zombie loot, Hostile Neural Networks datapacks and FE cost, shops, and MCA syringe interactions in-game.

Do not assume GitHub CI proves in-game behavior. Do not upload player saves or private credentials.
