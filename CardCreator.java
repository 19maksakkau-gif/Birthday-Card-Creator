// CardCreator.java
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;
import java.io.IOException;
import java.util.*;
import java.util.List;

public class CardCreator {
    private static final Map<Integer, Color[]> TEMPLATES = new HashMap<>();
    static {
        TEMPLATES.put(1, new Color[]{Color.WHITE, Color.BLACK});
        TEMPLATES.put(2, new Color[]{new Color(255, 215, 0), new Color(139, 0, 0)});
        TEMPLATES.put(3, new Color[]{new Color(173, 216, 230), new Color(255, 69, 0)});
    }

    public static Color hexToColor(String hex) {
        if (hex.startsWith("#")) hex = hex.substring(1);
        return new Color(Integer.parseInt(hex.substring(0,2), 16),
                         Integer.parseInt(hex.substring(2,4), 16),
                         Integer.parseInt(hex.substring(4,6), 16));
    }

    public static void createCard(String name, Integer age, String message, int template,
                                  String bgHex, String textHex, String photoPath,
                                  String bgImagePath, String output) throws Exception {
        int width = 800, height = 600;
        BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = img.createGraphics();

        // Background
        Color bgColor = bgHex != null ? hexToColor(bgHex) : TEMPLATES.get(template)[0];
        if (bgImagePath != null && new File(bgImagePath).exists()) {
            BufferedImage bgImg = ImageIO.read(new File(bgImagePath));
            g.drawImage(bgImg, 0, 0, width, height, null);
        } else {
            g.setColor(bgColor);
            g.fillRect(0, 0, width, height);
        }

        // Text color
        Color textColor = textHex != null ? hexToColor(textHex) : TEMPLATES.get(template)[1];
        g.setColor(textColor);
        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g.setFont(new Font("Liberation Sans", Font.BOLD, 60));
        FontMetrics fm = g.getFontMetrics();
        String title = "🎉 Happy Birthday! 🎉";
        g.drawString(title, (width - fm.stringWidth(title))/2, 80);

        g.setFont(new Font("Liberation Sans", Font.BOLD, 70));
        fm = g.getFontMetrics();
        g.drawString(name, (width - fm.stringWidth(name))/2, 200);

        if (age != null && age > 0) {
            g.setFont(new Font("Liberation Sans", Font.PLAIN, 50));
            fm = g.getFontMetrics();
            String ageStr = age.toString();
            g.drawString(ageStr, (width - fm.stringWidth(ageStr))/2, 280);
        }

        g.setFont(new Font("Liberation Sans", Font.ITALIC, 30));
        fm = g.getFontMetrics();
        // Wrap message
        String[] words = message.split(" ");
        StringBuilder line = new StringBuilder();
        List<String> lines = new ArrayList<>();
        for (String w : words) {
            if (line.length() + w.length() + 1 <= 30) {
                if (line.length() > 0) line.append(" ");
                line.append(w);
            } else {
                lines.add(line.toString());
                line = new StringBuilder(w);
            }
        }
        if (line.length() > 0) lines.add(line.toString());
        int y = 380;
        for (String l : lines) {
            g.drawString(l, (width - fm.stringWidth(l))/2, y);
            y += 40;
        }

        g.setFont(new Font("Liberation Sans", Font.PLAIN, 40));
        fm = g.getFontMetrics();
        String emoji = "🎂  🎈  🎁";
        g.drawString(emoji, (width - fm.stringWidth(emoji))/2, 520);

        // Photo
        if (photoPath != null && new File(photoPath).exists()) {
            BufferedImage photo = ImageIO.read(new File(photoPath));
            int size = 150;
            g.drawImage(photo, width - size - 20, 20, size, size, null);
        }

        g.dispose();
        ImageIO.write(img, "png", new File(output));
        System.out.println("Card saved to " + output);
    }

    public static void main(String[] args) throws Exception {
        Map<String, String> params = new HashMap<>();
        for (int i = 0; i < args.length; i++) {
            if (args[i].startsWith("-")) {
                String key = args[i].replaceFirst("^-+", "");
                if (i+1 < args.length && !args[i+1].startsWith("-")) {
                    params.put(key, args[++i]);
                } else {
                    params.put(key, "");
                }
            }
        }
        if (!params.containsKey("n")) {
            System.err.println("Error: -n name required");
            System.exit(1);
        }
        if (!params.containsKey("m")) {
            System.err.println("Error: -m message required");
            System.exit(1);
        }
        String name = params.get("n");
        String message = params.get("m");
        Integer age = params.containsKey("a") ? Integer.parseInt(params.get("a")) : null;
        int template = params.containsKey("t") ? Integer.parseInt(params.get("t")) : 1;
        if (template < 1 || template > 3) {
            System.err.println("Template must be 1-3");
            System.exit(1);
        }
        String bg = params.get("bg");
        String text = params.get("text");
        String photo = params.get("photo");
        String bgImage = params.get("bg-image");
        String output = params.getOrDefault("o", "card.png");

        createCard(name, age, message, template, bg, text, photo, bgImage, output);

        if (params.containsKey("html")) {
            String htmlFile = params.get("html");
            String bgColor = bg != null ? bg : "#FFFFFF";
            String textColor = text != null ? text : "#000000";
            String ageHtml = age != null ? "<div class=\"age\">" + age + "</div>" : "";
            String html = String.format("<!DOCTYPE html><html><head><title>Birthday Card</title>" +
                    "<style>body{font-family:sans-serif;text-align:center;background:#f0f0f0;}" +
                    ".card{background:%s;color:%s;border-radius:20px;padding:40px;max-width:600px;margin:50px auto;box-shadow:0 4px 8px rgba(0,0,0,0.2);}" +
                    "h1{font-size:3em;}.name{font-size:2.5em;}.age{font-size:2em;}.msg{font-size:1.5em;}</style>" +
                    "</head><body><div class=\"card\"><h1>🎉 Happy Birthday! 🎉</h1>" +
                    "<div class=\"name\">%s</div>%s<div class=\"msg\">%s</div><div style=\"font-size:3em;\">🎂 🎈 🎁</div>" +
                    "</div></body></html>", bgColor, textColor, name, ageHtml, message);
            java.nio.file.Files.write(java.nio.file.Paths.get(htmlFile), html.getBytes());
            System.out.println("HTML card saved to " + htmlFile);
        }
    }
}
