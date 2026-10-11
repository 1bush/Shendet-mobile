"""Gjeneron ikonat cartoon (PNG) për SHËNDETI-IM në `assets/`.

Përmbajtja:
- 1 logo hyrjeje (jeshile + zemër e bardhë + yje) → `assets/logo.png` (512px)
- 8 ikona modulesh (kartë e bardhë + fytyrë cartoon me ngjyrën e modulit)
  → `assets/food.png`, `pet.png`, `cosmetics.png`, `asistent.png`
- 1 splash → `assets/splash.png` (1024px, logo e zmadhuar)

Ekzekutimi: `python3 scripts/make_icons.py` (kërkon PIL).
Identiteti ruhet: jeshilja `#1a7a4a`, emri SHËNDETI-IM, tagline nuk preket.
"""

from PIL import Image, ImageDraw

S = 300  # kanavaca e vizatimit

# ─── Moduli → ngjyra ──────────────────────────────────────────────────────────
COLORS = {
    "food":      "#1a7a4a",
    "pet":       "#4a2d8a",
    "cosmetics": "#c0427a",
    "asistent":  "#f1c40f",
    "fatura":    "#1a7a4a",
    "barnat":    "#e74c3c",
    "harta":     "#7b52d4",
    "profili":   "#60a5fa",
}

DARK = "#1a2e1a"  # konturet e fytyrës


def make_draw():
    """Krijon kanavacë të re transparente; kthen (img, draw)."""
    img = Image.new("RGBA", (S, S), (0, 0, 0, 0))
    return img, ImageDraw.Draw(img)



def card(draw, color_hex, r=46, bw=18):
    """Të dykëndëshi i rimë me kruje + trenë të ngjyret."""
    draw.rounded_rectangle([12, 12, S - 12, S - 12], radius=r, fill="white", outline=color_hex, width=bw)


def save(img, name, size=512, outdir="assets"):
    """Ruan PNG-në e zmadhuar me LANCZOS në `assets/`."""
    out = img.resize((size, size), Image.LANCZOS)
    path = f"{outdir}/{name}.png"
    out.save(path)
    print("created", path)


# ─── Ndihmës vizatimi ─────────────────────────────────────────────────────────────
def brow(draw, x0, y0, x1, y1, width=12, color=DARK):
    draw.line([x0, y0, x1, y1], fill=color, width=width)


def mouth(draw, smile=True, color=DARK, width=14):
    if smile:
        draw.arc([115, 175, 185, 245], start=180, end=360, fill=color, width=width)  # smile curve
    else:
        draw.arc([115, 125, 185, 195], start=180, end=360, fill=color, width=width)  # frown


def eye(draw, x, y, r=8, color=DARK, dot=True):
    if dot:  # sy i vogël + qëngësi i madh
        draw.ellipse([x - 14, y - 14, x + 14, y + 14], fill=color)
        draw.ellipse([x - 4, y - 4, x + 4, y + 4], fill="white")
        draw.ellipse([x - 2, y - 2, x + 2, y + 2], fill=color)

# ─── 1. Ushqim (drithë + dy qëngësi) ────────────────────────────────────────────
def icon_food(draw):
    draw.ellipse([70, 120, 230, 260], fill="#1f8b59")
    draw.ellipse([78, 128, 222, 252], outline="#1a7a4a", width=16)
    draw.line([95, 65, 160, 80], fill="#2e8b57", width=26)
    draw.line([160, 80, 200, 110], fill="#2e8b57", width=24)
    eye(draw, 138, 130); eye(draw, 162, 130)
    mouth(draw, smile=True)
    draw.ellipse([128, 170, 172, 190], fill="#e74c3c")


# ─── 2. Kafshë (kënga, butësi) ──────────────────────────────────────────────────
def icon_pet(draw):
    draw.polygon([(120, 40), (45, 95), (105, 120)], fill="#4a2d8a")
    draw.polygon([(180, 40), (255, 95), (195, 120)], fill="#4a2d8a")
    draw.ellipse([70, 140, 230, 280], fill="#3b1d71")
    draw.ellipse([78, 148, 222, 272], outline="#4a2d8a", width=20)
    eye(draw, 138, 165, r=7); eye(draw, 162, 165, r=7)
    draw.ellipse([120, 190, 140, 230], fill="#e74c3c")
    draw.ellipse([160, 190, 180, 230], fill="#e74c3c")
    draw.arc([130, 235, 170, 285], start=0, end=180, fill="#e74c3c", width=20)


# ─── 3. Kozmetikë (buteli) ──────────────────────────────────────────────────────
def icon_cosmetics(draw):
    draw.rounded_rectangle([92, 150, 208, 235], radius=24, fill="#c0427a")
    draw.rounded_rectangle([105, 60, 195, 130], radius=18, fill="#e74c3c")
    draw.rounded_rectangle([118, 74, 182, 130], radius=6, fill="#c0427a")
    draw.ellipse([150, 100, 162, 112], fill="#fff2e0")
    brow(draw, 112, 188, 140, 172)
    brow(draw, 190, 172, 158, 188)
    eye(draw, 140, 200); eye(draw, 160, 200)
    mouth(draw, smile=True)
    draw.ellipse([124, 240, 176, 272], fill="#e74c3c")


