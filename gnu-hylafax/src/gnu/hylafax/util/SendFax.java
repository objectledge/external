// SendFax.java - gnu.hylafax implementation of the sendfax utility
// $Id: SendFax.java,v 1.4 2002/06/04 20:47:00 jaiger Exp $
//
// - basically gives an example for queuing a FAX job
//
// Copyright 2000, 2001, Joe Phillips <jaiger@innovationsw.com>
// Copyright 2001, Innovation Software Group, LLC - http://www.innovationsw.com
//
// This library is free software; you can redistribute it and/or
// modify it under the terms of the GNU Library General Public
// License as published by the Free Software Foundation; either
// version 2 of the License, or (at your option) any later version.
//
// This library is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
// Library General Public License for more details.
//
// You should have received a copy of the GNU Library General Public
// License along with this library; if not, write to the Free
// Software Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.
//
//
// for information on the HylaFAX FAX server see
//  http://www.hylafax.org/
//
// KNOWN ISSUES:
// - The password dialog is echoed to the screen, beware
// - not all symbolic pagesizes are supported yet (see Job.pagesizes)
// - can only queue a single job to the server per execution
//
// TODO:
// - make this class more flexible so it can be used by other programs
//   rather than only called from the command line
// - don't echo the password to the screen
//  

package gnu.hylafax.util;

// system includes
import java.lang.String;
import java.io.*;
import java.util.*;
import gnu.getopt.*;
import java.awt.*;

// home-grown includes
import gnu.hylafax.*;

/**
 * This class implements most of the sendfax program as supplied with
 * the HylaFAX distribution.  Not all options/features of the HylaFAX sendfax
 * command are supported.<P>
 * <UL>Specifically, 
 * <LI>only one Job with one destination can be queued per execution
 * <LI>only HylaFAX <I>server</I> native file formats can be part of a job.
 *     i.e. no client-side document conversions are performed
 * <LI>no built-in faxcover support
 * </UL>
 * The following command line options are
 * supported.<P>
 * <PRE>
 * -h <host>    specifiy server hostname
 * -u <user>    user to login to the server with
 * -v           verbose mode
 * -d <number>  specify a destination FAX <number>
 * -f <sender>  user <sender> as the identity of the FAX sender
 * -k <time>    kill the job if it doesn't complete after the indicated <time>
 * -t <tries>   make no more than <tries> attempts to deliver the FAX
 * -T <dials>   maximum number of <dials> to attempt for each job
 * -D           enable delivery notification
 * -R           enable delivery and retry notification
 * -N           disable delivery and retry notification
 * -P <pri>     assign the <pri> priority to the job (default: 127)
 * -l           use low resolution (98 lines/inch)
 * -m           use medium resolution (196 lines/inch)
 * -s <size>    specify the symbolic page <size> (legal, us-let, a3, a4, etc.)
 * </PRE>
 *<P>
 * Refer to the sendfax man page (from the HylaFAX distribution) for 
 * more information.
 *<P>
 * This program depends on the gnu.getopt package for command line parsing.
 * gnu.getopt (java-getopt) can be found at 
 * <a href="http://www.urbanophile.com/arenn/">http://www.urbanophile.com/arenn/</a>
 **/
public class SendFax {


