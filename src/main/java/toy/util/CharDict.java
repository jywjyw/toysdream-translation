package toy.util;

public class CharDict {
	public static void main(String[] args) {
		CharDict xy = new CharDict();
		System.out.println(xy.toCoord("2b01"));
		System.out.println(xy.toCode(2,6,8));
		
	}
	
	private String toCode(int region,int x, int y) {
		short s = (short) ((region-1)*PX*PY+(y-1)*PX+x-1);
		return String.format("%04X", changeHilo(s));
	}
	
	private short changeHilo(short s) {
		return (short) ((short)(s<<8)|(s>>>8));
	}
	
	private static final int PX=21,PY=21;
	public static FontPos toCoord(String code) {
		String _code = code.substring(2, 4)+code.substring(0,2);
		short s = Short.parseShort(_code, 16);
		int plus = PX*PY;
		int region=s/plus+1;
		int b = s-s/plus*plus;
		int coordY = b/PX+1;
		int coordX = b-b/PX*PX+1;
		return new FontPos(region, coordX, coordY);
	}

}
