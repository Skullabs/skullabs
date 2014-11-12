package skullabs.pdf;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import kikaha.hazelcast.Source;
import lombok.Cleanup;
import lombok.Getter;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.util.ImageIOUtil;

import trip.spi.Provided;
import trip.spi.Singleton;
import uworkers.api.Worker;

import com.hazelcast.core.MultiMap;
import com.typesafe.config.Config;

@Singleton
public class PDFImageConverter {

	@Getter( lazy = true )
	private final String outputdir = config.getString( "skul.images-dir" );

	@Provided
	@Source( "presentation-images" )
	MultiMap<Long, String> presentationImages;

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
		final File file = new File( pdf.getFileName() );
		convert( pdf.getIdentifier(), file );
	}

	/**
	 * Convert a PDF pages into PNG images.
	 *
	 * @param identifier
	 * @param pdfFile
	 * @throws IOException
	 */
	@SuppressWarnings( "unchecked" )
	public void convert( final long identifier, final File pdfFile ) throws IOException
	{
		final PDDocument pdf = PDDocument.loadNonSeq( pdfFile, null );
		final List<PDPage> allPages = pdf.getDocumentCatalog().getAllPages();

		int pageNumber = 0;
		for ( final PDPage page : allPages ) {
			final String imageName = convertToImage( page, identifier, pageNumber++ );
			presentationImages.put( identifier, imageName );
		}
	}

	String convertToImage( final PDPage page, final long identifier, final int pageNumber )
		throws IOException, FileNotFoundException
	{
		final String pageOutputDir = getOutputdir() + "/" + identifier + "/";
		ensureThatOutputDirExists( pageOutputDir );
		final String pageImageFileName = pageOutputDir + pageNumber + ".png";
		final BufferedImage image = page.convertToImage();
		@Cleanup final FileOutputStream output = new FileOutputStream( pageImageFileName );
		ImageIOUtil.writeImage( image, "png", output );
		return pageImageFileName;
	}

	void ensureThatOutputDirExists( final String outputDir ) {
		final File file = new File( outputDir );
		if ( !file.exists() )
			if ( !file.mkdirs() )
				throw new RuntimeException( "Could not create dir " + outputDir );
	}
}