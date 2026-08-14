# about-warframe

A beginner-written program (created with the help of AI) that compares Harrow's four Warframe abilities with a single enhanced ability of Nidus. The comparison explores how ability intensity and weapon critical hit rate affect performance.

This README explains the program's purpose, the input parameters, how the comparison is done, and how to run the code.

## Summary

- Purpose: Compare Harrow's four abilities against an enhanced Nidus ability across different ability intensities and weapon critical hit rates.
- Inputs:
  - `a` — weapon critical hit rate (expressed as a decimal between 0 and 1). Example: `a = 0.25` is a 25% critical hit chance.
  - `x` — ability intensity (a numeric value representing skill strength; interpretation depends on the program's scaling).

The program runs simulations or calculations to show how damage, criticals, or other metrics change for each ability as `a` and `x` vary.

## How it works (high level)

- For each ability, the program models the effect of ability intensity `x` on relevant outputs (for example: damage multiplier, proc chance, or other ability-specific mechanics).
- Weapon critical hit rate `a` is combined with the ability model (for example, to compute expected critical hits or expected damage) to produce comparable metrics.
- The program then outputs comparative results (tables, CSV, or plots) so you can see which ability performs better under different `(a, x)` pairs.

## Parameters

- a (float): Weapon critical hit rate. Range: 0.0 — 1.0.
- x (float): Ability intensity. Range depends on the ability (use positive numbers; the program will validate).

Note: Throughout the code and documentation, `a` is used for weapon critical hit rate and `x` for the Warframe ability intensity.

## Usage

1. Install any dependencies (if the project uses Python):

   pip install -r requirements.txt

2. Run the comparison script. Example:

   python compare.py --a 0.25 --x 2.0

   This should run the comparison with a 25% critical chance and ability intensity of 2.0.

3. Output formats:

- The program may print results to the terminal, save a CSV file, or generate a plot. Check the script for exact output file names.

## Example

Input:
- a = 0.25 (25% crit chance)
- x = 2.0 (ability intensity = 2)

Expected output (example format):

Ability | Metric
--- | ---
Harrow - Ability 1 | 123.45
Harrow - Ability 2 | 110.20
Harrow - Ability 3 | 140.75
Harrow - Ability 4 | 95.80
Nidus - Enhanced Ability | 130.10

(Actual numbers depend on the program's calculations.)

## Notes for beginners

- This project was written by a beginner with AI assistance. The code favors clarity over cleverness so it's easier to read and modify.
- If you're experimenting with ability names and game mechanics, double-check formulas against Warframe's current mechanics — balancing and ability behavior can change over time.

## Contributing

- Feel free to open issues or PRs to improve calculation accuracy, add plots, or include additional Warframes/abilities.

## License

This project is provided under the MIT License. See the LICENSE file for details.

---

Created by a beginner using AI — happy tinkering!
