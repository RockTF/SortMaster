package org.example;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class SortApp extends JFrame {
    private JTextField inputField;
    private JButton enterButton, sortButton, resetButton;
    private JPanel numbersPanel;
    private int[] numbers;
    private boolean ascending = true;

    public SortApp() {
        initUI();
        setupActions();
    }

    private void initUI() {
        setTitle("Sort Master");
        setSize(800, 600);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new CardLayout());

        JPanel introPanel = createIntroPanel();
        JPanel sortScreen = createSortScreen();

        add(introPanel, "Intro");
        add(sortScreen, "Sort");
    }

    /** Create intro panel for inputting the number of elements */
    private JPanel createIntroPanel() {
        JPanel introPanel = new JPanel(new BorderLayout());
        JLabel promptLabel = new JLabel("How many numbers to display?");
        inputField = new JTextField(5);
        setUpInputField();

        enterButton = createButton("Enter", new Color(173, 216, 230));
        introPanel.add(promptLabel, BorderLayout.NORTH);
        introPanel.add(inputField, BorderLayout.CENTER);
        introPanel.add(enterButton, BorderLayout.SOUTH);

        return introPanel;
    }

    /** Initialize input field with validation logic */
    private void setUpInputField() {
        inputField.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { validateInput(); }
            public void removeUpdate(DocumentEvent e) { validateInput(); }
            public void changedUpdate(DocumentEvent e) { validateInput(); }
        });
    }

    /** Input validator for the number of elements */
    private void validateInput() {
        try {
            int value = Integer.parseInt(inputField.getText());
            if (value < 1 || value > 1000) {
                showMessage("The number is too large or too small, the allowable range is 1 to 1000.");
                inputField.setText("");
                enterButton.setEnabled(false);
            } else {
                enterButton.setEnabled(true);
            }
        } catch (NumberFormatException ex) {
            enterButton.setEnabled(false);
        }
    }

    /** Create a button with specified text and background color */
    private JButton createButton(String text, Color bgColor) {
        JButton button = new JButton(text);
        button.setBackground(bgColor);
        button.setOpaque(true);
        button.setBorderPainted(false);
        return button;
    }

    /** Create the screen where numbers will be sorted */
    private JPanel createSortScreen() {
        numbersPanel = new JPanel();

        sortButton = createButton("Sort", new Color(0, 128, 0));
        resetButton = createButton("Reset", new Color(0, 128, 0));

        JPanel sortControlPanel = new JPanel();
        sortControlPanel.add(sortButton);
        sortControlPanel.add(resetButton);

        JPanel sortScreen = new JPanel(new BorderLayout());
        sortScreen.add(new JScrollPane(numbersPanel), BorderLayout.CENTER);
        sortScreen.add(sortControlPanel, BorderLayout.SOUTH);

        return sortScreen;
    }

    /** Set up actions for the buttons */
    private void setupActions() {
        enterButton.addActionListener(e -> handleEnterButton());
        sortButton.addActionListener(e -> sortNumbers());
        resetButton.addActionListener(e -> showIntroPanel());
    }

    /** Handle the Enter button click */
    private void handleEnterButton() {
        if (inputField.getText().isEmpty()) {
            showMessage("The input field is empty. Please enter a number.");
        } else {
            generateNumbers();
        }
    }

    /** Show a message dialog */
    private void showMessage(String message) {
        JOptionPane.showMessageDialog(this, message);
    }

    /** Show the intro panel */
    private void showIntroPanel() {
        ((CardLayout) getContentPane().getLayout()).show(getContentPane(), "Intro");
    }

    /** Generate random numbers */
    private void generateNumbers() {
        if (!enterButton.isEnabled()) return;

        int count = Integer.parseInt(inputField.getText());
        numbers = new Random().ints(count - 1, 1, 1001).toArray();
        numbers = Arrays.copyOf(numbers, count);
        numbers[count - 1] = new Random().nextInt(30) + 1;
        updateNumbersPanel();
        ((CardLayout) getContentPane().getLayout()).show(getContentPane(), "Sort");
        ascending = true;
    }

    /** Update the panel displaying numbers */
    private void updateNumbersPanel() {
        numbersPanel.removeAll();
        int columns = (numbers.length + 9) / 10;
        numbersPanel.setLayout(new GridLayout(0, columns, 10, 10));
        for (int number : numbers) {
            JButton button = new JButton(String.valueOf(number));
            button.addActionListener(e -> numberButtonClicked(number));
            numbersPanel.add(button);
        }
        numbersPanel.revalidate();
        numbersPanel.repaint();
    }

    /** Handle number button clicks */
    private void numberButtonClicked(int number) {
        if (number <= 30) {
            generateNumbers();
        } else {
            showMessage("Please select a value smaller or equal to 30.");
        }
    }

    /** Sort the numbers using quicksort */
    private void sortNumbers() {
        SwingWorker<Void, int[]> worker = new SwingWorker<>() {
            @Override
            protected Void doInBackground() {
                quickSort(0, numbers.length - 1);
                return null;
            }

            @Override
            protected void process(List<int[]> chunks) {
                int[] lastChunk = chunks.get(chunks.size() - 1);
                numbers = lastChunk.clone();
                updateNumbersPanel();
            }

            private void quickSort(int low, int high) {
                if (low < high) {
                    int pi = partition(low, high);
                    publish(numbers.clone());
                    quickSort(low, pi - 1);
                    quickSort(pi + 1, high);
                }
            }

            private int partition(int low, int high) {
                int pivot = numbers[high];
                int i = low - 1;
                for (int j = low; j < high; j++) {
                    boolean condition = ascending ? numbers[j] < pivot : numbers[j] > pivot;
                    if (condition) {
                        i++;
                        swap(i, j);
                    }
                }
                swap(i + 1, high);
                return i + 1;
            }

            private void swap(int i, int j) {
                int temp = numbers[i];
                numbers[i] = numbers[j];
                numbers[j] = temp;
            }
        };
        worker.execute();
        ascending = !ascending;
    }
}