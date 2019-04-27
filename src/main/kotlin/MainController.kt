import javafx.application.Platform
import javafx.event.ActionEvent
import javafx.event.EventHandler
import javafx.fxml.FXML
import javafx.fxml.FXMLLoader
import javafx.fxml.Initializable
import javafx.geometry.Pos
import javafx.scene.Node
import javafx.scene.Parent
import javafx.scene.Scene
import javafx.scene.control.*
import javafx.scene.control.Alert.AlertType
import javafx.scene.control.cell.CheckBoxTableCell
import javafx.scene.control.cell.PropertyValueFactory
import javafx.scene.image.ImageView
import javafx.scene.layout.VBox
import javafx.scene.text.Font
import javafx.stage.*
import java.awt.Desktop
import java.awt.Toolkit
import java.awt.datatransfer.StringSelection
import java.io.File
import java.io.IOException
import java.net.HttpURLConnection
import java.net.URI
import java.net.URL
import java.util.*

class MainController : Initializable {

    @FXML
    private lateinit var deviceMenu: Menu
    @FXML
    private lateinit var recoveryMenuItem: MenuItem
    @FXML
    private lateinit var reloadMenuItem: MenuItem
    @FXML
    private lateinit var infoTextArea: TextArea
    @FXML
    private lateinit var outputTextArea: TextArea
    @FXML
    private lateinit var progressBar: ProgressBar
    @FXML
    private lateinit var progressIndicator: ProgressIndicator
    @FXML
    private lateinit var uninstallerTableView: TableView<App>
    @FXML
    private lateinit var reinstallerTableView: TableView<App>
    @FXML
    private lateinit var uncheckTableColumn: TableColumn<App, Boolean>
    @FXML
    private lateinit var unappTableColumn: TableColumn<App, String>
    @FXML
    private lateinit var unpackageTableColumn: TableColumn<App, String>
    @FXML
    private lateinit var recheckTableColumn: TableColumn<App, Boolean>
    @FXML
    private lateinit var reappTableColumn: TableColumn<App, String>
    @FXML
    private lateinit var repackageTableColumn: TableColumn<App, String>
    @FXML
    private lateinit var dpiTextField: TextField
    @FXML
    private lateinit var widthTextField: TextField
    @FXML
    private lateinit var heightTextField: TextField
    @FXML
    private lateinit var partitionComboBox: ComboBox<String>
    @FXML
    private lateinit var scriptComboBox: ComboBox<String>
    @FXML
    private lateinit var autobootCheckBox: CheckBox
    @FXML
    private lateinit var imageLabel: Label
    @FXML
    private lateinit var romLabel: Label
    @FXML
    private lateinit var codenameTextField: TextField
    @FXML
    private lateinit var branchComboBox: ComboBox<String>
    @FXML
    private lateinit var versionLabel: Label
    @FXML
    private lateinit var installerPane: TabPane
    @FXML
    private lateinit var reinstallerTab: Tab
    @FXML
    private lateinit var fileExplorerPane: TitledPane
    @FXML
    private lateinit var camera2Pane: TitledPane
    @FXML
    private lateinit var resolutionPane: TitledPane
    @FXML
    private lateinit var flasherPane: TitledPane
    @FXML
    private lateinit var wiperPane: TitledPane
    @FXML
    private lateinit var oemPane: TitledPane
    @FXML
    private lateinit var dpiPane: TitledPane

    private var image: File? = null
    private var rom: File? = null
    private val comm = Command()
    private val device = Device()
    private lateinit var displayedcomm: Command
    private lateinit var flasher: Flasher
    private lateinit var installer: Installer

    companion object {
        val version = "6.2.6"
        lateinit var thread: Thread
    }

