/**
 * Created by romdhane on 13/06/17.
 */
public class Document {

    private String id;
    private String name;
    private Double score;

    public Document() {
    }


    public void setName(String name) {
        this.name = name;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Document(String id, String name, Double score) {
        this.id = id;

        this.name = name;
        this.score = score;
    }

    public String getName() {
        return name;
    }

    public Double getScore() {
        return score;
    }

    public String getId() {
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
        if (this.name == s) return true;
        else return false;

    }
}