package app

import javafx.application.Platform
import javafx.event.ActionEvent
import javafx.event.EventHandler
import javafx.fxml.FXML
import javafx.fxml.Initializable
import javafx.geometry.Pos
import javafx.scene.Node
import javafx.scene.control.*
import javafx.scene.control.Alert.AlertType
import javafx.scene.control.cell.CheckBoxTableCell
import javafx.scene.control.cell.PropertyValueFactory
import javafx.scene.image.Image
import javafx.scene.image.ImageView
import javafx.scene.layout.VBox
import javafx.scene.text.Font
import javafx.stage.DirectoryChooser
import javafx.stage.FileChooser
import javafx.stage.StageStyle
import java.awt.Desktop
import java.awt.Toolkit
import java.awt.datatransfer.StringSelection
import java.io.File
import java.io.FileWriter
import java.io.IOException
import java.net.HttpURLConnection
import java.net.URI
import java.net.URL
import java.util.*

class MainWindowController : Initializable {

    @FXML
    private lateinit var optionsMenu: Menu
    @FXML
    private lateinit var rebootMenu: Menu
    @FXML
    private lateinit var systemMenuItem: MenuItem
    @FXML
    private lateinit var recoveryMenuItem: MenuItem
    @FXML
    private lateinit var fastbootMenuItem: MenuItem
    @FXML
    private lateinit var edlMenuItem: MenuItem
    @FXML
    private lateinit var aboutMenuItem: MenuItem
    @FXML
    private lateinit var serialLabel: Label
    @FXML
    private lateinit var codenameLabel: Label
    @FXML
    private lateinit var bootloaderLabel: Label
    @FXML
    private lateinit var antiLabel: Label
    @FXML
    private lateinit var outputTextArea: TextArea
    @FXML
    private lateinit var progressBar: ProgressBar
    @FXML
    private lateinit var progressIndicator: ProgressIndicator
    @FXML
    private lateinit var uninstallerTableView: TableView<App>
    @FXML
    private lateinit var checkTableColumn: TableColumn<App, Boolean>
    @FXML
    private lateinit var appTableColumn: TableColumn<App, String>
    @FXML
    private lateinit var packageTableColumn: TableColumn<App, String>
    @FXML
    private lateinit var customappTextField: TextField
    @FXML
    private lateinit var uninstallButton: Button
    @FXML
    private lateinit var freezeButton: Button
    @FXML
    private lateinit var addButton: Button
    @FXML
    private lateinit var reboottwrpButton: Button
    @FXML
    private lateinit var disableButton: Button
    @FXML
    private lateinit var enableButton: Button
    @FXML
    private lateinit var disableEISButton: Button
    @FXML
    private lateinit var enableEISButton: Button
    @FXML
    private lateinit var readpropertiesButton: Button
    @FXML
    private lateinit var savepropertiesButton: Button
    @FXML
    private lateinit var dpiTextField: TextField
    @FXML
    private lateinit var dpiButton: Button
    @FXML
    private lateinit var antirbButton: Button
    @FXML
    private lateinit var browseimageButton: Button
    @FXML
    private lateinit var browseromButton: Button
    @FXML
    private lateinit var partitionComboBox: ComboBox<String>
    @FXML
    private lateinit var scriptComboBox: ComboBox<String>
    @FXML
    private lateinit var flashimageButton: Button
    @FXML
    private lateinit var flashromButton: Button
    @FXML
    private lateinit var autobootCheckBox: CheckBox
    @FXML
    private lateinit var imageLabel: Label
    @FXML
    private lateinit var romLabel: Label
    @FXML
    private lateinit var bootButton: Button
    @FXML
    private lateinit var cacheButton: Button
    @FXML
    private lateinit var dataButton: Button
    @FXML
    private lateinit var cachedataButton: Button
    @FXML
    private lateinit var unlockButton: Button
    @FXML
    private lateinit var lockButton: Button
    @FXML
    private lateinit var codenameTextField: TextField
    @FXML
    private lateinit var branchComboBox: ComboBox<String>
    @FXML
    private lateinit var getlinkButton: Button
    @FXML
    private lateinit var downloadromButton: Button
    @FXML
    private lateinit var versionLabel: Label
    @FXML
    private lateinit var uninstallerPane: TitledPane
    @FXML
    private lateinit var camera2Pane: TitledPane
    @FXML
    private lateinit var devicepropertiesPane: TitledPane
    @FXML
    private lateinit var flasherPane: TitledPane
    @FXML
    private lateinit var wiperPane: TitledPane
    @FXML
    private lateinit var oemPane: TitledPane
    @FXML
    private lateinit var dpiPane: TitledPane

