import javafx.application.Platform
import javafx.collections.FXCollections
import javafx.event.ActionEvent
import javafx.event.EventHandler
import javafx.fxml.FXML
import javafx.fxml.FXMLLoader
import javafx.fxml.Initializable
import javafx.geometry.Pos
import javafx.scene.Node
import javafx.scene.Scene
import javafx.scene.control.*
import javafx.scene.control.Alert.AlertType
import javafx.scene.control.cell.CheckBoxTableCell
import javafx.scene.control.cell.PropertyValueFactory
import javafx.scene.image.ImageView
import javafx.scene.input.KeyCode
import javafx.scene.layout.HBox
import javafx.scene.layout.VBox
import javafx.scene.text.Font
import javafx.stage.*
import kotlinx.coroutines.*
import java.awt.Desktop
import java.awt.Toolkit
import java.awt.datatransfer.StringSelection
import java.io.File
import java.net.URI
import java.net.URL
import java.util.*
import java.util.zip.ZipFile

class MainController : Initializable {

    @FXML
    private lateinit var deviceMenu: Menu

    @FXML
    private lateinit var appManagerMenu: Menu

    @FXML
    private lateinit var secondSpaceButton: CheckMenuItem

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
    private lateinit var tabPane: TabPane

    @FXML
    private lateinit var uninstallerTableView: TableView<App>

    @FXML
    private lateinit var uninstallerCopyAppNameMenuItem : MenuItem

    @FXML
    private lateinit var uninstallerCopyPackageNameMenuItem : MenuItem

    @FXML
    private lateinit var reinstallerTableView: TableView<App>

    @FXML
    private lateinit var reinstallerCopyAppNameMenuItem : MenuItem

    @FXML
    private lateinit var reinstallerCopyPackageNameMenuItem : MenuItem

    @FXML
    private lateinit var disablerTableView: TableView<App>

    @FXML
    private lateinit var disablerCopyAppNameMenuItem : MenuItem

    @FXML
    private lateinit var disablerCopyPackageNameMenuItem : MenuItem

    @FXML
    private lateinit var enablerTableView: TableView<App>

    @FXML
    private lateinit var enablerCopyAppNameMenuItem : MenuItem

    @FXML
    private lateinit var enablerCopyPackageNameMenuItem : MenuItem

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
    private lateinit var discheckTableColumn: TableColumn<App, Boolean>

    @FXML
    private lateinit var disappTableColumn: TableColumn<App, String>

    @FXML
    private lateinit var dispackageTableColumn: TableColumn<App, String>

    @FXML
    private lateinit var encheckTableColumn: TableColumn<App, Boolean>

    @FXML
    private lateinit var enappTableColumn: TableColumn<App, String>

    @FXML
    private lateinit var enpackageTableColumn: TableColumn<App, String>

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
    private lateinit var downloadProgress: Label

    @FXML
    private lateinit var versionLabel: Label

    @FXML
    private lateinit var appManagerPane: TabPane

    @FXML
    private lateinit var reinstallerTab: Tab

    @FXML
    private lateinit var disablerTab: Tab

    @FXML
    private lateinit var enablerTab: Tab

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

    @FXML
    private lateinit var downloaderPane: TitledPane

    private var image: File? = null
    private var romDirectory: File? = null

