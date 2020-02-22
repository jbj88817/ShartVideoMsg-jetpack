package com.mooc.ppjoke;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.mooc.ppjoke.model.Destination;
import com.mooc.ppjoke.ui.login.UserManager;
import com.mooc.ppjoke.utils.AppConfig;
import com.mooc.ppjoke.utils.NavGraphBuilder;
import com.mooc.ppjoke.utils.StatusBar;
import com.mooc.ppjoke.view.AppBottomBar;

import java.util.HashMap;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

public class MainActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener {

    private NavController navController;
    private AppBottomBar navView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme);
        StatusBar.fitSystemBar(this);
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

    @Override
    public void onBackPressed() {
//        boolean shouldIntercept = false;
//        int homeDestinationId = 0;
//        Fragment fragment = getSupportFragmentManager().getPrimaryNavigationFragment();
//        String tag = fragment.getTag();
//        int currentPageDestId = Integer.parseInt(tag);
//
//        HashMap<String, Destination> destConfig = AppConfig.getDestConfig();
//        Set<String> keySet = destConfig.keySet();
//        for (String key : keySet) {
//            Destination destination = destConfig.get(key);
//            if (!destination.isAsStarter() && destination.getId() != currentPageDestId) {
//                shouldIntercept = true;
//            }
//
//            if (destination.isAsStarter()) {
//                homeDestinationId = destination.getId();
//            }
//        }
//
//        if (shouldIntercept && homeDestinationId > 0) {
//            navView.setSelectedItemId(homeDestinationId);
//            return;
//        }
//        super.onBackPressed();

        int currentPageId = navController.getCurrentDestination().getId();
        int homeDestId = navController.getGraph().getStartDestination();
        if (currentPageId != homeDestId) {
            navView.setSelectedItemId(homeDestId);
            return;
        }
        finish();
    }
}
