import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class GamePanel extends JPanel {
    private final GameWorldFlyweight world;
    private final List<Enemy> enemies;
    private final List<Bullet> bullets = new ArrayList<>();
    private final Player player;
    private final EnemySpriteFactory factory;
    private int score = 0;
    private int highScore = 0;
    private long startTime;
    private int spawnInterval = 2000; // initial spawn interval ms
    private int panelW = 800, panelH = 600;
    private boolean gameOver = false;
    private double speedMultiplier = 1.0;
    private int lastSpeedIncreaseTick = 0; // how many 20s intervals applied
    private int spawnBatch = 1;
    private double spawnProbMultiplier = 1.0;
    private int bulletsPerShot = 1;

    public GamePanel(GameWorldFlyweight world) {
        this.world = world;
        this.enemies = world.getEnemies();
        this.factory = world.getFactory();
        EnemySprite playerSprite = factory.getSprite("player");
        this.player = new Player(panelW/2 - 16, panelH - 60, playerSprite);
        setPreferredSize(new Dimension(panelW, panelH));
        setFocusable(true);
        setupInput();
        startTime = System.currentTimeMillis();
        loadHighScore();
    }

    private void setupInput() {
        addKeyListener(new KeyAdapter() {
            @Override public void keyPressed(KeyEvent e) {
                if (gameOver) {
                    if (e.getKeyCode() == KeyEvent.VK_R) {
                        restartGame();
                    }
                    return;
                }
                player.press(e.getKeyCode());
                if (e.getKeyCode() == KeyEvent.VK_SPACE) {
                    if (player.canShoot()) {
                        EnemySprite bulletSprite = factory.getSprite("bullet");
                        // fire multiple bullets per shot in a wider horizontal spread so each is visible
                        int count = Math.max(1, bulletsPerShot);
                        // base spread scales with count but capped to avoid runaway distances
                        int spread = Math.min(60, 20 + count * 6);
                        double center = (count - 1) / 2.0;
                        for (int i = 0; i < count; i++) {
                            double offset = (i - center) * spread;
                            bullets.add(new Bullet(player.getX() + 16 + offset, player.getY() - 8, -6, bulletSprite));
                        }
                        player.markShot();
                    }
                }
            }

            @Override public void keyReleased(KeyEvent e) { player.release(e.getKeyCode()); }
        });
    }

    public void updatePositions() {
        if (gameOver) return;

        // speed increase every 20 seconds: multiply by 1.5
        long now = System.currentTimeMillis();
        int ticks = (int)((now - startTime) / 20000L);
        if (ticks > lastSpeedIncreaseTick) {
            int dif = ticks - lastSpeedIncreaseTick;
            for (int i=0;i<dif;i++) {
                speedMultiplier *= 1.5;
                spawnBatch *= 3; // increase number of enemies generated per spawn
                spawnProbMultiplier *= 3.0; // increase spawn frequency
                bulletsPerShot *= 5; // increase bullets per shot
            }
            // apply to existing enemies
            for (Enemy en : enemies) en.multiplySpeed(Math.pow(1.5, dif));
            lastSpeedIncreaseTick = ticks;
            System.out.println("Speed multiplier increased to: " + speedMultiplier + ", spawnBatch=" + spawnBatch + ", bulletsPerShot=" + bulletsPerShot);
        }

        player.update(panelW, panelH);

        // spawn logic: faster each minute
        long elapsedMin = (System.currentTimeMillis() - startTime) / 60000;
        int effectiveInterval = Math.max(200, spawnInterval - (int)(elapsedMin * 200));

        // occasionally add enemy based on interval
        if (Math.random() < spawnProbMultiplier * 30.0/1000.0 * (30.0/effectiveInterval)) {
            spawnRandomEnemy(spawnBatch);
        }

        for (Enemy e : new ArrayList<>(enemies)) e.updatePosition(panelW, panelH);

        Iterator<Bullet> bi = bullets.iterator();
        while (bi.hasNext()) {
            Bullet b = bi.next();
            b.update();
            if (b.isOffscreen(panelH)) { bi.remove(); continue; }
            Iterator<Enemy> ei = enemies.iterator();
            while (ei.hasNext()) {
                Enemy enemy = ei.next();
                if (b.getBounds().intersects(enemy.getBounds())) {
                    enemy.hit();
                    bi.remove();
                    if (enemy.isDead()) {
                        score += switch (enemy.getType()) {
                            case "alien" -> 3;
                            case "robot" -> 2;
                            default -> 1;
                        };
                        ei.remove();
                    }
                    break;
                }
            }
        }

        // if enemy intersects player, player loses life and enemy removed
        Iterator<Enemy> eiter = enemies.iterator();
        while (eiter.hasNext()) {
            Enemy en = eiter.next();
            if (en.getBounds().intersects(player.getBounds())) {
                player.loseLife();
                eiter.remove();
            }
        }

        if (score > highScore) { highScore = score; saveHighScore(); }

        if (player.getLives() <= 0) {
            gameOver = true;
            if (score > highScore) { highScore = score; saveHighScore(); }
            System.out.println("Game Over. Final score: " + score + ", High: " + highScore);
        }
    }

    private void spawnRandomEnemy(int count) {
        String[] types = new String[]{"alien","robot","zombie"};
        for (int k=0;k<count;k++) {
            String t = types[(int)(Math.random()*types.length)];
            EnemySprite s = factory.getSprite(t);
            int x = -s.getWidth() - (int)(Math.random()*100);
            int y = (int)(Math.random() * (panelH - s.getHeight()));
            double vx = (1 + Math.random()*2) * speedMultiplier;
            enemies.add(new Enemy(t, s, x, y, vx));
        }
    }

    @Override protected void paintComponent(Graphics g0) {
        super.paintComponent(g0);
        Graphics2D g = (Graphics2D) g0;
        g.setColor(Color.BLACK);
        g.fillRect(0,0,panelW,panelH);

        for (Enemy e : enemies) e.draw(g);
        for (Bullet b : bullets) b.draw(g);
        player.draw(g);

        g.setColor(Color.WHITE);
        g.drawString("Score: " + score + "  High: " + highScore + "  Lives: " + player.getLives(), 10, 16);

        if (gameOver) {
            Composite old = g.getComposite();
            g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.7f));
            g.setColor(Color.BLACK);
            g.fillRect(0,0,panelW,panelH);
            g.setComposite(old);

            g.setColor(Color.WHITE);
            g.setFont(g.getFont().deriveFont(36f));
            String msg = "GAME OVER";
            FontMetrics fm = g.getFontMetrics();
            int w = fm.stringWidth(msg);
            g.drawString(msg, (panelW-w)/2, panelH/2 - 40);

            g.setFont(g.getFont().deriveFont(20f));
            String scoreMsg = "Score: " + score;
            String highMsg = "High Score: " + highScore;
            g.drawString(scoreMsg, (panelW - g.getFontMetrics().stringWidth(scoreMsg))/2, panelH/2);
            g.drawString(highMsg, (panelW - g.getFontMetrics().stringWidth(highMsg))/2, panelH/2 + 30);

            String restart = "Press R to restart";
            g.drawString(restart, (panelW - g.getFontMetrics().stringWidth(restart))/2, panelH/2 + 80);
        }
    }

    private void loadHighScore() {
        try {
            java.io.File f = new java.io.File("highscore.dat");
            if (f.exists()) {
                try (java.io.DataInputStream in = new java.io.DataInputStream(new java.io.FileInputStream(f))) {
                    highScore = in.readInt();
                }
            }
        } catch (Exception ex) { /* ignore */ }
    }

    private void saveHighScore() {
        try {
            try (java.io.DataOutputStream out = new java.io.DataOutputStream(new java.io.FileOutputStream("highscore.dat"))) {
                out.writeInt(highScore);
            }
        } catch (Exception ex) { /* ignore */ }
    }

    private void restartGame() {
        score = 0;
        bullets.clear();
        enemies.clear();
        speedMultiplier = 1.0;
        lastSpeedIncreaseTick = 0;
        startTime = System.currentTimeMillis();
        world.generateEnemies(50);
        player.reset(panelW/2 - 16, panelH - 60);
        gameOver = false;
    }

    public int getScore() { return score; }
    public int getHighScore() { return highScore; }
}