    private fun setPanels(mode: Int) {
        when (mode) {
            0 -> {
                installerPane.isDisable = true
                camera2Pane.isDisable = true
                fileExplorerPane.isDisable = true
                resolutionPane.isDisable = true
                dpiPane.isDisable = true
                flasherPane.isDisable = true
                wiperPane.isDisable = true
                oemPane.isDisable = true
                deviceMenu.isDisable = true
                recoveryMenuItem.isDisable = true
                reloadMenuItem.isDisable = true
            }
            1 -> {
                installerPane.isDisable = false
                reinstallerTab.isDisable = !device.reinstaller
                camera2Pane.isDisable = false
                fileExplorerPane.isDisable = false
                resolutionPane.isDisable = false
                dpiPane.isDisable = device.recovery
                flasherPane.isDisable = true
                wiperPane.isDisable = true
                oemPane.isDisable = true
                deviceMenu.isDisable = false
                recoveryMenuItem.isDisable = false
                reloadMenuItem.isDisable = false
            }
            2 -> {
                installerPane.isDisable = true
                camera2Pane.isDisable = true
                fileExplorerPane.isDisable = true
                resolutionPane.isDisable = true
                dpiPane.isDisable = true
                flasherPane.isDisable = false
                wiperPane.isDisable = false
                oemPane.isDisable = false
                deviceMenu.isDisable = false
                recoveryMenuItem.isDisable = true
                reloadMenuItem.isDisable = false
            }
        }
    }

    private fun setUI() {
        setPanels(device.mode)
        when (device.mode) {
            0 -> infoTextArea.text = ""
            1 -> {
                infoTextArea.text = ""
                infoTextArea.appendText("Serial number:\t\t${device.serial}\n")
                infoTextArea.appendText("Codename:\t\t${device.codename}\n")
                infoTextArea.appendText("Bootloader:\t\t")
                if (device.bootloader)
                    infoTextArea.appendText("unlocked\n\n")
                else infoTextArea.appendText("locked\n\n")
            }
            2 -> {
                infoTextArea.text = ""
                infoTextArea.appendText("Serial number:\t\t${device.serial}\n")
                infoTextArea.appendText("Codename:\t\t${device.codename}\n")
                infoTextArea.appendText("Bootloader:\t\t")
                if (device.bootloader)
                    infoTextArea.appendText("unlocked\n")
                else infoTextArea.appendText("locked\n")
                if (device.anti != -1)
                    infoTextArea.appendText("Anti version:\t\t${device.anti}\n")
            }
        }
    }

    private fun checkADB(): Boolean {
        val adb = device.readADB()
        setUI()
        return adb
    }

    private fun checkFastboot(): Boolean {
        val fb = device.readFastboot()
        setUI()
        return fb
    }

    private fun loadDevice() {
        if ("Looking" !in outputTextArea.text)
            outputTextArea.text = "Looking for devices..."
        reloadMenuItem.isDisable = true
        progressIndicator.isVisible = true
        if (device.readADB()) {
            progressIndicator.isVisible = false
            installer.loadApps(device)
            codenameTextField.text = device.codename
            if (!device.recovery) {
                if (device.dpi != -1)
                    dpiTextField.text = device.dpi.toString()
                else dpiTextField.text = "ERROR"
                if (device.width != -1)
                    widthTextField.text = device.width.toString()
                else widthTextField.text = "ERROR"
                if (device.height != -1)
                    heightTextField.text = device.height.toString()
                else heightTextField.text = "ERROR"
            }
            outputTextArea.text = "Device found in ADB mode!\n\n"
            if (!device.reinstaller)
                outputTextArea.appendText("Note: The Reinstaller module doesn't support this device.")
            setUI()
            return
        }
        if (device.readFastboot()) {
            progressIndicator.isVisible = false
            codenameTextField.text = device.codename
            outputTextArea.text = "Device found in Fastboot mode!"
            setUI()
            return
        }
        if (device.auth && "Unauthorised" !in outputTextArea.text)
            outputTextArea.text = "Unauthorised device found!\nPlease allow USB debugging!"
        setUI()
    }

