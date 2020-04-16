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
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import sun.jvm.hotspot.debugger.cdbg.CDebugger;

public class LATimesIndexer {

    private static final String FBIS_DIRECTORY = "../latimes";
    private static final String [] IGNORE_FILES = {"readchg.txt", "readmela.txt", ".DS_Store"};
    private static BufferedReader br;
    private static int count = 0;

    /**
     * Indexes all the files in the LATimes directory
     * @throws IOException
     */
    public static void indexLATimes(IndexWriter iwriter) throws IOException {

        Directory fbisDir = FSDirectory.open(Paths.get(FBIS_DIRECTORY));

        for(String fbisFile : fbisDir.listAll()) {
            if(!fbisFile.equals(IGNORE_FILES[0]) && !fbisFile.equals(IGNORE_FILES[1])) {
                br = new BufferedReader(new FileReader(FBIS_DIRECTORY + "/" + fbisFile));
                addLATimesDocs(iwriter);
            }
        }
        System.out.println("LATIMES indexing complete: " + count + " documents indexed.");

        fbisDir.close();
    }

    /**
     * Function to get the contents of the tags: DOCNO, TEXT and TI and write it into
     * the index.
     * @param iwriter
     * @throws IOException
     */
    public static void addLATimesDocs(IndexWriter iwriter) throws IOException {

        String fileContents = readFile();
        org.jsoup.nodes.Document document = Jsoup.parse(fileContents);
        List<Element> list = document.getElementsByTag("DOC");

        for(Element doc : list) {
            Document laTimesDoc = new Document();

            if(doc.getElementsByTag("DOCNO") != null)
                laTimesDoc.add(new StringField("docno", removeOpeningAndClosingTags(doc, "DOCNO"), Field.Store.YES));
            if(doc.getElementsByTag("HEADLINE") != null)
                laTimesDoc.add(new TextField("headline", removeOpeningAndClosingTags(doc, "HEADLINE"), Field.Store.YES));
            if(doc.getElementsByTag("TEXT") != null)
                laTimesDoc.add(new TextField("text", removeOpeningAndClosingTags(doc, "TEXT") + removeOpeningAndClosingTags(doc, "GRAPHIC") + removeOpeningAndClosingTags(doc, "SUBJECT"), Field.Store.NO));

            iwriter.addDocument(laTimesDoc);
            count++;
        }
    }

    /**
     * Removes the opening and closing tags of the given content
     * @param doc
     * @param tag
     * @return String
     */
    private static String removeOpeningAndClosingTags(Element doc, String tag) {
        Elements element = doc.getElementsByTag(tag);
        Elements tmpElement = element.clone();
        String data = tmpElement.toString();
        if(data.contains("\n"))
            data = data.replaceAll("\n", " ").trim();
        if(data.contains(("<" + tag + ">").toLowerCase()))
            data = data.replaceAll("<" + tag.toLowerCase() + ">", "").trim();
        if(data.contains(("</" + tag + ">").toLowerCase()))
            data = data.replaceAll("</" + tag.toLowerCase() + ">", "").trim();
        if(data.contains("<p>"))
            data = data.replaceAll("<p>", "").trim();
        if(data.contains("</p>"))
            data = data.replaceAll("</p>", "").trim();
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
