import java.awt.*;

public class MAPHANDLER {
    static int [][]MAP= new int[21][21];;

    static int SCORE=0;
    static int ENERGIZER_COUNT;
    static int FOOD_COUNT;
    static boolean FRUIT_EATEN;
    static boolean FRUIT_VISIBLE;
    static boolean FRUIT_APPEARED;

    public static void resetValues(){
        ENERGIZER_COUNT=4;
        FOOD_COUNT=Utils.NUM_FOOD;
        FRUIT_EATEN=false;
        FRUIT_VISIBLE=false;
        FRUIT_APPEARED=false;
    }
    public static void copyMAP() {
        for(int i=0; i<MAP[0].length;i++){
            System.arraycopy(Utils.INITIAL_MAP[i], 0, MAP[i], 0, MAP.length);
        }
    }
    public static boolean isAtIntersection(Point grid){
        int x,y;
        x=grid.x;
        y=grid.y;

        int numRoutes=0;

        if(MAP[y][x]!=1){
            if(y!=MAP.length-1 && MAP[y+1][x]!=1 && MAP[y+1][x]!=-1){
                numRoutes++;
            }
            if(y!=0 &&MAP[y-1][x]!=1&& MAP[y-1][x]!=-1){
                numRoutes++;
            }
            if(x!=MAP.length-1 && MAP[y][x+1]!=1 && MAP[y+1][x]!=-1){
                numRoutes++;
            }
            if(x!=0 &&MAP[y][x-1]!=1 && MAP[y][x-1]!=-1){
                numRoutes++;
            }
        }

        return numRoutes>2;
    }
    public static boolean[] availableRoutes(Point location,int direction, boolean canGoThroughDoor,boolean canGoBackwards){
        int x=location.x;
        int y=location.y;
        boolean [] routes= new boolean[4];
        for(boolean r:routes)
            r=false;

        if(y!=MAP.length-1 && (direction!=0|| canGoBackwards) &&  MAP[y+1][x]!=1 && (MAP[y+1][x]!=-1|| canGoThroughDoor)){
            routes[2]=true;
        }
        if(y!=0  && (direction!=2|| canGoBackwards) && MAP[y-1][x]!=1 && (MAP[y-1][x]!=-1|| canGoThroughDoor)){
            routes[0]=true;
        }
        if(x!=MAP.length-1 && (direction!=3|| canGoBackwards) && MAP[y][x+1]!=1 && (MAP[y][x+1]!=-1|| canGoThroughDoor)) {
            routes[1]=true;
        }
        if(x!=0  && (direction!=1|| canGoBackwards) && MAP[y][x-1]!=1 && (MAP[y][x-1]!=-1|| canGoThroughDoor)){
            routes[3]=true;
        }

        return routes;
    }
    public static boolean checkCollision(int x_dir, int y_dir, Entity entity) {
        int [] wallCollisions= new int[4];

        wallCollisions[0]=MAP[((entity.coordinates.y+y_dir)/ Utils.CELL_LENGTH)][((entity.coordinates.x+x_dir)/ Utils.CELL_LENGTH)]; //top left
        wallCollisions[1]=MAP[((entity.coordinates.y+y_dir)/ Utils.CELL_LENGTH)][((entity.coordinates.x+Utils.CELL_LENGTH-entity.speed+x_dir)/ Utils.CELL_LENGTH)]; //top right
        wallCollisions[2]=MAP[((entity.coordinates.y+Utils.CELL_LENGTH-entity.speed+y_dir)/ Utils.CELL_LENGTH)][((entity.coordinates.x+x_dir)/ Utils.CELL_LENGTH)]; //bottom left
        wallCollisions[3]=MAP[((entity.coordinates.y+Utils.CELL_LENGTH-entity.speed+y_dir)/ Utils.CELL_LENGTH)][((entity.coordinates.x+Utils.CELL_LENGTH-entity.speed+x_dir)/ Utils.CELL_LENGTH)]; //bottom right

        //1.Checks for direction
        //2.Checks tiles in that direction for wall collision
        //3.Checks collision for food if entity is pacman


        if(x_dir==entity.speed){
            if( wallCollisions[1]==1||wallCollisions[3]==1||((wallCollisions[1]==-1||wallCollisions[3]==-1)&& !entity.canGoThroughDoor)) {
                return false;
            }
            else if(entity.isPacman){
                if(wallCollisions[1]==2) {
                    MAP[((entity.coordinates.y+y_dir)/ Utils.CELL_LENGTH)][((entity.coordinates.x+Utils.CELL_LENGTH-entity.speed+x_dir)/ Utils.CELL_LENGTH)] = 0;
                    SCORE+=10;
                    FOOD_COUNT--;
                }
                else if(wallCollisions[3]==2) {
                    MAP[((entity.coordinates.y+Utils.CELL_LENGTH-entity.speed+y_dir)/ Utils.CELL_LENGTH)][((entity.coordinates.x+Utils.CELL_LENGTH-entity.speed+x_dir)/ Utils.CELL_LENGTH)] = 0;
                    SCORE+=10;
                    FOOD_COUNT--;
                }
                else if(wallCollisions[1]==3) {
                    MAP[((entity.coordinates.y+y_dir)/ Utils.CELL_LENGTH)][((entity.coordinates.x+Utils.CELL_LENGTH-entity.speed+x_dir)/ Utils.CELL_LENGTH)] = 0;
                    ENERGIZER_COUNT--;
                    SCORE+=50;
                    FOOD_COUNT--;
                }
                else if(wallCollisions[3]==3) {
                    MAP[((entity.coordinates.y+Utils.CELL_LENGTH-entity.speed+y_dir)/ Utils.CELL_LENGTH)][((entity.coordinates.x+Utils.CELL_LENGTH-entity.speed+x_dir)/ Utils.CELL_LENGTH)] = 0;
                    ENERGIZER_COUNT--;
                    SCORE+=50;
                    FOOD_COUNT--;
                }
                else if(wallCollisions[1]==4||wallCollisions[3]==4 && FRUIT_VISIBLE) {
                   FRUIT_EATEN=true;
                }

            }

        }
        else if(x_dir==-entity.speed){
            if( wallCollisions[0]==1||wallCollisions[2]==1 ||((wallCollisions[0]==-1||wallCollisions[2]==-1)&& !entity.canGoThroughDoor)) {
                return false;
            }
            else if (entity.isPacman){
                if(wallCollisions[0]==2) {
                    MAP[((entity.coordinates.y+y_dir)/ Utils.CELL_LENGTH)][((entity.coordinates.x+x_dir)/ Utils.CELL_LENGTH)] = 0;
                    SCORE+=10;
                    FOOD_COUNT--;
                }
                else if(wallCollisions[2]==2) {
                    MAP[((entity.coordinates.y+Utils.CELL_LENGTH-entity.speed+y_dir)/ Utils.CELL_LENGTH)][((entity.coordinates.x+x_dir)/ Utils.CELL_LENGTH)] = 0;
                    SCORE+=10;
                    FOOD_COUNT--;
                }
                else if(wallCollisions[0]==3) {
                    MAP[((entity.coordinates.y+y_dir)/ Utils.CELL_LENGTH)][((entity.coordinates.x+x_dir)/ Utils.CELL_LENGTH)] = 0;
                    ENERGIZER_COUNT--;
                    SCORE+=50;
                    FOOD_COUNT--;
                }
                else if(wallCollisions[2]==3) {
                    MAP[((entity.coordinates.y+Utils.CELL_LENGTH-entity.speed+y_dir)/ Utils.CELL_LENGTH)][((entity.coordinates.x+x_dir)/ Utils.CELL_LENGTH)] = 0;
                    ENERGIZER_COUNT--;
                    SCORE+=50;
                    FOOD_COUNT--;
                }
                else if(wallCollisions[0]==4||wallCollisions[2]==4 && FRUIT_VISIBLE) {
                    FRUIT_EATEN=true;
                }
            }
        }
        else if(y_dir==entity.speed){
            if( wallCollisions[2]==1||wallCollisions[3]==1||((wallCollisions[2]==-1||wallCollisions[3]==-1)&& !entity.canGoThroughDoor)) {
                return false;
            }
            else if(entity.isPacman){
                if(wallCollisions[2]==2) {
                    MAP[((entity.coordinates.y+Utils.CELL_LENGTH-entity.speed+y_dir)/ Utils.CELL_LENGTH)][((entity.coordinates.x+x_dir)/ Utils.CELL_LENGTH)] = 0;
                    SCORE+=10;
                    FOOD_COUNT--;

                }
                else if(wallCollisions[3]==2) {
                    MAP[((entity.coordinates.y+Utils.CELL_LENGTH-entity.speed+y_dir)/ Utils.CELL_LENGTH)][((entity.coordinates.x+Utils.CELL_LENGTH-entity.speed+x_dir)/ Utils.CELL_LENGTH)] = 0;
                    SCORE+=10;
                    FOOD_COUNT--;
                }
                else if(wallCollisions[2]==3) {
                    MAP[((entity.coordinates.y+Utils.CELL_LENGTH-entity.speed+y_dir)/ Utils.CELL_LENGTH)][((entity.coordinates.x+x_dir)/ Utils.CELL_LENGTH)] = 0;
                    ENERGIZER_COUNT--;
                    SCORE+=50;
                    FOOD_COUNT--;
                }
                else if(wallCollisions[3]==3) {
                    MAP[((entity.coordinates.y+Utils.CELL_LENGTH-entity.speed+y_dir)/ Utils.CELL_LENGTH)][((entity.coordinates.x+Utils.CELL_LENGTH-entity.speed+x_dir)/ Utils.CELL_LENGTH)] = 0;
                    ENERGIZER_COUNT--;
                    SCORE+=50;
                    FOOD_COUNT--;
                }
                else if(wallCollisions[2]==4||wallCollisions[3]==4&& FRUIT_VISIBLE) {
                   FRUIT_EATEN=true;
                }
            }

        }
        else if(y_dir==-entity.speed){
            if( wallCollisions[1]==1||wallCollisions[0]==1||((wallCollisions[1]==-1||wallCollisions[0]==-1)&& !entity.canGoThroughDoor)) {
                return false;
            }
            else if(entity.isPacman){
                if(wallCollisions[1]==2) {
                    MAP[((entity.coordinates.y+y_dir)/ Utils.CELL_LENGTH)][((entity.coordinates.x+Utils.CELL_LENGTH-entity.speed+x_dir)/ Utils.CELL_LENGTH)] = 0;
                    SCORE+=10;
                    FOOD_COUNT--;
                }
                else if(wallCollisions[0]==2) {
                    MAP[((entity.coordinates.y+y_dir)/ Utils.CELL_LENGTH)][((entity.coordinates.x+x_dir)/ Utils.CELL_LENGTH)] = 0;
                    SCORE+=10;
                    FOOD_COUNT--;
                }
                else if(wallCollisions[1]==3) {
                    MAP[((entity.coordinates.y+y_dir)/ Utils.CELL_LENGTH)][((entity.coordinates.x+Utils.CELL_LENGTH-entity.speed+x_dir)/ Utils.CELL_LENGTH)] = 0;
                    ENERGIZER_COUNT--;
                    SCORE+=50;
                    FOOD_COUNT--;
                }
                else if(wallCollisions[0]==3) {
                    MAP[((entity.coordinates.y+y_dir)/ Utils.CELL_LENGTH)][((entity.coordinates.x+x_dir)/ Utils.CELL_LENGTH)] = 0;
                    ENERGIZER_COUNT--;
                    SCORE+=50;
                    FOOD_COUNT--;
                }
                else if(wallCollisions[1]==4||wallCollisions[0]==4 && FRUIT_VISIBLE) {
                    FRUIT_EATEN=true;
                }

            }

        }
        return true;
    }

    public static void checkGhostCollision(Ghost ghost,Pacman pacman){
        if(ghost.coordinates.x<=pacman.coordinates.x+5 && ghost.coordinates.x>=pacman.coordinates.x-5
                && ghost.coordinates.y<=pacman.coordinates.y+5 && ghost.coordinates.y>=pacman.coordinates.y-5)
        {
            if(!ghost.isFrightened && !ghost.isDead)
                ghost.killedPacman();
            else if(ghost.isFrightened)
                ghost.dies();
        }

    }

    public static boolean inTunnel(Entity entity) {
        if(!entity.isPacman){
            if(entity.coordinates.y==9*Utils.CELL_LENGTH){
                if((entity.coordinates.x<Utils.CELL_LENGTH) || (entity.coordinates.x>19*Utils.CELL_LENGTH)){
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
            if(entity.coordinates.y==9*Utils.CELL_LENGTH){
                if((entity.coordinates.x<25) || (entity.coordinates.x>Utils.JFRAME_WIDTH-26)){
                    return true;
                }
            }
        }

        return false;
    }

}
