import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

public class Game extends JPanel implements ActionListener {
    Timer timer;
    public MainPanel parent;
    private Animations animations;
    public Pacman pacman;
    public Ghost[] ghost;
    private Fruit fruit;

    public int level;

    public boolean gameOver;
    public boolean ghostCanMove;
    public boolean pacmanCanMove;


    private int highscore;
    public int lives;
    private int ghostsEaten;
    private int energizers;

    private boolean drawFruitPoints;
    private boolean drawGhostPoints;
    private boolean drawOneUp;
    public boolean drawReady;
    private boolean extraLifeAdded;

    private long fruitTimeStamp;
    private long drawFruitPointsStamp;
    private long oneUpPaintStamp;
    private long ghostPointsPaintStamp;

    private long lastprocessed;
    private long pacmanLastProcessed;
    private final long []ghostLastProcessed;

    private final Point ghostBonus= new Point();
    private final Point oneUp= new Point();

    private int ghostBonusPoints;

    public Game(MainPanel parent, int highscore){
        MAPHANDLER.FOOD_COUNT=Utils.NUM_FOOD;
        pacmanCanMove=true;
        ghostCanMove=false;

        this.highscore=highscore;
        this.parent=parent;


        pacman=new Pacman();
        ghost= new Ghost[4];
        for(int i=0; i<ghost.length;i++){
            ghost[i]= new Ghost(i,this);
        }


        fruit= new Fruit(Utils.CHERRIES);

        pacmanLastProcessed=System.currentTimeMillis();
        ghostLastProcessed= new long[ghost.length];
        for(long g:ghostLastProcessed){
            g=System.currentTimeMillis();
        }

        lives=Utils.LIVES;
        level=0;
        ghostsEaten=0;

        drawFruitPoints=false;
        drawGhostPoints=false;
        drawOneUp=false;
        gameOver=false;
        extraLifeAdded=false;

        timer= new Timer(1,this);
        timer.start();
        animations=new Animations(this);

        newLevel();
        lastprocessed=System.currentTimeMillis();
        setBackground(Color.BLACK);
    }

