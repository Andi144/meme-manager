import java.awt.Color
import java.awt.GridBagLayout
import javax.swing.JPanel
import java.awt.GridBagConstraints
import java.awt.Image
import javax.swing.JButton
import javax.swing.JLabel
import javax.swing.JTextField

class MemeEditPanel(img: Image) : JPanel() {
	
	var memeName
		get() = textFieldName.text.trim()
		set(value) {
			textFieldName.text = value
		}
	var memeTags: Set<String>
		get() = textFieldTags.text
			.split(",")
			.asSequence()
			.map { tag -> tag.trim() }
			.filterTo(mutableSetOf()) { tag -> tag.isNotEmpty() }
		set(value) {
			textFieldTags.text = value.joinToString(" ")
		}
	
	private val textFieldName = JTextField()
	private val textFieldTags = JTextField()
	val buttonAdd = JButton("Add")
	val buttonCancel = JButton("Cancel")
	
	init {
		layout = GridBagLayout()
		val c = GridBagConstraints()
		c.anchor = GridBagConstraints.WEST
		c.gridx = 0
		c.gridy = 0
		add(JLabel("Name:"), c)
		c.gridx = 1
		c.gridy = 0
		c.fill = GridBagConstraints.HORIZONTAL
		add(textFieldName, c)
		c.gridx = 0
		c.gridy = 1
		add(JLabel("Tags:"), c)
		c.gridx = 1
		c.gridy = 1
		c.fill = GridBagConstraints.HORIZONTAL
		add(textFieldTags, c)
		c.gridx = 0
		c.gridy = 2
		c.fill = GridBagConstraints.HORIZONTAL
		add(buttonAdd, c)
		c.gridx = 1
		c.gridy = 2
		c.fill = GridBagConstraints.HORIZONTAL
		add(buttonCancel, c)
		c.gridx = 0
		c.gridy = 3
		c.gridwidth = GridBagConstraints.REMAINDER
		c.gridheight = 1
		// TODO: width, height
		add(ImagePanel(img = img, imageWidth = 200, imageHeight = 100), c)
	}
	
}