package skullabs.presentation;

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
import skullabs.kernel.template.MustacheResponse;
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
		@FormParam( "title" ) final String title,
		@FormParam( "description" ) final String description,
		@FormParam( "showVideo" ) final Boolean showVideo,
		@FormParam( "showSlides" ) final Boolean showSlides ) throws IOException
	{
		val presentation = persistPresentation( file, title, description, showVideo, showSlides );
		return DefaultResponse.seeOther(
			"/presentation/edit/" + presentation.getIdentifier() );
	}

	Presentation persistPresentation( final File file, final String title, final String description,
		final Boolean showVideo, final Boolean showSlides ) throws IOException {
		val filePathAtUploadDir = copyToUploadDir( file );
		val presentation = new Presentation( title, description );
		presentation.setShowVideo( showVideo );
		presentation.setShowSlides( showSlides );
		pdfRepository.create( presentation, filePathAtUploadDir );
		return presentation;
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

	@Path( "edit/{id}" )
	@MultiPartFormData
	public Response handleEditPresentationUpload(
		@PathParam( "id" ) final Long pdfIdentifier,
		@FormParam( "filename" ) final File file,
		@FormParam( "title" ) final String title,
		@FormParam( "description" ) final String description,
		@FormParam( "showVideo" ) final Boolean showVideo,
		@FormParam( "showSlides" ) final Boolean showSlides,
		@FormParam( "numberOfSlides" ) final Integer numberOfSlides ) throws IOException
	{
		persistPresentation( pdfIdentifier, file, title, description, showVideo, showSlides, numberOfSlides );
		return DefaultResponse.seeOther(
			"/presentation/edit/" + pdfIdentifier );
	}

	private void persistPresentation( final Long pdfIdentifier, final File file, final String title, final String description,
		final Boolean showVideo, final Boolean showSlides, final Integer numberOfSlides ) throws IOException {
		val filePathAtUploadDir = copyToUploadDir( file );
		val presentation = new Presentation( pdfIdentifier, title, description );
		presentation.setShowVideo( showVideo );
		presentation.setShowSlides( showSlides );
		presentation.setNumberOfSlides( numberOfSlides );
		pdfRepository.create( presentation, filePathAtUploadDir );
	}

	String copyToUploadDir( final File file ) throws IOException {
		if ( file.length() == 0 )
			return null;
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
}