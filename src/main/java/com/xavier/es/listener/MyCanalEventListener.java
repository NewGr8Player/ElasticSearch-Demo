package com.xavier.es.listener;

import com.alibaba.otter.canal.protocol.CanalEntry;
import com.xavier.starter.canal.annotation.CanalEventListener;
import com.xavier.starter.canal.annotation.DeleteListenPoint;
import com.xavier.starter.canal.annotation.InsertListenPoint;
import com.xavier.starter.canal.annotation.UpdateListenPoint;

@CanalEventListener
public class MyCanalEventListener {

	@InsertListenPoint(table = "test")
	public void onEventInsert(CanalEntry.EventType eventType, CanalEntry.RowData rowData) {
		//do something...
	}

	@UpdateListenPoint(table = "test")
	public void onEventUpdate(CanalEntry.EventType eventType, CanalEntry.RowData rowData) {
		//do something...
	}

	@DeleteListenPoint(table = "test")
	public void onEventDelete(CanalEntry.EventType eventType, CanalEntry.RowData rowData) {
		//do something...
	}

}
