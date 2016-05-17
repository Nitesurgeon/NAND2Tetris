import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;

public class CompilationEngine {
	
	private Tokenizer tokenizer;
	private SymbolTable symTable;
	private VMWriter vmwriter;
	private String className;
	private String currentMethod;
	private String lvalue;
	private int functionLocals;
	private int fields;
	private boolean method;
	private boolean array;
	private boolean constructor;
	private String subClassName;
	private int args;
	
	public CompilationEngine(String input, String output)
	{
		tokenizer = new Tokenizer(input);
		symTable = new SymbolTable();
		vmwriter = new VMWriter(output);
		functionLocals = 0;
	}
	
	public void compileClass()
	{
		while (tokenizer.hasMoreTokens())
		{
			tokenizer.advance();
			if (tokenizer.tokenType() == TokenType.KEYWORD && tokenizer.keyWord().equals("class"))
			{
				tokenizer.advance();
			}
			else
			{
				System.out.println("error: class keyword expected");
				return;
			}
			if (tokenizer.tokenType() == TokenType.IDENTIFIER)
			{
				symTable.setClassName(tokenizer.identifier());
				className = tokenizer.identifier();
				tokenizer.advance();
			}
			else
			{
				System.out.println("error: class name expected");
				return;
			}
			if (tokenizer.tokenType() == TokenType.SYMBOL && tokenizer.symbol().equals("{"))
			{
				tokenizer.advance();
			}
			else
			{
				System.out.println("error: { expected at start of class");
				return;
			}
			while (tokenizer.tokenType() == TokenType.KEYWORD && (tokenizer.keyWord().equals("static") || tokenizer.keyWord().equals("field")))
			{
				if (tokenizer.keyWord().equals("static"))
					symTable.setCurrentKind("static");
				else
					symTable.setCurrentKind("this");
				compileVariableDeclaration();
			}
			fields = functionLocals;
			functionLocals = 0;
			while (tokenizer.tokenType() == TokenType.KEYWORD && (tokenizer.keyWord().equals("constructor") || tokenizer.keyWord().equals("function") || tokenizer.keyWord().equals("method")))
			{
				if (tokenizer.keyWord().equals("method"))
				{
					method = true;
				}
				else
					method = false;
				compileSubroutineDeclaration();
			}
			if (tokenizer.tokenType() == TokenType.SYMBOL && tokenizer.symbol().equals("}"))
			{
				tokenizer.advance();
			}
			else
			{
				System.out.println("error: } expected at end of class");
				return;
			}
		}
		System.out.println("Compilation successfull!");
		vmwriter.close();
	}
	
	public void compileVariableDeclaration()
	{
		tokenizer.advance();
		if (tokenizer.tokenType() == TokenType.IDENTIFIER || (tokenizer.tokenType() == TokenType.KEYWORD && (tokenizer.keyWord().equals("int") || tokenizer.keyWord().equals("char") || tokenizer.keyWord().equals("boolean"))))
		{
			symTable.setCurrentType(tokenizer.getToken());
			tokenizer.advance();
		}
		else
		{
			System.out.println("error: field variable type expected");
			return;
		}
		if (tokenizer.tokenType() == TokenType.IDENTIFIER)
		{
			symTable.define(tokenizer.identifier(), symTable.getCurrentType(), symTable.getCurrentKind());
			functionLocals += 1;
			tokenizer.advance();
		}
		else
		{
			System.out.println("error: field variable name expected");
			return;
		}
		while (tokenizer.tokenType() == TokenType.SYMBOL && tokenizer.symbol().equals(","))
		{
			tokenizer.advance();
			if (tokenizer.tokenType() == TokenType.IDENTIFIER)
			{
				symTable.define(tokenizer.identifier(), symTable.getCurrentType(), symTable.getCurrentKind());
				functionLocals += 1;
				tokenizer.advance();
			}
			else
			{
				System.out.println("error: field variable name after ',' expected");
				return;
			}
		}
		if (tokenizer.tokenType() == TokenType.SYMBOL && tokenizer.symbol().equals(";"))
		{
			tokenizer.advance();
		}
		else
		{
			System.out.println("error: ; expected at end of field declarations");
			return;
		}
	}
	
