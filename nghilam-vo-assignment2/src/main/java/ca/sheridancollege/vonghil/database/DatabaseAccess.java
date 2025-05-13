package ca.sheridancollege.vonghil.database;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Repository;

import ca.sheridancollege.vonghil.beans.Course;
import ca.sheridancollege.vonghil.beans.Lesson;
import ca.sheridancollege.vonghil.beans.User;

@Repository
public class DatabaseAccess {
	@Autowired
	protected NamedParameterJdbcTemplate jdbc;
	@Autowired
	private PasswordEncoder passwordEncoder;


	public List<Course> getCourses() {
		MapSqlParameterSource namedParameters = new MapSqlParameterSource();
		String query = "SELECT * FROM course";
		return jdbc.query(query, namedParameters, new BeanPropertyRowMapper<Course>(Course.class));
	}

	public void enrollCourse(Long courseId, Long userId) {
		MapSqlParameterSource namedParameters = new MapSqlParameterSource();
		namedParameters.addValue("course_id", courseId);
		namedParameters.addValue("user_id", userId);
		String query = "INSERT INTO Enrollment (course_id,user_id) VALUES (:course_id,:user_id)";
		jdbc.update(query, namedParameters);
	}

	public List<Course> getCoursesById(Long courseId) {
		MapSqlParameterSource namedParameters = new MapSqlParameterSource();
		namedParameters.addValue("id", courseId);
		String query = "SELECT * FROM Course WHERE id = :id";
		return jdbc.query(query, namedParameters, new BeanPropertyRowMapper<Course>(Course.class));
	}

	public List<Course> getEnrolledCourses(Long userId) {
		MapSqlParameterSource namedParameters = new MapSqlParameterSource();
		namedParameters.addValue("user_id", userId);
		String query = "SELECT c.* FROM Course c " + "JOIN Enrollment e ON c.id = e.course_id "
				+ "WHERE e.user_id = :user_id";
		return jdbc.query(query, namedParameters, new BeanPropertyRowMapper<Course>(Course.class));

	}

	public List<Lesson> getLessonsForCourse(Long courseId) {
		MapSqlParameterSource namedParameters = new MapSqlParameterSource();
		namedParameters.addValue("id", courseId);
		String query = "SELECT * FROM Lesson WHERE course_id = :id ORDER BY order_index";
		return jdbc.query(query, namedParameters, new BeanPropertyRowMapper<Lesson>(Lesson.class));

	}

	public List<Lesson> getLessonsById(Long lessonId) {
		MapSqlParameterSource namedParameters = new MapSqlParameterSource();
		namedParameters.addValue("id", lessonId);
		String query = "SELECT * FROM Lesson WHERE id = :id";
		return jdbc.query(query, namedParameters, new BeanPropertyRowMapper<Lesson>(Lesson.class));
	}

	public List<Long> getCompletedLessonsIds(Long userId, Long courseId) {
		MapSqlParameterSource namedParameters = new MapSqlParameterSource();
		namedParameters.addValue("course_id", courseId);
		namedParameters.addValue("user_id", userId);
		String query = "SELECT lesson_id FROM LessonProgress WHERE user_id = :user_id AND course_id = :course_id";

		return jdbc.queryForList(query, namedParameters, Long.class);

	}

	// Get count of completed lessons for a course
	public int getCompletedLessonCount(Long userId, Long courseId) {
		MapSqlParameterSource namedParameters = new MapSqlParameterSource();
		namedParameters.addValue("user_id", userId);
		namedParameters.addValue("course_id", courseId);

		String query = "SELECT COUNT(*) FROM LessonProgress WHERE user_id = :user_id AND course_id = :course_id";
		List<Integer> counts = jdbc.queryForList(query, namedParameters, Integer.class);
		Integer count = counts.isEmpty() ? 0 : counts.get(0);
		return count;
	}

	// Get total lesson count for a course
	public int getTotalLessonCount(Long courseId) {
		MapSqlParameterSource namedParameters = new MapSqlParameterSource();
		namedParameters.addValue("course_id", courseId);

		String query = "SELECT COUNT(*) FROM Lesson WHERE course_id = :course_id";
		List<Integer> counts = jdbc.queryForList(query, namedParameters, Integer.class);
		Integer count = counts.isEmpty() ? 0 : counts.get(0);
		return count;
	}

