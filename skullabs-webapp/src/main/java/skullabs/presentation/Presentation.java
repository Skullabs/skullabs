package skullabs.presentation;

import java.io.Serializable;

import lombok.Data;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import skullabs.commons.IntegerGenerator;

@Data
@RequiredArgsConstructor
public class Presentation implements Serializable {

	private static final long serialVersionUID = 467441129274697607L;

	@Getter( lazy = true )
	private final transient IntegerGenerator slides =
		new IntegerGenerator( numberOfSlides );

	final Long identifier;
	@NonNull String title;

	boolean processing;
	int numberOfSlides;

	public Presentation( final String title ) {
		this.identifier = System.nanoTime();
		this.processing = true;
		this.title = title;
	}

	public static Presentation empty() {
		return new Presentation( 0l, "" );
	}
}