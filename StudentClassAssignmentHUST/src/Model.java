import java.util.List;
import java.util.Map;


public class Model {
	private Student[] students;
	private Course[] courses;
	private StudentRegistration[] registrations;
	private ClassCourse[] classCourses;
	
	private Map<Student, List<StudentRegistration>> mStudent2Registration;
	private Map<StudentRegistration, List<ClassCourse>> mRegistration2ClassCourses;
	private Map<Course, List<ClassCourse>> mCourse2ClassCourses;
	
}
