
public enum KeyWord {
	CLASS, METHOD, FUNCTION, CONSTRUCTOR, INT, BOOLEAN, CHAR, VOID, VAR, STATIC, FIELD, LET, DO, IF, ELSE, WHILE, RETURN, TRUE, FALSE, NULL, THIS;
	
	public KeyWord toEnum(String t)
	{
		switch(t)
		{
		case "class":
			return KeyWord.CLASS;
		case "method":
			return KeyWord.METHOD;
		case "function":
			return KeyWord.FUNCTION;
		case "constructor":
			return KeyWord.CONSTRUCTOR;
		case "int":
			return KeyWord.INT;
		case "boolean":
			return KeyWord.BOOLEAN;
		case "char":
			return KeyWord.CHAR;
		case "void":
			return KeyWord.VOID;
		case "var":
			return KeyWord.VAR;
		case "static":
			return KeyWord.STATIC;
		case "field":
			return KeyWord.FIELD;
		case "let":
			return KeyWord.LET;
		case "do":
			return KeyWord.DO;
		case "if":
			return KeyWord.IF;
		case "else":
			return KeyWord.ELSE;
		case "while":
			return KeyWord.WHILE;
		case "return":
			return KeyWord.RETURN;
		case "true":
			return KeyWord.TRUE;
		case "false":
			return KeyWord.FALSE;
		case "null":
			return KeyWord.NULL;
		case "this":
			return KeyWord.THIS;
		}
		return null;
	}

}
