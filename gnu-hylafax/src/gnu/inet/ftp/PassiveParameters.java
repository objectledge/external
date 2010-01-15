// PassiveParameters.java
// $Id: PassiveParameters.java,v 1.1 2002/06/18 01:36:40 jaiger Exp $
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
package gnu.inet.ftp;

import java.net.*;
import java.lang.*;

/**
 * This class encapsulates the parameters of a passive data connection.
 **/
public class PassiveParameters extends Object{
   // private data members
   private InetAddress address;
   private Integer port;

   //
   // public methods
   //

   /**
    * create a new instance of PassiveParameters
    * @param address IP address
    * @param port TCP/IP port value
    */
   public PassiveParameters(String address, int port)
      throws UnknownHostException
   {
      this.address= InetAddress.getByName(address);
      this.port= new Integer(port);
   }// constructor

   /**
    * get the port value
    * @return port value as int
    */
   public int getPort(){
         return port.intValue();
   }// getPort

   /**
    * get the IP address
    * @return IP address
    */
   public InetAddress getInetAddress(){
         return address;
   }// getInetAddress

   /**
    * compare another PassiveParameters instance to this one.
    * @param other the other instance to compare this one with
    * @return true if the other instance equals this one, false if they are not equal
    **/
   public boolean equals(PassiveParameters other){
      if((this.port == other.port) && 
         (this.address.equals(other.address))){
         return true;
      }else{
         return false;
      }
   }// equals

}// PassiveParameters

// PassiveParameters.java
