package Quiz;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

public class JavaQuiz {
    static long maxTime = 20 * 60 * 1000; // 20 minutes in milliseconds
    static long startTime;
    static boolean quizEnded = false;
    static int score = 0;
    static ArrayList<String> wrongQuestions = new ArrayList<>();
    static ArrayList<String> wrongAnswers = new ArrayList<>();
    static ArrayList<String> correctAnswersList = new ArrayList<>();

    public static void main(String[] args) {
        // Questions and answers
        String[] questions = {
            "KAS IR JVM?",
            "KĀDU ATSLĒGAS VĀRDU IZMANTO, LAI IZVEIDOTU OBJEKTU?",
            "KO NOZĪMĒ ATSLĒGAS VĀRDS \"STATIC\"?",
            "KĀDA IR NOKLUSĒJUMA VĒRTĪBA int MAINĪGAJAM?",
            "KĀDU DATU TIPU IZMANTO, LAI GLABĀTU TEKSTU?",
            "KAS IR KOMPILATORS JAVA VALODĀ?",
            "KAS IR \"CLASS\" JAVA VALODĀ?",
            "KĀDA IR JAVA GALVENAIS METODE?",
            "KAS IR JAVA INTERFACE?",
            "KO DARĀ break KOMANDA?",
            "KAS IR \"POLIMORFISMS\" JAVA?",
            "KĀDU METODI IZMANTO OBJEKTA SALĪDZINĀŠANAI?",
            "KO NOZĪMĒ ATSLĒGAS VĀRDS \"FINAL\"?",
            "KAS NOTIEK, JA KĻŪDA NETIEK APSTRĀDĀTA AR try-catch?",
            "KĀDA IR JAVA \"ARRAY\" PAMATĪPAŠĪBA?",
            "KAS IR \"JAVA PACKAGE\"?",
            "KĀDA IR JAVA KOMPILĒŠANAS KOMANDA?",
            "KAS IR System.out.println?",
            "KĀDU ATSLĒGAS VĀRDU IZMANTO, LAI IZVEIDOTU IEMANTOJUMU (INHERITANCE)?",
            "KO DARA this ATSLĒGAS VĀRDS JAVA VALODĀ?"
        };

        String[][] options = {
            {"Izstrādes vide", "Java virtuālā mašīna", "Java bibliotēka", "Kompilators"},
            {"create", "object", "new", "instance"},
            {"Pieder klasei", "Nemainīgs", "Norāda tipu", "Funkcija automātiska"},
            {"0", "null", "1", "Nenoteikta"},
            {"char", "String", "int", "double"},
            {"Programma", "Tests", "Datubāze", "Funkcija"},
            {"Objekts", "Klase", "Funkcija", "Metode"},
            {"init()", "main()", "start()", "begin()"},
            {"Klase", "Funkcija", "Abstrakts veids", "Objekts"},
            {"Beidz ciklu", "Sāk ciklu", "Ignorē kļūdu", "Atgriežas sākumā"},
            {"Datu tips", "Objekts", "Metodes dažādas formas", "Kompilatora kļūda"},
            {"compareTo", "equals", "hashCode", "clone"},
            {"Mainīgs", "Ko nevar mainīt", "Privāts", "Pagaidu vērtība"},
            {"Programma turpina", "Programma apstājas", "Funkcija ignorē", "Tiek izlabota"},
            {"Nemainīgs garums", "Dinamisks", "Objekts", "Nav noteikumu"},
            {"Bibliotēka", "Funkcija", "Klases grupa", "Programma"},
            {"javac", "java", "compile", "run"},
            {"Datubāze", "Izejas metode", "Klase", "Objekts"},
            {"extends", "inherits", "implements", "override"},
            {"Norāda uz pašreizējo klasi", "Norāda uz nākamo klasi", "Izsauc metodi", "Izveido objektu"}
        };

        int[] correctAnswers = {2, 3, 1, 1, 2, 1, 2, 2, 3, 1, 3, 2, 2, 2, 1, 3, 1, 2, 1, 1};

        // Greeting and name input
        String name = JOptionPane.showInputDialog(null, "Laipni lūdzam Java viktorīnā!\nIevadiet savu vārdu:", "Java viktorīna", JOptionPane.PLAIN_MESSAGE);
        if (name == null || name.trim().isEmpty()) {
            JOptionPane.showMessageDialog(null, "Jūs neievadījāt vārdu. Iziešana no programmas.", "Java viktorīna", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Shuffle and select 15 random questions
        ArrayList<Integer> indices = new ArrayList<>();
        for (int i = 0; i < questions.length; i++) {
            indices.add(i);
        }
        Collections.shuffle(indices);
        indices = new ArrayList<>(indices.subList(0, 15)); // Select 15 random questions

        // Start the quiz
        startTime = System.currentTimeMillis();
        runQuiz(name, questions, options, correctAnswers, indices);
    }

    private static void runQuiz(String name, String[] questions, String[][] options, int[] correctAnswers, ArrayList<Integer> indices) {
        JFrame quizFrame = new JFrame("Java Viktorīna");
        quizFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        quizFrame.setSize(500, 300);
        quizFrame.setLayout(new BorderLayout());

        // Timer Label
        JLabel timerLabel = new JLabel("Atlikušais laiks: 20:00", SwingConstants.RIGHT);
        timerLabel.setFont(new Font("Arial", Font.BOLD, 16));
        quizFrame.add(timerLabel, BorderLayout.NORTH);

        // Question Label
        JLabel questionLabel = new JLabel("", SwingConstants.CENTER);
        questionLabel.setFont(new Font("Arial", Font.PLAIN, 18));
        quizFrame.add(questionLabel, BorderLayout.CENTER);

        // Options Panel
        JPanel optionsPanel = new JPanel(new GridLayout(2, 2));
        JRadioButton[] radioButtons = new JRadioButton[4];
        ButtonGroup buttonGroup = new ButtonGroup();
        for (int i = 0; i < 4; i++) {
            radioButtons[i] = new JRadioButton();
            buttonGroup.add(radioButtons[i]);
            optionsPanel.add(radioButtons[i]);
        }
        quizFrame.add(optionsPanel, BorderLayout.SOUTH);

        // Next Question Button
        JButton nextButton = new JButton("Nākamais");
        quizFrame.add(nextButton, BorderLayout.EAST);

        // Quiz Logic
        Timer timer = new Timer(1000, new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                long elapsedTime = System.currentTimeMillis() - startTime;
                long remainingTime = maxTime - elapsedTime;
                if (remainingTime <= 0) {
                    quizEnded = true;
                    ((Timer) e.getSource()).stop();
                    saveResults(name);
                    showResults(name, quizFrame);
                } else {
                    long minutes = (remainingTime / 1000) / 60;
                    long seconds = (remainingTime / 1000) % 60;
                    timerLabel.setText(String.format("Atlikušais laiks: %02d:%02d", minutes, seconds));
                }
            }
        });
        timer.start();

        final int[] currentQuestion = {0};
        nextButton.addActionListener(e -> {
            if (quizEnded) return;

            for (int i = 0; i < radioButtons.length; i++) {
                if (radioButtons[i].isSelected()) {
                    if (i == correctAnswers[indices.get(currentQuestion[0])] - 1) {
                        score++;
                    } else {
                        wrongQuestions.add(questions[indices.get(currentQuestion[0])]);
                        wrongAnswers.add(radioButtons[i].getText());
                        correctAnswersList.add(options[indices.get(currentQuestion[0])][correctAnswers[indices.get(currentQuestion[0])] - 1]);
                    }
                    break;
                }
            }

            currentQuestion[0]++;
            if (currentQuestion[0] < indices.size()) {
                loadQuestion(currentQuestion[0], questions, options, indices, questionLabel, radioButtons);
            } else {
                quizEnded = true;
                timer.stop();
                saveResults(name);
                showResults(name, quizFrame);
            }
        });

        loadQuestion(currentQuestion[0], questions, options, indices, questionLabel, radioButtons);
        quizFrame.setVisible(true);
    }

