package com.game;

import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        // SwingUtilities.invokeLater — запускает создание окна в правильном потоке Swing
        // Это стандартная практика при работе со Swing
        SwingUtilities.invokeLater(() -> {
            new GameWindow();
        });
    }
}
