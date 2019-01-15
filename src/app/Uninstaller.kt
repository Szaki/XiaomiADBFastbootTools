package app

import javafx.application.Platform
import javafx.collections.FXCollections
import javafx.collections.ObservableList
import javafx.scene.control.ProgressBar
import javafx.scene.control.ProgressIndicator
import javafx.scene.control.TableView
import javafx.scene.control.TextInputControl
import java.io.BufferedWriter
import java.io.IOException
import java.io.OutputStreamWriter
import java.util.*


class Uninstaller(var tv: TableView<App>, var progress: ProgressBar, var progressind: ProgressIndicator, control: TextInputControl) : Command(control) {

    lateinit var t: Thread
    lateinit var apps: ObservableList<App>

    init {
        pb.redirectErrorStream(false)
    }

    fun loadApps(device: Device) {
        apps = FXCollections.observableArrayList()
        apps.add(App("Analytics", "com.miui.analytics", true))
        apps.add(App("App Vault", "com.miui.personalassistant"))
        apps.add(App("App Vault", "com.mi.android.globalpersonalassistant"))
        apps.add(App("Browser", "com.android.browser"))
        apps.add(App("Calculator", "com.miui.calculator"))
        apps.add(App("Calendar", "com.android.calendar"))
        apps.add(App("Cleaner", "com.miui.cleanmaster"))
        apps.add(App("Clock", "com.android.deskclock"))
        apps.add(App("Compass", "com.miui.compass"))
        apps.add(App("Downloads", "com.android.providers.downloads.ui"))
        apps.add(App("Facebook", "com.facebook.katana"))
        apps.add(App("Facebook App Installer", "com.facebook.system"))
        apps.add(App("Facebook App Manager", "com.facebook.appmanager"))
        apps.add(App("Facebook Services", "com.facebook.services"))
        apps.add(App("Feedback", "com.miui.bugreport"))
        apps.add(App("FM Radio", "com.miui.fm"))
        apps.add(App("Games", "com.xiaomi.glgm"))
        apps.add(App("Gmail", "com.google.android.gm"))
        apps.add(App("Google App", "com.google.android.googlequicksearchbox"))
        apps.add(App("Google Assistant", "com.google.android.apps.googleassistant"))
        apps.add(App("Google Calculator", "com.google.android.calculator"))
        apps.add(App("Google Calendar", "com.google.android.calendar"))
        apps.add(App("Google Chrome", "com.android.chrome"))
        apps.add(App("Google Clock", "com.google.android.deskclock"))
        apps.add(App("Google Drive", "com.google.android.apps.docs"))
        apps.add(App("Google Duo", "com.google.android.apps.tachyon"))
        apps.add(App("Google Hangouts", "com.google.android.talk"))
        apps.add(App("Google Indic Keyboard", "com.google.android.apps.inputmethod.hindi"))
        apps.add(App("Google Keep", "com.google.android.keep"))
        apps.add(App("Google Korean Input", "com.google.android.inputmethod.korean"))
        apps.add(App("Google Maps", "com.google.android.apps.maps"))
        apps.add(App("Google Photos", "com.google.android.apps.photos"))
        apps.add(App("Google Pinyin Input", "com.google.android.inputmethod.pinyin"))
        apps.add(App("Google Play Books", "com.google.android.apps.books"))
        apps.add(App("Google Play Games", "com.google.android.play.games"))
        apps.add(App("Google Play Movies", "com.google.android.videos"))
        apps.add(App("Google Play Music", "com.google.android.music"))
        apps.add(App("Google Zhuyin Input", "com.google.android.apps.inputmethod.zhuyin"))
        apps.add(App("KLO Bugreport", "com.miui.klo.bugreport", true))
        apps.add(App("MAB", "com.xiaomi.ab", true))
        apps.add(App("Mail", "com.android.email"))
        apps.add(App("Mi Account", "com.xiaomi.account"))
        apps.add(App("Mi AI", "com.miui.voiceassist"))
        apps.add(App("Mi App Store", "com.xiaomi.mipicks"))
        apps.add(App("Mi Cloud", "com.miui.cloudservice"))
        apps.add(App("Mi Cloud Backup", "com.miui.cloudbackup"))
        apps.add(App("Mi Credit", "com.xiaomi.payment"))
        apps.add(App("Mi Drop", "com.xiaomi.midrop"))
        apps.add(App("Mi File Manager", "com.mi.android.globalFileexplorer"))
        apps.add(App("Mi Recycle", "com.xiaomi.mirecycle"))
        apps.add(App("Mi Roaming", "com.miui.virtualsim"))
        apps.add(App("Mi Video", "com.miui.video"))
        apps.add(App("Mi Video", "com.miui.videoplayer"))
        apps.add(App("Mi Wallet", "com.mipay.wallet"))
        apps.add(App("MiuiDaemon", "com.miui.daemon", true))
        apps.add(App("Mobile Device Information Provider", "com.amazon.appmanager"))
        apps.add(App("MSA", "com.miui.msa.global", true))
        apps.add(App("MSA", "com.miui.systemAdSolution", true))
        apps.add(App("Music", "com.miui.player"))
        apps.add(App("Notes", "com.miui.notes"))
        apps.add(App("PAI", "android.autoinstalls.config.Xiaomi.${device.codename}", true))
        apps.add(App("Quick Apps", "com.miui.hybrid", true))
        apps.add(App("Recorder", "com.android.soundrecorder"))
        apps.add(App("Scanner", "com.xiaomi.scanner"))
        apps.add(App("Screen Recorder", "com.miui.screenrecorder"))
        apps.add(App("Search", "com.android.quicksearchbox"))
        apps.add(App("Weather", "com.miui.weather2"))
        apps.add(App("Xiaomi VIP Account", "com.xiaomi.vipaccount"))
        apps.add(App("Xiaomi Service Framework", "com.xiaomi.xmsf"))
        apps.add(App("Xiaomi SIM Activate Service", "com.xiaomi.simactivate.service"))
        apps.add(App("Yellow Pages", "com.miui.yellowpage"))
        apps.add(App("YouTube", "com.google.android.youtube"))
        createTable()
    }

