package com.career;

import static java.util.stream.Collectors.collectingAndThen;
import static java.util.stream.Collectors.counting;
import static java.util.stream.Collectors.groupingBy;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class Student {
	
	public static final String file1 = "StudentsEnrolledMoreThan3Years.txt";
	public static final String header1 = "student_code,student_name,enrollment_year";
	public static final String file2 = "Top10Students.txt";
	public static final String header2 = "student_code,agv_grade";
	
	private String enrollment_year;
	private String student_code;
	private String student_name;
	
	public Student(String student_code) {
		super();
		this.student_code = student_code;
	}
	
	public Student(String student_code, String enrollment_year) {
		super();
		this.enrollment_year = enrollment_year;
		this.student_code = student_code;
	}
	
	public Student(String student_code,  String student_name, String enrollment_year) {
		super();
		this.enrollment_year = enrollment_year;
		this.student_code = student_code;
		this.student_name = student_name;
	}

	public String getStudent_name() {
		return student_name;
	}

	public void setStudent_name(String student_name) {
		this.student_name = student_name;
	}

	public String getEnrollment_year() {
		return enrollment_year;
	}

	public void setEnrollment_year(String enrollment_year) {
		this.enrollment_year = enrollment_year;
	}

	public String getStudent_code() {
		return student_code;
	}

	public void setStudent_code(String student_code) {
		this.student_code = student_code;
	}

	/**
	 * Create Student
	 */
	public static Function<String, Student> mapToStudent = (line) -> {
	  String[] row = line.split(",");
	  return new Student(row[0], row[1]);
	};
	/**
	 * Create Exam
	 */
	public static Function<String, Exam> mapToExam = (line) -> {
		  String[] row = line.split(",");
		  return new Exam(row[0], row[1], row[2], Integer.parseInt(row[3]));
		};
	/**
	 * 
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		
		String fileName_enr = null;
		String fileName_mar = null;
		fileName_enr = args[0];
		fileName_mar = args[1];
		
		List<Student> studentsFiltered = readEnrollments(fileName_enr);
		List<Exam> exams = readExams(fileName_mar);
		Long actual_year = (long) Calendar.getInstance().get(Calendar.YEAR);
		studentsFiltered.stream().filter(student -> (actual_year - Long.parseLong(student.getEnrollment_year())) > 3);
		getStudents3YearsOldEnr(file1, header1, studentsFiltered, exams);
		getTop10Students(file2, header2, exams);

    }

	/**
	 * @param fileName_enr
	 * @return
	 * @throws IOException 
	 */
	public static List<Student> readEnrollments(String fileName_enr) throws IOException {
		
		List<Student> studentsFiltered = new ArrayList<Student>();
		try (BufferedReader br = Files.newBufferedReader(Paths.get(fileName_enr))){
			studentsFiltered = readStudentLines(br);
		}
		catch (IOException e) {
			throw new IOException("File not found or empty");
        }
		return studentsFiltered;
	}

	/**
	 * @param br
	 * @return
	 */
	public static List<Student> readStudentLines(BufferedReader br) {
		List<Student> studentsFiltered;
		studentsFiltered =  br.lines()
		.skip(1)
		.map(mapToStudent)
		.collect(Collectors.toList());
		return studentsFiltered;
	}

	/**
	 * @param fileName_mar
	 * @return
	 * @throws IOException 
	 */
	public static List<Exam> readExams(String fileName_mar) throws IOException {
		List<Exam> exams_f = new ArrayList<Exam>();
		try (BufferedReader br = Files.newBufferedReader(Paths.get(fileName_mar))){
			exams_f = readExamLines(br);
		}
		catch (IOException e) {
           throw new IOException("File not found or empty");
        }
		return exams_f;
	}

	/**
	 * @param br
	 * @return
	 */
	public static List<Exam> readExamLines(BufferedReader br) {
		List<Exam> exams_f;
		exams_f =  br.lines()
		.skip(1)
		.map(mapToExam)
		.collect(Collectors.toList());
		return exams_f;
	}


	/**
	 * @param file2
	 * @param header2
	 * @param exams
	 */
	public static void getTop10Students(String file2, String header2, final List<Exam> exams) {
		List<String> elenco2 = new ArrayList<String>();
		Map<String, Long> topStudentCodes = exams.stream()
			 			 .filter(e -> e.getExam_grade() >= 18)
			             .collect(
			            		 collectingAndThen(groupingBy(Exam::getStudent_code, counting()), 
			                                        m -> { m.values().removeIf(v -> v <= 3L); return m; }));
		
		Map<String, Double> topStudentsAvgExamMarks =new HashMap<String,Double>();

		for (String s : topStudentCodes.keySet()) {
			topStudentsAvgExamMarks.put(s,exams.stream()
					.filter(e -> e.getStudent_code().equals(s) && e.getExam_grade() >= 18)
					.mapToDouble(e -> e.getExam_grade()).average().orElse(0.0)
					);
					
		}
		topStudentsAvgExamMarks.entrySet().stream()
		.sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
		.limit(10)
		//.forEach(s -> System.out.println(s.getKey() + ',' + Math.round(s.getValue()*100.0)/100.0));
		.forEach(s -> elenco2.add(s.getKey() + ',' + Math.round(s.getValue()*100.0)/100.0 + "\n"));
		try {
			writeFile(file2, elenco2, header2, "No student enrolled more than 3 years");
		} catch (Exception e1) {
			e1.printStackTrace();
		}
	}

	/**
	 * @param file1
	 * @param header1
	 * @param studentsFiltered
	 * @param exams
	 */
	public static void getStudents3YearsOldEnr(String file1, String header1, List<Student> studentsFiltered,
			final List<Exam> exams) {
		List<Student>studentList = new ArrayList<Student>();
		List<String> elenco1 = new ArrayList<String>();
		
		studentsFiltered.forEach(s -> exams.stream()
			.filter(e -> e.getStudent_code().equals(s.getStudent_code()))
			.filter(distinctByKeys(Exam::getStudent_code, Exam::getStudent_name))
            .map(e -> studentList.add(new Student(e.getStudent_code(), e.getStudent_name(), s.getEnrollment_year())))
            .collect(Collectors.toList()));
		//studentList.stream().forEach(s -> System.out.println(s.getStudent_code() + "," + s.getStudent_name() + "," + s.getEnrollment_year()));
		studentList.stream().forEach(s -> elenco1.add(s.getStudent_code() + "," + s.getStudent_name() + "," + s.getEnrollment_year()+"\n"));
		try {
			writeFile(file1, elenco1, header1, "No Student has passed almost 3 exams");
		} catch (Exception e1) {
			e1.printStackTrace();
		}
	}

	/**
	 * @param file
	 * @param elenco
	 * @param header
	 * @throws Exception 
	 */
	public static void writeFile(String file, List<String> elenco, String header, String msg) throws Exception {
		try(BufferedWriter writer = new BufferedWriter(new FileWriter(file, false))) {
			writer.write(header+"\n");
			if(elenco.size() == 0)
				throw new Exception();
			for (String item  : elenco) {
				writer.write(item);
			}
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		catch(Exception e) {
			throw new Exception(msg);
		}
	}
	/**
	 * 
	 * @param <T>
	 * @param keyExtractors
	 * @return
	 */
	private static <T> Predicate<T> distinctByKeys(Function<? super T, ?>... keyExtractors) 
	  {
	    final Map<List<?>, Boolean> seen = new ConcurrentHashMap<>();
	    return t -> 
	    {
	      final List<?> keys = Arrays.stream(keyExtractors)
	                  .map(ke -> ke.apply(t))
	                  .collect(Collectors.toList());
	      return seen.putIfAbsent(keys, Boolean.TRUE) == null;
	    };
	  }

	
	@Override
	public String toString() {
		return "Student [enrollment_year=" + enrollment_year + ", student_code=" + student_code + "]";
	}
	

}
