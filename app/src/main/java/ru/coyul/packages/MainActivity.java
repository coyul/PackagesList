package ru.coyul.packages;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import java.util.List;

public class MainActivity extends Activity {

    private ListView packagesListView;
    private PackagesAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        packagesListView = (ListView) findViewById(R.id.list_packages);
        List<PackageInfo> packages = getPackageManager().getInstalledPackages(0);
        adapter = new PackagesAdapter(packages);
        packagesListView.setAdapter(adapter);

        packagesListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                PackageInfo info = adapter.getItem(position);

                Intent startIntent = getPackageManager().getLaunchIntentForPackage(info.packageName);
                if (startIntent != null) {
                    startActivity(startIntent);
                }

                else {
                    Toast.makeText(MainActivity.this, getString(R.string.msg_error), Toast.LENGTH_LONG).show();
                }
            }
        });
    }
}
