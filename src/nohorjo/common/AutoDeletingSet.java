package nohorjo.common;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

/**
 * A {@link Set} that automatically removes items after the specified lifetime
 * in milliseconds
 * 
 * @author muhammed
 *
 * @param <E>
 *            type off object the {@link HashSet} should store
 */
public class AutoDeletingSet<E> implements Set<E> {

	long life;
	Set<E> set;

	/**
	 * Constructs this object with a new {@link HashSet}
	 * 
	 * @param life
	 *            lifetime of each object in milliseconds
	 */
	public AutoDeletingSet(long life) {
		this(life, new HashSet<E>());
	}

	/**
	 * Constructs this object
	 * 
	 * @param life
	 *            lifetime of each object in milliseconds
	 * @param set
	 *            initial {@link Set}
	 */
	public AutoDeletingSet(long life, Set<E> set) {
		this.life = life;
		this.set = set;
		for (final E e : set) {
			new Timer().schedule(new TimerTask() {

				@Override
				public void run() {
					remove(e);
				}
			}, life);
		}
	}

	@Override
	public int size() {
		return set.size();
	}

	@Override
	public boolean isEmpty() {
		return set.isEmpty();
	}

	@Override
	public boolean contains(Object o) {
		return set.contains(o);
	}

	@Override
	public Iterator<E> iterator() {
		return set.iterator();
	}

	@Override
	public Object[] toArray() {
		return set.toArray();
	}

	@Override
	public <T> T[] toArray(T[] a) {
		return set.toArray(a);
	}

	@Override
	public boolean add(final E e) {
		boolean added = set.add(e);
		new Timer().schedule(new TimerTask() {

			@Override
			public void run() {
				remove(e);
			}
		}, life);
		return added;
	}

	@Override
	public boolean remove(Object o) {
		return set.remove(o);
	}

	@Override
	public boolean containsAll(Collection<?> c) {
		return set.containsAll(c);
	}

	@Override
	public boolean addAll(final Collection<? extends E> c) {
		boolean added = set.addAll(c);
		new Timer().schedule(new TimerTask() {

			@Override
			public void run() {
				removeAll(c);
			}
		}, life);
		return added;
	}

	@Override
	public boolean retainAll(Collection<?> c) {
		return set.retainAll(c);
	}

	@Override
	public boolean removeAll(Collection<?> c) {
		return set.removeAll(c);
	}

	@Override
	public void clear() {
		set.clear();
	}

}
