package Entities;

import java.util.List;

/**
 * Created by romdhane on 15/06/17.
 */

// The evaluation entity consists in the query with its associated resulting documents, relevant documents and bag of words

public class EvaluationEntity {
    private Document query;
    private List<Document> documents;
    private List<Document>  relevant_documents;
    private List<String> bagOfWords;


    public EvaluationEntity(Document query, List<Document> documents, List<Document> relevant_documents, List<String> bagOfWords) {
        this.query = query;
        this.documents = documents;
        this.relevant_documents = relevant_documents;
        this.bagOfWords = bagOfWords;
    }

    public void setBagOfWords(List<String> bagOfWords) {
        this.bagOfWords = bagOfWords;
    }

    public List<String> getBagOfWords() {

        return bagOfWords;
    }

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

    public EvaluationEntity(Document query, List<Document> documents, List<Document> relevant_documents) {
        this.query = query;
        this.documents = documents;
        this.relevant_documents = relevant_documents;
    }

    public List<Document> getDocuments() {

        return documents;
    }

    public EvaluationEntity(Document query, List<Document>  relevant_documents) {

        this.query = query;
        this.relevant_documents = relevant_documents;
    }

    public void setDocuments(List<Document> documents) {
        this.documents = documents;
    }

    public EvaluationEntity() {

    }
}
