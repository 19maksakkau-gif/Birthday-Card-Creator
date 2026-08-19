# card_creator.php
<?php
$templates = [
    1 => ['bg' => '#FFFFFF', 'text' => '#000000'],
    2 => ['bg' => '#FFD700', 'text' => '#8B0000'],
    3 => ['bg' => '#ADD8E6', 'text' => '#FF4500']
];

function hexToRgb($hex) {
    $hex = str_replace('#', '', $hex);
    if (strlen($hex) == 6) {
        return [hexdec($hex[0].$hex[1]), hexdec($hex[2].$hex[3]), hexdec($hex[4].$hex[5])];
    }
    return [0,0,0];
}

function createCard($name, $age, $message, $template, $bgHex, $textHex, $photoPath, $bgImagePath, $output) {
    $width = 800; $height = 600;
    $img = imagecreatetruecolor($width, $height);

    // Background
    if ($bgImagePath && file_exists($bgImagePath)) {
        $bgImg = imagecreatefromstring(file_get_contents($bgImagePath));
        if ($bgImg) {
            imagecopyresampled($img, $bgImg, 0,0,0,0, $width, $height, imagesx($bgImg), imagesy($bgImg));
            imagedestroy($bgImg);
        } else {
            $bg = $templates[$template]['bg'];
        }
    } else {
        $bg = $bgHex ?: $templates[$template]['bg'];
    }
    if (!isset($bgImg) || !$bgImg) {
        list($r,$g,$b) = hexToRgb($bg);
        $bgColor = imagecolorallocate($img, $r, $g, $b);
        imagefilledrectangle($img, 0, 0, $width, $height, $bgColor);
    }

    // Text color
    $text = $textHex ?: $templates[$template]['text'];
    list($r,$g,$b) = hexToRgb($text);
    $textColor = imagecolorallocate($img, $r, $g, $b);

    // Font paths (using built-in GD fonts for simplicity, but we can use TTF if available)
    $font = '/usr/share/fonts/truetype/liberation/LiberationSans-Bold.ttf';
    $fontReg = '/usr/share/fonts/truetype/liberation/LiberationSans-Regular.ttf';
    $fontItalic = '/usr/share/fonts/truetype/liberation/LiberationSans-Italic.ttf';

    // Title
    if (file_exists($font)) {
        imagettftext($img, 60, 0, 200, 80, $textColor, $font, '🎉 Happy Birthday! 🎉');
        imagettftext($img, 70, 0, 200, 200, $textColor, $font, $name);
        if ($age) imagettftext($img, 50, 0, 200, 280, $textColor, $fontReg, $age);
        // Message wrap
        $wrapped = wordwrap($message, 30, "\n");
        imagettftext($img, 30, 0, 200, 380, $textColor, $fontItalic, $wrapped);
        imagettftext($img, 40, 0, 200, 520, $textColor, $fontReg, '🎂  🎈  🎁');
    } else {
        // Fallback to built-in
        imagestring($img, 5, 200, 50, 'Happy Birthday!', $textColor);
        imagestring($img, 5, 200, 100, $name, $textColor);
        if ($age) imagestring($img, 4, 200, 140, $age, $textColor);
        imagestring($img, 3, 200, 200, $message, $textColor);
    }

    // Photo
    if ($photoPath && file_exists($photoPath)) {
        $photo = imagecreatefromstring(file_get_contents($photoPath));
        if ($photo) {
            $size = 150;
            imagecopyresampled($img, $photo, $width - $size - 20, 20, 0,0, $size, $size, imagesx($photo), imagesy($photo));
            imagedestroy($photo);
        }
    }

    imagepng($img, $output);
    imagedestroy($img);
    echo "Card saved to $output\n";
}

$opts = getopt("n:a:m:t:o:", ["name:", "age:", "message:", "template:", "bg:", "text:", "photo:", "bg-image:", "output:", "html:"]);
$name = $opts['n'] ?? $opts['name'] ?? null;
$age = isset($opts['a']) ? (int)$opts['a'] : (isset($opts['age']) ? (int)$opts['age'] : null);
$message = $opts['m'] ?? $opts['message'] ?? null;
$template = isset($opts['t']) ? (int)$opts['t'] : (isset($opts['template']) ? (int)$opts['template'] : 1);
$bg = $opts['bg'] ?? null;
$text = $opts['text'] ?? null;
$photo = $opts['photo'] ?? null;
$bgImage = $opts['bg-image'] ?? null;
$output = $opts['o'] ?? $opts['output'] ?? 'card.png';
$html = $opts['html'] ?? null;

if (!$name || !$message) {
    fwrite(STDERR, "Error: -n and -m are required\n");
    exit(1);
}
if ($template < 1 || $template > 3) {
    fwrite(STDERR, "Template must be 1-3\n");
    exit(1);
}

createCard($name, $age, $message, $template, $bg, $text, $photo, $bgImage, $output);

if ($html) {
    $htmlContent = <<<HTML
<!DOCTYPE html>
<html><head><title>Birthday Card</title>
<style>body{font-family:sans-serif;text-align:center;background:#f0f0f0;}
.card{background:{$bg};color:{$text};border-radius:20px;padding:40px;max-width:600px;margin:50px auto;box-shadow:0 4px 8px rgba(0,0,0,0.2);}
h1{font-size:3em;}.name{font-size:2.5em;}.age{font-size:2em;}.msg{font-size:1.5em;}</style>
</head><body><div class="card">
<h1>🎉 Happy Birthday! 🎉</h1>
<div class="name">{$name}</div>
HTML;
    if ($age) $htmlContent .= "<div class=\"age\">$age</div>";
    $htmlContent .= "<div class=\"msg\">$message</div>";
    $htmlContent .= '<div style="font-size:3em;">🎂 🎈 🎁</div></div></body></html>';
    file_put_contents($html, $htmlContent);
    echo "HTML card saved to $html\n";
}
?>
