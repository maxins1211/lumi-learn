-- Insert roles
INSERT INTO sec_role (roleName) VALUES ('ROLE_USER');
INSERT INTO sec_role (roleName) VALUES ('ROLE_ADMIN');
INSERT INTO sec_role (roleName) VALUES ('ROLE_GUEST');

-- Insert default admin account (password: 123)
INSERT INTO sec_user (email, encryptedPassword)
VALUES ('admin@admin.com', '$2y$10$FKdXgCzUyAMVNxtmCEkUUuS4BWLieImGyw/MVqY2j2TQ6KPsb18Wi');
INSERT INTO sec_user (userId,email, encryptedPassword)
VALUES (99,'guest@guest.com', '$2y$10$hnbSSK9JKKpVYbd5FuPK8uhHnmE3HcpO11/KQmzxW09RS8Dk7LKDe');


-- Assign admin role
INSERT INTO user_role (userId, roleId)
VALUES (
    (SELECT userId FROM sec_user WHERE email = 'admin@admin.com'),
    (SELECT roleId FROM sec_role WHERE roleName = 'ROLE_ADMIN')
);
INSERT INTO user_role (userId, roleId)
VALUES (
    (SELECT userId FROM sec_user WHERE email = 'admin@admin.com'),
    (SELECT roleId FROM sec_role WHERE roleName = 'ROLE_USER')
);


-- Sample Courses
INSERT INTO Course (title, description, image_url) VALUES
('Java Programming Fundamentals', 'Learn the core concepts and features of the Java programming language.','/images/java-programming-tutorial.jpg'),
('Python for Data Science', 'Master Python programming and analyze data with popular Python libraries.','/images/data-science-with-python.png'),
('Web Development with HTML, CSS and JavaScript', 'Learn how to build beautiful and responsive websites using HTML, CSS, and JavaScript.','/images/html-css-js.jpg');

-- Sample Lessons for Java Programming Fundamentals
INSERT INTO Lesson (course_id, title, content, order_index, intro_video_id) VALUES
(1, 'Introduction to Java Programming',
'Java is a versatile, high-level programming language that adheres to the "write once, run anywhere" philosophy. 
Created in the mid-90s, Java remains one of the most popular programming languages for building web applications, 
enterprise-level applications, and mobile applications. This lesson introduces Java’s history, its features such as 
object-oriented programming, garbage collection for memory management, and platform independence. Dive into why 
Java’s robust ecosystem of libraries and frameworks makes it an excellent choice for modern developers.', 
1, 'mG4NLNZ37y4'),

(1, 'Java Development Environment Setup',
'A functional development environment is crucial for coding in Java. In this lesson, you’ll learn how to set up the 
Java Development Kit (JDK), configure environment variables like JAVA_HOME, and verify your installation. You will also 
explore the use of Integrated Development Environments (IDEs) such as Eclipse and IntelliJ IDEA to write, debug, and execute 
Java programs efficiently. Finally, we’ll cover best practices for using build tools like Maven and Gradle for managing dependencies.',
2, 'WRISYpKhIrc'),

(1, 'Data Types and Variables in Java',
'This lesson focuses on Java’s data typing system. Java uses a static type system, meaning variables must be declared with a type.
First, we’ll explore primitive data types such as byte, short, int, long, float, double, char, and boolean, and understand their 
memory usage. Next, we’ll dive into reference data types such as objects and arrays. You’ll also learn about type casting, 
variable scope, and how to use constants effectively to reduce bugs.', 
3, 'sVIvhzEizEQ'),

(1, 'Control Flow Statements',
'In this lesson, we’ll look at Java’s control flow mechanisms that help execute specific blocks of code based on certain conditions. 
We’ll start with conditional statements like if-else and switch-case, illustrating how branching works in Java programs. 
Then, we’ll move on to iteration control structures including for, while, and do-while loops. Through examples, 
you’ll understand where and how to use these structures effectively to solve real-world programming problems.', 
4, 'qX6oNPX3gmE'),

(1, 'Object-Oriented Programming Basics',
'Java was built with object-oriented programming (OOP) in mind, which organizes software design around objects rather 
than functions or logic. In this lesson, we’ll discuss key principles of OOP: encapsulation, inheritance, polymorphism, 
and abstraction. Through practical examples, you’ll learn how to create classes, use constructors, encapsulate data with access modifiers, 
and establish relationships between classes using inheritance. By the end, you’ll understand how OOP enhances reusability and modularity.', 
5, 'pTB0EiLXUC8');

