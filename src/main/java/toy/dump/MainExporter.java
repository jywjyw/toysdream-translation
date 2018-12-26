package toy.dump;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.Map.Entry;
import java.util.TreeMap;

import toy.CharTable;
import toy.Conf;
import toy.Resource;
import toy.util.Util;

public class MainExporter {
	
	public static void main(String[] args) throws Exception {
		MainExporter.export();
	}
	

	/**
	 * 剧情,对话,任务导出excel+xml, 菜单,道具,地名导出xml
	 */
	public static void export() throws IOException {
		File bin = new File(Conf.bin);
		Resource map = new Resource();
		String exportDir = Conf.export+"export\\";
		Util.mkdirs(exportDir);
		CharTable charTable = new CharTable();
		TreeMap<String, Integer> npcs = new TreeMap<>(), events = new TreeMap<>();
		for (Entry<Integer, String> e : map.entrySet_address_name()) {
			String v = e.getValue();
			if(v==null) continue;
			if (v.startsWith("npc")) {
				npcs.put(e.getValue(), e.getKey());
			} else if (v.startsWith("event")) {
				events.put(e.getValue(), e.getKey());
			} else if(v.equals("mission")) {
				new ExcelExporter(bin, charTable, Collections.singletonMap(v, e.getKey())).export(exportDir + v + ".xlsx");
				new XmlExporter().export(bin, charTable, e.getKey(), e.getValue(), exportDir);
			} else if(v.equals("menu") || v.equals("location") || v.equals("item")) {
				new XmlExporter().export(bin, charTable, e.getKey(), e.getValue(), exportDir);
			}
		}
		
		new XmlExporter().saveXml(exportDir+"npc.xml", new XmlExporter().toDoc(npcs, bin, charTable, "npc"));
		new XmlExporter().saveXml(exportDir+"event.xml", new XmlExporter().toDoc(events, bin, charTable, "event"));
		
		new ExcelExporter(bin, charTable, npcs).export(exportDir + "npc.xlsx");
		new ExcelExporter(bin, charTable, events).export(exportDir + "event.xlsx");
		
//		ExcelAutoTranslator.batchTranslate(exportDir + "mission.xlsx");
//		ExcelAutoTranslator.batchTranslate(exportDir + "npc.xlsx");
//		ExcelAutoTranslator.batchTranslate(exportDir + "event.xlsx");
		System.out.println("export finish..");
	}
	
	
	
////	public List<byte[]> rebuild() throws Exception {
////		Map<String,Address> confs = getConfig();
////		String what = "event103";
////		Address conf = confs.get(what);
////		File xml = new File(C.DESK+what+".xml");
////		return Util.rebuild(xml, conf.pointerCount, conf.textStartPos, conf.textEndPos);
////	}
////	
////	public void import_() throws Exception {
////		String dir = "f:\\jing\\toysdream\\";
////		File bin = new File("f:\\jing\\isopatcher\\FILELINK.BIN");
////		Map<String,Address> confs = getConfig();
////		String what = "event103";
////		Address conf = confs.get(what);
////		List<byte[]> rebuild =  Util.rebuild(new File(C.DESK+what+".xml"), conf.pointerCount, conf.textStartPos, conf.textEndPos);
////		new PointerTable2().import_(rebuild, bin, conf.pointerStartPos, conf.pointerInitVal, conf.pointerCount);
////	}
	
}
