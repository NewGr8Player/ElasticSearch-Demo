package com.xavier.es.api;

import com.xavier.es.util.ElasticsearchUtil;
import com.xavier.es.util.EsPage;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 基础查询接口
 *
 * @author NewGr8player
 */
@Api(value = "查询接口")
@RestController
public class ElasticsearchQueryApi {

	@ApiOperation(value = "根据传入条件进行分页查询", httpMethod = "POST")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "index", value = "索引名称(必须全小写)", paramType = "query", dataType = "String", example = "index"),
			@ApiImplicitParam(name = "type", value = "类型名称(必须全小写)", paramType = "query", dataType = "String", example = "typeA,typeB"),
			@ApiImplicitParam(name = "currentPage", value = "当前页", paramType = "query", dataType = "String", example = "1"),
			@ApiImplicitParam(name = "pageSize", value = "每页显示条数", paramType = "query", dataType = "String", example = "20"),
			@ApiImplicitParam(name = "fields", value = "需要显示的字段", paramType = "query", dataType = "String", example = "fieldA,fieldB"),
			@ApiImplicitParam(name = "sortField", value = "排序字段", paramType = "query", dataType = "String", example = "sort::asc,sort2::desc"),
			@ApiImplicitParam(name = "matchPhrase", value = "短语精准匹配", paramType = "query", dataType = "String", example = "true"),
			@ApiImplicitParam(name = "highlightFields", value = "高亮字段", paramType = "query", dataType = "String", example = "hla,hlb,hlc"),
			@ApiImplicitParam(name = "matchStr", value = "过滤条件", paramType = "query", dataType = "String", example = "xxx=111,aaa=222"),
			@ApiImplicitParam(name = "groupField", value = "聚合条件(不填写，不聚合)", paramType = "query", dataType = "String", example = "fieldName")
	})
	@PostMapping(path = "/page")
	public EsPage pageQuery(@RequestParam(name = "index") String index,
	                        @RequestParam(name = "type") String type,
	                        @RequestParam(name = "currentPage", defaultValue = "0") String currentPage,
	                        @RequestParam(name = "pageSize", defaultValue = "10") String pageSize,
	                        @RequestParam(name = "fields", defaultValue = "", required = false) String fields,
	                        @RequestParam(name = "sortField", defaultValue = "") String sortField,
	                        @RequestParam(name = "matchPhrase", defaultValue = "false") String matchPhrase,
	                        @RequestParam(name = "highlightFields", defaultValue = "", required = false) String highlightFields,
	                        @RequestParam(name = "matchStr") String matchStr,
	                        @RequestParam(name = "groupField",defaultValue = "") String groupField
	) {
		int currentPageNum = Integer.valueOf(currentPage);
		int pageSizeNum = Integer.valueOf(pageSize);
		boolean matchPhraseBoolean = Boolean.valueOf(matchPhrase);
		List<String> highlightFieldList = Arrays.stream(highlightFields.split(","))
				.filter(StringUtils::isNotBlank)
				.map(String::trim)
				.collect(Collectors.toList());
		List sortFieldList = Arrays.stream(sortField.split(","))
				.filter(s -> StringUtils.isNotBlank(s) && s.contains("::"))
				.map(s -> {
					Map fildInfoMap = new HashMap<String, String>();
					String[] fieldAndSort = s.split("::");
					fildInfoMap.put("field", fieldAndSort[0]);
					fildInfoMap.put("sort", fieldAndSort[1].toLowerCase());
					return fildInfoMap;
				})
				.collect(Collectors.toList());
		try {
			if (StringUtils.isNotBlank(groupField)) {
				return ElasticsearchUtil.searchDataPageGrouped(index, type,
						currentPageNum, pageSizeNum, 0, 0,
						fields, sortFieldList, matchPhraseBoolean, highlightFieldList
						, matchStr, groupField);
			} else {
				return ElasticsearchUtil.searchDataPage(index, type,
						currentPageNum, pageSizeNum, 0, 0,
						fields, sortFieldList, matchPhraseBoolean, highlightFieldList
						, matchStr);
			}

		} catch (Exception e) {
			e.printStackTrace();
			return new EsPage(0,0,0,new ArrayList<>());
		}
	}

}
