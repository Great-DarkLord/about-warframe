# about-warframe

A beginner-written program (created with the help of AI) that compares **Harrow's Covenant** (4th ability) with **Nidus's Virulence** (1st ability augmented by Teeming Virulence) across different ability intensities and weapon critical hit rates.

This README explains the program's purpose, the input parameters, how the comparison is done, and how to run the code.

## Summary

- **Purpose:** Compare how Harrow's Covenant and Nidus's augmented Virulence boost critical hit rates across varying ability intensities and weapon base critical hit rates.
- **Abilities Compared:**
  - **Harrow - Covenant** (4th ability): Grants allies within range a fixed **50% critical hit chance boost**.
  - **Nidus - Virulence + Teeming Virulence** (1st ability + augment mod): The Teeming Virulence augment grants a more complex critical hit chance boost based on ability intensity.
- **Inputs:**
  - `a` — weapon base critical hit rate (expressed as a decimal between 0 and 1). Example: `a = 0.25` is a 25% base critical hit chance.
  - `x` — ability intensity (a numeric value representing skill strength; directly affects Nidus's augment mod bonus).

The program runs calculations to show how the resulting critical hit rates compare for each ability as `a` and `x` vary.

## How it works (high level)

### Harrow - Covenant (4th Ability)

- Provides a **fixed 50% critical chance bonus** to all nearby allies.
- Final critical hit rate: `crit_rate = a + 0.50` (capped at 1.0 or 100%).
- Does not scale with ability intensity `x`.

### Nidus - Virulence + Teeming Virulence (1st Ability + Augment)

- The augment mod grants a critical hit chance bonus that **scales with ability intensity**.
- The bonus follows a more complex function that depends on `x` (ability intensity).
- Final critical hit rate: `crit_rate = a + f(x)`, where `f(x)` is the intensity-dependent bonus function.
- This allows for dynamic scaling compared to Harrow's static bonus.

### Comparison Logic

- For each weapon base critical hit rate `a` and ability intensity `x`, the program computes the resulting critical hit rate for both Harrow and Nidus.
- The output displays comparative metrics so you can see which ability offers better coverage across different scenarios.

## Parameters

- **a** (float): Weapon base critical hit rate. Range: 0.0 — 1.0.
  - Example: `a = 0.25` means your weapon has a 25% base critical hit chance.
- **x** (float): Warframe ability intensity. Range: 0.0+ (typically 0.5 to 3.0+ depending on builds).
  - Affects the Nidus augment bonus directly.
  - Does not affect Harrow's fixed 50% bonus.

Note: Throughout the code and documentation, `a` is used for weapon base critical hit rate and `x` for the Warframe ability intensity.

## Usage

1. Install any dependencies (if the project uses Python):

   ```bash
   pip install -r requirements.txt
   ```

2. Run the comparison script. Example:

   ```bash
   python compare.py --a 0.25 --x 2.0
   ```

   This will run the comparison with a 25% base critical chance and ability intensity of 2.0.

3. Output formats:

   - The program may print results to the terminal, save a CSV file, or generate a plot. Check the script for exact output file names.

## Example

Input:
- `a = 0.25` (25% weapon base crit chance)
- `x = 2.0` (ability intensity = 2.0)

Expected output (example format):

| Ability | Final Crit Rate | Notes |
| --- | --- | --- |
| Harrow - Covenant | 75% | 25% + 50% (fixed bonus) |
| Nidus - Virulence + Teeming Virulence | Varies with f(x) | Depends on augment scaling function |

(Actual numbers depend on the program's calculations and Warframe's current balance.)

## Notes for beginners

- This project was written by a beginner with AI assistance. The code prioritizes clarity and readability so it's easier to understand and modify.
- **Ability Mechanics:** The comparison focuses specifically on critical hit rate bonuses provided by these two abilities. Harrow's Covenant offers a simple, fixed bonus, while Nidus's augmented Virulence provides a scaling bonus tied to ability intensity.
- **Game Updates:** Warframe's ability balance and mechanics can change frequently. Always double-check formulas against the current game mechanics to ensure accuracy.
- **Augment Mods:** This program assumes the Teeming Virulence augment mod is equipped on Nidus's Virulence ability.

## Contributing

- Feel free to open issues or PRs to:
  - Improve calculation accuracy
  - Add visualizations or plots
  - Include additional Warframes or ability comparisons
  - Update mechanics to reflect game changes

## License

This project is provided under the MIT License. See the LICENSE file for details.

---

Created by a beginner using AI — happy tinkering!
