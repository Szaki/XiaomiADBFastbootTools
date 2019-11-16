object Device {

    var serial = ""
    var codename = ""
    var bootloader = false
    var camera2 = false
    var anti = -1
    var dpi = -1
    var width = -1
    var height = -1
    var props = mutableMapOf<String, String>()
    var mode = Mode.NONE
    var reinstaller = true
    var disabler = true
    val command = Command()

    fun readADB(): Boolean {
        val propstring = command.exec("adb shell getprop")
        when {
            "no devices" in propstring -> {
                if (mode != Mode.FASTBOOT && mode != Mode.FB_ERROR)
                    mode = Mode.NONE
                return false
            }
            "unauthorized" in propstring -> {
                mode = Mode.AUTH
                return false
            }
        }
        mode = if ("recovery" in command.exec("adb devices"))
            Mode.RECOVERY
        else Mode.ADB
        if (mode == Mode.ADB && serial in propstring && dpi != -1 && width != -1 && height != -1)
            return true
        props.clear()
        propstring.lineSequence().forEach {
            val parts = it.split("]: [")
            if (parts.size == 2)
                props[parts[0].trimStart('[')] = parts[1].trimEnd(']')
        }
        if (props["ro.serialno"].isNullOrEmpty() || props["ro.build.product"].isNullOrEmpty()) {
            mode = Mode.ADB_ERROR
            return false
        }
        serial = props["ro.serialno"] ?: ""
        codename = props["ro.build.product"] ?: ""
        bootloader = props["ro.boot.flash.locked"]?.contains("0") ?: false
        camera2 = props["persist.sys.camera.camera2"]?.contains("true") ?: false
        if (mode == Mode.ADB) {
            dpi = try {
                command.exec("adb shell wm density").substringAfterLast(':').trim().toInt()
            } catch (e: Exception) {
                -1
            }
            val size = command.exec("adb shell wm size")
            width = try {
                size.substringAfterLast(':').substringBefore('x').trim().toInt()
            } catch (e: Exception) {
                -1
            }
            height = try {
                size.substringAfterLast('x').trim().toInt()
            } catch (e: Exception) {
                -1
            }
        }
        return true
    }

    fun readFastboot(): Boolean {
        val status = command.exec("fastboot devices", err = false)
        when {
            status.isEmpty() -> {
                if (mode == Mode.FASTBOOT || mode == Mode.FB_ERROR)
                    mode = Mode.NONE
                return false
            }
            mode == Mode.FASTBOOT && serial in status -> return true
        }
        props.clear()
        command.exec("fastboot getvar all").lineSequence().forEach {
            if (it.startsWith('('))
                props[it.substringAfter(')').substringBeforeLast(':').trim()] = it.substringAfterLast(':').trim()
        }
        if (props["serialno"].isNullOrEmpty() || props["product"].isNullOrEmpty()) {
            mode = Mode.FB_ERROR
            return false
        }
        serial = props["serialno"] ?: ""
        codename = props["product"] ?: ""
        bootloader = props["unlocked"]?.contains("yes") ?: false
        anti = props["anti"]?.toInt() ?: -1
        mode = Mode.FASTBOOT
        return true
    }
}