	public void compileSubroutineDeclaration()
	{
		symTable.startSubroutine();
		tokenizer.advance();
		if (tokenizer.tokenType() == TokenType.IDENTIFIER || (tokenizer.tokenType() == TokenType.KEYWORD && (tokenizer.keyWord().equals("void") || tokenizer.keyWord().equals("int") || tokenizer.keyWord().equals("char") || tokenizer.keyWord().equals("boolean"))))
		{
			tokenizer.advance();
		}
		else
		{
			System.out.println("error: subroutine type expected");
			return;
		}
		if (tokenizer.tokenType() == TokenType.IDENTIFIER)
		{
			if (tokenizer.identifier().equals("new"))
			{
				constructor = true;
			}
			currentMethod = tokenizer.identifier();
			tokenizer.advance();
		}
		else
		{
			System.out.println("error: subroutine name expected");
			return;
		}
		if (tokenizer.tokenType() == TokenType.SYMBOL && tokenizer.symbol().equals("("))
		{
			tokenizer.advance();
		}
		else
		{
			System.out.println("error: ( expected for subroutine parameters");
			return;
		}
		symTable.setCurrentKind("argument");
		compileParameterList();
		if (tokenizer.tokenType() == TokenType.SYMBOL && tokenizer.symbol().equals(")"))
		{
			tokenizer.advance();
		}
		else
		{
			System.out.println("error: ) expected for subroutine parameters");
			return;
		}
		if (tokenizer.tokenType() == TokenType.SYMBOL && tokenizer.symbol().equals("{"))
		{
			tokenizer.advance();
		}
		else
		{
			System.out.println("error: { expected for subroutine body");
			return;
		}
		functionLocals = 0;
		while (tokenizer.tokenType() == TokenType.KEYWORD && tokenizer.keyWord().equals("var"))
		{
			symTable.setCurrentKind("local");
			compileVariableDeclaration();
		}
		vmwriter.writeFunction(className + "." + currentMethod, functionLocals);
		if (constructor)
		{
			vmwriter.writePush("constant", fields);
			vmwriter.writeCall("Memory.alloc" , 1);
			vmwriter.writePop("pointer", 0);
		}
		else if (method)
		{
			vmwriter.writePush("argument", 0);
			vmwriter.writePop("pointer", 0);
		}
		while (tokenizer.tokenType() == TokenType.KEYWORD && (tokenizer.keyWord().equals("let") || tokenizer.keyWord().equals("if") || tokenizer.keyWord().equals("while") || tokenizer.keyWord().equals("do") || tokenizer.keyWord().equals("return")))
		{
			compileStatements();
		}
		if (tokenizer.tokenType() == TokenType.SYMBOL && tokenizer.symbol().equals("}"))
		{
			tokenizer.advance();
		}
		else
		{
			System.out.println("error: } expected at end of subroutine");
			return;
		}
	}
	
	public void compileParameterList()
	{
		if (tokenizer.tokenType() == TokenType.SYMBOL && tokenizer.symbol().equals(")"))
			return;
		else if (tokenizer.tokenType() == TokenType.IDENTIFIER || (tokenizer.tokenType() == TokenType.KEYWORD && (tokenizer.keyWord().equals("int") || tokenizer.keyWord().equals("char") || tokenizer.keyWord().equals("boolean"))))
		{
			symTable.setCurrentType(tokenizer.getToken());
			tokenizer.advance();
			if (tokenizer.tokenType() == TokenType.IDENTIFIER)
			{
				symTable.define(tokenizer.identifier(), symTable.getCurrentType(), symTable.getCurrentKind());
				tokenizer.advance();
			}
			else
			{
				System.out.println("error: parameter name expected");
				return;
			}
			while (tokenizer.tokenType() == TokenType.SYMBOL && tokenizer.symbol().equals(","))
			{
				tokenizer.advance();
				if (tokenizer.tokenType() == TokenType.IDENTIFIER || (tokenizer.tokenType() == TokenType.KEYWORD && (tokenizer.keyWord().equals("int") || tokenizer.keyWord().equals("char") || tokenizer.keyWord().equals("boolean"))))
				{
					symTable.setCurrentType(tokenizer.getToken());
					tokenizer.advance();
					if (tokenizer.tokenType() == TokenType.IDENTIFIER)
					{
						symTable.define(tokenizer.identifier(), symTable.getCurrentType(), symTable.getCurrentKind());
						tokenizer.advance();
					}
					else
					{
						System.out.println("error: parameter name expected");
						return;
					}
				}
				else
				{
					System.out.println("error: parameter type expected");
					return;
				}
			}
		}
	}
	
