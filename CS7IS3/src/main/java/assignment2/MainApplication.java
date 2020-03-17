package assignment2;

import java.io.IOException;
import org.apache.lucene.queryparser.classic.ParseException;

public class MainApplication {
	public static void main(String[] args) throws IOException, ParseException {
//		FBISIndexer.indexFBIS();
		MyQueryParser.search();
	}
}
