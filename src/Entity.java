import java.awt.*;

public abstract class Entity {
    public boolean inTunnel;

    public int x_location, y_location;
    public int x_direction, y_direction;
    public int last_x_direction, last_y_direction;

    public int direction;
    public int last_direction;

    public int speed=TimeUtils.SPEED;
    public int level;

    public int currentFrame;
    public int frameCounter;
    public int frameSpeed;

    public boolean isPacman;
    public boolean isVisible;
    public boolean isDead;
    public boolean isFrightened;

    public boolean canGoThroughDoor;
    public boolean canMove;

    public abstract void dies();
    public abstract Image getSprite();

    public  void rotate(int x,int y){
        if(x==-speed){
            direction=3;
        }
        else if(x==speed){
            direction=1;
        }
        else if(y==-speed){
            direction=0;
        }
        else if(y==speed){
            direction=2;
        }
    }

    public void setLevel(int level){
        this.level=level;
    }
}
