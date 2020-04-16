package assignment2;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.List;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class FTIndexer {
	
    private static final String FT_DIRECTORY = "../ft";
    private static final String [] IGNORE_FILES = {"readfrcg", "readmeft", ".DS_Store"};
    private static BufferedReader br;
    private static int count = 0;

    /**
     * Indexes all the files in the FT directory
     * @throws IOException
     */
	public static void indexFT(IndexWriter iwriter) throws IOException {

        Directory ftDir = FSDirectory.open(Paths.get(FT_DIRECTORY));

        for(String ftFolder : ftDir.listAll()) {
            if(!ftFolder.equals(IGNORE_FILES[0]) && !ftFolder.equals(IGNORE_FILES[1]) && !ftFolder.equals(IGNORE_FILES[2])) {
                Directory ftFolderDoc = FSDirectory.open(Paths.get("../ft/" + ftFolder));
                for(String ftFile : ftFolderDoc.listAll()) {
                    br = new BufferedReader(new FileReader("../ft/" + ftFolder + "/" + ftFile));
                    addFTDocs(iwriter);
                }
            }
        }
        System.out.println("FT indexing complete: " + count + " documents indexed.");

        ftDir.close();
    }
	
	/**
     * Function to get the contents of the tags: DOCNO, TEXT and HEADLINE and write it into
     * the index.
     * @param iwriter
     * @throws IOException
     */
	public static void addFTDocs(IndexWriter iwriter) throws IOException {

        String fileContents = readFile();
        org.jsoup.nodes.Document document = Jsoup.parse(fileContents);
        List<Element> list = document.getElementsByTag("DOC");

        for(Element doc : list) {
            Document fbisDoc = new Document();

            if(doc.getElementsByTag("DOCNO") != null)
                fbisDoc.add(new StringField("docno", removeOpeningAndClosingTags(doc, "DOCNO"), Field.Store.YES));
            if(doc.getElementsByTag("HEADLINE") != null)
                fbisDoc.add(new TextField("headline", removeOpeningAndClosingTags(doc, "HEADLINE"), Field.Store.YES));
            if(doc.getElementsByTag("TEXT") != null)
                fbisDoc.add(new TextField("text", removeOpeningAndClosingTags(doc, "TEXT") + removeOpeningAndClosingTags(doc, "DATELINE"), Field.Store.NO));

            iwriter.addDocument(fbisDoc);
            count++;
        }
    }
	
	/**
     * Removes the opening and closing tags of the given content and also removes any comments
     * @param doc
     * @param tag
     * @return String
     */
    private static String removeOpeningAndClosingTags(Element doc, String tag) {
        Elements element = doc.getElementsByTag(tag);
        Elements tmpElement = element.clone();
        String data = tmpElement.toString();
        //data = data.replaceAll("<!-- .* -->", "");
        if(data.contains("\n"))
            data = data.replaceAll("\n", " ").trim();
        if(data.contains(("<" + tag + ">").toLowerCase()))
            data = data.replaceAll("<" + tag.toLowerCase() + ">", "").trim();
        if(data.contains(("</" + tag + ">").toLowerCase()))
            data = data.replaceAll("</" + tag.toLowerCase() + ">", "").trim();
        return data;
    }

    /**
     * Reads the contents of the file one by one and returns it as a string
     * @return
     * @throws IOException
     */
    private static String readFile() throws IOException {
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
