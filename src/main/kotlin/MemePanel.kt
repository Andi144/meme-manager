import java.awt.BorderLayout
import java.awt.Color
import java.awt.Image
import java.awt.Toolkit
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent
import javax.swing.BorderFactory
import javax.swing.JLabel
import javax.swing.JPanel

class MemePanel(meme: Meme) : JPanel() {
	
	private val imagePanel = ImagePanel(meme.imageFile)
	
	init {
		layout = BorderLayout()
		add(JLabel(meme.name).also { it.horizontalAlignment = JLabel.CENTER }, BorderLayout.NORTH)
		add(imagePanel, BorderLayout.CENTER)
		border = BorderFactory.createLineBorder(Color.BLACK, 1)
		
		addMouseListener(object : MouseAdapter() {
			override fun mouseEntered(e: MouseEvent?) {
				border = BorderFactory.createLineBorder(Color.RED, 1)
			}
			
			override fun mouseExited(e: MouseEvent?) {
				border = BorderFactory.createLineBorder(Color.BLACK, 1)
			}
			
			override fun mouseClicked(e: MouseEvent?) {
				setClipboard(imagePanel.image)
			}
		})
	}
	
	private fun setClipboard(image: Image) {
		Toolkit.getDefaultToolkit().systemClipboard.setContents(ImageSelection(image), null)
	}
	
}