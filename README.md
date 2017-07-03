# M2Internship_Entity_Resolution

This project handles prototypesof :
 - Vector space Model : term vector model, which is an algebraic model for representing text
 documents  as vectors of identifiers, such as, for example, index terms. It is used in information filtering,
 information retrieval, indexing and relevancy rankings.
 - Language model:  probability distribution over sequences of words. Given such a sequence, say of length m, it assigns a probability
P(w1, w2...wm) to the whole sequence.

****Classes******:
- Document: each document object is specified by it's id, it's name and it's ranking score
- EvaluationEntity: an evaluation entity is an object specified by it's corresponding query, it's resulting documents,
it's list of relevant documents and it's specific vocabulary (or bag of word)

- VSMTFxIDFVersion : handles the vector space model under it's different versions : Binary, TF , TFxIDF and
BM25
--> Binary: 0/1 indicating the term is present in the document or not
--> TF: the scoring function between a query and a document is obtained by calculating the dot product between TF
  vectors of each of the query and the candidate document
--> TF/IDF:the scoring function between a query and a document is obtained by calculating the dot product between TF/IDF
    vectors of each of the query and the candidate document
--> BM25 : the same thing, but the TF is normalised using a k factor
- Language Model : language model under different smoothing versions :jelinek mercer and dirichlet prior.

The term smoothing describes techniques for
adjusting the maximum likelihood estimate of probabilities to produce
more accurate probabilities. The name smoothing comes from the fact that these techniques
tend to make distributions more uniform, by adjusting low probabilities such as zero probabilities

- Evaluation : handles the evaluation of the implemented vector space model, by computing the micro average precision/recall,
the macro average precision/recall, mean average precision

*****Running the project:
mvn clean install
