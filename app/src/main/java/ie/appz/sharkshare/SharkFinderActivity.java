package ie.appz.sharkshare;

import android.app.Activity;
import android.content.ClipboardManager;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;

import java.util.ArrayList;
import java.util.List;

import ie.appz.sharkshare.adapters.SongAdapter;
import ie.appz.sharkshare.models.SongDetail;
import ie.appz.sharkshare.service.SharkFinderService;


public class SharkFinderActivity extends Activity {


    private List<SongDetail> songDetailList = new ArrayList<SongDetail>();
    private SongAdapter songAdapter = new SongAdapter();
    private ClipboardManager clipboard;
    private WindowManager windowManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = new Intent(this, SharkFinderService.class);
        intent.putExtras(getIntent().getExtras());

        startService(intent);
        finish();

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.shark_finder, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


}
