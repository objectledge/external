package cpdetector ;

import java.io.File ;
import java.io.IOException ;

import java.net.MalformedURLException ;

import java.nio.charset.Charset ;

import cpdetector.io.ASCIIDetector ;
import cpdetector.io.CodepageDetectorProxy ;
import cpdetector.io.JChardetFacade ;
import cpdetector.io.ParsingDetector ;

/**
 * @author demian reachable (at)\@ rootring.com
 * 
 * Simple class that tries to detect the encoding of files given on the command-line.
 */
public class CharsetPrinter
{
  private final CodepageDetectorProxy detector = CodepageDetectorProxy.getInstance () ;
  
  public CharsetPrinter ()
  {
    detector.add (new ParsingDetector (false)) ;
    detector.add (JChardetFacade.getInstance ()) ;
    detector.add (ASCIIDetector.getInstance ()) ;
  }

  public String guessEncoding (File f) throws MalformedURLException, IOException
  { 
    Charset charset = detector.detectCodepage (f.toURL ()) ;
    
    if (charset == null)
      return null ;
    
    return charset.name () ;
  }
  
  public static void main (String[] args)
  {
  	CharsetPrinter cp = new CharsetPrinter () ;
  	
  	if (args.length < 1)
  	{
		System.err.println ("Please provide one or more files to examine on the command line after the command.") ;
  	}
  	
  	try
  	{
  		File f ;
  		
  		for (int walk = 0 ; walk < args.length ; walk++)
  		{
			f = new File (args[walk]) ;
			
			if (f.exists () && f.canRead () && f.isFile ())
			{
				System.out.println (args[walk] + " appears to be " + cp.guessEncoding (f)) ;
			}
			else
  			{
				System.err.println (args[walk] + " is not a file, does not exists or is not readable at this time.") ;
				System.out.println (args[walk] + " appears to be UNKNOWN") ;
  			}
  		}
  	}
  	catch (MalformedURLException e)
  	{
  		System.err.println ("The filename makes no sense.") ;
  	}
  	catch (IOException e)
  	{
  		System.err.println ("Problem reading from file") ;
  		e.printStackTrace () ;
  	}
  }
}