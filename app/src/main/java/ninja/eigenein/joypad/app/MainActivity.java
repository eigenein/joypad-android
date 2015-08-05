package ninja.eigenein.joypad.app;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getFragmentManager().beginTransaction().replace(R.id.fragment_container, new LightFragment()).commit();
    }

    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        switch (item.getItemId()) {

            case R.id.menu_item_light:
                getFragmentManager().beginTransaction().replace(R.id.fragment_container, new LightFragment()).commit();
                return true;

            case R.id.menu_item_dark:
                getFragmentManager().beginTransaction().replace(R.id.fragment_container, new DarkFragment()).commit();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
