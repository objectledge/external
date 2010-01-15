// TransferEventSource.java -
// $Id: TransferEventSource.java,v 1.1 2002/06/18 01:36:40 jaiger Exp $
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

import java.util.Vector;

public abstract interface TransferEventSource {

   /**
    * register a transfer listener with the event source.  Each 
    * transfer listener registered with the event source will be notified
    * whenever a transfer event occurs.
    * @param listener the TransferListener to register with this event source
    **/
   public abstract void addTransferListener(TransferListener listener);

   /**
    * register a set of transfer listeners with the event source.  Each 
    * transfer listener registered with the event source will be notified
    * whenever a transfer event occurs.
    * @param listeners the TransferListeners to register with this event source
    **/
   public abstract void addTransferListeners(Vector listeners);

   /**
    * De-register a transfer listener with the event source.  Once a
    * listener has been de-registered, it should not receive any more
    * transfer events from the event source however this is not guaranteed.
    **/
   public abstract void removeTransferListener(TransferListener listener);

};// TransferEventSource

// TransferEventSource.java
