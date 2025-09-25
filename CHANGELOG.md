[2.1.5]

**Fixed**
- CompletionistBanner applying the wrong effect to nearby Players 
- Haley effect no longer overrides other flight sources; mayfly is granted once on start and revoked only when the effect ends

***

[2.1.4]

**Changed**
- Intoxication effect with progressive camera sway and slight random drift.
  - Movement speed penalty scaling with amplifier.
  - Periodic Nausea at severe intoxication levels.
  - High intoxication can trigger a Blackout; duration increased to **12s**.
* Empty Beer Mugs can now simply be picked up with a right-click.

***

[2.1.3]

**Fixed**
* Brewfest armor pieces no longer render as black/red when dyed leather is equipped. Fixed by updating custom armor models to use the correct `renderToBuffer(int color)` signature introduced in 1.21.1, restoring proper leather tinting.

***

[2.1.2]

**Fixed**
* ArmorItems not being rendered properly on NeoForge
* Startup crash

***

[2.1.1]

**Fixed**
* Server crashing upon startup
* Crash caused by unregistered custom MobEffects not being saved correctly

***

[2.1.0]

** Ported to 1.21.1 ** 

***

[2.0.6] 

**Added**
* You can now add your own Text to Gingerbread Wall Decoration

**Fixed**
* Bucket stack consumption in BrewKettleBlock to only consume one bucket at a time
* Baby Zombies spawning with slightly oversizd Brewery Clothing
* Single Brewing Station Parts being movable by using Pistons

***

[2.0.5] - 2025.02.15

**Added**
* Zombies have a really low Chance to spawn wearing a Brewfest Outfit and Holding a Bottle of Whiskey

**Changed**
* Increased Bar Counter crafting result count from "1" to "2"
* Increased Sideboard crafting result count from "1" to "2"
* Adjusted all Recipe .json the the new format
* All Brewery Armor Parts are now craftable
* Improved Plate, Bowl and Large Plate Textures
* Improved Sideboard Model & Logic 

**Fixed**
* All beers and brews from the same batch now have identical effects and durations






