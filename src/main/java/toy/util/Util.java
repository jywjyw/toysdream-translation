package toy.util;

import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.imageio.ImageIO;

public class Util {
	
	public static int hilo(int i) {
		return i>>>24|i>>>8&0xff00|i<<8&0xff0000|i<<24&0xff000000;
	}
	
	public static int hiloShort(int i) {
		return i>>>8&0xff|i<<8&0xff00;
	}
	
	private static String show(Set<String> set) {
		StringBuilder sb = new StringBuilder();
		for(String s : set) {
			sb.append(s+" ");
		}
		return sb.toString();
	}
	
	/** 
	 * MD5加密
	 * @param cont 要加密的字节数组 
	 * @return    加密后的字符串 
	 */  
	public static String toMd5(byte[] cont){  
	    try {  
	        MessageDigest md = MessageDigest.getInstance("MD5");  
	        md.update(cont);  
	        byte[] byteDigest = md.digest();  
	        int i;  
	        StringBuilder buf = new StringBuilder();  
	        for (int offset = 0; offset < byteDigest.length; offset++) {  
	            i = byteDigest[offset];  
	            if (i < 0)  i += 256;  
	            if (i < 16) buf.append("0"); 
	            buf.append(Integer.toHexString(i));
	        }
//	        return buf.toString().substring(8, 24);		//16位加密     
	        return buf.toString();						//32位加密    
	    } catch (NoSuchAlgorithmException e) {  
	        throw new RuntimeException(e); 
		}
	}
	
	/**
	 * 把request.getParameterMap()转成字符串, 适用于查看POST参数
	 * @param paramMap
	 * @param charset 是否要把参数urlencode
	 * @return
	 */
	public static String paramMapToString(Map paramMap, String charset)	{
		if(paramMap.size() == 0)	return "";
		StringBuilder sb = new StringBuilder();
		for(Object o : paramMap.entrySet())	{
			Entry e = (Entry)o;
			if(e.getValue() instanceof String[])	{
				for(String val : (String[])e.getValue())	{
					if(charset != null)	{
						try {
							val = URLEncoder.encode(val, charset);
						} catch (UnsupportedEncodingException e1) {
							e1.printStackTrace();
						}
					}
					sb.append(e.getKey()).append("=").append(val).append("&");
				}
			} else {
				String value = (String)e.getValue();
				if(charset != null)	{
					try {
						value = URLEncoder.encode(value, charset);
					} catch (UnsupportedEncodingException e1) {
						e1.printStackTrace();
					}
				}
				sb.append(e.getKey()).append("=").append(value).append("&");
			}
		}
		if(sb.toString().endsWith("&"))	{
			sb.deleteCharAt(sb.length()-1);
		}
		return sb.toString();
	}
	
	public static String join(List<String> list, String splitter)	{
		return join(list, splitter, null);
	}
	
	/**
	 * 将字符串集合分隔拼接成一条字符串
	 * @param list 集合
	 * @param splitter 分隔符
	 * @param replacement 将与分隔符有冲突的字符串替换
	 * @return
	 */
	public static String join(List<String> list, String splitter, String replacement)	{
		StringBuilder sb = new StringBuilder();
		for(int i=0; i<list.size(); i++)	{
			if(replacement != null && list.get(i) != null)	{
				sb.append(list.get(i).replace(splitter, replacement));
			} else	{
				sb.append(list.get(i));
			}
			if(i < list.size() - 1)	{
				sb.append(splitter);
			}
		}
		return sb.toString();
	}
	
	public static String toHexString(byte[] bs) {
		StringBuilder sb = new StringBuilder();
		for(int i=0;i<bs.length;i++) {
			sb.append(String.format("%02X", bs[i]&0xff));
		}
		return sb.toString();
	}
	
	/**
	 * 关闭多个流
	 * @param closeable
	 */
	public static void close(Closeable...closeable)	{
		for(Closeable c : closeable)	{
			try {
				if(c != null)	c.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	public static byte[] copyPartFile(String file, long startPos, int length) {
		byte[] buf = new byte[length];
		try {
			RandomAccessFile bin = new RandomAccessFile(new File(file), "r");
			bin.seek(startPos);
			bin.read(buf);
			bin.close();
			return buf;
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
	public static void overwriteFile(String file, long startPos, byte[] data) {
		try {
			RandomAccessFile bin = new RandomAccessFile(new File(file), "rw");
			bin.seek(startPos);
			bin.write(data);
			bin.close();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
	
	public static void writeFile(File target, byte[] data){
		try {
			FileOutputStream fos = new FileOutputStream(target);
			fos.write(data);
			fos.close();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
	
	public static void mkdirs(String dir){
		File f=new File(dir);
		if(!f.exists())
			f.mkdirs();
	}
	
	public static int substringCount(String parentStr, String substringRegex) {
		int count = 0;
		Pattern sub = Pattern.compile(substringRegex);
		Matcher m = sub.matcher(parentStr);
		while (m.find()) {
			count++;
		}
		return count;
	}
	
	public static byte[] scaleToPng(File img, int toWidth) throws IOException{
		BufferedImage src = ImageIO.read(img);
		double scaleRatio = (double)toWidth/src.getWidth();
		AffineTransform at = new AffineTransform();
		at.scale(scaleRatio,scaleRatio);
		BufferedImage dst = new BufferedImage(toWidth, (int)(src.getHeight()*scaleRatio), BufferedImage.TYPE_INT_ARGB);
		dst = new AffineTransformOp(at, AffineTransformOp.TYPE_BICUBIC).filter(src, dst);
		
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		ImageIO.write(dst, "png", bos);
		return bos.toByteArray();
	}
	
	public static void main(String[] args) {
		System.out.println(substringCount("{br}", "{br}"));
	}
	
}
