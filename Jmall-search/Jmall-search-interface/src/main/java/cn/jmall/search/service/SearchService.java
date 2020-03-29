package cn.jmall.search.service;

import cn.jmall.common.pojo.SearchResult;

public interface SearchService {

	public SearchResult search(String keyword, int page, int rows) throws Exception;
}
