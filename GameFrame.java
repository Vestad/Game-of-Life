import javax.swing.JFrame;

public class GameFrame extends JFrame {

    GameFrame() {
        this.add(new GamePanel(600,600));
        this.setTitle("Game Of Life");
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // Kill application
        this.setResizable(false); // Prevent frame from being resized
        this.pack();
        this.setVisible(true);
        this.setLocationRelativeTo(null);
    }
}