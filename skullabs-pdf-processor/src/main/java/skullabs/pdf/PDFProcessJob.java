package skullabs.pdf;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@Getter
@NoArgsConstructor
@RequiredArgsConstructor
public class PDFProcessJob {

	@NonNull
	Long identifier;

	@NonNull
	String fileName;
}
