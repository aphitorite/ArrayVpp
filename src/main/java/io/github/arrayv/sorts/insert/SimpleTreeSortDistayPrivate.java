package io.github.arrayv.sorts.insert;

import io.github.arrayv.main.ArrayVisualizer;
import io.github.arrayv.sorts.templates.Sort;
import java.awt.Color;
import io.github.arrayv.utils.*;
import java.awt.geom.Ellipse2D;
import io.github.arrayv.visuals.Visual;

final public class SimpleTreeSortDistayPrivate extends Sort {
	public SimpleTreeSortDistayPrivate(ArrayVisualizer arrayVisualizer) {
		super(arrayVisualizer);
		
		this.setSortListName("Simple Tree (Distay Private)");
		this.setRunAllSortsName("Simple Tree Sort (Distay Private)");
		this.setRunSortName("Simple Treesort (Distay Private)");
		this.setCategory("Insertion Sorts");
        this.setAuthors("Distray");
        this.setConstant("n log n");
		this.setBucketSort(false);
		this.setRadixSort(false);
		this.setUnreasonablySlow(false);
		this.setUnreasonableLimit(0);
		this.setBogoSort(false);
	}

	private int maxdepth;
	
	class Node extends Renderable {
		public Node left, right;
		public int index;
		public int depth;
		public boolean scanned;
		private int[] array;
		public Node(int[] array, int index) {
			left = right = null; depth = 0;
			this.array = array;
			this.index = index;
			Writes.changeAllocAmount(1);
			if(Renderer.visualSupportsRenderables()) {
				Renderer.registerRenderable(this);
			}
		}
		public void setLeftChild(Node n) {
			left = n;
			left.depth = depth + 1;
			maxdepth = Math.max(left.depth, maxdepth);
			Writes.changeAuxWrites(1);
		}
		public void setRightChild(Node n) {
			right = n;
			right.depth = depth + 1;
			maxdepth = Math.max(right.depth, maxdepth);
			Writes.changeAuxWrites(1);
		}
		public int getVal() {
			return array[index];
		}
		public int[] getPositions() {
			return arrayVisualizer.getTopPos(array, index);
		}
		public void render(int[] blank, ArrayVisualizer arrayVisualizer, Renderer renderer, Highlights highlights) {
			int[] pos = getPositions();
			int rx = pos[0], y = pos[1];
			Color t = mainRender.getColor();
			int doff = (int) (renderer.getXScale() / 2);
			if(left != null) {
				mainRender.setColor(left.scanned ? Color.GREEN : Color.GRAY);
				int[] lp = left.getPositions();
				mainRender.drawLine(rx, y+doff, lp[0], lp[1]+doff);
			}
			if(right != null) {
				mainRender.setColor(right.scanned ? Color.GREEN : Color.LIGHT_GRAY);
				int[] rp = right.getPositions();
				mainRender.drawLine(rx, y+doff, rp[0], rp[1]+doff);
			}
			mainRender.setColor(scanned ? Color.GREEN : Visual.getIntColor(depth, maxdepth + 1));
			double scale = 1.5d;
			Ellipse2D e = new Ellipse2D.Double(rx-renderer.getXScale()*(scale/2d), y-renderer.getXScale()*((scale-1d)/2d), renderer.getXScale()*scale, renderer.getXScale()*scale);
			mainRender.fill(e);
			mainRender.setColor(t);
		}
	}
		
	public void insertBranch(Node root, int index) { // iterative (A0726's TreeSort.java goes recursive)
		int val = root.array[index];
		int tree = 1;
		while(true) {
			Highlights.markArray(tree++, root.index);
			Delays.sleep(0.625);
			if(Reads.compareValues(val, root.getVal()) < 0) {
				if(root.left == null) {
					root.setLeftChild(new Node(root.array, index));
					break;
				} else {
					root = root.left;
				}
			} else {
				if(root.right == null) {
					root.setRightChild(new Node(root.array, index));
					break;
				} else {
					root = root.right;
				}
			}
		}
		Highlights.clearAllMarks();
	}
	
	public int traverseTree(int[] tree, Node root, int idx, int recursion) {
		Writes.recordDepth(recursion++);
		int v = 0;
		if(root.left != null) {
			Writes.recursion();
			v += traverseTree(tree, root.left, idx, recursion);
		}
		Highlights.markArray(1, root.index);
		Delays.sleep(1);
		Writes.changeAuxWrites(1);
		Writes.write(tree, idx + v++, root.getVal(), 1, true, true); // lose a value and gain
		root.scanned = true;
		if(root.right != null) {
			Writes.recursion();
			v += traverseTree(tree, root.right, idx + v, recursion);
		}
		return v;
	}
	
	@Override
	public void runSort(int[] array, int length, int bucketCount) {
		this.maxdepth = 0;
		Node root = new Node(array, 0);
		for(int i=1; i<length; i++) {
			insertBranch(root, i);
		}
		
		int[] tree = Writes.createExternalArray(length);
		
		traverseTree(tree, root, 0, 0);
		
		Renderer.unregisterAllRenderables();
		Writes.changeAllocAmount(-length);
		
		Writes.arraycopy(tree, 0, array, 0, length, 2, true, false);
		Writes.deleteExternalArray(tree);
	}
}
