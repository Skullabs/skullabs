package skullabs.kernel.template;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import kikaha.core.api.conf.Configuration;
import kikaha.urouting.api.RoutingException;
import lombok.Getter;
import lombok.experimental.Accessors;
import skullabs.kernel.InternalErrorException;
import trip.spi.Provided;
import trip.spi.Singleton;

import com.samskivert.mustache.Mustache;
import com.samskivert.mustache.Mustache.Compiler;
import com.samskivert.mustache.MustacheException;
import com.samskivert.mustache.Template;

@Singleton
@Accessors( fluent = true )
public class MustacheSerializer {

	final Compiler compiler = createMustacheCompiler();
	final Map<String, Template> cachedTemplates = new HashMap<>();

	@Getter( lazy = true )
	private final boolean shouldCacheTemplates = configuration.config().getBoolean( "mustache.cache-templates" );

	@Getter( lazy = true )
	private final Function<String, Template> templateCreator = createTemplateCreator();

	@Provided
	Configuration configuration;

	Compiler createMustacheCompiler() {
		return Mustache
			.compiler()
			.emptyStringIsFalse( true )
			.nullValue( "" )
			.withLoader(
				name -> {
					final File file = new File( formatFileName( name ) );
					return new FileReader( file );
				} );
	}

	public <T> void serialize( final T object, final OutputStream output )
		throws RoutingException {
		if ( object == null || !MustacheTemplate.class.isInstance( object ) )
			throw new InternalErrorException( "Could not render " + object + ". Use a MustacheTemplate as parameter." );
		serialize( (MustacheTemplate)object, output );
	}

	public void serialize( final MustacheTemplate object, final OutputStream output ) throws RoutingException {
		try {
			final Writer writer = new OutputStreamWriter( output );
			serialize( object, writer );
			writer.flush();
		} catch ( final IOException e ) {
			throw new RoutingException( e );
		}
	}

	public void serialize( final MustacheTemplate object, final Writer writer ) {
		try {
			final String templateName = object.templateName();
			final Template compiled = templateCreator().apply( templateName );
			compiled.execute( object.paramObject(), writer );
		} catch ( final MustacheException.Context cause ) {
			System.out.println( "Could not compile " + object.templateName() );
			throw cause;
		}
	}

	Template getCachedTemplate( final String templateName ) {
		Template compiled = cachedTemplates.get( templateName );
		if ( compiled == null )
			synchronized ( cachedTemplates ) {
				compiled = cachedTemplates.get( templateName );
				if ( compiled == null )
					cachedTemplates.put( templateName,
						compiled = compileTemplate( templateName ) );
			}
		return compiled;
	}

	Template compileTemplate( final String originalTemplateName ) {
		final String templateName = formatFileName( originalTemplateName );
		try {
			final FileReader source = new FileReader( templateName );
			final Template compiled = compiler.compile( source );
			source.close();
			return compiled;
		} catch ( final IOException cause ) {
			throw new RuntimeException( cause );
		}
	}

	String formatFileName( final String originalTemplateName ) {
		final String prefix = originalTemplateName.charAt( 0 ) == '/'
			? "" : configuration.resourcesPath();
		final String sufix = originalTemplateName.contains( "." )
			? "" : ".mustache";
		final String templateName = prefix + "/" + originalTemplateName + sufix;
		return templateName;
	}

	Function<String, Template> createTemplateCreator() {
		if ( shouldCacheTemplates() )
			return this::getCachedTemplate;
		return this::compileTemplate;
	}
}