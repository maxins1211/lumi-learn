package ca.sheridancollege.vonghil.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import ca.sheridancollege.vonghil.beans.Course;
import ca.sheridancollege.vonghil.beans.Lesson;
import ca.sheridancollege.vonghil.database.DatabaseAccess;

@Controller
@RequestMapping("/admin")
public class LessonController {

    @Autowired
    private DatabaseAccess da;

    // Display form to add a new lesson to a course
    @GetMapping("/courses/{courseId}/lessons/new")
    public String showAddLessonForm(@PathVariable Long courseId, Model model) {
        // Get the course
        List<Course> courses = da.getCoursesById(courseId);
        if (courses.isEmpty()) {
            return "redirect:/admin/courses";
        }
        
        Course course = courses.get(0);
        
        // Create a new Lesson with the courseId already set
        Lesson lesson = new Lesson();
        lesson.setCourseId(courseId);
        
        // Set default order index to the next available position
        List<Lesson> existingLessons = da.getLessonsForCourse(courseId);
        lesson.setOrderIndex(existingLessons.size() + 1);
        
        model.addAttribute("lesson", lesson);
        model.addAttribute("course", course);
        model.addAttribute("existingLessons", existingLessons);
        model.addAttribute("isEditMode", false);
        
        return "/admin/lesson-form";
    }
    
    // Process the form to add a new lesson
    @PostMapping("/lessons/save")
    public String saveLesson(@ModelAttribute Lesson lesson, BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            // Get the course for context
            List<Course> courses = da.getCoursesById(lesson.getCourseId());
            Course course = courses.isEmpty() ? new Course() : courses.get(0);

            // Get existing lessons for the course
            List<Lesson> existingLessons = da.getLessonsForCourse(lesson.getCourseId());

            model.addAttribute("course", course);
            model.addAttribute("existingLessons", existingLessons);
            model.addAttribute("isEditMode", lesson.getId() > 0);

            return "/admin/lesson-form";
        }

        // Extract YouTube ID if a full URL was provided
        if (lesson.getIntroVideoId() != null && !lesson.getIntroVideoId().isEmpty()) {
            if (lesson.getIntroVideoId().contains("youtu.be/")) {
                lesson.setIntroVideoId(lesson.getIntroVideoId().split("youtu.be/")[1]);
            } else if (lesson.getIntroVideoId().contains("youtube.com/watch?v=")) {
                lesson.setIntroVideoId(lesson.getIntroVideoId().split("v=")[1]);
            }

            // Remove any additional parameters
            if (lesson.getIntroVideoId().contains("&")) {
                lesson.setIntroVideoId(lesson.getIntroVideoId().split("&")[0]);
            }
        }

        // Save or update the lesson with proper ordering
        if (lesson.getId() > 0) {
            // Get current lesson order to check if order changed
            Integer oldOrder = da.getLessonOrderIndex(lesson.getId());
            if (oldOrder != null && oldOrder != lesson.getOrderIndex()) {
                // Handle reordering of lessons
                da.reorderLessonsForUpdate(lesson.getCourseId(), oldOrder, lesson.getOrderIndex());
            }
            da.updateLesson(lesson);
        } else {
            // For new lessons with specific order
            if (lesson.getOrderIndex() > 0) {
                da.reorderLessonsForInsert(lesson.getCourseId(), lesson.getOrderIndex());
            }
            da.addLesson(lesson);
        }

        return "redirect:/admin/courses/edit/" + lesson.getCourseId() + "?success=lesson";
    }
    
    // Display form to edit an existing lesson
    @GetMapping("/lessons/edit/{id}")
    public String showEditLessonForm(@PathVariable Long id, Model model) {
        List<Lesson> lessons = da.getLessonsById(id);
        if (lessons.isEmpty()) {
            return "redirect:/admin/courses";
        }
        
        Lesson lesson = lessons.get(0);
        
        // Get the course for context
        List<Course> courses = da.getCoursesById(lesson.getCourseId());
        Course course = courses.isEmpty() ? new Course() : courses.get(0);
        
        // Get existing lessons for the course
        List<Lesson> existingLessons = da.getLessonsForCourse(lesson.getCourseId());
        
        model.addAttribute("lesson", lesson);
        model.addAttribute("course", course);
        model.addAttribute("existingLessons", existingLessons);
        model.addAttribute("isEditMode", true);
        
        return "/admin/lesson-form";
    }
    
    // Process the request to delete a lesson
    @GetMapping("/lessons/delete/{id}")
    public String deleteLesson(@PathVariable Long id) {
        // Get the lesson to find its courseId
        List<Lesson> lessons = da.getLessonsById(id);
        if (!lessons.isEmpty()) {
            Long courseId = lessons.get(0).getCourseId();
            da.deleteLesson(id);
            return "redirect:/admin/courses/edit/" + courseId + "?success=lessonDeleted";
        }
        
        return "redirect:/admin/courses";
    }
}
