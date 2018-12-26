package toy.dump;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import toy.CharTable;
import toy.Conf;
import toy.Resource;
import toy.dump.BinScriptReader.Callback;

public class ExcelExporter implements BinScriptReader.Callback{
	
	File bin;
	CharTable charTable;
	Map<String, Integer> src;
	
	XSSFWorkbook book;
	XSSFSheet sheet;
	String key;

	public ExcelExporter(File bin,CharTable charTable,Map<String, Integer> src) {
		this.book = new XSSFWorkbook();
		this.sheet = book.createSheet();
		sheet.setColumnWidth(2, 20000);
		sheet.setColumnWidth(3, 10000);
		this.row = sheet.createRow(0);
		this.bin = bin;
		this.charTable = charTable;
		this.src = src;
	}

	public void export(String target) throws IOException {
		for (Entry<String, Integer> e : src.entrySet()) {
			this.key = e.getKey();
			this.pointerIndex = 1;
			this.row.createCell(0).setCellValue(key);
			this.row.createCell(1).setCellValue(pointerIndex);
			new Scripts().exportAsEvent(bin, charTable, e.getValue(), this);
		}

		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		try {
			book.write(bos);
		} catch (IOException e) {
			throw new RuntimeException(e);
		} finally {
			try {
				book.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		FileOutputStream fos = new FileOutputStream(target);
		fos.write(bos.toByteArray());
		fos.flush();
		fos.close();
	}
	
	boolean toLeft = true, speakBegin=false;
	int rowNum = 0, pointerIndex = 1;
	Row row = null;

	@Override
	public void isSingleCtrl(String s) {
		writeCtrl(s);
		if(speakBegin) {
			speakBegin = false;
		}
	}
	
	private void writeCtrl(String s) {
		if (toLeft) {
			append(row, 2, s);
		} else {
			row = sheet.createRow(++rowNum);
			append(row,2,s);
		}
		toLeft=true;
		row.createCell(0).setCellValue(key);
		row.createCell(1).setCellValue(pointerIndex);
	}
	
	@Override
	public void isChar(String s) {
		if(speakBegin) {
			append(row,2,s);
		}  else {
			if (toLeft) {
				append(row,3,s);
			} else {
				append(row,3,s);
			}
			toLeft=false;
		}
	}

	@Override
	public void speakerBegin(String s) {
		writeCtrl(s);
		speakBegin = !speakBegin;
	}

	@Override
	public void speakerEnd(String s) {
		writeCtrl(s);
		speakBegin = !speakBegin;
	}

	@Override
	public void newIndex() {
		pointerIndex++;
		rowNum++;
		row = sheet.createRow(rowNum);

		toLeft=true;
		speakBegin=false;
	}
	
	private void append(Row row, int cellIndex, String val) {
		Cell cell = row.getCell(cellIndex);
		if(cell==null) cell = row.createCell(cellIndex);
		cell.setCellValue(cell.getStringCellValue()+val);
	}

}