-- Sample Lessons for Python for Data Science
INSERT INTO Lesson (course_id, title, content, order_index, intro_video_id) VALUES
(2, 'Introduction to Python for Data Science',
'Python has become synonymous with data science due to its simplicity and versatility. This lesson introduces 
why Python is the language of choice for data scientists. We’ll explore its easy-to-read syntax, robust library 
ecosystem (including NumPy, Pandas, and Matplotlib), and its strong support for automation and scripting. 
You’ll learn how Python handles data visualization, big data processing, and machine learning tasks effectively.',
1, 't3uVPX6rwgM'),

(2, 'Python Environment Setup for Data Science',
'Setting up a Python environment for data science requires a little preparation. This lesson explains how to install 
Anaconda, a popular Python distribution that comes preloaded with tools like Jupyter Notebook and libraries for data analysis. 
We’ll also explore IDEs like PyCharm and VS Code, comparing their features. Additionally, tips for managing environments 
with pip and virtual environments will ensure you’re ready for efficient data science workflows.', 
2, 'TdbeymTcYYE'),

(2, 'Data Manipulation with Pandas',
'A key skill in data science is wrangling raw data into a clean, structured format. Using Pandas, you’ll learn how 
to load datasets from CSV and Excel files, filter rows, and extract important data. This lesson also covers how to 
create pivot tables, conduct group-by operations, and handle missing data. By the end, you’ll be able to transform raw 
datasets into insightful visualizations.', 
3, 'Liv6eeb1VfE'),

(2, 'Data Visualization with Matplotlib',
'Visualization is an essential tool in data interpretation. By using Matplotlib, you’ll learn how to generate a wide 
range of visualizations, including line graphs, scatter plots, bar graphs, and histograms. This lesson covers the basics 
of styling graphs, adding annotations, and integrating visualizations into your analysis workflow. Good visualizations 
make complex datasets accessible and meaningful to non-technical audiences.', 
4, 'a9UrKTVEeZA'),

(2, 'Introduction to Machine Learning in Python',
'In this lesson, you’ll take the first steps in building machine learning models using Python. We’ll introduce 
Scikit-learn, a powerful library for supervised and unsupervised learning. Topics include creating datasets, splitting 
data into training and testing sets, and building simple models such as linear regression and decision trees. 
This foundational knowledge paves the way for tackling more advanced topics in machine learning.', 
5, 'ukzFI9rgwfU');

-- Sample Lessons for Web Development with HTML and CSS
INSERT INTO Lesson (course_id, title, content, order_index, intro_video_id) VALUES
(3, 'Introduction to Web Development',
'The web development process begins with understanding the architecture of the internet. In this lesson, 
you’ll learn about the client-server model, domains, and how HTTP works. Discover the essential tools for 
developers, including browser developer tools, text editors, and version control systems like Git. This foundational 
knowledge sets the stage for creating modern, dynamic web applications.', 
1, 'ysEN5RaKOlA'),

(3, 'Understanding HTML Basics',
'HTML (HyperText Markup Language) is at the heart of every webpage on the internet. You’ll learn how to use 
tags like to create structure, embed images, and add hyperlinks. 
This lesson also covers semantic HTML and accessibility best practices for creating web pages that are usable by everyone.',
2, 'salY_Sm6mv4'),

(3, 'Getting Started with CSS',
'CSS (Cascading Style Sheets) is used to style the elements created with HTML. This lesson introduces styling 
techniques such as using selectors to apply styles, defining colors, adding spacing with padding/margin, 
and understanding the box model. You’ll also learn how to integrate external style sheets for reusable and maintainable designs.', 
3, '1PnVor36_40'),

(3, 'Building Layouts with Flexbox and Grid',
'Flexbox and CSS Grid are powerful tools for designing adaptable layouts. The lesson covers creating flex containers, 
aligning elements, and designing grid-based page structures. You’ll practice building responsive layouts that adapt 
perfectly to screens of all sizes using these modern CSS techniques.', 
4, '2GxAElWKaAo'),

(3, 'Working with Forms and Inputs',
'Forms are a critical part of web applications that enable user interaction. We’ll explore input types like text, 
email, numbers, and file uploads, along with techniques for validating user input. This lesson helps you create 
functional, user-friendly forms and explains how to integrate them with backend APIs for dynamic behavior.', 
5, 'zIN54lhJtQU');
