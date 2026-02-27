import java.awt.*;
import java.awt.event.KeyEvent;
import java.util.HashSet;
import java.util.Set;

public class Player {
    private double x, y;
    private final int w = 32, h = 32;
    private int lives = 10;
    private final int maxLives = 10;
    private final EnemySprite sprite;
    private final Set<Integer> keys = new HashSet<>();
    private long lastShot = 0;
    private final long shotCooldown = 250; // ms

    public Player(double x, double y, EnemySprite sprite) {
        this.x = x; this.y = y; this.sprite = sprite;
    }

    public void press(int key) { keys.add(key); }
    public void release(int key) { keys.remove(key); }

    public void update(int panelW, int panelH) {
        double speed = 4.0;
        if (keys.contains(KeyEvent.VK_LEFT) || keys.contains(KeyEvent.VK_A)) x -= speed;
        if (keys.contains(KeyEvent.VK_RIGHT) || keys.contains(KeyEvent.VK_D)) x += speed;
        if (keys.contains(KeyEvent.VK_UP) || keys.contains(KeyEvent.VK_W)) y -= speed;
        if (keys.contains(KeyEvent.VK_DOWN) || keys.contains(KeyEvent.VK_S)) y += speed;
        if (x < 0) x = 0;
        if (y < 0) y = 0;
        if (x > panelW - w) x = panelW - w;
        if (y > panelH - h) y = panelH - h;
    }

    public boolean canShoot() { return System.currentTimeMillis() - lastShot >= shotCooldown; }
    public void markShot() { lastShot = System.currentTimeMillis(); }

    public void draw(Graphics2D g) { g.drawImage(sprite.getImage(), (int)x, (int)y, null); }
    public Rectangle getBounds() { return new Rectangle((int)x, (int)y, w, h); }
    public double getX() { return x; }
    public double getY() { return y; }

    public void loseLife() { if (lives>0) lives--; }
    public int getLives() { return lives; }
    public int getMaxLives() { return maxLives; }
    public void addLife() { if (lives < maxLives) lives++; }

    public void reset(double x, double y) {
        this.x = x; this.y = y; this.lives = maxLives; this.keys.clear();
        this.lastShot = 0;
    }
}
