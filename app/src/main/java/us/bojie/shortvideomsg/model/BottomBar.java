package us.bojie.shortvideomsg.model;

import java.util.List;

public class BottomBar {


    /**
     * activeColor : #333333
     * inActiveColor : #666666
     * tabs : [{"size":24,"enable":true,"index":0,"pageUrl":"main/tabs/home","title":"Main"},{"size":24,"enable":true,"index":1,"pageUrl":"main/tabs/sofa","title":"Sofa"},{"size":40,"enable":true,"index":2,"tintColor":"#ff678f","pageUrl":"main/tabs/publish","title":""},{"size":24,"enable":true,"index":3,"pageUrl":"main/tabs/find","title":"Find"},{"size":24,"enable":true,"index":4,"pageUrl":"main/tabs/my","title":"Me"}]
     */

    private String activeColor;
    private String inActiveColor;
    private List<Tabs> tabs;

    public String getActiveColor() {
        return activeColor;
    }

    public void setActiveColor(String activeColor) {
        this.activeColor = activeColor;
    }

    public String getInActiveColor() {
        return inActiveColor;
    }

    public void setInActiveColor(String inActiveColor) {
        this.inActiveColor = inActiveColor;
    }

    public List<Tabs> getTabs() {
        return tabs;
    }

    public void setTabs(List<Tabs> tabs) {
        this.tabs = tabs;
    }

    public static class Tabs {
        /**
         * size : 24
         * enable : true
         * index : 0
         * pageUrl : main/tabs/home
         * title : Main
         * tintColor : #ff678f
         */

        private int size;
        private boolean enable;
        private int index;
        private String pageUrl;
        private String title;
        private String tintColor;

        public int getSize() {
            return size;
        }

        public void setSize(int size) {
            this.size = size;
        }

        public boolean isEnable() {
            return enable;
        }

        public void setEnable(boolean enable) {
            this.enable = enable;
        }

        public int getIndex() {
            return index;
        }

        public void setIndex(int index) {
            this.index = index;
        }

        public String getPageUrl() {
            return pageUrl;
        }

        public void setPageUrl(String pageUrl) {
            this.pageUrl = pageUrl;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getTintColor() {
            return tintColor;
        }

        public void setTintColor(String tintColor) {
            this.tintColor = tintColor;
        }
    }
}
