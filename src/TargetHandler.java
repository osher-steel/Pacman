import java.awt.*;
import java.util.Random;

import static java.lang.Math.abs;
import static java.lang.Math.multiplyHigh;

public class TargetHandler {
    public static Point REDGHOST_LOC;
    public static Point PACMAN_LOC;
    public static Random rand= new Random();

    public static void setRedGhostLoc(Point loc){
        REDGHOST_LOC=loc;
    }
    public static void setPacmanLoc(Point loc){
        PACMAN_LOC=loc;
    }
    public static Point getTarget(Ghost ghost, Pacman pacman, Game game){
        switch (ghost.mode) {
            case Utils.LEAVEHOUSE_MODE -> {
                if (ghost.canGoThroughDoor) {
                    return new Point(10, 7);
                }
            }
            case Utils.CHASE_MODE -> {
                switch (ghost.id) {
                    case 0 -> {
                        return makePacmanTarget(pacman);
                    }
                    case 1 -> {
                        return targetAheadOfPacman(pacman, 4);
                    }
                    case 2 -> {
                        return blueTarget(ghost, pacman, game);
                    }
                    case 3 -> {
                        return orangeTarget(ghost, pacman);
                    }
                }
            }
            case Utils.SCATTER_MODE -> {
                return ghost.grid_corner;
            }
            case Utils.FRIGHTENED_MODE -> {
                return new Point(randomTarget(ghost));
            }
            case Utils.DIED_MODE -> {
                if (ghost.grid_current.x == 10 && ghost.grid_current.y == 7) {
                    ghost.canGoThroughDoor = true;
                    return new Point(10, 9);
                } else if (ghost.grid_current.x == 10 && ghost.grid_current.y == 9) {
                    return new Point(9, 9);
                } else {
                    return new Point(10, 7);
                }
            }
        }
        return ghost.grid_target;
    }
    public static Point makePacmanTarget(Pacman pacman){
        Point temp= new Point();
        temp.x= (pacman.coordinates.x+(Utils.CELL_LENGTH/2))/Utils.CELL_LENGTH;
       temp.y=(pacman.coordinates.y+(Utils.CELL_LENGTH/2))/Utils.CELL_LENGTH;

       return temp;
    }
    public static Point targetAheadOfPacman(Pacman pacman, int distance) {
        Point temp;

        temp=makePacmanTarget(pacman);
        if(pacman.movement.x==pacman.speed ||pacman.last_movement.x==pacman.speed){
           temp.x+=distance;
        }
        else if(pacman.movement.x==-pacman.speed||pacman.last_movement.x==-pacman.speed){
            temp.x-=distance;
        }
        else if(pacman.movement.y==pacman.speed||pacman.last_movement.y==pacman.speed){
            temp.y+=distance;
        }
        else if(pacman.movement.y==-pacman.speed||pacman.last_movement.y==-pacman.speed){
            temp.y-=distance;
        }

        return temp;
    }
    public static Point blueTarget(Ghost ghost, Pacman pacman,Game game) {
        Point point=targetAheadOfPacman(pacman,2);

        int red_x=(game.ghost[0].coordinates.x+(Utils.CELL_LENGTH/2))/Utils.CELL_LENGTH;
        int red_y=(game.ghost[0].coordinates.y+(Utils.CELL_LENGTH/2))/Utils.CELL_LENGTH;
        int x_diff=ghost.grid_target.x-red_x;
        int y_diff=ghost.grid_target.y-red_y;

        point.x=point.x+x_diff;
        point.y=point.y+y_diff;

        if(point.x>Utils.NUM_COLUMNS-1)
            point.x=Utils.NUM_COLUMNS-1;
        if(point.y>Utils.NUM_ROWS-1)
            point.y=Utils.NUM_ROWS-1;
        if(point.x<0)
            point.x=0;
        if(point.y<0)
            point.y=0;


        return point;
    }
    public static Point orangeTarget(Ghost ghost, Pacman pacman) {
        int x_diff=ghost.grid_current.x-ghost.grid_target.x;
        int y_diff=ghost.grid_current.y-ghost.grid_target.y;

        if(!(abs(x_diff)+abs(y_diff)>6)){
          return ghost.grid_corner;
        }
        else
            return makePacmanTarget(pacman);

    }
    public static Point randomTarget(Ghost ghost) {
        return new Point( rand.nextInt(Utils.NUM_COLUMNS),
                rand.nextInt(Utils.NUM_ROWS));
    }
}
