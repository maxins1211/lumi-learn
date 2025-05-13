package ca.sheridancollege.vonghil.controllers;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import ca.sheridancollege.vonghil.beans.Course;
import ca.sheridancollege.vonghil.beans.Lesson;
import ca.sheridancollege.vonghil.beans.User;
import ca.sheridancollege.vonghil.database.DatabaseAccess;

@Controller
public class CourseController {
//	@Autowired
//    private SecurityService securityService;

	// Hardcoded user
//	String userId = "a1";
//	private Long getCurrentUserId() {
//		String email = "guest@guest.com"; // This gets the email (which is used as username)
//
//		// Look up the numeric userId that corresponds to this email
//		User user = da.findUserByEmail(email);
//		return user.getUserId(); // Return the actual Long userId
//	}

	@Autowired
	@Lazy
	DatabaseAccess da;

	public String home() {
		return "index";
	}

	@GetMapping("/courses")
	public String courses(Model model) {
		
//		Long userId = getCurrentUserId();
//		String userId = securityService.getCurrentUserId();
		model.addAttribute("courses", da.getCourses());

		// Get enrolled course IDs for the current user
		List<Course> enrolledCourses = da.getEnrolledCourses(Long.valueOf(99));
		Set<Long> enrolledCourseIds = enrolledCourses.stream().map(course -> course.getId())
				.collect(Collectors.toSet());

		model.addAttribute("enrolledCourseIds", enrolledCourseIds);
		model.addAttribute("activePage", "courses");

		return "courses";
	}

//	@GetMapping("/enrolled-courses")
//	public String enrolledCourses(Model model) {
//		Long userId = getCurrentUserId();
//		model.addAttribute("enrolledCourses", da.getEnrolledCourses(userId));
//		 model.addAttribute("userId", userId);
//		model.addAttribute("activePage", "enrolled-courses");
//		return "enrolled-courses";
//	}
//
//	@PostMapping("/enroll-course")
//	public String enrollCourse(Model model, @RequestParam Long id) {
//		Long userId = getCurrentUserId();
//		da.enrollCourse(id, userId);
//		model.addAttribute("course", da.getCoursesById(id).get(0));
//		return "enroll-course";
//	}
//
//	@GetMapping("/study-course/{courseId}")
//	public String studyCourse(Model model, @PathVariable Long courseId) {
//		Long userId = getCurrentUserId();
//		// Get course and its lessons
//		Course course = da.getCoursesById(courseId).get(0);
//		List<Lesson> lessons = da.getLessonsForCourse(courseId);
//
//		// Get completed lessons for this user and course
//		List<Long> completedLessons = da.getCompletedLessonsIds(userId, courseId);
//
//		// Calculate progress
//		int totalLessons = lessons.size();
//		int completedCount = completedLessons.size();
//		int completedPercentage = totalLessons > 0 ? (completedCount * 100) / totalLessons : 0;
//
//		model.addAttribute("course", course);
//		model.addAttribute("lessons", lessons);
//		model.addAttribute("completedLessons", completedLessons);
//		model.addAttribute("totalLessons", totalLessons);
//		model.addAttribute("completedLessonsCount", completedCount);
//		model.addAttribute("completedPercentage", completedPercentage);
//		model.addAttribute("activePage", "enrolled-courses");
//
//		return "study-course";
//	}
//
//	@GetMapping("/study-course/{courseId}/lesson/{lessonId}")
//	public String studyLesson(Model model, @PathVariable Long courseId, @PathVariable Long lessonId) {
//		Long userId = getCurrentUserId();
//		Course course = da.getCoursesById(courseId).get(0);
//		List<Lesson> lessons = da.getLessonsForCourse(courseId);
//		List<Lesson> activeLessonList = da.getLessonsById(lessonId);
//		Lesson activeLesson = activeLessonList.isEmpty() ? null : activeLessonList.get(0);
//
//		if (activeLesson == null) {
//			return "redirect:/study-course/" + courseId;
//		}
//
//		// Find previous and next lessons
//		Lesson previousLesson = null;
//		Lesson nextLesson = null;
//
//		for (int i = 0; i < lessons.size(); i++) {
//			if (lessons.get(i).getId() == lessonId) {
//				if (i > 0) {
//					previousLesson = lessons.get(i - 1);
//				}
//				if (i < lessons.size() - 1) {
//					nextLesson = lessons.get(i + 1);
//				}
//				break;
//			}
//		}
//
//		// Get completed lessons for this user and course
//		List<Long> completedLessons = da.getCompletedLessonsIds(userId, courseId);
//
//		// Calculate progress
//		int totalLessons = lessons.size();
//		int completedCount = completedLessons.size();
//		int completedPercentage = totalLessons > 0 ? (completedCount * 100) / totalLessons : 0;
//
//		model.addAttribute("course", course);
//		model.addAttribute("lessons", lessons);
//		model.addAttribute("activeLesson", activeLesson);
//		model.addAttribute("previousLesson", previousLesson);
//		model.addAttribute("nextLesson", nextLesson);
//		model.addAttribute("completedLessons", completedLessons);
//		model.addAttribute("totalLessons", totalLessons);
//		model.addAttribute("completedLessonsCount", completedCount);
//		model.addAttribute("completedPercentage", completedPercentage);
//		model.addAttribute("activePage", "enrolled-courses");
//
//		return "study-course";
//	}
//
//	@PostMapping("/mark-lesson-complete/{courseId}/{lessonId}")
//	public String markLessonComplete(@PathVariable Long courseId, @PathVariable Long lessonId) {
//		// Add this lesson to completed lessons for the user
//		Long userId = getCurrentUserId();
//		da.markLessonAsCompleted(userId, courseId, lessonId);
//
//		// Redirect back to the lesson
//		return "redirect:/study-course/" + courseId + "/lesson/" + lessonId;
//	}

}
