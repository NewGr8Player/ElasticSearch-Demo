package com.xavier.es.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.otter.canal.protocol.CanalEntry;
import com.xavier.es.util.ElasticsearchUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static com.xavier.config.BasicTableName.PT_PETITION_CASE;
import static com.xavier.config.BasicTableName.PT_PETITION_PERSON;
import static com.xavier.config.ReduceTableName.PT_PETITION_PERSON_CASE_REDUCE;

/**
 * 信访件信访人信息聚合
 *
 * @author NewGr8Player
 */
@Slf4j
@Service
public class ElasticSearchPersonCaseReduceService {

	/**
	 * 数据聚合
	 *
	 * @param tableName 表名
	 * @param rowData   数据
	 * @param eventType 事件类型
	 * @throws Exception
	 */
	public void reduce(String tableName, CanalEntry.RowData rowData, CanalEntry.EventType eventType) throws Exception {
		switch (tableName.toLowerCase()) {
			case PT_PETITION_PERSON:
				petitionPersonWithCase(rowData, eventType);
				break;
		}
	}

	/**
	 * 操作主访人与信访件信息联合文档
	 *
	 * @param rowData
	 */
	private void petitionPersonWithCase(CanalEntry.RowData rowData, CanalEntry.EventType eventType) throws Exception {

		Map<String, Object> dataMap = new HashMap<>();
		rowData.getAfterColumnsList().forEach(
				column -> {
					dataMap.put(column.getName(), column.getValue());//TODO 字段映射
				}
		);

		String id = (String) dataMap.get("petition_case_id");
		/* 信访件 */
		Map<String, Object> petitionCaseMap = Optional
				.ofNullable(ElasticsearchUtil.searchDataById(PT_PETITION_CASE, PT_PETITION_CASE, id, ""))
				.orElse(new HashMap<>());
		dataMap.putAll(petitionCaseMap);
		/* 信访件状态排序 */
		dataMap.put("petition_status_code_sort", selfDefineCode((String) petitionCaseMap.get("petition_status_code")));

		/* 索引是否存在 */
		if (!ElasticsearchUtil.isIndexExist(PT_PETITION_PERSON_CASE_REDUCE)) {
			ElasticsearchUtil.createIndex(PT_PETITION_PERSON_CASE_REDUCE);
			log.info("创建索引:{}-{}", PT_PETITION_PERSON_CASE_REDUCE, "ElasticSearchReduceService#petitionPersonWithCase");
		}

		switch (eventType) {
			case INSERT:
				ElasticsearchUtil.addData(JSONObject.parseObject(JSON.toJSONString(dataMap)), PT_PETITION_PERSON_CASE_REDUCE, PT_PETITION_PERSON_CASE_REDUCE, (String) dataMap.get("id"));
				break;
			case UPDATE:
				ElasticsearchUtil.addData(JSONObject.parseObject(JSON.toJSONString(dataMap)), PT_PETITION_PERSON_CASE_REDUCE, PT_PETITION_PERSON_CASE_REDUCE, (String) dataMap.get("id"));
				break;
			case DELETE:
				ElasticsearchUtil.deleteDataById(PT_PETITION_PERSON_CASE_REDUCE, PT_PETITION_PERSON_CASE_REDUCE, (String) dataMap.get("id"));
				break;
		}
	}

	/**
	 * 排序编号转换
	 *
	 * @param code
	 * @return
	 */
	private String selfDefineCode(String code) {
		switch (Optional.ofNullable(code).orElse("")) {
			case "14"://审核认定办结 14
				code = "000";
				break;
			case "11"://复核 11
				code = "100";
				break;
			case "7":
				code = "200";
				break;//复查 7
			default:
				code = "999";
				break;
		}
		return code;
	}
}
