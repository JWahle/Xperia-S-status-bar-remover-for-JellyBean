package de.jwahle.xperiasstatusbartogglesremover;

import java.io.DataOutputStream;
import java.io.IOException;

import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

class Restarter {
    
    private final Context context;

    public Restarter(Context context) {
        this.context = context;
    }
    
    
    void restart(final String packageName) {
        AsyncTask.execute(new Runnable() {
            @Override public void run() {
                restartImplNoExceptions(packageName);
            }
        });
    };
    
    private void restartImplNoExceptions(String packageName) {
        try {
            restartImpl(packageName);
        } catch (IOException e) {
            handleException();
        }
    }
    
    private void restartImpl(String packageName) throws IOException {
        Process su = Runtime.getRuntime().exec("su");
        DataOutputStream console = new DataOutputStream(su.getOutputStream());
        String killCommand = "killall " + packageName;
        execute(console, killCommand);
        execute(console, "exit");
    }

    private void execute(DataOutputStream console, String command) throws IOException {
        console.writeBytes(command);
        console.writeBytes("\n");
        console.flush();
    }
    
    private void handleException() {
        Toast.makeText(context, R.string.need_restart, Toast.LENGTH_SHORT)
        .show();
    }

}
