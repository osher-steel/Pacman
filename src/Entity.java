import java.awt.*;

public abstract class Entity {
    public boolean inTunnel;

    public Point coordinates= new Point();
    public Point movement= new Point();
    public Point last_movement= new Point();

    public int compass_direction;
    public int compass_last_direction;

    public int speed=TimeUtils.SPEED;
    public int level;

    public int currentFrame;
    public int frameCounter;
    public int frameSpeed;

    public boolean isPacman;
    public boolean isVisible;
    public boolean isDead;

    public boolean canGoThroughDoor;
    public boolean canMove;

    public abstract void dies();
    public abstract Image getSprite();
    public  void rotate(int x,int y){
        if(x==-speed){
            compass_direction=3;
        }
        else if(x==speed){
            compass_direction=1;
        }
        else if(y==-speed){
            compass_direction=0;
        }
        else if(y==speed){
            compass_direction=2;
        }
    }
}
