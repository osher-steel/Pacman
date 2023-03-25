import java.awt.*;
import java.util.Random;

import static java.lang.Math.abs;

public class Ghost extends Entity {
    private final Game game;

    public Point grid_target= new Point();
    public Point grid_current= new Point();
    public Point grid_corner= new Point();

    private int speed=TimeUtils.SPEED;
    public final int id;
    public int mode;
    private int lastMode;
    private final Image [] spriteList;
    private Image sprite;

    private int modeCounter;



    private long lastProcessed;

    private long lastPro;

    private long frightenedStamp;
    private long flashingStamp;
    private long inHouseStamp;

    private int timeToWaitInHouse;

    private boolean begOfLevel;
    private boolean flashing;
    public boolean isFrightened;

    public boolean moveNow;

//    Ghosts are forced to reverse direction by the system anytime the mode changes from: chase-to-scatter,
//    chase-to-frightened, scatter-to-chase, and scatter-to-frightened.
//    Ghosts do not reverse direction when changing back from frightened to chase or scatter modes.


    Ghost(int id, Game game){
        inTunnel=false;
        this.game=game;
        this.id=id;

        spriteList= new Image[4];

        currentFrame=0;
        frameCounter=0;
        frameSpeed=2;

        compass_direction=1;
        compass_last_direction=compass_direction;

        movement.x=speed;
        movement.y=0;

        isPacman=false;

        loadSprites();
        init(true);

        lastPro=System.currentTimeMillis();
    }

    private void loadSprites() {
        for(int i=0; i<spriteList.length;i++){
            spriteList[i]=Utils.loadImage(String.valueOf(i),"ghosts/"+id);
        }
    }

    //----------------------------------INIT----------------------------------------//
    public void init(boolean begOfLevel){
        this.begOfLevel=begOfLevel;

        initVars();
        initLocation();
        initMode(begOfLevel);
        update();
    }

    private void initVars() {
        if(id==0 || id==1)
            begOfLevel=false;

        isFrightened=false;
        canGoThroughDoor=false;
        isVisible=true;
        isDead=false;
        flashing=false;
        sprite=spriteList[3];

        moveNow=false;
    }

    private void initLocation(){
        switch (id) {
            case 0 -> {
                coordinates.x = 10 * Utils.CELL_LENGTH;
                coordinates.y = 7 * Utils.CELL_LENGTH;
                grid_corner.x=20;
                grid_corner.y=0;
                compass_direction=1;
                sprite=spriteList[1];
                TargetHandler.setRedGhostLoc(coordinates);
            }
            case 1 -> {
                coordinates.x = 10 * Utils.CELL_LENGTH;
                coordinates.y = 9 * Utils.CELL_LENGTH;
                grid_corner.x=0;
                grid_corner.y=0;
                compass_direction=1;
                sprite=spriteList[1];
            }
            case 2 -> {
                coordinates.x = 9 * Utils.CELL_LENGTH;
                coordinates.y = 9 * Utils.CELL_LENGTH;
                grid_corner.x=20;
                grid_corner.y=20;
                compass_direction=2;
                sprite=spriteList[2];
            }
            case 3 -> {
                coordinates.x = 11 * Utils.CELL_LENGTH;
                coordinates.y = 9 * Utils.CELL_LENGTH;
                grid_corner.x=0;
                grid_corner.y=20;
                compass_direction=3;
                sprite=spriteList[0];
            }
        }
    }

    private void initMode(boolean begOfLevel) {
        inHouseStamp=System.currentTimeMillis();
        lastProcessed=System.currentTimeMillis();
        modeCounter=1;
        lastMode=Utils.SCATTER_MODE;

        if(id==0)
            mode=Utils.SCATTER_MODE;
        else{
            mode=Utils.LEAVEHOUSE_MODE;

            if(id==1)
                canGoThroughDoor=true;

            if(!begOfLevel){
                timeToWaitInHouse=3000;
            }

            else{
                if(id==2)
                    timeToWaitInHouse=TimeUtils.INHOUSE_TIME1;
                else if(id==3)
                    timeToWaitInHouse=TimeUtils.INHOUSE_TIME2;
            }
        }

        writeMode();
    }

