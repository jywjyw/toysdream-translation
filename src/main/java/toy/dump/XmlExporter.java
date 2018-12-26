package toy.dump;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.TreeMap;
import java.util.Map.Entry;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;

import toy.CharTable;
import toy.Conf;

public class XmlExporter {
	
	public void export(File bin, CharTable charTable, int startPos, String rscName, String exportDir) throws IOException {
		List<String> txts = new Scripts().exportAsString(bin, charTable, startPos);	
		Document doc = DocumentHelper.createDocument();
		Element root = doc.addElement(rscName);
		int index=1;
		for(String s : txts) {
			Element indexE = root.addElement("_"+index);
			indexE.addElement("jp").setText(s);
			if(Conf.genChinese){
				indexE.addElement("zh").setText(s);
			}
			index++;
		}
		saveXml(exportDir+rscName+".xml", doc);
	}
	
	public Document toDoc(TreeMap<String,Integer> file_pos, File bin, CharTable charTable, String file) throws IOException {
		Document doc= DocumentHelper.createDocument();
		Element root = doc.addElement(file);
		for(Entry<String,Integer> e : file_pos.entrySet()) {
			Element elem = root.addElement(e.getKey());
			int i=1;
			for(String s : new Scripts().exportAsString(bin, charTable,e.getValue())) {
				Element indexE = elem.addElement("_"+i++);
				indexE.addElement("jp").setText(s);
				if(Conf.genChinese){
					indexE.addElement("zh").setText(s);
				}
			}
		}
		return doc;
	}
	
	public void saveXml(String file, Document doc) throws IOException {
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		OutputFormat fmt = OutputFormat.createPrettyPrint();
		fmt.setEncoding("gbk");
		XMLWriter writer = new XMLWriter(bos, fmt);
		writer.write(doc);
		writer.close();
		
		FileOutputStream fos = new FileOutputStream(file);
		fos.write(bos.toByteArray());
		fos.close();
	}

}
