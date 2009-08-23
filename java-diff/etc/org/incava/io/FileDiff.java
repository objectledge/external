package org.incava.io;

import java.io.*;
import java.util.*;
import org.incava.util.diff.*;


public class FileDiff
{
    public FileDiff(String fromFile, String toFile)
    {
        String[]         aLines = read(fromFile);
        String[]         bLines = read(toFile);
        List<Difference> diffs  = (new Diff<String>(aLines, bLines)).diff();
        
        for (Difference diff : diffs) {
            int        delStart = diff.getDeletedStart();
            int        delEnd   = diff.getDeletedEnd();
            int        addStart = diff.getAddedStart();
            int        addEnd   = diff.getAddedEnd();
            String     from     = toString(delStart, delEnd);
            String     to       = toString(addStart, addEnd);
            String     type     = delEnd != Difference.NONE && addEnd != Difference.NONE ? "c" : (delEnd == Difference.NONE ? "a" : "d");

            System.out.println(from + type + to);

            if (delEnd != Difference.NONE) {
                printLines(delStart, delEnd, "<", aLines);
                if (addEnd != Difference.NONE) {
                    System.out.println("---");
                }
            }
            if (addEnd != Difference.NONE) {
                printLines(addStart, addEnd, ">", bLines);
            }
        }
    }

    protected void printLines(int start, int end, String ind, String[] lines)
    {
        for (int lnum = start; lnum <= end; ++lnum) {
            System.out.println(ind + " " + lines[lnum]);
        }
    }

    protected String toString(int start, int end)
    {
        // adjusted, because file lines are one-indexed, not zero.

        StringBuffer buf = new StringBuffer();

        // match the line numbering from diff(1):
        buf.append(end == Difference.NONE ? start : (1 + start));
        
        if (end != Difference.NONE && start != end) {
            buf.append(",").append(1 + end);
        }
        return buf.toString();
    }

    protected String[] read(String fileName)
    {
        try {
            BufferedReader br       = new BufferedReader(new FileReader(fileName));
            List<String>   contents = new ArrayList<String>();
            
            String in;
            while ((in = br.readLine()) != null) {
                contents.add(in);
            }
            return (String[])contents.toArray(new String[] {});
        }
        catch (Exception e) {
            System.err.println("error reading " + fileName + ": " + e);
            System.exit(1);
            return null;
        }        
    }

    public static void main(String[] args)
    {
        if (args.length == 2) {
            new FileDiff(args[0], args[1]);
        }
        else {
            System.err.println("usage: org.incava.io.FileDiff from-file to-file");
        }
    }

}
