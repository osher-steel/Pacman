import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;

public class Game extends JPanel{
    public MainPanel parent;
    private Animations animations;
    public Pacman pacman;
    public Ghost[] ghost;
    private Fruit fruit;

    public int [][]map;

    public int score;
    private int highscore;
    public int lives;
    public int foodCount;
    public int level;
    private int ghostsEaten;

    public boolean fruitVisible;
    private boolean fruitUsed;
    private boolean drawFruitPoints;
    private boolean drawGhostPoints;
    private boolean drawOneUp;
    public boolean gameOver;
    public boolean drawReady;
    private boolean extraLifeAdded;

    private long fruitTimeStamp;
    private long fruitPaintStamp;
    private long oneUpPaintStamp;
    private long ghostPointsPaintStamp;

    private long lastprocessed;
    private long pacmanLastProcessed;
    private final long []ghostLastProcessed;

    private int ghostBonus_x,ghostBonus_y;
    private int oneUp_x,oneUp_y;

    private int ghostBonusPoints;

    public Game(MainPanel parent, int highscore){
        this.highscore=highscore;
        this.parent=parent;

        map= new int[21][21];
        copyMap();

        pacman=new Pacman();
        ghost= new Ghost[1];
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

        lastprocessed=System.currentTimeMillis();
        setBackground(Color.BLACK);
    }

