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
import java.util.Arrays;
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

import org.apache.lucene.search.similarities.LMJelinekMercerSimilarity;
import org.apache.lucene.search.similarities.MultiSimilarity;


public class MyQueryParser {

	// the location of the index
	private static String INDEX_DIRECTORY = "../index_files";

	// Limiting the number of search results
	private static int MAX_RESULTS = 1000;
	
	// Path for the file with all the queries
	private static String QUERIES_PATH = "../Query";
	
	// Path of the result of query
	private static String RESULT_PATH = "./query_results.txt";
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
		final List<String> stopWords = Arrays.asList(
				"a", "about", "above", "after", "again", "against", "ain", "all", "am", "an", 
				"and", "any", "are", "aren", "aren't", "as", "at", "be", "because", "been", "before",
				"being", "below", "between", "both", "but", "by", "can", "couldn", "couldn't", "d", "did",
				"didn", "didn't", "do", "does", "doesn", "doesn't", "doing", "don", "don't", "down", "during",
				"each", "few", "for", "from", "further", "had", "hadn", "hadn't", "has", "hasn", "hasn't", "have",
				"haven", "haven't", "having", "he", "her", "here", "hers", "herself", "him", "himself", "his", "how",
				"i", "if", "in", "into", "is", "isn", "isn't", "it", "it's", "its", "itself", "just", "ll", "m", "ma",
				"me", "mightn", "mightn't", "more", "most", "mustn", "mustn't", "my", "myself", "needn", "needn't", "no", "nor",
				"not", "now", "o", "of", "off", "on", "once", "only", "or", "other", "our", "ours", "ourselves", "out", "over",
				"own", "re", "s", "same", "shan", "shan't", "she", "she's", "should", "should've", "shouldn", "shouldn't", "so",
				"some", "such", "t", "than", "that", "that'll", "the", "their", "theirs", "them", "themselves", "then", "there",
				"these", "they", "this", "those", "through", "to", "too", "under", "until", "up", "ve", "very", "was", "wasn",
				"wasn't", "we", "were", "weren", "weren't", "what", "when", "where", "which", "while", "who", "whom", "why", "will",
				"with", "won", "won't", "wouldn", "wouldn't", "y", "you", "you'd", "you'll", "you're", "you've", "your", "yours",
				"yourself", "yourselves", "could", "he'd", "he'll", "he's", "here's", "how's", "i'd", "i'll", "i'm", "i've", "let's",
				"ought", "she'd", "she'll", "that's", "there's", "they'd", "they'll", "they're", "they've", "we'd", "we'll",
				"we're", "we've", "what's", "when's", "where's", "who's", "why's", "would","discuss","mention", "documents",
				"describe"  );
		

		// Emptying the file contents if it is already filled with values

		// Open the folder that contains our search index
		Directory directory = FSDirectory.open(Paths.get(INDEX_DIRECTORY));

		// Create objects to read and search across the index
		DirectoryReader ireader = DirectoryReader.open(directory);
		
		// Creating searcher to search across index
		IndexSearcher isearcher = new IndexSearcher(ireader);
		
		// Similarity
		isearcher.setSimilarity(new MultiSimilarity(new Similarity[]{new BM25Similarity(), new LMJelinekMercerSimilarity(0.6f)}));
		

		// Creating map for boost scores
		Map<String, Float> boost = new HashMap<String, Float>();
        boost.put("headline", (float) 0.05);
        boost.put("text", (float) 0.95);
		
        parser = new MultiFieldQueryParser(new String[]{"headline", "text"}, analyzer, boost);
				
		for(int i=0;i<title.size();i++) {
			
			String result = title.get(i)+ " "+ title.get(i)+ " "+ title.get(i)+ " "+ description.get(i)+ " "+ description.get(i)+ " "+ parseNarr(narrative.get(i));
			String[] allWords = result.split(" ");
			StringBuilder builder = new StringBuilder();
		    for(String word : allWords) {
		        if(!stopWords.contains(word)) {
		            builder.append(word);
		            builder.append(' ');
		        }
		    }
		    result = builder.toString().trim();
			
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
		String notRel = "";
		String[] arrOfNarr = narr.split("\\. "); 
		
		for(int i=0;i<arrOfNarr.length;i++) {
			String temp = arrOfNarr[i];
			if(temp.contains("not relevant") || temp.contains("irrelevant")) {
				if(temp.contains("unless")) {
					result=result+ " "+temp.split("unless")[1];
					notRel=notRel+ " "+temp.split("unless")[0];
				}
				if(temp.contains("a relevant")||temp.contains("is relevant")||temp.contains("are relevant")) {
					String[] splitOfNarr = narr.split(", "); 
					for(int j=0;j<splitOfNarr.length;j++) {
						if((temp.contains("a relevant")||splitOfNarr[j].contains("is relevant")||splitOfNarr[j].contains("are relevant"))&&splitOfNarr[j].contains("not relevant")==false) {
							result=result+ " "+splitOfNarr[j];
						}
						else
							notRel=notRel+ " "+splitOfNarr[j];
					}
				}
				continue;
			}
			else {
				result=result + " " + temp;
			}
		}
		
		if (notRel != "") {
			result = result + " - " + notRel;
		}
		
		if(result.contains("relevant"))
			result = result.replaceAll("relevant", "").trim();
		if(result.contains("documents"))
			result = result.replaceAll("documents", "").trim();
		if(result.contains("document"))
			result = result.replaceAll("document", "").trim();
		
		return result;
		
	}
	
	private static String clean(String str) {
		str = str.replace('?', ' ');
		str = str.replace('/', ' ');
		str = str.replace('(', ' ');
		str = str.replace(')', ' ');
		str = str.replace('-', ' ');
		return str;
	}
	
}
