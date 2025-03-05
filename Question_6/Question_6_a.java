package Question_6;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * This class coordinates three threads to print the sequence "0102030405..." up to a specified number n.
 * 
 * Algorithm Overview:
 * 1. Use a ReentrantLock and three Conditions (zero, odd, even) to synchronize threads.
 * 2. Track the current state (ZERO, ODD, EVEN) and the current number to print.
 * 3. The ZeroThread prints 0, then triggers OddThread or EvenThread based on the current number's parity.
 * 4. OddThread and EvenThread print their respective numbers, increment the current number, and trigger ZeroThread.
 * 5. All threads terminate gracefully after printing numbers up to n.
 */
class NumberPrinter {
    public void printZero() { System.out.print("0"); }
    public void printEven(int num) { System.out.print(num); }
    public void printOdd(int num) { System.out.print(num); }
}

class ThreadController {
    private final int n;
    private final NumberPrinter printer;
    private final Lock lock = new ReentrantLock();
    private final Condition zeroCondition = lock.newCondition();
    private final Condition oddCondition = lock.newCondition();
    private final Condition evenCondition = lock.newCondition();
    
    private enum State { ZERO, ODD, EVEN }
    private State currentState = State.ZERO;
    private int currentNumber = 1;

    public ThreadController(int n, NumberPrinter printer) {
        this.n = n;
        this.printer = printer;
    }

    /**
     * Starts and coordinates the three threads (ZeroThread, OddThread, EvenThread).
     */
    public void start() {
        Thread zeroThread = new Thread(this::zeroTask);
        Thread oddThread = new Thread(this::oddTask);
        Thread evenThread = new Thread(this::evenTask);

        zeroThread.start();
        oddThread.start();
        evenThread.start();

        try {
            zeroThread.join();
            oddThread.join();
            evenThread.join();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    /**
     * Task for ZeroThread: Prints 0 exactly n times and triggers the next thread.
     */
    private void zeroTask() {
        for (int i = 0; i < n; i++) {
            lock.lock();
            try {
                while (currentState != State.ZERO) {
                    zeroCondition.await();
                }
                printer.printZero();
                currentState = (currentNumber % 2 == 1) ? State.ODD : State.EVEN;
                if (currentState == State.ODD) {
                    oddCondition.signal();
                } else {
                    evenCondition.signal();
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            } finally {
                lock.unlock();
            }
        }
        // Signal all threads to terminate after completing n zeros
        lock.lock();
        try {
            currentNumber = n + 1;
            oddCondition.signalAll();
            evenCondition.signalAll();
        } finally {
            lock.unlock();
        }
    }

    /**
     * Task for OddThread: Prints odd numbers when triggered.
     */
    private void oddTask() {
        while (true) {
            lock.lock();
            try {
                if (currentNumber > n) break;
                while (currentState != State.ODD || currentNumber % 2 != 1) {
                    oddCondition.await();
                    if (currentNumber > n) break;
                }
                if (currentNumber > n) break;
                printer.printOdd(currentNumber);
                currentNumber++;
                currentState = State.ZERO;
                zeroCondition.signal();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            } finally {
                lock.unlock();
            }
        }
    }

    /**
     * Task for EvenThread: Prints even numbers when triggered.
     */
    private void evenTask() {
        while (true) {
            lock.lock();
            try {
                if (currentNumber > n) break;
                while (currentState != State.EVEN || currentNumber % 2 != 0) {
                    evenCondition.await();
                    if (currentNumber > n) break;
                }
                if (currentNumber > n) break;
                printer.printEven(currentNumber);
                currentNumber++;
                currentState = State.ZERO;
                zeroCondition.signal();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            } finally {
                lock.unlock();
            }
        }
    }
}

// Test Class
public class Question_6_a {
    public static void main(String[] args) {
        int n = 5;
        NumberPrinter printer = new NumberPrinter();
        ThreadController controller = new ThreadController(n, printer);
        controller.start(); // Output: 0102030405
    }
}

/*
Summary:
- The code uses synchronized threads to print the sequence "0102030405..." up to n.
- ZeroThread prints 0s and triggers Odd/EvenThread based on the current number.
- Odd/EvenThreads print their respective numbers and trigger ZeroThread.
- Tested with n=5, the output matches the example "0102030405".
- The algorithm ensures correct synchronization with locks/conditions, preventing race conditions.
- Code is structured with clear comments, follows Java standards, and handles thread termination gracefully.
*/