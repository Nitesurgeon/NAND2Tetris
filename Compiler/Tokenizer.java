import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.io.*;

public class Tokenizer {
	
	private PushbackReader reader;
	private char character;
	private int test;
	private String token;
	private TokenType tokenType;
	private List symbols = Arrays.asList('{', '}', '(', ')', '[', ']', '.', ',', ';', '+', '-', '*', '&', '|', '<', '>', '=', '~');
	private List<String> keywords = Arrays.asList("class", "constructor", "function", "method", "field", "static", "var", "int", "char", "boolean", "void", "true", "false", "null", "this", "let", "do", "if", "else", "while", "return");
	
	public Tokenizer (String file)
	{
		try 
		{
			reader = new PushbackReader(new InputStreamReader(new FileInputStream(file)));
		} 
		catch (FileNotFoundException e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public boolean hasMoreTokens()
	{
		try 
		{
			test = reader.read();
		} 
		catch (IOException e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (test == -1)
		{
			return false;
		}
		else
		{
			try 
			{
				reader.unread(test);
			} 
			catch (IOException e) 
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return true;
		}
	}
	
	public void advance()
	{
		String state = "start";
		token = "";
		tokenType = null;
		while (state != "finish")
		{
			if (!hasMoreTokens())
			{
				state = "finish";
			}
			else
			{
				try 
				{
					character = (char) reader.read();
				} 
				catch (IOException e) 
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
			switch (state)
			{
			case "start":
				if (Character.isLetter(character))
				{
					state = "identifier";
					tokenType = TokenType.IDENTIFIER;
					token += character;
				}
				else if (Character.isDigit(character))
				{
					state = "number";
					tokenType = TokenType.INT_CONST;
					token += character;
				}
				else if (character == '"')
				{
					state = "string";
					tokenType = TokenType.STRING_CONST;
				}
				else if (isSymbol(character))
				{
					state = "finish";
					tokenType = TokenType.SYMBOL;
					token += character;
				}
				else if (character == '/')
				{
					state = "comment?";
					tokenType = TokenType.COMMENT;
				}
				break;
			case "identifier":
				if (Character.isLetter(character) || character == '_')
					token += character;
				else
				{
					try 
					{
						reader.unread(character);
					} 
					catch (IOException e) 
					{
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					state = "finish";
					if (isKeyword(token))
						tokenType = TokenType.KEYWORD;
				}
				break;
			case "number":
				if (Character.isDigit(character))
					token += character;
				else
				{
					try 
					{
						reader.unread(character);
					} 
					catch (IOException e) 
					{
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					state = "finish";
				}
				break;
			case "string":
				if (character != '"')
					token += character;
				else
				{
					state = "finish";
				}
				break;
			case "comment?":
				if (character == '/')
				{
					state = "line comment";
				}
				else if (character == '*')
				{
					state = "block comment";
				}
				else
				{
					try 
					{
						reader.unread(character);
					} 
					catch (IOException e) 
					{
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					token = "/";
					tokenType = TokenType.SYMBOL;
					state = "finish";
				}
				break;
			case "line comment":
				if (character == '\n')
					state = "start";
				else
					break;
			case "block comment":
				if (character == '*')
				{
					state = "block over?";
				}
				else
					break;
				break;
			case "block over?":
				if (character == '/')
				{
					state = "start";
				}
				else
				{
					state = "block comment";
					break;
				}
			}
		}
	}
	
	public String getToken()
	{
		return token;
	}
	
	public TokenType tokenType()
	{
		if (tokenType == TokenType.IDENTIFIER)
		{
			if (isKeyword(token))
			{
				tokenType = TokenType.KEYWORD;
				return tokenType;
			}
			else
				return tokenType;
		}
		else
			return tokenType;
	}
	
	public String keyWord()
	{
		return token;
	}
	
	public String symbol()
	{
		return token;		
	}
	
	public String identifier()
	{
		return token;		
	}
	
	public int intVal()
	{
		return Integer.parseInt(token);
	}
	
	public String stringVal()
	{
		return token;		
	}
	
	public boolean isSymbol(char c)
	{
		if (symbols.contains(c))
		{
			return true;
		}
		else
			return false;
	}
	
	public boolean isSymbol(String t)
	{
		if (symbols.contains(t))
		{
			return true;
		}
		else
			return false;
	}
	
	public boolean isKeyword(String t)
	{
		if (keywords.contains(t))
		{
			return true;
		}
		else
			return false;
	}
	
	public boolean isNumber(String t)
	{
		for (int i = 0; i < t.length(); i++)
		{
			if (!(Character.isDigit(t.charAt(i))))
			{
				return false;
			}
		}
		return true;
	}
	
	public boolean isWord(String t)
	{
		for (int i = 0; i < t.length(); i++)
		{
			if (!(Character.isLetter(t.charAt(i)) && (t.charAt(i) != '_')))
			{
				return false;
			}
		}
		return true;
	}
}
