package skullabs.kernel.template;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors( fluent = true )
@NoArgsConstructor
public class MustacheTemplate {

	@NonNull
	String templateName;

	@NonNull
	Object paramObject;
}
