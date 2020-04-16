package assignment2;

import java.io.IOException;
import java.nio.file.Paths;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.search.similarities.*;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;


public class MainApplication {
	
	private static final String INDEX_DIRECTORY = "../index_files";
	
	public static void main(String[] args) throws IOException, ParseException {
		
		Directory indexDirectory = FSDirectory.open(Paths.get(INDEX_DIRECTORY));
		Analyzer analyzer = new MyAnalyzer();
		IndexWriterConfig config = new IndexWriterConfig(analyzer);
		config.setSimilarity(new MultiSimilarity(new Similarity[]{new BM25Similarity(), new LMJelinekMercerSimilarity(0.6f)}));
		config.setOpenMode(IndexWriterConfig.OpenMode.CREATE);
		IndexWriter iwriter = new IndexWriter(indexDirectory, config);

		System.out.println("Indexing FBIS.");
		FBISIndexer.indexFBIS(iwriter);

		System.out.println("Indexing FR94.");
		FR94Indexer.indexFR94(iwriter);

		System.out.println("Indexing FT.");
		FTIndexer.indexFT(iwriter);

		System.out.println("Indexing LATIMES.");
		LATimesIndexer.indexLATimes(iwriter);

		iwriter.close();
		indexDirectory.close();

		System.out.println("Indexing complete. Now starting to query the index.");
		MyQueryParser.search();
	}
}
