package models;

import book.Author;

import javax.swing.table.AbstractTableModel;
import java.util.Vector;

public class AuthorModel extends AbstractTableModel {
    // Fields
    private int size = 0;
    private Vector<String> firstNames = new Vector<>();
    private Vector<String> lastNames = new Vector<>();
    private Vector<Long> birthYears = new Vector<>();
    private Vector<Long> deadYears = new Vector<>();

    // Constructors
    public AuthorModel() {}

    // Override methods
    @Override
    public int getRowCount() {
        return size;
    }
    @Override
    public int getColumnCount() {
        return 5;
    }
    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        switch (columnIndex) {
            case 0: return rowIndex + 1;
            case 1: return firstNames.get(rowIndex);
            case 2: return lastNames.get(rowIndex);
            case 3: return birthYears.get(rowIndex);
            case 4: return deadYears.get(rowIndex);
        }
        return null;
    }
    @Override
    public String getColumnName(int columnIndex) {
        switch (columnIndex) {
            case 0: return "№";
            case 1: return "First name";
            case 2: return "Last name";
            case 3: return "Birth year";
            case 4: return "Dead year";
        }
        return "";
    }
    @Override
    public Class<?> getColumnClass(int columnIndex) {
        switch (columnIndex){
            case 0: return Integer.class;
            case 1: return String.class;
            case 2: return String.class;
            case 3: return Long.class;
            case 4: return Long.class;
        }
        return Object.class;
    }
    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        switch (columnIndex){
            case 0: return false;
            case 1: return true;
            case 2: return true;
            case 3: return true;
            case 4: return true;
        }
        return false;
    };
    @Override
    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
        switch (columnIndex) {
            case 1:
                firstNames.set(rowIndex, (String)aValue);
                break;
            case 2:
                lastNames.set(rowIndex, (String)aValue);
                break;
            case 3:
                birthYears.set(rowIndex, (Long)aValue);
                break;
            case 4:
                deadYears.set(rowIndex, (Long)aValue);
        }
    }

    // New methods
    public void add(Author author) {
        firstNames.add(author.getFirstName());
        lastNames.add(author.getLastName());
        birthYears.add(author.getBirthYear());
        if (author.getDeadYear() != -1) deadYears.add(author.getDeadYear());
        else deadYears.add(null);
        size++;
        fireTableDataChanged();
    }
    public void resize(int newSize) {
        if (size < newSize) {
            for (int i = size; i < newSize; ++i) {
                firstNames.add("");
                lastNames.add("");
                birthYears.add(null);
                deadYears.add(null);
            }
        }
        else {
            for (int i = size-1; i >= newSize; --i) {
                firstNames.remove(i);
                lastNames.remove(i);
                birthYears.remove(i);
                deadYears.remove(i);
            }
        }
        size = newSize;
        fireTableDataChanged();
    }
    public Author[] getAuthors() {
        Author[] authors = new Author[size];
        for (int i = 0; i < size; ++i)
            authors[i] = new Author(firstNames.get(i), lastNames.get(i), birthYears.get(i), deadYears.get(i));
        return authors;
    }
}

