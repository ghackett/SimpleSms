package com.episode6.android.simplesms.app.service;

import org.droidkit.DroidKit;
import org.droidkit.app.WakefulIntentService;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Typeface;
import android.telephony.SmsMessage;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.StyleSpan;

import com.episode6.android.simplesms.R;
import com.episode6.android.simplesms.app.activity.ConvoListActivity;
import com.episode6.android.simplesms.app.util.ContactUtil;
import com.episode6.android.simplesms.provider.Telephony;

public class NotificationService extends WakefulIntentService {
    
    public static final String TAG = "SimpleNotificationService";
    public static final int NOTIFY_NEW_MESSAGE_ID = R.layout.main;

    public NotificationService() {
        super(TAG);
    }

    @Override
    public String getTag() {
        return TAG;
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        try {
            SpannableString ticker = null;
            String title = null;
            CharSequence content = null;
            
            SmsMessage[] messages = Telephony.Sms.Intents.getMessagesFromIntent(intent);
            if (messages != null) {
                
                if (messages.length >= 1) {
                    String displayName = ContactUtil.getContactName(messages[0].getDisplayOriginatingAddress()); 
                    String msgBody = messages[0].getDisplayMessageBody();
                    ticker = new SpannableString(displayName + ": " + msgBody);
                    ticker.setSpan(new StyleSpan(Typeface.BOLD), 0, displayName.length()+1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                
                    InboxDetail detail = getUnreadDetail(messages.length, messages[0].getDisplayOriginatingAddress(), msgBody);
                    if (detail.samePerson) {
                        if (detail.unreadCount > 1) {
                            title = DroidKit.getString(R.string.same_person_multi_title, displayName, detail.unreadCount);
                        } else {
                            title = displayName;
                        }
                        content = msgBody;
                    } else {
                        title = DroidKit.getString(R.string.unread_notif_format, detail.unreadCount);
                        content = ticker;
                    }
            
                    Notification notif = new Notification(R.drawable.stat_notify_sms, ticker, System.currentTimeMillis());
                    notif.setLatestEventInfo(getApplicationContext(), title, content, PendingIntent.getActivity(getApplicationContext(), 0, new Intent(getApplicationContext(), ConvoListActivity.class), PendingIntent.FLAG_UPDATE_CURRENT));
                    notif.defaults = Notification.DEFAULT_ALL;
                    notif.flags = Notification.FLAG_AUTO_CANCEL;
                    
                    getNotificationManager().notify(NOTIFY_NEW_MESSAGE_ID, notif);
                }
            }
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }
    
    private static NotificationManager getNotificationManager() {
        return (NotificationManager) DroidKit.getSystemService(Context.NOTIFICATION_SERVICE);
    }
    
    public static void cancelMessageNotification() {
        getNotificationManager().cancel(NOTIFY_NEW_MESSAGE_ID);
    }
    
    
    private InboxDetail getUnreadDetail(int newMsgCount, String addr, String body) {
        InboxDetail rtr = new InboxDetail();
        Cursor c = DroidKit.getContentResolver().query(Telephony.Sms.Inbox.CONTENT_URI, new String[] {Telephony.Sms.Inbox.ADDRESS, Telephony.Sms.Inbox.BODY}, Telephony.Sms.Inbox.READ + "=0", null, null);
        if (c != null) {
            rtr.unreadCount = c.getCount() + newMsgCount;
            while (c.moveToNext()) {
                if (!addr.equals(c.getString(0))) {
                    rtr.samePerson = false;
                } else if (body != null && body.equals(c.getString(1))) {
                    rtr.unreadCount--;
                }
            }
            c.close();
        }
        return rtr;
    }
    
    private static class InboxDetail {
        public int unreadCount;
        public boolean samePerson;
        
        public InboxDetail() {
            unreadCount = 0;
            samePerson = true;
        }
    }

}
