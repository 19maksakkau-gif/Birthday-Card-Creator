🎂 Birthday Card Creator — Multi‑Language Greeting Card Generator
8 languages, one magical card maker – create personalized birthday cards with custom messages, photos, and themes – right from your terminal.

✨ Features
🎨 Multiple templates – choose from classic, modern, or playful designs

✍️ Custom text – name, age, and personalized message

🖼️ Add photos – overlay a portrait or logo onto the card

🌈 Color themes – pick background and text colors (hex or named)

🖌️ Font support – select from several built‑in fonts (where available)

📁 Output to PNG – high‑quality 800×600 card

💾 Save & load – store card settings in JSON for reuse

🌐 HTML export – generate a web‑ready card (optional)

🧰 Supported Languages
Language	File	Dependencies
Python	card_creator.py	Pillow, requests (optional)
Go	card_creator.go	github.com/fogleman/gg
JavaScript (Node)	card_creator.js	canvas, sharp
Ruby	card_creator.rb	rmagick (or chunky_png)
PHP	card_creator.php	GD extension
Java	CardCreator.java	Java AWT, javax.imageio
C#	CardCreator.cs	SixLabors.ImageSharp
C++	card_creator.cpp	CImg (header‑only)
🚀 Quick Start
All implementations share a similar CLI interface:

bash
# Generate a simple card
<creator> -n "Alice" -a 30 -m "Happy Birthday!" -o card.png

# Choose a template (1=classic, 2=modern, 3=playful)
<creator> -n "Bob" -a 25 -m "Have a great day!" -t 2 -o modern_card.png

# Customize colors
<creator> -n "Charlie" -a 40 -m "Cheers!" --bg "#FFD700" --text "#8B0000"

# Add a photo
<creator> -n "Diana" -a 35 -m "You rock!" --photo portrait.jpg

# Use a background image
<creator> -n "Eve" -a 28 -m "Best wishes" --bg-image beach.jpg

# Export to HTML
<creator> -n "Frank" -a 50 -m "Happy 50th!" --html card.html
Arguments:

-n, --name – recipient's name (required)

-a, --age – age (optional, displayed if given)

-m, --message – custom message (required)

-t, --template – template number (1‑3, default: 1)

--bg – background color (hex or name, default: #FFFFFF)

--text – text color (hex or name, default: #000000)

--photo – path to a photo to embed (optional)

--bg-image – path to a background image (optional)

-o, --output – output PNG file (default: card.png)

--html – output HTML file (optional)

📸 Example Output (Text Description)
text
╔═══════════════════════════════════════════════════════════════╗
║                                                               ║
║                    🎉 Happy Birthday! 🎉                     ║
║                                                               ║
║                          Alice                                ║
║                          30                                   ║
║                                                               ║
║               "May your day be filled with joy!"             ║
║                                                               ║
║                    🎂  🎈  🎁                               ║
║                                                               ║
╚═══════════════════════════════════════════════════════════════╝
(Actual output is a beautifully rendered 800×600 PNG.)

📁 Repository Structure
text
.
├── README.md
├── python/
│   └── card_creator.py
├── go/
│   └── card_creator.go
├── javascript/
│   └── card_creator.js
├── ruby/
│   └── card_creator.rb
├── php/
│   └── card_creator.php
├── java/
│   └── CardCreator.java
├── csharp/
│   └── CardCreator.cs
└── cpp/
    └── card_creator.cpp
