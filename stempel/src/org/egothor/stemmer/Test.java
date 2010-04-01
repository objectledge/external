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
 *  The Test class is used to test stemming methods.
 *
 * @author    Leo Galambos
 */
public class Test {

    static boolean backward;
    static boolean multi;
    static Trie trie;


    /**
     *  Test using some hard coded data.
     */
    public static void selfTest() {
        Trie t = new Trie(true);

        String keys[] = {"a", "ba", "bb", "c"};
        String vals[] = {"1", "2", "2", "4"};

        for (int i = 0; i < keys.length; i++) {
            t.add(keys[i], vals[i]);
        }

        System.out.println("Trie root:" + t.root +
                " size:" + t.rows.size() +
                " keys:" + t.cmds.size());

        t = t.reduce(new Optimizer());

        System.out.println("Trie root:" + t.root +
                " size:" + t.rows.size() +
                " keys:" + t.cmds.size());

        for (int i = 0; i < keys.length; i++) {
            System.out.println(keys[i] + " ---> " + t.getFully(keys[i]));
        }

        for (int i = 0; i < keys.length; i++) {
            System.out.println(keys[i] + " -l-> " + t.getLastOnPath(keys[i]));
        }

        System.out.println("ac -l-> " + t.getLastOnPath("ac"));
        System.out.println("abc -l-> " + t.getLastOnPath("abc"));
    }


    /**
     *  Entry point to the Test application.If no parameter is specified,
     *  {@link #selfTest()} is run. Otherwise, the user should specify a
     *  stemming method to use.
     *
     * @param  args  the command line argument
     */
    public static void main(java.lang.String[] args) {
        if (args.length < 1) {
            selfTest();
            return;
        }

        args[0].toUpperCase();

        backward = args[0].charAt(0) == '-';
        int qq = (backward) ? 1 : 0;
        boolean storeorig = false;

        if (args[0].charAt(qq) == '0') {
            storeorig = true;
            qq++;
        }

        multi = args[0].charAt(qq) == 'M';
        if (multi) {
            qq++;
        }

        char optimizer[] = new char[args[0].length() - qq];
        for (int i = 0; i < optimizer.length; i++) {
            optimizer[i] = args[0].charAt(qq + i);
        }

        for (int i = 1; i < args.length; i++) {
            LineNumberReader in;
//          System.out.println("[" + args[i] + "]");
            Diff diff = new Diff();
            try {
                int stems = 0;
                int words = 0;

                in = new LineNumberReader(
                        new BufferedReader(new FileReader(args[i])));
                for (String line = in.readLine(); line != null; line = in.readLine()) {
                    try {
                        line = line.toLowerCase();
                        StringTokenizer st = new StringTokenizer(line);
                        String stem = st.nextToken();
                        if (storeorig) {
                            words++;
                        }
                        while (st.hasMoreTokens()) {
                            String token = st.nextToken();
                            if (token.equals(stem) == false) {
                                words++;
                            }
                        }
                        stems++;
                    } catch (java.util.NoSuchElementException x) {
                        // no base token (stem) on a line
                    }
                }

                System.out.println("# words stems");
                System.out.println("H: " + words + " " + stems);

                double steps[] = {1.0, 0.9, 0.8, 0.7, 0.6, 0.5, 0.4, 0.3, 0.2, 0.1, 0.09, 0.08, 0.07, 0.06, 0.05, 0.04, 0.03};
                for (int lp = 0; lp < steps.length; lp++) {
                    int awords = 0;
                    int astems = 0;
                    int out = (int) (words * steps[lp]);
                    Random rnd = new Random();

                    allocTrie();

                    in = new LineNumberReader(
                            new BufferedReader(new FileReader(args[i])));
                    for (String line = in.readLine(); line != null; line = in.readLine()) {
                        try {
                            line = line.toLowerCase();
                            StringTokenizer st = new StringTokenizer(line);
                            String stem = st.nextToken();
                            if (storeorig) {
                                if (rnd.nextInt(words) < out) {
                                    trie.add(stem, "-a");
                                    awords++;
                                }
                            }
                            while (st.hasMoreTokens()) {
                                String token = st.nextToken();
                                if (token.equals(stem) == false) {
                                    if (rnd.nextInt(words) < out) {
                                        trie.add(token, diff.exec(token, stem));
                                        awords++;
                                    }
                                }
                            }
                        } catch (java.util.NoSuchElementException x) {
                            // no base token (stem) on a line
                        }
                    }

                    String err = Integer.toString(100 * (words - awords) / words);
                    err = Integer.toString(100 - (int) (steps[lp] * 100));

                    trie.printInfo("T: " + err + " ");

                    Optimizer o = new Optimizer();
                    Optimizer2 o2 = new Optimizer2();
                    Lift l = new Lift(true);
                    Lift e = new Lift(false);
                    Gener g = new Gener();

                    for (int j = 0; j < optimizer.length; j++) {
                        String prefix;
                        switch (optimizer[j]) {
                            case 'G':
                                trie = trie.reduce(g);
                                prefix = "G: ";
                                break;
                            case 'L':
                                trie = trie.reduce(l);
                                prefix = "L: ";
                                break;
                            case 'E':
                                trie = trie.reduce(e);
                                prefix = "E: ";
                                break;
                            case '2':
                                trie = trie.reduce(o2);
                                prefix = "2: ";
                                break;
                            case '1':
                                trie = trie.reduce(o);
                                prefix = "1: ";
                                break;
                            default:
                                continue;
                        }
                        trie.printInfo(prefix + err + " ");
                    }

                    System.out.print("Full: " + err + " ");
                    testTrie(trie, args[i], true, storeorig);
                    System.out.print("Last: " + err + " ");
                    testTrie(trie, args[i], false, storeorig);
                    /*
                     *  DataOutputStream os = new DataOutputStream(
                     *  new BufferedOutputStream(
                     *  new FileOutputStream(args[i] + ".out." + lp)));
                     *  trie.store(os);
                     *  os.close();
                     */
                }
            } catch (FileNotFoundException x) {
                x.printStackTrace();
            } catch (IOException x) {
                x.printStackTrace();
            }
        }
    }


