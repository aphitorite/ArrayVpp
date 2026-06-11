package io.github.arrayv.sorts.root;

import io.github.arrayv.main.ArrayVisualizer;
import io.github.arrayv.sorts.insert.InsertionSort;
import io.github.arrayv.sorts.templates.Sort;

public final class CbrtSort extends Sort {
	private InsertionSort insertSorter;
	
	public CbrtSort(ArrayVisualizer arrayVisualizer) {
		super(arrayVisualizer);
		
		this.setSortListName("Cbrt");
		this.setRunAllSortsName("Cbrtsort");
		this.setRunSortName("Cbrtsort");
		this.setCategory("Hybrid Sorts");
		this.setBucketSort(false);
		this.setRadixSort(false);
		this.setUnreasonablySlow(false);
		this.setUnreasonableLimit(0);
		this.setBogoSort(false);
        this.setAuthors("Control");
	}
	
	private void sqrtSwap(int[] arr, int a, int b, boolean auxwrite) {
		Writes.swap(arr, a, b, 1.0D, true, auxwrite);
		Highlights.clearMark(2);
	}
	
	private void sqrtMultiSwap(int[] arr, int a, int b, int swapsLeft, boolean auxwrite) {
		while (swapsLeft != 0) {
			sqrtSwap(arr, a++, b++, auxwrite);
			swapsLeft--;
		} 
	}
	
	private void sqrtInsertSort(int[] arr, int pos, int len, boolean auxwrite) {
		this.insertSorter.customInsertSort(arr, pos, len, 0.25D, auxwrite);
	}
	
