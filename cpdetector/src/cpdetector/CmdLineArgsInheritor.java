/*
 * Created on 11.04.2004
 *	
 */
package cpdetector;

import jargs.gnu.CmdLineParser;

import java.util.HashMap;
import java.util.Map;


/**
 * @author <a href="mailto:awester@de.ibm.com">Achim Westermann</a>
 *
 */
public abstract class CmdLineArgsInheritor
{

	private CmdLineParser cmdLineParser;

	/**
	 * <p>
	 * All {@link jargs.gnu.CommandLineParser.Option}s.
	 * </p>
	 * <p>
	 * Subclasses may specify more options in their constructor by invoking the protected 
	 * method #addOption(String,CmdLineParser.Option).
	 * </p>
	 */
	private Map cmdLineOptions;

    /**
     * 
     */
    public CmdLineArgsInheritor()
    {
		this.cmdLineOptions = new HashMap();
		this.cmdLineParser = new CmdLineParser();
    }
    
	/**
	 * Subclasses have to call from their constructor and implant their desired 
	 * options. Later on they can retrieve those options by using method <tt>getParsedCmdLineOption(String key)</tt> 
	 * and do whatever they desire with the value then contained within the option.  
	 * 
	 * @param key The same key as used for <tt>getParsedCmdLineOption(String key)</tt> 
	 */
	protected final void addCmdLineOption(String key,CmdLineParser.Option option){
		if(option==null){
			throw new IllegalArgumentException("Specify a valid Option of a type within jargs.gnu.CmdLineParser.Option instead of null!");
		}
		if(this.cmdLineOptions.containsKey(key)){
			throw new IllegalArgumentException("Ambiguity: Option: "+String.valueOf(key)+" already added.");
		}
		this.cmdLineOptions.put(key,option);	
		this.cmdLineParser.addOption(option);
	}
	
	/**
	 * Returns the option <b>value</b> of a parsed command line option.
	 */
	protected final Object getParsedCmdLineOption(String key)throws IllegalArgumentException{
		Object ret = this.cmdLineOptions.get(key);
		if(key==null){
			throw new IllegalArgumentException("Option with key: \""+String.valueOf(key)+"\" has not been set in constructor.");
		}
		return this.cmdLineParser.getOptionValue((CmdLineParser.Option)ret);
	}
	
	
	/**
	 * <p>
	 * This method has to be called initially by the code using 
	 * this instance in order to configure.
	 * </p>
	 * <p>
	 * Every subclass has to call <code>super.parseArgs(cmdLineArgs)</code> 
	 * and then retrieve the options needed from the returned CmdLineParser!
	 * </p>
	 * @param cmdLineArgs
	 */
	public void parseArgs(String[]cmdLineArgs)throws Exception{
		// The Exception is not caught: Testbucket is greedy for any type of 
		// output (piped back to it).
		this.cmdLineParser.parse(cmdLineArgs);
		
	}
	
	protected abstract void usage();

}