    var image: File? = null
    var rom: File? = null
    val comm = Command()
    val device = Device()
    lateinit var displayedcomm: Command
    lateinit var uninstaller: Uninstaller

    fun setUI() {
        when (device.mode) {
            0 -> {
                serialLabel.text = "-"
                bootloaderLabel.text = "-"
                codenameLabel.text = "-"
                antiLabel.text = "-"

                uninstallerPane.isDisable = true
                camera2Pane.isDisable = true
                devicepropertiesPane.isDisable = true
                dpiPane.isDisable = true
                flasherPane.isDisable = true
                wiperPane.isDisable = true
                oemPane.isDisable = true
                rebootMenu.isDisable = true
                recoveryMenuItem.isDisable = true
            }
            1 -> {
                serialLabel.text = device.serial
                codenameLabel.text = device.codename
                codenameTextField.text = device.codename
                if (device.bootloader)
                    bootloaderLabel.text = "unlocked"
                else bootloaderLabel.text = "locked"
                if (device.anti != -1)
                    antiLabel.text = device.anti.toString()
                else antiLabel.text = "unknown"

                uninstallerPane.isDisable = false
                camera2Pane.isDisable = false
                devicepropertiesPane.isDisable = false
                dpiPane.isDisable = device.recovery
                flasherPane.isDisable = true
                wiperPane.isDisable = true
                oemPane.isDisable = true
                rebootMenu.isDisable = false
                recoveryMenuItem.isDisable = false
            }
            2 -> {
                serialLabel.text = device.serial
                codenameLabel.text = device.codename
                codenameTextField.text = device.codename
                if (device.bootloader)
                    bootloaderLabel.text = "unlocked"
                else bootloaderLabel.text = "locked"
                if (device.anti != -1)
                    antiLabel.text = device.anti.toString()
                else antiLabel.text = "unknown"

                uninstallerPane.isDisable = true
                camera2Pane.isDisable = true
                devicepropertiesPane.isDisable = true
                dpiPane.isDisable = true
                flasherPane.isDisable = false
                wiperPane.isDisable = false
                oemPane.isDisable = false
                rebootMenu.isDisable = false
                recoveryMenuItem.isDisable = true
            }
        }
    }

    fun checkADB(): Boolean {
        if (device.readADB()) {
            setUI()
            return true
        }
        return false
    }

    fun checkFastboot(): Boolean {
        if (device.readFastboot()) {
            setUI()
            return true
        }
        return false
    }

    override fun initialize(url: URL, rb: ResourceBundle?) {
        setUI()
        outputTextArea.text = "Looking for devices..."
        progressIndicator.isVisible = true
        partitionComboBox.items.addAll(
                "boot", "cust", "modem", "persist", "recovery", "system")
        scriptComboBox.items.addAll(
                "Clean install", "Clean install and lock", "Update")
        branchComboBox.items.addAll(
                "Global Stable", "Global Developer", "China Stable", "China Developer")

        checkTableColumn.cellValueFactory = PropertyValueFactory("selected")
        checkTableColumn.setCellFactory { _ -> CheckBoxTableCell() }
        appTableColumn.cellValueFactory = PropertyValueFactory("appname")
        packageTableColumn.cellValueFactory = PropertyValueFactory("packagename")
        uninstallerTableView.columns.setAll(checkTableColumn, appTableColumn, packageTableColumn)

        displayedcomm = Command(outputTextArea)
        uninstaller = Uninstaller(uninstallerTableView, progressBar, progressIndicator, outputTextArea)

        val t = Thread {
            comm.exec("adb start-server")
            while (true) {
                if (device.mode == 0) {
                    outputTextArea.text = "Looking for devices..."
                    progressIndicator.isVisible = true
                    if (device.readADB()) {
                        progressIndicator.isVisible = false
                        outputTextArea.text = "Device found in ADB mode!"
                        uninstaller.loadApps(device)
                        Platform.runLater { setUI() }
                        if (!device.recovery) {
                            if (device.dpi != -1)
                                dpiTextField.text = device.dpi.toString()
                            else dpiTextField.text = "ERROR"
                        }
                        continue
                    }
                    if (device.readFastboot()) {
                        progressIndicator.isVisible = false
                        outputTextArea.text = "Device found in Fastboot mode!"
                        Platform.runLater { setUI() }
                        continue
                    }
                    Platform.runLater { setUI() }
                    if (device.auth)
                        outputTextArea.text = "Unauthorised device found!\nPlease allow USB debugging!"
                }
                try {
                    Thread.sleep(1000)
                } catch (ex: InterruptedException) {
                    comm.exec("adb kill-server")
                }
            }
        }
        t.isDaemon = true
        t.start()
    }

