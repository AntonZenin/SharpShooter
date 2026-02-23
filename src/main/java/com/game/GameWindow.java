package com.game;

import javax.swing.*;
import java.awt.*;

public class GameWindow extends JFrame{

    public GameWindow() {
        setTitle("Меткий стрелок"); //заголовок окна
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); //закрыть программу при закрытии окна
        setResizable(false);

        GamePanel gamePanel = new GamePanel();
        add(gamePanel);
        pack(); //подстраивание окна под размер содержимого

        setLocationRelativeTo(null); //центровка окна
        setVisible(true);
    }

}
