package book;

import org.json.simple.*;
import java.util.Arrays;

public class Book {
    // Fields
    private String name = "";
    private Author[] authors = new Author[0];
    private long price = 0;
    private long qty = 0;

    // Constructors
    public Book() {}
    public Book(String name, Author[] authors, Long price, Long qty) {
        this.name = name;
        this.authors = new Author[authors.length];
        for (int i = 0; i < authors.length; ++i)
            this.authors[i] = (Author)authors[i].clone();
        this.price = price;
        this.qty = qty;
    }

    // Getters & Setters
    public String getName() {
        return name;
    }
    public Author[] getAuthors() {
        return authors;
    }
    public long getPrice() {
        return price;
    }
    public long getQty() {
        return qty;
    }
    public void setName(String name) {
        this.name = name;
    }
    public void setAuthors(Author[] authors) {
        this.authors = new Author[authors.length];
        for (int i = 0; i < authors.length; ++i)
            this.authors[i] = (Author)authors[i].clone();
    }
    public void setPrice(long price) {
        this.price = price;
    }
    public void setQty(long qty) {
        this.qty = qty;
    }

    // Override methods
    @Override
    public String toString() {
        return "Book{" +
                "name='" + name + '\'' +
                ", authors=" + Arrays.toString(authors) +
                ", price=" + price +
                ", qty=" + qty +
                '}';
    }
    @Override
    public int hashCode() {
        int result = 10;
        result = 31 * result + name.hashCode();
        result = 31 * result + Arrays.hashCode(authors);
        return result;
    }
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof Book)) return false;
        Book book = (Book)obj;
        if (!name.equals(book.name) || (authors.length != book.authors.length))
            return false;
        for (int i = 0; i < authors.length; ++i) {
            if (!authors[i].equals(book.authors[i])) return false;
        }
        return true;
    }
    @Override
    public Book clone() {
        return new Book(name, authors, price, qty);
    }

    // New methods
    public String getAuthorsNames() {
        String res = "";
        if (authors.length != 0) res += authors[0].getName();
        for (int i = 1; i < authors.length; ++i)
            res+= ", " + authors[i].getName();
        return res;
    }
    public JSONObject getJSONObject() {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("name", name);
        JSONArray jsonArray = new JSONArray();
        for (Author author : authors)
            jsonArray.add(author.getJSONObject());
        jsonObject.put("authors", jsonArray);
        jsonObject.put("price", price);
        jsonObject.put("qty", qty);
        return jsonObject;
    }
    public static Book createJSONBook(JSONObject jsonObject) throws NullPointerException {
        if (jsonObject.get("name") == null) throw new NullPointerException("name is null");
        if (jsonObject.get("authors")==null) throw new NullPointerException("authors is null");
        if (jsonObject.get("price")==null) throw new NullPointerException("price is null");
        if (jsonObject.get("qty")==null) throw new NullPointerException("qty is null");
        String name = (String)jsonObject.get("name");
        JSONArray arr = (JSONArray)jsonObject.get("authors");
        Long price = (Long)jsonObject.get("price");
        Long qty = (Long)jsonObject.get("qty");
        if (name.equals("")) throw new NullPointerException("name is empty");
        if (price==null) throw new NullPointerException("price is not a number");
        if (price < 0) throw new NullPointerException("price is negative");
        if (qty==null) throw new NullPointerException("qty is not a number");
        if (qty < 0) throw new NullPointerException("qty is negative");
        Author authors[] = new Author[arr.size()];
        for (int i = 0; i < arr.size(); ++i) {
            try {
                authors[i] = Author.createJSONAuthor((JSONObject) arr.get(i));
            }
            catch (NullPointerException e) {
                throw new NullPointerException("Can't create " + (i+1) + " author --> " + e.getMessage());
            }
        }
        return new Book(name, authors, price, qty);
    }
}
