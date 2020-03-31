package assignment2;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.StringReader;
import java.io.OutputStream;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.core.LowerCaseFilter;
import org.apache.lucene.analysis.en.PorterStemFilter;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.analysis.standard.StandardTokenizer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.document.Document;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.similarities.BM25Similarity;
import org.apache.lucene.search.similarities.BooleanSimilarity;
import org.apache.lucene.search.similarities.ClassicSimilarity;
import org.apache.lucene.search.similarities.LMDirichletSimilarity;
import org.apache.lucene.search.similarities.Similarity;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.queryparser.classic.MultiFieldQueryParser;
import org.apache.lucene.queryparser.classic.ParseException;


public class MyQueryParser {

	// the location of the index
	private static String INDEX_DIRECTORY = "../index_files";

	// Limiting the number of search results
	private static int MAX_RESULTS = 1000;
	
	// Path for the file with all the queries
	private static String QUERIES_PATH = "../Query";
	
	// Path of the result of query
	private static String RESULT_PATH = "../query_results.txt";
	private static BufferedReader br;
	private static ArrayList<Query> queries = new ArrayList<Query>();
	private static MultiFieldQueryParser parser;
	private static ArrayList<String> number = new ArrayList<String>();
	private static ArrayList<String> title = new ArrayList<String>();
	private static ArrayList<String> description = new ArrayList<String>();
	private static ArrayList<String> narrative = new ArrayList<String>();
	
	
	
	
	
	public static void search() throws IOException, ParseException {
		
		OutputStream os = new FileOutputStream(new File(RESULT_PATH));
		String emptydata = "";
		os.write(emptydata.getBytes(), 0, emptydata.length());

		Analyzer analyzer = new MyAnalyzer();
		br = new BufferedReader(new FileReader(QUERIES_PATH + "/" + "topics.txt"));
		parseQuery();
		

		// Emptying the file contents if it is already filled with values

		// Open the folder that contains our search index
		Directory directory = FSDirectory.open(Paths.get(INDEX_DIRECTORY));

		// Create objects to read and search across the index
		DirectoryReader ireader = DirectoryReader.open(directory);
		
		// Creating searcher to search across index
		IndexSearcher isearcher = new IndexSearcher(ireader);
		
		// Similarity
//		isearcher.setSimilarity(new BM25Similarity());
		

		// Creating map for boost scores
		Map<String, Float> boost = new HashMap<String, Float>();
        boost.put("headline", (float) 0.1);
        boost.put("text", (float) 0.9);
		
        parser = new MultiFieldQueryParser(new String[]{"headline", "text"}, analyzer, boost);
				
		for(int i=0;i<title.size();i++) {
			
			String result = title.get(i)+ " "+ description.get(i)+ " "+ parseNarr(narrative.get(i));
			
//			System.out.println(result);
			Query query = null;
			query=parser.parse(result);
			queries.add(query);
			}		

		// Parsing all the queries in the given file		
		int queryNum = 0;
		String queryNo[];
		for(Query queryTemp : queries) {
			
			ScoreDoc[] hits = isearcher.search(queryTemp, MAX_RESULTS).scoreDocs;  
			for (int i = 0; i < hits.length; i++) {
	            int docno = hits[i].doc;
	            Document d = isearcher.doc(docno);
	            queryNo = number.get(queryNum).split(" ");
	            
		        String data = queryNo[1] + " 0 " + d.get("docno") + " 0 " + hits[i].score + " TEAM-2" + "\n";
				os.write(data.getBytes(), 0, data.length());
			}
			queryNum++;
			
		}
		
		System.out.println("Done. Output generated at 'query_results.txt'");
		
		// Closing everything
		
		os.close();
		ireader.close();
		directory.close();
	}
	private static void parseQuery() throws IOException, ParseException{
		String fileContents = readQueryFile();
		
		//filtering query
			
			//filtering Query Number
			org.jsoup.nodes.Document docNum = Jsoup.parse(fileContents);
			docNum.select("title").remove();
			docNum.select("desc").remove();
			List<Element> num = docNum.getElementsByTag("num");
			for(Element numData : num) {
				number.add(clean(numData.text()).trim().toLowerCase());
			}
			
			//filtering title
			org.jsoup.nodes.Document docQueryTitle = Jsoup.parse(fileContents);
			List<Element> titles = docQueryTitle.getElementsByTag("title");	
			for(Element titleData : titles) {
				title.add(clean(titleData.text()).trim().toLowerCase());
				
			}
			
			//filtering description		
			org.jsoup.nodes.Document docQueryDesc = Jsoup.parse(fileContents);
			docQueryDesc.select("narr").remove();
			List<Element> desc = docQueryDesc.getElementsByTag("desc");			
			for(Element descData : desc) {
				String temp = descData.text();
				if(temp.contains("Description:"))
					temp = temp.replaceAll("Description:", "").trim();
				description.add(clean(temp.trim().toLowerCase()));
			}	
			
			org.jsoup.nodes.Document docQueryNarr = Jsoup.parse(fileContents);
			List<Element> narr = docQueryNarr.getElementsByTag("narr");
			for(Element narrData : narr) {
				String temp = narrData.text();
				if(temp.contains("Narrative:"))
					temp = temp.replaceAll("Narrative:", "").trim();
				narrative.add(clean(temp.trim().toLowerCase()));
			}			
			
	}
	private static String readQueryFile() throws IOException {
		try {
			StringBuilder stringBuilder = new StringBuilder();
			String nextLine = br.readLine();

			while (nextLine != null) {
				stringBuilder.append(nextLine);
				stringBuilder.append("\n");
				nextLine = br.readLine();
			}
			return stringBuilder.toString();
		} finally {
			br.close();
		}
	}	
	
	private static String parseNarr(String narr) {
		String result= "";
		String[] arrOfNarr = narr.split("\\. "); 
		
		for(int i=0;i<arrOfNarr.length;i++) {
			String temp = arrOfNarr[i];
			if(temp.contains("not relevant")) {
				continue;
			}
			else {
				result=result + " " + temp;
			}
		}
		
		
		return result;
	}
	
	private static String clean(String str) {
		str = str.replace('?', ' ');
		str = str.replace('/', ' ');
		str = str.replace('(', ' ');
		str = str.replace(')', ' ');
		return str;
	}
	
}
