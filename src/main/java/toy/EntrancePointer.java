package toy;

import java.nio.ByteBuffer;

import toy.util.Util;

public class EntrancePointer {
	public int addr, size, unknown;

	public byte[] tobytes() {
		ByteBuffer buf = ByteBuffer.allocate(12);
		buf.putInt(Util.hilo(addr));
		buf.putInt(Util.hilo(size));
		buf.putInt(unknown);
		return buf.array();
	}
}