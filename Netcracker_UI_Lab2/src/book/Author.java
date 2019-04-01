package book;

import org.json.simple.*;

public class Author {
    // Fields
    private String firstName = "";
    private String lastName = "";
    private long birthYear = -1;
    private long deadYear = -1;

    // Constructors
    public Author() {}
    public Author(String firstName, String lastName, Long birthYear, Long deadYear) {
        this.firstName = firstName;
        this.lastName = lastName;
        if (birthYear != null) this.birthYear = birthYear;
        if (deadYear != null) this.deadYear = deadYear;
    }

    // Getters & Setters
    public String getFirstName() {
        return firstName;
    }
    public String getLastName() {
        return lastName;
    }
    public long getBirthYear() {
        return birthYear;
    }
    public long getDeadYear() {
        return deadYear;
    }
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }
    public void setBirthYear(int birthYear) {
        this.birthYear = birthYear;
    }
    public void setDeadYear(int deadYear) {
        this.deadYear = deadYear;
    }

    // Override methods
    @Override
    protected Object clone() {
        return new Author(firstName, lastName, birthYear, deadYear);
    }
    @Override
    public String toString() {
        return "Author{" +
                "firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", birthYear=" + birthYear +
                ", deadYear=" + deadYear +
                '}';
    }
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof  Author)) return false;
        Author author = (Author)obj;
        return firstName.equals(author.firstName) && lastName.equals(author.lastName) &&
                (birthYear == author.birthYear) && (deadYear == author.deadYear);
    }
    @Override
    public int hashCode() {
        int result = 102;
        result = 31 * result + firstName.hashCode();
        result = 31 * result + lastName.hashCode();
        result = 31 * result + (int)birthYear;
        result = 31 * result + (int)deadYear;
        return result;
    }

    // New methods
    public String getName() {
        return firstName + " " + lastName + "(" + birthYear + "-" + ((deadYear != -1) ? deadYear : "...") + ")";
    }
    public JSONObject getJSONObject() {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("firstName", firstName);
        jsonObject.put("lastName", lastName);
        jsonObject.put("birthYear", birthYear);
        if (deadYear != -1)
        jsonObject.put("deadYear", deadYear);
        return jsonObject;
    }
    public static Author createJSONAuthor(JSONObject jsonObject) throws NullPointerException {
        if (jsonObject.get("firstName")==null) throw new NullPointerException("firstName is null");
        if (jsonObject.get("lastName")==null) throw new NullPointerException("lastName is null");
        if (jsonObject.get("birthYear")==null) throw new NullPointerException("birthYear is null");
        String firstName = (String)(jsonObject.get("firstName"));
        String lastName = (String)(jsonObject.get("lastName"));
        Long birthYear = (Long)(jsonObject.get("birthYear"));
        Long deadYear = (Long)(jsonObject.get("deadYear"));
        if (firstName.equals("")) throw new NullPointerException("firstName is empty");
        if (lastName.equals("")) throw new  NullPointerException("lastName is empty");
        if (birthYear==null) throw new NullPointerException("birthYear is not a number");
        if (birthYear < 0) throw new NullPointerException("birthYear is negative");
        if (jsonObject.get("deadYear")!=null && deadYear==null) throw new NullPointerException("deadYear is not a number");
        if (jsonObject.get("deadYear")!=null && deadYear < 0) throw new NullPointerException("deadYear is negative");
        return new Author(firstName, lastName, birthYear, deadYear);
    }
}
