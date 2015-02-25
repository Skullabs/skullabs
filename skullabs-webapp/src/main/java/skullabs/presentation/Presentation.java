package skullabs.presentation;

import java.io.Serializable;

import lombok.Data;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class Presentation implements Serializable {

	private static final long serialVersionUID = 467441129274697607L;

	final Long identifier;
	@NonNull String title;
	@NonNull String description;

	Boolean showVideo;
	Boolean showSlides;
	boolean processing;
	Integer numberOfSlides = 0;

	public Presentation( final String title, final String description ) {
		this.identifier = System.nanoTime();
		this.processing = true;
		this.title = title;
		this.description = description;
	}

	public void setShowVideo( final Boolean showVideo ) {
		if ( showVideo != null )
			this.showVideo = showVideo;
	}

	public void setShowSlides( final Boolean showSlides ) {
		if ( showSlides != null )
			this.showSlides = showSlides;
	}

	public String getShowVideo() {
		return asCheckboxString( showVideo );
	}

	public String getShowSlides() {
		return asCheckboxString( showSlides );
	}

	String asCheckboxString( final Boolean value ) {
		return value != null && value ? "checked" : "";
	}

	public static Presentation empty() {
		return new Presentation( 0l, "", "" );
	}
}