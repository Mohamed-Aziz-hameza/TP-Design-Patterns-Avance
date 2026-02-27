import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class EnemySpriteFactory {
    private final Map<String, EnemySprite> cache = new HashMap<>();
    private int createdCount = 0;
    private static final int SPRITE_SIZE = 32;

    public EnemySprite getSprite(String type) {
        synchronized (cache) {
            if (cache.containsKey(type)) return cache.get(type);
            try {
                // Prefer classpath resource, else use the single user-provided folder
                String classpathName = "/resources/" + type + ".png";
                BufferedImage img = null;
                try (InputStream is = getClass().getResourceAsStream(classpathName)) {
                    if (is != null) {
                        img = ImageIO.read(is);
                        System.out.println("Loaded sprite from classpath: " + classpathName);
                    }
                }
                if (img == null) {
                    File tress = new File("C:\\Users\\user\\Documents\\DPA\\FW\\tressources");
                    File file = new File(tress, type + ".png");
                    if (file.exists()) {
                        img = ImageIO.read(file);
                        System.out.println("Loaded sprite from tressources: " + file.getAbsolutePath());
                    } else {
                        img = createPlaceholder(type);
                        System.out.println("Using placeholder for: " + type);
                    }
                }
                if (img != null) {
                    // scale to fixed sprite size
                    if (img.getWidth() != SPRITE_SIZE || img.getHeight() != SPRITE_SIZE) {
                        BufferedImage scaled = new BufferedImage(SPRITE_SIZE, SPRITE_SIZE, BufferedImage.TYPE_INT_ARGB);
                        Graphics2D g2 = scaled.createGraphics();
                        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
                        g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
                        g2.drawImage(img, 0, 0, SPRITE_SIZE, SPRITE_SIZE, null);
                        g2.dispose();
                        img = scaled;
                        System.out.println("Scaled image for '" + type + "' to " + SPRITE_SIZE + "x" + SPRITE_SIZE);
                    } else {
                        System.out.println("Image size for '" + type + "': " + img.getWidth() + "x" + img.getHeight());
                    }
                }
                EnemySprite s = new EnemySprite(type, img, SPRITE_SIZE, SPRITE_SIZE);
                cache.put(type, s);
                createdCount++;
                return s;
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private BufferedImage createPlaceholder(String type) {
        int size = 32;
        BufferedImage img = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = img.createGraphics();
        Color c = switch (type) {
            case "alien" -> new Color(0x4CAF50);
            case "robot" -> new Color(0x2196F3);
            case "zombie" -> new Color(0x9E9E9E);
            case "player" -> new Color(0xFF9800);
            case "bullet" -> new Color(0xF44336);
            default -> new Color(0x607D8B);
        };
        g.setColor(c);
        g.fillRect(0,0,size,size);
        g.setColor(Color.BLACK);
        g.drawString(type.substring(0, Math.min(3, type.length())), 4, 16);
        g.dispose();
        return img;
    }

    public int getSpritesCreated() { return createdCount; }
}
