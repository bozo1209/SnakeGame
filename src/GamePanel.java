import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.Random;

public class GamePanel extends JPanel implements ActionListener {

    static final int SCREEN_WIDTH = 600;
    static final int SCREEN_HEIGHT = 600;
    static final int UNIT_SIZE = 25;
    static final int GAME_UNITS = (SCREEN_WIDTH * SCREEN_HEIGHT)/UNIT_SIZE;
    static final int DELAY = 75;
    final int x[] = new int[GAME_UNITS];
    final int y[] = new int[GAME_UNITS];
    int bodyParts = 6;
    int applesEaten = 0;
    int appleX;
    int appleY;
    char direction = 'R';
    boolean running = false;
    static boolean pause = false;
    boolean anti = false;
    Timer timer;
    MyTimer myTimer;
    Random random;


    GamePanel(){
        random = new Random();
        this.setPreferredSize(new Dimension(SCREEN_WIDTH, SCREEN_HEIGHT));
        this.setBackground(Color.BLACK);
        this.setFocusable(true);
        this.addKeyListener(new MyKeyAdapter());
        startGame();
    }

    public void startGame(){
        newApple();
        running = true;
//        timer = new Timer(DELAY, this);
//        timer.start();
        myTimer = new MyTimer(DELAY, this);
        myTimer.start();
    }

    public void paintComponent(Graphics g){
        super.paintComponent(g);
        draw(g);
    }

    public void draw(Graphics g){
        if (running){
            for (int i = 0; i < SCREEN_HEIGHT / UNIT_SIZE; i++) {
                g.drawLine(i * UNIT_SIZE, 0, i * UNIT_SIZE, SCREEN_HEIGHT);
                g.drawLine(0, i * UNIT_SIZE, SCREEN_WIDTH, i * UNIT_SIZE);
            }

            Graphics2D g2d = (Graphics2D) g;
            if (anti){
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            }


            g2d.setColor(Color.RED);
            g2d.fillOval(appleX, appleY, UNIT_SIZE, UNIT_SIZE);

            for (int i = 0; i < bodyParts; i++) {
                if (i == 0) {
                    g2d.setColor(Color.green);
                    g2d.fillRect(x[i], y[i], UNIT_SIZE, UNIT_SIZE);
                } else {
                    g2d.setColor(new Color(45, 180, 0));
                    g2d.setColor(new Color(random.nextInt(255), random.nextInt(255), random.nextInt(255)));
                    g2d.fillRect(x[i], y[i], UNIT_SIZE, UNIT_SIZE);
                }
            }
            score(g);
        }else {
            gameOver(g);
        }
    }

    public void newApple(){
        appleX = random.nextInt((int) (SCREEN_WIDTH / UNIT_SIZE)) * UNIT_SIZE;
        appleY = random.nextInt((int) (SCREEN_HEIGHT / UNIT_SIZE)) * UNIT_SIZE;
    }

    public void move(){
        for (int i = bodyParts; i > 0; i--){
            x[i] = x[i - 1];
            y[i] = y[i - 1];

        }

        switch (direction){
            case 'U':
                y[0] = y[0] - UNIT_SIZE;
                break;
            case 'D':
                y[0] = y[0] + UNIT_SIZE;
                break;
            case 'L':
                x[0] = x[0] - UNIT_SIZE;
                break;
            case 'R':
                x[0] = x[0] + UNIT_SIZE;
                break;
        }
    }

    public void checkApple(){
        if (x[0] == appleX && y[0] == appleY){
            bodyParts++;
            applesEaten++;
            newApple();
        }
    }

    public void checkCollisions(){
        for (int i = bodyParts; i > 0; i--){
            if (x[0] == x[i] && y[0] == y[i]){
                running = false;
            }
        }
        if (x[0] < 0){
            running = false;
        }
        if (x[0] > SCREEN_WIDTH){
            running = false;
        }
        if (y[0] < 0){
            running = false;
        }
        if (y[0] > SCREEN_HEIGHT){
            running = false;
        }
        if (!running){
            myTimer.stop();
        }
    }

    public void gameOver(Graphics g){
        g.setColor(Color.RED);
        g.setFont(new Font("Ink Free", Font.BOLD, 75));
        FontMetrics metrics = getFontMetrics(g.getFont());
        g.drawString("Game Over", (SCREEN_WIDTH - metrics.stringWidth("Game Over"))/2, SCREEN_HEIGHT/2);
        score(g);
    }

    public void score(Graphics g){
        g.setColor(Color.RED);
        g.setFont(new Font("Ink Free", Font.BOLD, 40));
        FontMetrics metrics = getFontMetrics(g.getFont());
        g.drawString("Score: " + applesEaten, (SCREEN_WIDTH - metrics.stringWidth("Score: " + applesEaten))/2, g.getFont().getSize());
    }

    public void pause(Graphics g){
        if (!myTimer.hasWorking()){
            g.setColor(Color.BLUE);
            g.setFont(new Font("Ink Free", Font.BOLD, 40));
            FontMetrics metrics = getFontMetrics(g.getFont());
            g.drawString("Pause", (SCREEN_WIDTH - metrics.stringWidth("Pause"))/2, SCREEN_HEIGHT/2);
        }
    }

    public void pause2(){

    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (running){
            move();
            checkApple();
            checkCollisions();
        }
        repaint();
    }

    public class MyKeyAdapter extends KeyAdapter{
        @Override
        public void keyPressed(KeyEvent e){
            switch (e.getKeyCode()){
                case KeyEvent.VK_LEFT:
                    if (direction != 'R'){
                        direction = 'L';
                    }
                    break;
                case KeyEvent.VK_RIGHT:
                    if (direction != 'L'){
                        direction = 'R';
                    }
                    break;
                case KeyEvent.VK_UP:
                    if (direction != 'D'){
                        direction = 'U';
                    }
                    break;
                case KeyEvent.VK_DOWN:
                    if (direction != 'U'){
                        direction = 'D';
                    }
                    break;
                case KeyEvent.VK_SPACE:
                    if (!pause){
                        pause = true;
                        myTimer.stop();
                    }else {
                        pause = false;
                        myTimer.start();
                    }
                    break;
                case KeyEvent.VK_A:
                    if (!anti){
                        anti = true;
                    }else {
                        anti = false;
                    }
                    break;
            }
        }
    }

    public class MyTimer extends Timer{
        private boolean isWorking = false;
        public MyTimer(int delay, ActionListener listener) {
            super(delay, listener);
        }

        @Override
        public void start() {
            isWorking = true;
            super.start();
        }

        @Override
        public void stop() {
            isWorking = false;
            super.stop();
        }

        public boolean hasWorking(){
            return this.isWorking;
        }
    }
}
