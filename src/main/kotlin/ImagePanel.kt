import java.awt.Dimension

import java.awt.Graphics
import java.awt.Image
import java.awt.image.BufferedImage
import java.io.File
import javax.imageio.ImageIO
import javax.swing.JPanel

class ImagePanel(
	file: String? = null,
	img: Image? = null,
	private var imageWidth: Int = 100,
	private var imageHeight: Int = 100
) : JPanel() {
	
	val image: BufferedImage = if (file == null) img as BufferedImage else readImage(file)
	
	init {
		preferredSize = Dimension(imageWidth, imageHeight)
	}
	
	private fun readImage(file: String): BufferedImage = ImageIO.read(File(file))
	
	fun setImageSize(width: Int, height: Int) {
		imageWidth = width
		imageHeight = height
		// TODO: repaint?
	}
	
	override fun paintComponent(g: Graphics) {
		super.paintComponent(g)
		g.drawImage(image, 0, 0, imageWidth, imageHeight, this)
	}
	
}