	public void markLessonAsCompleted(Long userId, Long courseId, Long lessonId) {
		// First check if it's already marked as completed
		MapSqlParameterSource checkParams = new MapSqlParameterSource();
		checkParams.addValue("user_id", userId);
		checkParams.addValue("lesson_id", lessonId);

		String checkQuery = "SELECT COUNT(*) FROM LessonProgress WHERE user_id = :user_id AND lesson_id = :lesson_id";
		List<Integer> counts = jdbc.queryForList(checkQuery, checkParams, Integer.class);
		Integer count = counts.isEmpty() ? 0 : counts.get(0);

		if (count == 0) {
			// insert a new record when not completed yet
			MapSqlParameterSource insertParams = new MapSqlParameterSource();
			insertParams.addValue("user_id", userId);
			insertParams.addValue("course_id", courseId);
			insertParams.addValue("lesson_id", lessonId);

			String insertQuery = "INSERT INTO LessonProgress (user_id, course_id, lesson_id) VALUES (:user_id, :course_id, :lesson_id)";
			jdbc.update(insertQuery, insertParams);

			// Update the enrollment completion_status if all lessons are completed
			updateCourseCompletionStatus(userId, courseId);
		}
	}

	// Update course completion status if all lessons are completed
	private void updateCourseCompletionStatus(Long userId, Long courseId) {

		Integer totalLessons = getTotalLessonCount(courseId);

		Integer completedLessons = getCompletedLessonCount(userId, courseId);

		// If all lessons are completed, update the enrollment status
		if (totalLessons > 0 && totalLessons.equals(completedLessons)) {
			MapSqlParameterSource updateParams = new MapSqlParameterSource();
			updateParams.addValue("user_id", userId);
			updateParams.addValue("course_id", courseId);

			String updateQuery = "UPDATE Enrollment SET COMPLETION_STATUS = 'Completed' WHERE user_id = :user_id AND course_id = :course_id";
			jdbc.update(updateQuery, updateParams);
		} else {
			// Calculate completion percentage
			int percentage = totalLessons > 0 ? (completedLessons * 100) / totalLessons : 0;
			String status = "In Progress (" + percentage + "%)";

			MapSqlParameterSource updateParams = new MapSqlParameterSource();
			updateParams.addValue("status", status);
			updateParams.addValue("user_id", userId);
			updateParams.addValue("course_id", courseId);

			// Update the enrollment with the current progress
			String updateQuery = "UPDATE Enrollment SET completion_status = :status WHERE user_id = :user_id AND course_id = :course_id";
			jdbc.update(updateQuery, updateParams);
		}
	}

	// Check if a user is enrolled in a specific course
	public boolean isUserEnrolledInCourse(Long userId, Long courseId) {
		MapSqlParameterSource namedParameters = new MapSqlParameterSource();
		namedParameters.addValue("user_id", userId);
		namedParameters.addValue("course_id", courseId);

		String query = "SELECT COUNT(*) FROM enrollment WHERE user_id = :user_id AND course_id = :course_id";
		List<Integer> counts = jdbc.queryForList(query, namedParameters, Integer.class);
		Integer count = counts.isEmpty() ? 0 : counts.get(0);
		return count > 0;
	}

	// Get course completion percentage for a user
	public int getCourseCompletionPercentage(Long userId, Long courseId) {
		Integer totalLessons = getTotalLessonCount(courseId);
		Integer completedLessons = getCompletedLessonCount(userId, courseId);
		// Calculate percentage
		return totalLessons > 0 ? (completedLessons * 100) / totalLessons : 0;
	}

	public User findUserAccount(String email) {
		MapSqlParameterSource namedParameters = new MapSqlParameterSource();
		String query = "SELECT * FROM sec_user where email = :email";
		namedParameters.addValue("email", email);
		try {
			return jdbc.queryForObject(query, namedParameters, new BeanPropertyRowMapper<>(User.class));
		} catch (EmptyResultDataAccessException erdae) {
			return null;
		}
	}
	public User findUserByEmail(String email) {
	    MapSqlParameterSource namedParameters = new MapSqlParameterSource();
	    namedParameters.addValue("email", email);
	    
	    String query = "SELECT * FROM users WHERE email = :email";
	    List<User> users = jdbc.query(query, namedParameters, 
	                          new BeanPropertyRowMapper<User>(User.class));
	    
	    if (users.isEmpty()) {
	        return null;
	    }
	    return users.get(0);
	}

	public List<String> getRolesById(Long userId) {
		MapSqlParameterSource namedParameters = new MapSqlParameterSource();
		String query = "SELECT sec_role.roleName " + "FROM user_role, sec_role "
				+ "WHERE user_role.roleId = sec_role.roleId " + "AND userId = :userId";
		namedParameters.addValue("userId", userId);
		return jdbc.queryForList(query, namedParameters, String.class);
	}

	public Long saveUser(User user) {
	    // Insert user
	    String insertQuery = "INSERT INTO sec_user (email, encryptedPassword) VALUES (:email, :encryptedPassword)";
	    MapSqlParameterSource params = new MapSqlParameterSource();
	    params.addValue("email", user.getEmail());
	    params.addValue("encryptedPassword", user.getEncryptedPassword());
	    jdbc.update(insertQuery, params);
	    
	    // Retrieve the ID using the email as lookup
	    String selectQuery = "SELECT userId FROM sec_user WHERE email = :email";
	    Long userId = jdbc.queryForObject(selectQuery, params, Long.class);
	    
	    // Set ID on user object (optional)
	    user.setUserId(userId);
	    
	    return userId;
	}