    @FXML
    private fun reboottwrpButtonPressed(event: ActionEvent) {
        if (checkADB()) {
            if (device.recovery) {
                outputTextArea.text = "Device already in recovery mode!"
            } else {
                comm.exec("adb reboot recovery")
            }
        }
    }

    fun checkcamera2(): Boolean {
        return comm.exec("adb shell getprop persist.camera.HAL3.enabled").contains("1")
    }

    fun checkEIS(): Boolean {
        return comm.exec("adb shell getprop persist.camera.eis.enable").contains("1")
    }

    @FXML
    private fun disableButtonPressed(event: ActionEvent) {
        if (checkADB()) {
            if (!device.recovery) {
                outputTextArea.text = "ERROR: No device found in recovery mode!"
                return
            }
            comm.exec("adb shell setprop persist.camera.HAL3.enabled 0")
            if (!checkcamera2())
                outputTextArea.text = "Camera2 disabled!"
            else outputTextArea.text = "ERROR: Couldn't disable Camera2!"
        }
    }

    @FXML
    private fun enableButtonPressed(event: ActionEvent) {
        if (checkADB()) {
            if (!device.recovery) {
                outputTextArea.text = "ERROR: No device found in recovery mode!"
                return
            }
            comm.exec("adb shell setprop persist.camera.HAL3.enabled 1")
            if (checkcamera2())
                outputTextArea.text = "Camera2 enabled!"
            else outputTextArea.text = "ERROR: Couldn't enable Camera2!"
        }
    }

    @FXML
    private fun disableEISButtonPressed(event: ActionEvent) {
        if (checkADB()) {
            if (!device.recovery) {
                outputTextArea.text = "ERROR: No device found in recovery mode!"
                return
            }
            comm.exec("adb shell setprop persist.camera.eis.enable 0")
            if (!checkEIS())
                outputTextArea.text = "EIS disabled!"
            else outputTextArea.text = "ERROR: Couldn't disable EIS!"
        }
    }

    @FXML
    private fun enableEISButtonPressed(event: ActionEvent) {
        if (checkADB()) {
            if (!device.recovery) {
                outputTextArea.text = "ERROR: No device found in recovery mode!"
                return
            }
            comm.exec("adb shell setprop persist.camera.eis.enable 1")
            if (checkEIS())
                outputTextArea.text = "EIS enabled!"
            else outputTextArea.text = "ERROR: Couldn't enable EIS!"
        }
    }

    @FXML
    private fun readpropertiesButtonPressed(event: ActionEvent) {
        if (checkADB())
            displayedcomm.exec("adb shell getprop")
    }

    @FXML
    private fun savepropertiesButtonPressed(event: ActionEvent) {
        if (checkADB()) {
            val fc = FileChooser()
            val fileExtensions = FileChooser.ExtensionFilter("Text File (.txt)", "*.txt")
            fc.extensionFilters.add(fileExtensions)
            fc.title = "Save properties"
            val f = fc.showSaveDialog((event.source as Node).scene.window)
            when {
                f != null -> try {
                    val fw = FileWriter(f)
                    fw.write(comm.exec("adb shell getprop"))
                    fw.flush()
                    fw.close()
                } catch (ex: IOException) {
                    ex.printStackTrace()
                }
            }
        }
    }

    @FXML
    private fun dpiButtonPressed(event: ActionEvent) {
        if (checkADB()) {
            val attempt = displayedcomm.exec("adb shell wm density ${dpiTextField.text.trim()}")
            when {
                attempt.contains("permission") -> {
                    outputTextArea.text = "ERROR: Please allow USB debugging (Security settings)!"
                }
                attempt.contains("bad number") -> {
                    outputTextArea.text = "ERROR: Invalid value!"
                }
                attempt.isEmpty() -> {
                    outputTextArea.text = "Done!\nIf you notice any weird behaviour, please reboot the device."
                }
                else -> {
                    outputTextArea.text = "ERROR: Unexpected result!\n\n$attempt"
                }
            }
        }
    }

