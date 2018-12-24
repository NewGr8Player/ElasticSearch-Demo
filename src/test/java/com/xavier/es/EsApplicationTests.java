package com.xavier.es;

import com.alibaba.fastjson.JSONObject;
import com.xavier.es.util.ElasticsearchUtil;
import com.xavier.es.util.EsPage;
import org.apache.commons.lang3.time.DateUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpEntity;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.text.ParseException;
import java.util.*;


@RunWith(SpringRunner.class)
@SpringBootTest(classes = EsApplication.class)
public class EsApplicationTests {

	/**
	 * 创建索引
	 */
	@Test
	public void createIndexTest() {
		ElasticsearchUtil.createIndex("test_index");
		ElasticsearchUtil.createIndex("test_indexsssss");
	}

	/**
	 * 删除索引
	 */
	@Test
	public void deleteIndexTest() {
		ElasticsearchUtil.deleteIndex("test_indexsssss");
	}

	/**
	 * 判断索引是否存在
	 */
	@Test
	public void isIndexExistTest() {
		ElasticsearchUtil.isIndexExist("test_index");
	}

	/**
	 * 数据添加
	 */
	@Test
	public void addDataTest() {

		for (long i = 0; i < 100; i++) {
			Map<String, Object> map = new HashMap<>();

			map.put("name", "晓晓No." + i + "");
			map.put("age", i);
			map.put("interests", "阅读学习");
			map.put("about", "世界上没有优秀的理念，只有脚踏实地的结果");
			map.put("processTime", new Date());

			ElasticsearchUtil.addData(JSONObject.parseObject(JSONObject.toJSONString(map)), "test_index", "about_test", "id=" + i);
		}
	}

	/**
	 * 通过ID删除数据
	 */
	@Test
	public void deleteDataByIdTest() {

		for (int i = 0; i < 100; i++) {
			ElasticsearchUtil.deleteDataById("test_index", "about_test", "id=" + i);
		}
	}


	/**
	 * 通过ID更新数据
	 * <p>
	 * jsonObject 要增加的数据
	 * index      索引，类似数据库
	 * type       类型，类似表
	 * id         数据ID
	 */
	@Test
	public void updateDataByIdTest() {
		Map<String, Object> map = new HashMap<>();

		for (int i = 0; i < 100; i++) {
			map.put("name", "晓晓");
			map.put("age", 11);
			map.put("interests", "阅读学习");
			map.put("about", "这条数据被修改");
			map.put("processTime", new Date());

			ElasticsearchUtil.updateDataById(JSONObject.parseObject(JSONObject.toJSONString(map)), "test_index", "about_test", "id=" + i);
		}


	}

	/**
	 * 通过ID获取数据
	 * <p>
	 * index  索引，类似数据库
	 * type   类型，类似表
	 * id     数据ID
	 * fields 需要显示的字段，逗号分隔（缺省为全部字段）
	 */
	@Test
	public void searchDataByIdTest() {
		Map<String, Object> map = ElasticsearchUtil.searchDataById("test_index", "about_test", "id=11", null);
		System.out.println(JSONObject.toJSONString(map));
	}


