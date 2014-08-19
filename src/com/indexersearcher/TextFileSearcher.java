package com.indexersearcher;


import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.cn.smart.SmartChineseAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.highlight.Highlighter;
import org.apache.lucene.search.highlight.QueryScorer;
import org.apache.lucene.search.highlight.SimpleHTMLFormatter;
import org.apache.lucene.search.highlight.TextFragment;
import org.apache.lucene.search.highlight.TokenSources;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;

/** Simple command-line based search demo. */
public class TextFileSearcher {
	
	private IndexReader reader;
	private IndexSearcher searcher;
	private Analyzer analyzer;
	private QueryParser parser;
	
	private String index;
	private String field;
	private int hitsPerPage;

	private TextFileSearcher() throws IOException {
		
		index = "/home/wu/index";
	    field = "contents";
	    hitsPerPage = 10;
		
	    reader = DirectoryReader.open(FSDirectory.open(new File(index)));
		searcher = new IndexSearcher(reader);
		analyzer = new SmartChineseAnalyzer(Version.LUCENE_4_9);
		
		parser = new QueryParser(Version.LUCENE_4_9, field, analyzer);
	}
  
	private static TextFileSearcher instance = null;

	public static TextFileSearcher getTextFileSearcher() {
		try {
			instance = new TextFileSearcher();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			return null;
		}
		return instance;
	}
  
  
	public Map<String, String> getSearchResult(String queryString) throws Exception {

	      Query query = parser.parse(queryString);
	            
	      Map<String, String> result = new HashMap<String, String>();
	      
	   // Collect enough docs to show 5 pages
	      TopDocs results = searcher.search(query, 5 * hitsPerPage);
	      ScoreDoc[] hits = results.scoreDocs;
	      
	      int numTotalHits = results.totalHits;
	      
	      SimpleHTMLFormatter htmlFormatter = new SimpleHTMLFormatter();
	      Highlighter highlighter = new Highlighter(htmlFormatter, new QueryScorer(query));

	      for (int i = 0; i < hits.length; i++) {
	   
	          int id = hits[i].doc;
	          Document doc = searcher.doc(id);
	          String path = doc.get("path");
	          if (path != null) {
	        	  String content = doc.get("contents");
	        	  if (content != null) {
	        		  String text = doc.get("contents");
					  TokenStream tokenStream = TokenSources.getAnyTokenStream(searcher.getIndexReader(), id, "contents", analyzer);
					  TextFragment[] frag = highlighter.getBestTextFragments(tokenStream, text, false, 10);
					  StringBuilder sBuilder = new StringBuilder();
					  for (int j = 0; j < frag.length; j++) {
					      if ((frag[j] != null) && (frag[j].getScore() > 0)) {
					        sBuilder.append(frag[j].toString());
					      }
					  }
					  result.put(path, sBuilder.toString());
	        	  } else {
	        		  result.put(path, "");
	        	  }
	          } 
	                    
	        }
	        return result;
	}

	@Override
	protected void finalize() throws Throwable {
		// TODO Auto-generated method stub
		super.finalize();
		reader.close();
	}

}
