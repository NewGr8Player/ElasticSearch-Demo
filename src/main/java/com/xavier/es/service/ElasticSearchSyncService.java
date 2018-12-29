package com.xavier.es.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.otter.canal.protocol.CanalEntry;
import com.xavier.es.util.ElasticsearchUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;
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

	/**
	 * 插入操作
	 *
	 * @param tableName
	 * @param rowData
	 */
	public void insert(String tableName, CanalEntry.RowData rowData) throws Exception {
		/* 索引是否存在 */
		if (!ElasticsearchUtil.isIndexExist(tableName)) {
			ElasticsearchUtil.createIndex(tableName);
			log.info("创建索引:{}-{}", tableName, "ElasticSearchSyncService#insert");
		}
		Map<String, Object> dataMap = new HashMap<>();
		rowData.getAfterColumnsList().forEach(
				column -> {
					dataMap.put(column.getName(), column.getValue());//TODO 字段映射
				}
		);
		ElasticsearchUtil.addData(JSONObject.parseObject(JSON.toJSONString(dataMap)), tableName, tableName, (String) dataMap.get("id"));
	}

	/**
	 * 更新操作
	 *
	 * @param tableName
	 * @param rowData
	 */
	public void update(String tableName, CanalEntry.RowData rowData) throws IOException {
		/* 索引是否存在 */
		if (!ElasticsearchUtil.isIndexExist(tableName)) {
			ElasticsearchUtil.createIndex(tableName);
			log.info("创建索引:{}-{}", tableName, "ElasticSearchSyncService#update");
		}
		Map<String, Object> dataMap = new HashMap<>();
		rowData.getAfterColumnsList().forEach(
				column -> {
					dataMap.put(column.getName(), column.getValue());//TODO 字段映射
				}
		);
		ElasticsearchUtil.updateDataById(JSONObject.parseObject(JSON.toJSONString(dataMap)), tableName, tableName, (String) dataMap.get("id"));
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
	public void delete(String tableName, CanalEntry.RowData rowData) throws IOException {
		/* 索引是否存在 */
		if (!ElasticsearchUtil.isIndexExist(tableName)) {
			log.info("索引不存在:{}-{}", tableName, "ElasticSearchSyncService#delete");
		} else {
			String id = "";
			for (CanalEntry.Column column : rowData.getBeforeColumnsList()) {
				if ("id".equals(column.getName())) {
					id = column.getValue();
					break;
				}
			}
			ElasticsearchUtil.deleteDataById(tableName, tableName, id);
		}
	}
}
