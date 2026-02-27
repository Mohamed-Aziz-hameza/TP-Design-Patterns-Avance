import java.awt.*;

public class Bullet {
    private double x, y;
    private final double vy;
    private final EnemySprite sprite;

    public Bullet(double x, double y, double vy, EnemySprite sprite) {
        this.x = x;
        this.y = y;
        this.vy = vy;
        this.sprite = sprite;
    }

    public void update() { y += vy; }
    public void draw(Graphics2D g) { g.drawImage(sprite.getImage(), (int)x, (int)y, null); }
    public Rectangle getBounds() { return new Rectangle((int)x, (int)y, sprite.getWidth(), sprite.getHeight()); }
    public boolean isOffscreen(int h) { return y < -sprite.getHeight() || y > h + sprite.getHeight(); }
}
