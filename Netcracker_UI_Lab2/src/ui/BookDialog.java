package ui;

import book.Author;
import book.Book;
import models.AuthorModel;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

public class BookDialog extends JDialog {
    // Constants
    static final int LABEL_WIDTH = 150;
    static final int FIELD_WIDTH = 550;
    static final int TABLE_WIDTH = 30;
    static final int ITEM_HEIGHT = 30;
    static Font appFont = new Font("Verbana", Font.PLAIN, 20);
    static Font tableFont = new Font("Verbana", Font.PLAIN, 15);

    // Fields
    Book book;
    JTextField bookName;
    JTextField numberAuthors;
    AuthorModel authorModel;
    JTable authorTable;
    JTextField price;
    JTextField qty;

    // Constructor
    public BookDialog(JFrame parent, Book book) {
        super(parent, (book != null) ? "Update book" : "New book", true);
        setSize(750, 410);
        setResizable(false);
        setLocationRelativeTo(null);
        setLayout( new FlowLayout( FlowLayout.CENTER ));
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowListener() {
            @Override
            public void windowOpened(WindowEvent e) {}
            @Override
            public void windowClosing(WindowEvent e) {goBack();}
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

        // Name book
        addLabel("Book name: ", new Dimension(LABEL_WIDTH, ITEM_HEIGHT), appFont, JLabel.RIGHT, JLabel.TOP, this);
        bookName = addTextField(null, new Dimension(FIELD_WIDTH, ITEM_HEIGHT), new StringChecker(),appFont, this);

        // Authors counter
        addLabel("Authors count: ",  new Dimension(LABEL_WIDTH, ITEM_HEIGHT), appFont, JLabel.RIGHT, JLabel.TOP, this);
        numberAuthors = addTextField(null, new Dimension(FIELD_WIDTH, ITEM_HEIGHT), new CounterCheker(),appFont, this);

        // Authors
        addLabel("Authors: ",  new Dimension(LABEL_WIDTH, 6*ITEM_HEIGHT), appFont,JLabel.RIGHT, JLabel.TOP,  this);
        authorModel = new AuthorModel();
        authorTable = new JTable(authorModel);
        for (int i = 0; i < 5; ++i)
            authorTable.getColumnModel().getColumn(i).setCellRenderer(new statusChecker());
        authorTable.getColumnModel().getColumn(0).setPreferredWidth(TABLE_WIDTH);
        authorTable.getColumnModel().getColumn(0).setResizable(false);
        authorTable.getTableHeader().setReorderingAllowed(false);
        authorTable.getTableHeader().setFont(tableFont);
        authorTable.setFont(tableFont);
        authorTable.setRowHeight(ITEM_HEIGHT);
        addPane(authorTable, new Dimension(FIELD_WIDTH, 6 * ITEM_HEIGHT), false, this);

        // Price
        addLabel("Price: ", new Dimension(LABEL_WIDTH, ITEM_HEIGHT), appFont, JLabel.RIGHT, JLabel.TOP, this);
        price = addTextField(null, new Dimension(FIELD_WIDTH, ITEM_HEIGHT), new IntegerCheker(), appFont, this);

        // Qty
        addLabel("Qty: ", new Dimension(LABEL_WIDTH, ITEM_HEIGHT), appFont, JLabel.RIGHT, JLabel.TOP, this);
        qty = addTextField(null, new Dimension(FIELD_WIDTH, ITEM_HEIGHT), new IntegerCheker(), appFont, this);

        // Buttons
        JPanel buttonsPanel = addPanel(new Dimension(LABEL_WIDTH + FIELD_WIDTH, ITEM_HEIGHT), new GridLayout(1, 2, 5, 5), false, this);
        addButton("Okey", null, (arg0)->updateBook(),  buttonsPanel);
        addButton("Back", null, (arg0)->goBack(), buttonsPanel);

        // Params
        if (book != null) {
            bookName.setText(book.getName());
            numberAuthors.setText(Long.toString(book.getAuthors().length));
            price.setText(Long.toString(book.getPrice()));
            qty.setText(Long.toString(book.getQty()));
            for(Author author: book.getAuthors()) {
                authorModel.add(author);
            }
        }
        this.book = (book != null) ? book.clone() : new Book();
    }

    // Add Elements
    private JLabel addLabel(String name, Dimension size, Font font, int horAlig, int vertAlig, Container parent) {
        JLabel label = new JLabel(name);
        if (size != null) label.setPreferredSize(size);
        label.setFont(font);
        label.setHorizontalAlignment(horAlig);
        label.setVerticalAlignment(vertAlig);
        parent.add(label);
        return label;
    }
    private JTextField addTextField(String text, Dimension size, InputVerifier checker, Font font, Container parent) {
        JTextField textField = new JTextField();
        textField.setInputVerifier(checker);
        if (text != null) textField.setText(text);
        if (size != null) textField.setPreferredSize(size);
        textField.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        textField.setFont(font);
        parent.add(textField);
        return textField;
    }
    private JPanel addPanel(Dimension size, LayoutManager layout, boolean needBorders, Container parent) {
        JPanel panel = new JPanel(layout);
        if (size != null) panel.setPreferredSize(size);
        if (needBorders) panel.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        if (parent != null) parent.add(panel);
        return panel;
    }
    private JScrollPane addPane(Component body, Dimension size, boolean needBorders, Container parent) {
        JScrollPane pane = new JScrollPane(body);
        pane.setPreferredSize(size);
        if (needBorders) pane.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        parent.add(pane);
        return pane;
    }
    private JButton addButton(String text, Dimension size, ActionListener action, Container parent) {
        JButton button = new JButton(text);
        if (size != null) button.setPreferredSize(size);
        button.addActionListener(action);
        parent.add(button);
        return button;
    }

    // Buttons actions
    private void updateBook() {
        Book newBook = buildBook();
        if (checkValues(newBook.getName(), newBook.getAuthors(), newBook.getPrice(), newBook.getQty())) {
            ((UIApp) getParent()).selectedBook = newBook;
            dispose();
        }
    }
    private void goBack() {
        Book newBook = buildBook();
        if (book.equals(newBook))
            dispose();
        else {
            Object[] options = { "Yes", "No" };
            int answer = JOptionPane.showOptionDialog(this, "Do you want to exit without saving?",
                    "Exit without saving", JOptionPane.YES_NO_OPTION,
                    JOptionPane.QUESTION_MESSAGE, null, options, options[0]);
            if (answer == 0)
                dispose();
        }
    }

    // Renderers & Verifiers
    private class statusChecker extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int col) {
            JLabel label = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, col);
            String text = label.getText();
            boolean result = true;
            if (col == 1 || col == 2)
                result = text != null && !text.equals("");
            else if (col == 3) {
                try {
                    Long integer = Long.valueOf(text);
                    result = (integer != null && integer >= 0);
                } catch (NumberFormatException e) {
                    result = false;
                }
            }
            else if (col == 4 && text != null && !text.equals("")) {
                try{
                    Long integer = Long.valueOf(text);
                    result = (integer == null || integer >= 0);
                } catch (NumberFormatException e) {
                    result = false;
                }
            }
            if (hasFocus) label.setBorder(BorderFactory.createLineBorder(Color.GRAY));
            else if (result) label.setBorder(BorderFactory.createEmptyBorder(0,0,0,0));
            else label.setBorder(BorderFactory.createLineBorder(Color.RED));
            return label;
        }
    }
    private class StringChecker extends InputVerifier {
        @Override
        public boolean verify(JComponent input) {
            String text = ((JTextField)input).getText();
            boolean result = (text != null && !text.equals(""));
            if (result) input.setBorder(BorderFactory.createLineBorder(Color.GRAY));
            else input.setBorder(BorderFactory.createLineBorder(Color.RED));
            return result;
        }
    }
    private class CounterCheker extends InputVerifier {
        @Override
        public boolean verify(JComponent input) {
            String text = ((JTextField)input).getText();
            boolean result;
            try {
                Long value = Long.valueOf(text);
                result = value != null && value >= 0;
            }catch (NumberFormatException e) {
                result = false;
            }
            if (result) {
                authorModel.resize(Integer.valueOf(text));
                if (result) input.setBorder(BorderFactory.createLineBorder(Color.GRAY));
            }
            else input.setBorder(BorderFactory.createLineBorder(Color.RED));
            return result;
        }
    }
    private class IntegerCheker extends InputVerifier {
        @Override
        public boolean verify(JComponent input) {
            String text = ((JTextField)input).getText();
            boolean result;
            try {
                Long value = Long.valueOf(text);
                result = value != null && value >= 0;
            }catch (NumberFormatException e) {
                result = false;
            }
            if (result) input.setBorder(BorderFactory.createLineBorder(Color.GRAY));
            else input.setBorder(BorderFactory.createLineBorder(Color.RED));
            return result;
        }
    }

    // Check values
    private Book buildBook() {
        try {
            String name = bookName.getText();
            Author[] authors = authorModel.getAuthors();
            Long priceBook = Long.valueOf(price.getText());
            Long qtyBook = Long.valueOf(qty.getText());
            return new Book(name, authors, priceBook, qtyBook);
        } catch (NumberFormatException e) {
            return null;
        }
    }
    private boolean checkValues(String name, Author[] authors, Long price, Long qty) {
        boolean res = true;
        if (name == null || name.equals("")) res = false;
        if (price == null || price < 0) res = false;
        if (qty == null || qty < 0) res = false;
        for (Author author : authors) {
            if (author.getFirstName() == null || author.getFirstName().equals("")) res = false;
            if (author.getLastName() == null || author.getLastName().equals("")) res = false;
            if (author.getBirthYear() < 0) res = false;
        }
        return res;
    }
}
