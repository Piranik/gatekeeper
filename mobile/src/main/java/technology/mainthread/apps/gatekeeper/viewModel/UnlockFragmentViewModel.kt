package technology.mainthread.apps.gatekeeper.viewModel

import android.content.Context
import android.databinding.BaseObservable
import android.databinding.ObservableBoolean
import android.databinding.ObservableField
import android.support.annotation.StringRes
import android.support.design.widget.Snackbar
import android.view.View
import com.trello.rxlifecycle2.LifecycleTransformer
import technology.mainthread.apps.gatekeeper.common.rx.applyObservableSchedulers
import technology.mainthread.apps.gatekeeper.data.AppStateController
import technology.mainthread.apps.gatekeeper.model.event.AppEvent
import technology.mainthread.apps.gatekeeper.model.event.AppEventType
import technology.mainthread.apps.gatekeeper.service.ACTION_CHECK_STATE
import technology.mainthread.apps.gatekeeper.service.ACTION_PRIME
import technology.mainthread.apps.gatekeeper.service.ACTION_UNLOCK
import technology.mainthread.apps.gatekeeper.service.getGatekeeperStateIntent

class UnlockFragmentViewModel(private val context: Context,
                              private val rootView: View,
                              private val appStateController: AppStateController,
                              private val lifecycleTransformer: LifecycleTransformer<AppEvent>) : BaseObservable() {

    var loading = ObservableBoolean()

    var deviceStatus = ObservableField<String>()

    /**
     * Setup the device status thing.
     */
    fun initialize() {
        appStateController.observable
                .compose(lifecycleTransformer)
                .compose(applyObservableSchedulers<AppEvent>())
                .subscribe { event -> onAppEvent(event) }

        deviceStatus.set(appStateController.lastKnownGatekeeperState.label)
        context.startService(getGatekeeperStateIntent(context, ACTION_CHECK_STATE))
    }

    fun onUnlockClicked(view: View) {
        context.startService(getGatekeeperStateIntent(context, ACTION_UNLOCK))
    }

    fun onPrimeClicked(view: View) {
        context.startService(getGatekeeperStateIntent(context, ACTION_PRIME))
    }

    private fun onAppEvent(appEvent: AppEvent) {
        deviceStatus.set(appEvent.gatekeeperState.label)

        when (appEvent.appState) {
            AppEventType.CHECKING, AppEventType.UNLOCKING, AppEventType.PRIMING -> loading.set(true)
            AppEventType.COMPLETE -> {
                loading.set(false)
                displaySnackbar(appEvent.message)
            }
            AppEventType.READY -> loading.set(false)
        }
    }

    private fun displaySnackbar(@StringRes message: Int) {
        Snackbar.make(rootView, message, Snackbar.LENGTH_LONG).show()
    }
}