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
        String text = inputField.getText();
        if (text.isEmpty()) {
            setInputValid();
            return;
        }

        try {
            int value = Integer.parseInt(text);
            if (value < 1 || value > MAX_NUMBERS) {
                setInputInvalid(value > MAX_NUMBERS);
            } else {
                setInputValid();
            }
        } catch (NumberFormatException ex) {
            setInputInvalid(false);
        }
    }

    private void setInputValid() {
        enterButton.setEnabled(true);
    }

    private void setInputInvalid(boolean isTooLarge) {
        enterButton.setEnabled(false);
        if (isTooLarge) {
            showMessage("The number is too large. The allowable range is 1 to " + MAX_NUMBERS + " numbers");
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

        JPanel rightPanel = new JPanel();
        rightPanel.setLayout(new BoxLayout(rightPanel, BoxLayout.Y_AXIS));
        rightPanel.add(sortButton);
        rightPanel.add(Box.createVerticalStrut(10));
        rightPanel.add(resetButton);

        JPanel sortScreen = new JPanel(new BorderLayout());
        sortScreen.add(new JScrollPane(numbersPanel), BorderLayout.CENTER);
        sortScreen.add(rightPanel, BorderLayout.EAST);

        return sortScreen;
    }

    private void setupActions() {
        enterButton.addActionListener(e -> handleEnterButton());
        sortButton.addActionListener(e -> sortNumbers());
        resetButton.addActionListener(e -> showIntroPanel());
    }

    private void handleEnterButton() {
        if (inputField.getText().isEmpty()) {
            showMessage("The form cannot be empty, please enter a number in the range from 1 to " + MAX_NUMBERS);
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

        updateNumbersPanel(numbers);

        ((CardLayout) getContentPane().getLayout()).show(getContentPane(), "Sort");
        setSize(LARGE_SIZE);
        setLocationRelativeTo(null);
        ascending = true;
    }

    private void updateNumbersPanel(int[] numbers) {
        numbersPanel.removeAll();
        int totalNumbers = numbers.length;
        int columnsNeeded = (totalNumbers + 9) / 10;

        numbersPanel.setLayout(new GridLayout(10, columnsNeeded, 5, 5));

        for (int row = 0; row < 10; row++) {
            for (int col = 0; col < columnsNeeded; col++) {
                int index = col * 10 + row;
                if (index < totalNumbers) {
                    JButton button = createNumberButton(numbers[index]);
                    numbersPanel.add(button);
                } else {
                    numbersPanel.add(new JLabel(""));
                }
            }
        }

        numbersPanel.revalidate();
        numbersPanel.repaint();
    }

    private JButton createNumberButton(int number) {
        JButton button = new JButton(String.valueOf(number));
        button.setBackground(Color.BLUE);
        button.setForeground(Color.WHITE);
        button.setOpaque(true);
        button.setBorderPainted(false);
        button.addActionListener(e -> numberButtonClicked(Integer.parseInt(e.getActionCommand())));
        return button;
    }

    private void numberButtonClicked(int number) {
        if (number <= NUMBER_LIMIT) {
            generateNumbers();
        } else {
            showMessage("Please select a value smaller or equal to " + NUMBER_LIMIT);
        }
    }

    private void sortNumbers() {
        new SortWorker(numbers.clone(), ascending).execute();
        ascending = !ascending;
    }

    private class SortWorker extends SwingWorker<Void, int[]> {
        private final int[] arr;
        private final boolean ascending;

        public SortWorker(int[] arr, boolean ascending) {
            this.arr = arr;
            this.ascending = ascending;
        }

        @Override
        protected Void doInBackground() {
            quickSort(arr, 0, arr.length - 1, ascending);
            return null;
        }

        @Override
        protected void process(List<int[]> chunks) {
            updateNumbersPanel(chunks.get(chunks.size() - 1));
        }

        @Override
        protected void done() {
            numbers = arr;
            updateNumbersPanel(numbers);
        }

        private void quickSort(int[] arr, int low, int high, boolean ascending) {
            if (low < high) {
                int pi = partition(arr, low, high, ascending);
                publish(arr.clone());
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
                quickSort(arr, low, pi - 1, ascending);
                quickSort(arr, pi + 1, high, ascending);
            }
        }

        private int partition(int[] arr, int low, int high, boolean ascending) {
            int pivot = arr[high];
            int i = low - 1;
            for (int j = low; j < high; j++) {
                if ((ascending && arr[j] <= pivot) || (!ascending && arr[j] >= pivot)) {
                    i++;
                    int temp = arr[i];
                    arr[i] = arr[j];
                    arr[j] = temp;
                }
            }
            int temp = arr[i + 1];
            arr[i + 1] = arr[high];
            arr[high] = temp;
            return i + 1;
        }
    }

    private void initializeFrame() {
        setSize(SMALL_SIZE);
        setLocationRelativeTo(null);
        setVisible(true);
    }
}