import jdk.jshell.execution.Util;

import java.awt.*;

public class Fruit {
    private int id;
    private int points;
    private Image sprite;

    Fruit(int id){
        init(id);
    }

    private void init(int id){
        this.id=id;
        points=id;

        switch(id){
            case Utils.CHERRIES -> sprite= Utils.CHERRIES_IMG;
            case Utils.STRAWBERRY -> sprite= Utils.STRAWBERRY_IMG;
            case Utils.ORANGE -> sprite= Utils.ORANGE_IMG;
            case Utils.APPLE -> sprite= Utils.APPLE_IMG;
            case Utils.MELON -> sprite= Utils.MELON_IMG;
            case Utils.GALAXIAN -> sprite= Utils.GALAXIAN_IMG;
            case Utils.BELL -> sprite= Utils.BELL_IMG;
            case Utils.KEY -> sprite= Utils.KEY_IMG;

        }
    }

    public Image getSprite(){
        return sprite;
    }

    public int getPoints(){
        return points;
    }

    public void nextFruit(){
        switch(id){
            case Utils.BELL ->{
                init(Utils.KEY);
            }
            case Utils.GALAXIAN ->{
                init(Utils.BELL);
            }
            case Utils.MELON ->{
                init(Utils.GALAXIAN);
            }
            case Utils.APPLE ->{
                init(Utils.MELON);
            }
            case Utils.ORANGE ->{
                init(Utils.APPLE);
            }
            case Utils.STRAWBERRY ->{
                init(Utils.ORANGE);
            }
            case Utils.CHERRIES ->{
                init(Utils.STRAWBERRY);
            }
        }
    }

}
