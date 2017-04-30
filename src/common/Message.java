/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package common;

/**
 *
 * @author root
 */
public class Message {
    private String username;
    private String msg;

    public Message(String username, String msg) {
        this.username = username;
        this.msg = msg;
    }

    public String getUsername() {
        return username;
    }

    public String getMsg() {
        return msg;
    }
}
