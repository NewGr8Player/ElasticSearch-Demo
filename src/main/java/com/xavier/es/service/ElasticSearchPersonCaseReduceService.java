package com.xavier.es.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.otter.canal.protocol.CanalEntry;
import com.xavier.es.util.ElasticsearchUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
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
				column -> dataMap.put(column.getName(), column.getValue())
		);

		String personId = (String) dataMap.get("id");
		String petition_case_id = (String) dataMap.get("petition_case_id");
		if (StringUtils.isNotBlank(petition_case_id)) {
			/* 信访件 */
			Map<String, Object> petitionCaseMap = ElasticsearchUtil.searchDataById(PT_PETITION_CASE, PT_PETITION_CASE, petition_case_id, "");
			dataMap.putAll(petitionCaseMap);
			/* 信访件状态排序 */
			dataMap.put("petition_status_code_sort", selfDefineCode((String) petitionCaseMap.get("petition_status_code")));
		}

		/* 索引是否存在 */
		if (!ElasticsearchUtil.isIndexExist(PT_PETITION_PERSON_CASE_REDUCE)) {
			ElasticsearchUtil.createIndex(PT_PETITION_PERSON_CASE_REDUCE);
			log.info("创建索引:{}-{}", PT_PETITION_PERSON_CASE_REDUCE, "ElasticSearchReduceService#petitionPersonWithCase");
		}

		String reduceId = petition_case_id + "_" + personId;
		String reduceTableName = PT_PETITION_PERSON_CASE_REDUCE;
		String reduceTypeName = PT_PETITION_PERSON_CASE_REDUCE;
		switch (eventType) {
			case INSERT:
				ElasticsearchUtil.addData(JSONObject.parseObject(JSON.toJSONString(dataMap)), reduceTableName, reduceTypeName, reduceId);
				break;
			case UPDATE:
				ElasticsearchUtil.updateDataById(JSONObject.parseObject(JSON.toJSONString(dataMap)), reduceTableName, reduceTypeName, reduceId);
				break;
			case DELETE:
				ElasticsearchUtil.deleteDataById(reduceTableName, reduceTypeName, reduceId);
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
		switch (Integer.valueOf(Optional.ofNullable(code).orElse("99"))) {
			case 14://审核认定办结 14
				code = "a";
				break;
			case 12://复核 12
				code = "b";
				break;
			case 8:
				code = "c";
				break;//复查 8
			default:
				code = "z";
				break;
		}
		return code;
	}
}
