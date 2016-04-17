package technology.mainthread.apps.gatekeeper.view;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Handler;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;

import javax.inject.Inject;
import javax.inject.Singleton;

import technology.mainthread.apps.gatekeeper.R;
import technology.mainthread.apps.gatekeeper.data.VibratorTunes;
import technology.mainthread.apps.gatekeeper.service.GatekeeperIntentService;
import technology.mainthread.apps.gatekeeper.view.activity.MainActivity;
import timber.log.Timber;

@Singleton
public class NotifierHelper {

    // Notification IDs
    private static final int ID_HANDSET_CALLING = 1;
    private static final int ID_UNLOCKED = 2;
    private static final int ID_HANDSET_CALLED = 3;
    private static final int ID_PRIMED = 4;

    private final Context context;
    private final Resources resources;
    private final NotificationManagerCompat notificationManager;
    private final SharedPreferences preferences;
    private final Handler handler;

    // Cancels calling notification
    private Runnable cancelNotificationRunnable = new Runnable() {
        public void run() {
            Timber.d("handset call timed out");
            notificationManager.cancel(ID_HANDSET_CALLING);
            notifyHandsetCalled();
        }
    };

    @Inject
    public NotifierHelper(Context context, Resources resources, NotificationManagerCompat notificationManager, SharedPreferences preferences) {
        this.context = context;
        this.resources = resources;
        this.notificationManager = notificationManager;
        this.preferences = preferences;
        this.handler = new Handler();
    }

    // Public methods

    /**
     * Displays a handset calling notification will cancel after a minute
     */
    public void notifyHandsetCalling() {
        Timber.d("notifyHandsetCalling");
        notificationManager.cancelAll();

        PendingIntent unlockPendingIntent = PendingIntent.getService(context, 0, GatekeeperIntentService.getUnlockGatekeeperIntent(context), PendingIntent.FLAG_UPDATE_CURRENT);

        final NotificationCompat.Builder notification = getBaseNotification()
                .setContentTitle(resources.getString(R.string.notif_title_handset_ringing))
                .setContentIntent(getLogsPendingIntent())
                .setVibrate(getNotificationPreference())
                .setSound(getAlarmSound())
                .addAction(R.drawable.ic_lock_open_black_24dp,
                        resources.getString(R.string.notif_action_title_unlock), unlockPendingIntent)
                .setAutoCancel(true);

        notificationManager.notify(ID_HANDSET_CALLING, notification.build());

        handler.postDelayed(cancelNotificationRunnable, 60000); // run after a minute
    }

    /**
     * To be called when the unlock button has been pressed
     * Cancels calling notification
     * Then removes callback for dismissing and displaying the called notification
     */
    public void onUnlockPressed() {
        notificationManager.cancel(ID_HANDSET_CALLING);
        handler.removeCallbacks(cancelNotificationRunnable);
    }

    /**
     * Display notification when a response from the unlock service has been received
     *
     * @param success whether the unlock request was successful
     */
    public void notifyHandsetUnlocked(boolean success) {
        String title;
        String text;

        if (success) {
            title = resources.getString(R.string.notif_title_unlock_success);
            text = resources.getString(R.string.notif_text_unlock_success);
        } else {
            title = resources.getString(R.string.notif_title_unlock_failed);
            text = resources.getString(R.string.notif_text_unlock_failed);

        }

        Notification notification = getBaseNotification()
                .setContentTitle(title)
                .setContentText(text)
                .setContentIntent(getLogsPendingIntent())
                .setSound(getAlarmSound())
                .setAutoCancel(true)
                .build();

        notificationManager.notify(ID_UNLOCKED, notification);
    }

    /**
     * To be called when the prime button has been pressed
     */
    public void onPrimePressed() {
        // TODO - do we want to display something?
    }

    public void notifySystemPrimed(boolean success) {
        Notification notification = getBaseNotification().setContentTitle(success ? "System Primed!" : "Not primed").build();
        notificationManager.notify(ID_PRIMED, notification);
    }

    // \Public methods

    // Private methods

    private NotificationCompat.Builder getBaseNotification() {
        return new NotificationCompat.Builder(context)
                .setSmallIcon(R.drawable.ic_lock_open_black_24dp);
    }

    private void notifyHandsetCalled() {
        Timber.d("notifyHandsetCalled");
        PendingIntent contentPendingIntent = PendingIntent.getActivity(context, 0, MainActivity.getMainIntent(context, MainActivity.FRAG_LOGS), 0);

        NotificationCompat.Builder notification = getBaseNotification()
                .setContentTitle("Handset was called")
                .setContentIntent(contentPendingIntent)
                .setAutoCancel(true);

        notificationManager.notify(ID_HANDSET_CALLED, notification.build());
    }

    private PendingIntent getLogsPendingIntent() {
        return PendingIntent.getActivity(context, 0, MainActivity.getMainIntent(context, MainActivity.FRAG_LOGS), 0);
    }

    private Uri getAlarmSound() {
        return RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
    }

    private long[] getNotificationPreference() {
        String prefVibrateTune = preferences.getString("pref_vibrate_tune", null);

        long[] tune = VibratorTunes.VIBRATE_TUNES.get(prefVibrateTune);

        if (tune == null || tune.length == 0) {
            tune = VibratorTunes.STANDARD;
        }

        return tune;
    }
}