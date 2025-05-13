package ca.sheridancollege.vonghil.beans;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@Data
@NoArgsConstructor
@RequiredArgsConstructor
public class User {
	@NonNull
	private Long userId;
	@NonNull
	private String email;
	@NonNull
	private String encryptedPassword;
}
