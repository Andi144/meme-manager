import java.awt.BorderLayout
import javax.swing.*
import javax.swing.border.EmptyBorder
import javax.swing.event.DocumentEvent
import javax.swing.event.DocumentListener
import java.awt.Image
import java.awt.Toolkit
import java.awt.datatransfer.DataFlavor
import java.awt.event.*
import java.io.File
import java.nio.file.Files
import java.nio.file.Paths
import java.util.*
import javax.imageio.ImageIO

class MemeManager {
	
	private val frame = JFrame("Meme Manager")
	private val textFieldSearch = JTextField()
	private val panelMemes = JPanel()
	
	private val allMemes = mutableSetOf<Meme>()
	// the directory where all memes are stored; always in the directory where the MemeManager was started
	private val memeDir = "memes"
	// always in the directory where the MemeManager was started
	private val memeFile = "memes.csv"
	
	init {
		// create the meme directory if it does not exist
		Files.createDirectories(Paths.get(memeDir))
		
		val contentPane = JPanel(BorderLayout())
		contentPane.border = EmptyBorder(5, 5, 5, 5)
		
		frame.setBounds(0, 0, 1280, 720)
		frame.setLocationRelativeTo(null)
		frame.defaultCloseOperation = JFrame.EXIT_ON_CLOSE
		frame.contentPane = contentPane
		frame.addWindowListener(object : WindowAdapter() {
			override fun windowClosing(e: WindowEvent?) {
				// TODO
			}
		})
		
		// center
		contentPane.add(panelMemes)
		
		// north
		val panelNorth = JPanel(BorderLayout(5, 0))
		contentPane.add(panelNorth, BorderLayout.NORTH)
		panelNorth.add(JLabel("Search:"), BorderLayout.WEST)
		panelNorth.add(textFieldSearch, BorderLayout.CENTER)
		
		addMenuBar()
		loadAndSetMemes()
		
		// listeners
		textFieldSearch.document.addDocumentListener(object : DocumentListener {
			override fun changedUpdate(e: DocumentEvent?) {
				filter()
			}
			
			override fun insertUpdate(e: DocumentEvent?) {
				filter()
			}
			
			override fun removeUpdate(e: DocumentEvent?) {
				filter()
			}
			
			fun filter() {
				val text = textFieldSearch.text
				// val tags = text.split(",").filter { it.isNotEmpty() }
				val newMemes = allMemes.filterTo(mutableSetOf()) {
					//it.tags.containsAll(tags)
					// TODO: could just simply store one text string for tags instead of sets/lists
					it.tags.joinToString(separator = " ").contains(text, ignoreCase = true)
				}
				setMemes(newMemes)
			}
		})
	}
	
	private fun addMenuBar() {
		val menuBar = JMenuBar()
		frame.jMenuBar = menuBar
		
		val menuFile = JMenu("File")
		menuBar.add(menuFile)
		
		val menuItemAdd = JMenuItem("Add")
		menuItemAdd.addActionListener { _ ->
			val imageFromClipboard: Image? = try {
				Toolkit.getDefaultToolkit().systemClipboard.getData(DataFlavor.imageFlavor) as Image
			} catch (e: Exception) {
				JOptionPane.showMessageDialog(frame, "No image found in clipboard", "Error", JOptionPane.ERROR_MESSAGE)
				null
			}
			
			if (imageFromClipboard != null) {
				val dialog = JDialog()
				// TODO
//				dialog.isUndecorated = true
				dialog.contentPane.add(MemeEditPanel(imageFromClipboard).also {
					it.buttonCancel.addActionListener { _ -> dialog.dispose() }
					it.buttonAdd.addActionListener { _ ->
						if (allMemes.any { meme -> meme.name == it.memeName }) {
							JOptionPane.showMessageDialog(frame, "Name already exists", "Error", JOptionPane.ERROR_MESSAGE)
						} else {
							val image = Util.convertToBufferedImage(imageFromClipboard)
							val imageFile = "$memeDir/${UUID.randomUUID()}.png"
							ImageIO.write(image, "png", File(imageFile))
							val meme = Meme(it.memeName, it.memeTags, imageFile)
							allMemes.add(meme)
							Util.addMemeToCSV(meme, memeFile)
							setMemes(allMemes)
							dialog.dispose()
						}
					}
				})
				dialog.isModal = true
				dialog.pack()
				dialog.setLocationRelativeTo(frame)
				dialog.isVisible = true
			}
		}
		menuItemAdd.accelerator = KeyStroke.getKeyStroke(KeyEvent.VK_N, InputEvent.CTRL_DOWN_MASK)
		menuFile.add(menuItemAdd)
		
		val menuItemExit = JMenuItem("Exit")
		menuItemExit.addActionListener { frame.dispose() }
		menuItemExit.accelerator = KeyStroke.getKeyStroke(KeyEvent.VK_Q, InputEvent.CTRL_DOWN_MASK)
		menuFile.add(menuItemExit)
	}
	
	private fun loadAndSetMemes() {
		val newMemes: List<Meme> = Util.memesFromCSV(memeFile)
		allMemes.addAll(newMemes)
		setMemes(newMemes)
	}
	
	private fun setMemes(newMemes: Collection<Meme>) {
		panelMemes.removeAll()
		newMemes.forEach {
			val memePanel = MemePanel(it)
			memePanel.addMouseListener(object : MouseAdapter() {
				override fun mouseClicked(e: MouseEvent?) {
					setClipboard(memePanel.image)
				}
			})
			panelMemes.add(memePanel)
		}
		panelMemes.revalidate()
		panelMemes.repaint()
	}
	
	fun setClipboard(image: Image) {
		Toolkit.getDefaultToolkit().systemClipboard.setContents(ImageSelection(image), null)
	}
	
	fun activate() {
		frame.isVisible = true
	}
	
}

fun main(args: Array<String>) {
	try {
		UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName())
	} catch (e: Exception) {
		System.err.println("Fatal error: ")
		e.printStackTrace()
		System.err.println("Shutting down.")
		System.exit(-1)
	}
	SwingUtilities.invokeLater { MemeManager().activate() }
}