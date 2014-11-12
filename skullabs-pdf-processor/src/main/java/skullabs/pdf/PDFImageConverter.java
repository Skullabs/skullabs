package skullabs.pdf;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import lombok.Cleanup;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.val;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.util.ImageIOUtil;

import trip.spi.Provided;
import trip.spi.Singleton;
import uworkers.api.Worker;

import com.typesafe.config.Config;

@Singleton
public class PDFImageConverter {

	@Getter( lazy = true )
	private final String outputdir = config.getString( "skul.images-dir" );

	@Provided
	PDFImageRepository repository;

	@Provided
	Config config;

	/**
	 * Convert a {@link PDF} pages into PNG images.
	 *
	 * @param pdf
	 * @throws IOException
	 */
	@Worker( name = "presentation-images" )
	public void convert( final PDF pdf ) throws IOException {
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
	public void convert( final long identifier, final File pdfFile ) throws IOException
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
			try {
				int pageNumber = 0;
				repository.startProcessingPDF( identifier );
				for ( val page : allPages ) {
					val imageName = convertToImage( page, identifier, pageNumber++ );
					repository.storeImageForPDF( identifier, imageName );
				}
			} finally {
				repository.finishProcessingPDF( identifier );
			}
		}

		String convertToImage( final PDPage page, final long identifier, final int pageNumber )
			throws IOException, FileNotFoundException
		{
			val pageOutputDir = getOutputdir() + "/" + identifier + "/";
			ensureThatOutputDirExists( pageOutputDir );
			val pageImageFileName = pageOutputDir + pageNumber + ".png";
			val image = page.convertToImage();
			writeImage( pageImageFileName, image );
			return pageImageFileName;
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