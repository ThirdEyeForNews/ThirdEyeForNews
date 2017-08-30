package com.demo.thirdeye.utility;

/**
 * Created by ammu on 8/26/2017.
 */

public interface SmsListener {
    public void messageReceived(String sender,String messageText);
}