    @FXML
    private fun antirbButtonPressed(event: ActionEvent) {
        if (checkFastboot()) {
            val result = comm.exec("fastboot flash antirbpass dummy.img")
            if (result.contains("FAILED"))
                displayedcomm.exec("fastboot oem ignore_anti")
            else outputTextArea.text = result
        }
    }

    @FXML
    private fun browseimageButtonPressed(event: ActionEvent) {
        val fc = FileChooser()
        val fileExtensions = FileChooser.ExtensionFilter("Image File", "*.*")
        fc.extensionFilters.add(fileExtensions)
        fc.title = "Select an image"
        image = fc.showOpenDialog((event.source as Node).scene.window)
        imageLabel.text = image?.name
    }

    @FXML
    private fun flashimageButtonPressed(event: ActionEvent) {
        when {
            image != null && partitionComboBox.value != null ->
                if (image!!.absolutePath.isNotEmpty() && partitionComboBox.value.trim().isNotEmpty() && checkFastboot()) {
                    if (autobootCheckBox.isSelected && partitionComboBox.value.trim() == "recovery")
                        displayedcomm.exec(image, "fastboot flash ${partitionComboBox.value.trim()}", "fastboot boot")
                    else displayedcomm.exec(image, "fastboot flash ${partitionComboBox.value.trim()}")
                }
        }
    }

    @FXML
    private fun browseromButtonPressed(event: ActionEvent) {
        val dc = DirectoryChooser()
        dc.title = "Select the root directory of a Fastboot ROM"
        rom = dc.showDialog((event.source as Node).scene.window)
        outputTextArea.text = ""
        romLabel.text = "-"
        when {
            rom != null ->
                if (File(rom, "images").exists()) {
                    romLabel.text = rom?.name
                    outputTextArea.text = "Fastboot ROM found!"
                    File(rom, "flash_all.sh").setExecutable(true, false)
                    File(rom, "flash_all_lock.sh").setExecutable(true, false)
                    File(rom, "flash_all_except_storage.sh").setExecutable(true, false)
                    File(rom, "flash_all_except_data.sh").setExecutable(true, false)
                    File(rom, "flash_all_except_data_storage.sh").setExecutable(true, false)
                } else {
                    outputTextArea.text = "ERROR: Fastboot ROM not found!"
                    romLabel.text = "-"
                    rom = null
                }
        }
    }

    @FXML
    private fun flashromButtonPressed(event: ActionEvent) {
        when {
            rom != null && scriptComboBox.value != null ->
                if (checkFastboot()) {
                    val fs = FastbootFlasher(progressBar, progressIndicator, outputTextArea, rom!!)
                    progressBar.progress = 0.0
                    uninstallerPane.isDisable = true
                    camera2Pane.isDisable = true
                    devicepropertiesPane.isDisable = true
                    dpiPane.isDisable = true
                    flasherPane.isDisable = true
                    wiperPane.isDisable = true
                    oemPane.isDisable = true
                    rebootMenu.isDisable = true
                    recoveryMenuItem.isDisable = true
                    when (scriptComboBox.value) {
                        "Clean install" -> fs.exec("flash_all")
                        "Clean install and lock" -> fs.exec("flash_all_lock")
                        "Update" -> when {
                            File(rom, "flash_all_except_storage.sh").exists() -> fs.exec("flash_all_except_storage")
                            File(rom, "flash_all_except_data.sh").exists() -> fs.exec("flash_all_except_data")
                            File(rom, "flash_all_except_data_storage.sh").exists() -> fs.exec("flash_all_except_data_storage")
                        }
                    }
                }
        }
    }

    @FXML
    private fun bootButtonPressed(event: ActionEvent) {
        when {
            image != null ->
                if (image!!.absolutePath.isNotEmpty() && checkFastboot())
                    displayedcomm.exec(image, "fastboot boot")
        }
    }

    @FXML
    private fun cacheButtonPressed(event: ActionEvent) {
        if (checkFastboot())
            displayedcomm.exec("fastboot erase cache")
    }

    @FXML
    private fun dataButtonPressed(event: ActionEvent) {
        if (checkFastboot())
            displayedcomm.exec("fastboot erase userdata")
    }

