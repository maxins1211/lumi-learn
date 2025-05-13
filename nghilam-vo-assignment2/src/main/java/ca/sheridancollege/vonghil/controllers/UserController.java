package ca.sheridancollege.vonghil.controllers;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import ca.sheridancollege.vonghil.beans.Course;
import ca.sheridancollege.vonghil.beans.Lesson;
import ca.sheridancollege.vonghil.beans.User;
import ca.sheridancollege.vonghil.database.DatabaseAccess;

@Controller
@RequestMapping("/user")
public class UserController {

	@Autowired
	private DatabaseAccess da;

	@GetMapping("/courses")
	public String coursesPage(Model model) {
		// Get current authenticated user
		Long userId = getCurrentUserId();

		// Get all courses
		model.addAttribute("courses", da.getCourses());

		// Get enrolled course IDs for the current user
		List<Course> enrolledCourses = da.getEnrolledCourses(userId);
		Set<Long> enrolledCourseIds = enrolledCourses.stream().map(course -> course.getId())
				.collect(Collectors.toSet());

		model.addAttribute("enrolledCourseIds", enrolledCourseIds);
		model.addAttribute("activePage", "courses");

		return "/user/courses";
	}

	@PostMapping("/enroll-course")
	public String enrollCourse(@RequestParam Long id, Model model) {
		// Get current authenticated user
		Long userId = getCurrentUserId();
		model.addAttribute("course", da.getCoursesById(id).get(0));
		// Enroll the user in the course
		da.enrollCourse(id, userId);

		return "/user/enroll-course";
	}

	@GetMapping("/study-course/{courseId}")
	public String studyCourse(@PathVariable Long courseId, Model model) {
		// Get current authenticated user
		Long userId = getCurrentUserId();

		Course course = da.getCoursesById(courseId).get(0);
		List<Lesson> lessons = da.getLessonsForCourse(courseId);

		// Get completed lessons for this user and course
		List<Long> completedLessons = da.getCompletedLessonsIds(userId, courseId);

		// Calculate progress
		int totalLessons = lessons.size();
		int completedCount = completedLessons.size();
		int completedPercentage = totalLessons > 0 ? (completedCount * 100) / totalLessons : 0;

		model.addAttribute("course", course);
		model.addAttribute("lessons", lessons);
		model.addAttribute("completedLessons", completedLessons);
		model.addAttribute("totalLessons", totalLessons);
		model.addAttribute("completedLessonsCount", completedCount);
		model.addAttribute("completedPercentage", completedPercentage);
		model.addAttribute("activePage", "enrolled-courses");

		return "/user/study-course";
	}

	@GetMapping("/enrolled-courses")
	public String enrolledCourses(Model model) {
		Long userId = getCurrentUserId();
		model.addAttribute("enrolledCourses", da.getEnrolledCourses(userId));
		System.out.println(da.getEnrolledCourses(userId));
		model.addAttribute("userId", userId);
		model.addAttribute("activePage", "enrolled-courses");
		return "/user/enrolled-courses";
	}

	@GetMapping("/study-course/{courseId}/lesson/{lessonId}")
	public String studyLesson(@PathVariable Long courseId, @PathVariable Long lessonId, Model model) {
		// Get current authenticated user

		Long userId = getCurrentUserId();

		// Check if user is enrolled in this course
		if (!da.isUserEnrolledInCourse(userId, courseId)) {
			return "redirect:/user/courses";
		}

		Course course = da.getCoursesById(courseId).get(0);
		List<Lesson> lessons = da.getLessonsForCourse(courseId);
		List<Lesson> activeLessonList = da.getLessonsById(lessonId);
		Lesson activeLesson = activeLessonList.isEmpty() ? null : activeLessonList.get(0);

		if (activeLesson == null) {
			return "redirect:/user/study-course/" + courseId;
		}

		// Find previous and next lessons
		Lesson previousLesson = null;
		Lesson nextLesson = null;

		for (int i = 0; i < lessons.size(); i++) {
			if (lessons.get(i).getId() == lessonId) {
				if (i > 0) {
					previousLesson = lessons.get(i - 1);
				}
				if (i < lessons.size() - 1) {
					nextLesson = lessons.get(i + 1);
				}
				break;
			}
		}

		// Get completed lessons for this user and course
		List<Long> completedLessons = da.getCompletedLessonsIds(userId, courseId);

		// Calculate progress
		int totalLessons = lessons.size();
		int completedCount = completedLessons.size();
		int completedPercentage = totalLessons > 0 ? (completedCount * 100) / totalLessons : 0;

		model.addAttribute("course", course);
		model.addAttribute("lessons", lessons);
		model.addAttribute("activeLesson", activeLesson);
		model.addAttribute("previousLesson", previousLesson);
		model.addAttribute("nextLesson", nextLesson);
		model.addAttribute("completedLessons", completedLessons);
		model.addAttribute("totalLessons", totalLessons);
		model.addAttribute("completedLessonsCount", completedCount);
		model.addAttribute("completedPercentage", completedPercentage);
		model.addAttribute("activePage", "enrolled-courses");
		return "/user/study-course";
	}

	@PostMapping("/mark-lesson-complete/{courseId}/{lessonId}")
	public String markLessonComplete(@PathVariable Long courseId, @PathVariable Long lessonId) {
		// Get current authenticated user
		Long userId = getCurrentUserId();

		// Mark lesson as complete
		da.markLessonAsCompleted(userId, courseId, lessonId);

		return "redirect:/user/study-course/" + courseId + "/lesson/" + lessonId;
	}

	private Long getCurrentUserId() {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		String email = auth.getName();
		User user = da.findUserAccount(email);
		return user.getUserId();

	}

}
