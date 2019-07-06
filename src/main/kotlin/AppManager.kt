import javafx.application.Platform
import javafx.collections.ObservableList
import javafx.scene.control.ProgressBar
import javafx.scene.control.ProgressIndicator
import javafx.scene.control.TableView
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap
import kotlin.concurrent.thread


class AppManager : Command() {

    companion object {
        var user = 0
        lateinit var uninstallerTableView: TableView<App>
        lateinit var reinstallerTableView: TableView<App>
        lateinit var disablerTableView: TableView<App>
        lateinit var enablerTableView: TableView<App>
        lateinit var progress: ProgressBar
        lateinit var progressInd: ProgressIndicator
        val potentialApps = arrayListOf(
            "Analytics;com.miui.analytics",
            "App Vault;com.miui.personalassistant,com.mi.android.globalpersonalassistant",
            "Backup;com.miui.backup",
            "Blocklist;com.miui.antispam",
            "Browser;com.android.browser",
            "Calculator;com.miui.calculator",
            "Calendar;com.android.calendar",
            "CatchLog;com.bsp.catchlog",
            "Cleaner;com.miui.cleanmaster",
            "Clock;com.android.deskclock",
            "Compass;com.miui.compass",
            "DiagLogger;com.huaqin.diaglogger",
            "Downloads;com.android.providers.downloads.ui",
            "Facebook;com.facebook.system,com.facebook.appmanager,com.facebook.services",
            "Facemoji Keyboard for Xiaomi;com.facemoji.lite.xiaomi",
            "Feedback;com.miui.bugreport",
            "FM Radio;com.miui.fm",
            "Freeform;com.miui.freeform",
            "Game Turbo;com.xiaomi.gameboosterglobal",
            "Games;com.xiaomi.glgm",
            "Gboard;com.google.android.inputmethod.latin",
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
            "Google Lens;com.google.ar.lens",
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
            "MAB;com.xiaomi.ab",
            "Mail;com.android.email",
            "Market Feedback Agent;com.google.android.feedback",
            "Mi AI;com.miui.voiceassist",
            "Mi App Store;com.xiaomi.mipicks",
            "Mi Cloud;com.miui.cloudservice,com.miui.cloudservice.sysbase,com.miui.micloudsync,com.miui.cloudbackup",
            "Mi Credit;com.xiaomi.payment,com.micredit.in",
            "Mi Drop;com.xiaomi.midrop",
            "Mi File Manager;com.mi.android.globalFileexplorer",
            "Mi Pay;com.mipay.wallet.in,com.mipay.wallet.id",
            "Mi Recycle;com.xiaomi.mirecycle",
            "Mi Roaming;com.miui.virtualsim",
            "Mi Video;com.miui.video,com.miui.videoplayer",
            "Mi VR;com.mi.dlabs.vr",
            "Mi Wallet;com.mipay.wallet",
            "Mi Wallpaper;com.miui.miwallpaper",
            "MiuiDaemon;com.miui.daemon",
            "MiWebView;com.mi.webkit.core",
            "Mobile Device Information Provider;com.amazon.appmanager",
            "MSA;com.miui.msa.global,com.miui.systemAdSolution",
            "Music;com.miui.player",
            "News;com.mi.globalTrendNews",
            "Notes;com.miui.notes",
            "Package Installer;com.miui.global.packageinstaller",
            "PAI;android.autoinstalls.config.Xiaomi.${Device.codename}",
            "PartnerBookmarks;com.android.providers.partnerbookmarks",
            "PartnerNetflixActivation;com.netflix.partner.activation",
            "POCO Launcher;com.mi.android.globallauncher",
            "Quick Apps;com.miui.hybrid",
            "Quick Ball;com.miui.touchassistant",
            "Recorder;com.android.soundrecorder",
            "Scanner;com.xiaomi.scanner",
            "Screen Recorder;com.miui.screenrecorder",
            "Search;com.android.quicksearchbox",
            "SMS Extra;com.miui.smsextra",
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

        fun createTables() {
            uninstallerTableView.items.clear()
            reinstallerTableView.items.clear()
            disablerTableView.items.clear()
            enablerTableView.items.clear()
            val uninst = ArrayList<String>()
            val reinst = ArrayList<String>()
            val disable = ArrayList<String>()
            val enable = ArrayList<String>()
            val apps = HashMap<String, String>()
            exec("adb shell pm list packages -u --user $user").trim().lines().forEach {
                apps[it.substringAfter(':')] = "uninstalled"
            }
            exec("adb shell pm list packages -d --user $user").trim().lines().forEach {
                apps[it.substringAfter(':')] = "disabled"
            }
            exec("adb shell pm list packages -e --user $user").trim().lines().forEach {
                apps[it.substringAfter(':')] = "enabled"
            }
            potentialApps.forEach {
                uninst.clear()
                reinst.clear()
                disable.clear()
                enable.clear()
                val app = it.split(';')
                for (pkg in app[1].split(',')) {
                    if (apps[pkg].isNullOrBlank())
                        continue
                    when (apps[pkg]) {
                        "disabled" -> {
                            uninst.add(pkg)
                            enable.add(pkg)
                        }
                        "enabled" -> {
                            uninst.add(pkg)
                            disable.add(pkg)
                        }
                        "uninstalled" -> reinst.add(pkg)
                    }
                }
                if (uninst.isNotEmpty())
                    uninstallerTableView.items.add(App(app[0], uninst))
                if (reinst.isNotEmpty())
                    reinstallerTableView.items.add(App(app[0], reinst))
                if (disable.isNotEmpty())
                    disablerTableView.items.add(App(app[0], disable))
                if (enable.isNotEmpty())
                    enablerTableView.items.add(App(app[0], enable))
            }
            uninstallerTableView.refresh()
            reinstallerTableView.refresh()
            disablerTableView.refresh()
            enablerTableView.refresh()
        }

        fun addApp(app: String) {
            if (';' in app) {
                if (potentialApps.find { app.substringAfterLast(';') in it } == null)
                    potentialApps.add(app)
            } else {
                if (potentialApps.find { app in it } == null)
                    potentialApps.add("${app.substringAfterLast('.')};$app")
            }
        }

        fun uninstall(selected: ObservableList<App>, n: Int, func: () -> Unit) {
            pb.redirectErrorStream(false)
            tic.text = ""
            progress.progress = 0.0
            progressInd.isVisible = true
            thread(true) {
                selected.forEach {
                    it.packagenameProperty().get().trim().lines().forEach { pkg ->
                        proc = pb.command("${prefix}adb", "shell", "pm", "uninstall", "--user", "$user", pkg).start()
                        val scan = Scanner(proc.inputStream, "UTF-8").useDelimiter("")
                        var output = ""
                        while (scan.hasNextLine())
                            output += scan.nextLine() + '\n'
                        scan.close()
                        Platform.runLater {
                            tic.appendText("App: ${it.appnameProperty().get()}\n")
                            tic.appendText("Package: $pkg\n")
                            tic.appendText("Result: $output\n")
                            progress.progress += 1.0 / n
                        }
                    }
                }
                Platform.runLater {
                    tic.appendText("Done!")
                    progress.progress = 0.0
                    progressInd.isVisible = false
                    createTables()
                    func()
                }
            }
        }

        fun reinstall(selected: ObservableList<App>, n: Int, func: () -> Unit) {
            pb.redirectErrorStream(false)
            tic.text = ""
            progress.progress = 0.0
            progressInd.isVisible = true
            thread(true) {
                selected.forEach {
                    it.packagenameProperty().get().trim().lines().forEach { pkg ->
                        proc =
                            pb.command(
                                "${prefix}adb",
                                "shell",
                                "cmd",
                                "package",
                                "install-existing",
                                "--user",
                                "$user",
                                pkg
                            )
                                .start()
                        val scan = Scanner(proc.inputStream, "UTF-8").useDelimiter("")
                        var output = ""
                        while (scan.hasNextLine())
                            output += scan.nextLine() + '\n'
                        scan.close()
                        output = if ("installed for user" in output)
                            "Success\n"
                        else "Failure [${output.substringAfter(pkg).trim()}]\n"
                        Platform.runLater {
                            tic.appendText("App: ${it.appnameProperty().get()}\n")
                            tic.appendText("Package: $pkg\n")
                            tic.appendText("Result: $output\n")
                            progress.progress += 1.0 / n
                        }
                    }
                }
                Platform.runLater {
                    tic.appendText("Done!")
                    progress.progress = 0.0
                    progressInd.isVisible = false
                    createTables()
                    func()
                }
            }
        }

        fun disable(selected: ObservableList<App>, n: Int, func: () -> Unit) {
            pb.redirectErrorStream(false)
            tic.text = ""
            progress.progress = 0.0
            progressInd.isVisible = true
            thread(true) {
                selected.forEach {
                    it.packagenameProperty().get().trim().lines().forEach { pkg ->
                        proc = pb.command("${prefix}adb", "shell", "pm", "disable-user", "--user", "$user", pkg).start()
                        val scan = Scanner(proc.inputStream, "UTF-8").useDelimiter("")
                        var output = ""
                        while (scan.hasNextLine())
                            output += scan.nextLine() + '\n'
                        scan.close()
                        output = if ("disabled-user" in output)
                            "Success\n"
                        else "Failure\n"
                        Platform.runLater {
                            tic.appendText("App: ${it.appnameProperty().get()}\n")
                            tic.appendText("Package: $pkg\n")
                            tic.appendText("Result: $output\n")
                            progress.progress += 1.0 / n
                        }
                    }
                }
                Platform.runLater {
                    tic.appendText("Done!")
                    progress.progress = 0.0
                    progressInd.isVisible = false
                    createTables()
                    func()
                }
            }
        }

        fun enable(selected: ObservableList<App>, n: Int, func: () -> Unit) {
            pb.redirectErrorStream(false)
            tic.text = ""
            progress.progress = 0.0
            progressInd.isVisible = true
            thread(true) {
                selected.forEach {
                    it.packagenameProperty().get().trim().lines().forEach { pkg ->
                        proc = pb.command("${prefix}adb", "shell", "pm", "enable", "--user", "$user", pkg).start()
                        val scan = Scanner(proc.inputStream, "UTF-8").useDelimiter("")
                        var output = ""
                        while (scan.hasNextLine())
                            output += scan.nextLine() + '\n'
                        scan.close()
                        output = if ("enabled" in output)
                            "Success\n"
                        else "Failure\n"
                        Platform.runLater {
                            tic.appendText("App: ${it.appnameProperty().get()}\n")
                            tic.appendText("Package: $pkg\n")
                            tic.appendText("Result: $output\n")
                            progress.progress += 1.0 / n
                        }
                    }
                }
                Platform.runLater {
                    tic.appendText("Done!")
                    progress.progress = 0.0
                    progressInd.isVisible = false
                    createTables()
                    func()
                }
            }
        }
    }
}