    public void copyMap() {
        for(int i=0; i<map[0].length;i++){
            System.arraycopy(Utils.INITIAL_MAP[i], 0, map[i], 0, map.length);
        }
    }
    public boolean isAtIntersection(int x, int y,boolean canGoThroughDoor){

        int numRoutes=0;

        if(map[y][x]!=1){
            if(y!=map.length-1 && map[y+1][x]!=1 && map[y+1][x]!=-1){
                numRoutes++;
            }
            if(y!=0 &&map[y-1][x]!=1&& map[y-1][x]!=-1){
                numRoutes++;
            }
            if(x!=map.length-1 && map[y][x+1]!=1 && map[y+1][x]!=-1){
                numRoutes++;
            }
            if(x!=0 &&map[y][x-1]!=1 && map[y][x-1]!=-1){
                numRoutes++;
            }
        }

        return numRoutes>2;
    }
    public boolean[] availableRoutes(int x, int y,int direction, boolean canGoThroughDoor){

        boolean [] routes= new boolean[4];
        for(boolean r:routes)
            r=false;

        if(y!=map.length-1 && direction!=0 &&  map[y+1][x]!=1 && (map[y+1][x]!=-1|| canGoThroughDoor)){
           routes[2]=true;
        }
        if(y!=0  && direction!=2 && map[y-1][x]!=1 && (map[y-1][x]!=-1|| canGoThroughDoor)){
            routes[0]=true;
        }
        if(x!=map.length-1 && direction!=3 && map[y][x+1]!=1 && (map[y][x+1]!=-1|| canGoThroughDoor)) {
            routes[1]=true;
        }
        if(x!=0  && direction!=1 && map[y][x-1]!=1 && (map[y][x-1]!=-1|| canGoThroughDoor)){
            routes[3]=true;
        }

        return routes;
    }
    @Override
    public void paintComponent(Graphics g){
        super.paintComponent(g);


        g.setColor(Color.RED);
        g.fillRect(ghost[0].target_location[0] * Utils.CELL_LENGTH, ghost[0].target_location[1] * Utils.CELL_LENGTH, Utils.CELL_LENGTH, Utils.CELL_LENGTH);



        //Drawing the map
        for(int i=0; i<map.length;i++){
            for(int j=0; j<map[0].length; j++){
                switch (map[i][j]) {
                    case 0,2->{
                        g.setColor(Color.WHITE);
                        g.setFont(new Font(Font.SERIF, Font.PLAIN,10));
                        g.drawString(i+","+j,j*Utils.CELL_LENGTH,i*Utils.CELL_LENGTH+Utils.CELL_LENGTH);
                    }
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
//                    case 2 -> {
//                        if(!gameOver){
//                            g.setColor(Color.WHITE);
//                            g.fillOval(j * Utils.CELL_LENGTH + (Utils.CELL_LENGTH * 2 / 5),
//                                    i * Utils.CELL_LENGTH +(Utils.CELL_LENGTH * 2 / 5),
//                                    Utils.CELL_LENGTH / 5, Utils.CELL_LENGTH / 5);
//                        }
//                    }
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

//        for(int i=0; i<intersection_map.length; i++){
//            for(int j=0; j<intersection_map[i].length;j++){
//                if(intersection_map[j][i]==1 || intersection_map[j][i]==-1){
//                    g.setColor(Color.GREEN);
//                    g.fillRect(j * Utils.CELL_LENGTH,
//                            i * Utils.CELL_LENGTH, Utils.CELL_LENGTH, Utils.CELL_LENGTH);
//                }
//            }
//        }

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

        if(fruitVisible && System.currentTimeMillis()-fruitTimeStamp>=TimeUtils.FRUIT_VISIBLE_TIME){
            fruitVisible=false;
        }
    }
    private void checkEntityDelays() {
        if(System.currentTimeMillis()-pacmanLastProcessed>=TimeUtils.PACMAN_DELAY(level,false)){
            pacmanMove();
            pacmanLastProcessed=System.currentTimeMillis();
        }

        for(int i=0; i<ghostLastProcessed.length;i++){
            if(System.currentTimeMillis()-ghostLastProcessed[i]>=TimeUtils.GHOST_DELAY(level,ghost[i].mode,ghost[i].inTunnel)){
                ghostMove(ghost[i]);
                ghostLastProcessed[i]=System.currentTimeMillis();
            }
        }

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
    }

    private void pacmanMove(){
        pacman.update();

        if (pacman.canMove) {
            if(inTunnel(pacman)) {
                if(pacman.x_location<-Utils.CELL_LENGTH/2){
                    pacman.x_location=Utils.JFRAME_WIDTH+Utils.CELL_LENGTH/2;
                }
                else if(pacman.x_location>Utils.JFRAME_WIDTH+Utils.CELL_LENGTH/2){
                    pacman.x_location=-Utils.CELL_LENGTH/2;
                }
            }
            else {
                pacman.inTunnel=false;

                if (checkCollision(pacman.new_x_direction, pacman.new_y_direction, pacman)) {  //Checks new direction
                    pacman.x_direction = pacman.new_x_direction;
                    pacman.y_direction = pacman.new_y_direction;

                    pacman.rotate(pacman.new_x_direction, pacman.new_y_direction);
                }
                else if (!checkCollision(pacman.x_direction, pacman.y_direction, pacman)) {   //Checks old direction
                    pacman.last_x_direction = pacman.x_direction;
                    pacman.last_y_direction = pacman.y_direction;
                    pacman.x_direction = 0;
                    pacman.y_direction = 0;
                }
                pacman.speed = TimeUtils.SPEED;
            }
            pacman.x_location += pacman.x_direction;
            pacman.y_location += pacman.y_direction;
        }
    }

    private void ghostMove(Ghost ghost){
        if(ghost.canMove){
            if(inTunnel(ghost)) {
                if(ghost.x_location<-Utils.CELL_LENGTH/2){
                    ghost.x_location=Utils.JFRAME_WIDTH+Utils.CELL_LENGTH/2;
                }
                else if(ghost.x_location>Utils.JFRAME_WIDTH+Utils.CELL_LENGTH/2){
                    ghost.x_location=-Utils.CELL_LENGTH/2;
                }
            }
            else{
                ghost.inTunnel=false;
            }

            ghost.update();

            if (!ghost.inTunnel && !checkCollision(ghost.x_direction,ghost.y_direction, ghost)) {   //Checks old direction
                ghost.hitWall();
            }

            ghost.x_location += ghost.x_direction; //Moves entity in that direction
            ghost.y_location += ghost.y_direction;

            checkGhostCollision(ghost);
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

    public boolean checkCollision(int x_dir, int y_dir, Entity entity) {
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

    private void checkGhostCollision(Ghost ghost){
        int pacman_x= pacman.x_location;
        int pacman_y=pacman.y_location;

        int current_x=ghost.x_location;
        int current_y=ghost.y_location;

        if(current_x<=pacman_x+5 && current_x>=pacman_x-5 && current_y<=pacman_y+5 && current_y>=pacman_y-5){
            if(!ghost.isFrightened && !ghost.isDead)
                lostLife();
            else if(ghost.isFrightened)
                ghostKilled(ghost);
        }

    }

    private void ghostKilled(Ghost ghost) {
        ghost.dies();

        drawGhostPoints=true;
        ghostPointsPaintStamp=System.currentTimeMillis();
        ghostBonus_x=ghost.x_location;
        ghostBonus_y=ghost.y_location;

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
        for(Ghost g:ghost){
            g.makeFrightened();
        }

        ghostsEaten=0;
        score+=50;
        foodCount--;
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
