package toy.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class ChangePicSize {
	
	public static void main(String[] args) throws IOException {
		File[] pics = new File("C:\\Users\\Administrator\\Desktop\\toy\\").listFiles();
		for(File f:pics){
			byte[] buf = Util.scaleToPng(f, 420);
			FileOutputStream fos = new FileOutputStream("C:\\Users\\Administrator\\Desktop\\toysm\\"+f.getName());
			fos.write(buf);
			fos.close();
		}
	}

}