    private fun checkDevice() {
        thread = Thread {
            comm.exec("adb start-server")
            while (File(System.getProperty("user.home"), "xaft_tmp").exists()) {
                if (device.mode == 0)
                    Platform.runLater { loadDevice() }
                try {
                    Thread.sleep(2000)
                } catch (ie: InterruptedException) {
                    break
                }
            }
        }
        thread.isDaemon = true
        thread.start()
    }

    private fun confirm(func: () -> Unit) {
        val alert = Alert(Alert.AlertType.CONFIRMATION)
        alert.initStyle(StageStyle.UTILITY)
        alert.isResizable = false
        alert.dialogPane.prefWidth *= 0.6
        alert.dialogPane.prefHeight *= 0.6
        alert.headerText = "Are you sure?"
        val yes = ButtonType("Yes")
        val no = ButtonType("No", ButtonBar.ButtonData.CANCEL_CLOSE)
        alert.buttonTypes.setAll(yes, no)
        val result = alert.showAndWait()
        if (result.get() == yes)
            func()
    }

    override fun initialize(url: URL, rb: ResourceBundle?) {
        outputTextArea.text = "Looking for devices..."
        progressIndicator.isVisible = true
        checkDevice()
        partitionComboBox.items.addAll(
            "boot", "cust", "modem", "persist", "recovery", "system"
        )
        scriptComboBox.items.addAll(
            "Clean install", "Clean install and lock", "Update"
        )
        branchComboBox.items.addAll(
            "Global Stable",
            "Global Developer",
            "China Stable",
            "China Developer",
            "EEA Stable",
            "Russia Stable",
            "India Stable"
        )

        uncheckTableColumn.cellValueFactory = PropertyValueFactory("selected")
        uncheckTableColumn.setCellFactory { CheckBoxTableCell() }
        unappTableColumn.cellValueFactory = PropertyValueFactory("appname")
        unpackageTableColumn.cellValueFactory = PropertyValueFactory("packagename")
        recheckTableColumn.cellValueFactory = PropertyValueFactory("selected")
        recheckTableColumn.setCellFactory { CheckBoxTableCell() }
        reappTableColumn.cellValueFactory = PropertyValueFactory("appname")
        repackageTableColumn.cellValueFactory = PropertyValueFactory("packagename")
        uninstallerTableView.columns.setAll(uncheckTableColumn, unappTableColumn, unpackageTableColumn)
        reinstallerTableView.columns.setAll(recheckTableColumn, reappTableColumn, repackageTableColumn)

        displayedcomm = Command(outputTextArea)
        flasher = Flasher(outputTextArea, progressIndicator)
        installer =
            Installer(uninstallerTableView, reinstallerTableView, progressBar, progressIndicator, outputTextArea)
    }

    private fun checkcamera2(): Boolean = comm.exec("adb shell getprop persist.camera.HAL3.enabled").contains("1")

    private fun checkEIS(): Boolean = comm.exec("adb shell getprop persist.camera.eis.enable").contains("1")

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
    private fun openButtonPressed(event: ActionEvent) {
        if (checkADB()) {
            val fxmlLoader = FXMLLoader(javaClass.classLoader.getResource("FileExplorer.fxml"))
            val parent = fxmlLoader.load<Parent>()
            fxmlLoader.getController<FileExplorerController>().device = device
            val scene = Scene(parent)
            val stage = Stage()
            stage.scene = scene
            stage.initModality(Modality.APPLICATION_MODAL)
            stage.scene = scene
            stage.title = "File Explorer"
            stage.isResizable = false
            stage.showAndWait()
        }
    }

    @FXML
    private fun applyDpiButtonPressed(event: ActionEvent) {
        if (dpiTextField.text.trim().isEmpty())
            return
        if (checkADB()) {
            val attempt = displayedcomm.exec("adb shell wm density ${dpiTextField.text.trim()}")
            when {
                attempt.contains("permission") -> {
                    outputTextArea.text = "ERROR: Please allow USB debugging (Security settings)!"
                }
                attempt.contains("bad") -> {
                    outputTextArea.text = "ERROR: Invalid value!"
                }
                attempt.isEmpty() -> {
                    outputTextArea.text = "Done!"
                }
                else -> {
                    outputTextArea.text = "ERROR: $attempt"
                }
            }
        }
    }

