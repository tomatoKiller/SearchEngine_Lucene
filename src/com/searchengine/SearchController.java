package com.searchengine;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.indexersearcher.TextFileSearcher;
import com.jfinal.core.Controller;

public class SearchController extends Controller{
	static TextFileSearcher searcher = TextFileSearcher.getTextFileSearcher();
	public void search() {
		Map<String, String> result = new HashMap<String, String>();
		String query = getPara("keyword");
//		String query = "开源";
		System.out.println(query);
		try {
			if (searcher != null) {
				result = searcher.getSearchResult(query);
			} else {
				renderText("内部错误: searcher 为空");
			}
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			renderText("内部错误： getResult失败");
		}

		setAttr("result", result);
		render("search.html");

	}
	
	public void test() {

	}
	
	public void index() {
		render("index.html");
	}
}
