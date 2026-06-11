package io.github.arrayv.sorts.hybrid;

import java.util.ArrayList;
import java.util.Collection;

import io.github.arrayv.main.ArrayVisualizer;
import io.github.arrayv.sorts.insert.BinaryInsertionSort;
import io.github.arrayv.sorts.templates.GrailSorting;
import io.github.arrayv.utils.IndexedRotations;

public class TourneyCaiSort extends GrailSorting {
	public TourneyCaiSort(ArrayVisualizer arrayVisualizer) {
		super(arrayVisualizer);
        
        this.setSortListName("Tourney-Cai");
        this.setRunAllSortsName("Tourney-Cai Sort");
        this.setRunSortName("Tourney-Caisort");
        this.setCategory("Golf Sorts");
        this.setConstant("n sqrt n");
        this.setBucketSort(false);
        this.setRadixSort(false);
        this.setUnreasonablySlow(false);
        this.setQuestion("Set the base for this sort:", 3);
        this.setUnreasonableLimit(0);
        this.setBogoSort(false);
        this.setAuthors("Distray");
	}
	
	public int buf, bufsz;
	private int[] arr;
	private static final int minBinsert = 8;
	private BinaryInsertionSort binserter;
	
	private class Buffer {
		public int pstart, start, mid, end, index;
		public Buffer cL, cR, cW, cPar;
		public Buffer(int start, int mid, int end, int index) {
			this.pstart = this.start = start;
			this.mid = mid;
			this.end = end;
			this.index = index;
		}
		public Buffer() {
			this.pstart = this.start = this.mid = this.end = this.index = -1;
		}
		public int sortedLength() {
			if(end-mid <= 0) return -1;
			return end-mid;
		}
		public boolean oob() {
			return mid>=end || start>=end || start>mid;
		}
		public int bufferLength() {
			return mid-start;
		}
		public void copy(Buffer nxt) {
			this.pstart = nxt.pstart;
			this.index = nxt.index;
			this.start = nxt.start;
			this.mid = nxt.mid;
			this.end = nxt.end;
		}
		public void update() {
			if(cW != null) {
				if(cL != null)
					cL.update();
				if(cR != null)
					cR.update();
				copy(cW);
			}
		}
		public void build() {
			if(cL==null||cL.sortedLength()<=0||cL.oob()) {
				if(cR==null||cR.sortedLength()<=0||cR.oob()) {
					end=-1;
					cL=cR=cW=null;
					return;
				}
				copy(cW = cR);
			} else if(cR==null||cR.sortedLength()<=0||cR.oob()){
				copy(cW = cL);
			} else if(Reads.compareIndices(arr, cL.mid, cR.mid, 1, true) <= 0) {
				copy(cW = cL);
			} else {
				copy(cW = cR);
			}
		}
		public String toString() {
			return String.format("<%d, %d, %d>", start, mid, end);
		}
	}
	
	protected void grailRotate(int[] array, int pos, int lena, int lenb) {
		IndexedRotations.neon22(array, pos, pos+lena, pos+lena+lenb, 1, true, false);
	}
	
	private Buffer build(ArrayList<Buffer> tree, int start, int end) { // same as Narnia's build, but for TourneyCai
		if(start >= end)
			return tree.get(start);
		int mid=start+(end-start)/2;
		Buffer p = new Buffer();
		p.cL=build(tree, start, mid); // build left child
		p.cR=build(tree, mid+1, end); // build right child
		p.cL.cPar = p.cR.cPar = p;
		p.build(); // build parent of children
		return p;
	}
	
	private void sift(Buffer root) {
		Buffer now = root;
		while(now.cW != null) { // keep looking until you find a winner value
			now = now.cW;
		}
		if(now.sortedLength() > 0 && !now.oob()) // step further if you are in-bounds
			now.mid++;
		now = now.cPar;
		while(now != null) {
			now.build();
			now = now.cPar;
		}
	}
	
