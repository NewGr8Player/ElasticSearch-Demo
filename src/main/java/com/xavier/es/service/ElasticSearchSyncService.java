package com.xavier.es.service;

import com.alibaba.otter.canal.protocol.CanalEntry;
import com.xavier.es.util.ElasticsearchUtil;
import lombok.extern.slf4j.Slf4j;
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

	/**
	 * 插入操作
	 *
	 * @param tableName
	 * @param rowData
	 */
	public void insert(String tableName, CanalEntry.RowData rowData) {
		/* 索引是否存在 */
		if (!ElasticsearchUtil.isIndexExist(tableName)) {
			ElasticsearchUtil.createIndex(tableName);
		}
		Map<String, Object> dataMap = new HashMap<>();
		for (CanalEntry.Column column : rowData.getAfterColumnsList()) {
			log.info("==NAME:{}-TYPE:{}-VALUE:{}", column.getName(), column.getMysqlType(), column.getValue());
			dataMap.put(column.getName(), column.getValue());//TODO 字段映射
		}
		//ElasticsearchUtil.addData()
	}

}
