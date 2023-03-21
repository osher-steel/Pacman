import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

public class Game extends JPanel{
    MainPanel parent;
    Animations animations;
    Pacman pacman;
    Ghost[] ghost;
    Fruit fruit;

    public int [][]map;

    int score;
    int highscore;
    int lives;
    int foodCount;
    int level;
    int ghostsEaten;

    boolean fruitVisible;
    boolean fruitUsed;
    boolean drawFruitPoints;
    boolean drawGhostPoints;
    boolean drawOneUp;
    boolean gameOver;
    boolean drawReady;
    boolean extraLifeAdded;

    long fruitTimeStamp;
    long fruitPaintStamp;
    long oneUpPaintStamp;
    long ghostPointsPaintStamp;

    long lastprocessed;
    long pacmanLastProcessed;
    long []ghostLastProcessed;

    int ghostBonus_x,ghostBonus_y;
    int oneUp_x,oneUp_y;

    int ghostBonusPoints;

    Game(MainPanel parent, int highscore){
        this.highscore=highscore;
        this.parent=parent;

        map= new int[21][21];
        copyMap();
        setBackground(Color.BLACK);


        pacman=new Pacman();
        ghost= new Ghost[4];
        for(int i=0; i<ghost.length;i++){
            ghost[i]= new Ghost(i,this);
        }

        fruit= new Fruit(Utils.CHERRIES);

        lives=Utils.LIVES;
        score=0;
        level=0;
        ghostsEaten=0;
        foodCount=Utils.NUM_FOOD;

        fruitUsed=false;
        drawFruitPoints=false;
        drawGhostPoints=false;
        drawOneUp=false;
        gameOver=false;
        extraLifeAdded=false;
        animations=new Animations(this);
        newLevel();
        pacmanLastProcessed=System.currentTimeMillis();
        ghostLastProcessed= new long[ghost.length];
        for(long g:ghostLastProcessed){
            g=System.currentTimeMillis();
        }

        lastprocessed=System.currentTimeMillis();
    }

    public void copyMap() {
        for(int i=0; i<map[0].length;i++){
            System.arraycopy(Utils.INITIAL_MAP[i], 0, map[i], 0, map.length);
        }
    }

