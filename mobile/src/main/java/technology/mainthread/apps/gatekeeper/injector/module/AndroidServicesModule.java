package technology.mainthread.apps.gatekeeper.injector.module;

import android.app.Application;
import android.appwidget.AppWidgetManager;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationManagerCompat;

import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.wearable.Wearable;

import javax.inject.Named;
import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class AndroidServicesModule {

    private final Context context;

    public AndroidServicesModule(Application application) {
        this.context = application.getApplicationContext();
    }

    @Provides
    NotificationManagerCompat provideNotificationManager() {
        return NotificationManagerCompat.from(context);
    }

    @Provides
    GoogleApiAvailability providesGoogleApiAvailability() {
        return GoogleApiAvailability.getInstance();
    }

    @Provides
    ClipboardManager providesClipboardManager() {
        return (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
    }

    @Provides
    SharedPreferences providesDefaultSharedPreferences() {
        return PreferenceManager.getDefaultSharedPreferences(context);
    }

    @Provides
    Vibrator providesVibrator() {
        return (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
    }

    @Provides
    AppWidgetManager provideAppWidgetManager() {
        return AppWidgetManager.getInstance(context);
    }

    @Provides
    @Singleton
    @Named("wear")
    GoogleApiClient providesWearApiClient() {
        return new GoogleApiClient.Builder(context)
                .addApi(Wearable.API)
                .build();
    }

}