    @FXML
    private fun resetDpiButtonPressed(event: ActionEvent) {
        if (checkADB()) {
            val attempt = displayedcomm.exec("adb shell wm density reset")
            when {
                attempt.contains("permission") -> {
                    outputTextArea.text = "ERROR: Please allow USB debugging (Security settings)!"
                }
                attempt.isEmpty() -> {
                    outputTextArea.text = "Done!"
                }
                else -> {
                    outputTextArea.text = "ERROR: $attempt"
                }
            }
        }
    }

    @FXML
    private fun applyResButtonPressed(event: ActionEvent) {
        if (widthTextField.text.trim().isEmpty() || heightTextField.text.trim().isEmpty())
            return
        if (checkADB()) {
            val attempt =
                displayedcomm.exec("adb shell wm size ${widthTextField.text.trim()}x${heightTextField.text.trim()}")
            when {
                attempt.contains("permission") -> {
                    outputTextArea.text = "ERROR: Please allow USB debugging (Security settings)!"
                }
                attempt.contains("bad") -> {
                    outputTextArea.text = "ERROR: Invalid value!"
                }
                attempt.isEmpty() -> {
                    outputTextArea.text = "Done!"
                }
                else -> {
                    outputTextArea.text = "ERROR: $attempt"
                }
            }
        }
    }

    @FXML
    private fun resetResButtonPressed(event: ActionEvent) {
        if (checkADB()) {
            val attempt = displayedcomm.exec("adb shell wm size reset")
            when {
                attempt.contains("permission") -> {
                    outputTextArea.text = "ERROR: Please allow USB debugging (Security settings)!"
                }
                attempt.isEmpty() -> {
                    outputTextArea.text = "Done!"
                }
                else -> {
                    outputTextArea.text = "ERROR: $attempt"
                }
            }
        }
    }

    @FXML
    private fun readPropertiesMenuItemPressed(event: ActionEvent) {
        when (device.mode) {
            1 -> if (checkADB()) {
                displayedcomm.exec("adb shell getprop")
            }
            2 -> if (checkFastboot()) {
                displayedcomm.exec("fastboot getvar all")
            }
        }
    }

    @FXML
    private fun savePropertiesMenuItemPressed(event: ActionEvent) {
        when (device.mode) {
            1 -> if (checkADB()) {
                val fc = FileChooser()
                fc.extensionFilters.add(FileChooser.ExtensionFilter("Text File", "*"))
                fc.title = "Save properties"
                val f = fc.showSaveDialog((event.target as MenuItem).parentPopup.ownerWindow)
                f?.let {
                    try {
                        f.writeText(comm.exec("adb shell getprop"))
                    } catch (ex: Exception) {
                        ex.printStackTrace()
                        ExceptionAlert(ex)
                    }
                }
            }
            2 -> if (checkFastboot()) {
                val fc = FileChooser()
                fc.extensionFilters.add(FileChooser.ExtensionFilter("Text File", "*"))
                fc.title = "Save properties"
                val f = fc.showSaveDialog((event.target as MenuItem).parentPopup.ownerWindow)
                f?.let {
                    try {
                        f.writeText(comm.exec("fastboot getvar all"))
                    } catch (ex: Exception) {
                        ex.printStackTrace()
                        ExceptionAlert(ex)
                    }
                }
            }
        }
    }

    @FXML
    private fun antirbButtonPressed(event: ActionEvent) {
        if (checkFastboot()) {
            val result = comm.exec("fastboot flash antirbpass dummy.img")
            if ("FAILED" in result)
                displayedcomm.exec("fastboot oem ignore_anti")
            else outputTextArea.text = result
        }
    }

