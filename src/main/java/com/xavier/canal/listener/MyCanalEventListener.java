package com.xavier.canal.listener;

import com.alibaba.otter.canal.protocol.CanalEntry;
import com.xavier.es.service.ElasticSearchHighLevelQueryReduceService;
import com.xavier.es.service.ElasticSearchPersonCaseReduceService;
import com.xavier.es.service.ElasticSearchSyncService;
import com.xavier.starter.canal.annotation.CanalEventListener;
import com.xavier.starter.canal.annotation.ListenPoint;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Optional;

@Slf4j
@CanalEventListener
public class MyCanalEventListener {

	@Autowired
	private ElasticSearchSyncService elasticSearchSyncService;
	@Autowired
	private ElasticSearchPersonCaseReduceService elasticSearchPersonCaseReduceService;
	@Autowired
	private ElasticSearchHighLevelQueryReduceService elasticSearchHighLevelQueryReduceService;

	@ListenPoint
	public void onEvent(String tableName, CanalEntry.EventType eventType, CanalEntry.RowData rowData) throws Exception {
		try {
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
			elasticSearchPersonCaseReduceService.reduce(tableName, rowData, eventType);/* 信访人-信访件聚合 */
			elasticSearchHighLevelQueryReduceService.reduce(tableName, rowData, eventType);/* 高级查询平表聚合 */
		} catch (Exception e) {
			log.error(e.getMessage());
			log.error(e.getStackTrace().toString());
			e.printStackTrace();
		}
	}
}