    fun createTable() {
        val installed = Command().exec("adb shell pm list packages")
        val iterator = apps.iterator()
        while (iterator.hasNext()) {
            if (!installed.contains(iterator.next().packagenameProperty().get() + System.lineSeparator()))
                iterator.remove()
        }
        tv.items = apps
        tv.refresh()
    }

    fun uninstall(func: () -> Unit) {
        val undesirable = FXCollections.observableArrayList<App>()
        if (tv.items.size != 0) {
            for (app in tv.items)
                if (app.selectedProperty().get())
                    undesirable.add(app)
            if (undesirable.size == 0)
                return
        } else return
        val n = undesirable.size
        tic?.text = ""
        progressind.isVisible = true
        t = Thread {
            for (app in undesirable) {
                arguments = ("adb shell pm uninstall --user 0 ${app.packagenameProperty().get()}").split(" ").toTypedArray()
                arguments[0] = prefix + arguments[0]
                pb.command(*arguments)
                try {
                    proc = pb.start()
                } catch (ex: IOException) {
                    ex.printStackTrace()
                }
                scan = Scanner(proc.inputStream)
                var line = ""
                while (scan.hasNext())
                    line += scan.nextLine() + System.lineSeparator()
                scan.close()
                Platform.runLater {
                    tic?.appendText("App: ${app.appnameProperty().get()}${System.lineSeparator()}")
                    tic?.appendText("Package: ${app.packagenameProperty().get()}${System.lineSeparator()}")
                    tic?.appendText("Result: $line${System.lineSeparator()}")
                    progress.progress += 1.0 / n
                }
            }
            Platform.runLater {
                tic?.appendText("Done!")
                progress.progress = 0.0
                progressind.isVisible = false
                func()
                createTable()
            }
        }
        t.isDaemon = true
        t.start()
    }

    fun freeze(func: () -> Unit) {
        val undesirable = FXCollections.observableArrayList<App>()
        if (tv.items.size != 0) {
            for (app in tv.items)
                if (app.selectedProperty().get())
                    undesirable.add(app)
            if (undesirable.size == 0)
                return
        } else return
        val n = undesirable.size
        tic?.text = ""
        progressind.isVisible = true
        t = Thread {
            for (app in undesirable) {

                arguments = ("adb shell su pm disable ${app.packagenameProperty().get()}").split(" ").toTypedArray()
                arguments[0] = prefix + arguments[0]
                print(arguments)
                pb.command(arguments[0], arguments[1])
                try {
                    proc = pb.start()
                    commandOut(app.packagenameProperty().get())
                } catch (ex: IOException) {
                    ex.printStackTrace()
                }
                scan = Scanner(proc.inputStream)
                var line = ""
                while (scan.hasNext())
                    line += scan.nextLine() + System.lineSeparator()
                if (line == "")
                    line = "Failed!\n\tYou have no Root privileges"
                scan.close()
                Platform.runLater {
                    tic?.appendText("App: ${app.appnameProperty().get()}${System.lineSeparator()}")
                    tic?.appendText("Package: ${app.packagenameProperty().get()}${System.lineSeparator()}")
                    tic?.appendText("Result: $line${System.lineSeparator()}")
                    progress.progress += 1.0 / n
                }
            }
            Platform.runLater {
                tic?.appendText("Done!")
                progress.progress = 0.0
                progressind.isVisible = false
                func()
                createTable()
            }
        }
        t.isDaemon = true
        t.start()
    }

    private fun commandOut(pkg :String){
        var p_stdin : BufferedWriter = BufferedWriter(OutputStreamWriter(proc.outputStream))
        commandUpdate(p_stdin, "su")
        commandUpdate(p_stdin, "pm disable ${pkg}")
        commandUpdate(p_stdin, "exit")
        commandUpdate(p_stdin, "exit")
    }

    private fun commandUpdate(p:BufferedWriter, comm:String){
        p.write(comm)
        p.newLine()
        p.flush()
    }
}
