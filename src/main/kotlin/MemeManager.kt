import java.awt.BorderLayout
import java.awt.Image
import java.awt.Toolkit
import java.awt.datatransfer.DataFlavor
import java.awt.event.InputEvent
import java.awt.event.KeyEvent
import java.awt.event.WindowAdapter
import java.awt.event.WindowEvent
import java.io.File
import java.nio.file.Files
import java.nio.file.Paths
import java.util.*
import javax.imageio.ImageIO
import javax.swing.*
import javax.swing.border.EmptyBorder
import javax.swing.event.DocumentEvent
import javax.swing.event.DocumentListener

class MemeManager {
	
	private val frame = JFrame("Meme Manager")
	private val textFieldSearch = JTextField()
	private val panelMemes = JPanel(ModifiedFlowLayout())
	
	private val memeToPanel = mutableMapOf<Meme, MemePanel>()
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
		contentPane.add(JScrollPane(panelMemes).also {
			it.horizontalScrollBarPolicy = ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER
			it.verticalScrollBar.unitIncrement = 16
		})
		
		// north
		val panelNorth = JPanel(BorderLayout(5, 0))
		contentPane.add(panelNorth, BorderLayout.NORTH)
		panelNorth.add(JLabel("Search:"), BorderLayout.WEST)
		panelNorth.add(textFieldSearch, BorderLayout.CENTER)
		
		addMenuBar()
		loadAndSetStoredMemes()
		
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
				memeToPanel.forEach { (meme, memePanel) ->
					// TODO: could just simply store one text string for tags instead of sets/lists
					memePanel.isVisible = (meme.tags.joinToString(separator = " ").contains(text, ignoreCase = true))
				}
			}
		})
	}
	
	private fun addMenuBar() {
		val menuBar = JMenuBar()
		frame.jMenuBar = menuBar
		
		val menuFile = JMenu("File")
		menuBar.add(menuFile)
		
		val menuItemAdd = JMenuItem("Add")
		menuItemAdd.addActionListener {
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
				dialog.contentPane.add(MemeEditPanel(imageFromClipboard).also { memeEditPanel ->
					memeEditPanel.buttonCancel.addActionListener { dialog.dispose() }
					memeEditPanel.buttonAdd.addActionListener {
						if (allMemes.any { meme -> meme.name == memeEditPanel.memeName }) {
							JOptionPane.showMessageDialog(frame, "Name already exists", "Error", JOptionPane.ERROR_MESSAGE)
						} else {
							val image = Util.convertToBufferedImage(imageFromClipboard)
							val imageFile = "$memeDir/${UUID.randomUUID()}.png"
							ImageIO.write(image, "png", File(imageFile))
							val meme = Meme(memeEditPanel.memeName, memeEditPanel.memeTags, imageFile)
							val memePanel = MemePanel(meme)
							memeToPanel[meme] = memePanel
							panelMemes.add(memePanel)
							panelMemes.revalidate()
							panelMemes.repaint()
							Util.addMemeToCSV(meme, memeFile)
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
	
	private fun loadAndSetStoredMemes() {
		val newMemes: List<Meme> = Util.memesFromCSV(memeFile)
		newMemes.forEach {
			val memePanel = MemePanel(it)
			memeToPanel[it] = memePanel
			panelMemes.add(memePanel)
		}
		panelMemes.revalidate()
		panelMemes.repaint()
	}
	
	fun activate() {
		frame.isVisible = true
	}
	
}

fun main() {
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