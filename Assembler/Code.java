import java.util.Hashtable;


public class Code {
	
	Hashtable<String, String> compTable = new Hashtable<String, String>();
	
	public Code()
	{
		compTable.put("0", "0101010");
        compTable.put("1", "0111111");
        compTable.put("-1", "0111010");
        compTable.put("D", "0001100");
        compTable.put("A", "0110000");
        compTable.put("!D", "0001101");
        compTable.put("!A", "0110001");
        compTable.put("-D", "0001111");
        compTable.put("-A", "0110011");
        compTable.put("D+1", "0011111");
        compTable.put("A+1", "0110111");
        compTable.put("D-1", "0001110");
        compTable.put("A-1", "0110010");
        compTable.put("D+A", "0000010");
        compTable.put("D-A", "0010011");
        compTable.put("A-D", "0000111");
        compTable.put("D&A", "0000000");
        compTable.put("D|A", "0010101");
        compTable.put("M", "1110000");
        compTable.put("!M", "1110001");
        compTable.put("-M", "1110011");
        compTable.put("M+1", "1110111");
        compTable.put("M-1", "1110010");
        compTable.put("D+M", "1000010");
        compTable.put("D-M", "1010011");
        compTable.put("M-D", "1000111");
        compTable.put("D&M", "1000000");
        compTable.put("D|M", "1010101");
	}
	
	public String dest(String input)
	{
		String d1 = "0";
		String d2 = "0";
		String d3 = "0";
		if (input.contains("D"))
			d2 = "1";
		if (input.contains("M"))
			d3 = "1";
		if (input.contains("A"))
			d1 = "1";
		return d1 + d2 + d3;
	}
	
	public String comp(String input)
	{
		return compTable.get(input);
	}
	
	public String jump(String input)
	{
		String j1 = "0";
		String j2 = "0";
		String j3 = "0";
		if (input.contains("JGT"))
			j3 = "1";
		if (input.contains("JEQ"))
			j2 = "1";
		if (input.contains("JGE"))
		{
			j2 = "1";
			j3 = "1";
		}
		if (input.contains("JLT"))
			j1 = "1";
		if (input.contains("JNE"))
		{
			j1 = "1";
			j3 = "1";
		}
		if (input.contains("JLE"))
		{
			j1 = "1";
			j2 = "1";
		}
		if (input.contains("JMP"))
		{
			j1 = "1";
			j2 = "1";
			j3 = "1";
		}
		return j1 + j2 + j3;
	}

}