	public void compileStatements()
	{
		if (tokenizer.tokenType() == TokenType.KEYWORD && tokenizer.keyWord().equals("let"))
		{
			compileLetStatement();
		}
		if (tokenizer.tokenType() == TokenType.KEYWORD && tokenizer.keyWord().equals("if"))
		{
			compileIfStatement();
		}
		if (tokenizer.tokenType() == TokenType.KEYWORD && tokenizer.keyWord().equals("while"))
		{
			compileWhileStatement();
		}
		if (tokenizer.tokenType() == TokenType.KEYWORD && tokenizer.keyWord().equals("do"))
		{
			compileDoStatement();
		}
		if (tokenizer.tokenType() == TokenType.KEYWORD && tokenizer.keyWord().equals("return"))
		{
			compileReturnStatement();
		}
	}
	
	public void compileLetStatement()
	{
		tokenizer.advance();
		String bracket = tokenizer.identifier();
		lvalue = tokenizer.identifier();
		if (tokenizer.tokenType() == TokenType.IDENTIFIER)
		{
			tokenizer.advance();
		}
		else
		{
			System.out.println("error: variable name expected");
			return;
		}
		if (tokenizer.tokenType() == TokenType.SYMBOL && tokenizer.symbol().equals("["))
		{
			tokenizer.advance();
			compileExpression();
			if (tokenizer.tokenType() == TokenType.SYMBOL && tokenizer.symbol().equals("]"))
			{
				tokenizer.advance();
				vmwriter.writePush(symTable.kindOf(lvalue), symTable.indexOf(lvalue));
				vmwriter.writeArithmetic("add");
				vmwriter.writePop("pointer", 1);
			}
			else
			{
				System.out.println("error: ] expected for expression");
			}
		}
		else
		{
			symTable.print(bracket);
		}
		if (tokenizer.tokenType() == TokenType.SYMBOL && tokenizer.symbol().equals("="))
		{
			tokenizer.advance();
		}
		else
		{
			System.out.println("error: = expected for let statement");
			return;
		}
		compileExpression();
		if (array)
			vmwriter.writePop("that", 0);
		else
			vmwriter.writePop(symTable.kindOf(lvalue), symTable.indexOf(lvalue));
		if (tokenizer.tokenType() == TokenType.SYMBOL && tokenizer.symbol().equals(";"))
		{
			tokenizer.advance();
		}
		else
		{
			System.out.println("error: ; expected at end of let statement");
			return;
		}
	}
	
	public void compileIfStatement()
	{
		tokenizer.advance();
		if (tokenizer.tokenType() == TokenType.SYMBOL && tokenizer.symbol().equals("("))
		{
			tokenizer.advance();
		}
		else
		{
			System.out.println("error: ( expected for if statement");
			return;
		}
		compileExpression();
		if (tokenizer.tokenType() == TokenType.SYMBOL && tokenizer.symbol().equals(")"))
		{
			tokenizer.advance();
		}
		else
		{
			System.out.println("error: ) expected for if statement");
			return;
		}
		if (tokenizer.tokenType() == TokenType.SYMBOL && tokenizer.symbol().equals("{"))
		{
			tokenizer.advance();
		}
		else
		{
			System.out.println("error: { expected for if statement body");
			return;
		}
		while (tokenizer.tokenType() == TokenType.KEYWORD && (tokenizer.keyWord().equals("let") || tokenizer.keyWord().equals("if") || tokenizer.keyWord().equals("while") || tokenizer.keyWord().equals("do") || tokenizer.keyWord().equals("return")))
		{
			compileStatements();
		}
		if (tokenizer.tokenType() == TokenType.SYMBOL && tokenizer.symbol().equals("}"))
		{
			tokenizer.advance();
		}
		else
		{
			System.out.println("error: } expected for if statement body");
			return;
		}
		if (tokenizer.tokenType() == TokenType.KEYWORD && tokenizer.keyWord().equals("else"))
		{
			tokenizer.advance();
			if (tokenizer.tokenType() == TokenType.SYMBOL && tokenizer.symbol().equals("{"))
			{
				tokenizer.advance();
			}
			else
			{
				System.out.println("error: { expected for else body");
				return;
			}
			while (tokenizer.tokenType() == TokenType.KEYWORD && (tokenizer.keyWord().equals("let") || tokenizer.keyWord().equals("if") || tokenizer.keyWord().equals("while") || tokenizer.keyWord().equals("do") || tokenizer.keyWord().equals("return")))
			{
				compileStatements();
			}
			if (tokenizer.tokenType() == TokenType.SYMBOL && tokenizer.symbol().equals("}"))
			{
				tokenizer.advance();
			}
			else
			{
				System.out.println("error: } expected for else body");
				return;
			}
		}
	}
	
