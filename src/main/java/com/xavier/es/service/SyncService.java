package com.xavier.es.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.otter.canal.protocol.CanalEntry;
import com.alibaba.otter.canal.protocol.CanalEntry.Column;
import com.alibaba.otter.canal.protocol.CanalEntry.RowData;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Service
public class SyncService {

	@Autowired
	private ElasticsearchService elasticsearchService;

	protected void doSync(String database, String table, String index, String type, RowData rowData) {
		List<Column> columns = rowData.getAfterColumnsList();
		String primaryKey = Optional.ofNullable((String) new HashMap<>().get("null")).orElse("id");
		CanalEntry.Column idColumn = columns.stream().filter(
				column -> column.getIsKey() && primaryKey.equals(column.getName())
		).findFirst().orElse(null);
		if (idColumn == null || StringUtils.isBlank(idColumn.getValue())) {
			log.warn("insert_column_find_null_warn insert从column中找不到主键,database=" + database + ",table=" + table);
			return;
		}
		log.debug("insert_column_id_info insert主键id,database=" + database + ",table=" + table + ",id=" + idColumn.getValue());
		Map<String, Object> dataMap = parseColumnsToMap(columns);
		elasticsearchService.insertById(index, type, idColumn.getValue(), dataMap);
		log.debug("insert_es_info 同步es插入操作成功！database=" + database + ",table=" + table + ",data=" + JSON.toJSONString(dataMap));
	}

	Map<String, Object> parseColumnsToMap(List<Column> columns) {
		Map<String, Object> jsonMap = new HashMap<>();
		columns.forEach(column -> {
			if (column == null) {
				return;
			}
			jsonMap.put(column.getName(), column.getIsNull() ? null : getElasticsearchTypeObject(column.getMysqlType(), column.getValue()));
		});
		return jsonMap;
	}

	public Object getElasticsearchTypeObject(String mysqlType, String data) {
		Optional<Map.Entry<String, Converter>> result = mysqlTypeElasticsearchTypeMapping.entrySet().parallelStream().filter(entry -> mysqlType.toLowerCase().contains(entry.getKey())).findFirst();
		return (result.isPresent() ? result.get().getValue() : (Converter) data1 -> data1).convert(data);
	}

	private static Map<String, Converter> mysqlTypeElasticsearchTypeMapping = Maps.newHashMap();
	private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

	static {
		mysqlTypeElasticsearchTypeMapping.put("char", data -> data);
		mysqlTypeElasticsearchTypeMapping.put("text", data -> data);
		mysqlTypeElasticsearchTypeMapping.put("blob", data -> data);
		mysqlTypeElasticsearchTypeMapping.put("int", Long::valueOf);
		mysqlTypeElasticsearchTypeMapping.put("date", data -> LocalDateTime.parse(data, formatter));
		mysqlTypeElasticsearchTypeMapping.put("time", data -> LocalDateTime.parse(data, formatter));
		mysqlTypeElasticsearchTypeMapping.put("float", Double::valueOf);
		mysqlTypeElasticsearchTypeMapping.put("double", Double::valueOf);
		mysqlTypeElasticsearchTypeMapping.put("decimal", Double::valueOf);
	}

	private interface Converter {
		Object convert(String data);
	}
}
