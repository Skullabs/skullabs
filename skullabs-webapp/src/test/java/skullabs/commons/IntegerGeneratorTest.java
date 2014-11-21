package skullabs.commons;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import lombok.val;

import org.junit.Test;

public class IntegerGeneratorTest {

	@Test
	public void ensureThatGeneratesASequenceOfNumberUntil10() {
		val generator = new IntegerGenerator( 10 );
		int counter = 0;
		for ( final Integer integer : generator )
			assertThat( integer, is( counter++ ) );
		assertThat( counter, is( 10 ) );
	}
}
