package skullabs.kernel.template;

import io.undertow.server.HttpServerExchange;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class HtmlResponse {

	@Getter( lazy = true )
	private final String loggedUsername = exchange.getSecurityContext().getAuthenticatedAccount().getPrincipal().getName();

	final HttpServerExchange exchange;
	final Object response;
}
