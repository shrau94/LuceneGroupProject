package assignment2;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;

import java.nio.file.Paths;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
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

public class MyQueryParser {

	// the location of the index
	private static String INDEX_DIRECTORY = "../fbis_index";

	// Limiting the number of search results
	private static int MAX_RESULTS = 1000;
	
	// Path for the file with all the queries
	private static String QUERIES_PATH = "";
	
	// Path of the result of query
	private static String RESULT_PATH = "";
	
	
	
	public static void search() throws IOException, ParseException {

		Analyzer analyzer = new StandardAnalyzer();
		//OutputStream os = null;
		
		// Emptying the file contents if it is already filled with values

		// Open the folder that contains our search index
		Directory directory = FSDirectory.open(Paths.get(INDEX_DIRECTORY));

		// Create objects to read and search across the index
		DirectoryReader ireader = DirectoryReader.open(directory);
		
		// Creating searcher to search across index
		IndexSearcher isearcher = new IndexSearcher(ireader);
		

		// Creating the  parser and adding "title", "author", "bibliography", "words"
		MultiFieldQueryParser parser = new MultiFieldQueryParser(
				new String[] { "text", "ti" }, analyzer);

		// Parsing all the queries in the given file
		Query query = parser.parse("Macedonian"); 
		ScoreDoc[] hits = isearcher.search(query, MAX_RESULTS).scoreDocs;  
		for (int i = 0; i < hits.length; i++) {
            int docno = hits[i].doc;
            Document d = isearcher.doc(docno);
            System.out.println(d.get("docno"));
		}
		
		
		// Closing everything
		//os.close();
		ireader.close();
		directory.close();
	}
}
