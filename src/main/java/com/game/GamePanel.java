package com.game;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class GamePanel extends JPanel {

    public static final int WIDTH = 800;   // ширина игрового поля
    public static final int HEIGHT = 500; // высота игрового поля
    // игровые объекты
    private Arrow arrow;
    private Target nearTarget;
    private Target farTarget;

    private int score;
    private int shotCount;

    private boolean running;
    private boolean paused;

    private Thread gameThread;

    private static final int PLAYER_X = 60;
    private static final int PLAYER_Y = HEIGHT / 2;
    private static final double ARROW_SPEED = 6.0;
    private static final int FIELD_LEFT = 120;
    private static final int FIELD_RIGHT = 650;
    public GamePanel() {
        setPreferredSize(new Dimension(WIDTH, HEIGHT));
        setBackground(Color.WHITE);
        setLayout(null);

        arrow = new Arrow();
        initTargets();
        initButtons();
    }

    private void initTargets() {
        double midY = HEIGHT / 2.0;

        nearTarget = new Target(350, midY, 1.5, 1, 20);
        farTarget = new Target(550, midY, 3.0, 2, 10);
    }
    private void initButtons() {
        // Кнопка "Начало игры"
        JButton startButton = new JButton("Начало игры");
        startButton.setBounds(130, 455, 130, 30);
        startButton.addActionListener(e -> startGame());
        add(startButton);

        // Кнопка "Остановить игру"
        JButton stopButton = new JButton("Остановить игру");
        stopButton.setBounds(270, 455, 150, 30);
        stopButton.addActionListener(e -> stopGame());
        add(stopButton);

        // Кнопка "Пауза"
        JButton pauseButton = new JButton("Пауза");
        pauseButton.setBounds(430, 455, 100, 30);
        pauseButton.addActionListener(e -> togglePause());
        add(pauseButton);

        // Кнопка "Выстрел"
        JButton shootButton = new JButton("Выстрел");
        shootButton.setBounds(540, 455, 100, 30);
        shootButton.addActionListener(e -> shoot());
        add(shootButton);
    }
    private void startGame() {
        running = false;
        if (gameThread != null) {
            try {
                gameThread.join();

            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
        score = 0;   //сброс счёта и выстрелы
        shotCount = 0;

        arrow.active = false;  //сброс стрелы

        initTargets();   //возврат мишеней в середину

        running = false;
        paused = false;   //если поток был - остановка
        //создание и запуск нового потока
        gameThread = new Thread(() -> {
            running = true;
            while(running) {
                if (!paused) {
                    next(); //просчёт след. кадра (двигаем объекты)
                    repaint(); //перерисовка экрана
                }
                try {
                    Thread.sleep(16);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        });
        gameThread.start();
    }

    private void stopGame() {
        running = false;
        paused = false;
        arrow.active = false;
        repaint();
    }

    private void togglePause() {
        //если игра не запущена - ничего не делаем
        if(!running) return;
        paused = !paused;  //переключение паузы
    }

    private void shoot() {
        if (!running || paused) return; //если игра не идет, то стрелять нельзя
        if (arrow.active) return;  //если стрела летит, то нельзя стрелять

        shotCount++;
        arrow.shoot(PLAYER_X + 30, PLAYER_Y); //вылет стрелы от игрока
    }

    private void next() {
        // движение мишеней
        nearTarget.y += nearTarget.speed;
        farTarget.y += farTarget.speed;

        // если мишень вышла за нижнюю границу, то возврат наверх
        if (nearTarget.y > HEIGHT - 50) nearTarget.y = 50;
        if (farTarget.y > HEIGHT - 50) farTarget.y = 50;

        // движение стрелы
        if (arrow.active) {
            arrow.x += ARROW_SPEED;

            // Если вылет стрелы за правую границу - остановка стрелы
            if (arrow.x > FIELD_RIGHT) {
                arrow.active = false;
            }

            // проверка попадания
            if (hits(arrow, nearTarget)) {
                score += nearTarget.points; // +1
                arrow.active = false;
                nearTarget.y = HEIGHT / 2.0; //возврат мишени в середину
            }

            if (hits(arrow, farTarget)) {
                score += farTarget.points; // +2
                arrow.active = false;
                farTarget.y = HEIGHT / 2.0;
            }
        }
    }

    private boolean hits(Arrow arrow, Target target) {
        //расчет расстояния от стрелы до центра мишени
        double dx = arrow.x - target.x;
        double dy = arrow.y - target.y;
        double distance = Math.sqrt(dx * dx + dy * dy); //теорема Пифагора
        return distance <= target.radius; // если расстояние меньше радиуса - поападание
    }
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);  //swing очищает экран

        Graphics2D g2 = (Graphics2D) g;
        // сглаживание (более красиво)
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // стена желтая
        g2.setColor(Color.YELLOW);
        g2.fillRect(100, 0, 30, HEIGHT - 50);

        // ИГРОК - синий треугольник
        int[] px = {PLAYER_X, PLAYER_X + 30, PLAYER_X};
        int[] py = {PLAYER_Y - 15, PLAYER_Y, PLAYER_Y + 15};
        g2.setColor(Color.BLUE);
        g2.fillPolygon(px, py, 3);
        // отрисовка стрелы
        if (arrow.active) {
            g2.setColor(Color.BLACK);
            g2.setStroke(new BasicStroke(2));
            g2.drawLine((int) arrow.x, (int) arrow.y, (int) arrow.x - 20, (int) arrow.y);

            // наконечник
            int[] arrowX = {(int) arrow.x, (int) arrow.x - 8, (int) arrow.x - 8};
            int[] arrowY = {(int) arrow.y, (int) arrow.y - 4, (int) arrow.y + 4};
            g2.fillPolygon(arrowX, arrowY, 3);
        }
        // мишени
        drawTarget(g2, nearTarget);
        drawTarget(g2, farTarget);

        // счёт и выстрелы справа
        g2.setColor(Color.BLACK);
        g2.setFont(new Font("Arial", Font.BOLD, 16));
        g2.drawString("Счёт игрока:", 670, 50);
        g2.setFont(new Font("Arial", Font.PLAIN, 16));
        g2.drawString(String.valueOf(score), 670, 75);

        g2.setFont(new Font("Arial", Font.BOLD, 16));
        g2.drawString("Выстрелов:", 670, 110);
        g2.setFont(new Font("Arial", Font.PLAIN, 16));
        g2.drawString(String.valueOf(shotCount), 670, 135);

        // если игра не запущена
        if (!running) {
            g2.setFont(new Font("Arial", Font.BOLD, 24));
            g2.setColor(Color.GRAY);
            g2.drawString("Нажмите 'Начало игры'", 220, HEIGHT / 2);
        }
        // если пауза
        if (paused) {
            g2.setFont(new Font("Arial", Font.BOLD, 24));
            g2.setColor(Color.GRAY);
            g2.drawString("ПАУЗА", 340, HEIGHT / 2);
        }
    }

    // отрисовка одной мишени
    private void drawTarget(Graphics2D g2, Target target) {
        int x = (int) target.x;
        int y = (int) target.y;
        int r = (int) target.radius;

        // внешний красный круг
        g2.setColor(Color.RED);
        g2.fillOval(x - r, y - r, r * 2, r * 2);

        // внутренний белый круг
        g2.setColor((Color.WHITE));
        g2.fillOval(x - r/2, y - r/2, r, r);
    }
}