    @FXML
    private fun cachedataButtonPressed(event: ActionEvent) {
        if (checkFastboot())
            displayedcomm.exec("fastboot erase cache", "fastboot erase userdata")
    }

    @FXML
    private fun lockButtonPressed(event: ActionEvent) {
        if (checkFastboot())
            displayedcomm.exec("fastboot oem lock")
    }

    @FXML
    private fun unlockButtonPressed(event: ActionEvent) {
        if (checkFastboot())
            displayedcomm.exec("fastboot oem unlock")
    }

    @FXML
    private fun getlinkButtonPressed(event: ActionEvent) {
        if (codenameTextField.text.trim().isNotEmpty() && branchComboBox.value != null) {
            val codename = codenameTextField.text.trim()
            var url = URL("http://google.com")
            when (branchComboBox.value) {
                "Global Stable" -> url = URL("http://update.miui.com/updates/v1/fullromdownload.php?d=${codename}_global&b=F&r=global&n=")
                "Global Developer" -> url = URL("http://update.miui.com/updates/v1/fullromdownload.php?d=${codename}_global&b=X&r=global&n=")
                "China Stable" -> url = URL("http://update.miui.com/updates/v1/fullromdownload.php?d=${codename}&b=F&r=cn&n=")
                "China Developer" -> url = URL("http://update.miui.com/updates/v1/fullromdownload.php?d=${codename}&b=X&r=cn&n=")
            }
            val huc = url.openConnection() as HttpURLConnection
            huc.requestMethod = "GET"
            huc.setRequestProperty("Referer", "http://en.miui.com/a-234.html")
            huc.instanceFollowRedirects = false
            huc.connect()
            huc.disconnect()
            val link = huc.getHeaderField("Location")
            if (link != null && link.contains("bigota")) {
                versionLabel.text = link.substringAfter(".com/").substringBefore("/")
                outputTextArea.text = "${link}\n\nLink copied to clipboard!"
                Toolkit.getDefaultToolkit().systemClipboard.setContents(StringSelection(link), null)
            } else {
                versionLabel.text = "-"
                outputTextArea.text = "Link not found!"
            }
        }
    }

    @FXML
    private fun downloadromButtonPressed(event: ActionEvent) {
        if (codenameTextField.text.trim().isNotEmpty() && branchComboBox.value != null) {
            val codename = codenameTextField.text.trim()
            var url = URL("http://google.com")
            when (branchComboBox.value) {
                "Global Stable" -> url = URL("http://update.miui.com/updates/v1/fullromdownload.php?d=${codename}_global&b=F&r=global&n=")
                "Global Developer" -> url = URL("http://update.miui.com/updates/v1/fullromdownload.php?d=${codename}_global&b=X&r=global&n=")
                "China Stable" -> url = URL("http://update.miui.com/updates/v1/fullromdownload.php?d=${codename}&b=F&r=cn&n=")
                "China Developer" -> url = URL("http://update.miui.com/updates/v1/fullromdownload.php?d=${codename}&b=X&r=cn&n=")
            }
            val huc = url.openConnection() as HttpURLConnection
            huc.requestMethod = "GET"
            huc.setRequestProperty("Referer", "http://en.miui.com/a-234.html")
            huc.instanceFollowRedirects = false
            huc.connect()
            huc.disconnect()
            val link = huc.getHeaderField("Location")
            if (link != null && link.contains("bigota")) {
                versionLabel.text = link.substringAfter(".com/").substringBefore("/")
                outputTextArea.text = "Starting download in browser..."
                if (System.getProperty("os.name").toLowerCase().contains("linux"))
                    Runtime.getRuntime().exec("xdg-open ${link}")
                else Desktop.getDesktop().browse(URI(link))
            } else {
                versionLabel.text = "-"
                outputTextArea.text = "Link not found!"
            }
        }
    }

    @FXML
    private fun systemMenuItemPressed(event: ActionEvent) {
        when (device.mode) {
            1 -> if (checkADB()) comm.exec("adb reboot")
            2 -> if (checkFastboot()) comm.exec("fastboot reboot")
        }
    }

    @FXML
    private fun recoveryMenuItemPressed(event: ActionEvent) {
        when (device.mode) {
            1 -> if (checkADB()) comm.exec("adb reboot recovery")
        }
    }

    @FXML
    private fun fastbootMenuItemPressed(event: ActionEvent) {
        when (device.mode) {
            1 -> if (checkADB()) comm.exec("adb reboot bootloader")
            2 -> if (checkFastboot()) comm.exec("fastboot reboot bootloader")
        }
    }

