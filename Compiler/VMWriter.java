import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;

public class VMWriter {
	
	private BufferedWriter writer;
	
	public VMWriter(String output)
	{
		try 
		{
			writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(output)));
		} 
		catch (FileNotFoundException e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void writePush(String seg, int index)
	{
		try 
		{
			writer.write("push " + seg + " " + index + "\n");
		} 
		catch (IOException e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void writePop(String seg, int index)
	{
		try 
		{
			writer.write("pop " + seg + " " + index + "\n");
		} 
		catch (IOException e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void writeArithmetic(String com)
	{
		try 
		{
			writer.write(com + "\n");
		} 
		catch (IOException e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void writeLabel(String label)
	{
		try 
		{
			writer.write("label " + label + "\n");
		} 
		catch (IOException e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void writeGoTo(String label)
	{
		try 
		{
			writer.write("goto " + label + "\n");
		} 
		catch (IOException e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void writeIf(String label)
	{
		try 
		{
			writer.write("if-goto " + label + "\n");
		} 
		catch (IOException e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void writeCall(String name, int nArgs)
	{
		try 
		{
			writer.write("call " + name + " " + nArgs + "\n");
		} 
		catch (IOException e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void writeFunction(String name, int nLocals)
	{
		try 
		{
			writer.write("function " + name + " " + nLocals + "\n");
		} 
		catch (IOException e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void writeReturn()
	{
		try 
		{
			writer.write("return" + "\n");
		} 
		catch (IOException e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void close()
	{
		try 
		{
			writer.close();
		} 
		catch (IOException e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
