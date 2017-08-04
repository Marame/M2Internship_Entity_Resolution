package practice1.entities;

import java.util.List;

/**
 * Created by romdhane on 15/06/17.
 */

// The evaluation entity consists in the query with its associated resulting documents, relevant documents and bag of words

public class EvaluationEntity {
    private Document query;
    private List<Document>  relevant_documents;





    public void setRelevant_documents(List<Document> relevant_documents) {
        this.relevant_documents = relevant_documents;
    }

    public void setQuery(Document query) {

        this.query = query;
    }

    public List<Document> getRelevant_documents() {

        return relevant_documents;
    }

    public Document getQuery() {

        return query;
    }


    public EvaluationEntity(Document query, List<Document>  relevant_documents) {

        this.query = query;
        this.relevant_documents = relevant_documents;
    }


    public EvaluationEntity() {

    }
}
