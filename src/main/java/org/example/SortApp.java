package org.example;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class SortApp extends JFrame {

    private static final Dimension SMALL_SIZE = new Dimension(400, 200);
    private static final Dimension LARGE_SIZE = new Dimension(800, 600);
    private static final int MAX_NUMBERS = 1000;
    private static final int NUMBER_LIMIT = 30;

    private JTextField inputField;
    private JButton enterButton, sortButton, resetButton;
    private JPanel numbersPanel;
    private int[] numbers;
    private boolean ascending = true;

    public SortApp() {
        initUI();
        setupActions();
        initializeFrame();
    }

    private void initUI() {
        setTitle("Sort Master");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new CardLayout());

        JPanel introPanel = createIntroPanel();
        JPanel sortScreen = createSortScreen();

        add(introPanel, "Intro");
        add(sortScreen, "Sort");
    }

    private JPanel createIntroPanel() {
        JPanel introPanel = new JPanel(new SpringLayout());

        JLabel promptLabel = new JLabel("How many numbers to display?");
        inputField = new JTextField(5);

        setUpInputField();

        enterButton = createButton("Enter", new Color(173, 216, 230));

        introPanel.add(promptLabel);
        introPanel.add(inputField);
        introPanel.add(enterButton);

        layoutIntroPanel(introPanel, promptLabel);

        return introPanel;
    }

    private void layoutIntroPanel(JPanel introPanel, JLabel promptLabel) {
        SpringLayout layout = (SpringLayout) introPanel.getLayout();

        layout.putConstraint(SpringLayout.HORIZONTAL_CENTER, promptLabel, 0, SpringLayout.HORIZONTAL_CENTER, introPanel);
        layout.putConstraint(SpringLayout.NORTH, promptLabel, 20, SpringLayout.NORTH, introPanel);

        layout.putConstraint(SpringLayout.NORTH, inputField, 20, SpringLayout.SOUTH, promptLabel);
        layout.putConstraint(SpringLayout.HORIZONTAL_CENTER, inputField, 0, SpringLayout.HORIZONTAL_CENTER, introPanel);
        layout.putConstraint(SpringLayout.EAST, inputField, 0, SpringLayout.EAST, enterButton);
        layout.putConstraint(SpringLayout.WEST, inputField, 0, SpringLayout.WEST, enterButton);

        layout.putConstraint(SpringLayout.NORTH, enterButton, 20, SpringLayout.SOUTH, inputField);
        layout.putConstraint(SpringLayout.HORIZONTAL_CENTER, enterButton, 0, SpringLayout.HORIZONTAL_CENTER, introPanel);
    }

    private void setUpInputField() {
        inputField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                validateInput();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                validateInput();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                validateInput();
            }
        });
    }

    private void validateInput() {
        try {
            int value = Integer.parseInt(inputField.getText());
            if (value < 1 || value > MAX_NUMBERS) {
                enterButton.setEnabled(false);
                if (value > MAX_NUMBERS) {
                    showMessage("The number is too large. The allowable range is 1 to " + MAX_NUMBERS + " numbers");
                }
            } else {
                enterButton.setEnabled(true);
            }
        } catch (NumberFormatException ex) {
            enterButton.setEnabled(false);
        }
    }

    private JButton createButton(String text, Color bgColor) {
        JButton button = new JButton(text);
        button.setBackground(bgColor);
        button.setOpaque(true);
        button.setBorderPainted(false);
        return button;
    }

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

    private void setupActions() {
        enterButton.addActionListener(e -> handleEnterButton());
        sortButton.addActionListener(e -> sortNumbers());
        resetButton.addActionListener(e -> showIntroPanel());
    }

    private void handleEnterButton() {
        if (inputField.getText().isEmpty()) {
            showMessage("The input field is empty. Please enter a number.");
        } else {
            generateNumbers();
        }
    }

    private void showMessage(String message) {
        JOptionPane.showMessageDialog(this, message);
    }

    private void showIntroPanel() {
        ((CardLayout) getContentPane().getLayout()).show(getContentPane(), "Intro");
        setSize(SMALL_SIZE);
        setLocationRelativeTo(null);
    }

    private void generateNumbers() {
        if (!enterButton.isEnabled()) return;

        int count = Integer.parseInt(inputField.getText());
        numbers = new Random().ints(count - 1, 1, 1001).toArray();
        numbers = Arrays.copyOf(numbers, count);
        numbers[count - 1] = new Random().nextInt(NUMBER_LIMIT) + 1;

        updateNumbersPanel();

        ((CardLayout) getContentPane().getLayout()).show(getContentPane(), "Sort");
        setSize(LARGE_SIZE);
        setLocationRelativeTo(null);
        ascending = true;
    }

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

    private void numberButtonClicked(int number) {
        if (number <= NUMBER_LIMIT) {
            generateNumbers();
        } else {
            showMessage("Please select a value smaller or equal to " + NUMBER_LIMIT);
        }
    }

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
                int pivot = selectPivot(low, high);
                int i = low;
                int j = high - 1;

                while (true) {
                    while (ascending ? numbers[i] < pivot : numbers[i] > pivot) i++;
                    while (ascending ? numbers[j] > pivot : numbers[j] < pivot) j--;
                    if (i >= j) break;
                    swap(i, j);
                }
                swap(i, high - 1);
                return i;
            }

            private int selectPivot(int low, int high) {
                int middle = (low + high) / 2;
                if (numbers[low] > numbers[middle]) swap(low, middle);
                if (numbers[low] > numbers[high]) swap(low, high);
                if (numbers[middle] > numbers[high]) swap(middle, high);
                swap(middle, high - 1);
                return numbers[high - 1];
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

    private void initializeFrame() {
        setSize(SMALL_SIZE);
        setLocationRelativeTo(null);
        setVisible(true);
    }
}