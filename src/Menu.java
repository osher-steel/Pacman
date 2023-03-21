import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Random;

public class Menu extends JPanel implements ActionListener {
    MainPanel parent;
    Image logo;
    boolean pressed;
    Timer timer;
    Pacman pacman;
    Fruit fruit;

    int fruit_x;
    int fruit_y;
    int point_x;
    int point_y;
    int fruitTimer;
    int pointTimer;
    int points;
    int pacmanDelay;

    boolean fruitVisible;
    boolean drawFruitPoints;

    Menu(MainPanel parent){
        pacman=new Pacman();
        pacman.setFrameSpeed(10);
        this.parent=parent;
        timer= new Timer(1,this);
        timer.restart();
        setBackground(new Color(0,171,240));

        pressed=false;
        drawFruitPoints=false;
        fruitVisible=true;

        pointTimer=0;
        fruitTimer=10000;
        pacmanDelay=TimeUtils.PACMAN_DELAY(0,true);
        generateNewFruit();

        BufferedImage temp_buff;
        try {
            temp_buff = ImageIO.read(getClass().getResourceAsStream("sprites/menu/pacman_logo.png"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        logo=temp_buff.getScaledInstance(Utils.CELL_LENGTH*Utils.NUM_COLUMNS*2/3,Utils.CELL_LENGTH*Utils.NUM_ROWS/2,Image.SCALE_DEFAULT);
    }

    public void paintComponent(Graphics g){
        super.paintComponent(g);
        Graphics2D g2d=(Graphics2D) g;

        g2d.drawImage(logo,Utils.CELL_LENGTH*Utils.NUM_COLUMNS/6+10,40,this);

        g2d.setColor(new Color(255,69,0));
        g2d.setStroke(new BasicStroke(5));
        g2d.drawRoundRect((int) (Utils.CELL_LENGTH*5.3)+10,Utils.CELL_LENGTH*10,Utils.CELL_LENGTH*9,Utils.CELL_LENGTH*2,20,20);

       if(pressed)
           g2d.setPaint(Color.ORANGE.darker());
       else
           g2d.setPaint(Color.ORANGE);

        g2d.fillRoundRect((int) (Utils.CELL_LENGTH*5.3)+10,Utils.CELL_LENGTH*10,
                Utils.CELL_LENGTH*9,Utils.CELL_LENGTH*2,20,20);


        g2d.setColor(new Color(0,33,200));
        g2d.setFont(new Font("Futura",Font.BOLD,30));
        g2d.drawString("PLAY",(int) (Utils.CELL_LENGTH*5.3)+85,Utils.CELL_LENGTH*10+35);

        if(fruitVisible)
            g.drawImage(fruit.getSprite(),fruit_x,fruit_y,this);
        g.drawImage(pacman.getSprite(),pacman.x_location, pacman.y_location, this);

        if(drawFruitPoints){
            g.setColor(Color.WHITE);
            g.setFont(new Font(Font.SANS_SERIF,Font.BOLD,12));
            g.drawString(String.valueOf(points),point_x,point_y);
        }
    }


    public void mousePressed(MouseEvent e) {
        if(e.getX()>(int) (Utils.CELL_LENGTH*5.3)+10 && e.getX()<(int) (Utils.CELL_LENGTH*5.3)+10+Utils.CELL_LENGTH*9){
            if(e.getY()>Utils.CELL_LENGTH*10 && e.getY()<Utils.CELL_LENGTH*2+Utils.CELL_LENGTH*10){
                pressed=true;
            }
            repaint();
        }
    }

    public void mouseReleased(MouseEvent e) {
        if(e.getX()>(int) (Utils.CELL_LENGTH*5.3)+10 && e.getX()<(int) (Utils.CELL_LENGTH*5.3)+10+Utils.CELL_LENGTH*9){
            if(e.getY()>Utils.CELL_LENGTH*10 && e.getY()<Utils.CELL_LENGTH*2+Utils.CELL_LENGTH*10){
                parent.startGame();
                timer.stop();
            }
        }
        pressed=false;
        repaint();
    }

    public void restart(){
        timer.restart();
        pacman.x_location=Utils.CELL_LENGTH*10;
        pacman.y_location=Utils.CELL_LENGTH*18;
        pacman.x_direction=0;
        pacman.y_direction=0;
        generateNewFruit();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if(pacmanDelay<=0){
            pacman.x_location+=pacman.x_direction;
            pacman.y_location+= pacman.y_direction;

            if(pacman.x_location>=Utils.CELL_LENGTH*Utils.NUM_COLUMNS && pacman.x_direction==pacman.speed)
                pacman.x_location=-Utils.CELL_LENGTH;
            else if(pacman.x_location<=0 && pacman.x_direction==-pacman.speed)
                pacman.x_location=Utils.CELL_LENGTH*Utils.NUM_COLUMNS+ Utils.CELL_LENGTH;
            else if(pacman.y_location>=Utils.CELL_LENGTH*Utils.NUM_ROWS+Utils.NUM_ROWS*4+10 && pacman.y_direction==pacman.speed)
                pacman.y_location=-Utils.CELL_LENGTH;
            else if(pacman.y_location<=0 && pacman.y_direction==-pacman.speed)
                pacman.y_location=Utils.CELL_LENGTH*Utils.NUM_ROWS+Utils.NUM_ROWS*4+Utils.CELL_LENGTH;

            if(pacman.x_location>=fruit_x-15 && pacman.x_location<=fruit_x+15
                    && pacman.y_location>=fruit_y-15 && pacman.y_location<=fruit_y+15){
                fruitVisible=false;
                drawFruitPoints=true;
                fruitTimer=10000;
                point_x=fruit_x;
                point_y=fruit_y;
                points=fruit.getPoints();
            }

            pacman.update();

            if(drawFruitPoints)
                pointTimer++;

            if(pointTimer>=100){
                drawFruitPoints=false;
                pointTimer=0;
            }

            fruitTimer++;
            generateNewFruit();
            repaint();
            pacmanDelay=TimeUtils.PACMAN_DELAY(0,true);
        }
        else
            pacmanDelay--;
    }

    void generateNewFruit(){
        if(fruitTimer>=1000){
            Random rand= new Random();
            int r=rand.nextInt(8);
            fruit= new Fruit(Utils.CHERRIES);

            for(int i=0; i<r;i++){
                fruit.nextFruit();
            }

            fruit_x=rand.nextInt(Utils.CELL_LENGTH*Utils.NUM_COLUMNS);
            fruit_y=rand.nextInt(Utils.CELL_LENGTH*Utils.NUM_ROWS+Utils.NUM_ROWS*4);

            fruitTimer=0;
            fruitVisible=true;
        }
    }

    public void keyPressed(KeyEvent e) {
        if(e.getKeyCode()==KeyEvent.VK_UP){
            pacman.x_direction=0;
            pacman.y_direction=-pacman.speed;
            pacman.direction=0;
        }
        else if(e.getKeyCode()==KeyEvent.VK_DOWN){
            pacman.x_direction=0;
            pacman.y_direction=pacman.speed;
            pacman.direction=2;
        }
        else if(e.getKeyCode()==KeyEvent.VK_RIGHT){
            pacman.x_direction=pacman.speed;
            pacman.y_direction=0;
            pacman.direction=1;
        }
        else if(e.getKeyCode()==KeyEvent.VK_LEFT){
            pacman.x_direction=-pacman.speed;
            pacman.y_direction=0;
            pacman.direction=3;
        }
    }

}
