package us.bojie.shortvideomsg.model;

import java.io.Serializable;
import java.util.Objects;

public class Feed implements Serializable {

    public static final int TYPE_IMAGE = 1;
    public static final int TYPE_VIDEO = 2;
    /**
     * id : 378
     * itemId : 6760496353719097000
     * itemType : 2
     * createTime : 1574050718
     * duration : 12.634
     * feeds_text : #life common
     * authorId : 2651618079414472
     * activityIcon : null
     * activityText : null
     * width : 576
     * height : 1024
     * url : https://pipijoke.oss-cn-hangzhou.aliyuncs.com/6760496353719097603.mp4
     * cover : https://p3-dy.byteimg.com/img/mosaic-legacy/2e2c0000cef503944e331~576x1024_q80.webp
     */

    private int id;
    private long itemId;
    private int itemType;
    private int createTime;
    private double duration;
    private String feeds_text;
    private long authorId;
    private String activityIcon;
    private String activityText;
    private int width;
    private int height;
    private String url;
    private String cover;
    private User author;
    private Comment topComment;
    private Ugc ugc;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public long getItemId() {
        return itemId;
    }

    public void setItemId(long itemId) {
        this.itemId = itemId;
    }

    public int getItemType() {
        return itemType;
    }

    public void setItemType(int itemType) {
        this.itemType = itemType;
    }

    public int getCreateTime() {
        return createTime;
    }

    public void setCreateTime(int createTime) {
        this.createTime = createTime;
    }

    public double getDuration() {
        return duration;
    }

    public void setDuration(double duration) {
        this.duration = duration;
    }

    public String getFeeds_text() {
        return feeds_text;
    }

    public void setFeeds_text(String feeds_text) {
        this.feeds_text = feeds_text;
    }

    public long getAuthorId() {
        return authorId;
    }

    public void setAuthorId(long authorId) {
        this.authorId = authorId;
    }

    public String getActivityIcon() {
        return activityIcon;
    }

    public void setActivityIcon(String activityIcon) {
        this.activityIcon = activityIcon;
    }

    public String getActivityText() {
        return activityText;
    }

    public void setActivityText(String activityText) {
        this.activityText = activityText;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getCover() {
        return cover;
    }

    public void setCover(String cover) {
        this.cover = cover;
    }

    public User getAuthor() {
        return author;
    }

    public void setAuthor(User author) {
        this.author = author;
    }

    public Comment getTopComment() {
        return topComment;
    }

    public void setTopComment(Comment topComment) {
        this.topComment = topComment;
    }

    public Ugc getUgc() {
        return ugc;
    }

    public void setUgc(Ugc ugc) {
        this.ugc = ugc;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Feed feed = (Feed) o;
        return id == feed.id &&
                itemId == feed.itemId &&
                itemType == feed.itemType &&
                createTime == feed.createTime &&
                Double.compare(feed.duration, duration) == 0 &&
                authorId == feed.authorId &&
                width == feed.width &&
                height == feed.height &&
                Objects.equals(feeds_text, feed.feeds_text) &&
                Objects.equals(activityIcon, feed.activityIcon) &&
                Objects.equals(activityText, feed.activityText) &&
                Objects.equals(url, feed.url) &&
                Objects.equals(cover, feed.cover) &&
                Objects.equals(author, feed.author) &&
                Objects.equals(topComment, feed.topComment) &&
                Objects.equals(ugc, feed.ugc);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, itemId, itemType, createTime, duration, feeds_text, authorId, activityIcon, activityText, width, height, url, cover, author, topComment, ugc);
    }
}
