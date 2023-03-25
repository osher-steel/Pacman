public class TimeUtils {
    public final static int LEVELEND_ANIMATION=1000;
    public final static int READY_ANIMATION=2000;
    public final static int GAMEOVER_ANIMATION=3000;


    public final static int ONEUP_SCORE=15000;

    public final static int INHOUSE_AFTER_DEATH_TIME=1500;
    public final static int INHOUSE_TIME1=4000+READY_ANIMATION;
    public final static int INHOUSE_FOODCOUNT1=15;
    public final static int INHOUSE_TIME2=7000+READY_ANIMATION;
    public final static int INHOUSE_FOODCOUNT2=30;

    public final static int FRUIT_VISIBLE_TIME=8000;
    public final static int POINTS_VISIBLE_TIME=1000;
    public final static int ONEUP_VISIBLE_TIME=1500;

    public final static int FLASHING_TIME=3500;
    public final static int FLASHING_SPEED=200;



    public final static int FRUIT_VISIBLE_FOODCOUNT1=65;

    public final static int SPEED=2;

    public final static int MAX_DELAY= 10;

    public static int PACMAN_DELAY(int level,boolean inMenu,boolean frightened){
        if(!inMenu){
            if(!frightened){
                if(level==1)
                    return 19;
                else if(level<=4){
                    return 17;
                }
                else if(level<=21){
                    return 14;
                }
                else{
                    return 15;
                }
            }else
                return 15;
        }
        else return MAX_DELAY;
    }

    public static int GHOST_DELAY(int level,int mode){
        if (mode == Utils.FRIGHTENED_MODE) {
            if (level == 1) {
                return 30;
            } else if (level <= 4) {
                return 25;
            } else if (level <= 21) {
                return 20;
            } else
                return 18;
        }
        else if (mode == Utils.DIED_MODE) {
            return 6;
        }
        else {
            if (level == 1) {
                return 21;
            } else if (level <= 4) {
                return 19;
            } else if (level <= 21) {
                return 16;
            } else {
                return 15;
            }
        }

    }

    public static int FRIGHTENED_TIME(int level){
        if(level==1)
            return 10000;
        else if(level<=4){
            return 8000;
        }
        else if(level<=10){
            return 6000;
        }
        else if(level<=21){
            return 3500;
        }
        else{
            return 0;
        }
    }

    public static int FLASHING_TIME(int level){
        return FRIGHTENED_TIME(level)-FLASHING_TIME;
    }
}
