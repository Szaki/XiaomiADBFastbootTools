import javafx.application.Platform
import javafx.collections.FXCollections
import javafx.scene.control.ProgressBar
import javafx.scene.control.ProgressIndicator
import javafx.scene.control.TableView
import javafx.scene.control.TextInputControl
import java.io.IOException
import java.util.*
import kotlin.collections.ArrayList
import kotlin.concurrent.thread


class Installer(
    var uninstallTableView: TableView<App>,
    var reinstallTableView: TableView<App>,
    var progress: ProgressBar,
    var progressind: ProgressIndicator,
    control: TextInputControl
) : Command(control) {

    private lateinit var apps: ArrayList<String>
    private val command = Command()
    private lateinit var device: Device

    init {
        pb.redirectErrorStream(false)
    }

    fun loadApps(dev: Device) {
        device = dev
        apps = arrayListOf(
            "Analytics;com.miui.analytics",
            "App Vault;com.miui.personalassistant,com.mi.android.globalpersonalassistant",
            "Android Easter Egg;com.android.egg",
            "Backup;com.miui.backup",
            "Backup wallpapers;com.android.wallpaperbackup",
            "Bookmark Provider;com.android.bookmarkprovider",
            "Bookmars from partners for default Browser (advertising sponsors);com.android.providers.partnerbookmarks",
            "Blocklist;com.miui.antispam",
            "Browser;com.android.browser",
            "Calculator;com.miui.calculator",
            "Calendar;com.android.calendar",
            "Cleaner;com.miui.cleanmaster",
            "Clock;com.android.deskclock",
            "Call Log Backup/Restore;com.android.calllogbackup",
            "Cell Broadcasts;com.android.cellbroadcastreceiver",
            "Compass;com.miui.compass",
            "Default Print Service;com.android.bips",
            "DiagLogger;com.huaqin.diaglogger",
            "Downloads;com.android.providers.downloads.ui",
            "Emergency information;com.android.emergency",
            "Facebook;com.facebook.system,com.facebook.appmanager,com.facebook.services",
            "Feedback;com.miui.bugreport",
            "FM Radio;com.miui.fm",
            "Games;com.xiaomi.glgm",
            "Gmail;com.google.android.gm",
            "Google App;com.google.android.googlequicksearchbox",
            "Google Assistant;com.google.android.apps.googleassistant",
            "Google Calculator;com.google.android.calculator",
            "Google Calendar;com.google.android.calendar",
            "Google Chrome;com.android.chrome",
            "Google Clock;com.google.android.deskclock",
            "Google Drive;com.google.android.apps.docs",
            "Google Duo;com.google.android.apps.tachyon",
            "Google Hangouts;com.google.android.talk",
            "Google Indic Keyboard;com.google.android.apps.inputmethod.hindi",
            "Google Keep;com.google.android.keep",
            "Google Korean Input;com.google.android.inputmethod.korean",
            "Google Latin Keyboard;com.google.android.inputmethod.latin",
            "Google Maps;com.google.android.apps.maps",
            "Google Photos;com.google.android.apps.photos",
            "Google Pinyin Input;com.google.android.inputmethod.pinyin",
            "Google Play Books;com.google.android.apps.books",
            "Google Play Games;com.google.android.play.games",
            "Google Play Movies;com.google.android.videos",
            "Google Play Music;com.google.android.music",
            "Google Zhuyin Input;com.google.android.apps.inputmethod.zhuyin",
            "HybridAccessory;com.miui.hybrid.accessory",
            "Joyose;com.xiaomi.joyose",
            "KLO Bugreport;com.miui.klo.bugreport",
            "Live Wallpaper Picker;com.android.wallpaper.livepicker",
            "MAB;com.xiaomi.ab",
            "Mail;com.android.email",
            "Market Feedback Agent;com.google.android.feedback",
            "Mi AI;com.miui.voiceassist",
            "Mi App Store;com.xiaomi.mipicks",
            "Mi Community;com.mi.global.bbs",
            "Mi Cloud;com.miui.cloudservice,com.miui.cloudservice.sysbase,com.miui.micloudsync,com.miui.cloudbackup",
            "Mi Credit;com.xiaomi.payment,com.micredit.in",
            "Mi Drop;com.xiaomi.midrop",
            "Mi File Manager;com.mi.android.globalFileexplorer",
            "Mi Pay;com.mipay.wallet.in,com.mipay.wallet.id",
            "Mi Recycle;com.xiaomi.mirecycle",
            "Mi Roaming;com.miui.virtualsim",
            "Mi Store;com.mi.global.shop",
            "Mi Video;com.miui.video,com.miui.videoplayer",
            "Mi VR;com.mi.dlabs.vr",
            "Mi Wallet;com.mipay.wallet",
            "MiuiDaemon;com.miui.daemon",
            "MiWebView;com.mi.webkit.core",
            "Mobile Device Information Provider;com.amazon.appmanager",
            "MSA;com.miui.msa.global,com.miui.systemAdSolution",
            "Music;com.miui.player",
            "News;com.mi.globalTrendNews",
            "Notes;com.miui.notes",
            "Package Installer;com.miui.global.packageinstaller",
            "PartnerNetflixActivation;com.netflix.partner.activation",
            "Print Service Recommendation Service;com.google.android.printservice.recommendation",
            "Print Spooler;com.android.printspooler",
            "Photo Screensavers;com.android.dreams.phototable",
            "PAI;android.autoinstalls.config.Xiaomi.${device.codename}",
            "Quick Apps;com.miui.hybrid",
            "Recorder;com.android.soundrecorder",
            "Scanner;com.xiaomi.scanner",
            "Screen Recorder;com.miui.screenrecorder",
            "Search;com.android.quicksearchbox",
            "SMS Extra;com.miui.smsextra",
            "Touch Assistant;com.miui.touchassistant",
            "Translation Service;com.miui.translationservice,com.miui.translation.kingsoft,com.miui.translation.xmcloud,com.miui.translation.youdao",
            "UniPlay Service;com.milink.service",
            "VsimCore;com.miui.vsimcore",
            "Weather;com.miui.weather2,com.miui.providers.weather",
            "Xiaomi VIP Account;com.xiaomi.vipaccount",
            "Xiaomi Service Framework;com.xiaomi.xmsf",
            "Xiaomi SIM Activate Service;com.xiaomi.simactivate.service",
            "Yellow Pages;com.miui.yellowpage",
            "YouTube;com.google.android.youtube"
        )
        val packages = command.exec("adb shell cmd package install-existing xaft")
        device.reinstaller = !("not found" in packages || "Unknown command" in packages)
        createTables()
    }

    fun createTables() {
        val installed = if (device.reinstaller)
            command.exec("adb shell cmd package list packages")
        else command.exec("adb shell pm list packages")
        val all = command.exec("adb shell cmd package list packages -u")
        uninstallTableView.items.clear()
        reinstallTableView.items.clear()
        apps.forEach {
            val app = it.split(';')
            val uninst = ArrayList<String>()
            val reinst = ArrayList<String>()
            app[1].split(',').forEach { pkg ->
                if (installed.contains(pkg + System.lineSeparator()))
                    uninst.add(pkg)
                else if (all.contains(pkg + System.lineSeparator()))
                    reinst.add(pkg)
            }
            if (uninst.isNotEmpty())
                uninstallTableView.items.add(App(app[0], uninst))
            if (reinst.isNotEmpty())
                reinstallTableView.items.add(App(app[0], reinst))
        }
        uninstallTableView.refresh()
        reinstallTableView.refresh()
    }

    fun addApp(app: String) {
        val existing = apps.find { app in it }
        if (existing == null)
            apps.add("${app.split('.').last()};$app")
    }

    fun isAppSelected(option: Int): Boolean {
        val list = if (option == 0)
            uninstallTableView.items
        else reinstallTableView.items
        if (list.isNotEmpty()) {
            for (app in list)
                if (app.selectedProperty().get())
                    return true
            return false
        } else return false
    }

    fun uninstall(func: () -> Unit) {
        val selected = FXCollections.observableArrayList<App>()
        var n = 0
        uninstallTableView.items.forEach {
            if (it.selectedProperty().get()) {
                selected.add(it)
                n += it.packagenameProperty().get().lines().size
            }
        }
        tic?.text = ""
        progress.progress = 0.0
        progressind.isVisible = true
        thread(true, true) {
            selected.forEach {
                it.packagenameProperty().get().lines().forEach { pkg ->
                    val arguments =
                        ("adb shell pm uninstall --user 0 $pkg").split(' ').toTypedArray()
                    arguments[0] = prefix + arguments[0]
                    pb.command(*arguments)
                    try {
                        proc = pb.start()
                    } catch (ex: IOException) {
                        ex.printStackTrace()
                        ExceptionAlert(ex)
                    }
                    val scan = Scanner(proc.inputStream)
                    var line = ""
                    while (scan.hasNext())
                        line += scan.nextLine() + System.lineSeparator()
                    scan.close()
                    Platform.runLater {
                        tic?.appendText("App: ${it.appnameProperty().get()}\n")
                        tic?.appendText("Package: $pkg\n")
                        tic?.appendText("Result: $line\n")
                        progress.progress += 1.0 / n
                    }
                }
            }
            Platform.runLater {
                tic?.appendText("Done!")
                progress.progress = 0.0
                progressind.isVisible = false
                createTables()
                func()
            }
        }
    }

    fun reinstall(func: () -> Unit) {
        val selected = FXCollections.observableArrayList<App>()
        var n = 0
        reinstallTableView.items.forEach {
            if (it.selectedProperty().get()) {
                selected.add(it)
                n += it.packagenameProperty().get().lines().size
            }
        }
        tic?.text = ""
        progress.progress = 0.0
        progressind.isVisible = true
        thread(true, true) {
            selected.forEach {
                it.packagenameProperty().get().lines().forEach { pkg ->
                    val arguments =
                        ("adb shell cmd package install-existing $pkg").split(' ')
                            .toTypedArray()
                    arguments[0] = prefix + arguments[0]
                    pb.command(*arguments)
                    try {
                        proc = pb.start()
                    } catch (ex: IOException) {
                        ex.printStackTrace()
                        ExceptionAlert(ex)
                    }
                    val scan = Scanner(proc.inputStream)
                    var line = ""
                    while (scan.hasNext())
                        line += scan.nextLine() + System.lineSeparator()
                    scan.close()
                    if ("installed for user" in line)
                        line = "Success\n"
                    else line = "Failure [${line.substringAfter(pkg).trim()}]\n"
                    Platform.runLater {
                        tic?.appendText("App: ${it.appnameProperty().get()}\n")
                        tic?.appendText("Package: $pkg\n")
                        tic?.appendText("Result: $line\n")
                        progress.progress += 1.0 / n
                    }
                }
            }
            Platform.runLater {
                tic?.appendText("Done!")
                progress.progress = 0.0
                progressind.isVisible = false
                createTables()
                func()
            }
        }
    }
}