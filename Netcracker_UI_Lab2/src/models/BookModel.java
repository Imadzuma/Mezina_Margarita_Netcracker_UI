package models;

import javax.swing.table.AbstractTableModel;
import java.util.Vector;

import book.Book;
import org.json.simple.*;

public class BookModel extends AbstractTableModel {
    // Fields
    private Vector<Book> books = new Vector<>();

    // Constructors
    public BookModel() {}
    public BookModel(Book... books) {
        for(Book book : books)
            this.books.add(book.clone());
    }
    public BookModel(JSONArray jsonArray) {
        for (int i = 0; i < jsonArray.size(); ++i)
            this.books.add(Book.createJSONBook((JSONObject)jsonArray.get(i)));
    }

    // Override methods
    @Override
    public int getRowCount() {
        return books.size();
    }
    @Override
    public int getColumnCount() {
        return 5;
    }
    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        Book book =books.get(rowIndex);
        switch (columnIndex) {
            case 0: return rowIndex + 1;
            case 1: return book.getName();
            case 2: return book.getAuthorsNames();
            case 3: return book.getPrice();
            case 4: return book.getQty();
        }
        return null;
    }
    @Override
    public String getColumnName(int columnIndex) {
        switch (columnIndex) {
            case 0: return "№";
            case 1: return "Name";
            case 2: return "Authors";
            case 3: return "Price";
            case 4: return "Qty";
        }
        return "";
    }
    @Override
    public Class<?> getColumnClass(int columnIndex) {
        switch (columnIndex){
            case 0: return Integer.class;
            case 1: return String.class;
            case 2: return String.class;
            case 3: return Double.class;
            case 4: return Integer.class;
        }
        return Object.class;
    }

    // New methods
    public void add(Book book) {
        books.add(book.clone());
        fireTableDataChanged();
    }
    public void add(JSONArray jsonArray) throws NullPointerException {
        for (int i = 0; i < jsonArray.size(); ++i) {
            try {
                books.add(Book.createJSONBook((JSONObject) jsonArray.get(i)));
            }
            catch (NullPointerException e) {
                throw new NullPointerException("Can't create " + (i+1) + " book -->" + e.getMessage());
            }
        }
        fireTableDataChanged();
    }
    public void remove(int index) {
        if (index >= 0 && index < books.size()) {
            books.remove(index);
            fireTableDataChanged();
        }
    }
    public void clear() {
        books.clear();
        fireTableDataChanged();
    }
    public void setBook(int index, Book book) {
        if (index >= 0 && index < books.size()) {
            books.set(index, book);
            fireTableDataChanged();
        }
    }
    public Book getBook(int index) {
        if (index >= 0 && index < books.size())
            return books.get(index);
        return null;
    }
    public JSONArray getJSONArray() {
        JSONArray jsonArray = new JSONArray();
        for (int i = 0; i < books.size(); ++i)
            jsonArray.add(books.get(i).getJSONObject());
        return jsonArray;
    }
}
