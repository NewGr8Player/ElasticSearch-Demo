package com.xavier.canal.listener;

import com.alibaba.otter.canal.protocol.CanalEntry;
import com.xavier.es.service.ElasticSearchReduceService;
import com.xavier.es.service.ElasticSearchSyncService;
import com.xavier.starter.canal.annotation.CanalEventListener;
import com.xavier.starter.canal.annotation.ListenPoint;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

@Slf4j
@CanalEventListener
public class MyCanalEventListener {

	@Autowired
	private ElasticSearchSyncService elasticSearchSyncService;
	@Autowired
	private ElasticSearchReduceService elasticSearchReduceService;

	@ListenPoint
	public void onEvent(String tableName, CanalEntry.EventType eventType, CanalEntry.RowData rowData) throws Exception {
		log.debug("Table name is {}", tableName);
		switch (eventType) {
			case INSERT:
				elasticSearchSyncService.insert(tableName, rowData);
				break;
			case UPDATE:
				elasticSearchSyncService.update(tableName, rowData);
				break;
			case DELETE:
				elasticSearchSyncService.delete(tableName, rowData);
				break;
			default:
				log.debug("Not monitored action value:{},rowData:{}", eventType.getNumber(), rowData);
				break;
		}
		elasticSearchReduceService.reduce(tableName, rowData, eventType);// TODO 消息队列解耦！
	}
}
