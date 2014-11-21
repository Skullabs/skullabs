package skullabs.processor;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import kikaha.core.api.conf.Configuration;
import lombok.Cleanup;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.val;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.util.ImageIOUtil;

import skullabs.presentation.PDFProcessJob;
import skullabs.presentation.PresentationRepository;
import trip.spi.Provided;
import trip.spi.Singleton;
import uworkers.api.Worker;

import com.typesafe.config.Config;

@Singleton
public class PDFImageConverter {

	@Getter( lazy = true )
	private final String outputdir = readOutputImageDirFromConfiguration();

	@Provided
	Configuration kikahaConfig;

	@Provided
	PresentationRepository repository;

	@Provided
	Config config;

	String readOutputImageDirFromConfiguration() {
		String outputDir = config.getString( "skul.images-dir" );
		val resourcesPath = kikahaConfig.resourcesPath();
		if ( resourcesPath != null && !resourcesPath.isEmpty() )
			outputDir = resourcesPath + "/" + outputDir;
		return outputDir;
	}

	/**
	 * Convert a {@link PDFProcessJob} pages into PNG images.
	 *
	 * @param pdf
	 * @throws IOException
	 */
	@Worker( name = "pdf-pages-processor" )
	public void convert( final PDFProcessJob pdf ) throws IOException {
		val file = new File( pdf.getFileName() );
		convert( pdf.getIdentifier(), file );
	}

	/**
	 * Convert a PDF pages into PNG images.
	 *
	 * @param identifier
	 * @param pdfFile
	 * @throws IOException
	 */
	@SuppressWarnings( { "unchecked", "rawtypes" } )
	void convert( final long identifier, final File pdfFile ) throws IOException
	{
		@Cleanup val pdf = PDDocument.loadNonSeq( pdfFile, null );
		val allPages = pdf.getDocumentCatalog().getAllPages();
		val converter = new ImageConverter( identifier, allPages );
		converter.convert();
	}

	@RequiredArgsConstructor
	class ImageConverter {

		final long identifier;
		final List<PDPage> allPages;

		void convert() throws FileNotFoundException, IOException {
			int pageNumber = 0;
			try {
				for ( val page : allPages )
					convertToImage( page, pageNumber++ );
			} finally {
				repository.finishProcessingPDF( identifier, pageNumber );
			}
		}

		void convertToImage( final PDPage page, final int pageNumber )
			throws IOException, FileNotFoundException
		{
			val pageOutputDir = getOutputdir() + "/" + identifier + "/";
			ensureThatOutputDirExists( pageOutputDir );
			val pageImageFileName = pageOutputDir + pageNumber + ".png";
			val image = page.convertToImage();
			writeImage( pageImageFileName, image );
		}

		void writeImage( final String pageImageFileName, final BufferedImage image ) throws FileNotFoundException, IOException {
			@Cleanup val output = new FileOutputStream( pageImageFileName );
			ImageIOUtil.writeImage( image, "png", output );
		}

		void ensureThatOutputDirExists( final String outputDir ) {
			val file = new File( outputDir );
			if ( !file.exists() )
				if ( !file.mkdirs() )
					throw new RuntimeException( "Could not create dir " + outputDir );
		}
	}
}