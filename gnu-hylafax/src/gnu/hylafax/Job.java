// Job.java - a HylaFAX Job representation
// $Id: Job.java,v 1.8 2002/06/18 01:36:40 jaiger Exp $
//
// Copyright 2001, 2002 Innovation Software Group, LLC - http://www.innovationsw.com
//                Joe Phillips <jaiger@innovationsw.com>
//
// for information on the HylaFAX FAX server see
//  http://www.hylafax.org/
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
package gnu.hylafax;

// system includes
import java.lang.*;
import java.awt.*;
import java.util.*;
import java.io.*;

// home-grown includes
import gnu.inet.ftp.ServerResponseException;

/**
 * @see HylaFAXClientProtocol
 * @see HylaFAXClient
 **/
public class Job
{
   private HylaFAXClient client;
/*   private String FromUser;
   private String Killtime;
   private int MaximumDials;
   private int MaximumTries;
   private int Priority;
   private String Number;
   private String NotifyAddress;
   private int VerticalResolution;
   private Dimension PageDimension;
   private String NotifyType;
   private String PageChop;
   private int ChopThreshold;
   private String Document;
*/
   private long Id;

   public static int PRIORITY_NORMAL= 127;
   public static int PRIORITY_BULK= 207;
   public static int PRIORITY_HIGH= 63;

   public static int RESOLUTION_LOW= 98;
   public static int RESOLUTION_MEDIUM= 196;

   public static String NOTIFY_NONE= HylaFAXClientProtocol.NOTIFY_NONE;
   public static String NOTIFY_DONE= HylaFAXClientProtocol.NOTIFY_DONE;
   public static String NOTIFY_REQUEUED= HylaFAXClientProtocol.NOTIFY_REQUEUED;

   public static String CHOP_DEFAULT= "default";

   public static Hashtable pagesizes= new Hashtable();

   // the following pagesizes were taken from inspection of the HylaFAX
   // native clients and 
   // http://www.math.ntnu.no/~perhov/public/iso/iso-paper.html
   //
   static {
      pagesizes.put("us-let", new Dimension(216,279));
      pagesizes.put("na-let", new Dimension(216,279));
      pagesizes.put("a3", new Dimension(297,420));
      pagesizes.put("a4", new Dimension(210,297));
      pagesizes.put("a5", new Dimension(148,210));
      pagesizes.put("a6", new Dimension(105,148));
      pagesizes.put("b4", new Dimension(250,353));
      pagesizes.put("us-leg", new Dimension(216,356));
      pagesizes.put("us-led", new Dimension(279,432));
      pagesizes.put("us-exe", new Dimension(190,254));
/*  The following values are currently unknown to me ...
      pagesizes.put("jp-leg", new Dimension());
      pagesizes.put("jp-let", new Dimension());
*/
   }

   Job(HylaFAXClient c)
      throws ServerResponseException,
         IOException
   {
      synchronized(c){
         client= c;
         client.jnew();
         Id= client.job();
      }
   }// constructor

   Job(HylaFAXClient c, long id)
      throws ServerResponseException,
         IOException
   {
      synchronized(c){
         client= c;
         client.job(id);
         Id= client.job();
      }
   }// constructor


   public String getFromUser()
      throws ServerResponseException,
         IOException
   {
      return getProperty("FROMUSER");
   }// getFromUser

   public String getKilltime()
      throws ServerResponseException,
         IOException
   {
      return getProperty("LASTTIME");
   }// getKilltime

   public int getMaximumDials()
      throws ServerResponseException,
         IOException
   {
      return Integer.parseInt(getProperty("MAXDIALS"));
   }// getMaximumDials

   public int getMaximumTries()
      throws ServerResponseException,
         IOException
   {
      return Integer.parseInt(getProperty("MAXTRIES"));
   }// getMaximumTries

   public int getPriority()
      throws ServerResponseException,
         IOException
   {
      return Integer.parseInt(getProperty("SCHEDPRI"));
   }// getPriority

   public String getDialstring()
      throws ServerResponseException,
         IOException
   {
      return getProperty("DIALSTRING");
   }// getDialstring

   public String getNotifyAddress()
      throws ServerResponseException,
         IOException
   {
      return getProperty("NOTIFYADDR");
   }// getNotifyAddress

   public int getVerticalResolution()
      throws ServerResponseException,
         IOException
   {
      return Integer.parseInt(getProperty("VRES"));
   }// getVerticalResolution

   public Dimension getPageDimension()
      throws ServerResponseException,
         IOException
   {
      synchronized(client){
         return new Dimension(getPageWidth(), getPageLength());
      }
   }// getPageDimension

   public int getPageWidth()
      throws ServerResponseException,
         IOException
   {
      return Integer.parseInt(getProperty("PAGEWIDTH"));
   }// getPageWidth

   public int getPageLength()
      throws ServerResponseException,
         IOException
   {
      return Integer.parseInt(getProperty("PAGELENGTH"));
   }// getPageLength

   public String getNotifyType()
      throws ServerResponseException,
         IOException
   {
      return getProperty("NOTIFY");
   }// getNotifyType