   public static void main(String arguments[]){

      String user= "fax"; // -u
      String host= "localhost"; // -h
      String destination= null; // -d
      String from= user; // -f
      String killtime= "000259"; // -k
      int maxdials= 12; // -T
      int maxtries= 3; // -t
      int priority= 127; // -P
      String notifyaddr= user; // -f
      int resolution= 98; // -l, -m
      Dimension pagesize;   // -s
      String notify= "none";
      String pagechop= "default";
      int chopthreshold= 3;
      Vector documents= new Vector();
      boolean verbose= false;
      boolean from_is_set= false;

      pagesize= (Dimension)Job.pagesizes.get("na-let"); // default pagesize is US Letter

      Getopt g= new Getopt("SendFax",arguments,"k:t:T:DRNP:d:vf:h:lms:u:");
      char opt;
      while( (short)(opt = (char)g.getopt()) != -1){
         switch(opt){
         case 'd':
            // destination
            destination= g.getOptarg();
            break;
         case 'f':
            // from address
            from_is_set= true;
            from= g.getOptarg();
            break;
         case 'k':
            // killtime
            killtime= g.getOptarg();
            break;
         case 'h':
            host= g.getOptarg();
            break;
         case 'l':
            // low-res
            resolution= Job.RESOLUTION_LOW;
            break;
         case 'm':
            // medium-res
            resolution= Job.RESOLUTION_MEDIUM;
            break;
         case 't':
            maxtries= Integer.parseInt(g.getOptarg());
            break;
         case 'T':
            maxdials= Integer.parseInt(g.getOptarg());
            break;
         case 'D':
            notify= Job.NOTIFY_DONE;
            break;
         case 'R':
            notify= Job.NOTIFY_REQUEUED;
            break;
         case 'N':
            notify= Job.NOTIFY_NONE;
            break;
         case 'P':
            priority= Integer.parseInt(g.getOptarg());
            break;
         case 's':
            pagesize= (Dimension)Job.pagesizes.get(g.getOptarg());
            if(pagesize == null){
               // no good
               System.err.println("'"+g.getOptarg()+"' is not a valid pagesize value");
               usage(System.err);
               System.exit(-1);
            }
            break;
         case 'u':
            user= g.getOptarg();
            break;
         case 'v':
            // verbose mode
            verbose= true;
            break;
         case '?':
            usage(System.err);
            System.exit(-1);
            break;
         default:
            usage(System.err);
            System.exit(-1);
            // error
            break;
         }
      };// while processing options

      // validate some parameters
      if(!from_is_set){
         from= user;
      }

      // there should be a destination
      if(destination == null){
         // destination is required
         usage(System.err);
         System.exit(-1);
      }

      // make sure there is at least one file
      int i, count= 0;
      for(i= g.getOptind(); i<arguments.length; i++){
         count++;
      }
      if(count < 1){
          // at least one document is required
          usage(System.err);
          System.exit(-1);
      }


      // get down to business, send the FAX already

      HylaFAXClient c= new HylaFAXClient();
      try{
         c.setDebug(verbose);	// enable debug messages

         c.open(host);

         if(c.user(user)){
            // need password
            System.out.print("Password:");
            BufferedReader input= new BufferedReader(new InputStreamReader(System.in));
            String password= input.readLine();
            c.pass(password);
         }
         c.noop();	// for the heck of it
         c.tzone(HylaFAXClientProtocol.TZONE_LOCAL);

         // schlep files up to server
         for(i= g.getOptind(); i<arguments.length; i++){
            FileInputStream file= new FileInputStream(arguments[i]);
            String remote_filename= c.putTemporary(file);
            documents.addElement(remote_filename);
         } 


         Job job= c.createJob(); // start a new job

         // set job properties
         job.setFromUser(from);
         job.setNotifyAddress(from);
         job.setKilltime(killtime);
         job.setMaximumDials(maxdials);
         job.setMaximumTries(maxtries);
         job.setPriority(priority);
         job.setDialstring(destination);
         job.setVerticalResolution(resolution);
         job.setPageDimension(pagesize);
         job.setNotifyType(notify);
         job.setChopThreshold(chopthreshold);

         // add documents to the job
         for(i=0;i<documents.size();i++){
            String document= (String)documents.elementAt(i);
            job.addDocument(document);
         }

         c.submit(job); // submit the job to the scheduler

      }catch(Exception e){
         e.printStackTrace();
      }finally{
         // disconnect from the server
         try{
            c.quit();
         }catch(Exception e){
            // quit failed, not much we can do now
            e.printStackTrace();
         }
      }
   }// main

   /**
    * print out usage information on this program
    * @param out where to print the usage info
    */
   public static void usage(PrintStream out){
      out.println("usage:\n\tSendFax <options> file1 ...\n");
      out.println("where <options> can be:\n"+
                  "\t-h <host>    specifiy server hostname\n"+
                  "\t-u <user>    user to login to the server with\n"+
                  "\t-v           verbose mode\n"+
                  "\t-d <number>  specify a destination FAX <number>\n"+
                  "\t-f <sender>  user <sender> as the identity of the FAX sender\n"+
                  "\t-k <time>    kill the job if it doesn't complete after the\n"+
                  "\t             indicated <time> (default: \"000259\", 2 hours, 59 minutes)\n"+
                  "\t-t <tries>   make no more than <tries> attempts to deliver the FAX\n"+
                  "\t-T <dials>   maximum number of <dials> to attempt for each job\n"+
                  "\t-D           enable delivery notification\n"+
                  "\t-R           enable delivery and retry notification\n"+
                  "\t-N           disable delivery and retry notification\n"+
                  "\t-P <pri>     assign the <pri> priority to the job (default: 127)\n"+
                  "\t-l           use low resolution (98 lines/inch)\n"+
                  "\t-m           use medium resolution (196 lines/inch)\n"+
                  "\t-s <size>    specify the symbolic page <size>\n"+
                  "\t             (legal, us-let, a3, a4, etc.)\n"+
                  "\nFiles queued must be formats that the HylaFAX server can\n"+
                  " handle natively (PS, TIFF, etc.) as no client-side\n"+
                  " conversions are performed."
                  );
   }// usage

}// end of SendFax 

// end of file 
