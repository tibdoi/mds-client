package org.datacite.mds.client.ui.workers;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.swing.SwingWorker;

import org.datacite.mds.client.service.MdsApi;
import org.datacite.mds.client.ui.LogPanel;

public abstract class Worker<T> extends SwingWorker<Void, String> {

    MdsApi mdsApi = MdsApi.getInstance();

    LogPanel logPanel;
    
    List<T> list = new ArrayList<T>();

    String name;

    int failed = 0;
    int succeeded = 0 ;
    
    public Worker(String name) {
        super();
        this.name = name;
    }

    public void setLogPanel(LogPanel logPanel) {
        this.logPanel = logPanel;
    }

    public void setList(List<T> list) {
        this.list = list;
    }

    void log(Object obj) {
        if (logPanel != null) {
            logPanel.log(obj);
        }
    }
    
    @Override
    protected Void doInBackground() {
        int size = list.size();
        log("==========");
        log("starting job \"" + name + "\" (" + size + " elements)");
        setProgress(0);
        Iterator<T> iterator = list.iterator();
        for (int i = 0; iterator.hasNext() && !isCancelled(); i++) {
            int progress = 100 * i / size; 
            setProgress(progress);
            
            T elem = iterator.next();
            try {
                doInBackground(elem);
                succeeded++;
            } catch (Exception e) {
                failed++;
                log("failed: " + e.getMessage());
            }
        }
        return null;
    }
    
    abstract void doInBackground(T elem) throws Exception ;

    @Override
    protected void done() {
        // TODO Auto-generated method stub
        log("");
        if (isCancelled()) {
            log("job was cancelled");
        } else {
            log("job finished normally");
            setProgress(100);
        }
        logStats();
    }
    
    private void logStats() {
        int total = list.size();
        int processed = succeeded + failed;
        log("processed instructions: " + processed + "/" + total);
        log("successful: " + succeeded);
        log("failed: " + failed);
    }

}
