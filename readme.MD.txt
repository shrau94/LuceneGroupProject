
Format for execution of jar file:

* Traverse to - "/home/ubuntu/Group2_IR" path using 'cd' command
* java -jar IRGroup2_LuceneSearchEngine.jar

   ****OR****

Results - 
Available in the folder - "/home/ubuntu/Group2_IR" as results.txt



Please Note: my jar file has a total of 1000 hits 

Path to where the index files are generated and stored = "/home/ubuntu/Group2_IR/Data/index"
Path to source data - "/home/ubuntu/Group2_IR/Data" (Cranfield collection)

Format for execution of Trec_Eval:

* Traverse to - "/home/ubuntu/Group2_IR/trec_eval-9.0.7" path using 'cd' command
* ./trec_eval <QRelsCorrectedforTRECeval> <resultsfile>
  E.g., ./trec_eval test/QRelsCorrectedforTRECeval.txt  test/results.txt 