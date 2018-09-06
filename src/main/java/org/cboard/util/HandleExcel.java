package org.cboard.util;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HandleExcel {

	/**
	 * 从指定路径读取Excel文件，返回类型为List<Map<String,String>>
	 *
	 * @param path
	 * @throws IOException
	 */
	public static List<Map<String, String>> readExcel(String path) throws Exception {

		List<Map<String, String>> mapList = new ArrayList<Map<String, String>>();
		File file = new File(path);
		// 判断文件是否存在
		if (file.isFile() && file.exists()) {
			System.out.println(file.getPath());
			// 获取文件的后缀名 \\ .是特殊字符
			String[] split = file.getName().split("\\.");
			System.out.println(split[1]);
			Workbook wb;
			// 根据文件后缀（xls/xlsx）进行判断
			if ("xls".equals(split[1])) {
				// //获取文件流对象
				FileInputStream inputStream = new FileInputStream(file);
				wb = new HSSFWorkbook(inputStream);
			} else if ("xlsx".equals(split[1])) {
				FileInputStream inputStream = new FileInputStream(file);
				wb = new XSSFWorkbook(inputStream);
			} else {
				System.out.println("文件类型错误");
				return null;
			}

			// 开始解析
			Sheet sheet = wb.getSheetAt(0);
			// 第一行是列名，所以从第二行开始遍历
			int firstRowNum = sheet.getFirstRowNum() + 1;
			// getLastRowNum() 最后一行行标，比行数小1
			int lastRowNum = sheet.getLastRowNum();

			// 遍历行
			for (int rIndex = firstRowNum; rIndex <= lastRowNum; rIndex++) {
				Map<String, String> map = new HashMap<String, String>();
				// 获取当前行的内容
				Row row = sheet.getRow(rIndex);
				if (row != null) {
					int firstCellNum = row.getFirstCellNum();
					// getLastCellNum() 获取列数，比最后一列列标大1
					int lastCellNum = row.getLastCellNum();
					for (int cIndex = firstCellNum; cIndex < lastCellNum; cIndex++) {
						row.getCell(cIndex).setCellType(Cell.CELL_TYPE_STRING);
						// 获取单元格的值
						String value = row.getCell(cIndex).getStringCellValue();
						System.out.println("value:" + value);
						// 获取此单元格对应第一行的值
						String key = sheet.getRow(0).getCell(cIndex).getStringCellValue();
						System.out.println("key:" + key);
						// 第一行中的作为键，第n行的作为值
						if (value == null || "".equals(value)) {
							continue;
						}
						map.put(key, value);
						System.out.println("map:" + map);
					}
				}
				mapList.add(map);
				System.out.println("读取成功");
				System.out.println("mapList:" + mapList);
			}
		}
		return mapList;
	}
}
