package ca.sheridancollege.vonghil.beans;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class Lesson {
private long id;
private long courseId;
private String title;
private String content;
private int orderIndex;
private String introVideoId;
}
