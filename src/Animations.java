import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public final class Animations implements ActionListener {
    Game game;
    Timer timer;

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
        timer= new Timer(1,this);
        timer.start();

        gameOver=false;
        lostLife=false;
        begOfLevel=false;
        finishedLevel=false;
        pause=false;
    }

    public void start(){
        timer.restart();
    }
    @Override
    public void actionPerformed(ActionEvent e) {
        game.actionPerformed();

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
            }

            game.pacman.canMove= true;
            begOfLevel=false;
            game.drawReady=false;
        }

        if(gameOver && !lostLife && System.currentTimeMillis()-gameOverTimeStamp>=TimeUtils.GAMEOVER_ANIMATION){
            gameOver=false;
            game.parent.returnToMenu(game.score);
            timer.stop();
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

        game.pacman.deathAnimation();
        for(Ghost g:game.ghost){
            g.makeInvisible();
        }
    }

    public void finishedLevel(){
        game.pacman.canMove=false;
        for(Ghost g:game.ghost){
            g.makeInvisible();
        }
        game.fruitVisible=false;

        finishedLevel=true;
        finishedLevelTimeStamp=System.currentTimeMillis();
    }

    public void newLevel(){
        game.copyMap();
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
            g.canMove=false;
        }

        game.drawReady=true;
        begOfLevel=true;
        begOfLeveLTimeStamp=System.currentTimeMillis();
    }

    private void gameOver(){
        game.pacman.isVisible=false;
        game.fruitVisible=false;
        game.gameOver=true;

        gameOver=true;
        gameOverTimeStamp=System.currentTimeMillis();
    }

    private void pause() {
        pauseStamp=System.currentTimeMillis();
        pause=true;
    }
}
