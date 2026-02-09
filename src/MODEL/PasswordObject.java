package MODEL;

import java.util.Objects;

public class PasswordObject {
    private String title;
    private String password;
    private String login;
    private String url;
    private String notes;
    private String category;
    public PasswordObject(String title, String password, String login, String url, String notes, String category) {
        this.title = title;
        this.password = password;
        this.login = login;
        this.url = url;
        this.notes = notes;
        this.category = category;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    @Override
    public String toString() {
        return "PasswordObject{" +
                "title='" + title + '\'' +
                ", password='" + password + '\'' +
                ", login='" + login + '\'' +
                ", url='" + url + '\'' +
                ", notes='" + notes + '\'' +
                ", category='" + category + '\'' +
                '}';
    }
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PasswordObject)) return false;
        PasswordObject that = (PasswordObject) o;
        return Objects.equals(title, that.title) &&
                Objects.equals(password, that.password) &&
                Objects.equals(login, that.login) &&
                Objects.equals(url, that.url) &&
                Objects.equals(notes, that.notes) &&
                Objects.equals(category, that.category);
    }

    @Override
    public int hashCode() {
        return Objects.hash(title, password, login, url, notes, category);
    }
}