    //----------------------------------UPDATE----------------------------------------//
    public void update() {
        if(canMove){
            grid_current.x = coordinates.x / Utils.CELL_LENGTH;
            grid_current.y = coordinates.y / Utils.CELL_LENGTH;

            updateFrame();
            updateMode();

            if (MAPHANDLER.inTunnel(this)) {
                inTunnel=true;
                if(mode!=Utils.DIED_MODE)
                    speed=1;

                if (coordinates.x < -Utils.CELL_LENGTH / 2)
                    coordinates.x = Utils.JFRAME_WIDTH + Utils.CELL_LENGTH / 2;
                else if (coordinates.x > Utils.JFRAME_WIDTH + Utils.CELL_LENGTH / 2)
                    coordinates.x = -Utils.CELL_LENGTH / 2;

            } else{
                speed=2;
                if(canMakeDecision()){
                    grid_target=TargetHandler.getTarget(this,game.pacman,game);
                    chooseDirection();
                    moveNow=false;
                }

                if (!MAPHANDLER.checkCollision(movement.x, movement.y, this)) {
                    hitWall();
                }
                MAPHANDLER.checkGhostCollision(this, game.pacman);
            }

            if (id == 0)
                TargetHandler.setRedGhostLoc(coordinates);

            coordinates.x += movement.x;
            coordinates.y += movement.y;
        }
    }
    private void updateFrame() {
        //Updates flashing frame
        if (flashing && System.currentTimeMillis()-flashingStamp>=TimeUtils.FLASHING_SPEED) {
            flashingStamp=System.currentTimeMillis();

            currentFrame++;
            if (currentFrame >= Utils.FRIGHTENED_IMG.length)
                currentFrame = 0;

            sprite = Utils.FRIGHTENED_IMG[currentFrame];
        }

        //Updates frame
        if(mode!=Utils.FRIGHTENED_MODE && mode!=Utils.DIED_MODE && compass_last_direction!=compass_direction){
            sprite=spriteList[compass_direction];
            compass_last_direction=compass_direction;
        }
    }

    private void updateMode() {
        switch (mode) {
            case Utils.CHASE_MODE, Utils.SCATTER_MODE -> updateNormalMode();
            case Utils.LEAVEHOUSE_MODE -> updateLeaveHouseMode();
            case Utils.FRIGHTENED_MODE -> updateFrightenedMode();
            case Utils.DIED_MODE -> updateDiedMode();
        }
    }

    private boolean canMakeDecision() {
        //Only want to make a decision once per tile when the ghost first enters the tile
        //Since the ghost moves by 2 pixels, modulo function can give a 0 or a 1

        boolean x_accuracy=(coordinates.x%Utils.CELL_LENGTH==0 || coordinates.x%Utils.CELL_LENGTH==1);
        boolean y_accuracy=(coordinates.y%Utils.CELL_LENGTH==0 || coordinates.y%Utils.CELL_LENGTH==1);

        boolean isAtIntersection=MAPHANDLER.isAtIntersection(grid_current);

        if(grid_current.x==10 && grid_current.y==7 && mode==Utils.DIED_MODE)
            isAtIntersection=true;
        if(grid_current.x==10 && grid_current.y==9 && (mode==Utils.LEAVEHOUSE_MODE || mode==Utils.DIED_MODE))
            isAtIntersection=true;

        return ((isAtIntersection && x_accuracy && y_accuracy) || moveNow);
    }
    private void updateNormalMode(){
        int count=modeCounter;
        //Goes from Chase to Scatter depending on the time elapsed and the level
        if(game.level==1){
//            if(modeCounter==3){
//                System.out.println(System.currentTimeMillis()-lastProcessed);
//            }
            if((modeCounter==1||modeCounter==3) && System.currentTimeMillis()-lastProcessed>=7000){
                modeCounter++;
            }
            else if((modeCounter==5) && System.currentTimeMillis()-lastProcessed>=5000){
                modeCounter++;
                lastProcessed=System.currentTimeMillis();
            }
            else if((modeCounter==2||modeCounter==4) && System.currentTimeMillis()-lastProcessed>=20000){
                modeCounter++;
                lastProcessed=System.currentTimeMillis();
            }
        }
        else if(game.level==2||game.level==3||game.level==4){
            if((modeCounter==1||modeCounter==3) && System.currentTimeMillis()-lastProcessed>=7000){
                modeCounter++;
                lastProcessed=System.currentTimeMillis();
            }
            else if(modeCounter==5 && System.currentTimeMillis()-lastProcessed>=5000){
                modeCounter++;
                lastProcessed=System.currentTimeMillis();
            }
            else if((modeCounter==2||modeCounter==4) && System.currentTimeMillis()-lastProcessed>=20000){
                modeCounter++;
                lastProcessed=System.currentTimeMillis();
            }
        }
        else{
            if((modeCounter==1||modeCounter==3||modeCounter==5) && System.currentTimeMillis()-lastProcessed>=5000){
                modeCounter++;
                lastProcessed=System.currentTimeMillis();
            }
            else if((modeCounter==2||modeCounter==4) && System.currentTimeMillis()-lastProcessed>=20000){
                modeCounter++;
                lastProcessed=System.currentTimeMillis();
            }
        }


        switch (modeCounter) {
            case 1, 3, 5, 7 -> mode = Utils.SCATTER_MODE;
            case 2, 4, 6, 8 -> mode = Utils.CHASE_MODE;
        }

        if(modeCounter>count)
            writeMode();
    }

