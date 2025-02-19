import javax.sound.sampled.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;

public class StartWindow extends JFrame {
    private JTextField player1Field, player2Field, questionCountField;
    private JButton startButton;
    private Clip backgroundMusic;

    public StartWindow() {
        setTitle("Configuración del Juego");
        setSize(500, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());


        loadBackgroundMusic("tedecampana.wav");

        // Panel con imagen de fondo
        JPanel panel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                ImageIcon backgroundImage = new ImageIcon("puertalacage.jpg");
                g.drawImage(backgroundImage.getImage(), 0, 0, getWidth(), getHeight(), this);
            }
        };
        panel.setLayout(new GridBagLayout());


        Font customFont = new Font("Arial", Font.BOLD, 16);
        Color textColor = Color.WHITE;

        JLabel player1Label = new JLabel("Jugador 1:");
        player1Label.setFont(customFont);
        player1Label.setForeground(textColor);

        player1Field = new JTextField(10);
        player1Field.setFont(customFont);

        JLabel player2Label = new JLabel("Jugador 2:");
        player2Label.setFont(customFont);
        player2Label.setForeground(textColor);

        player2Field = new JTextField(10);
        player2Field.setFont(customFont);

        JLabel questionCountLabel = new JLabel("Número de Preguntas:");
        questionCountLabel.setFont(customFont);
        questionCountLabel.setForeground(textColor);

        questionCountField = new JTextField(5);
        questionCountField.setFont(customFont);


        ImageIcon buttonIcon = new ImageIcon("jugar.png");
        Image image = buttonIcon.getImage();
        Image resizedImage = image.getScaledInstance(150, 50, Image.SCALE_SMOOTH);  // Ajustamos el tamaño
        buttonIcon = new ImageIcon(resizedImage);


        startButton = new JButton("", buttonIcon);
        startButton.setFont(customFont);
        startButton.setBackground(new Color(50, 205, 50));
        startButton.setForeground(Color.BLACK);
        startButton.setFocusPainted(false);
        startButton.setBorderPainted(false);


        startButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                startGame();
            }
        });


        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(player1Label, gbc);

        gbc.gridx = 1;
        panel.add(player1Field, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        panel.add(player2Label, gbc);

        gbc.gridx = 1;
        panel.add(player2Field, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        panel.add(questionCountLabel, gbc);

        gbc.gridx = 1;
        panel.add(questionCountField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        panel.add(startButton, gbc);

        add(panel, BorderLayout.CENTER);
        setVisible(true);
    }

    private void startGame() {
        String player1 = player1Field.getText();
        String player2 = player2Field.getText();
        String questionCountStr = questionCountField.getText();

        if (player1.isEmpty() || player2.isEmpty() || questionCountStr.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Por favor, complete todos los campos.");
            return;
        }

        try {
            int questionCount = Integer.parseInt(questionCountStr);
            if (questionCount <= 0) {
                JOptionPane.showMessageDialog(this, "El número de preguntas debe ser mayor que 0.");
                return;
            }

            if (backgroundMusic != null && backgroundMusic.isRunning()) {
                backgroundMusic.stop();
            }

            // Cerrar ventana actual y abrir TrivialGameSwing
            dispose();
            SwingUtilities.invokeLater(() -> new TrivialGameSwing(player1, player2, questionCount));

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Ingrese un número válido para las preguntas.");
        }
    }

    private void loadBackgroundMusic(String filePath) {
        try {
            File musicFile = new File(filePath);
            AudioInputStream audioStream = AudioSystem.getAudioInputStream(musicFile);
            backgroundMusic = AudioSystem.getClip();
            backgroundMusic.open(audioStream);
            backgroundMusic.loop(Clip.LOOP_CONTINUOUSLY);
            backgroundMusic.start();
        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
            System.out.println("Error al cargar la música de fondo.");
        }
    }

    public static void main(String[] args) {
        new StartWindow();
    }
}