	public void compileWhileStatement()
	{
		tokenizer.advance();
		if (tokenizer.tokenType() == TokenType.SYMBOL && tokenizer.symbol().equals("("))
		{
			tokenizer.advance();
		}
		else
		{
			System.out.println("error: ( expected for while statement");
			return;
		}
		compileExpression();
		if (tokenizer.tokenType() == TokenType.SYMBOL && tokenizer.symbol().equals(")"))
		{
			tokenizer.advance();
		}
		else
		{
			System.out.println("error: ) expected for while statement");
			return;
		}
		if (tokenizer.tokenType() == TokenType.SYMBOL && tokenizer.symbol().equals("{"))
		{
			tokenizer.advance();
		}
		else
		{
			System.out.println("error: { expected for while body");
			return;
		}
		while (tokenizer.tokenType() == TokenType.KEYWORD && (tokenizer.keyWord().equals("let") || tokenizer.keyWord().equals("if") || tokenizer.keyWord().equals("while") || tokenizer.keyWord().equals("do") || tokenizer.keyWord().equals("return")))
		{
			compileStatements();
		}
		if (tokenizer.tokenType() == TokenType.SYMBOL && tokenizer.symbol().equals("}"))
		{
			tokenizer.advance();
		}
		else
		{
			System.out.println("error: } expected for while body");
			return;
		}
	}
	
	public void compileDoStatement()
	{
		tokenizer.advance();
		if (tokenizer.tokenType() == TokenType.IDENTIFIER)
		{
			tokenizer.advance();
			compileSubroutineCall();
		}
		else
		{
			System.out.println("error: Subroutine name expected for do statement");
			return;
		}
		if (tokenizer.tokenType() == TokenType.SYMBOL && tokenizer.symbol().equals(";"))
		{
			tokenizer.advance();
			vmwriter.writePop("temp", 0);
		}
		else
		{
			System.out.println("error: ; expected at end of do statement");
			return;
		}
	}
	
	public void compileReturnStatement()
	{
		tokenizer.advance();
		compileExpression();
		if (tokenizer.tokenType() == TokenType.SYMBOL && tokenizer.symbol().equals(";"))
		{
			tokenizer.advance();
		}
		else
		{
			System.out.println("error: ; expected at end of return statement");
			return;
		}
		vmwriter.writeReturn();
	}
	
	public void compileExpression()
	{
		compileTerm();
		while (tokenizer.tokenType() == TokenType.SYMBOL && (tokenizer.symbol().equals("+") || tokenizer.symbol().equals("-") || tokenizer.symbol().equals("*") || tokenizer.symbol().equals("/") || tokenizer.symbol().equals("&") || tokenizer.symbol().equals("|") || tokenizer.symbol().equals("<") || tokenizer.symbol().equals(">") || tokenizer.symbol().equals("=")))
		{
			String operator = tokenizer.symbol();
			tokenizer.advance();
			compileTerm();
			if (operator.equals("+"))
				vmwriter.writeArithmetic("add");
			else if (operator.equals("-"))
				vmwriter.writeArithmetic("sub");
			else if (operator.equals("*"))
				vmwriter.writeCall("Math.multiply", 2);
			else if (operator.equals("/"))
				vmwriter.writeCall("Math.divide", 2);
			else if (operator.equals("&"))
				vmwriter.writeArithmetic("and");
			else if (operator.equals("|"))
				vmwriter.writeArithmetic("or");
			else if (operator.equals("<"))
				vmwriter.writeArithmetic("lt");
			else if (operator.equals(">"))
				vmwriter.writeArithmetic("gt");
			else if (operator.equals("="))
				vmwriter.writeArithmetic("eq");
		}		
	}
	
