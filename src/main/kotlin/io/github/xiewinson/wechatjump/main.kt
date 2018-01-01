package io.github.xiewinson.wechatjump

import javafx.application.Application
import javafx.scene.image.Image
import javafx.scene.input.MouseEvent
import javafx.scene.layout.VBox
import tornadofx.App
import tornadofx.View
import java.io.File

fun main(args: Array<String>) {
    exec("adb devices")
    Application.launch(MyApp::class.java)
}

private fun exec(cmd: String) {
    val process = Runtime.getRuntime().exec(cmd)
    process.waitFor()
    process.inputStream.bufferedReader().use { reader ->
        println(reader.readText())
    }
}

private fun execPress(duration: Int) {
    exec("adb shell input swipe 500 500 500 500 $duration")
}

private fun execScreencap() {
    exec("adb shell screencap -p /sdcard/screen.png")
    exec("adb pull /sdcard/screen.png ./screen/screen.png")
}


class MyApp : App(MyView::class)
class MyView : View() {
    override val root = VBox()

    init {
        with(root) {
            execScreencap()
            val imageView = javafx.scene.image.ImageView(getScreenImage())
            this.add(imageView)
            imageView.fitHeight = 800.0
            imageView.fitWidth = 450.0
            var firstValue: MouseEvent? = null
            imageView.setOnMouseClicked { value ->
                if (firstValue == null) {
                    firstValue = value
                } else {
                    val distance = Math.sqrt(Math.pow(value.x - firstValue!!.x, 2.0) + Math.pow(value.y - firstValue!!.y, 2.0))

                    execPress((distance * 3.32).toInt())
                    firstValue = null

                    Thread.sleep(1500)
                    execScreencap()
                    imageView.image = getScreenImage()

                }
            }

        }
    }

    private fun getScreenImage() = Image(File("./screen/screen.png").inputStream())


}
