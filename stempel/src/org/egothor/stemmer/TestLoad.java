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
import java.util.*;

/**
 *  The TestLoad is used to run tests for a compiled stemmer file against
 *  the original file it was compiled from.
 *
 * @author    Leo Galambos
 */
public class TestLoad {
    /**
     *  Entry point to the TestLoad application. This program requires 2
     *  parameters: the first should be a compiled stemmer file,and the
     *  second should be the file the first was compiled from.
     *
     * @param  args  the command line arguments
     */
    public static void main(java.lang.String[] args) {
        if (args.length < 2) {
            System.out.println("Use 2 params or more.");
            return;
        }

        Trie trie;

        try {
            DataInputStream is = new DataInputStream(
                    new BufferedInputStream(
                    new FileInputStream(args[0])));
            String method = is.readUTF().toUpperCase();
            if (method.indexOf('M') < 0) {
                trie = new Trie(is);
            } else {
                trie = new MultiTrie2(is);
            }
            is.close();

            testTrie(trie, args[1], false);
        } catch (FileNotFoundException x) {
            x.printStackTrace();
            return;
        } catch (IOException x) {
            x.printStackTrace();
            return;
        }
    }


    /**
     *  A unit test for JUnit
     *
     * @param  trie     Description of the Parameter
     * @param  file     Description of the Parameter
     * @param  usefull  Description of the Parameter
     */
    static void testTrie(Trie trie, String file, boolean usefull) {
        try {
            LineNumberReader in = new LineNumberReader(
                    new BufferedReader(new InputStreamReader(new FileInputStream(file), "UTF-8")));
            int words = 0;
            int nobug = 0;
            long totalTime = 0L;
            long delta = 0L;
            Diff diff = new Diff();

            for (String line = in.readLine(); line != null; line = in.readLine()) {
                try {
                    line = line.toLowerCase();
                    StringTokenizer st = new StringTokenizer(line);
                    String stem = st.nextToken();
                    while (st.hasMoreTokens()) {
                        String token = st.nextToken();
                        words++;
                        delta = System.currentTimeMillis();
                        String cmd = (usefull) ? trie.getFully(token) : trie.getLastOnPath(token);
                        String stm = diff.apply(new StringBuffer(token), cmd).toString();
                        delta = System.currentTimeMillis() - delta;
                        totalTime += delta;
                        if (stm.equalsIgnoreCase(stem) == false) {
                            System.err.println(stem + " != " + token
                                    + "@" + cmd + "=" + stm);
                        } else {
                            nobug++;
                        }
                    }
                } catch (java.util.NoSuchElementException x) {
                    // no base token (stem) on a line
                }
            }
            System.out.println("tests " + words + " succ " + nobug +
                    " err " + ((float) (words - nobug) * 100 / (float) words) +
                    "% avg. time " + (double)totalTime / (double)words);
            trie.printInfo("Trie Info: ");
        } catch (FileNotFoundException x) {
            x.printStackTrace();
        } catch (IOException x) {
            x.printStackTrace();
        }
    }
}
