import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public final class Animations{
    Game game;

    boolean gameOver;
    boolean lostLife;
    boolean begOfLevel;
    boolean finishedLevel;
    boolean pause;

    long gameOverTimeStamp;
    long finishedLevelTimeStamp;
    long begOfLeveLTimeStamp;
    long pauseStamp;

    int pauseTime;

    public Animations(Game game){
        this.game=game;

        gameOver=false;
        lostLife=false;
        begOfLevel=false;
        finishedLevel=false;
        pause=false;
    }

    public void actionPerformed() {

        if(lostLife&& !game.pacman.deathAnimation){
            lostLife=false;
            pauseTime=1000;
            pause();
        }

        if(finishedLevel && System.currentTimeMillis()-finishedLevelTimeStamp>=TimeUtils.LEVELEND_ANIMATION){
            finishedLevel=false;
            newLevel();
        }

        if(begOfLevel && System.currentTimeMillis()-begOfLeveLTimeStamp>=TimeUtils.READY_ANIMATION){
            for (Ghost g : game.ghost) {
                g.canMove=true;
                g.moveNow=true;
            }

            game.ghostCanMove=true;

            game.pacman.canMove= true;
            begOfLevel=false;
            game.drawReady=false;
        }

        if(gameOver && !lostLife && System.currentTimeMillis()-gameOverTimeStamp>=TimeUtils.GAMEOVER_ANIMATION){
            gameOver=false;
            game.gameOver();
        }

        if(pause && System.currentTimeMillis()-pauseStamp>=pauseTime){
            pause=false;
            if(game.lives==0)
                gameOver();
            else
                revive();
        }
    }

    public void lostLife(){
        lostLife=true;
        game.pacman.canMove=false;
        game.pacman.deathAnimation();
        for(Ghost g:game.ghost){
            g.makeInvisible();
        }
    }

    public void finishedLevel(){
        game.pacman.canMove=false;
        game.pacman.updateFrame(true);
        for(Ghost g:game.ghost) {
            g.makeInvisible();
        }
        MAPHANDLER.FRUIT_VISIBLE=false;
        finishedLevel=true;
        finishedLevelTimeStamp=System.currentTimeMillis();
    }

    public void newLevel(){
        game.pacman.init();
        game.pacman.canMove=false;
        for(Ghost g:game.ghost){
            g.init(true);
            g.canMove=false;
        }

        game.drawReady=true;
        begOfLevel=true;
        begOfLeveLTimeStamp=System.currentTimeMillis();

    }

    private void revive(){
        game.pacman.init();
        game.pacman.canMove=false;
        for(Ghost g:game.ghost){
            g.init(false);
        }
        game.ghostCanMove=false;
        game.drawReady=true;
        begOfLevel=true;
        begOfLeveLTimeStamp=System.currentTimeMillis();
    }

    private void gameOver(){
        game.pacman.isVisible=false;
        MAPHANDLER.FRUIT_VISIBLE=false;
        game.gameOver=true;

        gameOver=true;
        gameOverTimeStamp=System.currentTimeMillis();
    }

    private void pause() {
        pauseStamp=System.currentTimeMillis();
        pause=true;
    }
}
