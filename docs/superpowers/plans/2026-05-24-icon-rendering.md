# Icon Rendering Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Write a Blender headless Python script that renders `.bbmodel` and `.blockymodel` files to 64×64 icon PNGs, then regenerate icons for apple martini, autumn sip, berry punch, and corn cooler.

**Architecture:** A single script (`scripts/render_icon.py`) runs inside Blender's background mode. It parses the model JSON, builds a Blender mesh scene from cube elements, sets up an isometric orthographic camera, and renders to a 64×64 transparent PNG. The script is invoked once per drink icon.

**Tech Stack:** Blender 4.5.1 LTS (`/Applications/Blender.app`), Python 3 (Blender's bundled bpy), JSON, base64

---

## Model → Icon Mapping

| Drink | Model file | Output icon |
|---|---|---|
| Berry punch | `src/main/resources/Common/Items/cocktail_berry_solo_cup.bbmodel` | `cocktail_berry_punch.png` |
| Corn cooler | `src/main/resources/Common/Items/cocktail_corn_jar.bbmodel` | `cocktail_corn_cooler.png` |
| Autumn sip | `src/main/resources/Common/Items/cocktail_pumkin.bbmodel` | `cocktail_autumn_sip.png` |
| Apple martini | `src/main/resources/Common/Items/cocktail_martini.blockymodel` | `cocktail_apple_martini.png` (texture: `BlockTextures/cocktail_martini.png`) |

---

## File Map

- **Create:** `scripts/render_icon.py` — Blender headless render script
- **Overwrite:** `src/main/resources/Common/Icons/ItemsGenerated/cocktail_berry_punch.png`
- **Overwrite:** `src/main/resources/Common/Icons/ItemsGenerated/cocktail_corn_cooler.png`
- **Overwrite:** `src/main/resources/Common/Icons/ItemsGenerated/cocktail_autumn_sip.png`
- **Overwrite:** `src/main/resources/Common/Icons/ItemsGenerated/cocktail_apple_martini.png`

---

## Task 1: Create `scripts/render_icon.py`

**Files:**
- Create: `scripts/render_icon.py`

- [ ] **Step 1: Create scripts directory and write the render script**

```bash
mkdir -p scripts
```

Then write `scripts/render_icon.py` with this exact content:

```python
#!/usr/bin/env python3
"""
Render a .bbmodel or .blockymodel to a 64x64 icon PNG using Blender headless.

Usage:
    /Applications/Blender.app/Contents/MacOS/Blender \
        --background --python scripts/render_icon.py \
        -- <model_path> <output_png>
"""

import sys
import os
import json
import math
import base64
import tempfile

import bpy
import bmesh
import mathutils

# ── Args ─────────────────────────────────────────────────────────────────────
argv = sys.argv[sys.argv.index("--") + 1 :] if "--" in sys.argv else []
if len(argv) < 2:
    print("Usage: blender --background --python render_icon.py -- <model> <output.png>")
    sys.exit(1)

MODEL_PATH = argv[0]
OUTPUT_PATH = argv[1]
ICON_SIZE = 64
SCALE = 1 / 16  # bbmodel pixel units → Blender units

# ── Scene setup ───────────────────────────────────────────────────────────────
bpy.ops.wm.read_factory_settings(use_empty=True)
scene = bpy.context.scene
col = bpy.data.collections.new("Icon")
scene.collection.children.link(col)

# ── Coord transform ───────────────────────────────────────────────────────────
# bbmodel: X=right, Y=up, Z=south(+Z toward viewer)
# Blender:  X=right, Y=north(-Y toward viewer), Z=up
# Mapping:  bx=x, by=-z, bz=y
def b(x, y, z):
    return (x * SCALE, -z * SCALE, y * SCALE)

# ── Texture helpers ───────────────────────────────────────────────────────────
def load_image_b64(b64_str, name="tex"):
    if "," in b64_str:
        b64_str = b64_str.split(",", 1)[1]
    data = base64.b64decode(b64_str)
    tmp = tempfile.NamedTemporaryFile(suffix=".png", delete=False)
    tmp.write(data); tmp.flush(); tmp.close()
    img = bpy.data.images.load(tmp.name)
    img.name = name
    os.unlink(tmp.name)
    return img

def load_image_file(path, name="tex"):
    if not os.path.exists(path):
        return None
    img = bpy.data.images.load(os.path.abspath(path))
    img.name = name
    return img

def make_unlit_material(img):
    mat = bpy.data.materials.new(name="IconMat")
    mat.use_nodes = True
    nt = mat.node_tree
    nt.nodes.clear()
    out    = nt.nodes.new("ShaderNodeOutputMaterial")
    emit   = nt.nodes.new("ShaderNodeEmission")
    transp = nt.nodes.new("ShaderNodeBsdfTransparent")
    mix    = nt.nodes.new("ShaderNodeMixShader")
    tex    = nt.nodes.new("ShaderNodeTexImage")
    tex.image = img
    tex.interpolation = "Closest"
    nt.links.new(emit.inputs["Color"],  tex.outputs["Color"])
    nt.links.new(mix.inputs[0],         tex.outputs["Alpha"])
    nt.links.new(mix.inputs[1],         transp.outputs["BSDF"])
    nt.links.new(mix.inputs[2],         emit.outputs["Emission"])
    nt.links.new(out.inputs["Surface"], mix.outputs["Shader"])
    mat.blend_method = "CLIP"
    return mat

# ── Mesh builder ──────────────────────────────────────────────────────────────
# Face definitions: (vertex indices in CCW winding, UV corners per loop)
# UV corners: BL=bottom-left, TL=top-left, TR=top-right, BR=bottom-right
# Vertices are indexed as:
#   0=(x1,y1,z1)  1=(x2,y1,z1)  2=(x2,y2,z1)  3=(x1,y2,z1)
#   4=(x1,y1,z2)  5=(x2,y1,z2)  6=(x2,y2,z2)  7=(x1,y2,z2)
# (bbmodel coords; face names follow Minecraft convention)
FACE_DEFS = {
    "north": {"vi": [0, 3, 2, 1], "uv": ["BL", "TL", "TR", "BR"]},
    "south": {"vi": [4, 7, 6, 5], "uv": ["BL", "TL", "TR", "BR"]},
    "east":  {"vi": [1, 5, 6, 2], "uv": ["BL", "BR", "TR", "TL"]},
    "west":  {"vi": [0, 4, 7, 3], "uv": ["BR", "BL", "TL", "TR"]},
    "up":    {"vi": [2, 3, 7, 6], "uv": ["TR", "TL", "BL", "BR"]},
    "down":  {"vi": [0, 1, 5, 4], "uv": ["BL", "BR", "TR", "TL"]},
}

def _uv_corners(u1, v1, u2, v2, tex_w, tex_h):
    return {
        "BL": (u1 / tex_w, 1 - v2 / tex_h),
        "TL": (u1 / tex_w, 1 - v1 / tex_h),
        "TR": (u2 / tex_w, 1 - v1 / tex_h),
        "BR": (u2 / tex_w, 1 - v2 / tex_h),
    }

def add_cube(frm, to, face_uvs, mat, tex_w, tex_h,
             rotation=None, pivot=None):
    """
    frm, to: [x, y, z] in bbmodel units.
    face_uvs: dict of face_name -> {"uv": [u1,v1,u2,v2]} (bbmodel format).
    rotation: [rx, ry, rz] in degrees (optional).
    pivot:    [x, y, z]    rotation origin in bbmodel units (optional).
    """
    x1, y1, z1 = frm
    x2, y2, z2 = to

    # Skip degenerate cubes
    if abs(x2-x1) < 0.01 and abs(y2-y1) < 0.01 and abs(z2-z1) < 0.01:
        return

    # 8 corner vertices in bbmodel space, mapped to Blender coords
    verts_b = [
        b(x1, y1, z1), b(x2, y1, z1), b(x2, y2, z1), b(x1, y2, z1),
        b(x1, y1, z2), b(x2, y1, z2), b(x2, y2, z2), b(x1, y2, z2),
    ]

    mesh = bpy.data.meshes.new("cm")
    obj  = bpy.data.objects.new("cube", mesh)
    col.objects.link(obj)
    bpy.context.view_layer.objects.active = obj

    bm = bmesh.new()
    vts = [bm.verts.new(v) for v in verts_b]
    bm.verts.ensure_lookup_table()
    uv_layer = bm.loops.layers.uv.new("UVMap")
    mesh.materials.append(mat)

    for fname, fdef in FACE_DEFS.items():
        if fname not in face_uvs:
            continue
        raw = face_uvs[fname]
        if raw is None:
            continue
        uv_rect = raw.get("uv", [0, 0, tex_w, tex_h])
        corners = _uv_corners(*uv_rect, tex_w, tex_h)
        face_verts = [vts[i] for i in fdef["vi"]]
        try:
            face = bm.faces.new(face_verts)
            for loop, corner_key in zip(face.loops, fdef["uv"]):
                loop[uv_layer].uv = corners[corner_key]
        except Exception:
            pass  # duplicate face; skip

    bm.to_mesh(mesh)
    bm.free()
    mesh.update()

    # Apply rotation around pivot
    if rotation and any(r != 0 for r in rotation):
        rx, ry, rz = [math.radians(r) for r in rotation]
        # bbmodel rotation axes in Blender coords (x→x, y→z, z→-y)
        euler = mathutils.Euler((rx, rz, -ry), "XYZ")
        if pivot:
            ox, oy, oz = b(*pivot)
            pivot_v = mathutils.Vector((ox, oy, oz))
            rot_m = euler.to_matrix().to_4x4()
            obj.matrix_world = (
                mathutils.Matrix.Translation(pivot_v)
                @ rot_m
                @ mathutils.Matrix.Translation(-pivot_v)
            )
        else:
            obj.rotation_euler = euler

# ── .bbmodel parser ───────────────────────────────────────────────────────────
def build_bbmodel(data, model_dir):
    textures = data.get("textures", [])
    img = None
    if textures:
        src = textures[0].get("source", "")
        if src.startswith("data:image/"):
            img = load_image_b64(src, "bb_tex")
        else:
            rel = textures[0].get("relative_path", "")
            if rel:
                img = load_image_file(
                    os.path.normpath(os.path.join(model_dir, rel))
                )
    if img is None:
        print(f"ERROR: no texture in {MODEL_PATH}")
        sys.exit(1)

    res   = data.get("resolution", {})
    tex_w = res.get("width",  64)
    tex_h = res.get("height", 64)
    mat   = make_unlit_material(img)

    for elem in data.get("elements", []):
        if elem.get("type") != "cube":
            continue
        add_cube(
            frm=elem["from"], to=elem["to"],
            face_uvs=elem.get("faces", {}),
            mat=mat, tex_w=tex_w, tex_h=tex_h,
            rotation=elem.get("rotation"),
            pivot=elem.get("origin"),
        )

# ── .blockymodel parser ───────────────────────────────────────────────────────
# Face name mapping: blockymodel → bbmodel convention
_BLK_TO_BB = {
    "front": "south", "back": "north",
    "right": "east",  "left": "west",
    "top":   "up",    "bottom": "down",
}

def _face_uv_blk(face_name, layout, sx, sy, sz):
    """Return [u1,v1,u2,v2] for a blockymodel face, or None if missing."""
    if face_name not in layout:
        return None
    e  = layout[face_name]
    ox = e["offset"]["x"]
    oy = e["offset"]["y"]
    face_dims = {
        "front": (sx, sy), "back":  (sx, sy),
        "right": (sz, sy), "left":  (sz, sy),
        "top":   (sx, sz), "bottom":(sx, sz),
    }
    fw, fh = face_dims.get(face_name, (sx, sy))
    return [ox, oy, ox + fw, oy + fh]

def _visit_nodes(nodes, mat, tex_w, tex_h, parent_world=None):
    if parent_world is None:
        parent_world = [0.0, 0.0, 0.0]

    for node in nodes:
        pos = node.get("position", {"x": 0, "y": 0, "z": 0})
        world = [
            parent_world[0] + pos["x"],
            parent_world[1] + pos["y"],
            parent_world[2] + pos["z"],
        ]

        shape = node.get("shape", {})
        if shape.get("type") == "box":
            settings = shape.get("settings", {})
            size = settings.get("size", {"x": 1, "y": 1, "z": 1})
            sx, sy, sz = size["x"], size["y"], size["z"]
            off = shape.get("offset", {"x": 0, "y": 0, "z": 0})
            cx = world[0] + off["x"]
            cy = world[1] + off["y"]
            cz = world[2] + off["z"]

            frm = [cx - sx / 2, cy - sy / 2, cz - sz / 2]
            to  = [cx + sx / 2, cy + sy / 2, cz + sz / 2]

            layout   = shape.get("textureLayout", {})
            face_uvs = {}
            for blk_name, bb_name in _BLK_TO_BB.items():
                raw = _face_uv_blk(blk_name, layout, sx, sy, sz)
                if raw:
                    face_uvs[bb_name] = {"uv": raw}

            # Quaternion → euler (degrees)
            orient = node.get("orientation", {"x": 0, "y": 0, "z": 0, "w": 1})
            q = mathutils.Quaternion(
                (orient["w"], orient["x"], orient["y"], orient["z"])
            )
            eu = q.to_euler("XYZ")
            rot_deg = [math.degrees(r) for r in eu]

            add_cube(
                frm=frm, to=to,
                face_uvs=face_uvs,
                mat=mat, tex_w=tex_w, tex_h=tex_h,
                rotation=rot_deg if any(abs(r) > 0.01 for r in rot_deg) else None,
                pivot=[cx, cy, cz],
            )

        _visit_nodes(node.get("children", []), mat, tex_w, tex_h, world)

def build_blockymodel(data, model_dir, model_name):
    # Texture lives in ../BlockTextures/<model_name>.png
    candidates = [
        os.path.normpath(os.path.join(model_dir, "..", "BlockTextures", f"{model_name}.png")),
        os.path.normpath(os.path.join(model_dir, f"{model_name}.png")),
    ]
    img = None
    for p in candidates:
        img = load_image_file(p)
        if img:
            break
    if img is None:
        print(f"ERROR: texture not found for {model_name}. Looked in: {candidates}")
        sys.exit(1)

    tex_w = tex_h = 64  # blockymodel textures are always 64×64
    mat = make_unlit_material(img)
    _visit_nodes(data.get("nodes", []), mat, tex_w, tex_h)

# ── Camera ────────────────────────────────────────────────────────────────────
def setup_camera():
    bpy.ops.object.camera_add()
    cam_obj = bpy.context.active_object
    scene.camera = cam_obj
    # Isometric: 45° yaw, 54.74° pitch (= 90° - arctan(1/√2))
    cam_obj.rotation_euler = (math.radians(54.74), 0, math.radians(45))
    cam_obj.location = mathutils.Vector((2.0, -2.0, 2.0))
    cam          = cam_obj.data
    cam.type     = "ORTHO"
    cam.ortho_scale = 1.3  # increase if model gets clipped
    return cam_obj

# ── Render ────────────────────────────────────────────────────────────────────
def setup_render():
    scene.render.engine                          = "BLENDER_EEVEE_NEXT"
    scene.render.film_transparent                = True
    scene.render.image_settings.file_format      = "PNG"
    scene.render.image_settings.color_mode       = "RGBA"
    scene.render.image_settings.compression      = 9
    scene.render.resolution_x                    = ICON_SIZE
    scene.render.resolution_y                    = ICON_SIZE
    scene.render.filepath                        = os.path.abspath(OUTPUT_PATH)

# ── Main ──────────────────────────────────────────────────────────────────────
with open(MODEL_PATH) as f:
    data = json.load(f)

model_dir  = os.path.dirname(os.path.abspath(MODEL_PATH))
model_name = os.path.splitext(os.path.basename(MODEL_PATH))[0]
if model_name.endswith(".blockymodel"):
    model_name = model_name[: -len(".blockymodel")]

if "meta" in data and "elements" in data:
    build_bbmodel(data, model_dir)
elif "nodes" in data:
    build_blockymodel(data, model_dir, model_name)
else:
    print("ERROR: unrecognized model format")
    sys.exit(1)

setup_camera()
setup_render()
bpy.ops.render.render(write_still=True)
print(f"✓ Icon saved → {OUTPUT_PATH}")
```

- [ ] **Step 2: Commit the script**

```bash
git add scripts/render_icon.py
git commit -m "Add Blender headless icon render script"
```

---

## Task 2: Test render — berry punch

**Files:**
- Modify (render output): `src/main/resources/Common/Icons/ItemsGenerated/cocktail_berry_punch.png`

- [ ] **Step 1: Run the render for berry punch**

```bash
/Applications/Blender.app/Contents/MacOS/Blender \
  --background --python scripts/render_icon.py \
  -- src/main/resources/Common/Items/cocktail_berry_solo_cup.bbmodel \
     src/main/resources/Common/Icons/ItemsGenerated/cocktail_berry_punch.png \
  2>&1 | tail -20
```

Expected: last line is `✓ Icon saved → .../cocktail_berry_punch.png`

- [ ] **Step 2: Open the output and visually inspect it**

```bash
open src/main/resources/Common/Icons/ItemsGenerated/cocktail_berry_punch.png
```

Expected: 64×64 PNG with the solo cup model rendered in isometric view, transparent background, no clipping.

- [ ] **Step 3: If model is clipped or too small, adjust `ortho_scale`**

In `scripts/render_icon.py`, find this line:

```python
    cam.ortho_scale = 1.3
```

- If model is clipped/cut off → increase value (try 1.6, 2.0)
- If model is tiny in frame → decrease value (try 1.0, 0.9)

Re-run step 1 after each adjustment until the icon looks good.

- [ ] **Step 4: Commit the adjusted script (if changed) and the berry punch icon**

```bash
git add scripts/render_icon.py \
        src/main/resources/Common/Icons/ItemsGenerated/cocktail_berry_punch.png
git commit -m "Render cocktail_berry_punch icon from solo cup model"
```

---

## Task 3: Render remaining three icons

**Files:**
- Modify: `src/main/resources/Common/Icons/ItemsGenerated/cocktail_corn_cooler.png`
- Modify: `src/main/resources/Common/Icons/ItemsGenerated/cocktail_autumn_sip.png`
- Modify: `src/main/resources/Common/Icons/ItemsGenerated/cocktail_apple_martini.png`

- [ ] **Step 1: Render corn cooler**

```bash
/Applications/Blender.app/Contents/MacOS/Blender \
  --background --python scripts/render_icon.py \
  -- src/main/resources/Common/Items/cocktail_corn_jar.bbmodel \
     src/main/resources/Common/Icons/ItemsGenerated/cocktail_corn_cooler.png \
  2>&1 | tail -5
open src/main/resources/Common/Icons/ItemsGenerated/cocktail_corn_cooler.png
```

Verify: looks correct (corn jar model, isometric, 64×64, transparent bg).

- [ ] **Step 2: Render autumn sip**

```bash
/Applications/Blender.app/Contents/MacOS/Blender \
  --background --python scripts/render_icon.py \
  -- src/main/resources/Common/Items/cocktail_pumkin.bbmodel \
     src/main/resources/Common/Icons/ItemsGenerated/cocktail_autumn_sip.png \
  2>&1 | tail -5
open src/main/resources/Common/Icons/ItemsGenerated/cocktail_autumn_sip.png
```

Verify: pumpkin cocktail model, isometric, 64×64, transparent bg.

- [ ] **Step 3: Render apple martini (blockymodel)**

```bash
/Applications/Blender.app/Contents/MacOS/Blender \
  --background --python scripts/render_icon.py \
  -- src/main/resources/Common/Items/cocktail_martini.blockymodel \
     src/main/resources/Common/Icons/ItemsGenerated/cocktail_apple_martini.png \
  2>&1 | tail -5
open src/main/resources/Common/Icons/ItemsGenerated/cocktail_apple_martini.png
```

Expected texture path resolved: `src/main/resources/Common/BlockTextures/cocktail_martini.png`
Verify: martini glass model, isometric, 64×64, transparent bg.

- [ ] **Step 4: Commit all three icons**

```bash
git add src/main/resources/Common/Icons/ItemsGenerated/cocktail_corn_cooler.png \
        src/main/resources/Common/Icons/ItemsGenerated/cocktail_autumn_sip.png \
        src/main/resources/Common/Icons/ItemsGenerated/cocktail_apple_martini.png
git commit -m "Regenerate icons for corn cooler, autumn sip, and apple martini"
```

---

## Troubleshooting

**"ERROR: texture not found"** for apple martini:
The blockymodel parser looks for `cocktail_martini.png` in `../BlockTextures/` relative to the model file. Verify the file exists:
```bash
ls src/main/resources/Common/BlockTextures/cocktail_martini.png
```
If missing, the texture was deleted with the old block textures and you'll need to restore or recreate it.

**UV looks wrong / faces are mirrored:**
The UV corner assignment per face (`FACE_DEFS`) follows Minecraft convention. If a specific face looks mirrored or rotated, find that face's `"uv"` list in `FACE_DEFS` and swap/rotate the corner keys. For example, for a mirrored north face change `["BL","TL","TR","BR"]` → `["BR","TR","TL","BL"]`.

**Model renders as all-black or missing faces:**
The `make_unlit_material` shader uses Emission so there's no lighting dependency. All-black usually means the texture image has a wrong path. Check the Blender stdout for `ERROR:` lines.

**`ortho_scale` adjustment doesn't match all models:**
Each model has a different bounding box. If models need very different scales, add a per-model override at the top of the script:
```python
ORTHO_SCALE_OVERRIDES = {
    "cocktail_martini": 1.0,
    "cocktail_pumkin": 1.5,
}
model_ortho = ORTHO_SCALE_OVERRIDES.get(model_name, 1.3)
```
Then replace `cam.ortho_scale = 1.3` with `cam.ortho_scale = model_ortho`.
