package assignment2;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.similarities.BM25Similarity;
import org.apache.lucene.search.similarities.BooleanSimilarity;
import org.apache.lucene.search.similarities.ClassicSimilarity;
import org.apache.lucene.search.similarities.Similarity;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.queryparser.classic.MultiFieldQueryParser;
import org.apache.lucene.queryparser.classic.ParseException;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.jsoup.parser.Parser;

public class MyQueryParser {

	// the location of the index
	private static String INDEX_DIRECTORY = "../fbis_index";

	// Limiting the number of search results
	private static int MAX_RESULTS = 1000;
	
	// Path for the file with all the queries
	private static String QUERIES_PATH = "../Query";
	
	// Path of the result of query
	private static String RESULT_PATH = "";
	private static BufferedReader br;
	private static ArrayList<Query> queries = new ArrayList<Query>();
	private static MultiFieldQueryParser parser;
	
	private static ArrayList<String> query = new ArrayList<String>();
	private static ArrayList<String> number = new ArrayList<String>();
	private static ArrayList<String> title = new ArrayList<String>();
	private static ArrayList<String> description = new ArrayList<String>();
	Analyzer analyzer = new StandardAnalyzer();
	
	
	
	
	public static void search() throws IOException, ParseException {

		Analyzer analyzer = new StandardAnalyzer();
		br = new BufferedReader(new FileReader(QUERIES_PATH + "/" + "topics.txt"));
		parseQuery();
				

		// Emptying the file contents if it is already filled with values

		// Open the folder that contains our search index
		Directory directory = FSDirectory.open(Paths.get(INDEX_DIRECTORY));

		// Create objects to read and search across the index
		DirectoryReader ireader = DirectoryReader.open(directory);
		
		// Creating searcher to search across index
		IndexSearcher isearcher = new IndexSearcher(ireader);
		

		// Creating the  parser and adding "title", "description"
		
		parser = new MultiFieldQueryParser(new String[] { "text", "ti" }, analyzer);
		
		for(int i=0;i<title.size();i++) {
			String result = title.get(i)+description.get(i);
			Query query = null;
			query=parser.parse(result);
			queries.add(query);
			}		

		// Parsing all the queries in the given file		
		
		for(Query queryTemp : queries) {
			
			ScoreDoc[] hits = isearcher.search(queryTemp, MAX_RESULTS).scoreDocs;  
			for (int i = 0; i < hits.length; i++) {
	            int docno = hits[i].doc;
	            Document d = isearcher.doc(docno);
	            System.out.println(d.get("docno"));
			}
			
		}
		
		
		
		// Closing everything

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
				number.add((numData.text()).trim());
			}
			
			//filtering title
			org.jsoup.nodes.Document docQueryTitle = Jsoup.parse(fileContents);
			List<Element> titles = docQueryTitle.getElementsByTag("title");	
			for(Element titleData : titles) {
				title.add((titleData.text()).trim());
			}
			
			//filtering description		
			org.jsoup.nodes.Document docQueryDesc = Jsoup.parse(fileContents);
			docQueryDesc.select("narr").remove();
			List<Element> desc = docQueryDesc.getElementsByTag("desc");			
			for(Element descData : desc) {
				String temp = descData.text();
				if(temp.contains("Description:"))
					temp = temp.replaceAll("Description:", "").trim();
				description.add(temp.trim());
			}	
			
	//		org.jsoup.nodes.Document docQueryNarr = Jsoup.parse(fileContents);
	//		List<Element> narr = docQueryNarr.getElementsByTag("narr");
	//		System.out.println(narr);	
			
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
	
		
}
