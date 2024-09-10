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
    private static final int RANDOM_LOWER_BOUND = 1;
    private static final int RANDOM_UPPER_BOUND = 1001;
    private static final int SORT_SLEEP_DURATION_MS = 100;

    private static final int NUM_ROWS = 10;
    private static final int NUM_COLUMNS_PER_ROW = 10;
    private static final int GRID_HORIZONTAL_GAP = 5;
    private static final int GRID_VERTICAL_GAP = 5;

    private static final Color BUTTON_ENTER_COLOR = new Color(173, 216, 230);
    private static final Color BUTTON_SORT_COLOR = new Color(0, 128, 0);
    private static final Color NUMBER_BUTTON_BACKGROUND_COLOR = Color.BLUE;
    private static final Color NUMBER_BUTTON_TEXT_COLOR = Color.WHITE;

    private final Random random = new Random();

    private JTextField inputField;
    private JButton enterButton, sortButton, resetButton;
    private JPanel numbersPanel;
    private int[] numbers;
    private boolean ascending = false;
    private SortWorker currentSortWorker;

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
        enterButton = createButton("Enter", BUTTON_ENTER_COLOR);

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
        sortButton = createButton("Sort", BUTTON_SORT_COLOR);
        resetButton = createButton("Reset", BUTTON_SORT_COLOR);

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
        resetButton.addActionListener(e -> resetAction());
    }

    private void handleEnterButton() {
        if (inputField.getText().isEmpty()) {
            showMessage("The form cannot be empty, please enter a number in the range from 1 to " + MAX_NUMBERS);
        } else {
            int count = Integer.parseInt(inputField.getText());
            generateNumbers(count);
        }
    }

    private void resetAction() {
        if (currentSortWorker != null && !currentSortWorker.isDone()) {
            currentSortWorker.cancel(true);
        }
        inputField.setText("");
        showIntroPanel();
    }

    private void showIntroPanel() {
        ((CardLayout) getContentPane().getLayout()).show(getContentPane(), "Intro");
        setSize(SMALL_SIZE);
        setLocationRelativeTo(null);
        ascending = false;
    }

    private void generateNumbers(int count) {
        if (!enterButton.isEnabled()) return;

        numbers = random.ints(count - 1, RANDOM_LOWER_BOUND, RANDOM_UPPER_BOUND).toArray();
        numbers = Arrays.copyOf(numbers, count);
        numbers[count - 1] = random.nextInt(NUMBER_LIMIT) + 1;

        updateNumbersPanel(numbers);

        ((CardLayout) getContentPane().getLayout()).show(getContentPane(), "Sort");
        setSize(LARGE_SIZE);
        setLocationRelativeTo(null);
        ascending = false;
    }

    private void updateNumbersPanel(int[] numbers) {
        numbersPanel.removeAll();
        int totalNumbers = numbers.length;
        int columnsNeeded = (totalNumbers + NUM_COLUMNS_PER_ROW - 1) / NUM_COLUMNS_PER_ROW;

        numbersPanel.setLayout(new GridLayout(NUM_ROWS, columnsNeeded, GRID_HORIZONTAL_GAP, GRID_VERTICAL_GAP));

        for (int row = 0; row < NUM_ROWS; row++) {
            for (int col = 0; col < columnsNeeded; col++) {
                int index = col * NUM_ROWS + row;
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
        button.setBackground(NUMBER_BUTTON_BACKGROUND_COLOR);
        button.setForeground(NUMBER_BUTTON_TEXT_COLOR);
        button.setOpaque(true);
        button.setBorderPainted(false);
        button.addActionListener(e -> numberButtonClicked(Integer.parseInt(e.getActionCommand())));
        return button;
    }

    private void numberButtonClicked(int number) {
        if (number <= NUMBER_LIMIT) {
            if (currentSortWorker != null && !currentSortWorker.isDone()) {
                currentSortWorker.cancel(true);
            }
            SwingUtilities.invokeLater(() -> generateNumbers(number));
        } else {
            showMessage("Please select a value smaller or equal to " + NUMBER_LIMIT);
        }
    }

    private void sortNumbers() {
        if (currentSortWorker != null && !currentSortWorker.isDone()) {
            currentSortWorker.cancel(true);
        }
        currentSortWorker = new SortWorker(numbers.clone(), ascending);
        currentSortWorker.execute();
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
            updateNumbersPanel(chunks.getLast());
        }

        @Override
        protected void done() {
            numbers = arr;
            updateNumbersPanel(numbers);
        }

        private void quickSort(int[] arr, int low, int high, boolean ascending) {
            if (low < high) {
                if (isCancelled()) return;
                int pi = partition(arr, low, high, ascending);
                publish(Arrays.copyOf(arr, arr.length));
                try {
                    Thread.sleep(SORT_SLEEP_DURATION_MS);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
                quickSort(arr, low, pi - 1, ascending);
                publish(Arrays.copyOf(arr, arr.length));
                quickSort(arr, pi + 1, high, ascending);
                publish(Arrays.copyOf(arr, arr.length));
            }
        }

        private int partition(int[] arr, int low, int high, boolean ascending) {
            int pivot = arr[high];
            int i = low - 1;
            for (int j = low; j < high; j++) {
                if (isCancelled()) return high;
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

    private void showMessage(String message) {
        JOptionPane.showMessageDialog(this, message);
    }
}