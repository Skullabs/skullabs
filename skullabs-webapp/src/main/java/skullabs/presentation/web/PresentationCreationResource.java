package skullabs.presentation.web;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import kikaha.urouting.api.DefaultResponse;
import kikaha.urouting.api.FormParam;
import kikaha.urouting.api.GET;
import kikaha.urouting.api.MultiPartFormData;
import kikaha.urouting.api.Path;
import kikaha.urouting.api.PathParam;
import kikaha.urouting.api.Response;
import lombok.val;
import skullabs.presentation.Presentation;
import skullabs.presentation.PresentationRepository;
import skullabs.web.kernel.template.MustacheResponse;
import trip.spi.Provided;
import trip.spi.Singleton;

import com.typesafe.config.Config;

@Singleton
@Path( "presentation" )
public class PresentationCreationResource {

	@Provided
	PresentationRepository pdfRepository;

	@Provided
	Config config;

	@GET
	@Path( "new" )
	public Response showNewPresentationUploadScreen() {
		return MustacheResponse.ok()
			.templateName( "presentation/upload-form.mustache" )
			.paramObject( Presentation.empty() );
	}

	@Path("new")
	@MultiPartFormData
	public Response handleNewPresentationUpload(
		@FormParam( "filename" ) final File file,
		@FormParam( "title" ) final String title ) throws IOException
	{
		val filePathAtUploadDir = copyToUploadDir( file );
		val presentation = new Presentation( title );
		pdfRepository.create( presentation, filePathAtUploadDir );
		return DefaultResponse.seeOther(
			"/presentation/edit/" + presentation.getIdentifier() );
	}

	String copyToUploadDir( final File file ) throws IOException {
		val path = file.toPath();
		val newFilePath = getUploadDir() + "/" + System.nanoTime();
		Files.copy( path, Paths.get( newFilePath ) );
		return newFilePath;
	}

	String getUploadDir() throws IOException {
		val uploadDir = config.getString( "skul.upload-dir" );
		val dir = new File( uploadDir );
		if ( !dir.exists() )
			if ( !dir.mkdirs() )
				throw new IOException( "Could not create " + uploadDir );
		return uploadDir;
	}

	@GET
	@Path( "edit/{id}" )
	public Response editPresentation(
		@PathParam( "id" ) final Long pdfIdentifier ) {
		val entity = pdfRepository.retrieve( pdfIdentifier );
		return MustacheResponse.ok()
			.templateName( "presentation/upload-form.mustache" )
			.paramObject( entity );
	}
}