	public void addRole(Long userId, String roleName) {
		// First get the roleId
		MapSqlParameterSource namedParameters = new MapSqlParameterSource();
		namedParameters.addValue("roleName", roleName);

		String query = "SELECT roleId FROM sec_role WHERE roleName = :roleName";
		Long roleId = jdbc.queryForObject(query, namedParameters, Long.class);

		// Then add the user-role relationship
		namedParameters = new MapSqlParameterSource();
		namedParameters.addValue("userId", userId);
		namedParameters.addValue("roleId", roleId);

		query = "INSERT INTO user_role (userId, roleId) VALUES (:userId, :roleId)";
		jdbc.update(query, namedParameters);
	}
	public boolean emailExists(String email) {
	    MapSqlParameterSource namedParameters = new MapSqlParameterSource();
	    namedParameters.addValue("email", email);
	    
	    String query = "SELECT COUNT(*) FROM sec_user WHERE email = :email";
	    Integer count = jdbc.queryForObject(query, namedParameters, Integer.class);
	    return count != null && count > 0;
	}
	
	public void assignUserRole(Long userId, String roleName) {
	    MapSqlParameterSource params = new MapSqlParameterSource();
	    params.addValue("roleName", roleName);
	    Long roleId;
	    try {
	        roleId = jdbc.queryForObject(
	            "SELECT id FROM roles WHERE role_name = :roleName", 
	            params, 
	            Long.class
	        );
	    } catch (EmptyResultDataAccessException e) {
	        throw new RuntimeException("Role not found: " + roleName);
	    }
	    params = new MapSqlParameterSource();
	    params.addValue("userId", userId);
	    params.addValue("roleId", roleId);
	    jdbc.update(
	        "INSERT INTO user_role (user_id, role_id) VALUES (:userId, :roleId)",
	        params
	    );
	}

	public void addCourse(Course course) {
	    MapSqlParameterSource namedParameters = new MapSqlParameterSource();
	    namedParameters.addValue("title", course.getTitle());
	    namedParameters.addValue("description", course.getDescription());
	    namedParameters.addValue("imageUrl", course.getImageUrl());
	    
	    String query = "INSERT INTO Course (title, description, image_url) VALUES (:title, :description, :imageUrl)";
	    
	    jdbc.update(query, namedParameters);
	}

	// Update an existing course
	public void updateCourse(Course course) {
	    MapSqlParameterSource namedParameters = new MapSqlParameterSource();
	    namedParameters.addValue("id", course.getId());
	    namedParameters.addValue("title", course.getTitle());
	    namedParameters.addValue("description", course.getDescription());
	    namedParameters.addValue("imageUrl", course.getImageUrl());
	    
	    String query = "UPDATE Course SET title = :title, description = :description, image_url = :imageUrl WHERE id = :id";
	    
	    jdbc.update(query, namedParameters);
	}

	// Delete a course
	public void deleteCourse(Long id) {
	    MapSqlParameterSource namedParameters = new MapSqlParameterSource();
	    namedParameters.addValue("id", id);
	    String deleteRelatedQuery = "DELETE FROM Lesson WHERE course_id = :id";
	    jdbc.update(deleteRelatedQuery, namedParameters);
	    String query = "DELETE FROM Course WHERE id = :id";
	    jdbc.update(query, namedParameters);
	}
	public void addLesson(Lesson lesson) {
	    MapSqlParameterSource namedParameters = new MapSqlParameterSource();
	    namedParameters.addValue("course_id", lesson.getCourseId());
	    namedParameters.addValue("title", lesson.getTitle());
	    namedParameters.addValue("content", lesson.getContent());
	    namedParameters.addValue("intro_video_id", lesson.getIntroVideoId());
	    
	    // Get the highest order_index for the course and add 1
	    Integer maxOrderIndex = getMaxLessonOrderIndex(lesson.getCourseId());
	    int newOrderIndex = (maxOrderIndex != null) ? maxOrderIndex + 1 : 1;
	    namedParameters.addValue("order_index", newOrderIndex);
	    
	    String query = "INSERT INTO Lesson (course_id, title, content, intro_video_id, order_index) " +
	                  "VALUES (:course_id, :title, :content, :intro_video_id, :order_index)";
	    
	    jdbc.update(query, namedParameters);
	}


