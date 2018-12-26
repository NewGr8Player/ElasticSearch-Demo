package com.xavier.es.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.otter.canal.protocol.CanalEntry;
import com.xavier.es.util.ElasticsearchUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * 同步数据
 *
 * @author NewGr8Player
 */
@Slf4j
@Service
public class ElasticSearchSyncService {

	@Value("${index.name}")
	private String indexName;

	/**
	 * 插入操作
	 *
	 * @param tableName
	 * @param rowData
	 */
	public void insert(String tableName, CanalEntry.RowData rowData) {
		/* 索引是否存在 */
		if (!ElasticsearchUtil.isIndexExist(indexName)) {
			ElasticsearchUtil.createIndex(indexName);
			log.info("创建索引:{}-{}", indexName, "ElasticSearchSyncService#insert");
		}
		Map<String, Object> dataMap = new HashMap<>();
		rowData.getAfterColumnsList().forEach(
				column -> {
					dataMap.put(column.getName(), column.getValue());//TODO 字段映射
				}
		);
		ElasticsearchUtil.addData(JSONObject.parseObject(JSON.toJSONString(dataMap)), indexName, tableName, String.valueOf(dataMap.get("id")));
	}

	/**
	 * 更新操作
	 *
	 * @param tableName
	 * @param rowData
	 */
	public void update(String tableName, CanalEntry.RowData rowData) {
		/* 索引是否存在 */
		if (!ElasticsearchUtil.isIndexExist(indexName)) {
			ElasticsearchUtil.createIndex(indexName);
			log.info("创建索引:{}-{}", indexName, "ElasticSearchSyncService#update");
		}
		Map<String, Object> dataMap = new HashMap<>();
		rowData.getAfterColumnsList().forEach(
				column -> {
					dataMap.put(column.getName(), column.getValue());//TODO 字段映射
				}
		);
		ElasticsearchUtil.updateDataById(JSONObject.parseObject(JSON.toJSONString(dataMap)), indexName, tableName, String.valueOf(dataMap.get("id")));
	}

	/**
	 * 删除操作
	 * (删除操作会导致大面积索引重建，建议使用逻辑删除，即更新部分标识位)
	 *
	 * @param tableName
	 * @param rowData
	 * @see com.xavier.es.service.ElasticSearchSyncService#update(String, CanalEntry.RowData)
	 */
	@Deprecated
	public void delete(String tableName, CanalEntry.RowData rowData) {
		/* 索引是否存在 */
		if (!ElasticsearchUtil.isIndexExist(indexName)) {
			log.info("索引不存在:{}-{}", indexName, "ElasticSearchSyncService#delete");
		} else {
			String id = "";
			for (CanalEntry.Column column : rowData.getBeforeColumnsList()) {
				if ("id".equals(column.getName())) {
					id = column.getValue();
					break;
				}
			}
			ElasticsearchUtil.deleteDataById(indexName, tableName, id);
		}
	}
}