    /**
     *  Description of the Method
     */
    static void allocTrie() {
        if (multi) {
            trie = new MultiTrie2(!backward);
        } else {
            trie = new Trie(!backward);
        }
    }


    /**
     *  A unit test for JUnit
     *
     * @param  trie       Description of the Parameter
     * @param  file       Description of the Parameter
     * @param  usefull    Description of the Parameter
     * @param  storeorig  Description of the Parameter
     */
    static void testTrie(Trie trie, String file, boolean usefull, boolean storeorig) {
        try {
            LineNumberReader in = new LineNumberReader(
                    new BufferedReader(new FileReader(file)));
            int words = 0;
            int nobug = 0;
            Diff diff = new Diff();

            for (String line = in.readLine(); line != null; line = in.readLine()) {
                try {
                    line = line.toLowerCase();
                    StringTokenizer st = new StringTokenizer(line);
                    String stem = st.nextToken();
                    if (storeorig) {
                        words++;
                        String cmd = (usefull) ? trie.getFully(stem) : trie.getLastOnPath(stem);
                        String stm = diff.apply(new StringBuffer(stem), cmd).toString();
                        if (stm.equalsIgnoreCase(stem) == false) {
                            System.err.println(stem + " != " + stem
                                    + "@" + cmd + "=" + stm);
                        } else {
                            nobug++;
                        }
                    }
                    while (st.hasMoreTokens()) {
                        String token = st.nextToken();
                        if (token.equals(stem)) {
                            continue;
                        }
                        words++;
                        String cmd = (usefull) ? trie.getFully(token) : trie.getLastOnPath(token);
                        String stm = diff.apply(new StringBuffer(token), cmd).toString();
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
            System.out.println("tests " + words + " succ " + nobug);
        } catch (FileNotFoundException x) {
            x.printStackTrace();
        } catch (IOException x) {
            x.printStackTrace();
        }
    }
}
