// ServerResponseException.java
// $Id: ServerResponseException.java,v 1.1 2002/06/18 01:36:40 jaiger Exp $
//
// Copyright 1997, Joe Phillips <jaiger@innovationsw.com>
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

import java.lang.String;

public class ServerResponseException extends Exception{
   private long code;
   private String message;

   /**
    * basic constructor.  creates a new ServerResponseException
    * given the server error message.  The input string must be in the
    * format of 3 digits, a space and the error message.  This is the
    * default format of the server responses.
    * @param str the server error message in the form 
    */
   public ServerResponseException(java.lang.String str){
      code= -1;	// not set yet
      message= str;
   };

   /**
    * get the server response code.
    * @exception NumberFormatException the first 3 characters of the message are <i>not</i> a number as they should be in a normal server response
    * @return server response code as long value
    */
   public long getCode()
      throws NumberFormatException
   {
      code= Integer.parseInt(message.substring(0,3));
      return code;
   };// getCode

   /**
    * get the server message.
    * @return the full server response message string
    */
   public String getMessage(){
      return message;
   };// getMessage

   /**
    * get a string representing this ServerResponseException
    * @return this ServerResponseException as a String
    */
   public String toString(){
      return message;
   };// toString

};// ServerResponseException

// ServerResponseException.java
