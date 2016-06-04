package technology.mainthread.apps.gatekeeper.injector.graph;

import technology.mainthread.apps.gatekeeper.GatekeeperApp;
import technology.mainthread.apps.gatekeeper.service.GatekeeperIntentService;
import technology.mainthread.apps.gatekeeper.service.MessagingInstanceIdService;
import technology.mainthread.apps.gatekeeper.service.MessagingService;
import technology.mainthread.apps.gatekeeper.service.MobileWearListenerService;
import technology.mainthread.apps.gatekeeper.view.activity.AuthActivity;
import technology.mainthread.apps.gatekeeper.view.activity.MainActivity;
import technology.mainthread.apps.gatekeeper.view.fragment.LogsFragment;
import technology.mainthread.apps.gatekeeper.view.fragment.SettingsFragment;
import technology.mainthread.apps.gatekeeper.view.fragment.UnlockFragment;
import technology.mainthread.apps.gatekeeper.view.widget.PrimeWidgetService;

public interface AppGraph {
    void inject(GatekeeperApp gatekeeperApp);

    void inject(UnlockFragment unlockFragment);

    void inject(SettingsFragment settingsFragment);

    void inject(GatekeeperIntentService gatekeeperIntentService);

    void inject(LogsFragment logsFragment);

    void inject(PrimeWidgetService primeWidgetService);

    void inject(MobileWearListenerService mobileWearListenerService);

    void inject(MessagingInstanceIdService messagingInstanceIdService);

    void inject(MessagingService messagingService);

    void inject(AuthActivity authActivity);

    void inject(MainActivity mainActivity);
}
