package hanhua;

public class CharDict {
	public static void main(String[] args) {
		CharDict xy = new CharDict();
		System.out.println(0x1058-0x1038);
		System.out.println(xy.toCoord("0000"));
	}
	
	public static void mainX(String[] args) {
		CharDict x = new CharDict();
		System.out.println("ゴ="+x.toCode(1,2,11));
		System.out.println("メ="+x.toCode(1,5,10));
		System.out.println("ン="+x.toCode(1,17,10));
		System.out.println("み="+x.toCode(1,6,6));
		System.out.println("ん="+x.toCode(1,20,6));
		System.out.println("な="+x.toCode(1,16,5));
		System.out.println(".="+x.toCode(1,16,12));
		System.out.println("换行");//01 f0
		System.out.println("で="+x.toCode(1,14,7));
		System.out.println("も="+x.toCode(1,9,6));
		System.out.println("俺="+x.toCode(1,21,15));
		System.out.println("‘="+x.toCode(1,15,12));
		System.out.println("こ="+x.toCode(1,5,5));
		System.out.println("の="+x.toCode(1,20,5));
		System.out.println("研="+x.toCode(2,2,3));
		System.out.println("究="+x.toCode(1,7,21));
		System.out.println("所=");//9d 02
		System.out.println("を="+x.toCode(1,19,6));
		System.out.println("失="+x.toCode(3,7,20));//0000 0101 0000 0111
		System.out.println("う="+x.toCode(1,19,4));
		System.out.println("の="+x.toCode(1,20,5));
		System.out.println("は="+x.toCode(1,21,5));
		System.out.println("。。。 ="+x.toCode(4,13,1));
		System.out.println("结束前的箭头");//04 f0
		System.out.println("结束");//ff ff
	}
	
	
	
	private String toCode(int region,int x, int y) {
		short s = (short) ((region-1)*PX*PY+(y-1)*PX+x-1);
		return String.format("%04X", changeHilo(s));
	}
	
	private short changeHilo(short s) {
		return (short) ((short)(s<<8)|(s>>>8));
	}
	
	private static final int PX=21,PY=21;
	public Coord toCoord(String code) {
		String _code = code.substring(2, 4)+code.substring(0,2);
		short s = Short.parseShort(_code, 16);
		int plus = PX*PY;
		int region=s/plus+1;
		int b = s-s/plus*plus;
		int coordY = b/PX+1;
		int coordX = b-b/PX*PX+1;
		return new Coord(region, coordX, coordY);
	}

}

class Coord {
	public int region,x,y;
	
	public Coord(int region, int x, int y) {
		this.region = region;
		this.x = x;
		this.y = y;
	}

	@Override
	public String toString() {
		return "Coord [region=" + region + ", x=" + x + ", y=" + y + "]";
	}
}