<div align="center">

# CITX (CIT Extension)

**A smart mod that makes renaming CIT items quick and easy with one command.**  
Made for builders, creators, and anyone tired of the anvil clanking.

</div>
   
---

<p align="center">
<img src="https://github.com/aerhazu/CITX/blob/main/src/main/resources/assets/citx/icon.png" width="200">
</p>
<p align="center">
<img src="https://img.shields.io/badge/Minecraft-1.20.1-green"> 
<img src="https://img.shields.io/badge/Minecraft-1.21.1-green"> 
</p>
<p align="center">
<img src="https://img.shields.io/badge/Loader-Fabric-orange">
<img src="https://img.shields.io/badge/Environment-Client-blue">
<img src="https://img.shields.io/badge/License-Apache_2.0-blue.svg">
</p>

---

### 👉 Features

- 🏷️ Rename items with simple commands
- 🔄 Swap CIT extensions instead of stacking them
- 📝 Full rename support for display names
- ♻️ One-command reset to default
- 🛡️ Smart logics, clean-ups, and naming conditions
- 💻 Client-side only, nothing to install server-side

---

### 🚀 Quick Reference

| Command | Alias | What it does |
|---|---|---|
| `/citx <input>` |  | Shortcut. Skips "extend" entirely, just adds/swaps the extension |
| `/citx extend <extension>` | `/citx e <extension>` | Adds or swaps a CIT extension, keeps the base name |
| `/citx rename <name>` | `/citx r <name>` | Replaces the entire display name |
| `/citx reset` | `/citx x` | Clears the custom name |

---

### #️⃣ Commands

<details>
<summary>/citx extend</summary>

#### Adds or swaps an extension

Keeps the item's base name and only touches the extension.

```
/citx extend <extension>
/citx e <extension>          # alias
/citx <input>            # shortcut, no subcommand needed
```

For quick extension changes you don't even need to type "extend" or "e". Just `/citx <input>` on its own works the same way:

```
/citx red
Grass Block → Grass Block_red
```

**Example (full form)**
```
/citx extend red
Grass Block → Grass Block_red
```

Run it again with a new extension and it replaces the old one, it doesn't stack:
```
Grass Block_old
/citx e new
→ Grass Block_new        ✅
→ Grass Block_old_new    ❌ (never happens)
```
</details>

<details>
<summary>/citx rename</summary>

#### Replaces the whole name

```
/citx rename <name>
/citx r <name>            # alias
```

**Example**
```
/citx rename Mystic Sword
→ Mystic Sword
```
</details>

<details>
   
<summary>/citx reset</summary>

#### Resets item name back to default 

```
/citx reset
/citx x                   # alias
```
</details>

---

### 🧹 Automatic Cleanup

You don't have to think about formatting. CITX handles it in the background:

- Collapses duplicate underscores
- Keeps only one extension at a time
- Adds a missing leading underscore
- Blocks malformed names before they happen

```
Stone__red → Stone_red
```

---

### 🔁 Example Workflow

```
Hold: Grass Block

/citx blue           → Grass Block_blue     (shortcut for extend)
/citx green          → Grass Block_green    (swapped, not stacked)
/citx r Emerald Trophy   → Emerald Trophy
/citx x              → Grass Block          (back to default)
```
