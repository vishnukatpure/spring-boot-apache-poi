package com.example.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.stereotype.Service;

@Service
public class JsonToXlsService {

	public OutputStream testReadXlsFile() throws Exception {
		// --- json String
		String stringToParse = "" + "{reportData:" + "{name: Machine Dashboard v2,cols: "
				+ "[ { name: machinename, type: String, description: Machine, width: 20% },"
				+ "{ name: partname, type: String, description: Part, width: 48% },"
				+ "{ name: targetquantity, type: String, description: Planned, width: 13% },"
				+ "{ name: cycletime, type: String, description: Cycle Time },"
				+ "{ name: productionplanstarttime, type: String, description: Production Plan Start Time },"
				+ "{ name: activecavity, type: String, description: Active Cavity },"
				+ "{ name: actualquantity, type: Date, description: Actual, width: 13% },"
				+ "{ name: downtimestart, type: Date, description: Down Time Start },"
				+ "{ name: category, type: Date, description: Down Time Category },"
				+ "{ name: mould, type: String, description: Mould},"
				+ "{ name: machinestatus, type: String, description: Machine Status " + "}],holidays: [],"
				+ "reportData: ["
				+ "{machinename: IMM-DL85,cell: cell1,isfamily: false,trial: false,partname: Mld'g bezel glove box LH-IB_LHD,targetquantity: 30000,cycletime: 60,productionplanstarttime: '26-08-2016:16:52:03',activecavity: 1,isNearTarget: false,hasNextPlan: false,isMouldChangeRequired: false,actualquantity: 5106,downtimestart: 0,category: a,machinestatus: UP},"
				+ "{machinename: IMM-DL85,cell: cell1,isfamily: false,trial: false,partname: Mld'g bezel glove box RH-IB_LHD,targetquantity: 30000,cycletime: 60,productionplanstarttime: '26-08-2016:16:52:03',activecavity: 1,isMouldChangeRequired: false,isNearTarget: false,hasNextPlan: false,actualquantity: 5106,downtimestart: 0,category:0 ,machinestatus: UP},"
				+ "{machinename: IMM-FM150-I,cell: cell2,isfamily: false,trial: true,partname: MLG'G - BEZEL,targetquantity: 500,cycletime: 76,productionplanstarttime: '26-08-2016:16:52:03',activecavity: 2,isNearTarget: true,nextmould: XBA E4 BEZEL,cuurmould: XENON - BEZEL,hasNextPlan: true,isMouldChangeRequired: true,actualquantity: 2054,downtimestart: 0,category: 0,machinestatus: UP},"
				+ "{machinename: Total,partname: 0,targetquantity: 30500,cycletime: 0,productionplanstarttime: -,activecavity: 0,actualquantity: 7160,downtimestart: 0,category:0  ,machinestatus: AGGREGATE}],records: 4} }";
		//-- template file
		File template = new File("E:\\project\\shopworx_report_template.xls");
		//------out file
		File fileCopy = new File("D:\\project\\new_copy.xls");
		OutputStream outFile = new FileOutputStream(fileCopy);

		// -- read file
		HSSFWorkbook workbook = readXlsTemplate(template);
		//-- parse string to json
		JSONObject jsonObj = parseStringToJsonObject(stringToParse);
		// -- append jsonData
		workbook = appendJsonDataToTemlate(workbook, jsonObj);
		return writeWorkBookToStream(workbook, outFile);
	}

	private JSONObject parseStringToJsonObject(String jsonString) throws JSONException {
		JSONObject json = new JSONObject(jsonString);
		return json;
	}

	private HSSFWorkbook readXlsTemplate(File file) throws Exception {
		FileInputStream stream = new FileInputStream(file);
		HSSFWorkbook workbook = new HSSFWorkbook(stream);
		return workbook;
	}

	private OutputStream writeWorkBookToStream(HSSFWorkbook workbook, OutputStream stream) throws IOException {
		workbook.write(stream);
		return stream;
	}

	private HSSFCellStyle getHeaderStyle(HSSFWorkbook workbook) {
		// Set Header Font
		HSSFFont headerFont = workbook.createFont();
		headerFont.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
		headerFont.setFontHeightInPoints((short) 11);

		// Set Header Style
		HSSFCellStyle headerStyle = workbook.createCellStyle();
		headerStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER);
		headerStyle.setVerticalAlignment(HSSFCellStyle.ALIGN_FILL);
		headerStyle.setFont(headerFont);
		return headerStyle;
	}
	
	private HSSFWorkbook appendJsonDataToTemlate(HSSFWorkbook workbook, JSONObject json) throws JSONException {

		int cellNum = 0;
		List<String> keys = new ArrayList<String>();
		if (json.has("reportData")) {
			JSONObject reportData = (JSONObject) json.get("reportData");
			if (reportData.has("cols")) {
				
				JSONArray cols = reportData.getJSONArray("cols");

				HSSFSheet sheet = workbook.getSheetAt(0);
				int lastRowIndex = sheet.getLastRowNum();
				HSSFRow row = sheet.createRow(++lastRowIndex);
				for (int i = 0; i < cols.length(); i++) {
					HSSFCell cell = row.createCell(i);
					JSONObject obj = (JSONObject) cols.get(i);
					cell.setCellValue((String) (obj.get("description")));
					keys.add((String) (obj.get("name")));
					cell.setCellStyle(getHeaderStyle(workbook));
				}
				// get column values and insert into row
				if (reportData.has("reportData")) {
					JSONArray rows = reportData.getJSONArray("reportData");
					for (int i = 0; i < rows.length(); i++) {
						row = sheet.createRow(++lastRowIndex);
						JSONObject obj = (JSONObject) rows.get(i);
						for (String key : keys) {
							HSSFCell cell = row.createCell(cellNum++);
							if (obj.has(key)) {
								if (obj.get(key) instanceof Date)
									cell.setCellValue((Date) obj.get(key));
								else if (obj.get(key) instanceof Boolean)
									cell.setCellValue((Boolean) obj.get(key));
								else if (obj.get(key) instanceof String)
									cell.setCellValue((String) (obj.get(key)));
								else if (obj.get(key) instanceof Double)
									cell.setCellValue((Double) obj.get(key));
							}
						}
						cellNum = 0;
					}
				}
			}
		}
		return workbook;
	}
}