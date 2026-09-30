#!/usr/bin/env python3
"""Generates the WeatherNow launcher icon (legacy raster mipmaps) using Pillow.
Design: sky-blue vertical gradient background, a warm sun disc, and a white cloud.
Run once during setup; output is committed as regular resource PNGs.
"""
import math
from PIL import Image, ImageDraw

SIZES = {
    "mipmap-mdpi": 48,
    "mipmap-hdpi": 72,
    "mipmap-xhdpi": 96,
    "mipmap-xxhdpi": 144,
    "mipmap-xxxhdpi": 192,
}

TOP_COLOR = (25, 118, 210)      # #1976D2
BOTTOM_COLOR = (100, 181, 246)  # #64B5F6
SUN_COLOR = (255, 179, 0)       # #FFB300
SUN_HIGHLIGHT = (255, 213, 79)  # #FFD54F
CLOUD_COLOR = (255, 255, 255)


def make_icon(size: int, round_mask: bool) -> Image.Image:
    scale = 4
    s = size * scale
    img = Image.new("RGBA", (s, s), (0, 0, 0, 0))
    draw = ImageDraw.Draw(img)

    # Vertical gradient background (rounded square, "squircle"-ish corners)
    for y in range(s):
        t = y / max(1, s - 1)
        r = int(TOP_COLOR[0] + (BOTTOM_COLOR[0] - TOP_COLOR[0]) * t)
        g = int(TOP_COLOR[1] + (BOTTOM_COLOR[1] - TOP_COLOR[1]) * t)
        b = int(TOP_COLOR[2] + (BOTTOM_COLOR[2] - TOP_COLOR[2]) * t)
        draw.line([(0, y), (s, y)], fill=(r, g, b, 255))

    corner_radius = int(s * 0.22)
    mask = Image.new("L", (s, s), 0)
    mask_draw = ImageDraw.Draw(mask)
    mask_draw.rounded_rectangle([(0, 0), (s - 1, s - 1)], radius=corner_radius, fill=255)
    bg = Image.new("RGBA", (s, s), (0, 0, 0, 0))
    bg.paste(img, (0, 0), mask)
    img = bg
    draw = ImageDraw.Draw(img)

    # Sun
    sun_cx, sun_cy, sun_r = s * 0.40, s * 0.38, s * 0.16
    draw.ellipse(
        [sun_cx - sun_r, sun_cy - sun_r, sun_cx + sun_r, sun_cy + sun_r],
        fill=SUN_COLOR,
    )
    hl_r = sun_r * 0.55
    draw.ellipse(
        [sun_cx - hl_r * 0.4, sun_cy - hl_r * 1.1, sun_cx + hl_r * 1.0, sun_cy + hl_r * 0.4],
        fill=SUN_HIGHLIGHT,
    )

    # Cloud made of overlapping ellipses
    cloud_cy = s * 0.64
    puffs = [
        (s * 0.30, cloud_cy, s * 0.14),
        (s * 0.44, cloud_cy - s * 0.06, s * 0.19),
        (s * 0.60, cloud_cy, s * 0.15),
        (s * 0.72, cloud_cy + s * 0.02, s * 0.12),
    ]
    for cx, cy, r in puffs:
        draw.ellipse([cx - r, cy - r, cx + r, cy + r], fill=CLOUD_COLOR)
    draw.rounded_rectangle(
        [s * 0.28, cloud_cy - s * 0.02, s * 0.78, cloud_cy + s * 0.14],
        radius=s * 0.08,
        fill=CLOUD_COLOR,
    )

    img = img.resize((size, size), Image.LANCZOS)

    if round_mask:
        round_alpha = Image.new("L", (size, size), 0)
        ImageDraw.Draw(round_alpha).ellipse([0, 0, size - 1, size - 1], fill=255)
        out = Image.new("RGBA", (size, size), (0, 0, 0, 0))
        out.paste(img, (0, 0), round_alpha)
        return out
    return img


def main(res_dir: str):
    import os
    for folder, size in SIZES.items():
        d = os.path.join(res_dir, folder)
        os.makedirs(d, exist_ok=True)
        make_icon(size, round_mask=False).save(os.path.join(d, "ic_launcher.png"))
        make_icon(size, round_mask=True).save(os.path.join(d, "ic_launcher_round.png"))
    print("Generated launcher icons in", res_dir)


if __name__ == "__main__":
    import sys
    main(sys.argv[1])
