import java.awt.*;

public class Enemy {
    private final EnemySprite sprite;
    private double x, y;
    private double vx;
    private int hp;
    private final String type;

    public Enemy(String type, EnemySprite sprite, double x, double y, double vx) {
        this.type = type;
        this.sprite = sprite;
        this.x = x;
        this.y = y;
        this.vx = vx;
        this.hp = switch (type) {
            case "alien" -> 3;
            case "robot" -> 2;
            case "zombie" -> 1;
            default -> 1;
        };
    }

    public void updatePosition(int panelW, int panelH) {
        x += vx;
        if (x > panelW) x = -sprite.getWidth();
        if (x < -sprite.getWidth()) x = panelW;
        // keep inside vertically
        if (y < 0) y = 0;
        if (y > panelH - sprite.getHeight()) y = panelH - sprite.getHeight();
    }

    public void multiplySpeed(double factor) {
        this.vx *= factor;
    }

    public void draw(Graphics2D g) {
        g.drawImage(sprite.getImage(), (int)x, (int)y, null);
    }

    public Rectangle getBounds() {
        return new Rectangle((int)x, (int)y, sprite.getWidth(), sprite.getHeight());
    }

    public void hit() { hp--; }
    public boolean isDead() { return hp <= 0; }
    public int getHp() { return hp; }
    public String getType() { return type; }
}
