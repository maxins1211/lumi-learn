package ca.sheridancollege.vonghil.beans;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class Course {
	private long id;
	private String title;
	private String description;
	private String imageUrl;
}
