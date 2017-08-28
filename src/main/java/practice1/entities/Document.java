package practice1.entities;

/**
 * Created by romdhane on 13/06/17.
 */
public class Document {

    private Integer id;
    private String name;
    private Double score;

    public Document() {
    }


    public void setContent(String name) {
        this.name = name;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Document(int id, String name, Double score) {
        this.id = id;

        this.name = name;
        this.score = score;
    }

    public String getContent() {
        return name;
    }

    public Double getScore() {
        return score;
    }

    public Integer getId() {
        return id;
    }

    public void setScore(Double score) {
        this.score = score;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Document)) return false;

        Document document = (Document) o;

        return getId() != null ? getId().equals(document.getId()) : document.getId() == null;
    }

    @Override
    public int hashCode() {
        return getId() != null ? getId().hashCode() : 0;
    }

    public String toString() {
        return this.getId() + ", " + this.getContent() + ", " + this.getScore();
    }
}