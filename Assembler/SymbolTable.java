import java.util.Hashtable;


public class SymbolTable {
	
	private Hashtable<String, Integer> symbolTable = new Hashtable<String, Integer>();
	
	public SymbolTable()
	{
		symbolTable.put("SP", 0);
		symbolTable.put("LCL", 1);
		symbolTable.put("ARG", 2);
		symbolTable.put("THIS", 3);
		symbolTable.put("THAT", 4);
		for (int i = 0; i<16; i++)
		{
            String r = "R" + String.valueOf(i);
            symbolTable.put(r,i);
        }
		symbolTable.put("SCREEN", 16384);
		symbolTable.put("KBD", 24576);
	}
	
	public void addEntry(String sym, Integer addr)
	{
        symbolTable.put(sym,addr);
    }

    public Boolean contains(String sym)
    {
        return symbolTable.containsKey(sym);
    }

    public int getAddress(String sym)
    {
        return symbolTable.get(sym);
    }

}
