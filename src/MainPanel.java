import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;

public class MainPanel extends JPanel  implements MouseListener, KeyListener{
    CardLayout cardLayout;
    Game game;
    Menu menu;
    boolean inGame;
    int highscore;

    public static void main(String[] args) {
        JFrame frame= new JFrame();
        MainPanel mainPanel=new MainPanel();
        frame.add(mainPanel);
        frame.setSize(Utils.JFRAME_WIDTH,Utils.JFRAME_HEIGHT);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }

    MainPanel(){
        InputStream is=getClass().getResourceAsStream("highscore/highscore.txt");
        InputStreamReader isr= new InputStreamReader(is);
        BufferedReader bufferedReader= new BufferedReader(isr);

        try {
            highscore=Integer.parseInt(bufferedReader.readLine());
            bufferedReader.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        cardLayout= new CardLayout();
        setLayout(cardLayout);
        inGame=false;

        menu= new Menu(this);
        add(menu,"menu");

        cardLayout.show(this,"menu");

        addKeyListener(this);
        addMouseListener(this);
        setFocusable(true);
    }

    public void startGame(){
        inGame=true;
        game= new Game(this,highscore);
        add(game,"game");
        cardLayout.show(this,"game");
    }

    public void returnToMenu(int score) {
        if(score>highscore){
            highscore=score;
            saveScore();
        }

        inGame=false;
        menu.restart();
        cardLayout.show(this,"menu");
    }

    private void saveScore() {
        try {
            BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter("src/highscore/highscore.txt"));
            bufferedWriter.write(String.valueOf(highscore));
            bufferedWriter.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if(inGame)
            game.keyPressed(e);
        else
            menu.keyPressed(e);
    }

    @Override
    public void mousePressed(MouseEvent e) {
        if(!inGame)
            menu.mousePressed(e);

    }

    @Override
    public void mouseReleased(MouseEvent e) {
        if(!inGame)
            menu.mouseReleased(e);
    }




    @Override
    public void keyTyped(KeyEvent e) {

    }

    @Override
    public void keyReleased(KeyEvent e) {

    }

    @Override
    public void mouseClicked(MouseEvent e) {

    }

    @Override
    public void mouseEntered(MouseEvent e) {

    }

    @Override
    public void mouseExited(MouseEvent e) {

    }

}
