
public class STEntry {
	
	private String type;
	private String kind;
	private int index;
	
	public STEntry(String t, String k, int i)
	{
		type = t;
		kind = k;
		index = i;
	}
	
	public String getType()
	{
		return type;
	}
	
	public String getKind()
	{
		return kind;
	}
	
	public int getIndex()
	{
		return index;
	}

}
