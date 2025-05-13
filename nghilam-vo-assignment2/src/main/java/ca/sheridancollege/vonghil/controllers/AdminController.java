package ca.sheridancollege.vonghil.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import ca.sheridancollege.vonghil.beans.Course;
import ca.sheridancollege.vonghil.database.DatabaseAccess;

@Controller
@RequestMapping("/admin")
public class AdminController {
    
    @Autowired
    private DatabaseAccess da;
    
    // Display all courses for admin
    @GetMapping("/courses")
    public String adminCourses(Model model) {
        model.addAttribute("courses", da.getCourses());
        model.addAttribute("activePage", "admin");
        return "admin/courses";
    }
    
    // Show form to add a new course
    @GetMapping("/courses/new")
    public String showAddCourseForm(Model model) {
        model.addAttribute("course", new Course());
        model.addAttribute("isEditMode", false);
        model.addAttribute("activePage", "admin");
        return "admin/course-form";
    }
    
    // Process the add course form submission
    @PostMapping("/courses/add")
    public String addCourse(@ModelAttribute Course course) {
        da.addCourse(course);
        return "redirect:/admin/courses";
    }
    
    // Show form to edit an existing course
    @GetMapping("/courses/edit/{id}")
    public String showEditCourseForm(@PathVariable Long id, Model model) {
        Course course = da.getCoursesById(id).get(0);
        model.addAttribute("course", course);
        model.addAttribute("isEditMode", true);
        model.addAttribute("lessons", da.getLessonsForCourse(id));
        model.addAttribute("activePage", "admin");
        return "admin/course-form";
    }
    
    // Process the edit course form submission
    @PostMapping("/courses/update")
    public String updateCourse(@ModelAttribute Course course) {
        da.updateCourse(course);
        return "redirect:/admin/courses";
    }
    
    // Delete a course 
    @GetMapping("/courses/delete/{id}")
    public String deleteCourse(@PathVariable Long id) {
        da.deleteCourse(id);
        return "redirect:/admin/courses";
    }
    
}