    @Override
    public void paintComponent(Graphics g){
        super.paintComponent(g);

        if(Utils.DRAW_TARGETS){
            g.setColor(Color.RED);
            g.fillRect(ghost[0].grid_target.x * Utils.CELL_LENGTH, ghost[0].grid_target.y * Utils.CELL_LENGTH, Utils.CELL_LENGTH, Utils.CELL_LENGTH);

            g.setColor(Color.PINK);
            g.fillRect(ghost[1].grid_target.x * Utils.CELL_LENGTH, ghost[1].grid_target.y * Utils.CELL_LENGTH, Utils.CELL_LENGTH, Utils.CELL_LENGTH);

            g.setColor(Color.CYAN);
            g.fillRect(ghost[2].grid_target.x  * Utils.CELL_LENGTH, ghost[2].grid_target.y * Utils.CELL_LENGTH, Utils.CELL_LENGTH, Utils.CELL_LENGTH);

            g.setColor(Color.ORANGE);
            g.fillRect(ghost[3].grid_target.x  * Utils.CELL_LENGTH, ghost[3].grid_target.y * Utils.CELL_LENGTH, Utils.CELL_LENGTH, Utils.CELL_LENGTH);

        }

         drawMap(g);


        for(Ghost gh:ghost){
            if(gh.isVisible)
                g.drawImage(gh.getSprite(),gh.coordinates.x,gh.coordinates.y,this);
        }

        //Game info
        g.setColor(Color.WHITE);
        g.setFont(new Font("IMPACT",Font.BOLD,18));
        g.drawString("SCORE: "+MAPHANDLER.SCORE,Utils.CELL_LENGTH*10-20,Utils.CELL_LENGTH*(Utils.NUM_ROWS+1));
        g.drawString("HIGH SCORE: "+highscore,Utils.CELL_LENGTH*9-25,Utils.CELL_LENGTH*(Utils.NUM_ROWS+2));
        g.drawString("LEVEL: "+level,Utils.CELL_LENGTH*17+10,Utils.CELL_LENGTH*(Utils.NUM_ROWS+2));
        for(int i=0; i<lives;i++){
            g.drawImage(Utils.LIFE_IMG,Utils.CELL_LENGTH+(i*Utils.CELL_LENGTH),Utils.CELL_LENGTH*(Utils.NUM_ROWS)+10,this);
        }


        if(drawFruitPoints){
            g.setColor(Color.PINK);
            g.setFont(new Font(Font.SANS_SERIF,Font.BOLD,12));
            g.drawString(String.valueOf(fruit.getPoints()),10*Utils.CELL_LENGTH,12*Utils.CELL_LENGTH-(Utils.CELL_LENGTH/3));
        }
        if(drawGhostPoints){
            g.setColor(Color.PINK);
            g.setFont(new Font(Font.SANS_SERIF,Font.BOLD,12));
            g.drawString(String.valueOf(ghostBonusPoints),ghostBonus.x,ghostBonus.y);
        }
        if(drawOneUp){
            g.setColor(Color.PINK);
            g.setFont(new Font(Font.SANS_SERIF,Font.BOLD,15));
            g.drawString("1UP",oneUp.x,oneUp.y);
        }


        if(pacman.isVisible){
           g.drawImage(pacman.getSprite(),pacman.coordinates.x, pacman.coordinates.y, this);
       }

        if(gameOver){
            g.setColor(Color.RED);
            g.setFont(new Font("Impact",Font.BOLD,20));
            g.drawString("GAME OVER",Utils.CELL_LENGTH*9-5,Utils.CELL_LENGTH*12-3);
        }

        if(drawReady){
            g.setColor(Color.YELLOW);
            g.setFont(new Font("Impact",Font.BOLD,20));
            g.drawString("READY !",Utils.CELL_LENGTH*9+10,Utils.CELL_LENGTH*12-3);
        }


    }
    public void drawMap(Graphics g){
         for(int i=0; i<MAPHANDLER.MAP.length;i++){
             for(int j=0; j<MAPHANDLER.MAP[0].length; j++){
                 switch (MAPHANDLER.MAP[i][j]) {
                     //                    case 0,2->{
                     //                        g.setColor(Color.WHITE);
                     //                        g.setFont(new Font(Font.SERIF, Font.PLAIN,10));
                     //                        g.drawString(i+","+j,j*Utils.CELL_LENGTH,i*Utils.CELL_LENGTH+Utils.CELL_LENGTH);
                     //                    }
                     case -1 -> {
                         g.setColor(Color.gray);
                         g.fillRect(j * Utils.CELL_LENGTH,
                                 i * Utils.CELL_LENGTH, Utils.CELL_LENGTH, Utils.CELL_LENGTH);
                     }
                     case 1 -> {
                         g.setColor(Color.BLUE);
                         g.fillRect(j * Utils.CELL_LENGTH,
                                 i * Utils.CELL_LENGTH, Utils.CELL_LENGTH, Utils.CELL_LENGTH);
                     }
                     case 2 -> {
                         if(!gameOver){
                             g.setColor(Color.WHITE);
                             g.fillOval(j * Utils.CELL_LENGTH + (Utils.CELL_LENGTH * 2 / 5),
                                     i * Utils.CELL_LENGTH +(Utils.CELL_LENGTH * 2 / 5),
                                     Utils.CELL_LENGTH / 5, Utils.CELL_LENGTH / 5);
                         }
                     }
                     case 3 -> {
                         if(!gameOver){
                             g.setColor(Color.WHITE);
                             g.fillOval(j * Utils.CELL_LENGTH + (Utils.CELL_LENGTH / 4), i * Utils.CELL_LENGTH +
                                     (Utils.CELL_LENGTH / 4), Utils.CELL_LENGTH / 2, Utils.CELL_LENGTH / 2);
                         }
                     }
                     case 4->{
                         if(MAPHANDLER.FRUIT_VISIBLE){
                             g.drawImage(fruit.getSprite(), j*Utils.CELL_LENGTH, i*Utils.CELL_LENGTH,this);
                         }
                     }
                     //                    case 8->{
                     //                        g.setColor(Color.RED);
                     //                        g.fillRect(j * Utils.CELL_LENGTH,
                     //                                i * Utils.CELL_LENGTH, Utils.CELL_LENGTH, Utils.CELL_LENGTH);
                     //                    }
                     //                    case Utils.CYAN->{
                     //                        g.setColor(Color.CYAN);
                     //                        g.fillRect(j * Utils.CELL_LENGTH,
                     //                                i * Utils.CELL_LENGTH, Utils.CELL_LENGTH, Utils.CELL_LENGTH);
                     //                    }
                     //                    case Utils.PINK->{
                     //                        g.setColor(Color.PINK);
                     //                        g.fillRect(j * Utils.CELL_LENGTH,
                     //                                i * Utils.CELL_LENGTH, Utils.CELL_LENGTH, Utils.CELL_LENGTH);
                     //                    }
                     //                    case Utils._ORANGE->{
                     //                        g.setColor(Color.ORANGE);
                     //                        g.fillRect(j * Utils.CELL_LENGTH,
                     //                                i * Utils.CELL_LENGTH, Utils.CELL_LENGTH, Utils.CELL_LENGTH);
                     //                    }
                 }
             }
         }
     }
    public void keyPressed(KeyEvent e) {

        if(e.getKeyCode()==KeyEvent.VK_RIGHT){
            pacman.new_movement.x=pacman.speed;
            pacman.new_movement.y=0;
        }else if(e.getKeyCode()==KeyEvent.VK_LEFT){
            pacman.new_movement.x=-pacman.speed;
            pacman.new_movement.y=0;
        }else if(e.getKeyCode()==KeyEvent.VK_DOWN){
            pacman.new_movement.y=pacman.speed;
            pacman.new_movement.x=0;
        }else if(e.getKeyCode()==KeyEvent.VK_UP){
            pacman.new_movement.y=-pacman.speed;
            pacman.new_movement.x=0;
        }
    }

