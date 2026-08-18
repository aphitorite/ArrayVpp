package io.github.arrayv.sorts.distribute;

import java.util.function.Function;

import io.github.arrayv.main.ArrayVisualizer;
import io.github.arrayv.sorts.quick.PDQBranchedSort;
import io.github.arrayv.sorts.templates.Sort;

// Copyright Malte Skarupke 2016.
// Distributed under the Boost Software License, Version 1.0.
// (See http://www.boost.org/LICENSE_1_0.txt)

final class PartitionInfo {
	private int count;
	private int offset;
	private int next_offset;
	
	public PartitionInfo() {
		this.setCount(0);
	}

	public int getCount() {
		return this.count;
	}

	public void setCount(int count) {
		this.count = count;
	}
	
	public void incrementCount() {
		++this.count;
	}

	public int getOffset() {
		return this.offset;
	}

	public void incrementOffset() {
		this.offset++;
	}

	public void setOffset(int offset) {
		this.offset = offset;
	}

	public int getNextOffset() {
		return this.next_offset;
	}

	public void setNextOffset(int next_offset) {
		this.next_offset = next_offset;
	}
}

public class SkaSort extends Sort {
	final private static int StdSortThreshold = 128;
	final private static int AmericanFlagSortThreshold = 1024;
	
	public SkaSort(ArrayVisualizer arrayVisualizer) {
		super(arrayVisualizer);
		
		this.setSortListName("Ska");
		this.setRunAllSortsName("Ska Sort");
		this.setRunSortName("Skasort");
		this.setCategory("Distribution Sorts");
	    this.setAuthors("Malte Skarupke");
		this.setBucketSort(false); // fixed bucket count
		this.setRadixSort(false);
		this.setUnreasonablySlow(false);
		this.setUnreasonableLimit(0);
		this.setBogoSort(false);
	}
	/*
	 * Made from a half-complete port of Skasort that was carried through ArrayV
	 * for a majority of its life.
	 */
	
	// key extraction for signed int
	private int extract_key(int val) {
		return val ^ 0x80000000;
	}
	private int current_byte(int val, int nbytes, int offset) {
		return (val << (8 * offset)) >>> (8 * (nbytes - 1));
	}
	
	private int custom_std_partition(int[] partvals, int begin, int end, Function<Integer, Boolean> checker) {
		for (;; ++begin)
		{
			if (begin == end)
				return end;
			if (!checker.apply(partvals[begin]))
				break;
		}
		for(int i = begin + 1; i != end; ++i)
		{
			if (!checker.apply(partvals[i]))
				continue;

			Writes.swap(partvals, begin++, i, 1, true, false);
		}
		return begin;
	}
	
	private void ska_byte_sort(int[] array, int begin, int end, int bytes, int b_offset) {
		Writes.changeAllocAmount(256);
		PartitionInfo partitions[] = new PartitionInfo[256];
		for (int it = 0; it < 256; ++it) {
			partitions[it] = new PartitionInfo();
		}
		for (int it = begin; it != end; ++it) {
			partitions[current_byte(extract_key(array[it]), bytes, b_offset)].incrementCount();
		}
		int remaining_partitions[] = new int[256];
		int total = 0;
		int num_partitions = 0;
		for (int i = 0; i < 256; ++i) {
			int count = partitions[i].getCount();
			if (count != 0) {
				partitions[i].setOffset(total);
				total += count;
				remaining_partitions[num_partitions] = i;
				++num_partitions;
			}
			partitions[i].setNextOffset(total);
		}
		for (int last_remaining = num_partitions; last_remaining > 1;) {
			last_remaining = custom_std_partition(remaining_partitions, 0, last_remaining, (partition) -> {
				int begin_offset = partitions[partition].getOffset();
				int end_offset = partitions[partition].getNextOffset();
				if (begin_offset == end_offset)
					return false;

				for (int ULFT = begin + begin_offset; ULFT < begin + end_offset; ++ULFT) {
					int this_partition = current_byte(extract_key(array[ULFT]), bytes, b_offset);
					int this_offset = partitions[this_partition].getOffset();
					partitions[this_partition].incrementOffset();
					Writes.swap(array, ULFT, begin + this_offset, 1, true, false);
				}
				// [using a reference to the offset, might have changed]
				return partitions[partition].getOffset() != end_offset;
			});
		}
		if (b_offset + 1 != bytes) {
			for (int it = num_partitions; it != 0; --it) {
				int partition = remaining_partitions[it - 1];
				int start_offset = (partition == 0 ? 0 : partitions[partition - 1].getNextOffset());
				int end_offset = partitions[partition].getNextOffset();
				int partition_begin = begin + start_offset;
				int partition_end = begin + end_offset;
				if (!StdSortIfLessThanThreshold(array, partition_begin, partition_end)) {
					this.sort(array, partition_begin, partition_end, bytes, b_offset + 1);
				}
			}
		}
		Writes.changeAllocAmount(-256);
	}
	
	private boolean StdSortIfLessThanThreshold(int[] array, int begin, int end) {
		if (end - begin >= StdSortThreshold)
			return false;
		if (end - begin > 1) {
			PDQBranchedSort pdqSort = new PDQBranchedSort(arrayVisualizer);
			pdqSort.customSort(array, begin, end);
		}
		return true;
	}
	
	private void sort(int[] array, int begin, int end, int bytes, int offset) {
		if (end - begin < AmericanFlagSortThreshold) {
			AmericanFlagSort flagSort = new AmericanFlagSort(arrayVisualizer);
			flagSort.customSort(array, begin, end, 128);
		} else {
			this.ska_byte_sort(array, begin, end, bytes, offset);
		}
	}
	
	private void ska_sort(int[] array, int begin, int end) {
		// [used to call the wrapper "in_place_radix_sort", trimmed for redundancy]
		this.sort(array, begin, end, 4, 0);
	}
	
	@Override
	public void runSort(int[] array, int sortLength, int bucketCount) throws Exception {
		this.ska_sort(array, 0, sortLength);
	}
}