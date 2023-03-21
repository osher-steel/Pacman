import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;

public class Utils {
    public static final int JFRAME_HEIGHT=Utils.CELL_LENGTH*Utils.NUM_ROWS+Utils.NUM_ROWS*4+10;
    public static final int JFRAME_WIDTH=Utils.CELL_LENGTH*Utils.NUM_COLUMNS;

    public final static int RED=20;
    public final static int PINK=5;
    public final static int CYAN=10;
    public final static int _ORANGE=15;

    public final static int CELL_LENGTH=25;
    public final static int NUM_COLUMNS=21;
    public final static int NUM_ROWS=21;
    public final static int NUM_FOOD=150;
    public final static int LIVES=3;

    public final static int CHASE_MODE=80;
    public final static int SCATTER_MODE=81;
    public final static int FRIGHTENED_MODE=82;
    public final static int LEAVEHOUSE_MODE=83;
    public final static int DIED_MODE =84;

    public final static int CHERRIES=100;
    public final static int STRAWBERRY=300;
    public final static int ORANGE=500;
    public final static int APPLE=700;
    public final static int MELON=1000;
    public final static int GALAXIAN=2000;
    public final static int BELL=3000;
    public final static int KEY=5000;

    public static Image loadImage(String filename,String folder){
        BufferedImage temp_buff;
        try {
            temp_buff = ImageIO.read(Utils.class.getResourceAsStream("sprites/"+ folder +"/"+ filename+".png"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
         return temp_buff.getScaledInstance(CELL_LENGTH,CELL_LENGTH,Image.SCALE_DEFAULT);
    }

    public final static Image CHERRIES_IMG=loadImage("_cherries","fruit");
    public final static Image STRAWBERRY_IMG=loadImage("_strawberry","fruit");
    public final static Image ORANGE_IMG=loadImage("_orange","fruit");
    public final static Image APPLE_IMG=loadImage("_apple","fruit");
    public final static Image MELON_IMG=loadImage("_melon","fruit");
    public final static Image GALAXIAN_IMG=loadImage("_galaxian","fruit");
    public final static Image BELL_IMG=loadImage("_bell","fruit");
    public final static Image KEY_IMG=loadImage("_key","fruit");


    public final static Image []FRIGHTENED_IMG={
            loadImage("0","ghosts/frightened"),
            loadImage("1","ghosts/frightened"),
    };
    public final static Image[] DEAD_IMG={
            loadImage("0","ghosts/dead"),loadImage("1","ghosts/dead"),
            loadImage("2","ghosts/dead"),loadImage("3","ghosts/dead"),
    };

    public final static Image LIFE_IMG=loadImage("1","pacman/1");

    public static Image LOAD_PACMAN_DEATH(int i) {
        return loadImage(String.valueOf(i),"pacman/dead");
    }


    public final static int [][]INITIAL_MAP={{0,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,0},
            {0,1,2,2,2,2,2,2,2,2,1,2,2,2,2,2,2,2,2,1,0},
            {0,1,2,1,1,2,1,1,1,2,1,2,1,1,1,2,1,1,2,1,0},
            {0,1,3,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,3,1,0},
            {0,1,2,1,1,2,1,2,1,1,1,1,1,2,1,2,1,1,2,1,0},
            {0,1,2,2,2,2,1,2,2,2,1,2,2,2,1,2,2,2,2,1,0},
            {0,1,1,1,1,2,1,1,1,0,1,0,1,1,1,2,1,1,1,1,0},
            {0,0,0,0,1,2,1,0,0,0,0,0,0,0,1,2,1,0,0,0,0},
            {1,1,1,1,1,2,1,0,1,1,-1,1,1,0,1,2,1,1,1,1,1},
            {0,0,0,0,0,2,0,0,1,0,0,0,1,0,0,2,0,0,0,0,0},
            {1,1,1,1,1,2,1,0,1,1,1,1,1,0,1,2,1,1,1,1,1},
            {0,0,0,0,1,2,1,0,0,0,4,0,0,0,1,2,1,0,0,0,0},
            {0,1,1,1,1,2,1,0,1,1,1,1,1,0,1,2,1,1,1,1,0},
            {0,1,2,2,2,2,2,2,2,2,1,2,2,2,2,2,2,2,2,1,0},
            {0,1,2,1,1,2,1,1,1,2,1,2,1,1,1,2,1,1,2,1,0},
            {0,1,2,2,1,2,2,2,2,2,0,2,2,2,2,2,1,2,2,1,0},
            {0,1,1,2,1,2,1,2,1,1,1,1,1,2,1,2,1,2,1,1,0},
            {0,1,3,2,2,2,1,2,2,2,1,2,2,2,1,2,2,2,3,1,0},
            {0,1,2,1,1,1,1,1,1,2,1,2,1,1,1,1,1,1,2,1,0},
            {0,1,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,1,0},
            {0,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,0}};



}
