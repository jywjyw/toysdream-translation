package hanhua;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.util.List;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;

public class PointerTable1 {
	
	public byte[] export(File filelinkbin, File charTbl, long pointerPos, long textStartPos) throws IOException {
		CharTable charTable = new CharTable(charTbl);
		RandomAccessFile pointer = new RandomAccessFile(filelinkbin, "r");
		pointer.seek(pointerPos);
		RandomAccessFile text = new RandomAccessFile(filelinkbin, "r");
		text.seek(textStartPos);
		Document doc = DocumentHelper.createDocument();
		Element root = doc.addElement("xml");
		
		int pointerCount = 0;
		while(pointer.readInt()!=0x02f0ffff) {
			pointerCount++;
		}
		long _textPos = textStartPos;
		for(int i=1;i<=pointerCount;i++) {
			TxtResult result = Util.readText(text, _textPos, charTable);
			_textPos = result.textFilePos;
			Element indexE = root.addElement("_"+i);
			indexE.addElement("japanese").setText(result.text);
//			indexE.addElement("chinese").setText(txt);
		}
		pointer.close();
		text.close();
		
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		OutputFormat fmt = OutputFormat.createPrettyPrint();
		fmt.setEncoding("gbk");
	    XMLWriter writer = new XMLWriter(bos, fmt);
		writer.write(doc);
		writer.close();
		return bos.toByteArray();
	}
	
	
	public void import_(List<byte[]> rebuild, File filelinkbin,
			long pointerPos, int pointerStartValue, int pointerCount) throws IOException{

		ByteBuffer pointers = ByteBuffer.allocate(4*(pointerCount+1));
		int last = Util.hilo(pointerStartValue);
		for(byte[] txt : rebuild) {
			pointers.putInt(Util.hilo(last));
			last = last+txt.length;
		}
		pointers.putInt(0x02f0ffff);	//指针与正文的分隔符
		
		RandomAccessFile bin = new RandomAccessFile(filelinkbin, "rw");
		bin.seek(pointerPos);
		bin.write(pointers.array());
		for(byte[] txt : rebuild) {
			bin.write(txt);
		}
		bin.close();
	}
	
	

}