    @FXML
    private fun edlMenuItemPressed(event: ActionEvent) {
        when (device.mode) {
            1 -> if (checkADB()) comm.exec("adb reboot edl")
            2 -> if (checkFastboot()) comm.exec("fastboot oem edl")
        }
    }

    @FXML
    private fun freezeButtonPressed(event: ActionEvent) {
        if (checkADB()) {
            progressBar.progress = 0.0
            uninstallerPane.isDisable = true
            camera2Pane.isDisable = true
            devicepropertiesPane.isDisable = true
            dpiPane.isDisable = true
            flasherPane.isDisable = true
            wiperPane.isDisable = true
            oemPane.isDisable = true
            rebootMenu.isDisable = true
            recoveryMenuItem.isDisable = true
            uninstaller.freeze {               uninstallerPane.isDisable = false
                camera2Pane.isDisable = false
                devicepropertiesPane.isDisable = false
                dpiPane.isDisable = device.recovery
                flasherPane.isDisable = true
                wiperPane.isDisable = true
                oemPane.isDisable = true
                rebootMenu.isDisable = false
                recoveryMenuItem.isDisable = false
            }
        }
    }

    @FXML
    private fun uninstallButtonPressed(event: ActionEvent) {
        if (checkADB()) {
            progressBar.progress = 0.0
            uninstallerPane.isDisable = true
            camera2Pane.isDisable = true
            devicepropertiesPane.isDisable = true
            dpiPane.isDisable = true
            flasherPane.isDisable = true
            wiperPane.isDisable = true
            oemPane.isDisable = true
            rebootMenu.isDisable = true
            recoveryMenuItem.isDisable = true
            uninstaller.uninstall {
                uninstallerPane.isDisable = false
                camera2Pane.isDisable = false
                devicepropertiesPane.isDisable = false
                dpiPane.isDisable = device.recovery
                flasherPane.isDisable = true
                wiperPane.isDisable = true
                oemPane.isDisable = true
                rebootMenu.isDisable = false
                recoveryMenuItem.isDisable = false
            }
        }
    }

    @FXML
    private fun addButtonPressed(event: ActionEvent) {
        when {
            customappTextField.text != null ->
                if (customappTextField.text.trim().isNotEmpty())
                    uninstaller.apps.add(App("Custom app", customappTextField.text.trim()))
        }
        customappTextField.text = null
        uninstaller.tv.refresh()
    }

    @FXML
    private fun aboutMenuItemPressed(event: ActionEvent) {
        val alert = Alert(AlertType.INFORMATION)
        alert.initStyle(StageStyle.UTILITY)
        alert.title = "About"
        alert.graphic = ImageView(Image(this.javaClass.classLoader.getResource("smallicon.png").toString()))
        alert.headerText = "Xiaomi ADB/Fastboot Tools${System.lineSeparator()}Version 5.2.1${System.lineSeparator()}Created by Saki_EU"
        val vb = VBox()
        vb.alignment = Pos.CENTER


        val discord = Hyperlink("Xiaomi Discord")
        discord.onAction = EventHandler {
            if (System.getProperty("os.name").toLowerCase().contains("linux"))
                Runtime.getRuntime().exec("xdg-open https://discord.gg/xiaomi")
            else Desktop.getDesktop().browse(URI("https://discord.gg/xiaomi"))
        }
        discord.font = Font(14.0)
        val twitter = Hyperlink("Twitter")
        twitter.onAction = EventHandler {
            if (System.getProperty("os.name").toLowerCase().contains("linux"))
                Runtime.getRuntime().exec("xdg-open https://twitter.com/Saki_EU")
            else Desktop.getDesktop().browse(URI("https://twitter.com/Saki_EU"))
        }
        twitter.font = Font(14.0)
        val github = Hyperlink("Project page on GitHub")
        github.onAction = EventHandler {
            if (System.getProperty("os.name").toLowerCase().contains("linux"))
                Runtime.getRuntime().exec("xdg-open https://github.com/Saki-EU/XiaomiADBFastbootTools")
            else Desktop.getDesktop().browse(URI("https://github.com/Saki-EU/XiaomiADBFastbootTools"))
        }
        github.font = Font(14.0)
        vb.children.addAll(discord, twitter, github)
        alert.dialogPane.content = vb
        alert.showAndWait()
    }
}
