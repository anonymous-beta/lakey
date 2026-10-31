#!/usr/bin/env python3
"""
Converts Assets/Lekey.jpg into all required Android launcher icons
and places them in the correct mipmap folders.
"""

from PIL import Image
import os
import sys

SOURCE = "Assets/Lekey.jpg"
RES_DIR = "app/src/main/res"

# Android mipmap sizes
SIZES = {
    "mipmap-mdpi": 48,
    "mipmap-hdpi": 72,
    "mipmap-xhdpi": 96,
    "mipmap-xxhdpi": 144,
    "mipmap-xxxhdpi": 192,
}

def main():
    if not os.path.exists(SOURCE):
        print(f"ERROR: Source logo not found at {SOURCE}")
        sys.exit(1)

    img = Image.open(SOURCE).convert("RGBA")

    # Make it square (center crop)
    w, h = img.size
    min_dim = min(w, h)
    left = (w - min_dim) // 2
    top = (h - min_dim) // 2
    img = img.crop((left, top, left + min_dim, top + min_dim))

    for folder, size in SIZES.items():
        out_dir = os.path.join(RES_DIR, folder)
        os.makedirs(out_dir, exist_ok=True)

        resized = img.resize((size, size), Image.Resampling.LANCZOS)
        out_path = os.path.join(out_dir, "ic_launcher.png")
        resized.save(out_path, "PNG")
        print(f"Generated {out_path}")

        # Also generate round icon
        round_path = os.path.join(out_dir, "ic_launcher_round.png")
        resized.save(round_path, "PNG")
        print(f"Generated {round_path}")

    print("All icons generated successfully.")

if __name__ == "__main__":
    main()