# ─── 4. Asistenti (robot me antenë) ─────────────────────────────────────────────
def icon_asistent(draw):
    draw.rounded_rectangle([75, 80, 225, 245], radius=26, fill="#f1c40f")
    draw.rounded_rectangle([115, 245, 185, 275], radius=10, fill="#e6c200")
    draw.line([150, 35, 150, 80], fill="#333", width=20)
    draw.ellipse([138, 22, 162, 50], fill="#fff")
    draw.ellipse([148, 30, 152, 40], fill="#e74c3c")
    draw.polygon([(112, 155), (112, 175), (138, 165)], fill="#333")
    draw.polygon([(188, 155), (188, 175), (162, 165)], fill="#333")
    eye(draw, 140, 190, r=9)
    eye(draw, 160, 190, r=9)
    eye(draw, 140, 212, r=9)
    eye(draw, 160, 212, r=9)
    mouth(draw, smile=False, color="#333")
    draw.line([138, 250, 138, 272], fill="#333", width=14)
    draw.line([162, 250, 162, 272], fill="#333", width=14)

# ─── 5. Fatura (pritisë me shpresë) ─────────────────────────────────────────────
def icon_fatura(draw):
    draw.polygon([(120, 90), (180, 90), (165, 130), (120, 130)], fill="#1a7a4a")
    draw.rounded_rectangle([60, 120, 240, 260], radius=14, fill="white", outline="#1a7a4a", width=20)
    draw.line([110, 170, 190, 170], fill="#1a7a4a", width=14)
    draw.line([110, 210, 190, 210], fill="#1a7a4a", width=14)
    draw.line([110, 250, 190, 250], fill="#1a7a4a", width=14)
    eye(draw, 205, 140, r=6)
    mouth(draw, smile=False)
    draw.ellipse([220, 200, 240, 240], fill="#e74c3c")


# ─── 6. Barnat (pill) ───────────────────────────────────────────────────────────
def icon_barnat(draw):
    draw.rounded_rectangle([95, 110, 205, 200], radius=45, fill="#e74c3c", outline="#c0392b", width=16)
    draw.pieslice([80, 100, 165, 185], start=180, end=360, fill="white")
    draw.pieslice([150, 100, 220, 185], start=0, end=180, fill="white")
    draw.line([120, 100, 140, 110], fill="#c0392b", width=14)
    draw.line([160, 110, 180, 100], fill="#c0392b", width=14)
    mouth(draw, smile=True)
    draw.ellipse([120, 220, 180, 260], fill="#e74c3c")


# ─── 7. Harta (mapë) ────────────────────────────────────────────────────────────
def icon_harta(draw):
    draw.polygon([(150, 35), (115, 150), (185, 150)], fill="#7b52d4")
    draw.ellipse([65, 115, 235, 290], fill="#7b52d4", outline="#9b7bc4", width=14)
    draw.ellipse([95, 155, 205, 245], fill="white")
    draw.ellipse([110, 170, 190, 220], fill="#f1c40f")
    draw.ellipse([122, 182, 178, 212], fill="#e74c3c")
    draw.line([120, 250, 180, 250], fill="#e74c3c", width=12)
    eye(draw, 135, 150, r=7)
    eye(draw, 165, 150, r=7)
    mouth(draw, smile=False)


# ─── 8. Profili (personazh me shpresë) ──────────────────────────────────────────
def icon_profili(draw):
    draw.ellipse([85, 85, 215, 205], fill="#3b82f6")
    draw.ellipse([93, 93, 207, 197], outline="#60a5fa", width=16)
    eye(draw, 130, 145, r=9)
    eye(draw, 170, 145, r=9)
    mouth(draw, smile=True)
    draw.ellipse([128, 195, 172, 235], fill="#e74c3c")
    draw.rounded_rectangle([40, 205, 260, 305], radius=40, fill=DARK)

# ─── Logo është te Gjeneratorët më poshtë (make_logo me size/name) ────────────────

# ─── Gjeneratorët ─────────────────────────────────────────────────────────────────
def make_icon(name, fn):
    """Krijon ikonën cartoon: karta + fytyra e modulit, dhe e ruan në disk."""
    img, draw = make_draw()
    card(draw, COLORS[name])
    fn(draw)
    save(img, name)


def make_logo(size=512, name="logo"):
    """Logo e hyrjes: kartë jeshile + zemër e bardhë + yje të verdha."""
    img, d = make_draw()
    card(d, "#1a7a4a", r=90, bw=22)
    d.ellipse([90, 110, 210, 210], fill="#ffffff")
    d.ellipse([75, 85, 225, 225], outline="#ffffff", width=18)
    d.polygon([(85, 200), (215, 200), (150, 275)], fill="#ffffff")
    d.polygon([(55, 255), (85, 235), (85, 295)], fill="#f1c40f")
    d.polygon([(245, 255), (215, 235), (215, 295)], fill="#f1c40f")
    save(img, name, size=size)


# ─── Ekzekutimi ─────────────────────────────────────────────────────────────────
if __name__ == "__main__":
    import os
    os.makedirs("assets", exist_ok=True)
    make_logo()            # assets/logo.png (512px)
    make_logo(1024, "splash")  # assets/splash.png (1024px)
    ok = 0
    icons = [
        ("food", icon_food),
        ("pet", icon_pet),
        ("cosmetics", icon_cosmetics),
        ("asistent", icon_asistent),
        ("fatura", icon_fatura),
        ("barnat", icon_barnat),
        ("harta", icon_harta),
        ("profili", icon_profili),
    ]
    for name, fn in icons:
        try:
            make_icon(name, fn)
            ok += 1
        except Exception as e:  # noqa: BLE001
            print(f"FAILED {name}: {e!r}")
    print(f"ikonat e kryera: {ok}/{len(icons)} + logo + splash")
