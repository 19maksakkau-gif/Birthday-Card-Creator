// card_creator.go
package main

import (
	"flag"
	"fmt"
	"image"
	"image/color"
	"image/draw"
	"image/png"
	"os"
	"strconv"
	"strings"

	"github.com/fogleman/gg"
)

var templates = map[int]struct{ bg, text color.RGBA }{
	1: {color.RGBA{255, 255, 255, 255}, color.RGBA{0, 0, 0, 255}},
	2: {color.RGBA{255, 215, 0, 255}, color.RGBA{139, 0, 0, 255}},
	3: {color.RGBA{173, 216, 230, 255}, color.RGBA{255, 69, 0, 255}},
}

func hexToColor(hex string) color.RGBA {
	if strings.HasPrefix(hex, "#") {
		hex = hex[1:]
	}
	if len(hex) == 6 {
		r, _ := strconv.ParseUint(hex[0:2], 16, 8)
		g, _ := strconv.ParseUint(hex[2:4], 16, 8)
		b, _ := strconv.ParseUint(hex[4:6], 16, 8)
		return color.RGBA{uint8(r), uint8(g), uint8(b), 255}
	}
	return color.RGBA{0, 0, 0, 255}
}

func createCard(name string, age int, message string, template int, bgHex, textHex string, photoPath, bgImagePath, output string) error {
	dc := gg.NewContext(800, 600)

	// Background
	var bg color.RGBA
	if bgHex != "" {
		bg = hexToColor(bgHex)
	} else {
		bg = templates[template].bg
	}
	dc.SetColor(bg)
	dc.Clear()

	// If background image provided, load and draw
	if bgImagePath != "" {
		img, err := gg.LoadImage(bgImagePath)
		if err == nil {
			dc.DrawImage(img, 0, 0)
		}
	}

	// Text color
	var textCol color.RGBA
	if textHex != "" {
		textCol = hexToColor(textHex)
	} else {
		textCol = templates[template].text
	}
	dc.SetColor(textCol)

	// Draw title
	if err := dc.LoadFontFace("/usr/share/fonts/truetype/liberation/LiberationSans-Bold.ttf", 60); err != nil {
		dc.LoadFontFace("", 60)
	}
	dc.DrawStringAnchored("🎉 Happy Birthday! 🎉", 400, 80, 0.5, 0.5)

	// Name
	if err := dc.LoadFontFace("/usr/share/fonts/truetype/liberation/LiberationSans-Bold.ttf", 70); err != nil {
		dc.LoadFontFace("", 70)
	}
	dc.DrawStringAnchored(name, 400, 200, 0.5, 0.5)

	// Age
	if age > 0 {
		if err := dc.LoadFontFace("/usr/share/fonts/truetype/liberation/LiberationSans-Regular.ttf", 50); err != nil {
			dc.LoadFontFace("", 50)
		}
		dc.DrawStringAnchored(strconv.Itoa(age), 400, 280, 0.5, 0.5)
	}

	// Message - wrap manually (simple)
	if err := dc.LoadFontFace("/usr/share/fonts/truetype/liberation/LiberationSans-Italic.ttf", 30); err != nil {
		dc.LoadFontFace("", 30)
	}
	// Simple word wrap: split into chunks of ~20 chars
	words := strings.Fields(message)
	var lines []string
	line := ""
	for _, w := range words {
		if len(line)+len(w)+1 <= 30 {
			if line == "" {
				line = w
			} else {
				line += " " + w
			}
		} else {
			lines = append(lines, line)
			line = w
		}
	}
	if line != "" {
		lines = append(lines, line)
	}
	y := 380
	for _, l := range lines {
		dc.DrawStringAnchored(l, 400, float64(y), 0.5, 0.5)
		y += 40
	}

	// Emojis
	if err := dc.LoadFontFace("/usr/share/fonts/truetype/liberation/LiberationSans-Regular.ttf", 40); err != nil {
		dc.LoadFontFace("", 40)
	}
	dc.DrawStringAnchored("🎂  🎈  🎁", 400, 520, 0.5, 0.5)

	// Photo embedding
	if photoPath != "" {
		img, err := gg.LoadImage(photoPath)
		if err == nil {
			// resize to 150x150
			src := gg.NewContextForImage(img)
			srcW, srcH := img.Bounds().Dx(), img.Bounds().Dy()
			newW, newH := 150, 150
			dc.DrawImage(src.Image(), 800-newW-20, 20)
		}
	}

	return dc.SavePNG(output)
}

func main() {
	name := flag.String("n", "", "Recipient's name")
	age := flag.Int("a", 0, "Age")
	message := flag.String("m", "", "Custom message")
	template := flag.Int("t", 1, "Template (1-3)")
	bgHex := flag.String("bg", "", "Background color (hex)")
	textHex := flag.String("text", "", "Text color (hex)")
	photo := flag.String("photo", "", "Photo path")
	bgImage := flag.String("bg-image", "", "Background image path")
	output := flag.String("o", "card.png", "Output file")
	html := flag.String("html", "", "HTML output")
	flag.Parse()

	if *name == "" || *message == "" {
		fmt.Println("Error: -n and -m are required")
		os.Exit(1)
	}
	if *template < 1 || *template > 3 {
		fmt.Println("Template must be 1-3")
		os.Exit(1)
	}

	err := createCard(*name, *age, *message, *template, *bgHex, *textHex, *photo, *bgImage, *output)
	if err != nil {
		fmt.Println("Error:", err)
		os.Exit(1)
	}
	fmt.Printf("Card saved to %s\n", *output)

	if *html != "" {
		htmlContent := fmt.Sprintf(`<!DOCTYPE html>
<html><head><title>Birthday Card</title>
<style>body{font-family:sans-serif;text-align:center;background:#f0f0f0;}
.card{background:%s;color:%s;border-radius:20px;padding:40px;max-width:600px;margin:50px auto;box-shadow:0 4px 8px rgba(0,0,0,0.2);}
h1{font-size:3em;}.name{font-size:2.5em;}.age{font-size:2em;}.msg{font-size:1.5em;}</style>
</head><body><div class="card">
<h1>🎉 Happy Birthday! 🎉</h1>
<div class="name">%s</div>
%s
<div class="msg">%s</div>
<div style="font-size:3em;">🎂 🎈 🎁</div>
</div></body></html>`, *bgHex, *textHex, *name, func() string { if *age > 0 { return fmt.Sprintf(`<div class="age">%d</div>`, *age) } else { return "" } }(), *message)
		os.WriteFile(*html, []byte(htmlContent), 0644)
		fmt.Printf("HTML card saved to %s\n", *html)
	}
}
