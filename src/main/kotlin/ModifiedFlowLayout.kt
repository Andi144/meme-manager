import java.awt.*

/**
 * A modified version of FlowLayout that allows containers using this
 * Layout to behave in a reasonable manner when placed inside a
 * JScrollPane
 * @author Babu Kalakrishnan
 * Modifications by greearb and jzd
 */
class ModifiedFlowLayout : FlowLayout {
	
	constructor() : super() {}
	
	constructor(align: Int) : super(align) {}
	constructor(align: Int, hgap: Int, vgap: Int) : super(align, hgap, vgap) {}
	
	override fun minimumLayoutSize(target: Container): Dimension {
		// Size of largest component, so we can resize it in
		// either direction with something like a split-pane.
		return computeMinSize(target)
	}
	
	override fun preferredLayoutSize(target: Container): Dimension {
		return computeSize(target)
	}
	
	private fun computeSize(target: Container): Dimension {
		synchronized(target.treeLock) {
			val hgap = hgap
			val vgap = vgap
			var w = target.width
			
			// Let this behave like a regular FlowLayout (single row)
			// if the container hasn't been assigned any size yet
			if (w == 0) {
				w = Integer.MAX_VALUE
			}
			
			var insets: Insets? = target.insets
			if (insets == null) {
				insets = Insets(0, 0, 0, 0)
			}
			var reqdWidth = 0
			
			val maxwidth = w - (insets.left + insets.right + hgap * 2)
			val n = target.componentCount
			var x = 0
			var y = insets.top + vgap // FlowLayout starts by adding vgap, so do that here too.
			var rowHeight = 0
			
			for (i in 0 until n) {
				val c = target.getComponent(i)
				if (c.isVisible) {
					val d = c.preferredSize
					if (x == 0 || x + d.width <= maxwidth) {
						// fits in current row.
						if (x > 0) {
							x += hgap
						}
						x += d.width
						rowHeight = Math.max(rowHeight, d.height)
					} else {
						// Start of new row
						x = d.width
						y += vgap + rowHeight
						rowHeight = d.height
					}
					reqdWidth = Math.max(reqdWidth, x)
				}
			}
			y += rowHeight
			y += insets.bottom
			return Dimension(reqdWidth + insets.left + insets.right, y)
		}
	}
	
	private fun computeMinSize(target: Container): Dimension {
		synchronized(target.treeLock) {
			var minx = Integer.MAX_VALUE
			var miny = Integer.MIN_VALUE
			var found_one = false
			val n = target.componentCount
			
			for (i in 0 until n) {
				val c = target.getComponent(i)
				if (c.isVisible) {
					found_one = true
					val d = c.preferredSize
					minx = Math.min(minx, d.width)
					miny = Math.min(miny, d.height)
				}
			}
			return if (found_one) {
				Dimension(minx, miny)
			} else Dimension(0, 0)
		}
	}
	
}