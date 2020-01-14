package com.mooc.ppjoke.model;

import java.util.List;

public class SofaTab {

    /**
     * activeSize : 16
     * normalSize : 14
     * activeColor : #ED7282
     * normalColor : #666666
     * select : 0
     * tabGravity : 0
     * tabs : [{"title":"Pics","index":0,"tag":"pics","enable":true},{"title":"Video","index":1,"tag":"video","enable":true},{"title":"Text","index":2,"tag":"text","enable":true}]
     */

    public int activeSize;
    public int normalSize;
    public String activeColor;
    public String normalColor;
    public int select;
    public int tabGravity;
    public List<Tabs> tabs;

    public static class Tabs {
        /**
         * title : Pics
         * index : 0
         * tag : pics
         * enable : true
         */

        public String title;
        public int index;
        public String tag;
        public boolean enable;
    }
}