    @FXML
    private fun browseimageButtonPressed(event: ActionEvent) {
        val fc = FileChooser()
        fc.extensionFilters.add(FileChooser.ExtensionFilter("Image File", "*.*"))
        fc.title = "Select an image"
        image = fc.showOpenDialog((event.source as Node).scene.window)
        imageLabel.text = image?.name
    }

    @FXML
    private fun flashimageButtonPressed(event: ActionEvent) {
        image?.let {
            partitionComboBox.value?.let {
                if (image!!.absolutePath.isNotEmpty() && partitionComboBox.value.trim().isNotEmpty() && checkFastboot()) {
                    confirm {
                        if (autobootCheckBox.isSelected && partitionComboBox.value.trim() == "recovery")
                            flasher.exec(image, "fastboot flash ${partitionComboBox.value.trim()}", "fastboot boot")
                        else flasher.exec(image, "fastboot flash ${partitionComboBox.value.trim()}")
                    }
                }
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
        rom?.let {
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
        rom?.let {
            scriptComboBox.value?.let {
                if (checkFastboot()) {
                    confirm {
                        val rf = ROMFlasher(progressBar, progressIndicator, outputTextArea, rom!!)
                        setPanels(0)
                        when (scriptComboBox.value) {
                            "Clean install" -> rf.exec("flash_all")
                            "Clean install and lock" -> rf.exec("flash_all_lock")
                            "Update" -> when {
                                File(
                                    rom,
                                    "flash_all_except_data_storage.sh"
                                ).exists() -> rf.exec("flash_all_except_data_storage")
                                File(rom, "flash_all_except_data.sh").exists() -> rf.exec("flash_all_except_data")
                                else -> rf.exec("flash_all_except_storage")
                            }
                        }
                    }
                }
            }
        }
    }

    @FXML
    private fun bootButtonPressed(event: ActionEvent) {
        image?.let {
            if (image!!.absolutePath.isNotEmpty() && checkFastboot())
                flasher.exec(image, "fastboot boot")
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
            confirm { displayedcomm.exec("fastboot erase userdata") }
    }

    @FXML
    private fun cachedataButtonPressed(event: ActionEvent) {
        if (checkFastboot())
            confirm { displayedcomm.exec("fastboot erase cache", "fastboot erase userdata") }
    }

    @FXML
    private fun lockButtonPressed(event: ActionEvent) {
        if (checkFastboot())
            confirm { displayedcomm.exec("fastboot oem lock") }
    }

    @FXML
    private fun unlockButtonPressed(event: ActionEvent) {
        if (checkFastboot())
            displayedcomm.exec("fastboot oem unlock")
    }

    @FXML
    private fun getlinkButtonPressed(event: ActionEvent) {
        branchComboBox.value?.let {
            if (codenameTextField.text.trim().isNotEmpty()) {
                val codename = codenameTextField.text.trim()
                val url = when (branchComboBox.value) {
                    "Global Stable" ->
                        URL("http://update.miui.com/updates/v1/fullromdownload.php?d=${codename}_global&b=F&r=global&n=")
                    "Global Developer" ->
                        URL("http://update.miui.com/updates/v1/fullromdownload.php?d=${codename}_global&b=X&r=global&n=")
                    "China Stable" ->
                        URL("http://update.miui.com/updates/v1/fullromdownload.php?d=${codename}&b=F&r=cn&n=")
                    "China Developer" ->
                        URL("http://update.miui.com/updates/v1/fullromdownload.php?d=${codename}&b=X&r=cn&n=")
                    "EEA Stable" ->
                        URL("http://update.miui.com/updates/v1/fullromdownload.php?d=${codename}_eea_global&b=F&r=eea&n=")
                    "Russia Stable" ->
                        URL("http://update.miui.com/updates/v1/fullromdownload.php?d=${codename}_ru_global&b=F&r=global&n=")
                    "India Stable" ->
                        URL("http://update.miui.com/updates/v1/fullromdownload.php?d=${codename}_india_global&b=F&r=global&n=")
                    else -> URL("http://google.com")
                }
                val huc = url.openConnection() as HttpURLConnection
                huc.requestMethod = "GET"
                huc.setRequestProperty("Referer", "http://en.miui.com/a-234.html")
                huc.instanceFollowRedirects = false
                try {
                    huc.connect()
                    huc.disconnect()
                } catch (e: IOException) {
                    return
                }
                val link = huc.getHeaderField("Location")
                link?.let {
                    if ("bigota" in link) {
                        versionLabel.text = link.substringAfter(".com/").substringBefore('/')
                        outputTextArea.text += "\n\n$link\n\nLink copied to clipboard!"
                        Toolkit.getDefaultToolkit().systemClipboard.setContents(StringSelection(link), null)
                    } else {
                        versionLabel.text = "-"
                        outputTextArea.text += "\n\nLink not found!"
                    }
                }
            }
        }
    }

    @FXML
    private fun downloadromButtonPressed(event: ActionEvent) {
        branchComboBox.value?.let {
            if (codenameTextField.text.trim().isNotEmpty()) {
                val codename = codenameTextField.text.trim()
                val url = when (branchComboBox.value) {
                    "Global Stable" ->
                        URL("http://update.miui.com/updates/v1/fullromdownload.php?d=${codename}_global&b=F&r=global&n=")
                    "Global Developer" ->
                        URL("http://update.miui.com/updates/v1/fullromdownload.php?d=${codename}_global&b=X&r=global&n=")
                    "China Stable" ->
                        URL("http://update.miui.com/updates/v1/fullromdownload.php?d=${codename}&b=F&r=cn&n=")
                    "China Developer" ->
                        URL("http://update.miui.com/updates/v1/fullromdownload.php?d=${codename}&b=X&r=cn&n=")
                    "EEA Stable" ->
                        URL("http://update.miui.com/updates/v1/fullromdownload.php?d=${codename}_eea_global&b=F&r=eea&n=")
                    "Russia Stable" ->
                        URL("http://update.miui.com/updates/v1/fullromdownload.php?d=${codename}_ru_global&b=F&r=global&n=")
                    "India Stable" ->
                        URL("http://update.miui.com/updates/v1/fullromdownload.php?d=${codename}_india_global&b=F&r=global&n=")
                    else -> URL("http://google.com")
                }
                val huc = url.openConnection() as HttpURLConnection
                huc.requestMethod = "GET"
                huc.setRequestProperty("Referer", "http://en.miui.com/a-234.html")
                huc.instanceFollowRedirects = false
                huc.connect()
                huc.disconnect()
                val link = huc.getHeaderField("Location")
                link?.let {
                    if ("bigota" in link) {
                        versionLabel.text = link.substringAfter(".com/").substringBefore('/')
                        outputTextArea.text += "\n\nStarting download in browser..."
                        if ("linux" in System.getProperty("os.name").toLowerCase())
                            Runtime.getRuntime().exec("xdg-open $link")
                        else Desktop.getDesktop().browse(URI(link))
                    } else {
                        versionLabel.text = "-"
                        outputTextArea.text += "\n\nLink not found!"
                    }
                }
            }
        }
    }

    @FXML
    private fun systemMenuItemPressed(event: ActionEvent) {
        when (device.mode) {
            1 -> if (checkADB()) {
                comm.exec("adb reboot")
                outputTextArea.clear()
                infoTextArea.clear()
                loadDevice()
            }
            2 -> if (checkFastboot()) {
                comm.exec("fastboot reboot")
                outputTextArea.clear()
                infoTextArea.clear()
                loadDevice()
            }
        }
    }

    @FXML
    private fun recoveryMenuItemPressed(event: ActionEvent) {
        when (device.mode) {
            1 -> if (checkADB()) {
                comm.exec("adb reboot recovery")
                outputTextArea.clear()
                infoTextArea.clear()
                loadDevice()
            }
        }
    }

    @FXML
    private fun fastbootMenuItemPressed(event: ActionEvent) {
        when (device.mode) {
            1 -> if (checkADB()) {
                comm.exec("adb reboot bootloader")
                outputTextArea.clear()
                infoTextArea.clear()
                loadDevice()
            }
            2 -> if (checkFastboot()) {
                comm.exec("fastboot reboot bootloader")
                outputTextArea.clear()
                infoTextArea.clear()
                loadDevice()
            }
        }
    }

    @FXML
    private fun edlMenuItemPressed(event: ActionEvent) {
        when (device.mode) {
            1 -> if (checkADB()) {
                comm.exec("adb reboot edl")
                outputTextArea.clear()
                infoTextArea.clear()
                loadDevice()
            }
            2 -> if (checkFastboot()) {
                comm.exec("fastboot oem edl")
                outputTextArea.clear()
                infoTextArea.clear()
                loadDevice()
            }
        }
    }

    @FXML
    private fun reloadMenuItemPressed(event: ActionEvent) {
        outputTextArea.clear()
        infoTextArea.clear()
        loadDevice()
    }

    @FXML
    private fun uninstallButtonPressed(event: ActionEvent) {
        if (installer.isAppSelected(0) && checkADB()) {
            setPanels(0)
            installer.uninstall {
                setPanels(1)
            }
        }
    }

    @FXML
    private fun reinstallButtonPressed(event: ActionEvent) {
        if (installer.isAppSelected(1) && checkADB()) {
            setPanels(0)
            installer.reinstall {
                setPanels(1)
            }
        }
    }

    @FXML
    private fun addButtonPressed(event: ActionEvent) {
        if (checkADB()) {
            val fxmlLoader = FXMLLoader(javaClass.classLoader.getResource("AppAdder.fxml"))
            val parent = fxmlLoader.load<Parent>()
            fxmlLoader.getController<AppAdderController>().installer = installer
            val scene = Scene(parent)
            val stage = Stage()
            stage.scene = scene
            stage.initModality(Modality.APPLICATION_MODAL)
            stage.scene = scene
            stage.isResizable = false
            stage.showAndWait()
        }
    }

    @FXML
    private fun aboutMenuItemPressed(event: ActionEvent) {
        val linux = "linux" in System.getProperty("os.name").toLowerCase()
        val alert = Alert(AlertType.INFORMATION)
        alert.initStyle(StageStyle.UTILITY)
        alert.title = "About"
        alert.graphic = ImageView("icon.png")
        alert.headerText =
            "Xiaomi ADB/Fastboot Tools\nVersion $version\nCreated by Saki_EU"
        val vb = VBox()
        vb.alignment = Pos.CENTER
        val discord = Hyperlink("Xiaomi Community on Discord")
        discord.onAction = EventHandler {
            if (linux)
                Runtime.getRuntime().exec("xdg-open https://discord.gg/xiaomi")
            else Desktop.getDesktop().browse(URI("https://discord.gg/xiaomi"))
        }
        discord.font = Font(15.0)
        val twitter = Hyperlink("Saki_EU on Twitter")
        twitter.onAction = EventHandler {
            if (linux)
                Runtime.getRuntime().exec("xdg-open https://twitter.com/Saki_EU")
            else Desktop.getDesktop().browse(URI("https://twitter.com/Saki_EU"))
        }
        twitter.font = Font(15.0)
        val github = Hyperlink("Repository on GitHub")
        github.onAction = EventHandler {
            if (linux)
                Runtime.getRuntime().exec("xdg-open https://github.com/Saki-EU/XiaomiADBFastbootTools")
            else Desktop.getDesktop().browse(URI("https://github.com/Saki-EU/XiaomiADBFastbootTools"))
        }
        github.font = Font(15.0)
        vb.children.addAll(discord, twitter, github)
        alert.dialogPane.content = vb
        alert.isResizable = false
        alert.showAndWait()
    }
}
