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
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class FR94Indexer {
    private static final String FR94_INDEX_DIRECTORY = "../fr94_index";
    private static final String FR94_DIRECTORY = "../fr94";
    private static final String [] IGNORE_FILES = {"readchg", "readmefr", ".DS_Store"};
    private static BufferedReader br;
    private static int count = 0;

    /**
     * Indexes all the files in the FBIS directory
     * @throws IOException
     */
    public static void indexFR94() throws IOException {

        Directory fr94Dir = FSDirectory.open(Paths.get(FR94_DIRECTORY));
        Directory indexDirectory = FSDirectory.open(Paths.get(FR94_INDEX_DIRECTORY));
        Analyzer analyzer = new StandardAnalyzer();
        IndexWriterConfig config = new IndexWriterConfig(analyzer);
        config.setOpenMode(IndexWriterConfig.OpenMode.CREATE);
        IndexWriter iwriter = new IndexWriter(indexDirectory, config);

        for(String fr94Folder : fr94Dir.listAll()) {
            if(!fr94Folder.equals(IGNORE_FILES[0]) && !fr94Folder.equals(IGNORE_FILES[1]) && !fr94Folder.equals(IGNORE_FILES[2])) {
                Directory fr94FolderDoc = FSDirectory.open(Paths.get("../fr94/"+fr94Folder));
                for(String fr94File : fr94FolderDoc.listAll()) {
                    System.out.println("Indexing " + fr94File);
                    br = new BufferedReader(new FileReader("../fr94/" + fr94Folder + "/" + fr94File));
                    addFR94Docs(iwriter);
                }
            }
        }
        System.out.println("FR94 indexing complete: " + count + " documents indexed.");
        iwriter.close();
        indexDirectory.close();
        fr94Dir.close();
    }

    /**
     * Function to get the contents of the tags: DOCNO, TEXT and TI and write it into
     * the index.
     * @param iwriter
     * @throws IOException
     */
    public static void addFR94Docs(IndexWriter iwriter) throws IOException {

        String fileContents = readFile();
        org.jsoup.nodes.Document document = Jsoup.parse(fileContents);
        List<Element> list = document.getElementsByTag("DOC");

        for(Element doc : list) {
            Document fbisDoc = new Document();

            if(doc.getElementsByTag("DOCNO") != null)
                fbisDoc.add(new TextField("docno", removeOpeningAndClosingTags(doc, "DOCNO"), Field.Store.YES));
            if(doc.getElementsByTag("PARENT") != null)
                fbisDoc.add(new TextField("text", removeOpeningAndClosingTags(doc, "TEXT"), Field.Store.YES));
            if(doc.getElementsByTag("TEXT") != null)
                fbisDoc.add(new TextField("headline", removeOpeningAndClosingTags(doc, "TI"), Field.Store.YES));

            iwriter.addDocument(fbisDoc);
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
        data = data.replaceAll("<!-- .* -->", "");
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
