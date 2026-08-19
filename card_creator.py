# card_creator.py
import sys, os, argparse, json
from PIL import Image, ImageDraw, ImageFont, ImageFilter
import textwrap

TEMPLATES = {
    1: {"name": "Classic", "bg": (255,255,255), "text": (0,0,0), "font_size": 60},
    2: {"name": "Modern", "bg": (255,215,0), "text": (139,0,0), "font_size": 70},
    3: {"name": "Playful", "bg": (173,216,230), "text": (255,69,0), "font_size": 65}
}

def create_card(name, age, message, template, bg_color, text_color, photo_path, bg_image_path, output):
    # Use template defaults if colors not overridden
    if bg_color is None:
        bg_color = TEMPLATES[template]["bg"]
    else:
        bg_color = tuple(int(bg_color.lstrip('#')[i:i+2], 16) for i in (0,2,4))
    if text_color is None:
        text_color = TEMPLATES[template]["text"]
    else:
        text_color = tuple(int(text_color.lstrip('#')[i:i+2], 16) for i in (0,2,4))

    # Create base image
    width, height = 800, 600
    if bg_image_path and os.path.exists(bg_image_path):
        bg_img = Image.open(bg_image_path).resize((width, height))
        img = bg_img.convert("RGB")
    else:
        img = Image.new("RGB", (width, height), bg_color)

    draw = ImageDraw.Draw(img)
    try:
        font_large = ImageFont.truetype("/usr/share/fonts/truetype/liberation/LiberationSans-Bold.ttf", 60)
        font_medium = ImageFont.truetype("/usr/share/fonts/truetype/liberation/LiberationSans-Regular.ttf", 40)
        font_small = ImageFont.truetype("/usr/share/fonts/truetype/liberation/LiberationSans-Italic.ttf", 30)
    except:
        font_large = ImageFont.load_default()
        font_medium = ImageFont.load_default()
        font_small = ImageFont.load_default()

    # Draw title
    title = "🎉 Happy Birthday! 🎉"
    draw.text((width//2, 50), title, font=font_large, fill=text_color, anchor="mt")

    # Draw name
    draw.text((width//2, 200), name, font=font_large, fill=text_color, anchor="mt")

    # Draw age if provided
    if age:
        draw.text((width//2, 280), str(age), font=font_medium, fill=text_color, anchor="mt")

    # Draw message with wrapping
    wrapped = textwrap.fill(message, width=20)
    draw.text((width//2, 380), wrapped, font=font_small, fill=text_color, anchor="mt")

    # Draw emoji decorations (just text)
    draw.text((width//2, 500), "🎂  🎈  🎁", font=font_medium, fill=text_color, anchor="mt")

    # Embed photo if provided
    if photo_path and os.path.exists(photo_path):
        try:
            photo = Image.open(photo_path)
            # Resize to fit in top-right corner
            photo.thumbnail((150, 150))
            img.paste(photo, (width - photo.width - 20, 20))
        except Exception as e:
            print(f"Warning: could not embed photo: {e}", file=sys.stderr)

    # Apply a subtle filter for better look (optional)
    # img = img.filter(ImageFilter.SHARPEN)

    img.save(output)
    print(f"Card saved to {output}")

def main():
    parser = argparse.ArgumentParser(description="Birthday Card Creator")
    parser.add_argument('-n', '--name', required=True, help="Recipient's name")
    parser.add_argument('-a', '--age', type=int, help="Age (optional)")
    parser.add_argument('-m', '--message', required=True, help="Custom message")
    parser.add_argument('-t', '--template', type=int, choices=[1,2,3], default=1, help="Template (1-3)")
    parser.add_argument('--bg', help="Background color (hex, e.g. #FF0000)")
    parser.add_argument('--text', help="Text color (hex)")
    parser.add_argument('--photo', help="Path to photo to embed")
    parser.add_argument('--bg-image', help="Path to background image")
    parser.add_argument('-o', '--output', default="card.png", help="Output PNG file")
    parser.add_argument('--html', help="Export to HTML file (optional)")
    args = parser.parse_args()

    create_card(args.name, args.age, args.message, args.template, args.bg, args.text, args.photo, args.bg_image, args.output)

    if args.html:
        html_content = f"""<!DOCTYPE html>
<html><head><title>Birthday Card</title>
<style>body{{font-family:sans-serif;text-align:center;background:#f0f0f0;}}
.card{{background:{args.bg or '#FFFFFF'};color:{args.text or '#000000'};
border-radius:20px;padding:40px;max-width:600px;margin:50px auto;box-shadow:0 4px 8px rgba(0,0,0,0.2);}}
h1{{font-size:3em;}} .name{{font-size:2.5em;}} .age{{font-size:2em;}} .msg{{font-size:1.5em;}}</style>
</head><body><div class="card">
<h1>🎉 Happy Birthday! 🎉</h1>
<div class="name">{args.name}</div>
{'' if not args.age else f'<div class="age">{args.age}</div>'}
<div class="msg">{args.message}</div>
<div style="font-size:3em;">🎂 🎈 🎁</div>
</div></body></html>"""
        with open(args.html, 'w') as f:
            f.write(html_content)
        print(f"HTML card saved to {args.html}")

if __name__ == "__main__":
    main()
