package client.operator;

import client.model.FileWaitingQueue;
import client.model.SocketConnection;

public abstract class ThreadFileOperator extends Thread{
    FileWaitingQueue fwq;
    SocketConnection sc;
    boolean stop;
    int pause;

    public ThreadFileOperator(FileWaitingQueue fwq, SocketConnection sc){
        this.fwq = fwq;
        this.sc = sc;
        this.stop = false;
        this.pause = -1;
    }

    @Override
    abstract public void run();
    abstract public void stopThread();
    public void setPause(int nbPause){
        this.pause = nbPause;
    }

    public void pause(){
        try{
            System.out.println(getName()+" en pause pendant : "+ pause/1000 +"s");
            sleep(pause);
            System.out.println(getName()+" pause finis");
            this.pause = -1;
        } catch (InterruptedException e) {
            System.out.println(getName()+" pause finis");
            this.pause = -1;
        }
    }
}
