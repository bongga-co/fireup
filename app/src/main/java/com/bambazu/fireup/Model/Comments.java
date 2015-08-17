package com.bambazu.fireup.Model;

/**
 * Created by Sil on 15/08/2015.
 */
public class Comments {
    private int icon;
    private String nick;
    private String date;
    private String post;
    private int rating;

    public Comments(String nick, String date, String post, int rating){
        this.nick = nick;
        this.date = date;
        this.post = post;
        this.rating = rating;
    }

    public Comments(int icon, String nick, String date, String post, int rating){
        this.icon = icon;
        this.nick = nick;
        this.date = date;
        this.post = post;
        this.rating = rating;
    }

    public int getIcon() {
        return icon;
    }

    public void setIcon(int icon) {
        this.icon = icon;
    }

    public String getNick() {
        return nick;
    }

    public void setNick(String nick) {
        this.nick = nick;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getPost() {
        return post;
    }

    public void setPost(String post) {
        this.post = post;
    }

    public int getRating(){
        return rating;
    }

    public void setRating(int rating){
        this.rating = rating;
    }
}
