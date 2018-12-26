package toy;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

public class Resource {
	
	Map<Integer,String> address_name = new HashMap<>();
//	Map<String,Integer> name_address = new HashMap<>();
	List<MyEntry> list = new LinkedList<>();
	
	public Resource() {
		BufferedReader reader = null;
		InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream("resource.prop");
		try {
			reader  = new BufferedReader(new InputStreamReader(is, "gbk"));
			String l = null;
			while((l=reader.readLine())!=null){
				if(!l.startsWith("#") && l.length()>0) {
					String[] arr = l.split("=",2);
					int address = Integer.parseInt(arr[0].replace("0x", "").replace("0X", ""), 16);
					String name = null;
					if(arr.length>1) name= arr[1];
					list.add(new MyEntry(address, name));
					address_name.put(address, name);
	//				name_address.put(name, address);
				}
			}
		} catch (IOException e) {
			throw new RuntimeException();
		} finally{
			try {
				reader.close();
			} catch (IOException e) {
			}
		}
	}
	
	public boolean containAddress(int address) {
		return address_name.containsKey(address);
	}
	
	public String getName(int address) {
		return address_name.get(address);
	}
	
	public Set<Entry<Integer,String>> entrySet_address_name() {
		return address_name.entrySet();
	}
	
	public int getLengthByAddr(String addr){
		int addrInt=Integer.parseInt(addr,16);
		int index = list.indexOf(new MyEntry(addrInt,""));
		if(index==-1)
			throw new RuntimeException(addr+" not found");
		if(list.size()>index+1) {
			return list.get(index+1).address-addrInt;
		} else {
			return Conf.BIN_LEN-addrInt;	//FILELINK.BIN的长度
		}
	}
	
	public int getLengthByName(String name) {
		int startAddress = getUniqueAddress(name);
		Integer index=null;
		for(int i=0;i<list.size();i++) {
			MyEntry e = list.get(i);
			if(name.equals(e.name)) {
				index=i;
				break;
			}
		}
		if(index==null)
			throw new RuntimeException(name+" not found");
		if(list.size()>index+1) {
			return list.get(index+1).address-startAddress;
		} else {
			return Conf.BIN_LEN-startAddress;	//FILELINK.BIN的长度
		}
	}
	
	public int getUniqueAddress(String name) {
		Integer key = null;
		for(Entry<Integer,String> e : address_name.entrySet()) {
			if(name.equals(e.getValue())) {
				if(key!=null) {
					throw new RuntimeException(name+" is not unique result");
				}
				key = e.getKey();
			}
		}
		if(key==null)
			throw new RuntimeException(name+" not found");
		return key;
	}
	
	class MyEntry {
		public int address;
		public String name;
		public MyEntry(int address, String name) {
			this.address = address;
			this.name = name;
		}
		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + getOuterType().hashCode();
			result = prime * result + address;
			return result;
		}
		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			MyEntry other = (MyEntry) obj;
			if (!getOuterType().equals(other.getOuterType()))
				return false;
			if (address != other.address)
				return false;
			return true;
		}
		private Resource getOuterType() {
			return Resource.this;
		}
	}
	
}