    public void start(){
        animations.start();
    }
    @Override
    public void paintComponent(Graphics g){
        super.paintComponent(g);

        //Drawing the map
        for(int i=0; i<map.length;i++){
            for(int j=0; j<map[0].length; j++){
                switch (map[i][j]) {
//                    case 0,2->{
//                        g.setColor(Color.WHITE);
//                        g.setFont(new Font(Font.SERIF, Font.PLAIN,10));
//                        g.drawString(j+","+i,j*Utils.CELL_LENGTH,i*Utils.CELL_LENGTH+Utils.CELL_LENGTH);
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
                        if(fruitVisible){
                            g.drawImage(fruit.getSprite(), j*Utils.CELL_LENGTH, i*Utils.CELL_LENGTH,this);
                        }
                    }
//                    case Utils.RED->{
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


        for(Ghost gh:ghost){
            if(gh.isVisible)
                g.drawImage(gh.getSprite(),gh.x_location,gh.y_location,this);
        }

        //Game info
        g.setColor(Color.WHITE);
        g.setFont(new Font("IMPACT",Font.BOLD,18));
        g.drawString("SCORE: "+score,Utils.CELL_LENGTH*10-13,Utils.CELL_LENGTH*(Utils.NUM_ROWS+1));
        g.drawString("HIGH SCORE: "+highscore,Utils.CELL_LENGTH*9-10,Utils.CELL_LENGTH*(Utils.NUM_ROWS+2));
        g.drawString("LEVEL: "+level,Utils.CELL_LENGTH*17,Utils.CELL_LENGTH*(Utils.NUM_ROWS+2));
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
            g.drawString(String.valueOf(ghostBonusPoints),ghostBonus_x,ghostBonus_y);
        }
        if(drawOneUp){
            g.setColor(Color.PINK);
            g.setFont(new Font(Font.SANS_SERIF,Font.BOLD,12));
            g.drawString("1UP",oneUp_x,oneUp_y);
        }


        if(pacman.isVisible){
           g.drawImage(pacman.getSprite(),pacman.x_location, pacman.y_location, this);
       }

        if(gameOver){
            g.setColor(Color.RED);
            g.setFont(new Font("Impact",Font.BOLD,20));
            g.drawString("GAME   OVER",Utils.CELL_LENGTH*9-10,Utils.CELL_LENGTH*12-3);
        }

        if(drawReady){
            g.setColor(Color.YELLOW);
            g.setFont(new Font("Impact",Font.BOLD,20));
            g.drawString("READY !",Utils.CELL_LENGTH*9+10,Utils.CELL_LENGTH*12-3);
        }
    }

    public void actionPerformed() {
        lastprocessed=System.currentTimeMillis();
        checkTimers();
        checkEntityDelays();
        checkStatus();
        repaint();
    }

    private void checkStatus() {
        if(score>=TimeUtils.ONEUP_SCORE && !extraLifeAdded){
            drawOneUp=true;
            oneUpPaintStamp=System.currentTimeMillis();
            lives++;
            extraLifeAdded=true;
            oneUp_x=pacman.x_location;
            oneUp_y=pacman.y_location;
        }

        if(foodCount==0){
            newLevel();
        }

        if(score>=highscore)
            highscore=score;

        if(!fruitUsed && foodCount<=TimeUtils.FRUIT_VISIBLE_FOODCOUNT1){
            fruitVisible=true;
            fruitUsed=true;
            fruitTimeStamp=System.currentTimeMillis();
        }

        if(fruitVisible && System.currentTimeMillis()-fruitTimeStamp>=TimeUtils.FRUIT_VISIBLE_TIME){
            fruitVisible=false;
        }
    }

    private void checkEntityDelays() {
        if(System.currentTimeMillis()-pacmanLastProcessed>=TimeUtils.PACMAN_DELAY(level,false)){
            pacman.update();
            confirmMove(pacman);
            pacmanLastProcessed=System.currentTimeMillis();
        }

        for(int i=0; i<ghostLastProcessed.length;i++){
            if(System.currentTimeMillis()-ghostLastProcessed[i]>=TimeUtils.GHOST_DELAY(level,ghost[i].mode,ghost[i].inTunnel)){
                ghost[i].update();
                confirmMove(ghost[i]);

                ghostLastProcessed[i]=System.currentTimeMillis();
            }
        }

    }

    private void checkTimers() {
        if(drawFruitPoints && System.currentTimeMillis()-fruitPaintStamp>=TimeUtils.POINTS_VISIBLE_TIME){
            drawFruitPoints=false;
        }

        if(drawGhostPoints && System.currentTimeMillis()-ghostPointsPaintStamp>=TimeUtils.POINTS_VISIBLE_TIME){
            drawGhostPoints=false;
        }

        if(drawOneUp && System.currentTimeMillis()-oneUpPaintStamp>=TimeUtils.POINTS_VISIBLE_TIME){
            drawOneUp=false;
        }
    }

    void confirmMove(Entity entity){
        if(entity.canMove) {
            if(inTunnel(entity)) {
                if(entity.x_location<-Utils.CELL_LENGTH){
                    entity.x_location=Utils.JFRAME_WIDTH+Utils.CELL_LENGTH-10;
                }
                else if(entity.x_location>Utils.JFRAME_WIDTH+Utils.CELL_LENGTH){
                    entity.x_location=-Utils.CELL_LENGTH+10;
                }
            }
            else {
                entity.inTunnel=false;
                if (checkCollision(entity.new_x_direction, entity.new_y_direction, entity)) {  //Checks new direction
                    entity.x_direction = entity.new_x_direction;
                    entity.y_direction = entity.new_y_direction;

                    entity.rotate(entity.new_x_direction, entity.new_y_direction);
                }
                else if (!checkCollision(entity.x_direction, entity.y_direction, entity)) {   //Checks old direction
                    entity.last_x_direction = entity.x_direction;
                    entity.last_y_direction = entity.y_direction;
                    entity.x_direction = 0;
                    entity.y_direction = 0;
                }
                entity.speed = TimeUtils.SPEED;
            }
        entity.x_location += entity.x_direction; //Moves entity in that direction
        entity.y_location += entity.y_direction;

        if(!entity.isPacman)
            checkGhostCollision(entity);
        }
    }

    private boolean inTunnel(Entity entity) {
        if(!entity.isPacman){
            if(entity.y_location==9*Utils.CELL_LENGTH){
                if((entity.x_location<Utils.CELL_LENGTH) || (entity.x_location>19*Utils.CELL_LENGTH)){
                    entity.inTunnel=true;
                    return true;
                }
                else{
                    entity.inTunnel=false;
                    return false;
                }
            }
        }
        else{
            if(entity.y_location==9*Utils.CELL_LENGTH){
                if((entity.x_location<25) || (entity.x_location>Utils.JFRAME_WIDTH-26)){
                    return true;
                }
            }
        }

        return false;
    }

    boolean checkCollision(int x_dir, int y_dir, Entity entity) {
        int [] wallCollisions= new int[4];

        wallCollisions[0]=map[((entity.y_location+y_dir)/ Utils.CELL_LENGTH)][((entity.x_location+x_dir)/ Utils.CELL_LENGTH)]; //top left
        wallCollisions[1]=map[((entity.y_location+y_dir)/ Utils.CELL_LENGTH)][((entity.x_location+Utils.CELL_LENGTH-entity.speed+x_dir)/ Utils.CELL_LENGTH)]; //top right
        wallCollisions[2]=map[((entity.y_location+Utils.CELL_LENGTH-entity.speed+y_dir)/ Utils.CELL_LENGTH)][((entity.x_location+x_dir)/ Utils.CELL_LENGTH)]; //bottom left
        wallCollisions[3]=map[((entity.y_location+Utils.CELL_LENGTH-entity.speed+y_dir)/ Utils.CELL_LENGTH)][((entity.x_location+Utils.CELL_LENGTH-entity.speed+x_dir)/ Utils.CELL_LENGTH)]; //bottom right

        //1.Checks for direction
        //2.Checks tiles in that direction for wall collision
        //3.Checks collision for food if entity is pacman


        if(x_dir==entity.speed){
            if( wallCollisions[1]==1||wallCollisions[3]==1||((wallCollisions[1]==-1||wallCollisions[3]==-1)&& !entity.canGoThroughDoor)) {
                return false;
            }
            else if(entity.isPacman){
                if(wallCollisions[1]==2) {
                    map[((entity.y_location+y_dir)/ Utils.CELL_LENGTH)][((entity.x_location+Utils.CELL_LENGTH-entity.speed+x_dir)/ Utils.CELL_LENGTH)] = 0;
                    score+=10;
                    foodCount--;
                }
                else if(wallCollisions[3]==2) {
                    map[((entity.y_location+Utils.CELL_LENGTH-entity.speed+y_dir)/ Utils.CELL_LENGTH)][((entity.x_location+Utils.CELL_LENGTH-entity.speed+x_dir)/ Utils.CELL_LENGTH)] = 0;
                    score+=10;
                    foodCount--;
                }
                else if(wallCollisions[1]==3) {
                    map[((entity.y_location+y_dir)/ Utils.CELL_LENGTH)][((entity.x_location+Utils.CELL_LENGTH-entity.speed+x_dir)/ Utils.CELL_LENGTH)] = 0;
                    energizerEaten();
                }
                else if(wallCollisions[3]==3) {
                    map[((entity.y_location+Utils.CELL_LENGTH-entity.speed+y_dir)/ Utils.CELL_LENGTH)][((entity.x_location+Utils.CELL_LENGTH-entity.speed+x_dir)/ Utils.CELL_LENGTH)] = 0;
                    energizerEaten();
                }
                else if(wallCollisions[1]==4||wallCollisions[3]==4) {
                    fruitEaten();
                }

            }

        }
        else if(x_dir==-entity.speed){
            if( wallCollisions[0]==1||wallCollisions[2]==1 ||((wallCollisions[0]==-1||wallCollisions[2]==-1)&& !entity.canGoThroughDoor)) {
                return false;
            }
            else if (entity.isPacman){
                if(wallCollisions[0]==2) {
                map[((entity.y_location+y_dir)/ Utils.CELL_LENGTH)][((entity.x_location+x_dir)/ Utils.CELL_LENGTH)] = 0;
                    score+=10;
                    foodCount--;
                }
                else if(wallCollisions[2]==2) {
                map[((entity.y_location+Utils.CELL_LENGTH-entity.speed+y_dir)/ Utils.CELL_LENGTH)][((entity.x_location+x_dir)/ Utils.CELL_LENGTH)] = 0;
                    score+=10;
                    foodCount--;
                }
                 else if(wallCollisions[0]==3) {
                map[((entity.y_location+y_dir)/ Utils.CELL_LENGTH)][((entity.x_location+x_dir)/ Utils.CELL_LENGTH)] = 0;
                energizerEaten();
                }
                 else if(wallCollisions[2]==3) {
                map[((entity.y_location+Utils.CELL_LENGTH-entity.speed+y_dir)/ Utils.CELL_LENGTH)][((entity.x_location+x_dir)/ Utils.CELL_LENGTH)] = 0;
                energizerEaten();
                }
                else if(wallCollisions[0]==4||wallCollisions[2]==4) {
                    fruitEaten();
                }
            }
        }
        else if(y_dir==entity.speed){
            if( wallCollisions[2]==1||wallCollisions[3]==1||((wallCollisions[2]==-1||wallCollisions[3]==-1)&& !entity.canGoThroughDoor)) {
                return false;
            }
            else if(entity.isPacman){
                 if(wallCollisions[2]==2) {
                    map[((entity.y_location+Utils.CELL_LENGTH-entity.speed+y_dir)/ Utils.CELL_LENGTH)][((entity.x_location+x_dir)/ Utils.CELL_LENGTH)] = 0;
                    score+=10;
                     foodCount--;
                }
                else if(wallCollisions[3]==2) {
                    map[((entity.y_location+Utils.CELL_LENGTH-entity.speed+y_dir)/ Utils.CELL_LENGTH)][((entity.x_location+Utils.CELL_LENGTH-entity.speed+x_dir)/ Utils.CELL_LENGTH)] = 0;
                    score+=10;
                     foodCount--;
                }
                else if(wallCollisions[2]==3) {
                    map[((entity.y_location+Utils.CELL_LENGTH-entity.speed+y_dir)/ Utils.CELL_LENGTH)][((entity.x_location+x_dir)/ Utils.CELL_LENGTH)] = 0;
                    energizerEaten();
                }
                else if(wallCollisions[3]==3) {
                    map[((entity.y_location+Utils.CELL_LENGTH-entity.speed+y_dir)/ Utils.CELL_LENGTH)][((entity.x_location+Utils.CELL_LENGTH-entity.speed+x_dir)/ Utils.CELL_LENGTH)] = 0;
                    energizerEaten();
                }
                 else if(wallCollisions[2]==4||wallCollisions[3]==4) {
                     fruitEaten();
                 }
            }

        }
        else if(y_dir==-entity.speed){
            if( wallCollisions[1]==1||wallCollisions[0]==1||((wallCollisions[1]==-1||wallCollisions[0]==-1)&& !entity.canGoThroughDoor)) {
                return false;
            }
            else if(entity.isPacman){
                 if(wallCollisions[1]==2) {
                    map[((entity.y_location+y_dir)/ Utils.CELL_LENGTH)][((entity.x_location+Utils.CELL_LENGTH-entity.speed+x_dir)/ Utils.CELL_LENGTH)] = 0;
                    score+=10;
                     foodCount--;
                }
                else if(wallCollisions[0]==2) {
                    map[((entity.y_location+y_dir)/ Utils.CELL_LENGTH)][((entity.x_location+x_dir)/ Utils.CELL_LENGTH)] = 0;
                    score+=10;
                     foodCount--;
                }
                else if(wallCollisions[1]==3) {
                    map[((entity.y_location+y_dir)/ Utils.CELL_LENGTH)][((entity.x_location+Utils.CELL_LENGTH-entity.speed+x_dir)/ Utils.CELL_LENGTH)] = 0;
                    energizerEaten();
                }
                else if(wallCollisions[0]==3) {
                    map[((entity.y_location+y_dir)/ Utils.CELL_LENGTH)][((entity.x_location+x_dir)/ Utils.CELL_LENGTH)] = 0;
                    energizerEaten();
                }
                 else if(wallCollisions[1]==4||wallCollisions[0]==4) {
                     fruitEaten();
                 }

            }

        }
        return true;
    }

    private void checkGhostCollision(Entity entity){
        int pacman_x= pacman.x_location;
        int pacman_y=pacman.y_location;

        int current_x=entity.x_location;
        int current_y=entity.y_location;

        if(current_x<=pacman_x+5 && current_x>=pacman_x-5 && current_y<=pacman_y+5 && current_y>=pacman_y-5){
            if(!entity.isFrightened && !entity.isDead)
                lostLife();
            else if(entity.isFrightened)
                ghostKilled(entity);
        }

    }

    private void ghostKilled(Entity entity) {
        entity.dies();

        drawGhostPoints=true;
        ghostPointsPaintStamp=System.currentTimeMillis();
        ghostBonus_x=entity.x_location;
        ghostBonus_y=entity.y_location;

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

        score+=ghostBonusPoints;
    }

    private void lostLife() {
        lives--;
        animations.lostLife();
    }

    private void newLevel() {
        level++;
        pacman.setLevel(level);
        for(Ghost g:ghost)
            g.setLevel(level);

        foodCount=Utils.NUM_FOOD;
        fruitUsed=false;
        if(level==2||level==3 || level==5 || level==7 ||level==9 || level==11 ||level==13){
            fruit.nextFruit();
        }

        if(level==1)
            animations.newLevel();
        else
            animations.finishedLevel();
    }

    private void energizerEaten(){
        ghostsEaten=0;
        score+=50;
        foodCount--;
        for(Ghost g:ghost){
            g.makeFrightened();
        }
    }

    private void fruitEaten(){
        if(fruitVisible){
            score+=fruit.getPoints();
            fruitVisible=false;
            drawFruitPoints=true;
            fruitPaintStamp=System.currentTimeMillis();
        }
    }

    public void keyPressed(KeyEvent e) {

        if(e.getKeyCode()==KeyEvent.VK_RIGHT){
            pacman.new_x_direction=pacman.speed;
            pacman.new_y_direction=0;
        }else if(e.getKeyCode()==KeyEvent.VK_LEFT){
            pacman.new_x_direction=-pacman.speed;
            pacman.new_y_direction=0;
        }else if(e.getKeyCode()==KeyEvent.VK_DOWN){
            pacman.new_y_direction=pacman.speed;
            pacman.new_x_direction=0;
        }else if(e.getKeyCode()==KeyEvent.VK_UP){
            pacman.new_y_direction=-pacman.speed;
            pacman.new_x_direction=0;
        }
    }

}
