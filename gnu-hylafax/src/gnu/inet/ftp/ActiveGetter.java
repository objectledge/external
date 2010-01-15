// ActiveGetter.java
// $Id: ActiveGetter.java,v 1.1 2002/06/18 01:36:40 jaiger Exp $
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
 * This class implements an FTP-style data connection server thread for 
 * GETing files/data non-passively.
 * <P>
 * This class is used internally to the FtpClient class.
 **/
public class ActiveGetter extends Getter {
   // private data
   private InetAddress address;
   private int port;
   private ServerSocket server;
   private int timeout;

   // public constructors
   //

   /**
    * Create a new ActiveGetter with the given OutputStream for data output.
    * @throws IOException an IO error occurred with the ServerSocket
    **/
   public ActiveGetter(OutputStream out)
     throws IOException
   {
      super();
    
      // create server socket
      this.server= new ServerSocket(0);
      this.timeout= 30*1000;	// 30s timeout
      // store the port that the server is listening on 
      this.port= server.getLocalPort();
      this.address= this.server.getInetAddress();

      this.ostream= out;
   };// end of default constructor

   //
   // public methods
   //

   /**
    * get the local port this ActiveGetter is listening on
    * @return port number
    */
   public synchronized int getPort(){
      return port;
   };// getPort

   /**
    * get the local IP address that this ActiveGetter is listening on
    * @return server socket IP address
    */
   public InetAddress getInetAddress(){
      return address;
   };// getInetAddress

   /**
    * Set the connection timeout in milliseconds.  This method must be 
    * called before start()/run() for the value to take affect.
    * @param milliseconds the socket timeout value in milliseconds
    */
   public void setTimeout(int milliseconds){
      timeout= milliseconds;
   };// setTimeout

   /**
    * get data from server using given parameters.
    */
   public void run(){
      boolean signalClosure= false;
      Socket sock= null;
      InputStream istream;
      long amount= 0;
      long buffer_size= 0;
      byte buffer[]= new byte[BUFFER_SIZE];
      this.cancelled= false;	// reset cancelled flag
 
      try{
         // wait for connection
         server.setSoTimeout(timeout);	// can only wait so long
         sock= server.accept();
         signalConnectionOpened(
            new ConnectionEvent(sock.getInetAddress(), sock.getPort())
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
            sock.close();	// make sure the socket is closed
            signalTransferCompleted();
         }
      }catch(InterruptedIOException eiioe){
         signalConnectionFailed(eiioe);
         if(!cancelled){
            if(debug){
               eiioe.printStackTrace();
            }
         }
      }catch(Exception ee){
         signalConnectionFailed(ee);
         if(debug){
            ee.printStackTrace();
         }
      };
      if(signalClosure == true){
         signalConnectionClosed(
            new ConnectionEvent(sock.getInetAddress(), sock.getPort())
            );
      };
   };// run

};// ActiveGetter 

// ActiveGetter.java
