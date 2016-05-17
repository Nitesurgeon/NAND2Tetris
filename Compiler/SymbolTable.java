import java.util.Hashtable;

public class SymbolTable {
	private Hashtable<String, STEntry> classScope;
	private Hashtable<String, STEntry> methodScope;
	private String className;
	private String currentType;
	private String currentKind;
	
	private int staticIndex;
	private int fieldIndex;
	private int argIndex;
	private int varIndex;
	
	public SymbolTable()
	{
		classScope = new Hashtable<String, STEntry>();
		methodScope = new Hashtable<String, STEntry>();
		staticIndex = 0;
		fieldIndex = 0;
		argIndex= 1;
		varIndex = 0;
	}
	
	public void setClassName(String name)
	{
		className = name;
	}
	
	public void setCurrentType(String type)
	{
		currentType = type;
	}
	
	public String getCurrentType()
	{
		return currentType;
	}
	
	public void setCurrentKind(String kind)
	{
		currentKind = kind;
	}
	
	public String getCurrentKind()
	{
		return currentKind;
	}
	
	public void startSubroutine()
	{
		methodScope = new Hashtable<String, STEntry>();
		methodScope.put("this", new STEntry(className, "argument", 0));
		argIndex = 1;
		varIndex = 0;
	}
	
	public void define(String name, String type, String kind)
	{
		if (kind.equalsIgnoreCase("this"))
		{
			classScope.put(name, new STEntry(type, kind, fieldIndex));
			fieldIndex++;
		}
		else if (kind.equalsIgnoreCase("static"))
		{
			classScope.put(name, new STEntry(type, kind, staticIndex));
			staticIndex++;
		}
		else if (kind.equalsIgnoreCase("argument"))
		{
			methodScope.put(name, new STEntry(type, kind, argIndex));
			argIndex++;
		}
		else if (kind.equalsIgnoreCase("local"))
		{
			methodScope.put(name, new STEntry(type, kind, varIndex));
			varIndex++;
		}
	}
	
	public int varCount(String kind)
	{
		if (kind.equalsIgnoreCase("this"))
		{
			return fieldIndex + 1;
		}
		else if (kind.equalsIgnoreCase("static"))
		{
			return staticIndex + 1;
		}
		else if (kind.equalsIgnoreCase("argument"))
		{
			return argIndex + 1;
		}
		else if (kind.equalsIgnoreCase("local"))
		{
			return varIndex + 1;
		}
		else
			return 0;
	}
	
	public String kindOf(String name)
	{
		if (classScope.containsKey(name))
		{
			return classScope.get(name).getKind();
		}
		else if (methodScope.containsKey(name))
		{
			return methodScope.get(name).getKind();
		}
		else
			return null;
	}
	
	public String typeOf(String name)
	{
		if (classScope.containsKey(name))
		{
			return classScope.get(name).getType();
		}
		else if (methodScope.containsKey(name))
		{
			return methodScope.get(name).getType();
		}
		else
			return "NONE";
	}
	
	public int indexOf(String name)
	{
		if (classScope.containsKey(name))
		{
			return classScope.get(name).getIndex();
		}
		else if (methodScope.containsKey(name))
		{
			return methodScope.get(name).getIndex();
		}
		else
			return 0;
	}
	
	public void print(String name)
	{
		if (methodScope.containsKey(name))
		{
			System.out.println(name + "   " + methodScope.get(name).getType() + "   " + methodScope.get(name).getKind() + "   " + methodScope.get(name).getIndex());
		}
		else if (classScope.containsKey(name))
		{
			System.out.println(name + "   " + classScope.get(name).getType() + "   " + classScope.get(name).getKind() + "   " + classScope.get(name).getIndex());
		}
		else
		{
			System.out.println("Variable " + name + " not found in Symbol Table");
		}
	}

}
