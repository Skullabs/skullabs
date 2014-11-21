package skullabs.commons;

import java.util.Map.Entry;
import java.util.function.Consumer;

import lombok.RequiredArgsConstructor;

import com.hazelcast.map.AbstractEntryProcessor;

@RequiredArgsConstructor
public class Executor<T> extends AbstractEntryProcessor<Long, T> {

	private static final long serialVersionUID = -3022357615527188870L;

	final Consumer<T> wrappedConsumer;

	@Override
	public Object process( final Entry<Long, T> entry ) {
		final T t = entry.getValue();
		wrappedConsumer.accept( t );
		entry.setValue( t );
		return null;
	}
}
