import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Random;
import java.util.List;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.AudioInputStream;
import java.io.File;
import java.awt.geom.AffineTransform;

public class ProyectoParcial2MendozaReyesAngelEmanuel extends JFrame {
    private int rocketX;
    private int rocketY;
    private final int rocketWidth = 100;
    private final int rocketHeight = 100;
    private final List<Point> redBalls = new ArrayList<>();
    private final List<Shape> shapes = new ArrayList<>();
    private boolean movingUp = false;
    private boolean movingDown = false;
    private boolean movingLeft = false;
    private boolean movingRight = false;
    private int vidas = 3;
    private int puntos = 0;
    private boolean gameWon = false;
    private double scale = 1.0;
    private boolean scalingUp = true;
    private double rotationAngle = 0;
    private double skewX = 0;
    private double skewY = 0;
    private boolean skewingUp = true;
    private double alienSkewX = 0;
    private double alienSkewY = 0;
    private boolean alienSkewingUp = true;
    
    public ProyectoParcial2MendozaReyesAngelEmanuel() {
        setTitle("ProyectoParcial2 - Mendoza Reyes Angel Emanuel");
        setIconImage(Toolkit.getDefaultToolkit().getImage(getClass().getResource("/cohete.png")));
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        
        rocketX = (getWidth() - rocketWidth) / 2;
        rocketY = getHeight() - rocketHeight - 10;

        SpaceBackgroundPanel panel = new SpaceBackgroundPanel();
        add(panel);

        Toolkit toolkit = Toolkit.getDefaultToolkit();
        Image image = toolkit.getImage(getClass().getResource("/nave.png"));
        Cursor c = toolkit.createCustomCursor(image, new Point(0, 0), "nave");
        setCursor(c);
        
        playBackgroundSound("Galaxia.wav");
        initializeShapes();

addKeyListener(new KeyAdapter() {
    @Override
    public void keyPressed(KeyEvent e) {
        switch (e.getKeyCode()) {
            case KeyEvent.VK_W:
                movingUp = true;
                break;
            case KeyEvent.VK_A:
                movingLeft = true;
                break;
            case KeyEvent.VK_S:
                movingDown = true;
                break;
            case KeyEvent.VK_D:
                movingRight = true;
                break;
            case KeyEvent.VK_P:
                redBalls.add(new Point(rocketX + rocketWidth / 2 - 5, rocketY));
                playSound("Laser.wav");
                break;
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        switch (e.getKeyCode()) {
            case KeyEvent.VK_W:
                movingUp = false;
                break;
            case KeyEvent.VK_A:
                movingLeft = false;
                break;
            case KeyEvent.VK_S:
                movingDown = false;
                break;
            case KeyEvent.VK_D:
                movingRight = false;
                break;
        }
    }
});

Timer timer = new Timer(50, e -> {
    for (int i = 0; i < redBalls.size(); i++) {
        Point p = redBalls.get(i);
        p.y -= 10;
        if (p.y < 0) {
            redBalls.remove(i);
            i--;
            continue;
        }

        for (int j = 0; j < shapes.size(); j++) {
            Shape shape = shapes.get(j);
            if (isCollision(p, shape)) {
                if (shape.type == ShapeType.ALIEN) {
                    puntos++;
                    if (puntos >= 5 && !gameWon) {
                        gameWon = true;
                        int option = JOptionPane.showConfirmDialog(this, "¡Felicidades, ganaste el juego! ¿Deseas jugar otra vez?", "Juego Ganado", JOptionPane.YES_NO_OPTION);
                        if (option == JOptionPane.YES_OPTION) {
                            vidas = 3;
                            puntos = 0;
                            rocketX = (getWidth() - rocketWidth) / 2; 
                            rocketY = getHeight() - rocketHeight - 10;
                            movingUp = false;
                            movingDown = false;
                            movingLeft = false;
                            movingRight = false;
                            shapes.clear();
                            initializeShapes();
                            gameWon = false;
                        } else {
                            System.exit(0);
                        }
                    }
                }
                shapes.remove(j);
                redBalls.remove(i);
                i--;
                break;
            }
        }
    }
    repaint();
});
timer.start();

Timer shapeTimer = new Timer(50, e -> {
    Random rand = new Random();
    if (rand.nextInt(100) < 5) { 
        int x = rand.nextInt(getWidth());
        int y = -rand.nextInt(200); 
        int size = rand.nextInt(30) + 20; 

        switch (rand.nextInt(5)) {
            case 0:
                shapes.add(new Shape(x, y, size, size, ShapeType.CIRCLE));
                break;
            case 1:
                shapes.add(new Shape(x, y, size, size, ShapeType.RECTANGLE));
                break;
            case 2:
                shapes.add(new Shape(x, y, size, size, ShapeType.SQUARE));
                break;
            case 3:
                shapes.add(new Shape(x, y, size, size, ShapeType.OVAL));
                break;
            case 4:
                shapes.add(new Shape(x, y, 50, 50, ShapeType.ALIEN));
                break;
        }
    }

    for (Shape shape : shapes) {
        shape.y += 10; 
        if (shape.y > getHeight()) {
            shape.y = -shape.height; 
            shape.x = new Random().nextInt(getWidth());
        }

        if (isCollisionWithRocket(shape) && !gameWon) {
            vidas--;
            shapes.remove(shape);
            if (vidas <= 0) {
                int option = JOptionPane.showConfirmDialog(this, "¿Deseas jugar otra vez?", "Juego Terminado", JOptionPane.YES_NO_OPTION);
                if (option == JOptionPane.YES_OPTION) {
                    vidas = 3;
                    puntos = 0;
                    rocketX = (getWidth() - rocketWidth) / 2; 
                    rocketY = getHeight() - rocketHeight - 10; 
                    movingUp = false;
                    movingDown = false;
                    movingLeft = false;
                    movingRight = false;
                    shapes.clear(); 
                    initializeShapes();
                    gameWon = false; 
                } else {
                    System.exit(0);
                }
            }
            break;
        }
    }
    repaint();
});
shapeTimer.start();

Timer scaleTimer = new Timer(50, e -> {
    if (scalingUp) {
        scale += 0.05;
        if (scale >= 1.5) {
            scalingUp = false;
        }
    } else {
        scale -= 0.05;
        if (scale <= 0.5) {
            scalingUp = true;
        }
    }
    repaint();
});
scaleTimer.start();

Thread rocketMovementThread = new Thread(() -> {
    while (true) {
        if (movingUp && rocketY > 0) rocketY -= 10;
        if (movingDown && rocketY < getHeight() - rocketHeight) rocketY += 10;
        if (movingLeft && rocketX > 0) rocketX -= 10;
        if (movingRight && rocketX < getWidth() - rocketWidth) rocketX += 10;
        repaint();
        try {
            Thread.sleep(50);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
});
rocketMovementThread.start();

Timer rotationTimer = new Timer(50, e -> {
    rotationAngle += 5;
    if (rotationAngle >= 360) {
        rotationAngle = 0;
    }
    repaint();
});
rotationTimer.start();

Timer skewTimer = new Timer(50, e -> {
    if (skewingUp) {
        skewX += 0.01;
        skewY += 0.01;
        if (skewX >= 0.5 || skewY >= 0.5) {
            skewingUp = false;
        }
    } else {
        skewX -= 0.01;
        skewY -= 0.01;
        if (skewX <= -0.5 || skewY <= -0.5) {
            skewingUp = true;
        }
    }
    repaint();
});
skewTimer.start();

Timer alienSkewTimer = new Timer(50, e -> {
    if (alienSkewingUp) {
        alienSkewX += 0.01;
        alienSkewY += 0.01;
        if (alienSkewX >= 0.5 || alienSkewY >= 0.5) {
            alienSkewingUp = false;
        }
    } else {
        alienSkewX -= 0.01;
        alienSkewY -= 0.01;
        if (alienSkewX <= -0.5 || alienSkewY <= -0.5) {
            alienSkewingUp = true;
        }
    }
    repaint();
});
alienSkewTimer.start();
}

private boolean isCollision(Point ball, Shape shape) {
    Rectangle ballRect = new Rectangle(ball.x, ball.y, 10, 10);
    Rectangle shapeRect = new Rectangle(shape.x, shape.y, shape.width, shape.height);
    return ballRect.intersects(shapeRect);
}

private boolean isCollisionWithRocket(Shape shape) {
    Rectangle rocketRect = new Rectangle(rocketX, rocketY, rocketWidth, rocketHeight);
    Rectangle shapeRect = new Rectangle(shape.x, shape.y, shape.width, shape.height);
    return rocketRect.intersects(shapeRect);
}

    private void playSound(String soundFile) {
        try {
            AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(new File(soundFile).getAbsoluteFile());
            Clip clip = AudioSystem.getClip();
            clip.open(audioInputStream);
            clip.start();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void playBackgroundSound(String soundFile) {
        try {
            AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(new File(soundFile).getAbsoluteFile());
            Clip clip = AudioSystem.getClip();
            clip.open(audioInputStream);
            clip.loop(Clip.LOOP_CONTINUOUSLY);
            clip.start();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

private void initializeShapes() {
    Random rand = new Random();
    for (int i = 0; i < 20; i++) { 
        int x = rand.nextInt(getWidth());
        int y = -rand.nextInt(200); 
        int size = rand.nextInt(30) + 20; 

        switch (rand.nextInt(5)) { 
            case 0:
                shapes.add(new Shape(x, y, size, size, ShapeType.CIRCLE));
                break;
            case 1:
                shapes.add(new Shape(x, y, size, size, ShapeType.RECTANGLE));
                break;
            case 2:
                shapes.add(new Shape(x, y, size, size, ShapeType.SQUARE));
                break;
            case 3:
                shapes.add(new Shape(x, y, size, size, ShapeType.OVAL));
                break;
            case 4:
                shapes.add(new Shape(x, y, 50, 50, ShapeType.ALIEN)); 
                break;
        }
    }
}
    public static void main(String[] args) {
        ProyectoParcial2MendozaReyesAngelEmanuel frame = new ProyectoParcial2MendozaReyesAngelEmanuel();
        frame.setVisible(true);
    }

    class SpaceBackgroundPanel extends JPanel {
        @Override
        protected void paintComponent(Graphics g) {
        
        super.paintComponent(g);

    Graphics2D g2d = (Graphics2D) g;

    Color colorAzulOscuro = new Color(0, 0, 51);
    Color colorNegro = new Color(0, 0, 0);
    GradientPaint gradient = new GradientPaint(0, 0, colorAzulOscuro, 0, getHeight(), colorNegro);
    g2d.setPaint(gradient);
    g2d.fillRect(0, 0, getWidth(), getHeight());

    g2d.setColor(Color.WHITE);
    Random aleatorio = new Random();
    for (int i = 0; i < 100; i++) {
        int x = aleatorio.nextInt(getWidth());
        int y = aleatorio.nextInt(getHeight());
        int tamañoEstrella = aleatorio.nextInt(3) + 1;
        g2d.fillOval(x, y, tamañoEstrella, tamañoEstrella);
    } 
            BufferedImage texture = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_ARGB);
            for (int y = 0; y < getHeight(); y += 10) {
                for (int x = 0; x < getWidth(); x += 10) {
                    int gray = aleatorio.nextInt(256);
                    Color color = new Color(gray, gray, gray, 50);
                    for (int dy = 0; dy < 10; dy++) {
                        for (int dx = 0; dx < 10; dx++) {
                            if (x + dx < getWidth() && y + dy < getHeight()) {
                                texture.setRGB(x + dx, y + dy, color.getRGB());
                            }
                        }
                    }
                }
            }
            g2d.drawImage(texture, 0, 0, this);
            drawShapes(g2d);
            drawSmallStars(g2d, 50);
            drawRedBalls(g2d);
            drawRocketImage(g2d);
            drawRedTriangles(g2d);
            drawText(g2d);
}

private void drawText(Graphics2D g2d) {
    g2d.setColor(Color.WHITE);
    g2d.setFont(new Font("Monospaced", Font.BOLD, 24)); 
    g2d.drawString("Vidas: " + vidas, 40, 30); 
    g2d.drawString("Puntos: " + puntos, 40, 60);
}

private void drawShapes(Graphics2D g2d) {
    for (Shape shape : shapes) {
        switch (shape.type) {
            case CIRCLE:
                drawCircle(g2d, shape.x, shape.y, shape.width);
                break;
            case RECTANGLE:
                drawRectangle(g2d, shape.x, shape.y, shape.width, shape.height);
                break;
            case SQUARE:
                drawSquare(g2d, shape.x, shape.y, shape.width);
                break;
            case OVAL:
                drawOval(g2d, shape.x, shape.y, shape.width, shape.height);
                break;
            case ALIEN:
                drawAlienImage(g2d, shape.x, shape.y); 
                break;
        }
    }
}

private void drawSmallStars(Graphics2D g2d, int numberOfStars) {
            Random rand = new Random();
            for (int i = 0; i < numberOfStars; i++) {
                int x = rand.nextInt(getWidth());
                int y = rand.nextInt(getHeight());
                g2d.setColor(i % 2 == 0 ? Color.WHITE : Color.YELLOW);
                if (i % 2 == 0) {
                    drawDashedStar(g2d, x, y, 5);
                } else {
                    drawSolidStar(g2d, x, y, 5);
                }
            }
        }

        private void drawDashedStar(Graphics2D g2d, int x, int y, int size) {
            float[] dashPattern = {5, 5};
            BasicStroke dashed = new BasicStroke(1, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10, dashPattern, 0);
            g2d.setStroke(dashed);

            g2d.drawLine(x - size, y, x + size, y); 
            g2d.drawLine(x, y - size, x, y + size); 
            g2d.drawLine(x - size, y - size, x + size, y + size); 
            g2d.drawLine(x - size, y + size, x + size, y - size); 
        }

        private void drawSolidStar(Graphics2D g2d, int x, int y, int size) {
            BasicStroke solid = new BasicStroke(1);
            g2d.setStroke(solid);

            g2d.drawLine(x - size, y, x + size, y); 
            g2d.drawLine(x, y - size, x, y + size); 
            g2d.drawLine(x - size, y - size, x + size, y + size); 
            g2d.drawLine(x - size, y + size, x + size, y - size); 
        }

        private void drawRocketImage(Graphics2D g2d) {
            ImageIcon rocketIcon = new ImageIcon(getClass().getResource("/cohete.png"));
            g2d.drawImage(rocketIcon.getImage(), rocketX, rocketY, rocketWidth, rocketHeight, this);
        }

    private void drawAlienImage(Graphics2D g2d, int x, int y) {
    AffineTransform originalTransform = g2d.getTransform();

    AffineTransform skewTransform = new AffineTransform();
    skewTransform.translate(x + 25, y + 25);
    skewTransform.shear(alienSkewX, alienSkewY);
    skewTransform.translate(-(x + 25), -(y + 25));
    g2d.transform(skewTransform);

    ImageIcon alienIcon = new ImageIcon(getClass().getResource("/alien.png"));
    int alienWidth = 50;
    int alienHeight = 50;
    g2d.drawImage(alienIcon.getImage(), x, y, alienWidth, alienHeight, this);

    g2d.setTransform(originalTransform);
}

private void drawRedTriangles(Graphics2D g2d) {
    int panelHeight = getHeight();
    int triangleHeight = (int) (50 * scale); 
    int triangleWidth = (int) (30 * scale);

    for (int y = 0; y < panelHeight; y += 50) {
        drawRedTriangle(g2d, 0, y, triangleWidth, triangleHeight, new Color(255, 0, 0), new Color(139, 0, 0), true); 
        drawRedTriangle(g2d, getWidth(), y, triangleWidth, triangleHeight, new Color(255, 0, 0), new Color(139, 0, 0), false);
    }
}

    private void drawRedTriangle(Graphics2D g2d, int x, int y, int width, int height, Color color1, Color color2, boolean leftSide) {
    GradientPaint gradient = new GradientPaint(x, y, color1, x + width, y + height, color2);
    g2d.setPaint(gradient);

    int[] xPoints;
    int[] yPoints = {y, y + height / 2, y + height};

    if (leftSide) {
        xPoints = new int[]{x, x + width, x};
    } else {
        xPoints = new int[]{x, x - width, x};
    }

    g2d.fillPolygon(xPoints, yPoints, 3);

    BufferedImage texture = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
    Random rand = new Random();
    for (int i = 0; i < width; i += 5) {
        for (int j = 0; j < height; j += 5) {
            int gray = rand.nextInt(256);
            Color color = new Color(gray, gray, gray, 50);
            for (int dy = 0; dy < 5; dy++) {
                for (int dx = 0; dx < 5; dx++) {
                    if (i + dx < width && j + dy < height) {
                        texture.setRGB(i + dx, j + dy, color.getRGB());
                    }
                }
            }
        }
    }
    g2d.drawImage(texture, x - (leftSide ? 0 : width), y, this);
}
        private void drawRedBalls(Graphics2D g2d) {
    g2d.setColor(Color.RED);
    for (Point p : redBalls) {
        g2d.fillOval(p.x, p.y, 10, 10);
    }
}

}

private enum ShapeType {
    CIRCLE, RECTANGLE, SQUARE, OVAL, ALIEN
}

private class Shape {
    int x, y, width, height;
    ShapeType type;

    Shape(int x, int y, int width, int height, ShapeType type) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.type = type;
    }
}
private void drawCircle(Graphics2D g2d, int x, int y, int diameter) {
    AffineTransform originalTransform = g2d.getTransform();

    AffineTransform skewTransform = new AffineTransform();
    skewTransform.translate(x + diameter / 2, y + diameter / 2);
    skewTransform.shear(skewX, skewY);
    skewTransform.translate(-(x + diameter / 2), -(y + diameter / 2));
    g2d.transform(skewTransform);

    GradientPaint gradient = new GradientPaint(x, y, Color.RED, x + diameter, y + diameter, Color.RED);
    g2d.setPaint(gradient);
    g2d.fillOval(x, y, diameter, diameter);

    BufferedImage texture = new BufferedImage(diameter, diameter, BufferedImage.TYPE_INT_ARGB);
    Random rand = new Random();
    for (int i = 0; i < diameter; i += 5) {
        for (int j = 0; j < diameter; j += 5) {
            int gray = rand.nextInt(256);
            Color color = new Color(gray, gray, gray, 50); 
            for (int dy = 0; dy < 5; dy++) {
                for (int dx = 0; dx < 5; dx++) {
                    if (i + dx < diameter && j + dy < diameter) {
                        texture.setRGB(i + dx, j + dy, color.getRGB());
                    }
                }
            }
        }
    }
    g2d.drawImage(texture, x, y, this);
    g2d.setTransform(originalTransform);
}

private void drawRectangle(Graphics2D g2d, int x, int y, int width, int height) {
    width = width * 2; 

    AffineTransform originalTransform = g2d.getTransform();

    g2d.rotate(Math.toRadians(rotationAngle), x + width / 2, y + height / 2);

    GradientPaint gradient = new GradientPaint(x, y, Color.GRAY, x + width, y + height, Color.GRAY);
    g2d.setPaint(gradient);
    g2d.fillRect(x, y, width, height);

    BufferedImage texture = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
    Random rand = new Random();
    for (int i = 0; i < width; i += 5) {
        for (int j = 0; j < height; j += 5) {
            int gray = rand.nextInt(256);
            Color color = new Color(gray, gray, gray, 50);
            for (int dy = 0; dy < 5; dy++) {
                for (int dx = 0; dx < 5; dx++) {
                    if (i + dx < width && j + dy < height) {
                        texture.setRGB(i + dx, j + dy, color.getRGB());
                    }
                }
            }
        }
    }
    g2d.drawImage(texture, x, y, this);

    g2d.setTransform(originalTransform);
}

private void drawSquare(Graphics2D g2d, int x, int y, int size) {
    AffineTransform originalTransform = g2d.getTransform();

    g2d.rotate(Math.toRadians(rotationAngle), x + size / 2, y + size / 2);

    GradientPaint gradient = new GradientPaint(x, y, Color.BLUE, x + size, y + size, Color.BLUE);
    g2d.setPaint(gradient);
    g2d.fillRect(x, y, size, size);

    BufferedImage texture = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
    Random rand = new Random();
    for (int i = 0; i < size; i += 5) {
        for (int j = 0; j < size; j += 5) {
            int gray = rand.nextInt(256);
            Color color = new Color(gray, gray, gray, 50);
            for (int dy = 0; dy < 5; dy++) {
                for (int dx = 0; dx < 5; dx++) {
                    if (i + dx < size && j + dy < size) {
                        texture.setRGB(i + dx, j + dy, color.getRGB());
                    }
                }
            }
        }
    }
    g2d.drawImage(texture, x, y, this);

    g2d.setTransform(originalTransform);
}

private void drawOval(Graphics2D g2d, int x, int y, int width, int height) {
    width = width * 2;

    AffineTransform originalTransform = g2d.getTransform();

    AffineTransform skewTransform = new AffineTransform();
    skewTransform.translate(x + width / 2, y + height / 2);
    skewTransform.shear(skewX, skewY);
    skewTransform.translate(-(x + width / 2), -(y + height / 2));
    g2d.transform(skewTransform);

    GradientPaint gradient = new GradientPaint(x, y, new Color(139, 69, 19), x + width, y + height, new Color(160, 82, 45));
    g2d.setPaint(gradient);
    g2d.fillOval(x, y, width, height);

    BufferedImage texture = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
    Random rand = new Random();
    for (int i = 0; i < width; i += 5) {
        for (int j = 0; j < height; j += 5) {
            int gray = rand.nextInt(256);
            Color color = new Color(gray, gray, gray, 50);
            for (int dy = 0; dy < 5; dy++) {
                for (int dx = 0; dx < 5; dx++) {
                    if (i + dx < width && j + dy < height) {
                        texture.setRGB(i + dx, j + dy, color.getRGB());
                    }
                }
            }
        }
    }
    g2d.drawImage(texture, x, y, this);
    g2d.setTransform(originalTransform);
}
}