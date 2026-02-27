import java.awt.Image;

public class EnemySprite {
    private final String type;
    private final Image image;
    private final int width;
    private final int height;

    public EnemySprite(String type, Image image, int width, int height) {
        this.type = type;
        this.image = image;
        this.width = width;
        this.height = height;
    }

    public String getType() { return type; }
    public Image getImage() { return image; }
    public int getWidth() { return width; }
    public int getHeight() { return height; }
}
