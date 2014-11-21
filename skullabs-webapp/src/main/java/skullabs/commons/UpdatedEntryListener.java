package skullabs.commons;

import java.util.function.BiConsumer;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

import com.hazelcast.core.EntryEvent;
import com.hazelcast.core.EntryListener;
import com.hazelcast.core.IMap;

@RequiredArgsConstructor
public class UpdatedEntryListener<K, T> implements EntryListener<K, T> {

	final IMap<K, T> imap;
	final BiConsumer<K, T> updateListener;
	volatile String entryListenerId;

	@Override
	public void entryUpdated( final EntryEvent<K, T> event ) {
		try {
			updateListener.accept( event.getKey(), event.getValue() );
		} finally {
			if ( !imap.removeEntryListener( entryListenerId ) )
				throw new IllegalStateException( "Could not unregister listener" );
		}
	}

	@Override
	public void entryAdded( final EntryEvent<K, T> event ) {}

	@Override
	public void entryRemoved( final EntryEvent<K, T> event ) {}

	@Override
	public void entryEvicted( final EntryEvent<K, T> event ) {}

	public static <K, T> UpdatedEntryListenerBuilder<K, T> listenOnce( final IMap<K, T> imap ) {
		return new UpdatedEntryListenerBuilder<>( imap );
	}

	@Getter
	@Setter
	@Accessors( fluent = true )
	@RequiredArgsConstructor
	public static class UpdatedEntryListenerBuilder<K, T> {

		final IMap<K, T> imap;
		K forKey;
		boolean includeValue;

		public UpdatedEntryListener<K, T> then( final BiConsumer<K, T> callback ) {
			final UpdatedEntryListener<K, T> listener = new UpdatedEntryListener<>( imap, callback );
			final String entryListenerId = imap.addEntryListener( listener, forKey, includeValue );
			listener.entryListenerId = entryListenerId;
			return listener;
		}
	}
}
