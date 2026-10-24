# Project Exodus — Minecraft 1.21.1 NeoForge

Project Exodus is a Create Cosmonautics / Aeronautics survival modpack, focused on planetary exploration, Spore infection, Mekanism, PatientZero biomedical progression, and automation.

## Release state — v2.9

The complete **Project Exodus v2.9** CurseForge export is currently held as a conversation download and is **not yet present in this GitHub repository**. Do not mistake this source repository for a complete modpack release.

The local v2.9 export currently contains 86 CurseForge-managed mod entries, including Create Propulsion: Simulated and Industrial Foregoing: Extra Upgrades, plus override configurations, FTB Quests chapters, and locally distributed mod files. Those resources have NOT all been uploaded here.

### Addon code and builds

The repository contains NeoForge Java source for the Project Exodus compatibility mod, including:
- Spore environment compatibility
- PatientZero anti-Spore phenotype experiments
- MCA villager/PatientZero virus-dose interaction handler

CI builds compile but are **not a guarantee of in-game behavior**. Download latest successful JAR from [Actions](../../actions/workflows/build.yml). Do not combine old and new copies.

### Publish the complete pack

1. Download `Project-Exodus-Industrial-Foregoing-Extra-Upgrades-v2.9.zip` from the conversation.
2. Upload the unmodified ZIP to `modpack/Project-Exodus-Profile.zip` in this repository, or publish it as a GitHub Release asset.
3. Run the [packaging workflow](../../actions/workflows/package-modpack.yml) and verify it actually includes the uploaded profile.
4. Test CurseForge import, mod loading, FTB Quests, planets, and MCA injection in a disposable instance.

Do not upload private world saves or access tokens.
