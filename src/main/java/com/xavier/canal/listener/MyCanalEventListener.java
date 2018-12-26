package com.xavier.canal.listener;

import com.alibaba.otter.canal.protocol.CanalEntry;
import com.xavier.es.service.ElasticSearchSyncService;
import com.xavier.starter.canal.annotation.CanalEventListener;
import com.xavier.starter.canal.annotation.ListenPoint;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Objects;

@Slf4j
@CanalEventListener
public class MyCanalEventListener {

	@Autowired
	private ElasticSearchSyncService elasticSearchSyncService;

	@ListenPoint
	public void onEvent(String tableName, CanalEntry.EventType eventType, CanalEntry.RowData rowData) {
		log.info("Database changed , table name is {}", tableName);
		Objects.requireNonNull(eventType);
		switch (eventType) {
			case INSERT:
				elasticSearchSyncService.insert(tableName,rowData);
				break;
			case UPDATE:
				elasticSearchSyncService.update(tableName,rowData);
				break;
			case DELETE:
				elasticSearchSyncService.delete(tableName,rowData);
				break;
			default:
				log.debug("Not monitored action value:{},rowData:{}", eventType.getNumber(), rowData);
				break;
		}
	}
}
