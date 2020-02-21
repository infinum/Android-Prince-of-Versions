package co.infinum.povexampleapp

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.Toast
import co.infinum.princeofversions.Loader
import co.infinum.princeofversions.NetworkLoader
import co.infinum.princeofversions.PrinceOfVersions
import co.infinum.princeofversions.PrinceOfVersionsCancelable
import co.infinum.princeofversions.UpdateResult
import co.infinum.princeofversions.UpdateStatus
import co.infinum.princeofversions.UpdaterCallback

class KotlinCommonExample : AppCompatActivity() {

    private val defaultCallback = object : UpdaterCallback {
        override fun onSuccess(result: UpdateResult) {
            when (result.status) {
                UpdateStatus.REQUIRED_UPDATE_NEEDED -> {
                    toastIt(
                        getString(
                            R.string.update_available_msg,
                            getString(R.string.mandatory),
                            result.updateVersion
                        ),
                        Toast.LENGTH_SHORT
                    )
                }
                UpdateStatus.NEW_UPDATE_AVAILABLE -> {
                    toastIt(getString(R.string.update_available_msg, getString(R.string.not_mandatory), result.info.lastVersionAvailable), Toast.LENGTH_SHORT)
                }
                UpdateStatus.NO_UPDATE_AVAILABLE -> toastIt(getString(R.string.no_update_available), Toast.LENGTH_SHORT)
            }
        }

        override fun onError(throwable: Throwable) {
            throwable.printStackTrace()
            toastIt(String.format(getString(R.string.update_exception), throwable.message), Toast.LENGTH_SHORT)
        }
    }

    private val handler = Handler(Looper.getMainLooper())

    private lateinit var updater: PrinceOfVersions

    private lateinit var loader: Loader

    private var cancelable: PrinceOfVersionsCancelable? = null

    /**
     * This instance represents a very slow loader, just to give you enough time to invoke cancel option.
     */
    private lateinit var slowLoader: Loader

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_common_usage)

        initUI()

        /*  create new instance of updater */
        updater = PrinceOfVersions.Builder().build(this)
        /*  create specific loader factory for loading from internet  */
        loader = NetworkLoader("https://pastebin.com/raw/4pVYKz0r")
        slowLoader = createSlowLoader(loader)
    }

    override fun onStop() {
        super.onStop()
        onCancelClick()
    }

    private fun initUI() {
        val btnCheck = findViewById<View>(R.id.btnCheck)
        val btnCancelTest = findViewById<View>(R.id.btnCancelTest)
        val btnCancel = findViewById<View>(R.id.btnCancel)
        val btnCheckSync = findViewById<View>(R.id.btnCheckSync)
        btnCheck.setOnClickListener { onCheckClick() }
        btnCancelTest.setOnClickListener { onCancelTestClick() }
        btnCancel.setOnClickListener { onCancelClick() }
        btnCheckSync.setOnClickListener { onCheckSyncClick() }
    }

    private fun onCheckClick() {
        /*  call check for updates for start checking and remember return value if you need cancel option    */
        val cancelable = updater.checkForUpdates(loader, defaultCallback)
        replaceCancelable(cancelable)
    }

    private fun onCheckSyncClick() {
        /*  call check for updates for start checking and remember return value if you need cancel option    */
        val thread = Thread(Runnable {
            try {
                val result = updater.checkForUpdates(loader)
                toastItOnMainThread("Update check finished with status " + result.status + " and version " + result.info.lastVersionAvailable,
                    Toast.LENGTH_LONG)
            } catch (throwable: Throwable) {
                toastItOnMainThread("Error occurred " + throwable.message, Toast.LENGTH_LONG)
            }
        }, "Example thread")
        thread.start()
    }

    private fun onCancelTestClick() {
        /*  same call as few lines higher, but using another loader, this one is very slow loader just to demonstrate cancel
        functionality. */
        val cancelable = updater.checkForUpdates(slowLoader, defaultCallback)
        replaceCancelable(cancelable)
    }

    private fun onCancelClick() {
        /*  cancel current checking request, checking if context is not consumed yet is not necessary   */
        this.cancelable?.cancel()
    }

    private fun replaceCancelable(cancelable: PrinceOfVersionsCancelable) {
        /*  started new checking, kill current one if not dead and remember new context */
        this.cancelable?.cancel()
        this.cancelable = cancelable
    }

    private fun toastIt(message: String, duration: Int) {
        Toast.makeText(applicationContext, message, duration).show()
    }

    private fun toastItOnMainThread(message: String, duration: Int) {
        handler.post { toastIt(message, duration) }
    }

    private fun createSlowLoader(loader: Loader): Loader {
        return Loader {
            Thread.sleep(2000)
            loader.load()
        }
    }
}
