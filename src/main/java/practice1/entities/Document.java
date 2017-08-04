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


    public void setName(String name) {
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

    public int getId() {
        return id;
    }

    public void setScore(Double score) {
        this.score = score;
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }

    public boolean equals(String s) {
        if (this.name.toLowerCase().equals(s.toLowerCase())) return true;
        else return false;

    }
}