	// Delete a lesson
	public void deleteLesson(Long lessonId) {
	    // First delete any progress records for this lesson
	    MapSqlParameterSource progressParams = new MapSqlParameterSource();
	    progressParams.addValue("lesson_id", lessonId);
	    String progressQuery = "DELETE FROM LessonProgress WHERE lesson_id = :lesson_id";
	    jdbc.update(progressQuery, progressParams);
	    
	    // Then delete the lesson
	    MapSqlParameterSource lessonParams = new MapSqlParameterSource();
	    lessonParams.addValue("id", lessonId);
	    String lessonQuery = "DELETE FROM Lesson WHERE id = :id";
	    jdbc.update(lessonQuery, lessonParams);
	}

	// Get the highest order_index for a course
	private Integer getMaxLessonOrderIndex(Long courseId) {
	    MapSqlParameterSource namedParameters = new MapSqlParameterSource();
	    namedParameters.addValue("course_id", courseId);
	    
	    String query = "SELECT MAX(order_index) FROM Lesson WHERE course_id = :course_id";
	    try {
	        return jdbc.queryForObject(query, namedParameters, Integer.class);
	    } catch (Exception e) {
	        return 0; // If no lessons exist yet
	    }
	}

	// Reorder lessons (optional: for drag-and-drop functionality)
	public void updateLessonOrder(Long lessonId, int newOrderIndex) {
	    MapSqlParameterSource namedParameters = new MapSqlParameterSource();
	    namedParameters.addValue("id", lessonId);
	    namedParameters.addValue("order_index", newOrderIndex);
	    
	    String query = "UPDATE Lesson SET order_index = :order_index WHERE id = :id";
	    jdbc.update(query, namedParameters);
	}
	public void updateLesson(Lesson lesson) {
	    MapSqlParameterSource namedParameters = new MapSqlParameterSource();
	    namedParameters.addValue("id", lesson.getId());
	    namedParameters.addValue("title", lesson.getTitle());
	    namedParameters.addValue("content", lesson.getContent());
	    namedParameters.addValue("intro_video_id", lesson.getIntroVideoId());
	    namedParameters.addValue("order_index", lesson.getOrderIndex());

	    String query = "UPDATE Lesson SET title = :title, content = :content, " +
	                  "intro_video_id = :intro_video_id, order_index = :order_index WHERE id = :id";

	    jdbc.update(query, namedParameters);
	}

	// Method to handle reordering when updating an existing lesson's order
	public void reorderLessonsForUpdate(Long courseId, int oldOrder, int newOrder) {
	    if (oldOrder < newOrder) {
	        // Move lesson to a later position
	        // Shift lessons between old and new positions up (decrease order index)
	        MapSqlParameterSource namedParameters = new MapSqlParameterSource();
	        namedParameters.addValue("course_id", courseId);
	        namedParameters.addValue("old_order", oldOrder);
	        namedParameters.addValue("new_order", newOrder);
	        
	        String query = "UPDATE Lesson SET order_index = order_index - 1 " +
	                       "WHERE course_id = :course_id AND order_index > :old_order " +
	                       "AND order_index <= :new_order";
	        
	        jdbc.update(query, namedParameters);
	    } else if (oldOrder > newOrder) {
	        // Move lesson to an earlier position
	        // Shift lessons between new and old positions down (increase order index)
	        MapSqlParameterSource namedParameters = new MapSqlParameterSource();
	        namedParameters.addValue("course_id", courseId);
	        namedParameters.addValue("old_order", oldOrder);
	        namedParameters.addValue("new_order", newOrder);
	        
	        String query = "UPDATE Lesson SET order_index = order_index + 1 " +
	                       "WHERE course_id = :course_id AND order_index >= :new_order " +
	                       "AND order_index < :old_order";
	        
	        jdbc.update(query, namedParameters);
	    }
	    // If oldOrder == newOrder, no reordering needed
	}

	// Method to handle reordering when inserting a new lesson at a specific order
	public void reorderLessonsForInsert(Long courseId, int orderIndex) {
	    // Shift all lessons at or after the insertion point down
	    MapSqlParameterSource namedParameters = new MapSqlParameterSource();
	    namedParameters.addValue("course_id", courseId);
	    namedParameters.addValue("order_index", orderIndex);
	    
	    String query = "UPDATE Lesson SET order_index = order_index + 1 " +
	                   "WHERE course_id = :course_id AND order_index >= :order_index";
	    
	    jdbc.update(query, namedParameters);
	}

	// Get current order index for a lesson
	public Integer getLessonOrderIndex(Long lessonId) {
	    MapSqlParameterSource namedParameters = new MapSqlParameterSource();
	    namedParameters.addValue("id", lessonId);
	    
	    String query = "SELECT order_index FROM Lesson WHERE id = :id";
	    
	    try {
	        return jdbc.queryForObject(query, namedParameters, Integer.class);
	    } catch (Exception e) {
	        return null; // If the lesson doesn't exist
	    }
	}
	
}
