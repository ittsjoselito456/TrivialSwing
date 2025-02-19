import javax.sound.sampled.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class TrivialGameSwing {
    private List<Question> questions;
    private int currentQuestionIndex = 0;
    private JLabel questionLabel;
    private JButton[] optionButtons;
    private JLabel resultLabel;
    private JLabel playerTurnLabel;
    private int[] playerScores = new int[2];
    private int currentPlayer = 0;
    private JProgressBar gameProgressBar;
    private JProgressBar[] playerProgressBars;
    private Clip correctSound;
    private Clip incorrectSound;
    private Clip backgroundMusic;
    private int totalQuestions;
    private String[] playerNames;

    public TrivialGameSwing(String player1, String player2, int totalQuestions) {
        this.playerNames = new String[]{player1, player2};
        this.totalQuestions = totalQuestions;

        questions = XMLReader.readQuestionsFromXML("Preguntes2025.xml");

        JFrame frame = new JFrame("Trivial Game");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(600, 450);

        try {
            File correctSoundFile = new File("correcto.wav");
            File incorrectSoundFile = new File("incorrecto.wav");
            File backgroundSoundFile = new File("dale2.wav");

            AudioInputStream audioStreamCorrect = AudioSystem.getAudioInputStream(correctSoundFile);
            AudioInputStream audioStreamIncorrect = AudioSystem.getAudioInputStream(incorrectSoundFile);
            AudioInputStream audioStreamBackground = AudioSystem.getAudioInputStream(backgroundSoundFile);

            correctSound = AudioSystem.getClip();
            incorrectSound = AudioSystem.getClip();
            backgroundMusic = AudioSystem.getClip();

            correctSound.open(audioStreamCorrect);
            incorrectSound.open(audioStreamIncorrect);
            backgroundMusic.open(audioStreamBackground);
        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
            e.printStackTrace();
        }

        backgroundMusic.loop(Clip.LOOP_CONTINUOUSLY);

        questionLabel = new JLabel();
        questionLabel.setForeground(Color.WHITE);
        optionButtons = new JButton[4];
        for (int i = 0; i < 4; i++) {
            optionButtons[i] = new JButton();
            optionButtons[i].setForeground(Color.BLACK);
            final int index = i;
            optionButtons[i].addActionListener(e -> checkAnswer(index, -1));
        }
        resultLabel = new JLabel();
        resultLabel.setForeground(Color.WHITE);
        playerTurnLabel = new JLabel("Turno del Jugador " + playerNames[currentPlayer]);
        playerTurnLabel.setForeground(Color.WHITE);

        gameProgressBar = new JProgressBar(0, totalQuestions);
        gameProgressBar.setStringPainted(true);
        gameProgressBar.setForeground(Color.BLACK);
        playerProgressBars = new JProgressBar[2];
        for (int i = 0; i < 2; i++) {
            playerProgressBars[i] = new JProgressBar(0, totalQuestions);
            playerProgressBars[i].setStringPainted(true);
            playerProgressBars[i].setForeground(Color.BLACK);
            playerProgressBars[i].setString(playerNames[i] + ": 0");
        }

        JPanel panel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                ImageIcon backgroundImage = new ImageIcon("lacage.jpg");
                g.drawImage(backgroundImage.getImage(), 0, 0, getWidth(), getHeight(), this);
            }
        };
        panel.setLayout(new GridLayout(9, 1));
        panel.add(playerTurnLabel);
        panel.add(questionLabel);
        for (JButton button : optionButtons) {
            panel.add(button);
        }
        panel.add(resultLabel);
        panel.add(gameProgressBar);
        panel.add(playerProgressBars[0]);
        panel.add(playerProgressBars[1]);

        frame.add(panel);
        frame.setVisible(true);

        loadQuestion();
    }

    private void loadQuestion() {
        if (currentQuestionIndex < totalQuestions) {
            Question currentQuestion = questions.get(currentQuestionIndex);


            List<String> options = new ArrayList<>(currentQuestion.getOptions());
            String correctAnswer = currentQuestion.getCorrectAnswer();


            Collections.shuffle(options);


            int correctIndex = options.indexOf(correctAnswer);


            questionLabel.setText(currentQuestion.getText());
            for (int i = 0; i < 4; i++) {
                optionButtons[i].setText(options.get(i));


                for (ActionListener al : optionButtons[i].getActionListeners()) {
                    optionButtons[i].removeActionListener(al);
                }

                final int index = i;

                optionButtons[i].addActionListener(e -> checkAnswer(index, correctIndex));
            }

            resultLabel.setText("");
            gameProgressBar.setValue(currentQuestionIndex);
            updatePlayerProgressBars();
        } else {
            endGame();
        }
    }

    private void checkAnswer(int selectedIndex, int correctIndex) {
        if (selectedIndex == correctIndex) {
            resultLabel.setText("¡Correcto!");
            correctSound.setFramePosition(0);
            correctSound.start();
            playerScores[currentPlayer]++;
        } else {
            resultLabel.setText("Incorrecto. La respuesta correcta es: " + questions.get(currentQuestionIndex).getCorrectAnswer());
            incorrectSound.setFramePosition(0);
            incorrectSound.start();
        }

        currentQuestionIndex++;
        changeTurnWithFadeEffect();
    }

    private void changeTurnWithFadeEffect() {
        Timer timer = new Timer(50, new ActionListener() {
            float opacity = 1.0f;
            boolean fadingOut = true;

            @Override
            public void actionPerformed(ActionEvent e) {
                if (fadingOut) {
                    opacity -= 0.1f;
                    if (opacity <= 0) {
                        fadingOut = false;
                        opacity = 0;
                        currentPlayer = (currentPlayer + 1) % 2;
                        playerTurnLabel.setText("Turno del Jugador " + playerNames[currentPlayer]);
                    }
                } else {
                    opacity += 0.1f;
                    if (opacity >= 1) {
                        opacity = 1;
                        ((Timer) e.getSource()).stop();
                        loadQuestion();
                    }
                }
                playerTurnLabel.setForeground(new Color(255, 255, 255, (int) (opacity * 255)));
            }
        });
        timer.start();
    }

    private void updatePlayerProgressBars() {
        for (int i = 0; i < 2; i++) {
            playerProgressBars[i].setValue(playerScores[i]);
            playerProgressBars[i].setString(playerNames[i] + ": " + playerScores[i]);
        }
    }

    private void endGame() {
        questionLabel.setText("¡Juego Terminado!");
        for (JButton button : optionButtons) {
            button.setEnabled(false);
        }
        String winner = (playerScores[0] > playerScores[1]) ? playerNames[0] : (playerScores[1] > playerScores[0]) ? playerNames[1] : "Empate";
        resultLabel.setText("Ganador: " + winner);

        playerProgressBars[0].setString(playerNames[0] + ": " + playerScores[0]);
        playerProgressBars[1].setString(playerNames[1] + ": " + playerScores[1]);

        if (backgroundMusic != null && backgroundMusic.isRunning()) {
            backgroundMusic.stop();
        }

        JOptionPane.showMessageDialog(null, "¡El juego ha terminado!\nGanador: " + winner + "\n\nPuntuaciones:\n" + playerNames[0] + ": " + playerScores[0] + "\n" + playerNames[1] + ": " + playerScores[1], "Fin del Juego", JOptionPane.INFORMATION_MESSAGE);
    }
}