	public void compileTerm()
	{
		if (tokenizer.tokenType() == TokenType.INT_CONST)
		{
			vmwriter.writePush("constant", tokenizer.intVal());
			tokenizer.advance();
		}
		else if (tokenizer.tokenType() == TokenType.STRING_CONST)
		{
			vmwriter.writePush("constant", tokenizer.stringVal().length());
			vmwriter.writeCall("String.new", 1);
			for (int i = 0; i < tokenizer.stringVal().length(); i++)
			{
				vmwriter.writePush("constant", (int)tokenizer.stringVal().charAt(i));
				vmwriter.writeCall("String.appendChar", 2);
			}
			tokenizer.advance();
		}
		else if (tokenizer.tokenType() == TokenType.KEYWORD && (tokenizer.keyWord().equals("true") || tokenizer.keyWord().equals("false") || tokenizer.keyWord().equals("null") || tokenizer.keyWord().equals("this")))
		{
			if (tokenizer.keyWord().equals("true"))
			{
				vmwriter.writePush("constant", 0);
				vmwriter.writeArithmetic("not");
			}
			else if (tokenizer.keyWord().equals("false") || tokenizer.keyWord().equals("null"))
			{
				vmwriter.writePush("constant", 0);
			}
			else if (tokenizer.keyWord().equals("this"))
			{
				vmwriter.writePush("pointer", 0);
			}
			tokenizer.advance();
		}
		else if (tokenizer.tokenType() == TokenType.IDENTIFIER)
		{
			String identifier = tokenizer.identifier();
			tokenizer.advance();
			if (tokenizer.tokenType() == TokenType.SYMBOL && tokenizer.symbol().equals("["))
			{
				tokenizer.advance();
				compileExpression();
				vmwriter.writePush(symTable.kindOf(identifier), symTable.indexOf(identifier));
				vmwriter.writeArithmetic("add");
				vmwriter.writePop("pointer", 1);
				if (tokenizer.tokenType() == TokenType.SYMBOL && tokenizer.symbol().equals("]"))
				{
					tokenizer.advance();
				}
				else
				{
					System.out.println("error: ] expected for end of term expression");
					return;
				}
			}
			else if (tokenizer.tokenType() == TokenType.SYMBOL && (tokenizer.symbol().equals("(") || tokenizer.symbol().equals(".")))
			{
				subClassName = identifier;
				compileSubroutineCall();
			}
			else
			{
				vmwriter.writePush(symTable.kindOf(identifier), symTable.indexOf(identifier));
				symTable.print(identifier);
			}
		}
		else if (tokenizer.tokenType() == TokenType.SYMBOL && tokenizer.symbol().equals("("))
		{
			tokenizer.advance();
			compileExpression();
			if (tokenizer.tokenType() == TokenType.SYMBOL && tokenizer.symbol().equals(")"))
			{
				tokenizer.advance();
			}
			else
			{
				System.out.println("error: ) expected for end of term expression");
				return;
			}
		}
		else if (tokenizer.tokenType() == TokenType.SYMBOL && (tokenizer.symbol().equals("-") || tokenizer.symbol().equals("~")))
		{
			String neg = tokenizer.symbol();
			tokenizer.advance();
			compileTerm();
			if (neg.equals("-"))
				vmwriter.writeArithmetic("neg");
			else if (neg.equals("~"))
				vmwriter.writeArithmetic("not");
		}
	}
	
	public void compileSubroutineCall()
	{
		if (tokenizer.tokenType() == TokenType.SYMBOL && tokenizer.symbol().equals("."))
		{
			tokenizer.advance();
			if (tokenizer.tokenType() == TokenType.IDENTIFIER)
			{
				String subCallName = tokenizer.identifier();
				tokenizer.advance();
				if (tokenizer.tokenType() == TokenType.SYMBOL && tokenizer.symbol().equals("("))
				{
					tokenizer.advance();
					args = 0;
					compileExpressionList();
					if (tokenizer.tokenType() == TokenType.SYMBOL && tokenizer.symbol().equals(")"))
					{
						vmwriter.writeCall(subClassName + "." + subCallName, args);
						tokenizer.advance();
					}
					else
					{
						System.out.println("error: ) expected at end of subroutine call");
						return;
					}
				}
			}
		}
	}
	
	public void compileExpressionList()
	{
		if (tokenizer.tokenType() == TokenType.SYMBOL && tokenizer.symbol().equals(")"))
		{
			return;
		}
		else
		{
			compileExpression();
			args += 1;
			while (tokenizer.tokenType() == TokenType.SYMBOL && tokenizer.symbol().equals(","))
			{
				tokenizer.advance();
				compileExpression();
				args += 1;
			}
		}
	}
	
	/*
	 * function classname.subname n
	 * if keyword is constructor
	 * push m # fields
	 * call memory.alloc 1
	 * pop pointer 0
	 * 
	 * if keyword is method
	 * ...
	 */
	

}
