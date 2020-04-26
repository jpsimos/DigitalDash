package psimos.jacob.BLE112;

import android.util.Log;

import java.util.LinkedList;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;

/**
 * Created by JacobPsimos on 2/7/2017.
 */

public class BLECommandQueue {

    private BLEConnection connection = null;
    private LinkedList<BLECommand> commandQueue = null;

    private Semaphore commandLock = new Semaphore(1, true);
    private Executor commandExecutor = Executors.newSingleThreadExecutor();


    public BLECommandQueue(BLEConnection connection){
        this.commandQueue = new LinkedList<BLECommand>();
        this.connection = connection;
    }

    protected void queueCommand(BLECommand command){

        Log.d("ble112", "Queued Command");
        synchronized (commandQueue){
            commandQueue.add(command);
            commandExecutor.execute(new ExecuteCommandRunnable(command));
        }
    }

    protected void dequeueCommand(){
        synchronized (commandQueue) {
            if (!commandQueue.isEmpty()) {
                commandQueue.pop();
                commandLock.release();
            }
        }
    }


    class ExecuteCommandRunnable implements Runnable{
        private BLECommand command = null;
        public ExecuteCommandRunnable(BLECommand command){
            this.command = command;
        }

        @Override
        public void run() {
            commandLock.acquireUninterruptibly();
            command.execute(connection.gatt);
        }
    }   /* End ExecuteCommandRunnable class */
}


