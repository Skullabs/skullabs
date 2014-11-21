package skullabs.web.kernel;

import java.io.Serializable;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@EqualsAndHashCode
@RequiredArgsConstructor
public class User implements Serializable {

	private static final long serialVersionUID = -3278587347520465945L;

	final long id;

	@NonNull
	String name;

	@NonNull
	String username;

	@NonNull
	String password;

	public User() {
		this.id = System.currentTimeMillis();
	}
}
