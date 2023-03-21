import java.awt.*;

public class Pacman extends Entity{
    private Image sprite;
    private Image[][] spriteList;
    private Image[] spriteDeathList;
    int deathCounter;
    boolean deathAnimation;
    int deathSpeed;


    Pacman(){
        isPacman=true;
        isVisible=false;
        isFrightened=false;
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
        x_location=10*Utils.CELL_LENGTH;
        y_location=15*Utils.CELL_LENGTH;

        direction=1;
        last_direction=direction;
        x_direction=1;
        y_direction=0;
        rotate(x_direction,y_direction);

        new_x_direction=0;
        new_y_direction=0;
        canMove=true;
        isVisible=true;
    }

    public void update() {

        if (canMove) {
            frameCounter++;

            if (frameCounter >= frameSpeed) {
                frameCounter = 0;
                currentFrame++;

                if (currentFrame > 2)
                    currentFrame = 0;

                sprite = spriteList[currentFrame][direction];
            }

            if (last_direction != direction) {
                sprite = spriteList[currentFrame][direction];
                last_direction = direction;
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
