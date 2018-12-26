package com.xavier.es.service;

import com.xavier.es.util.ElasticsearchUtil;
import com.xavier.es.util.EsPage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 查询
 *
 * @author NewGr8Player
 */
@Slf4j
@Service
public class ElasticsearchQueryService {

	/**
	 * 使用分词查询,并分页（高亮）
	 *
	 * @param index              索引名称
	 * @param type               类型名称,可传入多个type逗号分隔
	 * @param currentPage        当前页
	 * @param pageSize           每页显示条数
	 * @param startTime          开始时间
	 * @param endTime            结束时间
	 * @param fields             需要显示的字段，逗号分隔（缺省为全部字段）
	 * @param sortField          排序字段
	 * @param matchPhrase        true 使用，短语精准匹配
	 * @param highlightFieldList 高亮字段
	 * @param matchStr           过滤条件（xxx=111,aaa=222）
	 * @return
	 */
	public EsPage basicQuery(String index, String type, int currentPage, int pageSize, long startTime, long endTime,
	                         String fields, String sortField, boolean matchPhrase, List<String> highlightFieldList,
	                         String matchStr) {
		return ElasticsearchUtil.searchDataPage(index, type, currentPage, pageSize, startTime, endTime,
				fields, sortField, matchPhrase, highlightFieldList
				, matchStr);
	}

	/**
	 * 分页查询（高亮）
	 */
	public void pageQuery() {

	}

}
