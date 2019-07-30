package hanhua;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.LinkedList;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;

public class PointerTable3 {
	
	public static void main(String[] args) throws IOException {
		String dir = "D:\\ps3\\hanhua\\toysdream\\";
		File bin = new File(dir+"FILELINK.BIN");
		File tbl = new File(dir+"toys-dream.tbl"); 
		
		PointerTable3.Callback untilText = new PointerTable3.Callback() {
			@Override
			public boolean isBreak(int readed, long pos, long textPos) {
				return pos>textPos;
			}
		};
		
		
		byte[] menu = new PointerTable3().export(bin,tbl,0x0012d51c, 0x0012da88, untilText);	
		save(dir+"menu.xml", menu);
		
		byte[] location = new PointerTable3().export(bin,tbl,0x0012ffb0, 0x00130460, untilText);	
		save(dir+"location.xml", location);
		
		byte[] item = new PointerTable3().export(bin,tbl,0x0013128c, 0x00131a8c, new PointerTable3.Callback() {
			@Override
			public boolean isBreak(int readed, long pos, long textPos) {
				return readed==0x02f0ffff;
			}
		});	
		save("dir+item.xml", item);
		
		byte[] mission = new PointerTable3().export(bin,tbl,0x00134468, 0x0013549c, new PointerTable3.Callback() {
			@Override
			public boolean isBreak(int readed, long pos, long textPos) {
				return readed==0x02f0ffff;
			}
		});	//pointer end=0x0015A414;
		save(dir+"mission.xml", mission);
		
		byte[] npc1 = new PointerTable3().export(bin,tbl,0x00257de0, 0x00257f88, untilText);
		save(dir+"npc1.xml", npc1);
		byte[] npc2 = new PointerTable3().export(bin,tbl,0x0025acb8, 0x0025ae58, untilText);
		save(dir+"npc2.xml", npc2);
		byte[] npc3 = new PointerTable3().export(bin,tbl,0x0025d798, 0x0025d964, untilText);
		save(dir+"npc3.xml", npc3);
		byte[] npc4 = new PointerTable3().export(bin,tbl,0x00260968, 0x00260a8c, untilText);
		save(dir+"npc4.xml", npc4);
		byte[] npc5 = new PointerTable3().export(bin,tbl,0x00262190, 0x002622b0, untilText);
		save(dir+"npc5.xml", npc5);
		byte[] npc6 = new PointerTable3().export(bin,tbl,0x00263324, 0x00263440, untilText);
		save(dir+"npc6.xml", npc6);
		byte[] npc7 = new PointerTable3().export(bin,tbl,0x00264ab4, 0x00264b1c, untilText);
		save(dir+"npc7.xml", npc7);
		byte[] npc8 = new PointerTable3().export(bin,tbl,0x00265b90, 0x00265d60, untilText);
		save(dir+"npc8.xml", npc8);
		byte[] npc9 = new PointerTable3().export(bin,tbl,0x00267e70, 0x00267fa0, untilText);
		save(dir+"npc9.xml", npc9);
		byte[] npc10 = new PointerTable3().export(bin,tbl,0x002697a8, 0x00269838, untilText);
		save(dir+"npc10.xml", npc10);
		
		System.out.println("over...");
	}
	
	private static void save(String file, byte[] bs) {
		try {
			FileOutputStream fos = new FileOutputStream(file);
			fos.write(bs);
			fos.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	interface Callback {
		boolean isBreak(int readed, long pos, long textPos);
	}
	
	public byte[] export(File filelinkbin, File charTbl, long pointerPos, long textPos, Callback callback) throws IOException {
		RandomAccessFile pointer = new RandomAccessFile(filelinkbin, "r");
		pointer.seek(pointerPos);	
		long posP=pointerPos;
		
		RandomAccessFile text = new RandomAccessFile(filelinkbin, "r");
		text.seek(textPos);
		CharTable charTable = new CharTable(charTbl);
		
		int index=0, bufP=0, bufT=0;
		int prevT=0, prev2T=0;
		Integer lastP = null;
		StringBuilder s;
		Document doc = DocumentHelper.createDocument();
		Element root = doc.addElement("xml");
		while(true) {
			bufP = pointer.readInt();
			posP+=4;
//			if(bufP==0x02f0ffff)	break;
			if(callback.isBreak(bufP, posP, textPos)) break;
			
			bufP=hilo(bufP);
			if(lastP != null) {
				int readLen = bufP-lastP;
				s = new StringBuilder();
				for(int i=0; i<readLen; i+=2) {
					bufT=text.readUnsignedShort();
					if(prev2T==0x08f0 && prevT==0x0000) {
						s.append(String.format("{icon%04X}", bufT));
					} else if(prevT==0x08f0){
					} else if(prevT==0x03F0){
						if(bufT==0x0000) {
							s.append("{03f0[");
						} else if(bufT==0xffff){
							s.append("]03f0}");
						}
					} else if(bufT==0x03f0||bufT==0x08f0) {
					} else if(bufT==0x00f0) {
						s.append("{ }");
					} else if(bufT==0x01f0) {
						s.append("{br}");
					} else if(bufT==0x02f0){
						s.append("{02f0}");
					} else if(bufT==0x04f0) {
						s.append("{wait}");
					} else if(bufT==0xFFFF) {
						s.append("{end}");
					} else if(charTable.containChar(bufT)){
						s.append(charTable.getChar(bufT));
					} else {
						s.append(String.format("[%04X]", bufT));
					}
					prev2T=prevT;
					prevT=bufT;
				}
				index++;
//				System.out.println(index+"=="+s.toString());
				Element indexE = root.addElement(index+"");
				indexE.addElement("japanese").setText(s.toString());
//				indexE.addElement("chinese").setText(s.toString());
//				indexE.addElement("final").setText(s.toString());
			}
			lastP = bufP;
		}
		pointer.close();
		text.close();
		
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		OutputFormat fmt = OutputFormat.createPrettyPrint();
		fmt.setEncoding("utf-8");
	    XMLWriter writer = new XMLWriter(bos, fmt);
		writer.write(doc);
		writer.close();
		return bos.toByteArray();
	}
	
	private int hilo(int i) {
		return i>>>24|i>>>8&0xff00|i<<8&0xff0000|i<<24&0xff000000;
	}
	
	private boolean eq(LinkedList<Integer> list, int index, int value) {
		try {
			Integer exist = list.get(index);
			if(exist==value) return true;
		} catch (IndexOutOfBoundsException e) {}
		return false;
	}
	
	public void import_() {
		
	}

}
