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

import java.io.*;

/**
 *  The Stock object is a stemmer. It consists of a set of stems and a
 *  stemming trie. The set of stems specifies the set of words that should
 *  not be transformed to their base forms (the wrong word might result).
 *  <p>
 *
 *  To save memory, the stem set can be destroyed and a simple strategy can
 *  be used in its place. For example, only transform longer terms (5 or
 *  more characters), and if the transformation generates a short stem
 *  (under 4 characters), do not accept it and instead use the original
 *  word rather than the very short stem that could be a mistake.
 *
 * @author    Leo Galambos
 */
public class Stock {
    org.egothor.stemmer.Trie stemmer = null;


    /**
     *  Constructor for the Stock object. It constructs the default stemmer
     *  that is loaded from a file that is specified by the system <code>default.stemmer</code>
     *  property.
     */
    public Stock() {
        String stemBin = System.getProperty("default.stemmer");
        if (stemBin == null) {
            return;
        }

        try {
            System.out.println("Default stemmer " + stemBin);
            DataInputStream in = new DataInputStream(
                    new BufferedInputStream(
                    new FileInputStream(stemBin)));
            String method = in.readUTF().toUpperCase();
            if (method.indexOf('M') < 0) {
                stemmer = new org.egothor.stemmer.Trie(in);
            } else {
                stemmer = new org.egothor.stemmer.MultiTrie2(in);
            }
            System.out.println("Default stemmer loaded.");
            in.close();
        } catch (IOException x) {
            x.printStackTrace();
            stemmer = null;
        }
    }


    /**
     *  Return the Trie (stemmer) for the given language.
     *
     * @param  lang  the language for which a stemmer should be returned
     * @return       a Trie (stemmer)
     */
    public Trie getStemmer(java.util.Locale lang) {
        return stemmer;
    }
}