	/**
	 * 使用分词查询
	 * <p>
	 * index          索引名称
	 * type           类型名称,可传入多个type逗号分隔
	 * startTime      开始时间
	 * endTime        结束时间
	 * size           文档大小限制
	 * fields         需要显示的字段，逗号分隔（缺省为全部字段）
	 * sortField      排序字段
	 * matchPhrase    true 使用，短语精准匹配
	 * highlightField 高亮字段
	 * matchStr       过滤条件（xxx=111,aaa=222）
	 */
	@Test
	public void searchListData() throws ParseException {

		long startTime = DateUtils.parseDate("2017-11-22 00:00:00", "yyyy-MM-dd HH:mm:ss").getTime();
		long endTime = DateUtils.parseDate("2017-11-23 00:00:00", "yyyy-MM-dd HH:mm:ss").getTime();

		String index = "all_assignservice-*";
		String type = "all_assignservice";
		String matchStr = "message=C000211171122024601";
		int size = 1000;

		List<Map<String, Object>> mapList = ElasticsearchUtil.searchListData(index, type, startTime, endTime, size, "", "", false, null, matchStr);

		Set<String> guidList = new HashSet<String>() {
		};

		Set<String> requestIdList = new HashSet<String>() {
		};

		for (Map<String, Object> guid : mapList) {

			String message = guid.get("message").toString();

			Integer guidIndex = message.indexOf("guid");

			if (message.indexOf("guid") > 1 && message.indexOf("action=>insertWorkOrder") > 1) {

				guidList.add(message.substring(guidIndex + 7, guidIndex + 26));
			}
		}

		for (String guid : guidList) {

			matchStr = "message=" + guid;

			List<Map<String, Object>> tmpMap2 = ElasticsearchUtil.searchListData(index, type, startTime, endTime, size, "", "", false, null, matchStr);

			for (Map<String, Object> requestId : tmpMap2) {

				String message = requestId.get("message").toString();

				if (message.indexOf("crm自动推荐入参:{\"bigClassId") > 1) {
					requestIdList.add(requestId.get("requestId").toString());
				}
			}
		}

		for (String requestId : requestIdList) {

			matchStr = "requestId=" + requestId;
			List<Map<String, Object>> tmpMap3 = ElasticsearchUtil.searchListData(index, type, startTime, endTime, size, "", "", false, null, matchStr);

			for (Map<String, Object> item : tmpMap3) {

				String message = item.get("message").toString();

				Integer startIndex = message.indexOf("crm自动推荐时间-->派工RPC调用-->返回结果为：{");
				Integer endIndex = message.lastIndexOf("}");

				if (startIndex > 1) {

					String resultData = message.substring(startIndex + 28, endIndex + 1);

					JSONObject jsonObject = JSONObject.parseObject(resultData);

					jsonObject = JSONObject.parseObject(jsonObject.get("data").toString());


					System.out.println(jsonObject.get("recommendDabi").toString() + " " + jsonObject.get("startTime").toString());
					System.out.println(jsonObject.get("recommendDabi").toString() + " " + jsonObject.get("endTime").toString());
				}

			}

		}

	}

	/**
	 * 使用分词查询,并分页
	 * <p>
	 * index          索引名称
	 * type           类型名称,可传入多个type逗号分隔
	 * currentPage    当前页
	 * pageSize       每页显示条数
	 * startTime      开始时间
	 * endTime        结束时间
	 * fields         需要显示的字段，逗号分隔（缺省为全部字段）
	 * sortField      排序字段
	 * matchPhrase    true 使用，短语精准匹配
	 * highlightField 高亮字段
	 * matchStr       过滤条件（xxx=111,aaa=222）
	 */
	@Test
	public void searchDataPage() {
		EsPage esPage = ElasticsearchUtil.searchDataPage("test_index", "about_test", 10, 5, 0, 0,
				"", "processTime", false,
				Arrays.asList("interests", "name"), "interests=学习,name=晓晓");
		System.out.println(JSONObject.toJSONString(esPage.getRecordList()));

	}

	@Test
	public void restRequestTest() {
		/* 不建议使用这个方法！仅供本身查询使用 */
		RestTemplate restTemplate = new RestTemplate();

		String sql = "SELECT * FROM test_index";

		MultiValueMap<String, Object> headers = new LinkedMultiValueMap<String, Object>();
		headers.add("Accept", "application/json");
		headers.add("Content-Type", "application/json");
		String requestBody = "{\"sql\":\"" + sql + "\"}";
		HttpEntity request = new HttpEntity(requestBody, headers);
		Map apiResponse = restTemplate.postForObject("http://127.0.0.1:9200/_sql", request, Map.class);
		if (null != apiResponse.get("hits") && null != apiResponse.get("hits")) {
			System.out.println(apiResponse.get("hits"));
		}
	}
}
