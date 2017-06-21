# M2Internship_Entity_Resolution
This project handles a prototype of Vector space Model

****Classes******:
- Document: each document object is specified by it's id, it's name and it's ranking score
- EvaluationEntity: an evaluation entity is an object specified by it's corresponding query, it's resulting documents,
it's list of relevant documents and it's specific vocabulary (or bag of word)
-VSMTFxIDFVersion : handles the vector space model under it's different versions : Binary, TF , TFxIDF and
BM25
-Evaluation : handles the evaluation of the implemented vector space model