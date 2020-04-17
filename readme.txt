Please find the details of Assessment 2 inline for TEAM ROCKET. We have added the engine on two AWS instances, the details are given below.

RESULTS:  

1. Instance 1 – MAP = 0.3380
2. Instance 2 – MAP = 0.3477 

LINK TO JOURNAL:
https://docs.google.com/document/d/1d2ZQ4v_xJhEkwzImjXp2YEfp-CtGEdYtREKCoGgTJwo/edit

LINK TO GITHUB REPOSITORY:
https://github.com/shrau94/LuceneGroupProject

RESOURCE DETAILS: 

Public DNS – ‘ec2-3-80-189-102.compute-1.amazonaws.com’ Github repository – ‘https://github.com/shrau94/LueceneGroupProject.git’ .
pem file – ‘CS7is3.pem’ is attached to the submission and also present in the github repository mentioned above. 
Command to ssh into the AWS instance – ‘ssh -i <path to CS7is3.pem file> <user>@<public DNS>’ 

E.g.,
For Instance 1:  ssh -i CS7is3.pem ubuntu@ec2-34-207-157-117.compute-1.amazonaws.com
For Instance 2:  ssh -i CS7is3.pem ubuntu@ec2-3-88-239-250.compute-1.amazonaws.com

EXECUTION OF THE JAR FILE FOR BOTH INSTANCES:

* Traverse to - "/home/ubuntu/Group2_IR" path using 'cd' command

java -jar IRGroup2_LuceneSearchEngine.jar

FORMAT TO EXECUTE TREC_EVAL FOR BOTH INSTANCES:

* Traverse to - "/home/ubuntu/Group2_IR/trec_eval-9.0.7" path using 'cd' command
* ./trec_eval <QRelsCorrectedforTRECeval> <resultsfile>
  
E.g., 
./trec_eval test/qrels.assignment2.part1 test/query_results.txt  

OTHER DETAILS: 

- To find the source code traverse to "/home/ubuntu/Group2_IR/CS7IS3/src/main/java/assignment2"

- Results are available in the folder - "/home/ubuntu/Group2_IR" as query_results.txt

- Path to where the index files are generated and stored = "/home/ubuntu/Group2_IR/index_files"
- Paths to source data - "/home/ubuntu/Group2_IR/fbis" | "/home/ubuntu/Group2_IR/fr94" | "/home/ubuntu/Group2_IR/ft" | "/home/ubuntu/Group2_IR/latimes"  