	// *still in desperate need of cleanup*
	public void caiMerge(int[] array, int... ptrs) {
		this.arr = array;
		ArrayList<Buffer> buffers = new ArrayList<>();
		for(int i=0; i<ptrs.length-1; i++) {
			buffers.add(new Buffer(ptrs[i], ptrs[i], ptrs[i+1], i));
		}
		buffers.get(0).start = buffers.get(0).pstart = buf;
		Buffer root = build(buffers, 0, ptrs.length-2);
		int to = buf;
		while(true) {
			int maxBuffer = 0;
			boolean oob = true;
			for(int i=1; i<buffers.size(); i++) {
				if(buffers.get(maxBuffer).bufferLength() < buffers.get(i).bufferLength()) {
					maxBuffer = i;
				}
				oob = oob && buffers.get(i).oob(); // check whether everything's gone
			}
			Buffer now = buffers.get(maxBuffer);
			if(oob) { // nope out (ensure you don't get stuck)
				break;
			}
			while(now.bufferLength() > 0) { // merge the values while buffer remains
				// break away under the same conditions as Cai Mk. II,
				// but adjusted for Tourney-Cai
				if(root.index == -1 || now.sortedLength() <= 0 || now.oob())
					break;
				Writes.swap(array, now.start++, root.mid, 1, true, false); // swap with the winner,
				sift(root); // resift, and update winner values at same time
			}
			if(now.bufferLength() > 0) { // still buffer remaining,
				while(now.bufferLength() > 0) { // merge outside of the subarray
					// functionally the same as minKey1's breakaway
					if(root.index == -1)
						break;
					Writes.swap(array, now.start++, root.mid, 1, true, false); // swap with the winner,
					sift(root); // resift
				}
			}
			if(maxBuffer > 0) { // push merged section back, if required
				int e = now.pstart;
				IndexedRotations.neon22(array, to, e, now.start, 1, true, false);
				for(int i=maxBuffer-1; i>=0; i--) { // adjust all the subarrays behind accordingly
					buffers.get(i).end += now.start-e;
					buffers.get(i).mid += now.start-e;
					buffers.get(i).start += now.start-e;
					buffers.get(i).pstart += now.start-e;
				}
				// update all the values to match
				// (resifting takes way more steps, so use a different function)
				root.update();
				to += now.start - e;
				now.pstart = now.start;
			} else { // just change the variables, nothing else needs to be done here
				to = now.pstart = now.start;
			}
		}
		// push remaining buffers back (Caisort can't handle its buffer, apparently)
		for(Buffer i : buffers) {
			if(i.sortedLength() > 0) {
				for(int j=i.mid; j<i.end; j++) {
					Writes.swap(array, to++, j, 1, true, false);
				}
			}
		}
		buf = ptrs[ptrs.length-1]-bufsz;
	}
	
	public void caiMerge(int[] array, Collection<Integer> ptrs) {
		Integer[] norm = ptrs.toArray(new Integer[0]);
		int[] prim = new int[ptrs.size()];
		for(int i=0; i<ptrs.size(); i++) {
			prim[i] = norm[i];
		}
		caiMerge(array, prim);
	}
	
	private void binsertruns(int[] array, int start, int end) {
		int m=Math.max(2*minBinsert, bufsz);
		for(int i=start; i<end; i+=m) {
			binserter.binaryInsertSort(array, i, Math.min(i+m, end), 0.025, 0.05);
		}
	}
	
	public void runCai(int[] array, int start, int end, int base) {
		buf = start;
		bufsz = (int) Math.pow(end-start, 0.5d);
		bufsz = grailFindKeys(array, start, end-start, bufsz);
		binserter = new BinaryInsertionSort(arrayVisualizer);
		if(bufsz < 4) {
			binserter.binaryInsertSort(array, start, end, 0.5, 0.5);
			return;
		}
		binsertruns(array, buf+bufsz, end);
		for(int j=Math.max(bufsz, 2*minBinsert); j<end-start; j*=base) {
			for(int i=buf+bufsz; i<end; i+=base*j) {
				if(i+j >= end)
					break;
				ArrayList<Integer> ptrs = new ArrayList<>();
				int l = i;
				for(int k = 0; k < base && l < end; k++) {
					ptrs.add(l);
					l += j;
				}
				ptrs.add(Math.min(l, end));
				caiMerge(array, ptrs);
			}
			IndexedRotations.neon22(array, start, buf, buf+bufsz, 1, true, false);
			buf=start;
		}
		binserter.binaryInsertSort(array, buf, buf+bufsz, 0.25, 0.05);
		grailMergeWithoutBuffer(array, buf, bufsz, end-(buf+bufsz));
	}
	
	@Override
	public int validateAnswer(int val) {
		if(val < 2) return 2;
		return val;
	}
	
	@Override
	public void runSort(int[] array, int len, int base) {
		runCai(array, 0, len, base);
	}
}