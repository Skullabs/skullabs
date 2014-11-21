package skullabs.commons;

import java.util.Iterator;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class IntegerGenerator implements Iterable<Integer> {

	final int max;

	@Override
	public Iterator<Integer> iterator() {
		return new IntegerIterator();
	}

	class IntegerIterator implements Iterator<Integer> {

		volatile int cursor = 0;

		@Override
		public boolean hasNext() {
			return cursor < max;
		}

		@Override
		public Integer next() {
			return cursor++;
		}
	}
}
