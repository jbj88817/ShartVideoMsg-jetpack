package us.bojie.shortvideomsg;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import us.bojie.shortvideomsg.model.Destination;
import us.bojie.shortvideomsg.model.User;
import us.bojie.shortvideomsg.ui.login.UserManager;
import us.bojie.shortvideomsg.utils.AppConfig;
import us.bojie.shortvideomsg.utils.NavGraphBuilder;
import us.bojie.shortvideomsg.view.AppBottomBar;

public class MainActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener {

    private NavController navController;
    private AppBottomBar navView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        navView = findViewById(R.id.nav_view);
        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment);
        navController = NavHostFragment.findNavController(fragment);
        NavGraphBuilder.build(navController, this, fragment.getId());
        navView.setOnNavigationItemSelectedListener(this);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        HashMap<String, Destination> destConfig = AppConfig.getDestConfig();
        for (Map.Entry<String, Destination> entry : destConfig.entrySet()) {
            Destination value = entry.getValue();
            if (value != null && !UserManager.get().isLogin() && value.isNeedLogin() && value.getId() == menuItem.getItemId()) {
                UserManager.get().login(this).observe(this, user -> navView.setSelectedItemId(menuItem.getItemId()));
                return false;
            }
        }
        navController.navigate(menuItem.getItemId());
        return !TextUtils.isEmpty(menuItem.getTitle());
    }
}
