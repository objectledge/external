// FaxStat.java - gnu.hylafax implementation of the faxstat utility
// $Id: FaxStat.java,v 1.5 2002/06/04 20:47:00 jaiger Exp $
//
// - basically gives an example for getting the status a FAX job
//
// Copyright 2001, Joe Phillips <jaiger@net-foundry.com>
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
// TODO:
// - make this class more flexible
//  

package gnu.hylafax.util;

// system includes
import java.lang.String;
import java.io.*;
import java.util.*;
import gnu.getopt.*;

// home-grown includes
import gnu.hylafax.*;

/**
 * This class implements most of the faxstat program as supplied with
 * the HylaFAX distribution.  The following command line options are
 * supported.<P>
 * <PRE>
 * -a		get status of archive/ (IGNORED)
 * -d		get status of doneq/
 * -f		get status of docq/
 * -g		display GMT timestamps
 * -h <host>	specifiy server hostname
 * -l		display local timestamps
 * -i		get additional status info
 * -r		get status of receive queue
 * -s		get status of send queue
 * -u		user to login to the server with
 * -v		verbose mode
 *</PRE>
 *<P>
 * Refer to the faxstat man page (from the HylaFAX distribution) for
 * more information.  
 * This program depends on the gnu.getopt package for command line parsing.
 * gnu.getopt (java-getopt) can be found at
 * <a href="http://www.urbanophile.com/arenn/">http://www.urbanophile.com/arenn/
</a>
 **/
public class FaxStat {

   private static final int TIME_LOCAL=0;
   private static final int TIME_GMT=1;

   public static void main(String arguments[]){

      String host= "localhost";
      int timestamp= TIME_LOCAL;
      String user= "fax";
      boolean moreinfo= false;
      boolean verbose= false;
      Vector options= new Vector();

      Getopt g= new Getopt("FaxStat",arguments,"advfgh:lirsu:");
      char opt;
      while( (short)(opt = (char)g.getopt()) != -1){
         switch(opt){
         case 'a':
            // archive/ status
            options.addElement(new Character(opt));
            break;
         case 'd':
            // doneq/ status
            options.addElement(new Character(opt));
            break;
         case 'f':
            // docq/ status
            options.addElement(new Character(opt));
            break;
         case 'g':
            // GMT timestamps
            timestamp= TIME_GMT;
            break;
         case 'h':
            host= g.getOptarg();
            break;
         case 'l':
            // LOCAL timestamps
            timestamp= TIME_LOCAL;
            break;
         case 'i':
            // additional info
            moreinfo= true;
            break;
         case 'r':
            // receive queue
            options.addElement(new Character(opt));
            break;
         case 's':
            // send queue
            options.addElement(new Character(opt));
            break;
         case 'u':
            user= g.getOptarg();
            break;
         case 'v':
            // verbose mode
            verbose= true;
            break;
         case '?':
            break;
         default:
            // error
            break;
         }
      };// while processing options

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
         if(timestamp == TIME_LOCAL){
            c.tzone(HylaFAXClientProtocol.TZONE_LOCAL);
         }else{
            c.tzone(HylaFAXClientProtocol.TZONE_GMT);
         }

         // LIST status
         Vector status= c.getList("status");
         for(int i=0;i<status.size();i++){
            System.out.println(status.elementAt(i));
         }

         // get file/job lists
         Enumeration opts= options.elements();
         while(opts.hasMoreElements()){
            Vector list= new Vector();
            opt= ((Character)opts.nextElement()).charValue();
            switch(opt){
            case 'a':
               list= c.getList("archive");
               break;
            case 'd':
               list= c.getList("doneq");
               break;
            case 'f':
               list= c.getList("docq");
               break;
            case 'r':
               list= c.getList("recvq");
               break;
            case 's':
               list= c.getList("sendq");
               break;
            default:
               // shouldn't happen at all
               //  should we bail on this condition?
               break;
            }
            System.out.println();
            Enumeration lines= list.elements();
            while(lines.hasMoreElements()){
               System.out.println(lines.nextElement());
            }
         }// for each opt

         c.quit();

      }catch(Exception e){
         e.printStackTrace();
      }
   };// main
};// FaxStat

// FaxStat.java
