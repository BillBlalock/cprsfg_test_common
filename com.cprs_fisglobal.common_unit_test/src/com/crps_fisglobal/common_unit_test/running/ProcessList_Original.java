package com.crps_fisglobal.common_unit_test.running;

/**
 * Example based on
 * http://java.sun.com/developer/technicalArticles/Collections/Using/index.html
 */
import java.io.Serializable;
import java.util.AbstractList;
import java.util.Collection;
import java.util.Iterator;

import com.crps_fisglobal.common.running.ProcessObject;

public class ProcessList_Original extends AbstractList<ProcessObject> implements Cloneable, Serializable {

	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	// highest index for each slot
	private int sublist[];

	// element value for each slot
	private ProcessObject objlist[];

	// highest current valid slot number
	private int currmax;

	// find the lowest valid index for a slot
	private int slotlo(int index) {
		return (index == 0 ? 0 : sublist[index - 1] + 1);
	}

	// find the highest valid index for a slot
	private int slothi(int index) {
		return sublist[index];
	}

	// check whether two objects are equivalent
	// (handles null)
	private static boolean equals(Object obj1, Object obj2) {
		return (obj1 == null ? (obj2 == null) : obj1.equals(obj2));
	}

	// make room for a new slot, and push up others
	private void makeslot(int slot, int n) {
		if (currmax + n >= sublist.length)
			growlist();

		if (slot < currmax) {
			System.arraycopy(sublist, slot + 1, sublist, slot + 1 + n, currmax
					- slot);
			System.arraycopy(objlist, slot + 1, objlist, slot + 1 + n, currmax
					- slot);
		}

		currmax += n;
	}

	// grow the subscript and object lists
	private void growlist() {
		int len = sublist.length * 2;

		int newsub[] = new int[len];
		System.arraycopy(sublist, 0, newsub, 0, sublist.length);
		sublist = newsub;

		ProcessObject newobj[] = new ProcessObject[len];
		System.arraycopy(objlist, 0, newobj, 0, objlist.length);
		objlist = newobj;
	}

	// find the slot corresponding to an index
	private int findslot(int index) {
		int lo = 0;
		int hi = currmax;

		// binary search

		while (lo <= hi) {
			int mid = (lo + hi) / 2;
			if (index < slotlo(mid))
				hi = mid - 1;
			else if (index > slothi(mid))
				lo = mid + 1;
			else
				return mid;
		}

		// should never get here

		throw new Error();
	}

	// default constructor
	public ProcessList_Original() {
		sublist = new int[10];
		objlist = new ProcessObject[10];
		currmax = -1;
	}

	// constructor from a Collection
	public ProcessList_Original(Collection<ProcessObject> c) {
		this();
		Iterator<ProcessObject> iter = c.iterator();
		while (iter.hasNext())
			add(iter.next());
	}

	// number of elements currently in the list
	public int size() {
		return currmax == -1 ? 0 : sublist[currmax] + 1;
	}

	// get an element value based on an index
	public ProcessObject get(int index) {
		if (index < 0 || index >= size())
			throw new IndexOutOfBoundsException();

		return objlist[findslot(index)];
	}

	// set an element to a new value
	public ProcessObject set(int index, ProcessObject element) {
		// remove then add

		ProcessObject obj = remove(index);
		add(index, element);
		return obj;
	}

	// add a new element, pushing up other elements
	public void add(int index, ProcessObject element) {
		int sz = size();
		if (index < 0 || index > sz)
			throw new IndexOutOfBoundsException();

		// adding to the end of the list?

		if (index == sz) {
			if (sz > 0 && equals(objlist[currmax], element)) {

				// same as current last element

				sublist[currmax]++;
			} else {

				// not same, append to end

				if (currmax + 1 == sublist.length)
					growlist();
				currmax++;
				sublist[currmax] = sz;
				objlist[currmax] = element;
			}
		} else {
			int slot = findslot(index);
			int startincr = slot;
			if (!equals(objlist[slot], element)) {
				if (index == slotlo(slot)) {

					// push current slot up

					makeslot(slot, 1);
					sublist[slot + 1] = sublist[slot];
					sublist[slot] = index;
					objlist[slot + 1] = objlist[slot];
					objlist[slot] = element;
					startincr++;
				} else {

					// split current slot

					makeslot(slot, 2);
					sublist[slot + 2] = sublist[slot];
					sublist[slot + 1] = index;
					sublist[slot] = index - 1;
					objlist[slot + 2] = objlist[slot];
					objlist[slot + 1] = element;
					startincr += 2;
				}
			}

			// bump up max indices

			for (int i = startincr; i <= currmax; i++)
				sublist[i]++;
		}
	}

	// remove an element
	public ProcessObject remove(int index) {
		if (index < 0 || index >= size())
			throw new IndexOutOfBoundsException();

		int slot = findslot(index);
		ProcessObject obj = objlist[slot];

		// if this index is the only one in the slot,
		// delete the slot and shift down

		if (slotlo(slot) == slothi(slot)) {
			if (slot < currmax) {
				System.arraycopy(sublist, slot + 1, sublist, slot, currmax
						- slot);
				System.arraycopy(objlist, slot + 1, objlist, slot, currmax
						- slot);
			}
			objlist[currmax--] = null;
		}

		// decrement indices

		for (int i = slot; i <= currmax; i++)
			sublist[i]--;

		return obj;
	}
}
