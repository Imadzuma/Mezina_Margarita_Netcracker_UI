package ui;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.*;
import java.util.Scanner;

import book.Book;
import models.BookModel;
import org.json.simple.*;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class UIApp extends JFrame {
    // Constants
    final int ITEM_HEIGHT = 40;
    final int TABLE_HEIGHT = 30;
    Font appFont = new Font("Verbana", Font.PLAIN, 17);
    Font tableFont = new Font("Verbana", Font.PLAIN, 13);

    // Fields
    JMenuBar menuBar;
    BookModel bookModel;
    JTable table;
    Book selectedBook = null;
    File selectedFile = null;
    boolean wasChanged = false;

    // Constructors
    public UIApp() {
        // Create JFrame
        super("Library");
        setSize(1000, 600);
        setMinimumSize(new Dimension(300, 200));
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowListener() {
            @Override
            public void windowOpened(WindowEvent e) {}
            @Override
            public void windowClosing(WindowEvent e) {exitApp();}
            @Override
            public void windowClosed(WindowEvent e) {}
            @Override
            public void windowIconified(WindowEvent e) {}
            @Override
            public void windowDeiconified(WindowEvent e) {}
            @Override
            public void windowActivated(WindowEvent e) {}
            @Override
            public void windowDeactivated(WindowEvent e) {}
        });

        // Create menu
        menuBar = new JMenuBar();
        JMenu fileMenu = new JMenu("File");
        fileMenu.setFont(appFont);
        addMenuItem("New", (arg0)->newFile(), appFont, fileMenu);
        addMenuItem("Open", (arg0)->openFile(), appFont, fileMenu);
        addMenuItem("Save", (arg0)->saveFile(), appFont, fileMenu);
        addMenuItem("Save as", (arg0)->saveAsFile(), appFont, fileMenu);
        fileMenu.addSeparator();
        addMenuItem("Exit", (arg0)->exitApp(), appFont, fileMenu);
        menuBar.add(fileMenu);
        setJMenuBar(menuBar);

        // Add table
        bookModel = new BookModel();
        table = new JTable(bookModel);
        table.setFont(appFont);
        table.getColumnModel().getColumn(0).setMaxWidth(40);
        table.getTableHeader().setReorderingAllowed(false);
        table.getTableHeader().setFont(appFont);
        table.setRowHeight(TABLE_HEIGHT);
        JScrollPane jScrollPane=new JScrollPane(table);
        getContentPane().add(jScrollPane);

        // Add buttons
        JPanel buttonsPanel = new JPanel();
        buttonsPanel.setPreferredSize(new Dimension(this.getWidth(), ITEM_HEIGHT));
        buttonsPanel.setLayout(new GridLayout(1, 3, 10, 10));
        add(buttonsPanel, BorderLayout.SOUTH);
        addButton("Add", (arg0)->addBook(), appFont, buttonsPanel);
        addButton("Update", (arg0)->updateBook(), appFont, buttonsPanel);
        addButton("Delete", (arg0)->deleteBook(), appFont, buttonsPanel);

        // Pack & Display
        pack();
        setVisible(true);
    }

    // Create elements menu
    private JMenuItem addMenuItem(String name, ActionListener action, Font font, Container parent) {
        JMenuItem menuItem = new JMenuItem(name);
        menuItem.addActionListener(action);
        menuItem.setFont(font);
        parent.add(menuItem);
        return menuItem;
    }
    private JButton addButton(String name, ActionListener action, Font font, Container parent) {
        JButton button = new JButton(name);
        button.addActionListener(action);
        button.setFont(font);
        parent.add(button);
        return button;
    }

    // Menu actions
    private void newFile() {
        System.out.println("UIApp.NewFile");
        if (wasChanged) {
            Object[] options = { "Yes", "No" };
            int answer = JOptionPane.showOptionDialog(this, "Do you want to save changes?",
                    "Exit without saving", JOptionPane.YES_NO_OPTION,
                    JOptionPane.QUESTION_MESSAGE, null, options, options[0]);
            if (answer == 0)
                saveFile();
        }
        selectedFile = null;
        bookModel.clear();
        setTitle("Library");
        wasChanged = false;

    }
    private void openFile() {
        if (wasChanged) {
            Object[] options = { "Yes", "No" };
            int answer = JOptionPane.showOptionDialog(this, "Do you want to save changes?",
                    "Exit without saving", JOptionPane.YES_NO_OPTION,
                    JOptionPane.QUESTION_MESSAGE, null, options, options[0]);
            if (answer == 0)
                saveFile();
        }
        JFileChooser fileChooser = new JFileChooser(new File("./data"));
        fileChooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
        fileChooser.setFileFilter(new FileNameExtensionFilter("JSON files", "json"));
        if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            bookModel.clear();
            selectedFile = fileChooser.getSelectedFile();
            this.setTitle("Library: " + selectedFile.getName());
            parseFile();
            wasChanged = false;
        }
    }
    private void saveFile() {
        if (selectedFile==null)
            saveAsFile();
        else
            writeInFile();
        wasChanged = false;
    }
    private void saveAsFile() {
        JFileChooser fileChooser = new JFileChooser(new File("./data"));
        fileChooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
        fileChooser.setFileFilter(new FileNameExtensionFilter("JSON files", "json"));
        if(fileChooser.showSaveDialog(this)  == JFileChooser.APPROVE_OPTION) {
            selectedFile = fileChooser.getSelectedFile();
            this.setTitle("Library: " + selectedFile.getName());
            writeInFile();
            wasChanged = false;
        }
    }
    private void exitApp() {
        System.out.println("UIApp.exitApp");
        if (wasChanged) {
            Object[] options = { "Save", "Not Save", "Back"};
            int answer = JOptionPane.showOptionDialog(this, "Do you want to save changes?",
                    "Exit without saving", JOptionPane.YES_NO_OPTION,
                    JOptionPane.QUESTION_MESSAGE, null, options, options[0]);
            if (answer == 0) saveFile();
            if (answer == 0 || answer == 1) System.exit(0);
        }
        else
            System.exit(0);
    }

    // Buttons actions
    private void addBook() {
        JDialog dialog = new BookDialog(this, null);
        dialog.setVisible(true);
        if (selectedBook != null) {
            wasChanged = true;
            bookModel.add(selectedBook);
            selectedBook = null;
        }
    }
    private void updateBook() {
        int rowIndex = table.getSelectedRow();
        if (rowIndex != -1) {
            Book book = bookModel.getBook(rowIndex);
            JDialog dialog = new BookDialog(this, book);
            dialog.setVisible(true);
            if (selectedBook != null) {
                bookModel.setBook(rowIndex, selectedBook);
                wasChanged = true;
            }
        }
    }
    private void deleteBook() {
        int rowIndex = table.getSelectedRow();
        if (rowIndex != -1) {
            bookModel.remove(rowIndex);
            wasChanged = true;
        }
    }

    // Work with files
    private void parseFile() {
        if (!findFileFormat().equals("json"))
            JOptionPane.showMessageDialog(this, "Not JSON format: " + selectedFile.getAbsolutePath(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        else {
            try {
                FileReader fileReader = new FileReader(selectedFile.getAbsolutePath());
                Scanner readScanner = new Scanner(fileReader);
                String parsingString = "";
                while (readScanner.hasNextLine())
                    parsingString += readScanner.nextLine();
                parseString(parsingString);
                fileReader.close();
            } catch (FileNotFoundException e) {
                JOptionPane.showMessageDialog(this, "Can't open file: " + selectedFile.getAbsolutePath(),
                        "Error", JOptionPane.ERROR_MESSAGE);
            } catch (IOException e) {
                JOptionPane.showMessageDialog(this, "Can't close file: " + selectedFile.getAbsolutePath(),
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    private void parseString(String str) {
        JSONParser parser = new JSONParser();
        try {
            JSONArray jsonArray = (JSONArray)parser.parse(str);
            try {
                bookModel.add(jsonArray);
            }
            catch (NullPointerException e) {
                JOptionPane.showMessageDialog(this, "Can't parse json format: " + e.getMessage(),
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (ParseException e) {
            JOptionPane.showMessageDialog(this, "Can't parse file: " + selectedFile.getAbsolutePath(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    private void writeInFile() {
        try {
            FileWriter fileWriter = new FileWriter(selectedFile.getAbsolutePath());
            JSONArray jsonArray = bookModel.getJSONArray();
            String writeString = jsonArray.toJSONString();
            fileWriter.write(updateJSONString(writeString));
            fileWriter.close();
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Can't update file: " + selectedFile.getAbsolutePath(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }

    }
    String findFileFormat() {
        if (selectedFile==null) return "";
        String filePath = selectedFile.getAbsolutePath();
        int dotIndex = filePath.lastIndexOf('.');
        String fileFormat = filePath.substring(dotIndex+1);
        return fileFormat;
    }
    String updateJSONString(String jsonStr) {
        String result = "";
        int pos = 0;
        int bordeCount = 0;
        for (int i = 0; i < jsonStr.length() - 1; ++i) {
            char elem = jsonStr.charAt(i);
            if (elem == '{') {
                if (bordeCount == 0) {
                    result += jsonStr.substring(pos, i) + "\n";
                    pos = i;
                }
                bordeCount++;
            } else if (elem == '}')
                bordeCount--;
        }
        result += jsonStr.substring(pos, jsonStr.length()-1);
        if (jsonStr.charAt(jsonStr.length()-1)==']')
            result += "\n";
        result+=jsonStr.charAt(jsonStr.length()-1);
        return result;
    }

    // Main function
    public static void main(String[] args) {
        SwingUtilities.invokeLater(()->new UIApp());
    }
}