    private void updateLeaveHouseMode() {

        if(grid_current.x==10 && grid_current.y==7){
            lastProcessed+=2000;
            mode=lastMode;
            writeMode();
            canGoThroughDoor=false;
            begOfLevel=false;
        }
        else{
            if(begOfLevel){
                if((id==2 && MAPHANDLER.FOOD_COUNT<=Utils.NUM_FOOD-TimeUtils.INHOUSE_FOODCOUNT1) || (id==3 && MAPHANDLER.FOOD_COUNT<=Utils.NUM_FOOD-TimeUtils.INHOUSE_FOODCOUNT2))
                    canGoThroughDoor=true;
                else if(id==2 && System.currentTimeMillis()-inHouseStamp>=timeToWaitInHouse || id==3 && System.currentTimeMillis()-inHouseStamp>=timeToWaitInHouse){
                    canGoThroughDoor=true;
                }
            }
            else{
                if(System.currentTimeMillis()-inHouseStamp>=TimeUtils.INHOUSE_AFTER_DEATH_TIME){
                    canGoThroughDoor=true;
                }
            }
        }
    }

    private void updateFrightenedMode() {
        if(System.currentTimeMillis()-frightenedStamp>=TimeUtils.FLASHING_TIME(level) && !flashing){
            flashing=true;
            flashingStamp=System.currentTimeMillis();
        }

        if(System.currentTimeMillis()-frightenedStamp>=TimeUtils.FRIGHTENED_TIME(level)){
            flashing=false;
            lastProcessed+=(System.currentTimeMillis()-frightenedStamp);
            System.out.println(System.currentTimeMillis()-frightenedStamp);
            isFrightened=false;
            mode=lastMode;
            sprite=spriteList[0];

            writeMode();
        }
    }

    private void updateDiedMode() {
        if(grid_current.x==9 && grid_current.y==9){
            lastProcessed+=3000;
            compass_direction=1;
            mode=Utils.LEAVEHOUSE_MODE;
            inHouseStamp=System.currentTimeMillis();
            timeToWaitInHouse=1000;
            isDead=false;
            canGoThroughDoor=false;
            sprite=spriteList[0];

            writeMode();
        }

    }