    private static void loadQuestion(int questionIndex, String[] questions, String[][] options, ArrayList<Integer> indices,
                                      JLabel questionLabel, JRadioButton[] radioButtons) {
        questionLabel.setText("Jautājums: " + questions[indices.get(questionIndex)]);
        String[] currentOptions = options[indices.get(questionIndex)];
        for (int i = 0; i < radioButtons.length; i++) {
            radioButtons[i].setText(currentOptions[i]);
            radioButtons[i].setSelected(false);
        }
    }

    private static void saveResults(String name) {
        try {
            String filePath = System.getProperty("user.home") + "/Desktop/viktorina_rezultati.txt";
            double percentage = (score / 15.0) * 100;
            long elapsedSeconds = (System.currentTimeMillis() - startTime) / 1000;

            FileWriter writer = new FileWriter(filePath, true);
            writer.write("Vārds: " + name + "\n");
            writer.write("Punkti: " + score + "/15\n");
            writer.write("Procenti: " + String.format("%.2f", percentage) + "%\n");
            writer.write("Laiks: " + elapsedSeconds + " sekundes\n");
            writer.write("===================================\n");
            writer.close();

            JOptionPane.showMessageDialog(null, "Rezultāti tika saglabāti failā: " + filePath, "Rezultāti saglabāti", JOptionPane.INFORMATION_MESSAGE);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Kļūda saglabājot rezultātus: " + e.getMessage(), "Kļūda", JOptionPane.ERROR_MESSAGE);
        }
    }
    //Testa koments
    private static void showResults(String name, JFrame quizFrame) {
    	String performanceMessage;
        
        if (score == 15) {
            performanceMessage = "Apsveicam! Tu atbildēji pareizi uz visiem jautājumiem!";
        } else if (score >= 5) {
            performanceMessage = "Vajag iepazīties labāk ar Java valodu!";
        } else {
            performanceMessage = "Diemžēl liela daļa tavu atbilžu bija nepareizas.";
        }

        String message = "Paldies, " + name + ", ka piedalījāties viktorīnā!\n" +
                         "Jūsu punktu skaits: " + score + "/15\n" + performanceMessage;
        if (!wrongQuestions.isEmpty()) {
            message += "\nNepareizie atbilžu jautājumi un atbildes:\n";
            for (int i = 0; i < wrongQuestions.size(); i++) {
                message += "\nJautājums: " + wrongQuestions.get(i) + "\nTava atbilde: " + wrongAnswers.get(i) + "\nPareizā atbilde: " + correctAnswersList.get(i) + "\n";
            }
        }
        JOptionPane.showMessageDialog(null, message, "Java viktorīna", JOptionPane.INFORMATION_MESSAGE);
        quizFrame.dispose();
    }
}

