import javafx.application.Application
import javafx.application.Platform
import javafx.event.EventHandler
import javafx.fxml.FXMLLoader
import javafx.geometry.Pos
import javafx.scene.Parent
import javafx.scene.Scene
import javafx.scene.control.Alert
import javafx.scene.control.Hyperlink
import javafx.scene.image.Image
import javafx.scene.image.ImageView
import javafx.scene.layout.VBox
import javafx.scene.text.Font
import javafx.stage.Stage
import javafx.stage.StageStyle
import java.awt.Desktop
import java.io.File
import java.io.IOException
import java.net.HttpURLConnection
import java.net.URI
import java.net.URL


class XiaomiADBFastbootTools : Application() {

    private val command = Command()
    private val tmp = File(System.getProperty("user.home"), "xaft_tmp")

    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            launch(XiaomiADBFastbootTools::class.java)
        }
    }

    private fun createFile(file: String, exec: Boolean) {
        val bytes = this.javaClass.classLoader.getResourceAsStream(file).readBytes()
        val newfile = if ('/' in file)
            File(tmp, file.substringAfterLast('/'))
        else File(tmp, file)
        if (!newfile.exists()) {
            try {
                newfile.createNewFile()
                newfile.writeBytes(bytes)
            } catch (ex: IOException) {
                ex.printStackTrace()
                ExceptionAlert(ex)
            }
        }
        newfile.setExecutable(exec, false)
    }

    fun setupFiles() {
        val os = System.getProperty("os.name").toLowerCase()
        tmp.mkdir()
        createFile("dummy.img", false)
        when {
            "win" in os -> {
                createFile("windows/adb.exe", true)
                createFile("windows/fastboot.exe", true)
                createFile("windows/AdbWinApi.dll", false)
                createFile("windows/AdbWinUsbApi.dll", false)
            }
            "mac" in os -> {
                createFile("darwin/adb", true)
                createFile("darwin/fastboot", true)
            }
            else -> {
                createFile("linux/adb", true)
                createFile("linux/fastboot", true)
            }
        }
    }

    fun versionToInt(ver: String): Int {
        val bits = ("$ver.0").split('.')
        return bits[0].toInt() * 100 + bits[1].toInt() * 10 + bits[2].toInt()
    }

    fun checkVersion() {
        val huc =
            URL("https://github.com/Saki-EU/XiaomiADBFastbootTools/releases/latest").openConnection() as HttpURLConnection
        huc.requestMethod = "GET"
        huc.setRequestProperty("Referer", "https://github.com/")
        huc.instanceFollowRedirects = false
        try {
            huc.connect()
            huc.disconnect()
        } catch (e: IOException) {
            return
        }
        val link = huc.getHeaderField("Location")
        val latest = link.substringAfterLast('/')
        if (versionToInt(latest) > versionToInt(MainController.version)) {
            val alert = Alert(Alert.AlertType.INFORMATION)
            alert.initStyle(StageStyle.UTILITY)
            alert.title = "New version available!"
            alert.graphic = ImageView("mitu.png")
            alert.headerText =
                "Version $latest is available!"
            val vb = VBox()
            vb.alignment = Pos.CENTER
            val download = Hyperlink("Download")
            download.onAction = EventHandler {
                if ("linux" in System.getProperty("os.name").toLowerCase())
                    Runtime.getRuntime().exec("xdg-open $link")
                else Desktop.getDesktop().browse(URI(link))
            }
            download.font = Font(15.0)
            vb.children.add(download)
            alert.dialogPane.content = vb
            alert.showAndWait()
        }
    }

    @Throws(Exception::class)
    override fun start(stage: Stage) {
        if (tmp.exists() && !tmp.deleteRecursively()) {
            val alert = Alert(Alert.AlertType.ERROR)
            alert.title = "Fatal Error"
            alert.headerText = "ERROR: Please kill adb in Task Manager and try again!"
            alert.showAndWait()
            Platform.exit()
        }
        setupFiles()
        val root = FXMLLoader.load<Parent>(javaClass.classLoader.getResource("Main.fxml"))
        val scene = Scene(root)
        stage.scene = scene
        stage.title = "Xiaomi ADB/Fastboot Tools"
        stage.icons.add(Image("icon.png"))
        stage.show()
        stage.isResizable = true
        checkVersion()
        if (!File(tmp, "adb").exists() && !File(tmp, "adb.exe").exists()) {
            val alert = Alert(Alert.AlertType.ERROR)
            alert.title = "Fatal Error"
            alert.headerText = "ERROR: Couldn't initialize ADB!"
            alert.showAndWait()
            Platform.exit()
        }
    }

    override fun stop() {
        MainController.thread.interrupt()
        command.exec("adb kill-server")
        while (tmp.exists() && !tmp.deleteRecursively())
            Thread.sleep(500)
    }

}
