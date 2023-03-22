import java.awt.*;
import java.util.Random;

import static java.lang.Math.abs;

public class Ghost extends Entity {
    private Random rand=new Random();

    private int id;
    public int mode;
    private int lastMode;
    private Game game;
    private Image [] spriteList;
    private Image sprite;

    private int modeCounter;



    public int []target_location= new int[2];
    private int []current_location= new int[2];
    private int []corner_location= new int[2];
    private long lastProcessed;

    private long lastPro;

    private long frightenedStamp;
    private long flashingStamp;
    private long inHouseStamp;

    private int timeToWaitInHouse;

    private boolean begOfLevel;
    private boolean flashing;
    private boolean choseRandom;
    private boolean justStarted;

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

        direction=1;
        last_direction=direction;

        x_direction=speed;
        y_direction=0;

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
        canMove=true;
        isDead=false;
        flashing=false;
        choseRandom=false;
        sprite=spriteList[3];
        justStarted=true;
    }

    private void initLocation(){
        switch (id) {
            case 0 -> {
                x_location = 10 * Utils.CELL_LENGTH;
                y_location = 7 * Utils.CELL_LENGTH;
                corner_location[0]=20;
                corner_location[1]=0;
                direction=1;
                sprite=spriteList[1];
            }
            case 1 -> {
                x_location = 9 * Utils.CELL_LENGTH;
                y_location = 9 * Utils.CELL_LENGTH;
                corner_location[0]=0;
                corner_location[1]=0;
                direction=1;
                sprite=spriteList[1];
            }
            case 2 -> {
                x_location = 10 * Utils.CELL_LENGTH;
                y_location = 9 * Utils.CELL_LENGTH;
                corner_location[0]=20;
                corner_location[1]=20;
                direction=2;
                sprite=spriteList[2];
            }
            case 3 -> {
                x_location = 11 * Utils.CELL_LENGTH;
                y_location = 9 * Utils.CELL_LENGTH;
                corner_location[0]=0;
                corner_location[1]=20;
                direction=3;
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

    public void update(){
        current_location[0]= x_location/Utils.CELL_LENGTH;
        current_location[1]=y_location/Utils.CELL_LENGTH;

        if(canMove){
            if (flashing && System.currentTimeMillis()-flashingStamp>=TimeUtils.FLASHING_SPEED) {
                flashingStamp=System.currentTimeMillis();
                currentFrame++;
                if (currentFrame >= Utils.FRIGHTENED_IMG.length)
                    currentFrame = 0;

                sprite = Utils.FRIGHTENED_IMG[currentFrame];
            }

            if(mode!=Utils.FRIGHTENED_MODE && mode!=Utils.DIED_MODE && last_direction!=direction){
                sprite=spriteList[direction];
                last_direction=direction;
            }

            updateMode();
            boolean x_accuracy=(x_location%Utils.CELL_LENGTH==0 || x_location%Utils.CELL_LENGTH==1);
            boolean y_accuracy=(y_location%Utils.CELL_LENGTH==0 || y_location%Utils.CELL_LENGTH==1);

            if(game.isAtIntersection(current_location[0],current_location[1]) && x_accuracy &&y_accuracy )
            {
                System.out.println("Intersection Cell:"+current_location[0]+","+current_location[1] );
                System.out.println("Mode"+mode);
                getTarget();
                chooseDirection();;
            }
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

    private void updateNormalMode(){
//        int count=modeCounter;
//        //Goes from Chase to Scatter depending on the time elapsed and the level
//        if(game.level==1){
//            if((modeCounter==1||modeCounter==3) && System.currentTimeMillis()-lastProcessed>=7000){
//                modeCounter++;
//            }
//            else if((modeCounter==5||modeCounter==7) && System.currentTimeMillis()-lastProcessed>=5000){
//                modeCounter++;
//                lastProcessed=System.currentTimeMillis();
//            }
//            else if((modeCounter==2||modeCounter==4||modeCounter==6) && System.currentTimeMillis()-lastProcessed>=20000){
//                modeCounter++;
//                lastProcessed=System.currentTimeMillis();
//            }
//        }
//        else if(game.level==2||game.level==3||game.level==4){
//            if((modeCounter==1||modeCounter==3) && System.currentTimeMillis()-lastProcessed>=7000){
//                modeCounter++;
//                lastProcessed=System.currentTimeMillis();
//            }
//            else if(modeCounter==5 && System.currentTimeMillis()-lastProcessed>=5000){
//                modeCounter++;
//                lastProcessed=System.currentTimeMillis();
//            }
//            else if(modeCounter==7 && System.currentTimeMillis()-lastProcessed>=16){
//                modeCounter++;
//                lastProcessed=System.currentTimeMillis();
//            }
//            else if((modeCounter==2||modeCounter==4) && System.currentTimeMillis()-lastProcessed>=20000){
//                modeCounter++;
//                lastProcessed=System.currentTimeMillis();
//            }
//            else if(modeCounter==6 && System.currentTimeMillis()-lastProcessed>=1033000){
//                modeCounter++;
//                lastProcessed=System.currentTimeMillis();
//            }
//        }
//        else{
//            if((modeCounter==1||modeCounter==3||modeCounter==5) && System.currentTimeMillis()-lastProcessed>=5000){
//                modeCounter++;
//                lastProcessed=System.currentTimeMillis();
//            }
//            else if(modeCounter==7 && System.currentTimeMillis()-lastProcessed>=16){
//                modeCounter++;
//                lastProcessed=System.currentTimeMillis();
//            }
//            else if((modeCounter==2||modeCounter==4) && System.currentTimeMillis()-lastProcessed>=20000){
//                modeCounter++;
//                lastProcessed=System.currentTimeMillis();
//            }
//            else if(modeCounter==6 && System.currentTimeMillis()-lastProcessed>=1037000){
//                modeCounter++;
//                lastProcessed=System.currentTimeMillis();
//            }
//        }
//
//
//        switch (modeCounter) {
//            case 1, 3, 5, 7 -> mode = Utils.SCATTER_MODE;
//            case 2, 4, 6, 8 -> mode = Utils.CHASE_MODE;
//        }
//
//        if(modeCounter>count)
//            writeMode();
        mode=Utils.CHASE_MODE;
    }

    private void updateLeaveHouseMode() {

        if(current_location[0]==10 && current_location[1]==7){
            lastProcessed+=frightenedStamp;
            mode=lastMode;
            writeMode();
            canGoThroughDoor=false;
            begOfLevel=false;
        }
        else{
            if(begOfLevel){
                if((id==2 && game.foodCount<=Utils.NUM_FOOD-TimeUtils.INHOUSE_FOODCOUNT1) || (id==3 && game.foodCount<=Utils.NUM_FOOD-TimeUtils.INHOUSE_FOODCOUNT2))
                    canGoThroughDoor=true;
                else if(id==2 && System.currentTimeMillis()-inHouseStamp>=timeToWaitInHouse || id==3 && System.currentTimeMillis()-inHouseStamp>=timeToWaitInHouse){
                    canGoThroughDoor=true;
                }
            }
            else{
                if(System.currentTimeMillis()-inHouseStamp>=timeToWaitInHouse){
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
            choseRandom=false;
            flashing=false;
            lastProcessed+=frightenedStamp;
            isFrightened=false;
            mode=lastMode;
            sprite=spriteList[0];

            writeMode();
        }
    }

    private void updateDiedMode() {
        if(current_location[0]==9 && current_location[1]==9){
            mode=Utils.LEAVEHOUSE_MODE;
            inHouseStamp=System.currentTimeMillis();
            timeToWaitInHouse=1000;
            isDead=false;
            canGoThroughDoor=false;

            sprite=spriteList[0];

            writeMode();
        }
    }

    private void getTarget(){
        switch (mode) {
            case Utils.LEAVEHOUSE_MODE -> {
                if (canGoThroughDoor) {
                    target_location[0] = 10;
                    target_location[1] = 7;
                } else {
                    if (current_location[0] == 9) {
                        target_location[0] = 13;
                        target_location[1] = 9;
                    } else if (current_location[0] == 11) {
                        target_location[0] = 7;
                        target_location[1] = 9;
                    }
                }
            }
            case Utils.CHASE_MODE -> {
                switch (id) {
                    case 0 -> {
                        makePacmanTarget();
                    }
                    case 1 -> {
                        targetAheadOfPacman(4);
                    }
                    case 2 -> {
                        blueTarget();
                    }
                    case 3 -> {
                        orangeTarget();
                    }
                }
            }
            case Utils.SCATTER_MODE -> {
                target_location[0] = corner_location[0];
                target_location[1] = corner_location[1];
            }
            case Utils.FRIGHTENED_MODE -> {
                if (!choseRandom) {
                    randomTarget();
                    choseRandom = true;
                } else {
                    if (current_location[0] == target_location[0] && current_location[1] == target_location[1])
                        choseRandom = false;
                }
            }
            case Utils.DIED_MODE -> {
                target_location[0] = 9;
                target_location[1] = 9;
            }
        }

    }

    private void makePacmanTarget(){
        target_location[0]= (game.pacman.x_location+(Utils.CELL_LENGTH/2))/Utils.CELL_LENGTH;
        target_location[1]=(game.pacman.y_location+(Utils.CELL_LENGTH/2))/Utils.CELL_LENGTH;
    }

    private void targetAheadOfPacman(int distance) {
        makePacmanTarget();
        if(game.pacman.x_direction==game.pacman.speed ||game.pacman.last_x_direction==game.pacman.speed){
            target_location[0]+=distance;
        }
        else if(game.pacman.x_direction==-game.pacman.speed||game.pacman.last_x_direction==-game.pacman.speed){
            target_location[0]-=distance;
        }
        else if(game.pacman.y_direction==game.pacman.speed||game.pacman.last_y_direction==game.pacman.speed){
            target_location[1]+=distance;
        }
        else if(game.pacman.y_direction==-game.pacman.speed||game.pacman.last_y_direction==-game.pacman.speed){
            target_location[1]-=distance;
        }

    }

    private void blueTarget() {
        targetAheadOfPacman(2);
        int red_x=(game.ghost[0].x_location+(Utils.CELL_LENGTH/2))/Utils.CELL_LENGTH;
        int red_y=(game.ghost[0].y_location+(Utils.CELL_LENGTH/2))/Utils.CELL_LENGTH;
        int x_diff=target_location[0]-red_x;
        int y_diff=target_location[1]-red_y;

        target_location[0]=target_location[0]+x_diff;
        target_location[1]=target_location[1]+y_diff;
    }

    private void orangeTarget() {
        makePacmanTarget();
        int x_diff=current_location[0]-target_location[0];
        int y_diff=current_location[1]-target_location[1];

        if(!(abs(x_diff)+abs(y_diff)>6)){
            target_location[0]=corner_location[0];
            target_location[1]=corner_location[1];
        }
    }

    private void randomTarget() {
            target_location[0]=rand.nextInt(Utils.NUM_COLUMNS);
            target_location[1]=rand.nextInt(Utils.NUM_ROWS);
    }

    private void chooseDirection(){
        int x_diff=current_location[0]-target_location[0];
        int y_diff=current_location[1]-target_location[1];

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

        boolean [] routes= game.availableRoutes(current_location[0],current_location[1],direction);

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
                temp=rand.nextInt(4);
                if(routes[temp])
                    newDirection=temp;
            }
        }

        direction=newDirection;

        directionConverter();
    }
    @Override
    public void dies() {
        mode=Utils.DIED_MODE;
        canGoThroughDoor=true;
        isDead=true;

        sprite=Utils.DEAD_IMG[0];
        timeToWaitInHouse=1500;

        flashing=false;
        isFrightened=false;

        writeMode();
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
        x_location=0;
        y_location=0;
        x_direction=0;
        y_direction=0;
        canMove=false;
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


       // System.out.println(color +" "+ modeType);
    }

    public void hitWall(){
        int numRoutes=0;
        int onlyRoute=-1;

        boolean [] routes= game.availableRoutes(current_location[0],current_location[1],direction);

        for(int i=0; i<4; i++){
            if(routes[i]){
                onlyRoute=i;
                numRoutes++;
            }
        }
        if(numRoutes>1){
            do {
                direction = rand.nextInt(4);
            } while (!routes[direction]);
        }
        else
            direction=onlyRoute;

        directionConverter();
    }

    private void directionConverter(){
        switch(direction){
            case 0:{
                x_direction=0;
                y_direction=-speed;
                break;
            }
            case 1: {
                x_direction=speed;
                y_direction=0;
                break;
            }
            case 2:
            {
                x_direction=0;
                y_direction=speed;
                break;
            }
            case 3:
            {
                x_direction=-speed;
                y_direction=0;
                break;
            }
        }
    }
}
