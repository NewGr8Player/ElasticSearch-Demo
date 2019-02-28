package com.xavier.es.service;

import com.xavier.es.util.ElasticsearchSqlUtil;
import com.xavier.es.util.EsPage;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

/**
 * sql查询
 *
 * @author NewGr8Player
 */
@Slf4j
@Service
public class ElasticsearchSqlQueryService {

	/**
	 * 基础sql分页查询
	 *
	 * @param indexName   索引名称
	 * @param fields      查询字段
	 * @param cond        查询条件
	 * @param currentPage 当前页码
	 * @param pageSize    页面大小
	 * @return
	 */
	public EsPage basicSqlPageQuery(String indexName, String fields, String cond, int currentPage, int pageSize) {
		StringBuffer sql = new StringBuffer();
		sql.append("select ");
		/* 查询向里面不能使用.keyword分词内容 */
		sql.append(StringUtils.isBlank(fields) ? " * " : fields.replace(".keyword", ""));
		sql.append(" FROM ");
		sql.append(indexName);
		sql.append(" WHERE ").append(cond);
		return ElasticsearchSqlUtil.findPageBySql(sql.toString(), currentPage, pageSize);
	}
}
