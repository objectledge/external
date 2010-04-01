/*
                    Egothor Software License version 1.00
                    Copyright (C) 1997-2004 Leo Galambos.
                 Copyright (C) 2002-2004 "Egothor developers"
                      on behalf of the Egothor Project.
                             All rights reserved.

   This  software  is  copyrighted  by  the "Egothor developers". If this
   license applies to a single file or document, the "Egothor developers"
   are the people or entities mentioned as copyright holders in that file
   or  document.  If  this  license  applies  to the Egothor project as a
   whole,  the  copyright holders are the people or entities mentioned in
   the  file CREDITS. This file can be found in the same location as this
   license in the distribution.

   Redistribution  and  use  in  source and binary forms, with or without
   modification, are permitted provided that the following conditions are
   met:
    1. Redistributions  of  source  code  must retain the above copyright
       notice, the list of contributors, this list of conditions, and the
       following disclaimer.
    2. Redistributions  in binary form must reproduce the above copyright
       notice, the list of contributors, this list of conditions, and the
       disclaimer  that  follows  these  conditions  in the documentation
       and/or other materials provided with the distribution.
    3. The name "Egothor" must not be used to endorse or promote products
       derived  from  this software without prior written permission. For
       written permission, please contact Leo.G@seznam.cz
    4. Products  derived  from this software may not be called "Egothor",
       nor  may  "Egothor"  appear  in  their name, without prior written
       permission from Leo.G@seznam.cz.

   In addition, we request that you include in the end-user documentation
   provided  with  the  redistribution  and/or  in the software itself an
   acknowledgement equivalent to the following:
   "This product includes software developed by the Egothor Project.
    http://egothor.sf.net/"

   THIS  SOFTWARE  IS  PROVIDED  ``AS  IS''  AND ANY EXPRESSED OR IMPLIED
   WARRANTIES,  INCLUDING,  BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF
   MERCHANTABILITY  AND  FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
   IN  NO  EVENT  SHALL THE EGOTHOR PROJECT OR ITS CONTRIBUTORS BE LIABLE
   FOR   ANY   DIRECT,   INDIRECT,  INCIDENTAL,  SPECIAL,  EXEMPLARY,  OR
   CONSEQUENTIAL  DAMAGES  (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
   SUBSTITUTE  GOODS  OR  SERVICES;  LOSS  OF  USE,  DATA, OR PROFITS; OR
   BUSINESS  INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,
   WHETHER  IN  CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE
   OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN
   IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.

   This  software  consists  of  voluntary  contributions  made  by  many
   individuals  on  behalf  of  the  Egothor  Project  and was originally
   created by Leo Galambos (Leo.G@seznam.cz).
 */
package org.egothor.stemmer;

import java.util.*;
import java.io.*;

/**
 *  The MultiTrie is a Trie of Tries. It stores words and their associated
 *  patch commands. The MultiTrie handles patch commmands individually
 *  (each command by itself).
 *
 * @author    Leo Galambos
 */
public class MultiTrie extends Trie {
    final String EOM = "*";

    Vector tries = new Vector();

    int BY = 1;


    /**
     *  Constructor for the MultiTrie object.
     *
     * @param  is               the input stream
     * @exception  IOException  if an I/O error occurs
     */
    public MultiTrie(DataInput is) throws IOException {
        super(false);
        forward = is.readBoolean();
        BY = is.readInt();
        for (int i = is.readInt(); i > 0; i--) {
            tries.addElement(new Trie(is));
        }
    }


    /**
     *  Constructor for the MultiTrie object
     *
     * @param  forward  set to <tt>true</tt> if the elements should be read
     *      left to right
     */
    public MultiTrie(boolean forward) {
        super(forward);
    }


    /**
     *  Return the element that is stored in a cell associated with the
     *  given key.
     *
     * @param  key  the key to the cell holding the desired element
     * @return      the element
     */
    public String getFully(String key) {
        StringBuffer result = new StringBuffer(tries.size() * 2);
        for (int i = 0; i < tries.size(); i++) {
            String r = ((Trie) tries.elementAt(i)).getFully(key);
            if (EOM.equalsIgnoreCase(r)) {
                return result.toString();
            }
            result.append(r);
        }
        return result.toString();
    }


    /**
     *  Return the element that is stored as last on a path belonging to
     *  the given key.
     *
     * @param  key  the key associated with the desired element
     * @return      the element that is stored as last on a path
     */
    public String getLastOnPath(String key) {
        StringBuffer result = new StringBuffer(tries.size() * 2);
        for (int i = 0; i < tries.size(); i++) {
            String r = ((Trie) tries.elementAt(i)).getLastOnPath(key);
            if (EOM.equalsIgnoreCase(r)) {
                return result.toString();
            }
            result.append(r);
        }
        return result.toString();
    }


    /**
     *  Write this data structure to the given output stream.
     *
     * @param  os               the output stream
     * @exception  IOException  if an I/O error occurs
     */
    public void store(DataOutput os) throws IOException {
        os.writeBoolean(forward);
        os.writeInt(BY);
        os.writeInt(tries.size());
        Enumeration e = tries.elements();
        while (e.hasMoreElements()) {
            ((Trie) e.nextElement()).store(os);
        }
    }


    /**
     *  Add an element to this structure consisting of the given key and
     *  patch command. This method will return without executing if the
     *  <tt>cmd</tt> parameter's length is 0.
     *
     * @param  key  the key
     * @param  cmd  the patch command
     */
    public void add(String key, String cmd) {
        if (cmd.length() == 0) {
            return;
        }
        int levels = cmd.length() / BY;
        while (levels >= tries.size()) {
            tries.addElement(new Trie(forward));
        }
        for (int i = 0; i < levels; i++) {
            ((Trie) tries.elementAt(i)).add(key, cmd.substring(BY * i, BY * i + BY));
        }
        ((Trie) tries.elementAt(levels)).add(key, EOM);
    }


    /**
     *  Remove empty rows from the given Trie and return the newly reduced
     *  Trie.
     *
     * @param  by  the Trie to reduce
     * @return     the newly reduced Trie
     */
    public Trie reduce(Reduce by) {
        Vector h = new Vector();
        Enumeration e = tries.elements();
        while (e.hasMoreElements()) {
            Trie a = (Trie) e.nextElement();
            h.addElement(a.reduce(by));
        }
        MultiTrie m = new MultiTrie(forward);
        m.tries = h;
        return m;
    }


    /**
     *  Print the given prefix and the position(s) in the Trie where it
     *  appears.
     *
     * @param  prefix  the desired prefix
     */
    public void printInfo(String prefix) {
        int c = 0;
        for (Enumeration e = tries.elements(); e.hasMoreElements(); c++) {
            Trie a = (Trie) e.nextElement();
            /*
             *  if (c==0) {
             *  Enumeration f = a.cmds.elements();
             *  while (f.hasMoreElements()) {
             *  System.out.print( "("+((String)f.nextElement())+")" );
             *  }
             *  }
             */
            a.printInfo(prefix + "[" + c + "] ");
        }
    }
}