	private void sqrtMergeRight(int[] arr, int pos, int leftLen, int rightLen, int dist, boolean auxwrite) {
		int mergedPos = leftLen + rightLen + dist - 1;
		int right = leftLen + rightLen - 1;
		int left = leftLen - 1;

		if(pos < 0)
			pos = 0;
		while (left >= 0) {
			Highlights.markArray(2, pos + left);
			Highlights.markArray(3, pos + right);
			
			if (right < leftLen || Reads.compareValues(arr[pos + left], arr[pos + right]) > 0) {
				Writes.write(arr, pos + mergedPos--, arr[pos + left--], 1.0D, true, auxwrite); continue;
			} 
			Writes.write(arr, pos + mergedPos--, arr[pos + right--], 1.0D, true, auxwrite);
		} 
		
		Highlights.clearMark(3);
		
		if (right != mergedPos) {
			while (right >= leftLen) {
				Writes.write(arr, pos + mergedPos--, arr[pos + right--], 1.0D, true, auxwrite);
				Highlights.markArray(2, pos + right);
			} 
		}
		
		Highlights.clearMark(2);
	}


	
	private void sqrtMergeLeftWithXBuf(int[] arr, int pos, int leftEnd, int rightEnd, int dist, boolean auxwrite) {
		int left = 0;
		int right = leftEnd;
		rightEnd += leftEnd;
		
		if(pos < 0)
			pos = 0;
		
		while (right < rightEnd) {
			if (left == leftEnd || Reads.compareValues(arr[pos + left], arr[pos + right]) > 0) {
				Writes.write(arr, pos + dist++, arr[pos + right++], 1.0D, true, auxwrite);
			} else {
				Writes.write(arr, pos + dist++, arr[pos + left++], 1.0D, true, auxwrite);
			} 
			Highlights.markArray(2, pos + left);
			Highlights.markArray(3, pos + right);
		} 
		
		Highlights.clearMark(3);
		
		if (dist != left) {
			while (left < leftEnd) {
				Writes.write(arr, pos + dist++, arr[pos + left++], 1.0D, true, auxwrite);
				Highlights.markArray(2, pos + left);
			} 
		}
		
		Highlights.clearMark(2);
	}

	
	private void sqrtMergeDown(int[] arr, int arrPos, int[] buffer, int bufPos, int leftLen, int rightLen, boolean auxwrite) {
		int arrMerge = 0, bufMerge = 0;
		int dist = 0 - rightLen;
		if(arrPos < 0)
			arrPos = 0;
		while (bufMerge < rightLen) {
			if (arrMerge == leftLen || Reads.compareValues(arr[arrPos + arrMerge], buffer[bufPos + bufMerge]) >= 0) {
				Writes.write(arr, arrPos + dist++, buffer[bufPos + bufMerge++], 1.0D, true, auxwrite);
			} else {
				Writes.write(arr, arrPos + dist++, arr[arrPos + arrMerge++], 1.0D, true, auxwrite);
			} 
			Highlights.markArray(2, arrPos + arrMerge);
			Highlights.markArray(3, bufPos + bufMerge);
		} 
		
		Highlights.clearMark(3);
		
		if (dist != arrMerge) {
			while (arrMerge < leftLen) {
				Writes.write(arr, arrPos + dist++, arr[arrPos + arrMerge++], 1.0D, true, auxwrite);
				Highlights.markArray(2, arrPos + arrMerge);
			} 
		}
		
		Highlights.clearMark(2);
	}

	
	private SqrtState sqrtSmartMergeWithXBuf(int[] arr, int pos, int leftOverLen, int leftOverFrag, int blockLen, boolean auxwrite) {
		int length, dist = 0 - blockLen, left = 0, right = leftOverLen, leftEnd = right, rightEnd = right + blockLen;
		int typeFrag = 1 - leftOverFrag;
		
		while (left < leftEnd && right < rightEnd) {
			if (Reads.compareValues(arr[pos + left], arr[pos + right]) - typeFrag < 0) {
				Writes.write(arr, pos + dist++, arr[pos + left++], 1.0D, true, auxwrite);
			} else {
				Writes.write(arr, pos + dist++, arr[pos + right++], 1.0D, true, auxwrite);
			} 
			Highlights.markArray(2, pos + left);
			Highlights.markArray(3, pos + right);
		} 
		
		Highlights.clearMark(3);
		
		int fragment = leftOverFrag;
		
		if (left < leftEnd) {
			length = leftEnd - left;
			
			while (left < leftEnd) {
				Writes.write(arr, pos + --rightEnd, arr[pos + --leftEnd], 1.0D, true, auxwrite);
				Highlights.markArray(2, pos + leftEnd);
			} 
		} else {
			
			length = rightEnd - right;
			fragment = typeFrag;
		} 
		
		Highlights.clearMark(2);
		
		return new SqrtState(length, fragment);
	}

	
	private void sqrtMergeBuffersLeftWithXBuf(int[] keys, int midkey, int[] arr, int pos, int blockCount, int regBlockLen, int aBlockCount, int lastLen, boolean auxwrite) {
		if (blockCount == 0) {
			int aBlocksLen = aBlockCount * regBlockLen;
			sqrtMergeLeftWithXBuf(arr, pos, aBlocksLen, lastLen, 0 - regBlockLen, auxwrite);
			
			return;
		} 
		int leftOverLen = regBlockLen;
		int leftOverFrag = (Reads.compareOriginalValues(keys[0], midkey) < 0) ? 0 : 1;
		int processIndex = regBlockLen;


		
		for (int keyIndex = 1; keyIndex < blockCount; keyIndex++, processIndex += regBlockLen) {
			int i = processIndex - leftOverLen;
			int nextFrag = (Reads.compareOriginalValues(keys[keyIndex], midkey) < 0) ? 0 : 1;
			
			if (nextFrag == leftOverFrag) {
				Writes.arraycopy(arr, pos + i, arr, pos + i - regBlockLen, leftOverLen, 1.0D, true, auxwrite);
				
				i = processIndex;
				leftOverLen = regBlockLen;
			} else {
				
				SqrtState results = sqrtSmartMergeWithXBuf(arr, pos + i, leftOverLen, leftOverFrag, regBlockLen, auxwrite);
				
				leftOverLen = results.getLeftOverLen();
				leftOverFrag = results.getLeftOverFrag();
			} 
		} 
		
		int restToProcess = processIndex - leftOverLen;
		
		if (lastLen != 0) {
			if (leftOverFrag != 0) {
				Writes.arraycopy(arr, pos + restToProcess, arr, pos + restToProcess - regBlockLen, leftOverLen, 1.0D, true, auxwrite);
				
				restToProcess = processIndex;
				leftOverLen = regBlockLen * aBlockCount;
				leftOverFrag = 0;
			} else {
				
				leftOverLen += regBlockLen * aBlockCount;
			} 
			sqrtMergeLeftWithXBuf(arr, pos + restToProcess, leftOverLen, lastLen, 0 - regBlockLen, auxwrite);
		} else {
			
			Writes.arraycopy(arr, pos + restToProcess, arr, pos + restToProcess - regBlockLen, leftOverLen, 1.0D, true, auxwrite);
		} 
	}

	
	private void sqrtBuildBlocks(int[] arr, int pos, int len, int buildLen, boolean auxwrite) {
		for (int dist = 1; dist < len; dist += 2) {
			int extraDist = 0;
			if (Reads.compareValues(arr[pos + dist - 1], arr[pos + dist]) > 0) extraDist = 1;
			
			Writes.write(arr, pos + dist - 3, arr[pos + dist - 1 + extraDist], 1.0D, true, auxwrite);
			Writes.write(arr, pos + dist - 2, arr[pos + dist - extraDist], 1.0D, true, auxwrite);
		} 
		if (len % 2 != 0) Writes.write(arr, pos + len - 3, arr[pos + len - 1], 1.0D, true, auxwrite);
		
		pos -= 2;
		
		for (int part = 2; part < buildLen; part *= 2) {
			int left = 0;
			int right = len - 2 * part;
			
			while (left <= right) {
				sqrtMergeLeftWithXBuf(arr, pos + left, part, part, 0 - part, auxwrite);
				left += 2 * part;
			} 
			
			int rest = len - left;
			
			if (rest > part) {
				sqrtMergeLeftWithXBuf(arr, pos + left, part, rest - part, 0 - part, auxwrite);
			} else {
				
				for (; left < len; Writes.write(arr, pos + left - part, arr[pos + left++], 1.0D, true, auxwrite));
			} 
			
			pos -= part;
		} 
		int restToBuild = len % 2 * buildLen;
		int leftOverPos = len - restToBuild;
		
		if (restToBuild <= buildLen) {
			Writes.arraycopy(arr, pos + leftOverPos, arr, pos + leftOverPos + buildLen, restToBuild, 1.0D, true, auxwrite);
		} else {
			sqrtMergeRight(arr, pos + leftOverPos, buildLen, restToBuild - buildLen, buildLen, auxwrite);
		} 
		while (leftOverPos > 0) {
			leftOverPos -= 2 * buildLen;
			sqrtMergeRight(arr, pos + leftOverPos, buildLen, buildLen, buildLen, auxwrite);
		} 
	}


	
	private void sqrtCombineBlocks(int[] arr, int pos, int len, int buildLen, int regBlockLen, int[] tags, boolean auxwrite) {
		int combineLen = len / 2 * buildLen;
		int leftOver = len % 2 * buildLen;
		
		if (leftOver <= buildLen) {
			len -= leftOver;
			leftOver = 0;
		} 
		
		int leftIndex = 0;
		
		for (int i = 0; i <= combineLen && (
			i != combineLen || leftOver != 0); i++) {
			
			int blockPos = pos + i * 2 * buildLen;
			int blockCount = ((i == combineLen) ? leftOver : (2 * buildLen)) / regBlockLen;
			
			int tagIndex = blockCount + ((i == combineLen) ? 1 : 0);
			for (int j = 0; j <= tagIndex; ) { Writes.write(tags, j, j, 1.0D, true, true); j++; }
			
			int midkey = buildLen / regBlockLen;
			
			for (tagIndex = 1; tagIndex < blockCount; tagIndex++) {
				leftIndex = tagIndex - 1;
				
				for (int rightIndex = tagIndex; rightIndex < blockCount; rightIndex++) {
					int rightComp = Reads.compareValues(arr[blockPos + leftIndex * regBlockLen], arr[blockPos + rightIndex * regBlockLen]);
					if (rightComp > 0 || (rightComp == 0 && tags[leftIndex] > tags[rightIndex])) leftIndex = rightIndex;
				
				} 
				if (leftIndex != tagIndex - 1) {
					sqrtMultiSwap(arr, blockPos + (tagIndex - 1) * regBlockLen, blockPos + leftIndex * regBlockLen, regBlockLen, auxwrite);
					sqrtSwap(tags, tagIndex - 1, leftIndex, true);
				} 
			} 
			int aBlockCount = 0;
			int lastLen = 0;
			
			if (i == combineLen) lastLen = leftOver % regBlockLen;
			
			if (lastLen != 0) {
				while (aBlockCount < blockCount && Reads.compareValues(arr[blockPos + blockCount * regBlockLen], 
						arr[blockPos + (blockCount - aBlockCount - 1) * regBlockLen]) < 0) {
					aBlockCount++;
				}
			}
			sqrtMergeBuffersLeftWithXBuf(tags, midkey, arr, blockPos, blockCount - aBlockCount, regBlockLen, aBlockCount, lastLen, auxwrite);
		} 
		for (leftIndex = len - 1; leftIndex >= 0; ) { Writes.write(arr, pos + leftIndex, arr[pos + leftIndex - regBlockLen], 1.0D, true, auxwrite); leftIndex--; }
	
	}
	public void sqrtCommonSort(int[] arr, int pos, int len, int[] extBuf, int extBufPos, int[] tags, boolean auxwrite) {
		this.insertSorter = new InsertionSort(this.arrayVisualizer);
		
		if (len <= 16) {
			sqrtInsertSort(arr, pos, len, auxwrite);
			Highlights.clearAllMarks();
			
			return;
		} 
		int blockLen = 1;
		while (blockLen * blockLen * blockLen < len) {
			blockLen *= 2;
		}

		
		Writes.arraycopy(arr, pos, extBuf, extBufPos, blockLen, 1.0D, true, auxwrite);
		
		sqrtCommonSort(extBuf, extBufPos, blockLen, arr, pos, tags, !auxwrite);
		
		sqrtBuildBlocks(arr, pos + blockLen, len - blockLen, blockLen, auxwrite);
		
		int buildLen = blockLen;
		
		while (len > (buildLen *= 2)) {
			sqrtCombineBlocks(arr, pos + blockLen, len - blockLen, buildLen, blockLen, tags, auxwrite);
		}
		sqrtMergeDown(arr, pos + blockLen, extBuf, extBufPos, len - blockLen, blockLen, auxwrite);
		
		Highlights.clearAllMarks();
	}

	
	public void runSort(int[] array, int len, int bucketCount) {
		int bufferLen = 1;
		
		for (; bufferLen * bufferLen * bufferLen < len; bufferLen *= 2);
		int numKeys = (len - 1) / bufferLen + 2;
		
		int[] extBuf = Writes.createExternalArray(bufferLen);
		int[] tags = Writes.createExternalArray(numKeys);
		
		sqrtCommonSort(array, 0, len, extBuf, 0, tags, false);
		
		Writes.deleteExternalArray(extBuf);
		Writes.deleteExternalArray(tags);
	}
}