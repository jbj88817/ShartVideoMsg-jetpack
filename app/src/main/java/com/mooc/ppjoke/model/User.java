package com.mooc.ppjoke.model;

import java.io.Serializable;
import java.util.Objects;

public class User implements Serializable {

    /**
     * id : 962
     * userId : 3223400206308231
     * name : Come on
     * avatar : https://p3-dy.byteimg.com/img/p1056/8c50025c85244140910a513345ae7358~200x200.webp
     * description :
     * likeCount : 0
     * topCommentCount : 0
     * followCount : 0
     * followerCount : 2
     * qqOpenId : null
     * expires_time : 0
     * score : 0
     * historyCount : 0
     * commentCount : 0
     * favoriteCount : 0
     * feedCount : 0
     * hasFollow : true
     */

    private int id;
    private long userId;
    private String name;
    private String avatar;
    private String description;
    private int likeCount;
    private int topCommentCount;
    private int followCount;
    private int followerCount;
    private long qqOpenId;
    private int expires_time;
    private int score;
    private int historyCount;
    private int commentCount;
    private int favoriteCount;
    private int feedCount;
    private boolean hasFollow;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getLikeCount() {
        return likeCount;
    }

    public void setLikeCount(int likeCount) {
        this.likeCount = likeCount;
    }

    public int getTopCommentCount() {
        return topCommentCount;
    }

    public void setTopCommentCount(int topCommentCount) {
        this.topCommentCount = topCommentCount;
    }

    public int getFollowCount() {
        return followCount;
    }

    public void setFollowCount(int followCount) {
        this.followCount = followCount;
    }

    public int getFollowerCount() {
        return followerCount;
    }

    public void setFollowerCount(int followerCount) {
        this.followerCount = followerCount;
    }

    public long getQqOpenId() {
        return qqOpenId;
    }

    public void setQqOpenId(long qqOpenId) {
        this.qqOpenId = qqOpenId;
    }

    public int getExpiresTime() {
        return expires_time;
    }

    public void setExpiresTime(int expiresTime) {
        this.expires_time = expiresTime;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public int getHistoryCount() {
        return historyCount;
    }

    public void setHistoryCount(int historyCount) {
        this.historyCount = historyCount;
    }

    public int getCommentCount() {
        return commentCount;
    }

    public void setCommentCount(int commentCount) {
        this.commentCount = commentCount;
    }

    public int getFavoriteCount() {
        return favoriteCount;
    }

    public void setFavoriteCount(int favoriteCount) {
        this.favoriteCount = favoriteCount;
    }

    public int getFeedCount() {
        return feedCount;
    }

    public void setFeedCount(int feedCount) {
        this.feedCount = feedCount;
    }

    public boolean isHasFollow() {
        return hasFollow;
    }

    public void setHasFollow(boolean hasFollow) {
        this.hasFollow = hasFollow;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return id == user.id &&
                userId == user.userId &&
                likeCount == user.likeCount &&
                topCommentCount == user.topCommentCount &&
                followCount == user.followCount &&
                followerCount == user.followerCount &&
                qqOpenId == user.qqOpenId &&
                expires_time == user.expires_time &&
                score == user.score &&
                historyCount == user.historyCount &&
                commentCount == user.commentCount &&
                favoriteCount == user.favoriteCount &&
                feedCount == user.feedCount &&
                hasFollow == user.hasFollow &&
                Objects.equals(name, user.name) &&
                Objects.equals(avatar, user.avatar) &&
                Objects.equals(description, user.description);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, userId, name, avatar, description, likeCount, topCommentCount, followCount, followerCount, qqOpenId, expires_time, score, historyCount, commentCount, favoriteCount, feedCount, hasFollow);
    }
}
