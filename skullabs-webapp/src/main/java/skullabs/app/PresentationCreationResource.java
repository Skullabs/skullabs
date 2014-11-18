package skullabs.app;

import java.io.File;

import kikaha.urouting.api.DefaultResponse;
import kikaha.urouting.api.FormParam;
import kikaha.urouting.api.GET;
import kikaha.urouting.api.MultiPartFormData;
import kikaha.urouting.api.Path;
import kikaha.urouting.api.PathParam;
import kikaha.urouting.api.Response;
import skullabs.kernel.template.MustacheResponse;
import skullabs.pdf.Presentation;
import skullabs.pdf.PresentationRepository;
import trip.spi.Provided;
import trip.spi.Singleton;

@Singleton
@Path( "presentation" )
public class PresentationCreationResource {

	@Provided
	PresentationRepository pdfRepository;

	@GET
	@Path( "new" )
	public Response showNewPresentationUploadScreen() {
		return MustacheResponse.ok()
			.templateName( "presentation/upload-form.mustache" );
	}

	@Path("new")
	@MultiPartFormData
	public Response handleNewPresentationUpload(
		@FormParam( "filename" ) final File file,
		@FormParam( "title" ) final String title )
	{
		final Presentation presentation = new Presentation( title );
		pdfRepository.create( presentation, file.getPath() );
		return DefaultResponse.seeOther(
			"/presentation/edit/" + presentation.getIdentifier() );
	}

	@GET
	@Path( "edit/{id}" )
	public Response editPresentation(
		@PathParam( "id" ) final Long pdfIdentifier ) {
		final Presentation entity = pdfRepository.retrieve( pdfIdentifier );
		return MustacheResponse.ok()
			.templateName( "presentation/edit-form.mustache" )
			.paramObject( entity );
	}
}