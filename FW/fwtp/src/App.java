import javax.swing.*;

public class App {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            GameWorldFlyweight world = new GameWorldFlyweight();
            world.generateEnemies(50);

            JFrame frame = new JFrame("Jeu Flyweight");
            frame.setSize(800,600);
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

            GamePanel panel = new GamePanel(world);
            frame.add(panel);
            frame.pack();
            frame.setVisible(true);

            Timer timer = new Timer(30, e-> {
                panel.updatePositions();
                panel.repaint();
            });
            timer.start();
        });
    }
}
