import java.io.FileWriter;
import java.io.IOException;

import javax.swing.JFileChooser;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.filechooser.FileNameExtensionFilter;

public class Assembler {

	public static void main(String[] args) 
	{
		// If there's a command line argument, assume it's an ASM file and
		// assemble it.
		if (args.length != 0) 
		{
			new Assembler(args[0]);
			return;
		}

		// Otherwise, pop-up a JFileChooser to allow the user to use a GUI
		// to select the ASM file.

		//Schedule a job for the event dispatch thread:
		//creating and showing the assembler's JFileChooser.
		SwingUtilities.invokeLater(new Runnable() 
		{
			public void run() 
			{
				//Turn off metal's use of bold fonts
				UIManager.put("swing.boldMetal", Boolean.FALSE);
				JFileChooser fc = new JFileChooser();
				// Only allow the selection of files with an extension of .asm.
				fc.setFileFilter(new FileNameExtensionFilter("ASM files", "asm"));
				// Uncomment the following to allow the selection of files and
				// directories.  The default behavior is to allow only the selection
				// of files.
				fc.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
				if (fc.showOpenDialog(null) == JFileChooser.APPROVE_OPTION)
					new Assembler(fc.getSelectedFile().getPath());
				else
					System.out.println("No file selected; terminating.");
			}
		});
	}

	private Assembler(String asmFile) 
	{
		// ASM file name without the .asm extension.
		String basename = new String(asmFile.substring(0, asmFile.indexOf('.')));
		// Un-comment the following once you have a SymbolTable class.
		SymbolTable symbolTable = new SymbolTable();	
		Parser parser = new Parser(asmFile);
		// Un-comment the following once you have a Code class.
		Code code = new Code();
		// Output file
		FileWriter hackFile;
		// Memory location of next variable
		int nextVariableLoc = 16;
		// Memory location of next assembly command.
		int nextCommandLoc = 0;

		while(parser.hasMoreCommands())
		{
			parser.advance();
			if (parser.commandType() == CommandType.L_COMMAND)
			{
				symbolTable.addEntry(parser.symbol(), nextCommandLoc);
				System.out.println(parser.symbol() + " added to Symbol Table at index " + nextCommandLoc);
			}
			else
			{
				nextCommandLoc++;
			}
			
		}

		parser.reset();

		try 
		{
			hackFile = new FileWriter(basename + ".hack");

			while (parser.hasMoreCommands())
			{
				parser.advance();
				String binary = null;
				if (parser.commandType() == CommandType.A_COMMAND)
				{
					if (symbolTable.contains(parser.symbol()))
					{
						binary = "0" + toImmediate(symbolTable.getAddress(parser.symbol()));
					}
					else if (Character.isDigit(parser.symbol().charAt(0)))
					{
						binary = "0" + toImmediate(Integer.parseInt(parser.symbol()));
					}
					else
					{
						symbolTable.addEntry(parser.symbol(), nextVariableLoc);
						nextVariableLoc++;
						binary = "0" + toImmediate(symbolTable.getAddress(parser.symbol()));
					}						
				}
				else if (parser.commandType() == CommandType.C_COMMAND)
				{
					String c = code.comp(parser.comp());
					String d = code.dest(parser.dest());
					String j = code.jump(parser.jump());
					binary = "111" + c + d + j;
				}
				if (binary != null) {
                    System.out.println(binary);
                    hackFile.write(binary);
                    hackFile.append('\n');
                }
			}

			// Uncomment the following to confirm that the HACK file is being
			// written.
			//hackFile.write("Hello world.\n");

			hackFile.close();
		} 
		catch (IOException e) 
		{
			e.printStackTrace();
			System.exit(1);
		}

	}

	// You might find this useful.
	// Convert the decimal value val to a 15 bit binary string.  Assume that
	// val's value lies between 0 and 32,767, inclusive.  In other words, assume
	// that val is non-negative and will fit into a 15 bit number.
	private String toImmediate(int val) {
		String field = "";

		for (int i = 0; i < 15; i++) {
			field = (val % 2) + field;
			val /= 2;
		}
		return field;
	}
}
