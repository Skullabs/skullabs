package skullabs.pdf;

import java.io.Serializable;

import lombok.Data;
import lombok.NonNull;

@Data
public class Presentation implements Serializable {

	private static final long serialVersionUID = 467441129274697607L;

	final Long identifier;

	@NonNull
	String title;

	@NonNull
	Boolean processing;

	public Presentation( final String title ) {
		this.identifier = System.nanoTime();
		this.processing = true;
		this.title = title;
	}
}