   public String getPageChop()
      throws ServerResponseException,
         IOException
   {
      return getProperty("PAGECHOP");
   }// getPageChop

   public int getChopThreshold()
      throws ServerResponseException,
         IOException
   {
      return Integer.parseInt(getProperty("CHOPTHRESHOLD"));
   }// getChopThreshold

   public String getDocumentName()
      throws ServerResponseException,
         IOException
   {
      return getProperty("DOCUMENT");
   }// getDocumentName

   public String getRetrytime()
      throws ServerResponseException,
         IOException
   {
      return getProperty("RETRYTIME");
   }// getRetrytime

   /**
    * Get the value for an arbitrary property for this job.
    * Developers using this method should be familiar with the HylaFAX client protocol in order to provide the correct key values and how to interpret the values returned.
    * This method is thread-safe.
    * @exception ServerResponseException the server responded with an error.  This is likely due to a protocol error.
    * @exception IOException an i/o error occured
    * @return a String value for the given property key
    */
   public String getProperty(String key)
      throws ServerResponseException,
         IOException
   {
      synchronized(client){
         long j= client.job();
         client.job(Id);
         String tmp= client.jparm(key);
         client.job(j);
         return tmp;
      }
   }// getProperty

   /**
    * get the job-id of this Job instance. 
    * @return job id
    */
   public long getId(){
      return Id;
   }// getId

   public void setFromUser(String value)
      throws ServerResponseException,
         IOException
   {
      setProperty("FROMUSER", value);
   }// setFromUser

   public void setKilltime(String value)
      throws ServerResponseException,
         IOException
   {
      setProperty("LASTTIME", value);
   }// setKilltime

   public void setMaximumDials(int value)
      throws ServerResponseException,
         IOException
   {
      setProperty("MAXDIALS", value);
   }// setMaximumDials

   public void setMaximumTries(int value)
      throws ServerResponseException,
         IOException
   {
      setProperty("MAXTRIES", value);
   }// setMaximumTries

   public void setPriority(int value)
      throws ServerResponseException,
         IOException
   {
      setProperty("SCHEDPRI", value);
   }// setPriority

   public void setDialstring(String value)
      throws ServerResponseException,
         IOException
   {
      setProperty("DIALSTRING", value);
   }// setDialstring

   public void setNotifyAddress(String value)
      throws ServerResponseException,
         IOException
   {
      setProperty("NOTIFYADDR", value);
   }// setNotifyAddress

   public void setVerticalResolution(int value)
      throws ServerResponseException,
         IOException
   {
      setProperty("VRES", value);
   }// setVerticalResolution

   public void setPageDimension(Dimension value)
      throws ServerResponseException,
         IOException
   {
      synchronized(client){
         setPageWidth((int)value.getWidth());
         setPageLength((int)value.getHeight());
      }
   }// setPageDimension

   public void setPageWidth(int width)
      throws ServerResponseException,
         IOException
   {
      setProperty("PAGEWIDTH", width);
   }// setPageWidth

   public void setPageLength(int length)
      throws ServerResponseException,
         IOException
   {
      setProperty("PAGELENGTH", length);
   }// setPageLength

   /**
    * set the notification type.  For possible values, see the NOTIFY_*
    * members of this class.
    * @param value the new notification type
    * @exception ServerResponseException the server responded with an error.  This is likely a protocol violation.
    * @exception IOException an IO error occurred while communicating with the server
    */
   public void setNotifyType(String value)
      throws ServerResponseException,
         IOException
   {
      setProperty("NOTIFY", value);
   }// setNotifyType

   public void setPageChop(String value)
      throws ServerResponseException,
         IOException
   {
      setProperty("PAGECHOP", value);
   }// setPageChop

   public void setChopThreshold(int value)
      throws ServerResponseException,
         IOException
   {
      setProperty("CHOPTHRESHOLD", value);
   }// setChopThreshold

   public void addDocument(String value)
      throws ServerResponseException,
         IOException
   {
      setProperty("DOCUMENT", value);
   }// addDocument

   public void setRetrytime(String value)
      throws ServerResponseException,
         IOException
   {
      setProperty("RETRYTIME", value);
   }// setRetrytime

   /**
    * Set any arbitrary property on this job.
    * In order to use this method, developers should be familiar with the HylaFAX client protocol.
    * This method is thread-safe.
    * @exception ServerResponseException the server responded with an error code.  This is likely a protocol violation.
    * @exception IOException an i/o error occured
    */
   public void setProperty(String parameter, String value)
      throws ServerResponseException,
         IOException
   {
      synchronized(client){
         long j= client.job();
         client.job(Id);
         client.jparm(parameter, value);
         client.job(j);
      }
   }// setProperty

   /**
    * Set any arbitrary property on this job to an integer value.
    * In order to use this method, developers should be familiar with the HylaFAX client protocol.
    * This method is thread-safe.
    * @exception ServerResponseException the server responded with an error code.  This is likely a protocol violation.
    * @exception IOException an i/o error occured
    */
   public void setProperty(String property, int value)
      throws ServerResponseException,
         IOException
   {
      setProperty(property, (new Integer(value)).toString());
   }// setProperty

}// Job class

// Job.java
