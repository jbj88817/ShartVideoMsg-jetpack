package com.mooc.ppjoke.utils;

import android.content.res.AssetManager;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.mooc.ppjoke.model.BottomBar;
import com.mooc.ppjoke.model.Destination;
import com.mooc.ppjoke.model.SofaTab;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Collections;
import java.util.HashMap;

import us.bojie.libcommon.AppGlobals;

public class AppConfig {

    private static HashMap<String, Destination> sDestConfig;

    private static BottomBar sBottomBar;
    private static SofaTab sSofaTab, sFindTabConfig;

    public static HashMap<String, Destination> getDestConfig() {
        if (sDestConfig == null) {
            String content = parseFile("destination.json");
            sDestConfig = JSON.parseObject(content, new TypeReference<HashMap<String, Destination>>() {
            }.getType());
        }

        return sDestConfig;
    }

    public static BottomBar getsBottomBar() {
        if (sBottomBar == null) {
            String content = parseFile("main_tabs_config.json");
            sBottomBar = JSON.parseObject(content, BottomBar.class);
        }
        return sBottomBar;
    }

    public static SofaTab getsSofaTab() {
        if (sSofaTab == null) {
            String content = parseFile("sofa_tabs_config.json");
            sSofaTab = JSON.parseObject(content, SofaTab.class);
            Collections.sort(sSofaTab.tabs, (o1, o2) -> o1.index < o2.index ? -1 : 1);
        }
        return sSofaTab;
    }

    public static SofaTab getsFindTab() {
        if (sFindTabConfig == null) {
            String content = parseFile("find_tabs_config.json");
            sFindTabConfig = JSON.parseObject(content, SofaTab.class);
            Collections.sort(sFindTabConfig.tabs, (o1, o2) -> o1.index < o2.index ? -1 : 1);
        }
        return sFindTabConfig;
    }

    private static String parseFile(String fileName) {
        AssetManager assets = AppGlobals.getApplication().getResources().getAssets();
        InputStream is = null;
        BufferedReader reader = null;
        StringBuilder sb = new StringBuilder();
        try {
            is = assets.open(fileName);
            reader = new BufferedReader(new InputStreamReader(is));
            String line = null;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (is != null) {
                    is.close();
                }

                if (reader != null) {
                    reader.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
        return sb.toString();
    }

}
