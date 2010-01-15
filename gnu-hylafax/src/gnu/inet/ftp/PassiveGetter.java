// PassiveGetter.java
// $Id: PassiveGetter.java,v 1.1 2002/06/18 01:36:40 jaiger Exp $
//
// Copyright 2000, Joe Phillips <jaiger@innovationsw.com>
// Copyright 2001, 2002 Innovation Software Group, LLC - http://www.innovationsw.com
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
// TODO:
// - implement compressed streams
//
package gnu.inet.ftp;

// system includes
import java.lang.*;
import java.io.*;
import java.net.*;
import java.util.*;
import java.util.zip.*;

// home-grown imports
// <none>

/**
 * This class implements an FTP-style data connection thread for GETing 
 * files/data passively.  This class is used internally to the FtpClient
 * class.
 **/
public class PassiveGetter extends Getter {
   // private data
   private PassiveConnection connection;

   // public constructors
   //

   /**
    * Create a new PassiveGetter instance with the given OutpuStream for
    * data output and using the given PassiveParameters to connect to
    * the server.
    * @param out the OutputStream where retrieved data will be written
    * @param connection the PassiveConnection to the server
    **/
   public PassiveGetter(OutputStream out, PassiveConnection connection){
      super();
      this.setDebug(false);
    
      this.ostream= out;
      this.connection= connection;
   };// end of default constructor

   //
   // public methods
   //

   /**
    * set the debug flag value.  true will cause debug output to
    * be emitted by the Getter.  false will cause this Getter to
    * run silently.
    * @param value (true|false)
    */
   public synchronized void setDebug(boolean value){
      this.debug= value;
   };// setDebug

   /**
    * cancel a running transfer
    * sets a flag and calls interrupt()
    * can only be called once
    */
   public void cancel(){
      if(!cancelled){
         cancelled= true;
         interrupt();
      }
   };// cancel

   /**
    * get data from server using given parameters.
    */
   public void run(){
      boolean signalClosure= false;
      Socket sock= null;
      InputStream istream;
      long amount= 0;
      int buffer_size= 0;
      byte buffer[]= new byte[BUFFER_SIZE];
      this.cancelled= false;	// reset cancelled flag
      PassiveParameters parameters= connection.getPassiveParameters();
 
      try{
         // make connection
         sock= connection.getSocket();
         signalConnectionOpened(
            new ConnectionEvent(parameters.getInetAddress(), parameters.getPort())
            );
         signalClosure= true;
         signalTransferStarted();

         try{

            // handle different mode settings
            switch(mode){
               case FtpClient.MODE_ZLIB:
                  istream= new InflaterInputStream(sock.getInputStream());
                  break;
               case FtpClient.MODE_STREAM:
               default:
                  istream= sock.getInputStream();
                  break;
            };// switch

            int len;
            while(!cancelled && ((len= istream.read(buffer)) > 0)){
               ostream.write(buffer, 0, len);
               amount+= len;
               buffer_size+= len;
               if(buffer_size >= BUFFER_SIZE){
                  buffer_size= buffer_size%BUFFER_SIZE;
                  signalTransfered(amount);
               }
               yield();
            }

            ostream.flush();
         }catch(InterruptedIOException iioe){
            if(!cancelled){
               if(debug){
                  iioe.printStackTrace();
               }
            }
         }catch(Exception e){
            if(debug){
               e.printStackTrace();
            }
         }finally{
            signalTransferCompleted();
         };
      }catch(Exception ee){
         signalConnectionFailed(ee);
         if(debug){
            ee.printStackTrace();
         }
      }
      if(signalClosure == true){
         signalConnectionClosed(
            new ConnectionEvent(parameters.getInetAddress(), parameters.getPort())
            );
      }
         
   };// run

};// PassiveGetter 

// PassiveGetter.java
