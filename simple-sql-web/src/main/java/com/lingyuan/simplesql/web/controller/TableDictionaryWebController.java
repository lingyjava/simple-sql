package com.lingyuan.simplesql.web.controller;

import com.lingyuan.simplesql.common.db.TableDictionaryHelper;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
public class TableDictionaryWebController {

	private final TableDictionaryHelper db = new TableDictionaryHelper();

	@GetMapping("/tables")
	public String tableDictionaryPage(
			@RequestParam(value = "tableName", required = false) String tableName,
			@RequestParam(value = "databaseName", required = false) String databaseName,
			Model model
	) {
		List<TableDictionaryHelper.TableDictionaryInfo> records;
		if (StringUtils.hasText(tableName) && StringUtils.hasText(databaseName)) {
			records = db.searchByTableName(tableName).stream()
					.filter(i -> i.getDatabaseName() != null && i.getDatabaseName().toLowerCase().contains(databaseName.toLowerCase()))
					.collect(Collectors.toList());
		} else if (StringUtils.hasText(tableName)) {
			records = db.searchByTableName(tableName);
		} else if (StringUtils.hasText(databaseName)) {
			records = db.searchByDatabaseName(databaseName);	
		} else {
			records = db.getAllTableDictionary();
		}
		model.addAttribute("records", records);
		model.addAttribute("tableName", tableName);
		model.addAttribute("databaseName", databaseName);
		return "table_dictionary";
	}

	@PostMapping("/tables/add")
	public String addTableDictionary(
			@RequestParam("tableName") String tableName,
			@RequestParam(value = "databaseName", required = false) String databaseName,
			@RequestParam(value = "remark", required = false) String remark,
			Model model
	) {
		if (!StringUtils.hasText(tableName)) {
			model.addAttribute("error", "表名不能为空");
			return "redirect:/tables";
		}
		db.addTableDictionary(
			tableName.trim(), 
			StringUtils.hasText(databaseName) ? databaseName.trim() : null,
			StringUtils.hasText(remark) ? remark.trim() : null
		);
		return "redirect:/tables";
	}

	@PostMapping("/tables/delete")
	public String deleteTableDictionary(@RequestParam("id") Integer id) {
		if (id != null) {
			db.deleteTableDictionary(id);
		}
		return "redirect:/tables";
	}

	@GetMapping(value = "/api/tables", produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public List<Map<String, Object>> apiTableDictionaryList() {
		return db.getAllTableDictionary().stream().map(i -> {
			Map<String, Object> m = new HashMap<>();
			m.put("id", i.getId());
			m.put("tableName", i.getTableName());
			m.put("databaseName", i.getDatabaseName());
			m.put("remark", i.getRemark());
			m.put("createTime", i.getCreateTime());
			return m;
		}).collect(Collectors.toList());
	}

	@GetMapping(value = "/api/tables/lookup", produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public ResponseEntity<Map<String, Object>> apiTableDictionaryLookup(@RequestParam("name") String name) {
		if (!StringUtils.hasText(name)) {
			return ResponseEntity.badRequest().build();
		}
		List<TableDictionaryHelper.TableDictionaryInfo> list = db.searchByTableName(name);
		if (list.isEmpty()) {
			return ResponseEntity.notFound().build();
		}
		TableDictionaryHelper.TableDictionaryInfo first = list.get(0);
		Map<String, Object> m = new HashMap<>();
		m.put("id", first.getId());
		m.put("tableName", first.getTableName());
		m.put("databaseName", first.getDatabaseName());
		m.put("remark", first.getRemark());
		m.put("createTime", first.getCreateTime());
		return ResponseEntity.ok(m);
	}
}


