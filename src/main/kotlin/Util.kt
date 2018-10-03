import java.awt.Image
import java.awt.image.BufferedImage
import java.nio.file.Files
import java.nio.file.Paths
import java.nio.file.StandardOpenOption

object Util {
	
	private const val tagSeparator = "|"
	
	fun memesFromCSV(file: String): List<Meme> =
		Files.readAllLines(Paths.get(file)).map {
			val parts = it.split(",")
			Meme(parts[0], parts[1].split(tagSeparator).toSet(), parts[2])
		}
	
	fun addMemeToCSV(meme: Meme, memeFile: String) {
		val memeString = "${meme.name},${meme.tags.joinToString(tagSeparator)},${meme.imageFile}"
		Files.write(Paths.get(memeFile), listOf(memeString), StandardOpenOption.CREATE, StandardOpenOption.APPEND)
	}
	
	fun convertToBufferedImage(img: Image): BufferedImage {
		if (img is BufferedImage) {
			return img
		}
		
		// Create a buffered image with transparency
		val image = BufferedImage(img.getWidth(null), img.getHeight(null), BufferedImage.TYPE_INT_ARGB)
		
		// Draw the image on to the buffered image
		val bGr = image.createGraphics()
		bGr.drawImage(img, 0, 0, null)
		bGr.dispose()
		
		// Return the buffered image
		return image
	}
	
}