    private suspend fun setPanels(mode: Mode?) {
        withContext(Dispatchers.Main) {
            when (mode) {
                Mode.ADB -> {
                    appManagerPane.isDisable = false
                    appManagerMenu.isDisable = false
                    reinstallerTab.isDisable = !Device.reinstaller
                    disablerTab.isDisable = !Device.disabler
                    enablerTab.isDisable = !Device.disabler
                    camera2Pane.isDisable = true
                    fileExplorerPane.isDisable = false
                    resolutionPane.isDisable = false
                    dpiPane.isDisable = false
                    flasherPane.isDisable = true
                    wiperPane.isDisable = true
                    oemPane.isDisable = true
                    deviceMenu.isDisable = false
                    recoveryMenuItem.isDisable = false
                    reloadMenuItem.isDisable = false
                }
                Mode.RECOVERY -> {
                    appManagerPane.isDisable = true
                    appManagerMenu.isDisable = true
                    reinstallerTab.isDisable = true
                    disablerTab.isDisable = true
                    enablerTab.isDisable = true
                    camera2Pane.isDisable = false
                    fileExplorerPane.isDisable = true
                    resolutionPane.isDisable = true
                    dpiPane.isDisable = true
                    flasherPane.isDisable = true
                    wiperPane.isDisable = true
                    oemPane.isDisable = true
                    deviceMenu.isDisable = false
                    recoveryMenuItem.isDisable = false
                    reloadMenuItem.isDisable = false
                }
                Mode.FASTBOOT -> {
                    appManagerPane.isDisable = true
                    appManagerMenu.isDisable = true
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
                else -> {
                    appManagerPane.isDisable = true
                    appManagerMenu.isDisable = true
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
            }
        }
    }

    private suspend fun setUI() {
        setPanels(Device.mode)
        withContext(Dispatchers.Main) {
            when (Device.mode) {
                Mode.ADB -> {
                    tabPane.selectionModel.select(0)
                    infoTextArea.text = "Serial number:\t\t${Device.serial}\n" +
                            "Codename:\t\t${Device.codename}\n"
                    if (Device.bootloader)
                        infoTextArea.appendText("Bootloader:\t\tunlocked\n")
                    if (Device.camera2)
                        infoTextArea.appendText("Camera2:\t\t\tenabled")
                    codenameTextField.text = Device.codename
                    dpiTextField.text = if (Device.dpi != -1)
                        Device.dpi.toString()
                    else "ERROR"
                    widthTextField.text = if (Device.width != -1)
                        Device.width.toString()
                    else "ERROR"
                    heightTextField.text = if (Device.height != -1)
                        Device.height.toString()
                    else "ERROR"

                }
                Mode.RECOVERY -> {
                    tabPane.selectionModel.select(0)
                    infoTextArea.text = "Serial number:\t\t${Device.serial}\n" +
                            "Codename:\t\t${Device.codename}\n"
                    if (Device.bootloader)
                        infoTextArea.appendText("Bootloader:\t\tunlocked\n")
                    if (Device.camera2)
                        infoTextArea.appendText("Camera2:\t\t\tenabled")
                    codenameTextField.text = Device.codename
                }
                Mode.FASTBOOT -> {
                    tabPane.selectionModel.select(1)
                    infoTextArea.text = "Serial number:\t\t${Device.serial}\n" +
                            "Codename:\t\t${Device.codename}\n" +
                            "Bootloader:\t\t"
                    if (Device.bootloader)
                        infoTextArea.appendText("unlocked")
                    else infoTextArea.appendText("locked")
                    if (Device.anti != -1)
                        infoTextArea.appendText("\nAnti version:\t\t${Device.anti}")
                    codenameTextField.text = Device.codename
                }
                else -> {
                    infoTextArea.clear()
                    codenameTextField.clear()
                    dpiTextField.clear()
                    widthTextField.clear()
                    heightTextField.clear()
                }
            }
        }
    }

    private suspend fun checkDevice() {
        Device.mode = null
        setUI()
        withContext(Dispatchers.Main) {
            outputTextArea.text = "Looking for devices...\n"
            progressIndicator.isVisible = true
        }
        do {
            Device.readADB()
            Device.readFastboot()
            withContext(Dispatchers.Main) {
                when (Device.mode) {
                    Mode.ADB -> {
                        progressIndicator.isVisible = false
                        outputTextArea.text = "Device connected in ADB mode!\n"
                        if (!Device.reinstaller || !Device.disabler)
                            outputTextArea.appendText("Note:\nThis device isn't fully supported by the App Manager.\nAs a result, some modules have been disabled.\n\n")
                        AppManager.readPotentialApps()
                        AppManager.createTables()
                    }
                    Mode.RECOVERY -> {
                        progressIndicator.isVisible = false
                        outputTextArea.text = "Device connected in Recovery mode!\n\n"
                    }
                    Mode.FASTBOOT -> {
                        progressIndicator.isVisible = false
                        outputTextArea.text = "Device connected in Fastboot mode!\n\n"
                    }
                    Mode.AUTH -> {
                        if ("Unauthorised" !in outputTextArea.text)
                            outputTextArea.text =
                                    "Unauthorised device found!\nPlease allow USB debugging on the device!\n\n"
                    }
                    Mode.ADB_ERROR -> {
                        if ("loaded" !in outputTextArea.text)
                            outputTextArea.text =
                                    "ERROR: The device cannot be loaded!\nTry setting the USB configuration to data transfer or launching the application with root/admin privileges!\n\n"
                    }
                    Mode.FASTBOOT_ERROR -> {
                        if ("loaded" !in outputTextArea.text)
                            outputTextArea.text = "ERROR: The device cannot be loaded!\nTry launching the application with root/admin privileges!\n\n"
                    }
                    else -> {
                    }
                }
            }
            setUI()
            delay(1000)
        } while (!(Device.mode == Mode.ADB || Device.mode == Mode.FASTBOOT || Device.mode == Mode.RECOVERY))
    }

    private fun TableView<App>.setKeyListener() {
        this.setOnKeyPressed {
            if (it.code == KeyCode.ENTER || it.code == KeyCode.SPACE)
                this.selectionModel.selectedItems.forEach { app ->
                    app.apply {
                        selectedProperty().set(!selectedProperty().get())
                    }
                }
        }
    }

    override fun initialize(url: URL, rb: ResourceBundle?) {
        uncheckTableColumn.cellValueFactory = PropertyValueFactory("selected")
        uncheckTableColumn.setCellFactory { CheckBoxTableCell() }
        unappTableColumn.cellValueFactory = PropertyValueFactory("appname")
        unpackageTableColumn.cellValueFactory = PropertyValueFactory("packagename")

        recheckTableColumn.cellValueFactory = PropertyValueFactory("selected")
        recheckTableColumn.setCellFactory { CheckBoxTableCell() }
        reappTableColumn.cellValueFactory = PropertyValueFactory("appname")
        repackageTableColumn.cellValueFactory = PropertyValueFactory("packagename")

        discheckTableColumn.cellValueFactory = PropertyValueFactory("selected")
        discheckTableColumn.setCellFactory { CheckBoxTableCell() }
        disappTableColumn.cellValueFactory = PropertyValueFactory("appname")
        dispackageTableColumn.cellValueFactory = PropertyValueFactory("packagename")

        encheckTableColumn.cellValueFactory = PropertyValueFactory("selected")
        encheckTableColumn.setCellFactory { CheckBoxTableCell() }
        enappTableColumn.cellValueFactory = PropertyValueFactory("appname")
        enpackageTableColumn.cellValueFactory = PropertyValueFactory("packagename")

        partitionComboBox.items.addAll(
                "boot", "cust", "modem", "persist", "recovery", "system"
        )
        scriptComboBox.items.addAll(
                "Clean install", "Clean install and lock", "Update"
        )
        branchComboBox.items.addAll(
                "China Stable",
                "EEA Stable",
                "Global Stable",
                "India Stable",
                "Indonesia Stable",
                "Russia Stable"
        )

        uninstallerTableView.columns.setAll(uncheckTableColumn, unappTableColumn, unpackageTableColumn)
        reinstallerTableView.columns.setAll(recheckTableColumn, reappTableColumn, repackageTableColumn)
        disablerTableView.columns.setAll(discheckTableColumn, disappTableColumn, dispackageTableColumn)
        enablerTableView.columns.setAll(encheckTableColumn, enappTableColumn, enpackageTableColumn)

        uninstallerTableView.setKeyListener()
        reinstallerTableView.setKeyListener()
        disablerTableView.setKeyListener()
        enablerTableView.setKeyListener()

        Command.outputTextArea = outputTextArea
        Command.progressIndicator = progressIndicator
        ROMFlasher.outputTextArea = outputTextArea
        ROMFlasher.progressBar = progressBar
        ROMFlasher.progressIndicator = progressIndicator
        AppManager.uninstallerTableView = uninstallerTableView
        AppManager.reinstallerTableView = reinstallerTableView
        AppManager.disablerTableView = disablerTableView
        AppManager.enablerTableView = enablerTableView
        AppManager.progress = progressBar
        AppManager.progressInd = progressIndicator
        AppManager.outputTextArea = outputTextArea

        GlobalScope.launch(Dispatchers.IO) {
            try {
                val link =
                        URL("https://api.github.com/repos/Szaki/XiaomiADBFastbootTools/releases/latest").readText()
                                .substringAfter("\"html_url\":\"").substringBefore('"')
                val latest = link.substringAfterLast('/')
                if (latest > XiaomiADBFastbootTools.version)
                    withContext(Dispatchers.Main) {
                        val vb = VBox()
                        val download = Hyperlink("Download")
                        Alert(AlertType.INFORMATION).apply {
                            initStyle(StageStyle.UTILITY)
                            title = "New version available!"
                            graphic = ImageView("mitu.png")
                            headerText =
                                    "Version $latest is available!"
                            vb.alignment = Pos.CENTER
                            download.onAction = EventHandler {
                                if (XiaomiADBFastbootTools.linux)
                                    Runtime.getRuntime().exec("xdg-open $link")
                                else Desktop.getDesktop().browse(URI(link))
                            }
                            download.font = Font(15.0)
                            vb.children.add(download)
                            dialogPane.content = vb
                            showAndWait()
                        }
                    }
            } catch (ex: Exception) {
                // OK
            }
            if (!Command.check()) {
                withContext(Dispatchers.Main) {
                    val hb = HBox(15.0)
                    val label = Label()
                    val indicator = ProgressIndicator()
                    Alert(AlertType.WARNING).apply {
                        initStyle(StageStyle.UTILITY)
                        title = "Downloading SDK Platform Tools..."
                        headerText =
                                "ERROR: Cannot find ADB/Fastboot!\nDownloading the latest version..."
                        hb.alignment = Pos.CENTER
                        label.font = Font(15.0)
                        indicator.setPrefSize(35.0, 35.0)
                        hb.children.addAll(indicator, label)
                        dialogPane.content = hb
                        isResizable = false
                        show()
                        withContext(Dispatchers.IO) {
                            val file = File(XiaomiADBFastbootTools.dir, "platform-tools.zip")
                            when {
                                XiaomiADBFastbootTools.win -> Downloader(
                                        "https://dl.google.com/android/repository/platform-tools-latest-windows.zip",
                                        file, label
                                ).start(this)
                                XiaomiADBFastbootTools.linux -> Downloader(
                                        "https://dl.google.com/android/repository/platform-tools-latest-linux.zip",
                                        file, label
                                ).start(this)
                                else -> Downloader(
                                        "https://dl.google.com/android/repository/platform-tools-latest-darwin.zip",
                                        file, label
                                ).start(this)
                            }
                            withContext(Dispatchers.Main) {
                                label.text = "Unzipping..."
                            }
                            File(XiaomiADBFastbootTools.dir, "platform-tools").mkdirs()
                            ZipFile(file).use { zip ->
                                zip.stream().forEach { entry ->
                                    if (entry.isDirectory)
                                        File(XiaomiADBFastbootTools.dir, entry.name).mkdirs()
                                    else zip.getInputStream(entry).use { input ->
                                        File(XiaomiADBFastbootTools.dir, entry.name).apply {
                                            outputStream().use { output ->
                                                input.copyTo(output)
                                            }
                                            setExecutable(true, false)
                                        }
                                    }
                                }
                            }
                            file.delete()
                        }
                        hb.children.remove(indicator)
                        label.text = "Done!"
                    }
                }
                if (!Command.check(true))
                    withContext(Dispatchers.Main) {
                        Alert(AlertType.ERROR).apply {
                            title = "Fatal Error"
                            headerText =
                                    "ERROR: Couldn't run ADB/Fastboot!"
                            showAndWait()
                        }
                        Platform.exit()
                    }
            }
            checkDevice()
        }
    }

    private suspend fun checkCamera2(): Boolean =
            "1" in Command.exec(mutableListOf("adb", "shell", "getprop", "persist.camera.HAL3.enabled"))

    private suspend fun checkEIS(): Boolean =
            "1" in Command.exec(mutableListOf("adb", "shell", "getprop", "persist.camera.eis.enable"))

    @FXML
    private fun disableCamera2ButtonPressed(event: ActionEvent) {
        GlobalScope.launch {
            if (Device.checkRecovery()) {
                Command.exec(mutableListOf("adb", "shell", "setprop", "persist.camera.HAL3.enabled", "0"))
                withContext(Dispatchers.Main) {
                    outputTextArea.text = if (!checkCamera2())
                        "Camera2 disabled!"
                    else "ERROR: Couldn't disable Camera2!"
                }
            } else checkDevice()
        }
    }

    @FXML
    private fun enableCamera2ButtonPressed(event: ActionEvent) {
        GlobalScope.launch {
            if (Device.checkRecovery()) {
                Command.exec(mutableListOf("adb", "shell", "setprop", "persist.camera.HAL3.enabled", "1"))
                withContext(Dispatchers.Main) {
                    outputTextArea.text = if (checkCamera2())
                        "Camera2 enabled!"
                    else "ERROR: Couldn't enable Camera2!"
                }
            } else checkDevice()
        }
    }

    @FXML
    private fun disableEISButtonPressed(event: ActionEvent) {
        GlobalScope.launch {
            if (Device.checkRecovery()) {
                Command.exec(mutableListOf("adb", "shell", "setprop", "persist.camera.eis.enable", "0"))
                withContext(Dispatchers.Main) {
                    outputTextArea.text = if (!checkEIS())
                        "EIS disabled!"
                    else "ERROR: Couldn't disable EIS!"
                }
            } else checkDevice()
        }
    }

    @FXML
    private fun enableEISButtonPressed(event: ActionEvent) {
        GlobalScope.launch {
            if (Device.checkRecovery()) {
                Command.exec(mutableListOf("adb", "shell", "setprop", "persist.camera.eis.enable", "1"))
                withContext(Dispatchers.Main) {
                    outputTextArea.text = if (checkEIS())
                        "EIS enabled!"
                    else "ERROR: Couldn't enable EIS!"
                }
            } else checkDevice()
        }
    }

    @FXML
    private fun openButtonPressed(event: ActionEvent) {
        GlobalScope.launch(Dispatchers.IO) {
            if (Device.checkADB()) {
                val scene = Scene(FXMLLoader(javaClass.classLoader.getResource("FileExplorer.fxml")).load())
                withContext(Dispatchers.Main) {
                    Stage().apply {
                        this.scene = scene
                        initModality(Modality.APPLICATION_MODAL)
                        title = "File Explorer"
                        isResizable = false
                        showAndWait()
                    }
                }
            } else checkDevice()
        }
    }

    @FXML
    private fun applyDpiButtonPressed(event: ActionEvent) {
        if (dpiTextField.text.isNotBlank())
            GlobalScope.launch {
                if (Device.checkADB()) {
                    val attempt =
                            Command.execDisplayed(mutableListOf("adb", "shell", "wm", "density", dpiTextField.text.trim()))
                    withContext(Dispatchers.Main) {
                        outputTextArea.text = when {
                            "permission" in attempt ->
                                "ERROR: Please allow USB debugging (Security settings)!"
                            "bad" in attempt ->
                                "ERROR: Invalid value!"
                            attempt.isEmpty() ->
                                "Done!"
                            else ->
                                "ERROR: $attempt"
                        }
                    }
                } else checkDevice()
            }
    }

    @FXML
    private fun resetDpiButtonPressed(event: ActionEvent) {
        GlobalScope.launch {
            if (Device.checkADB()) {
                val attempt = Command.execDisplayed(mutableListOf("adb", "shell", "wm", "density", "reset"))
                withContext(Dispatchers.Main) {
                    outputTextArea.text = when {
                        "permission" in attempt ->
                            "ERROR: Please allow USB debugging (Security settings)!"
                        attempt.isEmpty() ->
                            "Done!"
                        else ->
                            "ERROR: $attempt"
                    }
                }
            } else checkDevice()
        }
    }

    @FXML
    private fun applyResButtonPressed(event: ActionEvent) {
        if (widthTextField.text.isNotBlank() && heightTextField.text.isNotBlank())
            GlobalScope.launch {
                if (Device.checkADB()) {
                    val attempt =
                            Command.execDisplayed(
                                    mutableListOf(
                                            "adb",
                                            "shell",
                                            "wm",
                                            "size",
                                            "${widthTextField.text.trim()}x${heightTextField.text.trim()}"
                                    )
                            )
                    withContext(Dispatchers.Main) {
                        outputTextArea.text = when {
                            "permission" in attempt ->
                                "ERROR: Please allow USB debugging (Security settings)!"
                            "bad" in attempt ->
                                "ERROR: Invalid value!"
                            attempt.isEmpty() ->
                                "Done!"
                            else ->
                                "ERROR: $attempt"
                        }
                    }
                } else checkDevice()
            }
    }

    @FXML
    private fun resetResButtonPressed(event: ActionEvent) {
        GlobalScope.launch {
            if (Device.checkADB()) {
                val attempt = Command.execDisplayed(mutableListOf("adb", "shell", "wm", "size", "reset"))
                withContext(Dispatchers.Main) {
                    outputTextArea.text = when {
                        "permission" in attempt ->
                            "ERROR: Please allow USB debugging (Security settings)!"
                        attempt.isEmpty() ->
                            "Done!"
                        else ->
                            "ERROR: $attempt"
                    }
                }
            } else checkDevice()
        }
    }

    @FXML
    private fun readPropertiesMenuItemPressed(event: ActionEvent) {
        GlobalScope.launch {
            when (Device.mode) {
                Mode.ADB, Mode.RECOVERY -> {
                    if (Device.checkADB())
                        Command.execDisplayed(mutableListOf("adb", "shell", "getprop")) else checkDevice()
                }
                Mode.FASTBOOT -> {
                    if (Device.checkFastboot())
                        Command.execDisplayed(mutableListOf("fastboot", "getvar", "all")) else checkDevice()
                }
                else -> {
                }
            }
        }
    }

    @FXML
    private fun savePropertiesMenuItemPressed(event: ActionEvent) {
        GlobalScope.launch {
            when (Device.mode) {
                Mode.ADB, Mode.RECOVERY -> {
                    if (Device.checkADB()) {
                        val props = Command.exec(mutableListOf("adb", "shell", "getprop"))
                        withContext(Dispatchers.Main) {
                            FileChooser().apply {
                                extensionFilters.add(FileChooser.ExtensionFilter("Text File", "*"))
                                title = "Save properties"
                                showSaveDialog((event.target as MenuItem).parentPopup.ownerWindow)?.let {
                                    withContext(Dispatchers.IO) {
                                        try {
                                            it.writeText(props)
                                        } catch (ex: Exception) {
                                            ex.printStackTrace()
                                            ex.alert()
                                        }
                                    }
                                }
                            }
                        }
                    } else checkDevice()
                }
                Mode.FASTBOOT -> {
                    if (Device.checkFastboot()) {
                        val props = Command.exec(mutableListOf("fastboot", "getvar", "all"))
                        withContext(Dispatchers.Main) {
                            FileChooser().apply {
                                extensionFilters.add(FileChooser.ExtensionFilter("Text File", "*"))
                                title = "Save properties"
                                showSaveDialog((event.target as MenuItem).parentPopup.ownerWindow)?.let {
                                    withContext(Dispatchers.IO) {
                                        try {
                                            it.writeText(props)
                                        } catch (ex: Exception) {
                                            ex.printStackTrace()
                                            ex.alert()
                                        }
                                    }
                                }
                            }
                        }
                    } else checkDevice()
                }
                else -> checkDevice()
            }
        }
    }

    @FXML
    private fun antirbButtonPressed(event: ActionEvent) {
        GlobalScope.launch {
            if (Device.checkFastboot()) {
                File("dummy.img").apply {
                    writeBytes(ByteArray(8192))
                    withContext(Dispatchers.Main) {
                        if ("FAILED" in Command.exec(mutableListOf("fastboot", "oem", "ignore", "anti"))) {
                            if ("FAILED" in Command.exec(
                                            mutableListOf(
                                                    "fastboot",
                                                    "flash",
                                                    "antirbpass",
                                                    "dummy.img"
                                            )
                                    )
                            ) {
                                outputTextArea.text = "Couldn't disable anti-rollback safeguard!"
                            } else outputTextArea.text = "Anti-rollback safeguard disabled!"
                        } else outputTextArea.text = "Anti-rollback safeguard disabled!"
                    }
                    delete()
                }
            } else checkDevice()
        }
    }

    @FXML
    private fun browseimageButtonPressed(event: ActionEvent) {
        FileChooser().apply {
            extensionFilters.add(FileChooser.ExtensionFilter("Image File", "*.*"))
            title = "Select an image"
            image = showOpenDialog((event.source as Node).scene.window)
            imageLabel.text = image?.name
        }
    }

    @FXML
    private fun flashimageButtonPressed(event: ActionEvent) {
        image?.let {
            partitionComboBox.value?.let { pcb ->
                if (it.absolutePath.isNotBlank() && pcb.isNotBlank())
                    GlobalScope.launch {
                        if (Device.checkFastboot()) {
                            if (confirm()) {
                                if (autobootCheckBox.isSelected && pcb.trim() == "recovery")
                                    Command.exec(
                                            mutableListOf("fastboot", "flash", pcb.trim()),
                                            mutableListOf("fastboot", "boot"),
                                            image = it
                                    )
                                else Command.exec(mutableListOf("fastboot", "flash", pcb.trim()), image = it)
                            }
                        } else checkDevice()
                    }
            }
        }
    }

    @FXML
    private fun browseromButtonPressed(event: ActionEvent) {
        DirectoryChooser().apply {
            title = "Select the root directory of a Fastboot ROM"
            romLabel.text = "-"
            romDirectory = showDialog((event.source as Node).scene.window)?.let { dir ->
                when {
                    ' ' in dir.absolutePath -> {
                        outputTextArea.text = "ERROR: Space found in the pathname!"
                        null
                    }
                    "images" in dir.list()!! -> {
                        romLabel.text = dir.name
                        outputTextArea.text = "Fastboot ROM found!"
                        dir.listFiles()?.forEach {
                            if (!it.isDirectory)
                                it.setExecutable(true, false)
                        }
                        dir
                    }
                    else -> {
                        outputTextArea.text = "ERROR: Fastboot ROM not found!"
                        null
                    }
                }
            }
        }
    }

    @FXML
    private fun flashromButtonPressed(event: ActionEvent) {
        romDirectory?.let { dir ->
            ROMFlasher.directory = dir
            scriptComboBox.value?.let { scb ->
                GlobalScope.launch {
                    if (Device.checkFastboot()) {
                        if (confirm()) {
                            setPanels(null)
                            when (scb) {
                                "Clean install" -> ROMFlasher.flash("flash_all")
                                "Clean install and lock" -> ROMFlasher.flash("flash_all_lock")
                                "Update" -> ROMFlasher.flash(
                                        dir.list()?.find { "flash_all_except" in it }?.substringBefore(
                                                '.'
                                        )
                                )
                            }
                        }
                    } else checkDevice()
                }
            }
        }
    }

    @FXML
    private fun bootButtonPressed(event: ActionEvent) {
        image?.let {
            if (it.absolutePath.isNotBlank())
                GlobalScope.launch {
                    if (Device.checkFastboot())
                        Command.exec(mutableListOf("fastboot", "boot"), image = it) else checkDevice()
                }
        }
    }

    @FXML
    private fun cacheButtonPressed(event: ActionEvent) {
        GlobalScope.launch {
            if (Device.checkFastboot())
                Command.execDisplayed(mutableListOf("fastboot", "erase", "cache")) else checkDevice()
        }
    }

    @FXML
    private fun dataButtonPressed(event: ActionEvent) {
        GlobalScope.launch {
            if (Device.checkFastboot()) {
                if (confirm("All your data will be gone.")) {
                    Command.execDisplayed(
                            mutableListOf("fastboot", "erase", "userdata")
                    )
                }
            } else checkDevice()
        }
    }

    @FXML
    private fun cachedataButtonPressed(event: ActionEvent) {
        GlobalScope.launch {
            if (Device.checkFastboot()) {
                if (confirm("All your data will be gone.")) {
                    Command.execDisplayed(
                            mutableListOf("fastboot", "erase", "cache"),
                            mutableListOf("fastboot", "erase", "userdata")
                    )
                }
            } else checkDevice()
        }
    }

    @FXML
    private fun lockButtonPressed(event: ActionEvent) {
        GlobalScope.launch {
            if (Device.checkFastboot()) {
                if (confirm("Your partitions must be intact in order to successfully lock the bootloader.")) {
                    if (confirm("All your data will be gone.")) {
                        Command.execDisplayed(mutableListOf("fastboot", "oem", "lock"))
                    }
                }
            } else checkDevice()
        }
    }

    @FXML
    private fun unlockButtonPressed(event: ActionEvent) {
        GlobalScope.launch {
            if (Device.checkFastboot()) {
                if (confirm("All your data will be gone.")) {
                    Command.execDisplayed(mutableListOf("fastboot", "oem", "unlock"))
                }
            } else checkDevice()
        }
    }

    @FXML
    private fun getlinkButtonPressed(event: ActionEvent) {
        branchComboBox.value?.let {
            if (codenameTextField.text.isNotBlank()) {
                GlobalScope.launch {
                    withContext(Dispatchers.Main) {
                        outputTextArea.appendText("\nLooking for $it...\n")
                        progressIndicator.isVisible = true
                    }
                    val link = getLink(it, codenameTextField.text.trim())
                    withContext(Dispatchers.Main) {
                        if (link != null && "bigota" in link) {
                            versionLabel.text = link.substringAfter(".com/").substringBefore('/')
                            progressIndicator.isVisible = false
                            outputTextArea.appendText("$link\nLink copied to clipboard!\n")
                            Toolkit.getDefaultToolkit().systemClipboard.setContents(StringSelection(link), null)
                        } else {
                            versionLabel.text = "-"
                            progressIndicator.isVisible = false
                            outputTextArea.appendText("Link not found!\n")
                        }
                    }
                }
            }
        }
    }

    @FXML
    private fun downloadromButtonPressed(event: ActionEvent) {
        branchComboBox.value?.let { branch ->
            if (codenameTextField.text.isNotBlank()) {
                DirectoryChooser().apply {
                    title = "Select the download location of the Fastboot ROM"
                    showDialog((event.source as Node).scene.window)?.let {
                        outputTextArea.appendText("Looking for $branch...\n")
                        progressIndicator.isVisible = true
                        GlobalScope.launch {
                            val link = getLink(branch, codenameTextField.text.trim())
                            if (link != null && "bigota" in link) {
                                withContext(Dispatchers.Main) {
                                    versionLabel.text = link.substringAfter(".com/").substringBefore('/')
                                    outputTextArea.appendText("Starting download...\n")
                                    downloaderPane.isDisable = true
                                }
                                Downloader(link, File(it, link.substringAfterLast('/')), downloadProgress).start(this)
                                withContext(Dispatchers.Main) {
                                    progressIndicator.isVisible = false
                                    outputTextArea.appendText("Download complete!\n\n")
                                    downloadProgress.text = "100.0%"
                                    downloaderPane.isDisable = false
                                }
                            } else {
                                withContext(Dispatchers.Main) {
                                    versionLabel.text = "-"
                                    progressIndicator.isVisible = false
                                    outputTextArea.appendText("Link not found!\n\n")
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    @FXML
    private fun systemMenuItemPressed(event: ActionEvent) {
        GlobalScope.launch {
            when (Device.mode) {
                Mode.ADB, Mode.RECOVERY -> {
                    if (Device.checkADB())
                        Command.exec(mutableListOf("adb", "reboot"))
                    checkDevice()
                }
                Mode.FASTBOOT -> {
                    if (Device.checkFastboot())
                        Command.exec(mutableListOf("fastboot", "reboot"))
                    checkDevice()
                }
                else -> {
                }
            }
        }
    }

    @FXML
    private fun recoveryMenuItemPressed(event: ActionEvent) {
        GlobalScope.launch {
            when (Device.mode) {
                Mode.ADB, Mode.RECOVERY -> {
                    if (Device.checkADB())
                        Command.exec(mutableListOf("adb", "reboot", "recovery"))
                    checkDevice()
                }
                else -> {
                }
            }
        }
    }

    @FXML
    private fun fastbootMenuItemPressed(event: ActionEvent) {
        GlobalScope.launch {
            when (Device.mode) {
                Mode.ADB, Mode.RECOVERY -> {
                    if (Device.checkADB())
                        Command.exec(mutableListOf("adb", "reboot", "bootloader"))
                    checkDevice()
                }
                Mode.FASTBOOT -> {
                    if (Device.checkFastboot())
                        Command.exec(mutableListOf("fastboot", "reboot", "bootloader"))
                    checkDevice()
                }
                else -> {
                }
            }
        }
    }

    @FXML
    private fun edlMenuItemPressed(event: ActionEvent) {
        GlobalScope.launch {
            when (Device.mode) {
                Mode.ADB, Mode.RECOVERY -> {
                    if (Device.checkADB())
                        Command.exec(mutableListOf("adb", "reboot", "edl"))
                    checkDevice()
                }
                Mode.FASTBOOT -> {
                    if (Device.checkFastboot())
                        Command.exec(mutableListOf("fastboot", "oem", "edl"))
                    checkDevice()
                }
                else -> {
                }
            }
        }
    }

    @FXML
    private fun reloadMenuItemPressed(event: ActionEvent) = GlobalScope.launch { checkDevice() }

    @FXML
    private fun uninstallButtonPressed(event: ActionEvent) {
        if (isAppSelected(uninstallerTableView.items))
            GlobalScope.launch {
                if (Device.checkADB()) {
                    if (confirm()) {
                        setPanels(null)
                        val selected = FXCollections.observableArrayList<App>()
                        var n = 0
                        uninstallerTableView.items.forEach {
                            if (it.selectedProperty().get()) {
                                selected.add(it)
                                n += it.packagenameProperty().get().trim().lines().size
                            }
                        }
                        AppManager.uninstall(selected, n)
                        setPanels(Device.mode)
                    }
                } else checkDevice()
            }
    }

    @FXML
    private fun reinstallButtonPressed(event: ActionEvent) {
        if (isAppSelected(reinstallerTableView.items))
            GlobalScope.launch {
                if (Device.checkADB()) {
                    if (confirm()) {
                        setPanels(null)
                        val selected = FXCollections.observableArrayList<App>()
                        var n = 0
                        reinstallerTableView.items.forEach {
                            if (it.selectedProperty().get()) {
                                selected.add(it)
                                n += it.packagenameProperty().get().trim().lines().size
                            }
                        }
                        AppManager.reinstall(selected, n)
                        setPanels(Device.mode)
                    }
                } else checkDevice()
            }
    }

    @FXML
    private fun disableButtonPressed(event: ActionEvent) {
        if (isAppSelected(disablerTableView.items))
            GlobalScope.launch {
                if (Device.checkADB()) {
                    if (confirm()) {
                        setPanels(null)
                        val selected = FXCollections.observableArrayList<App>()
                        var n = 0
                        disablerTableView.items.forEach {
                            if (it.selectedProperty().get()) {
                                selected.add(it)
                                n += it.packagenameProperty().get().trim().lines().size
                            }
                        }
                        AppManager.disable(selected, n)
                        setPanels(Device.mode)
                    }
                } else checkDevice()
            }
    }

    @FXML
    private fun enableButtonPressed(event: ActionEvent) {
        if (isAppSelected(enablerTableView.items))
            GlobalScope.launch {
                if (Device.checkADB()) {
                    if (confirm()) {
                        setPanels(null)
                        val selected = FXCollections.observableArrayList<App>()
                        var n = 0
                        enablerTableView.items.forEach {
                            if (it.selectedProperty().get()) {
                                selected.add(it)
                                n += it.packagenameProperty().get().trim().lines().size
                            }
                        }
                        AppManager.enable(selected, n)
                        setPanels(Device.mode)
                    }
                } else checkDevice()
            }
    }

    @FXML
    private fun secondSpaceButtonPressed(event: ActionEvent) {
        GlobalScope.launch {
            if (Device.checkADB())
                AppManager.apply {
                    user = if (secondSpaceButton.isSelected)
                        "10"
                    else "0"
                    createTables()
                }
        }
    }

    @FXML
    private fun addAppsButtonPressed(event: ActionEvent) {
        GlobalScope.launch(Dispatchers.IO) {
            if (Device.checkADB()) {
                val scene = Scene(FXMLLoader(javaClass.classLoader.getResource("AppAdder.fxml")).load())
                withContext(Dispatchers.Main) {
                    Stage().apply {
                        initModality(Modality.APPLICATION_MODAL)
                        this.scene = scene
                        isResizable = false
                        showAndWait()
                    }
                }
            } else checkDevice()
        }
    }

    @FXML
    private fun aboutMenuItemPressed(event: ActionEvent) {
        Alert(AlertType.INFORMATION).apply {
            initStyle(StageStyle.UTILITY)
            title = "About"
            graphic = ImageView("icon.png")
            headerText =
                    "Xiaomi ADB/Fastboot Tools\nVersion ${XiaomiADBFastbootTools.version}\nCreated by Szaki\n\n" +
                            "SDK Platform Tools\n${
                                runBlocking {
                                    Command.exec(
                                            mutableListOf(
                                                    "adb",
                                                    "--version"
                                            )
                                    ).lines()[1]
                                }
                            }"
            val vb = VBox()
            vb.alignment = Pos.CENTER
            val discord = Hyperlink("Xiaomi Community on Discord")
            discord.onAction = EventHandler {
                if (XiaomiADBFastbootTools.linux)
                    Runtime.getRuntime().exec("xdg-open http://discord.szaki.io/")
                else Desktop.getDesktop().browse(URI("http://discord.szaki.io/"))
            }
            discord.font = Font(15.0)
            val twitter = Hyperlink("Szaki on Twitter")
            twitter.onAction = EventHandler {
                if (XiaomiADBFastbootTools.linux)
                    Runtime.getRuntime().exec("xdg-open http://twitter.szaki.io/")
                else Desktop.getDesktop().browse(URI("http://twitter.szaki.io/"))
            }
            twitter.font = Font(15.0)
            val github = Hyperlink("Repository on GitHub")
            github.onAction = EventHandler {
                if (XiaomiADBFastbootTools.linux)
                    Runtime.getRuntime().exec("xdg-open https://github.com/Szaki/XiaomiADBFastbootTools")
                else Desktop.getDesktop().browse(URI("https://github.com/Szaki/XiaomiADBFastbootTools"))
            }
            github.font = Font(15.0)
            vb.children.addAll(discord, twitter, github)
            dialogPane.content = vb
            isResizable = false
            showAndWait()
        }
    }

    @FXML
    private fun uninstallerCopyAppNameMenuItemPressed(event: ActionEvent) {
        copyAppName(uninstallerTableView)
    }

    @FXML
    private fun uninstallerCopyPackageNameMenuItemPressed(event: ActionEvent) {
        copyPackageName(uninstallerTableView)
    }

    @FXML
    private fun reinstallerCopyAppNameMenuItemPressed(event: ActionEvent) {
        copyAppName(reinstallerTableView)
    }

    @FXML
    private fun reinstallerCopyPackageNameMenuItemPressed(event: ActionEvent) {
        copyPackageName(reinstallerTableView)
    }

    @FXML
    private fun disablerCopyAppNameMenuItemPressed(event: ActionEvent) {
        copyAppName(disablerTableView)
    }

    @FXML
    private fun disablerCopyPackageNameMenuItemPressed(event: ActionEvent) {
        copyPackageName(disablerTableView)
    }

    @FXML
    private fun enablerCopyAppNameMenuItemPressed(event: ActionEvent) {
        copyAppName(enablerTableView)
    }

    @FXML
    private fun enablerCopyPackageNameMenuItemPressed(event: ActionEvent) {
        copyPackageName(enablerTableView)
    }

    private fun copyAppName(tableView: TableView<App>) {
        val selectedItem = tableView.selectionModel.selectedItem

        if (selectedItem != null) {
            setClipboardValue(selectedItem.appnameProperty().value)
        }
    }

    private fun copyPackageName(tableView: TableView<App>) {
        val selectedItem = tableView.selectionModel.selectedItem

        if (selectedItem != null) {
            setClipboardValue(selectedItem.packagenameProperty().value)
        }
    }

    private fun setClipboardValue(value: String) {
        val toolkit = Toolkit.getDefaultToolkit()
        val clipboard = toolkit.systemClipboard
        val stringSelection = StringSelection(value)
        clipboard.setContents(stringSelection, stringSelection)
    }
}
