package skullabs.kernel.template;

import java.util.ArrayList;

import kikaha.urouting.api.Header;
import kikaha.urouting.api.Mimes;
import kikaha.urouting.api.Response;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.experimental.Accessors;

@Getter
@Accessors( fluent = true )
@NoArgsConstructor( staticName = "ok" )
public class MustacheResponse implements Response {

	final MustacheTemplate entity = new MustacheTemplate();
	final String contentType = Mimes.HTML;
	final String encoding = "UTF-8";
	final Iterable<Header> headers = new ArrayList<>();

	@NonNull
	Integer statusCode = 200;

	public MustacheResponse paramObject( final Object entity ) {
		this.entity.paramObject( entity );
		return this;
	}

	public MustacheResponse templateName( final String templateName ) {
		this.entity.templateName( templateName );
		return this;
	}
}