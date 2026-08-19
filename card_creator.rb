# card_creator.rb
require 'optparse'
require 'rmagick'
include Magick

TEMPLATES = {
  1 => { bg: 'white', text: 'black' },
  2 => { bg: '#FFD700', text: '#8B0000' },
  3 => { bg: '#ADD8E6', text: '#FF4500' }
}

def hex_to_rgb(hex)
  hex = hex[1..-1] if hex.start_with?('#')
  hex.scan(/../).map { |c| c.to_i(16) }
end

def create_card(name, age, message, template, bg_hex, text_hex, photo_path, bg_image_path, output)
  width, height = 800, 600
  bg_color = bg_hex || TEMPLATES[template][:bg]
  text_color = text_hex || TEMPLATES[template][:text]

  # Create image
  if bg_image_path && File.exist?(bg_image_path)
    img = ImageList.new(bg_image_path).first
    img.resize!(width, height)
  else
    bg_rgb = bg_color.is_a?(String) ? hex_to_rgb(bg_color) : [255,255,255]
    img = Image.new(width, height) { self.background_color = "rgb(#{bg_rgb.join(',')})" }
  end

  draw = Draw.new
  text_rgb = text_color.is_a?(String) ? hex_to_rgb(text_color) : [0,0,0]
  draw.fill = "rgb(#{text_rgb.join(',')})"
  draw.gravity = NorthGravity

  # Title
  draw.font = '/usr/share/fonts/truetype/liberation/LiberationSans-Bold.ttf'
  draw.pointsize = 60
  draw.annotate(img, 0,0,0,20, '🎉 Happy Birthday! 🎉')

  # Name
  draw.pointsize = 70
  draw.annotate(img, 0,0,0,140, name)

  # Age
  if age
    draw.pointsize = 50
    draw.annotate(img, 0,0,0,220, age.to_s)
  end

  # Message wrapped
  draw.pointsize = 30
  draw.font = '/usr/share/fonts/truetype/liberation/LiberationSans-Italic.ttf'
  wrapped = message.scan(/.{1,30}/).join("\n")
  draw.annotate(img, 0,0,0,320, wrapped)

  # Emojis
  draw.pointsize = 40
  draw.font = '/usr/share/fonts/truetype/liberation/LiberationSans-Regular.ttf'
  draw.annotate(img, 0,0,0,460, '🎂  🎈  🎁')

  # Photo
  if photo_path && File.exist?(photo_path)
    photo = ImageList.new(photo_path).first
    photo.resize!(150, 150)
    img.composite!(photo, width - photo.columns - 20, 20, Magick::OverCompositeOp)
  end

  img.write(output)
  puts "Card saved to #{output}"
end

options = {}
OptionParser.new do |opts|
  opts.banner = "Usage: card_creator.rb [options]"
  opts.on('-n NAME', '--name NAME', 'Recipient name') { |v| options[:name] = v }
  opts.on('-a AGE', '--age AGE', Integer, 'Age') { |v| options[:age] = v }
  opts.on('-m MESSAGE', '--message MESSAGE', 'Custom message') { |v| options[:message] = v }
  opts.on('-t TEMPLATE', '--template TEMPLATE', Integer, 'Template (1-3)') { |v| options[:template] = v }
  opts.on('--bg COLOR', 'Background color') { |v| options[:bg] = v }
  opts.on('--text COLOR', 'Text color') { |v| options[:text] = v }
  opts.on('--photo PATH', 'Photo path') { |v| options[:photo] = v }
  opts.on('--bg-image PATH', 'Background image path') { |v| options[:bg_image] = v }
  opts.on('-o OUTPUT', '--output OUTPUT', 'Output PNG') { |v| options[:output] = v }
  opts.on('--html HTML', 'Output HTML file') { |v| options[:html] = v }
end.parse!

unless options[:name] && options[:message]
  warn "Error: -n and -m are required"
  exit 1
end
options[:template] ||= 1
options[:output] ||= 'card.png'

create_card(
  options[:name], options[:age], options[:message], options[:template],
  options[:bg], options[:text], options[:photo], options[:bg_image], options[:output]
)

if options[:html]
  html = <<~HTML
  <!DOCTYPE html>
  <html><head><title>Birthday Card</title>
  <style>body{font-family:sans-serif;text-align:center;background:#f0f0f0;}
  .card{background:#{options[:bg] || 'FFFFFF'};color:#{options[:text] || '000000'};
  border-radius:20px;padding:40px;max-width:600px;margin:50px auto;box-shadow:0 4px 8px rgba(0,0,0,0.2);}
  h1{font-size:3em;}.name{font-size:2.5em;}.age{font-size:2em;}.msg{font-size:1.5em;}</style>
  </head><body><div class="card">
  <h1>🎉 Happy Birthday! 🎉</h1>
  <div class="name">#{options[:name]}</div>
  #{options[:age] ? "<div class=\"age\">#{options[:age]}</div>" : ''}
  <div class="msg">#{options[:message]}</div>
  <div style="font-size:3em;">🎂 🎈 🎁</div>
  </div></body></html>
  HTML
  File.write(options[:html], html)
  puts "HTML card saved to #{options[:html]}"
end
