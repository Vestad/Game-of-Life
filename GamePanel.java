import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.Random;

public class GamePanel extends JPanel implements ActionListener {
    static final int UNIT_SIZE = 20;
    final int SCREEN_WIDTH;
    final int SCREEN_HEIGHT;
    final int GAME_UNITS;
    static final int DELAY = 100; // Higher number -> slower game
    final int[] ROWS_ON_SCREEN;
    final int[] COLUMNS_ON_SCREEN;
    final int NR_OF_ROWS;
    final int NR_OF_COLS;
    int currentPopulationSize;
    int generation = 0;
    Cell[] cells;
    boolean running = false;
    Random random = new Random();
    Timer timer;

    GamePanel(int SCREEN_WIDTH, int SCREEN_HEIGHT) {
        this.SCREEN_WIDTH = SCREEN_WIDTH;
        this.SCREEN_HEIGHT = SCREEN_HEIGHT;
        this.ROWS_ON_SCREEN = new int[SCREEN_HEIGHT/UNIT_SIZE];
        this.COLUMNS_ON_SCREEN = new int[SCREEN_WIDTH/UNIT_SIZE];
        this.NR_OF_ROWS = ROWS_ON_SCREEN.length;
        this.NR_OF_COLS = COLUMNS_ON_SCREEN.length;
        this.GAME_UNITS = (NR_OF_ROWS * NR_OF_COLS);
        this.cells = new Cell[GAME_UNITS];

        this.setPreferredSize(new Dimension(SCREEN_WIDTH, SCREEN_HEIGHT));
        this.setBackground(Color.lightGray);
        this.setFocusable(true);
        this.addKeyListener(new MyKeyAdapter());
        startGame();
    }

    public void startGame() {
        generateSeed(); // The initial pattern of the system
        
        // The first generation is created by applying the rules simultaneously to every cell in the seed, live or dead
        findNextGeneration(); // Create first generation

        timer = new Timer(DELAY, this);
        timer.start();
        running = true;
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        draw(g);
    }

    public void draw(Graphics g) {

        if (running) {
            // Paint living cells
            for (int x = 0; x<(NR_OF_ROWS); x++) {
                for (int y = 0; y<(NR_OF_COLS); y++) {
                    int n = x* NR_OF_COLS + y;
                    if (cells[n].isAlive()) {
                        g.setColor(Color.yellow);
                        g.fillRect(cells[n].getX(), cells[n].getY(), UNIT_SIZE, UNIT_SIZE);
                    }
                }
            }

            // Draw grid
            for (int i = 0; i < (NR_OF_ROWS); i++) {
                g.setColor(Color.gray);
                g.drawLine(i * UNIT_SIZE, 0, i * UNIT_SIZE, SCREEN_HEIGHT);
                g.drawLine(0, i * UNIT_SIZE, SCREEN_WIDTH, i * UNIT_SIZE);
            }

            g.setColor(Color.black);
            g.setFont(new Font("VT323", Font.BOLD, 40));
            FontMetrics metrics = getFontMetrics(g.getFont());
            g.drawString("Number of Cells: "+ currentPopulationSize, (SCREEN_WIDTH - metrics.stringWidth("Number of Cells: "+ currentPopulationSize))/2, g.getFont().getSize());
            g.drawString("Generation: "+generation, (SCREEN_WIDTH - metrics.stringWidth("Generation: "+generation))/2, (SCREEN_HEIGHT - g.getFont().getSize()) );
        }
        else {
            extinction(g);
        }
    }

    public void generateSeed(){
        for (int x = 0; x<(NR_OF_ROWS); x++) {
            for (int y = 0; y<(NR_OF_COLS); y++) {
                int n = x* NR_OF_COLS + y;
                cells[n] = new Cell();
                cells[n].setCoordinates(x*UNIT_SIZE,y*UNIT_SIZE);
            }
        }
        int initialSeed = random.nextInt(GAME_UNITS);
        for (int i = 0; i < initialSeed; i++) {
            int random_x = random.nextInt(NR_OF_ROWS);
            int random_y = random.nextInt(NR_OF_COLS);
            int random_n = random_x* NR_OF_COLS + random_y;
            if (!cells[random_n].isAlive()) {
                cells[random_n].setAlive();
                currentPopulationSize++;
            }
        }
        findCellNeighbours();
    }

    public void findNextGeneration() {
        if (currentPopulationSize > 1) {
            for (Cell cell : cells) {
                // Evaluate next state based on previous state of the cell and it's neighbours
                applyRules(cell);
            }
        } else {
            running = false;
        }
        generation++;
        findCellNeighbours(); 
    }

    private void applyRules(Cell cell) {
        if (cell.isAlive()) {
            // Living cells with less than two neighbours die due to underpopulation
            // Living cells with more than three neighbours die due to overpopulation
            if ( ( (cell.getNeighbours() < 2) || (cell.getNeighbours() > 3) ) ) {
                cell.kill();
                currentPopulationSize--;
            } // Live cells with 2-3 neighbours stay alive in the next generation.
        } else {
            // Dead cells with exactly three neighbours becomes alive, as by reproduction.
            if (cell.getNeighbours() == 3) {
                cell.setAlive();
                currentPopulationSize++;
            } // All other dead cells stay dead.
        }
    }

    public void findCellNeighbours() {
        int MAX_X = NR_OF_ROWS-1;
        int MAX_Y = NR_OF_COLS-1;
        for (int x = 0; x < MAX_X; x++) {
            for (int y = 0; y < MAX_Y-1; y++) {
                int n = x*NR_OF_ROWS + y;
                cells[n].resetNeighbours();
                if ((y>0) && (cells[x*NR_OF_ROWS + y-1].isAlive())) cells[n].addNeighbour(); // North
                if ((y>0) && (cells[(x+1)*NR_OF_ROWS + y-1].isAlive())) cells[n].addNeighbour(); // Northeast
                if ((x < MAX_X-1) && (cells[(x+1)*NR_OF_ROWS +y].isAlive())) cells[n].addNeighbour(); // East
                if (((x < MAX_X-1) && (y < MAX_Y-1)) && (cells[(x+1)*NR_OF_ROWS + y+1].isAlive())) cells[n].addNeighbour(); // Southeast
                if ((y < MAX_X-1) && (cells[x*NR_OF_ROWS +y+1].isAlive())) cells[n].addNeighbour(); // South
                if (((x > 0) && (y < MAX_Y-1)) && (cells[(x-1)*NR_OF_ROWS + y+1].isAlive())) cells[n].addNeighbour(); // Southwest
                if (((x > 0)) && (cells[(x-1)*NR_OF_ROWS + y].isAlive())) cells[n].addNeighbour(); // West
                if (((y>0) && (x>0)) && (cells[(x-1)*NR_OF_ROWS + y-1].isAlive())) cells[n].addNeighbour(); // Northwest
            }
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        repaint();
    }

    public class MyKeyAdapter extends KeyAdapter {
        @Override
        public void keyPressed(KeyEvent e) {
            if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
                findNextGeneration();
            }
        }
    }

    public void extinction(Graphics g) {
        g.setColor(Color.red);
        g.setFont(new Font("VT323", Font.BOLD, 75));
        FontMetrics metrics1 = getFontMetrics(g.getFont());
        g.drawString("Colony went extinct", (SCREEN_WIDTH - metrics1.stringWidth("Colony went extinct"))/2, SCREEN_HEIGHT/2);
    }
} 