package com.example.steve.impromptu.Main;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.steve.impromptu.R;
import com.parse.Parse;

public class ActivityMain extends FragmentActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shell_main);

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.impromptu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void stream (View view) {
        Toast.makeText(this, "show stream", Toast.LENGTH_SHORT).show();
    }

    public void map (View view) {
        Toast.makeText(this, "show map", Toast.LENGTH_SHORT).show();
    }

    public void invites (View view) {
        Toast.makeText(this, "show invites", Toast.LENGTH_SHORT).show();
    }

    public void friends (View view) {
        Toast.makeText(this, "show friends", Toast.LENGTH_SHORT).show();
    }

    public void profile (View view) {
        Toast.makeText(this, "show profile", Toast.LENGTH_SHORT).show();
    }

    public void compose (View view) {
        Toast.makeText(this, "show compose event", Toast.LENGTH_SHORT).show();
    }
}
