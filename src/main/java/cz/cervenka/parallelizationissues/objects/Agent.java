package cz.cervenka.parallelizationissues.objects;

public class Agent {

    private boolean action;

    public synchronized void act() {
        this.action = !this.action;
    }

    public synchronized boolean isActing() {
        return !this.action;
    }
}
