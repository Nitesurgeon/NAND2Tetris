import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Parser {

    // Index of the current assembly command in the commands ArrayList.  It is
    // initialized via reset() to -1 to match the Parser's description in the
    // textbook.  Namely, that advance() be called on the Parser _before_ performing
    // any actual parsing.
    private int index = 0;
    private ArrayList<String> commands;
    private String currentCommand;

    public Parser(String asmFile) {
	commands = new ArrayList<String>();
	reset();
	fill(asmFile);
    }

    // Reset the parser.  Used prior to the second pass.    
    public void reset() {
	index = -1;
    }

    // Fill the commands list with assembly commands.    
    private void fill(String src) {
	Scanner scanner;
	String line;
	int index;

	try {
	    scanner = new Scanner(new File(src));

	    while (scanner.hasNextLine()) {
		line = scanner.nextLine();
		// Remove all whitespace characters
		line = line.replaceAll("\\s", "");
		// Remove comments
		index = line.indexOf("//");
		if (index != -1)
		    line = line.substring(0, index);
		// At this point, the line might be empty.
		// If it isn't empty, add it to the commands list.
		if (line.length() > 0) {
		    commands.add(line);
		    // Uncomment the following Java statement to see the
		    // assembly lines being stored in the commands ArrayList.
		    System.out.println("Fill: " + line);
		}
	    }
	} catch (FileNotFoundException e) {
	    e.printStackTrace();
	    System.exit(1);
	}

    }
    
    public String getCommand()
    {
    	return currentCommand;
    }
    
    public void advance()
    {
    	if (hasMoreCommands())
    	{
    		index++;
    		currentCommand = commands.get(index);
    	}
    	else System.out.println("End of line");
    }
    
    public boolean hasMoreCommands()
    {
    	if (index < commands.size()-1) return true;
    	else return false;
    }
    
    public CommandType commandType()
    {
    	if (currentCommand.startsWith("@"))
    		return CommandType.A_COMMAND;
    	else if(currentCommand.startsWith("("))
    		return CommandType.L_COMMAND;
    	else
    		return CommandType.C_COMMAND;
    }
    
    public String symbol()
    {
    	int lastIndex = currentCommand.length()-1;
    	if(commandType() == CommandType.A_COMMAND)
    	{
    		return currentCommand.substring(1);
    	}
    	else if (commandType() == CommandType.L_COMMAND)
    	{
    		return currentCommand.substring(1,lastIndex);
    	}
    	else
    	{
    		System.out.println("Command type is not A or L");
    		return "";
    	}
    }
    
    public String dest()
    {
    	if (commandType() == CommandType.C_COMMAND)
    	{
    		if (currentCommand.contains("="))
    		{
    			int i = currentCommand.indexOf("=");
				return currentCommand.substring(0, i);
    		}
    		else
        	{
        		System.out.println("Command does not contain a DEST field");
        		return "";
        	}
    	}
    	else 
    	{
    		System.out.println("Command Type is not C");
    		return "";
    	}
    }
    
    public String comp()
    {
    	if (commandType() == CommandType.C_COMMAND)
    	{
    		if (currentCommand.contains("=") && currentCommand.contains(";"))
    		{
    			int semi = currentCommand.indexOf(";");
    			int eq = currentCommand.indexOf("=");
    			return currentCommand.substring(eq, semi);
    		}
    		else if (currentCommand.contains(";"))
        	{
    			int semi = currentCommand.indexOf(";");
    			return currentCommand.substring(0, semi);
        	}
    		else if (currentCommand.contains("="))
    		{
    			int i = currentCommand.indexOf("=");
				return currentCommand.substring(i+1);
    		}
    		else
    		{
    			return currentCommand;
    		}
    	}
    	else 
    	{
    		System.out.println("Command Type is not C");
    		return "";
    	}
    }
    
    public String jump()
    {
    	if (commandType() == CommandType.C_COMMAND)
    	{
    		if (currentCommand.contains(";"))
    		{
    			int i = currentCommand.indexOf(";");
    			return currentCommand.substring(i+1);
    		}
    		else
        	{
        		System.out.println("Command does not contain a JMP field");
        		return "";
        	}
    	}
    	else
    	{
    		System.out.println("Command Type is not C");
    		return "";
    	}
    }
}