    private void chooseDirection(){
        int x_diff=grid_current.x-grid_target.x;
        int y_diff=grid_current.y-grid_target.y;

        boolean x_diff_isHigher=(abs(x_diff)>abs((y_diff)));

        int [] idealDirection= new int[4];
        for(int i:idealDirection){
            i=0;
        }

        //Gets target vertical and horizontal direction
        if(y_diff>0){
            if(x_diff_isHigher){
                idealDirection[0]=1;
            }
            else{
                idealDirection[0]=2;
            }
        }
        else if(y_diff<0){
            if(x_diff_isHigher){
                idealDirection[2]=1;
            }
            else{
                idealDirection[2]=2;
            }
        }


        if(x_diff>0){
            if(x_diff_isHigher){
                idealDirection[3]=2;
            }
            else{
                idealDirection[3]=1;
            }
        }
        else if(x_diff<0){
            if(x_diff_isHigher){
                idealDirection[1]=2;
            }
            else{
                idealDirection[1]=1;
            }
        }

        boolean [] routes= MAPHANDLER.availableRoutes(grid_current,compass_direction,canGoThroughDoor,false);

        int newDirection=-1;

        for(int i=0; i<4; i++){
            if(routes[i] && idealDirection[i]==2){
                newDirection=i;
            }
        }
        if(newDirection==-1){
            for(int i=0; i<4; i++){
                if(routes[i] && idealDirection[i]==2){
                    newDirection=i;
                }
            }
        }
        if(newDirection==-1){
            int temp;
            while(newDirection==-1){
                temp=TargetHandler.rand.nextInt(4);
                if(routes[temp])
                    newDirection=temp;
            }
        }

        compass_direction=newDirection;
        directionConverter();
    }


    //----------------------------------RESULT EVENTS----------------------------------------//
    @Override
    public void dies() {
        game.ghostKilled(this);
        mode=Utils.DIED_MODE;
        isDead=true;

        sprite=Utils.DEAD_IMG[0];
        timeToWaitInHouse=1500;

        flashing=false;
        isFrightened=false;

        writeMode();
    }
    public void killedPacman() {
        game.lostLife();
    }
    public void makeFrightened(){
        //Cannot make frightened if ghost is in the house or is dead
        if(mode!=Utils.LEAVEHOUSE_MODE && mode !=Utils.DIED_MODE){
            if(mode!=Utils.FRIGHTENED_MODE){
                lastMode=mode;
            }
            mode=Utils.FRIGHTENED_MODE;
            sprite=Utils.FRIGHTENED_IMG[0];

            isFrightened=true;
            flashing=false;
            frightenedStamp=System.currentTimeMillis();
        }

        writeMode();
    }
    public void makeInvisible(){
        isVisible=false;
        coordinates.x=0;
        coordinates.y=0;
        movement.x=0;
        movement.y=0;
    }

    @Override
    public Image getSprite(){
        return sprite;
    }

    public int getMode(){
        return mode;
    }

    private void writeMode(){
        String color="null";
        String modeType="null";

        switch(id){
            case 0-> color="red";
            case 1-> color="pink";
            case 2-> color="blue";
            case 3-> color="orange";
        }

        if(mode==Utils.SCATTER_MODE)
            modeType="scatter";
        else if(mode==Utils.CHASE_MODE)
            modeType="chase";
        else if(mode==Utils.FRIGHTENED_MODE)
            modeType="frightened";
        else if(mode==Utils.DIED_MODE)
            modeType="dead";
        else if(mode==Utils.LEAVEHOUSE_MODE)
            modeType="leave house";


        System.out.println("Level:" +game.level+" Mode: "+ modeType + "Modecounter: "+modeCounter);
    }

    public void hitWall(){
        int numRoutes=0;
        int onlyRoute=-1;

        boolean canGoBackWards=(mode==Utils.LEAVEHOUSE_MODE ||canGoThroughDoor);
        boolean [] routes= MAPHANDLER.availableRoutes(grid_current,compass_direction,canGoThroughDoor,canGoBackWards);

        for(int i=0; i<4; i++){
            if(routes[i]){
                onlyRoute=i;
                numRoutes++;
            }
        }
        if(numRoutes!=1){
            do {
                compass_direction = TargetHandler.rand.nextInt(4);
            } while (!routes[compass_direction]);
        }
        else
            compass_direction=onlyRoute;

        directionConverter();
    }

    private void directionConverter(){
        switch(compass_direction){
            case 0:{
               movement.x=0;
               movement.y=-speed;
                break;
            }
            case 1: {
               movement.x=speed;
               movement.y=0;
                break;
            }
            case 2:
            {
               movement.x=0;
                movement.y=speed;
                break;
            }
            case 3:
            {
               movement.x=-speed;
               movement.y=0;
                break;
            }
        }
    }

}
