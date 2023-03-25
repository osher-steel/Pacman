import java.awt.*;

public class Pacman extends Entity{
    public  int speed=TimeUtils.SPEED;
    private Image sprite;
    private Image[][] spriteList;
    private Image[] spriteDeathList;

    Point new_movement= new Point();

    int deathCounter;
    boolean deathAnimation;
    int deathSpeed;


    Pacman(){
        isPacman=true;
        isVisible=false;
        deathAnimation=false;
        spriteList=new Image[3][4];
        spriteDeathList=new Image[13];

        currentFrame=0;
        frameCounter=0;
        deathCounter=0;
        frameSpeed=10;
        deathSpeed=3;

        loadSprites();
        init();
    }

    private void loadSprites() {
        for(int i=0; i<4;i++){
            spriteList[0][i]=Utils.loadImage("1","pacman/0");
        }

        for(int i=1; i<spriteList.length;i++){
            for(int j=0; j<spriteList[i].length;j++){
                spriteList[i][j]=Utils.loadImage(String.valueOf(j),"pacman/"+i);
            }
        }

        for(int i=0; i<spriteDeathList.length;i++){
            spriteDeathList[i]=Utils.LOAD_PACMAN_DEATH(i);
        }


    }

    public void deathAnimation(){
        canMove=false;
        frameCounter=0;
        deathAnimation=true;

    }

    public void init(){
        sprite=spriteList[1][0];
        coordinates.x=10*Utils.CELL_LENGTH;
        coordinates.y=15*Utils.CELL_LENGTH;

        compass_direction=1;
        compass_last_direction=compass_direction;
        movement.x=speed;
        movement.y=0;
        rotate(movement.x,movement.y);

        new_movement.x=speed;
        new_movement.y=0;
        canMove=true;
        isVisible=true;
    }

    public void updateFrame(boolean endOfLevel) {

        if (canMove) {
            frameCounter++;

            if (frameCounter >= frameSpeed) {
                frameCounter = 0;
                currentFrame++;

                if (currentFrame > 2)
                    currentFrame = 0;

                sprite = spriteList[currentFrame][compass_direction];
            }

            if (compass_last_direction != compass_direction) {
                sprite = spriteList[currentFrame][compass_direction];
                compass_last_direction = compass_direction;
            }
        }
        else if(deathAnimation){
            if(frameCounter>=deathSpeed){
                sprite=spriteDeathList[deathCounter];

                deathCounter++;
                if(deathCounter==spriteDeathList.length){
                    deathAnimation=false;
                    deathCounter=0;
                }
                frameCounter=0;
            }
            frameCounter++;
        }
        else if(endOfLevel)
            sprite=spriteList[0][0];
    }

    public void update(){
        updateFrame(false);

        if (canMove) {
            if(MAPHANDLER.inTunnel(this)) {
                if(coordinates.x<-Utils.CELL_LENGTH/2){
                    coordinates.x=Utils.JFRAME_WIDTH+Utils.CELL_LENGTH/2;
                }
                else if(coordinates.x>Utils.JFRAME_WIDTH+Utils.CELL_LENGTH/2){
                    coordinates.x=-Utils.CELL_LENGTH/2;
                }
            }
            else {
               inTunnel=false;

                if (MAPHANDLER.checkCollision(new_movement.x, new_movement.y, this)) {  //Checks new direction
                    movement.x = new_movement.x;
                    movement.y = new_movement.y;

                    rotate(new_movement.x, new_movement.y);
                }
                else if (!MAPHANDLER.checkCollision(movement.x, movement.y, this)) {   //Checks old direction
                    last_movement.x = movement.x;
                    last_movement.y = movement.y;
                   movement.x = 0;
                   movement.y = 0;
                }
                speed = TimeUtils.SPEED;
            }
            coordinates.x += movement.x;
            coordinates.y += movement.y;

            TargetHandler.setPacmanLoc(coordinates);
        }
    }
    @Override
    public void dies() {

    }

    @Override
    public Image getSprite(){
        return sprite;
    }

    public void setFrameSpeed(int frameSpeed) {
        this.frameSpeed = frameSpeed;
    }

}
