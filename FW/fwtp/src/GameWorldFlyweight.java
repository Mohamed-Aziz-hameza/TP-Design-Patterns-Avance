import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class GameWorldFlyweight {
    private final List<Enemy> enemies = new ArrayList<>();
    private final EnemySpriteFactory factory = new EnemySpriteFactory();
    private final Random rnd = new Random();

    public void generateEnemies(int n) {
        Runtime rt = Runtime.getRuntime();
        System.gc();
        long before = rt.totalMemory() - rt.freeMemory();
        System.out.println("Used memory before: " + before + " bytes");

        String[] types = new String[]{"alien", "robot", "zombie"};
        for (int i = 0; i < n; i++) {
            String t = types[rnd.nextInt(types.length)];
            EnemySprite s = factory.getSprite(t);
            int x = rnd.nextInt(Math.max(1, 800 - s.getWidth()));
            int y = rnd.nextInt(Math.max(1, 600 - s.getHeight()));
            double vx = 1 + rnd.nextDouble() * 2;
            enemies.add(new Enemy(t, s, x, y, vx));
        }

        System.gc();
        long after = rt.totalMemory() - rt.freeMemory();
        System.out.println("Used memory after: " + after + " bytes");
        System.out.println("Sprites actually created: " + factory.getSpritesCreated());

        // naive estimate: if each enemy had its own image, images size * count
        System.out.println("(Estimate) naive image count would be: " + enemies.size());
    }

    public List<Enemy> getEnemies() { return enemies; }
    public EnemySpriteFactory getFactory() { return factory; }
}
