package toy.util;

public class FontPos {
	public int region,x,y;
	
	public FontPos(int region, int x, int y) {
		this.region = region;
		this.x = x;
		this.y = y;
	}
	
	public FontXy toFontXy() {
		FontXy b = new FontXy();
		b.x1 = (x-1)*12+(4-region)*(12*21+4);
		b.y1 = (y-1)*12;
		b.x2 = b.x1+11;
		b.y2 = b.y1+11;
		return b;
	}

	@Override
	public String toString() {
		return "Coord [region=" + region + ", x=" + x + ", y=" + y + "]";
	}
}