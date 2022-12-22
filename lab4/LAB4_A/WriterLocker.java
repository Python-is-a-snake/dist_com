package LAB4_A;
import java.util.Map;

public class WriterLocker {
    private Map<Thread, Integer> threadsToRead;
    private int writeAccesses = 0;
    private int writeRequests = 0;
    private Thread writingThread = null;

    public synchronized void readingLock() throws InterruptedException{
        Thread callingThread = Thread.currentThread();
        while(! canGrantReadAccess(callingThread)){
            wait();
        }
        threadsToRead.put(callingThread,
                (getReadAccessCount(callingThread) + 1));
    }
    private boolean canGrantReadAccess(Thread callingThread){
        if( beingWriter(callingThread)) return true;
        if( hasWriter()) return false;
        if( beingReader(callingThread)) return true;
        if( hasPermissionToWrite()) return false;
        return true;
    }

    public synchronized void readingUnlock(){
        Thread currentThread = Thread.currentThread();
        if(!beingReader(currentThread)){
            throw new IllegalMonitorStateException(" ERROR OCCURRED ");
        }
        int accessCount = getReadAccessCount(currentThread);
        if(accessCount == 1){
            threadsToRead.remove(currentThread);
        }
        else {
            threadsToRead.put(currentThread, (accessCount -1));
        }
        notifyAll();
    }

    public synchronized void writingLock() throws InterruptedException{
        writeRequests++;
        Thread currentThread = Thread.currentThread();
        while(! canGrantWriteAccess(currentThread)){
            wait();
        }
        writeRequests--;
        writeAccesses++;
        writingThread = currentThread;
    }
    public synchronized void writingUnlock() throws InterruptedException{
        if(!beingWriter(Thread.currentThread())){
            throw new IllegalMonitorStateException(" ERROR OCCURRED ");
        }
        writeAccesses--;
        if(writeAccesses == 0){
            writingThread = null;
        }
        notifyAll();
    }

    private boolean canGrantWriteAccess(Thread callingThread){
        if(beingOnlyReader(callingThread))return true;
        if(hasReaders())return false;
        if(writingThread == null)return true;
        if(!beingWriter(callingThread))return false;
        return true;
    }

    private int getReadAccessCount(Thread callingThread){
        Integer accessCount = threadsToRead.get(callingThread);
        if(accessCount == null) return 0;
        return accessCount.intValue();
    }
    private boolean hasPermissionToWrite(){
        return this.writeRequests > 0;
    }
    private boolean hasReaders(){
        return threadsToRead.size() > 0;
    }
    private boolean beingReader(Thread callingThread){
        return threadsToRead.get(callingThread) != null;
    }
    private boolean beingOnlyReader(Thread callingThread){
        return threadsToRead.size() == 1 &&
                threadsToRead.get(callingThread) != null;
    }
    private boolean hasWriter(){
        return writingThread != null;
    }
    private boolean beingWriter(Thread callingThread){
        return writingThread == callingThread;
    }

}
