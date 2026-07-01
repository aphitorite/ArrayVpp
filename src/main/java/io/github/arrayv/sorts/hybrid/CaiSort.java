package io.github.arrayv.sorts.hybrid;

import java.util.ArrayList;
import java.util.Collection;

import io.github.arrayv.main.ArrayVisualizer;
import io.github.arrayv.sorts.insert.BranchlessBinaryInsertionSort;
import io.github.arrayv.sorts.templates.GrailSorting;
import io.github.arrayv.utils.IndexedRotations;

public class CaiSort extends GrailSorting {
	public CaiSort(ArrayVisualizer arrayVisualizer) {
		super(arrayVisualizer);
        
        this.setSortListName("Cai");
        this.setRunAllSortsName("Cai Sort");
        this.setRunSortName("Caisort");
        this.setCategory("Golf Sorts");
        this.setConstant("n log n");
        this.setBucketSort(false);
        this.setRadixSort(false);
        this.setUnreasonablySlow(false);
        this.setQuestion("Set the base for this sort:", 3);
        this.setUnreasonableLimit(0);
        this.setBogoSort(false);
        this.setAuthors("Distray");
	}
	
	private void multiSwap(int[] array, int locA, int locB, int length) {
		while(length-- > 0) {
			Writes.swap(array, locA++, locB++, 1, true, false);
		}
	}
	
	private int buf, bufsz;
	private static final int minBinsert = 8;
	private BranchlessBinaryInsertionSort binserter;
	
	private class Buffer {
		public int pstart, start, mid, end;
		public Buffer(int start, int mid, int end) {
			this.pstart = this.start = start;
			this.mid = mid;
			this.end = end;
		}
		public int sortedLength() {
			if(end-mid <= 0) return -1;
			return Math.max(end-mid, 0/* start-pstart */);
		}
		public boolean oob() {
			return mid>=end || start>=end || start>mid;
		}
		public int bufferLength() {
			return mid-start;
		}
		public String toString() {
			return String.format("<%d, %d, %d>", start, mid, end);
		}
	}
	
	protected void grailRotate(int[] array, int pos, int lena, int lenb) {
		IndexedRotations.neon22(array, pos, pos+lena, pos+lena+lenb, 1, true, false);
	}
	
	// gonna switch to Tournamerge
	protected int minKey(int[] array, ArrayList<Buffer> buffers, int exclude) {
		int min=exclude;
		if(buffers.get(min).sortedLength() <= 0 || buffers.get(min).oob())
			return -1;
		for(int i=0; i<buffers.size(); i++) {
			if(buffers.get(i).sortedLength() <= 0 || buffers.get(min).oob()) continue;
			if(i != exclude && Reads.compareIndices(array, buffers.get(i).mid, buffers.get(min).mid, 0.1, true) < 0)
				min=i;
		}
		return min;
	}
	
	private boolean allInRange(ArrayList<Buffer> buffers) {
		if(buffers.size() == 1) return false;
		for(Buffer i : buffers) {
			if(i.sortedLength() <= 0)
				return false;
		}
		return true;
	}
	
	// private boolean allInRange(ArrayList<Buffer> buffers, Buffer exclude) {
	// 	if(buffers.size() == 1) return false;
	// 	for(Buffer i : buffers) {
	// 		if(i != exclude && i.sortedLength() <= 0)
	// 			return false;
	// 	}
	// 	return true;
	// }
	
	protected void caiMerge(int[] array, int... ptrs) {
		ArrayList<Buffer> buffers = new ArrayList<>();
		for(int i=0; i<ptrs.length-1; i++) {
			buffers.add(new Buffer(ptrs[i], ptrs[i], ptrs[i+1]));
		}
		buffers.get(0).start = buffers.get(0).pstart = buf;
		int to = buf;
		while(buffers.size() > 1) {
			int maxBuffer = 0;
			for(int i=1; i<buffers.size(); i++) {
				if((buffers.get(maxBuffer).bufferLength() < buffers.get(i).bufferLength() ||
					buffers.get(maxBuffer).oob()) && !buffers.get(i).oob()) {
					maxBuffer = i;
				}
			}
			Buffer now = buffers.get(maxBuffer);
			if(now.bufferLength() <= 0 || now.oob()) { // unknown
				break;
			}
			while(now.bufferLength() > 0 && allInRange(buffers)) {
				int j = minKey(array, buffers, maxBuffer);
				Writes.swap(array, now.start++, buffers.get(j).mid++, 1, true, false);
			}
			System.out.println(buffers);
			if(now.bufferLength() > 0) {
				while(now.bufferLength() > 0) {
					int j = minKey(array, buffers, maxBuffer);
					if(j < 0) break;
					Writes.swap(array, now.start++, buffers.get(j).mid++, 1, true, false);
				}
				while(now.mid < now.end) {
					Writes.swap(array, now.start++, now.mid++, 1, true, false);
				}
			}
			if(maxBuffer > 0) {
				int e = now.pstart;
				IndexedRotations.neon22(array, to, e, now.start, 1, true, false);
				for(int i=maxBuffer-1; i>=0; i--) {
					buffers.get(i).end += now.start-e;
					buffers.get(i).mid += now.start-e;
					buffers.get(i).start += now.start-e;
					buffers.get(i).pstart += now.start-e;
				}
				to += now.start - e;
				now.pstart = now.start;
			} else {
				to = now.pstart = now.start;
			}
			for(int i=buffers.size()-1; i>=0; i--) {
				Buffer cur = buffers.get(i);
				if(cur != now && cur.sortedLength() <= 0) {
					IndexedRotations.neon22(array, cur.pstart, cur.mid, ptrs[ptrs.length-1], 1, true, false);
					for(int j=buffers.size()-1; j>i; j--) {
						buffers.get(j).end -= cur.mid-cur.pstart;
						buffers.get(j).mid -= cur.mid-cur.pstart;
						buffers.get(j).start -= cur.mid-cur.pstart;
						buffers.get(j).pstart -= cur.mid-cur.pstart;
					}
					buffers.remove(i);
				}
			}
		}
		for(Buffer i : buffers) { // push the buffers remaining
			if(i.sortedLength() > 0) {
				if(i.sortedLength() <= bufsz) {
					multiSwap(array, ptrs[ptrs.length-1]-(i.end-i.pstart), i.pstart, i.end-i.pstart);
					int m = to-1, le = ptrs[ptrs.length-1]-1, ls = ptrs[ptrs.length-1]-(i.end-i.pstart), t=m+(le-ls)+1;
					while(m >= ptrs[0] && le >= ls) {
						if(Reads.compareValues(array[m], array[le]) > 0) {
							Writes.swap(array, m--, t--, 1, true, false);
						} else {
							to++;
							Writes.swap(array, le--, t--, 1, true, false);
						}
					}
					while(le >= ls) {
						to++;
						Writes.swap(array, le--, t--, 1, true, false);
					}
				} else {
					grailMergeWithoutBuffer(array, ptrs[0], to-ptrs[0], i.end-i.pstart);
				}
			}
		}
		buf = ptrs[ptrs.length-1]-bufsz;
	}
	
	private void caiMerge(int[] array, Collection<Integer> ptrs) {
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
		binserter = new BranchlessBinaryInsertionSort(arrayVisualizer);
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