     //CALLED EVERY TIME TIMER SHOOTS
    @Override
    public void actionPerformed(ActionEvent e){
        lastprocessed=System.currentTimeMillis();
        animations.actionPerformed();

        checkPaintTimers();
        checkEntityDelays();
        checkGlobalValues();
        repaint();
    }

    private void checkPaintTimers() {
        if(drawFruitPoints && System.currentTimeMillis()-drawFruitPointsStamp>=TimeUtils.POINTS_VISIBLE_TIME){
            drawFruitPoints=false;
        }

        if(drawGhostPoints && System.currentTimeMillis()-ghostPointsPaintStamp>=TimeUtils.POINTS_VISIBLE_TIME){
            drawGhostPoints=false;
        }

        if(drawOneUp && System.currentTimeMillis()-oneUpPaintStamp>=TimeUtils.ONEUP_VISIBLE_TIME){
            drawOneUp=false;
        }

        if(MAPHANDLER.FRUIT_VISIBLE && System.currentTimeMillis()-fruitTimeStamp>=TimeUtils.FRUIT_VISIBLE_TIME){
            MAPHANDLER.FRUIT_VISIBLE=false;
        }
    }

    private void checkEntityDelays() {
        boolean frightened=false;
        for(Ghost g:ghost){
            if (g.isFrightened) {
                frightened = true;
                break;
            }
        }
        if(System.currentTimeMillis()-pacmanLastProcessed>=TimeUtils.PACMAN_DELAY(level,false,frightened)){
            pacman.update();
            pacmanLastProcessed=System.currentTimeMillis();
        }

        if(ghostCanMove){
            for(int i=0; i<ghostLastProcessed.length;i++){
                if( System.currentTimeMillis()-ghostLastProcessed[i]>=TimeUtils.GHOST_DELAY(level,ghost[i].mode)){
                    ghost[i].update();
                    ghostLastProcessed[i]=System.currentTimeMillis();
                }
            }
        }

    }

    private void checkGlobalValues() {
        if(MAPHANDLER.FOOD_COUNT==0){
            newLevel();
        }

        if(MAPHANDLER.SCORE>=highscore)
            highscore=MAPHANDLER.SCORE;

        if(MAPHANDLER.ENERGIZER_COUNT<energizers){
            ateEnergizer();
            energizers--;
        }

        if(MAPHANDLER.FOOD_COUNT<TimeUtils.FRUIT_VISIBLE_FOODCOUNT1 && !MAPHANDLER.FRUIT_APPEARED){
            MAPHANDLER.FRUIT_APPEARED=true;
            MAPHANDLER.FRUIT_VISIBLE=true;
            fruitTimeStamp=System.currentTimeMillis();
        }

        if(MAPHANDLER.FRUIT_EATEN && MAPHANDLER.FRUIT_VISIBLE){
            MAPHANDLER.SCORE+=fruit.getPoints();
            MAPHANDLER.FRUIT_VISIBLE=false;
            drawFruitPoints=true;
            drawFruitPointsStamp=System.currentTimeMillis();
        }

        if(MAPHANDLER.SCORE>=TimeUtils.ONEUP_SCORE && !extraLifeAdded){
            drawOneUp=true;
            oneUpPaintStamp=System.currentTimeMillis();
            lives++;

            extraLifeAdded=true;
            oneUp.x=pacman.coordinates.x;
            oneUp.y=pacman.coordinates.y;
        }
    }


    //CALLED WHEN A CRITERIA IS MET
    private void newLevel() {
        MAPHANDLER.resetValues();
        MAPHANDLER.copyMAP();

        energizers=4;
        ghostsEaten=0;

        if(level==2||level==3 || level==5 || level==7 ||level==9 || level==11 ||level==13){
            fruit.nextFruit();
        }

        level++;

        if(level==0){
            animations.newLevel();
        }
        else{
            animations.finishedLevel();
        }

    }
    public void lostLife() {
        lives--;
        animations.lostLife();
    }
    public void ateEnergizer(){
        ghostsEaten=0;
        for(Ghost g:ghost){
            g.makeFrightened();
        }
    }
    public void ghostKilled(Ghost ghost) {

        drawGhostPoints=true;
        ghostPointsPaintStamp=System.currentTimeMillis();

        ghostBonus.x=ghost.coordinates.x;
        ghostBonus.y=ghost.coordinates.y;

        switch (ghostsEaten) {
            case 0 -> {
                ghostBonusPoints = 200;
                ghostsEaten++;
            }
            case 1 -> {
                ghostBonusPoints = 400;
                ghostsEaten++;
            }
            case 2 -> {
                ghostBonusPoints = 800;
                ghostsEaten++;
            }
            case 3 -> {
                ghostBonusPoints = 1600;
                ghostsEaten++;
            }
        }

        MAPHANDLER.SCORE+=ghostBonusPoints;
    }

    public void gameOver(){
        timer.stop();
        parent.returnToMenu(MAPHANDLER.SCORE);
        MAPHANDLER.SCORE=0;
    }
}
