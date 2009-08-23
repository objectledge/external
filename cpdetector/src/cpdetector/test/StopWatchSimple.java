/**
 * StopWatchSimple, a basic implementation of the interface IStopWatch 
 * meant for measurements of short times (>hours). 
 * Copyright (C) 2002  Achim Westermann, Achim.Westermann@gmx.de
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Library General Public
 * License as published by the Free Software Foundation; either
 * version 2 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Library General Public License for more details.
 *
 * You should have received a copy of the GNU Library General Public
 * License along with this library; if not, write to the Free
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 *
 *  If you modify or optimize the code in a useful way please let me know. 
 *  Achim.Westermann@gmx.de
 */

package cpdetector.test;

/**
 *  A very simple implementation of the IStopWatch that does nothing more
 *  than the interface describes. Only the start - value, the running - boolean
 *  and the summation of runtimes is hold: Little RAM - consumption and little
 *  calculations here.
 *
 * @author  <a href='mailto:Achim.Westermann@gmx.de'>Achim Westermann</a>
 * @version 1.1
 */
public class StopWatchSimple implements IStopWatch{
    protected long start;
    protected long allms;
    protected boolean running = false;
    
    /**
     * Creates new StopWatchSimple. The measurement of time is not started here.
     */
    public StopWatchSimple() {
    }
    
    /**
     * Creates new StopWatchSimple. The measurement of time is started if param is true.
     */
    public StopWatchSimple(boolean start) {
        if(start)start();
    }
    
    /**
     * This method may serve two purposes:<br>
     * <ul>
     * <li>
     * Running- Reset: <br>
     * The start- value of the measurement is set to the current time, even if
     * the StopWatch is running.
     * </li>
     * <li>
     * Next- measurement- Reset:<br>
     * A flag is set which causes the next call to start to delete the old
     * start - value.
     * </li>
     * </ul>
     * Note that continuous calls to <code>start()</code> and <code>stop()</code>
     * without calling reset will always relate the measurement of time to the
     * first time <code>start()</code> was called!
     */
    public final void reset() {
        this.start = System.currentTimeMillis();
        this.allms = 0;
    }
    
    /**
     * Sets the state to running. This allows a call to <code>stop()</code>
     * to make a new measurement by taking the current time.
     * If <code>reset()</code> was invoked before, the start - time is set
     * to the return value of <code>System.currentTimeMillis()</code>.
     * Else the old value is preserved.
     * False is returned if a measurement is already in progress.
     * <b> A call to start will only start a new measurement with the current
     * Time, if it is the first run or reset was called before. Else the time
     * kept after the next call to stop will be the sum of all previous runtimes.
     */
    public final boolean start(){
        if(!running){
            this.start = System.currentTimeMillis();
            this.running = true;
            return true;
        }
        return false;
    }
    
    /**
     * This method does not change the state from running to !running but performs
     * an update of the overall measurement- data inside.
     * The difference to <code>stop()<code>:<br>
     * After <code>stop()</code> has been called the state is set to !running which
     * causes a new start-value to be set during the next call to <code>start()</code>.
     * The call to <code>snapShot()</code> does not switch the state.
     * If afterwards <code>start()</code> is called, no new value gets assigned
     * to the start- value of the StopWatch. Despite of this <code>snapShot</code>
     * Adds the period from the start-value to now to the internal total measurement-
     * call. To avoid double - summation of the same time- periods a new start- value
     * is set directly.
     */
    public long snapShot() {
        long restart = System.currentTimeMillis();
        this.allms += restart-this.start;
        this.start = restart;
        return allms;
    }
    
    /**
     * Stops the measurement by assinging current time in ms to the stop value.
     * Return true if a valid measurement has been made (StopWatch was running before invoking this method).
     */
    public synchronized boolean stop() {
        if(this.running){
            // filling internal data
            this.allms += System.currentTimeMillis() - this.start;
            this.running = false;
            return true;
        }
        return false;
    }
    
    /**
     * Returns the current value of the IStopWatch in ms.
     * This has to be the sum of all previous measurements (circles  of <code>start()-stop()</code>)
     * not interrupted by calls to <code>reset()</code>.
     */
    public final long getPureMilliSeconds() {
        if(running)
            this.snapShot();
        return this.allms;
    }
    
    public String toString(){
        if(running)
            this.snapShot();
        return String.valueOf(allms)+" ms";
    }

    public static void main(String[]args){
        try{
            System.out.println("Stopping the sleep for 5000 ms");
            final IStopWatch test = new StopWatchSimple(true);
            Thread.sleep(5000);
            test.stop();
            System.out.println("Stopped the StopWatch: ");
            System.out.println(test.toString());
            System.out.println("Stopping the next sleep of 4000 ms (no reset was called!).");
            System.out.println("    test.start():"+test.start());
            Thread.sleep(4000);
            System.out.println("    test.stop(): "+test.stop());
            System.out.println("Stopped the StopWatch: ");
            System.out.println(test.toString());
            System.out.println("Calling reset(). ");
            test.reset();
            System.out.println("Stopping a sleep of 3000 ms.");
            System.out.println("    test.start(): "+test.start());
            Thread.sleep(3000);
            System.out.println("    test.stop(): "+test.stop());
            System.out.println("Stopped the StopWatch: ");
            System.out.println(test.toString());   
            System.out.println("Calling reset()");
            test.reset();
            new Thread(){
                public void run(){
                    for(int i=0;i<3;i++){
                        try{
                            sleep(1500);
                        }catch(InterruptedException e){
                            e.printStackTrace();
                        }
                        test.snapShot(); 
                        System.out.println(this.getName()+" performed a snapShot on "+test);
                    }
                }
            }.start();
            System.out.println("Stopping a sleep of 10000 ms. A Thread has been started to take 3 snapShots!");
            System.out.println("    test.start(): "+test.start());
            Thread.sleep(10000);
            System.out.println("    test.stop(): "+test.stop());
            System.out.println("Stopped the StopWatch: ");
            System.out.println(test.toString());   
        }catch(Throwable f){
            f.printStackTrace();
        }
    }

}
